package com.inca.npbi.client.design;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.anyprint.impl.Parts;
import com.inca.np.anyprint.impl.PrintCalcer;
import com.inca.np.anyprint.impl.Tocnmoney;
import com.inca.np.auth.ClientUserManager;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.GroupDBTableModel;
import com.inca.np.util.DecimalHelper;
import com.inca.np.util.ExprBase;

public class BICellCalcer extends ExprBase {

	ReportcalcerDatasourceIF report = null;

	public BICellCalcer(ReportcalcerDatasourceIF caller) {
		this.report = caller;
	}



	/**
	 * 计算表达式
	 * 
	 * @param r
	 *            行号
	 * @param expr
	 *            表达式
	 * @return 用字符返回表达式
	 * @throws Exception
	 */
	public String calc(int r, String expr) throws Exception {
		expr=expr.trim();
		if (expr.length() == 0)
			return "";
		
		//如果没有"也没有(),说明是汉字标题
		if(expr.indexOf("\"")<0 && expr.indexOf("(")<0 && expr.indexOf("{")<0){
			return expr;
		}
		
		valueStack = new Stack();
		opStack=new Stack<String>();
		calcresult = "";

		try {
			doCalcExpr(r, expr);
		} catch (Exception calce) {
			logger.error("表达式" + expr + "错误:" + calce.getMessage());
			throw calce;
		}
		return new String(calcresult);
	}

	@Override
	public void doCalcExpr(int r, String expr) throws Exception {
		// 将表达式进行分解，放到一个值的栈。自底向下的分析。
		/*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i是数字0-9
		 */
		if (expr == null || expr.length() == 0)
			return;
		expr = expr.trim();
		Vector<String> words = splitWord(expr);
		Enumeration<String> en = words.elements();
		while (en.hasMoreElements()) {
			String word = (String) en.nextElement();

			if (word.equals("{")) {
				String colname = (String) en.nextElement();
				String value = null;
				value = report.getParameter(colname);
				if (value == null) {
					value = report.getDbmodel().getItemValue(r, colname);
					if (value == null) {
						throw new Exception("表达式错误没有找到列" + colname);
					}
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("必须用{}包含列名");
				}
				pushNumberString(value);
			} else if (word.toLowerCase().equals("sum")) {
				// 算总单合计
				String v = calcSum(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("max")) {
				// 算最大值
				String v = calcMax(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("min")) {
				// 算最小值
				String v = calcMin(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("count")) {
				// 算计数
				String v = calcCount(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("pagesum")) {
				// 算页合计
				String v = calcPageSum(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("tocn")) {
				// 算总单合计
				String v = toCn(r, en);
				pushNumberString(v);
			} else if (word.toLowerCase().equals("avg")) {
				// 算平均值
				String v = calcAvg(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("getrow")) {
				// 取当前行号
				String v = String.valueOf(r + 1);
				pushNumberString(v);

				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("getrow语法为getrow()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("getrow语法为getrow()");
				if (en.hasMoreElements() == false)
					throw new Exception("getrow语法为getrow()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("getrow语法为getrow()");
			} else if (word.toLowerCase().equals("rowcount")) {
				// 取总单记录数
				DBTableModel dbmodel = report.getDbmodel();
				String v = calcRowcount(dbmodel);
				pushNumberString(v);

				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("rowcountmst语法为rowcount()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("rowcountmst语法为rowcountm()");
				if (en.hasMoreElements() == false)
					throw new Exception("rowcountmst语法为rowcount()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("rowcountmst语法为rowcount()");
			} else if (word.toLowerCase().equals("username")) {
				String v = ClientUserManager.getCurrentUser().getUsername();
				pushNumberString(v);
				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("username语法为username()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("username语法为username()");
				if (en.hasMoreElements() == false)
					throw new Exception("username语法为username()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("username语法为username()");
			} else if (word.toLowerCase().equals("getitem")) {
				// 取一页的第一行的某列
				if (en.hasMoreElements() == false)
					throw new Exception("getitem语法为getitem(列名)");
				String v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("getitem语法为getitem(列名");
				if (en.hasMoreElements() == false)
					throw new Exception("getitem语法为getitem(列名");
				String colname = (String) en.nextElement();
				if (en.hasMoreElements() == false)
					throw new Exception("getitem语法为getitem(列名");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("getitem语法为getitem(列名");

				// colname可能是表达式
				BICellCalcer exprhelper = new BICellCalcer(report);
				colname = exprhelper.calc(0, colname);

				DBTableModel dbmodel = report.getDbmodel();
				int row = report.getCurrow();
				if (row < 0)
					row = 0;
				if (row >= 0 && row < dbmodel.getRowCount()) {
					v = dbmodel.getItemValue(row, colname);
				} else {
					v = "";
				}
				pushNumberString(v);

			} else if (word.toLowerCase().equals("page")
					|| word.toLowerCase().equals("pageno")) {
				// 取当前页号
				/*
				 * PageInfo pageinfo = report.getCurrentPage(); int pageno = 0;
				 * if (pageinfo != null) pageno = pageinfo.getPageno();
				 */

				int pageno = report.getPrintingpageno();
				String v = String.valueOf(pageno + 1);
				pushNumberString(v);

				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("page语法为page()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("page语法为page()");
				if (en.hasMoreElements() == false)
					throw new Exception("page语法为page()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("page语法为page()");

			} else if (word.toLowerCase().equals("pagecount")) {
				// 取页数

				String v = String.valueOf(report.getPagecount());
				pushNumberString(v);

				if (en.hasMoreElements() == false)
					throw new Exception("pagecount语法为pagecount()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("pagecount语法为pagecount()");
				if (en.hasMoreElements() == false)
					throw new Exception("pagecount语法为pagecount()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("pagecount语法为pagecount()");

			} else {
				super.processWord(word, en, r);
			}

			if (isOperation(word)) {
				memop = word;
			} else {
				memop = "";
			}

		}

		// 结束了，归约
		// System.out.println("---------before calc");
		// dumpStack();

		calcStack();
		// dump值，调试
		// dumpStack();

		calcresult = (String) valueStack.pop();

	}

	/**
	 * 进行合计计算
	 * 
	 * @param en
	 * @return
	 * @throws Exception
	 */
	String calcSum(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "sum函数为sum(列 for 域)或sum(列)，域=all或group 组号";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// s应为all或group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// 算全部的合计
				String sum = calcDecimalSum(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// 算组，继续求组号
				String groupno = "";
				if (en.hasMoreElements()) {
					groupno = (String) en.nextElement();
					if (groupno.equals(")")) {
						// groupno = String.valueOf(report.getCurrentGroupNo());
					} else {
						if (en.hasMoreElements() == false)
							throw new Exception(syntax);
						s = (String) en.nextElement();
						if (s.equals(")") == false)
							throw new Exception(syntax);
					}
				}
				return calcGroupDecimalSum(dbmodel, columnname, row);
				// String sum = report.calcGroupSum(row,
				// Integer.parseInt(groupno), columnname);
				// return sum;
			} else if (s.equals("page")) {
				// 算页合计
				/*
				 * PageInfo pageinfo = report.getCurrentPage(); String sum = "";
				 * if (pageinfo != null)
				 * report.calcPageSum(pageinfo.getStartrow(),
				 * pageinfo.getEndrow(), columnname); if (en.hasMoreElements() ==
				 * false) throw new Exception(syntax); s = (String)
				 * en.nextElement(); if (s.equals(")") == false) throw new
				 * Exception(syntax); return sum;
				 */
				return "";
			}

		} else if (s.equals(")")) {
			// 算全部的合计
			String sum = calcDecimalSum(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	
	/**
	 * 求最大值. 所有的行或分组内的行值的最大值.
	 * @param row
	 * @param en
	 * @param mdflag
	 * @return
	 * @throws Exception
	 */
	String calcMax(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "max函数为max(列 for 域)或sum(列)，域=all或group";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// s应为all或group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// 算全部的合计
				String sum = calcDecimalMax(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// 算组，继续求组号
				String groupno = "";
				if (en.hasMoreElements()) {
					groupno = (String) en.nextElement();
					if (groupno.equals(")")) {
						// groupno = String.valueOf(report.getCurrentGroupNo());
					} else {
						if (en.hasMoreElements() == false)
							throw new Exception(syntax);
						s = (String) en.nextElement();
						if (s.equals(")") == false)
							throw new Exception(syntax);
					}
				}
				return calcGroupDecimalMax(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// 算全部的合计
			String sum = calcDecimalMax(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String calcMin(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "min函数为min(列 for 域)或sum(列)，域=all或group";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// s应为all或group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// 算全部的合计
				String sum = calcDecimalMin(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// 算组，继续求组号
				String groupno = "";
				if (en.hasMoreElements()) {
					groupno = (String) en.nextElement();
					if (groupno.equals(")")) {
						// groupno = String.valueOf(report.getCurrentGroupNo());
					} else {
						if (en.hasMoreElements() == false)
							throw new Exception(syntax);
						s = (String) en.nextElement();
						if (s.equals(")") == false)
							throw new Exception(syntax);
					}
				}
				return calcGroupDecimalMin(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// 算全部的合计
			String sum = calcDecimalMin(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}
	String calcAvg(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "avg函数为min(列 for 域)或sum(列)，域=all或group";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// s应为all或group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// 算全部的合计
				String sum = calcDecimalAvg(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// 算组，继续求组号
				String groupno = "";
				if (en.hasMoreElements()) {
					groupno = (String) en.nextElement();
					if (groupno.equals(")")) {
						// groupno = String.valueOf(report.getCurrentGroupNo());
					} else {
						if (en.hasMoreElements() == false)
							throw new Exception(syntax);
						s = (String) en.nextElement();
						if (s.equals(")") == false)
							throw new Exception(syntax);
					}
				}
				return calcGroupDecimalAvg(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// 算全部的合计
			String sum = calcDecimalAvg(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String calcCount(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "count函数为count(列 for 域)或sum(列)，域=all或group";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// s应为all或group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// 算全部的合计
				String sum = calcDecimalCount(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// 算组，继续求组号
				String groupno = "";
				if (en.hasMoreElements()) {
					groupno = (String) en.nextElement();
					if (groupno.equals(")")) {
						// groupno = String.valueOf(report.getCurrentGroupNo());
					} else {
						if (en.hasMoreElements() == false)
							throw new Exception(syntax);
						s = (String) en.nextElement();
						if (s.equals(")") == false)
							throw new Exception(syntax);
					}
				}
				return calcGroupDecimalCount(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// 算全部的合计
			String sum = calcDecimalCount(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String calcPageSum(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "pagesum函数为pagesum(列)";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// 因为列名是用{}引起来的
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// 检查下一个字符，应该是 for 或者 )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		// 算全部的合计
		int pageno = report.getPrintingpageno();
		String sum = calcPageDecimalSum(dbmodel, pageno, columnname);
		return sum;
	}

	String toCn(int row, Enumeration en) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "tocn(表达式)";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);

		ResultExpr resultexpr = getExpr(en);
		String expr1 = resultexpr.expr;

		BICellCalcer helper = new BICellCalcer(report);
		expr1 = helper.calc(row, expr1);

		return Tocnmoney.toChinese(expr1);
	}


	String calcGroupDecimalSum(DBTableModel dbmodel, String columnname, int row) {
		GroupDBTableModel gdm=(GroupDBTableModel)dbmodel;
		return DecimalHelper.trimZero(gdm.getGroupSum(row, columnname));

	}

	String calcGroupDecimalAvg(DBTableModel dbmodel, String columnname, int row) {
		String ct=calcGroupDecimalCount(dbmodel,columnname,row);
		if(DecimalHelper.comparaDecimal(ct, "0")==0)return "";
		String sum=calcGroupDecimalSum(dbmodel,columnname,row);
		String avg=DecimalHelper.divide(sum,ct,20);
		return DecimalHelper.trimZero(avg);
	}

	String calcGroupDecimalMax(DBTableModel dbmodel, String columnname, int row) {
		GroupDBTableModel gdm=(GroupDBTableModel)dbmodel;
		return gdm.getGroupMax(row, columnname);
	}

	String calcGroupDecimalMin(DBTableModel dbmodel, String columnname, int row) {
		GroupDBTableModel gdm=(GroupDBTableModel)dbmodel;
		return gdm.getGroupMin(row, columnname);
	}
	String calcGroupDecimalCount(DBTableModel dbmodel, String columnname, int row) {
		GroupDBTableModel gdm=(GroupDBTableModel)dbmodel;
		return gdm.getGroupRowcount(row, columnname);
	}

	String calcPageDecimalSum(DBTableModel dbmodel, int pageno,
			String columnname) {
		Splitpageinfo pinfo = report.getPageinfo(pageno);
		BigDecimal sum = new BigDecimal(0);
		for (int r = pinfo.startrow; r <= pinfo.endrow; r++) {
			BigDecimal v = new BigDecimal(dbmodel.getItemValue(r, columnname));
			sum = sum.add(v);
		}
		return sum.toPlainString();
	}

	public static void main(String[] argv) {
		BICellCalcer calcer = new BICellCalcer(null);
		String s = "";
		try {
			// s = calcer.calc(0,"\"你好\"");
			//s = calcer.calc(0, "\"a\" <> \"b\"");
			//s = calcer.calc(0, "round(\"\",2)");
			s = calcer.calc(0, "if(3<\"\",2,1)");
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
		System.out.println(s);
	}

	@Override
	protected ExprBase recreate() {
		return new BICellCalcer(report);
	}
}
