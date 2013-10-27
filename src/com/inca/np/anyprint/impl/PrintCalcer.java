package com.inca.np.anyprint.impl;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.anyprint.impl.Parts.Splitpageinfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.ExprBase;

/**
 * 单据打印使用的表达式计算器.
 * @author user
 *
 */
public class PrintCalcer extends ExprBase{

	Parts report = null;

	public PrintCalcer(Parts caller) {
		this.report = caller;
	}


	public void doCalcExpr(int r, String expr) throws Exception {
		// 将表达式进行分解，放到一个值的栈。自底向下的分析。
		/*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i是数字0-9
		 */
		if (expr == null || expr.length() == 0)
			return;
		expr = expr.trim();
		Vector<String> words = splitWord(expr);
		Enumeration<String>  en = words.elements();
		while (en.hasMoreElements()) {
			String word = (String) en.nextElement();
			if (word.equals("{")) {
				String colname = (String) en.nextElement();
				if(colname.equalsIgnoreCase("printcopy")){
					pushNumberString(String.valueOf(report.getPrintcopy()));
					continue;
				}
				if(colname.equalsIgnoreCase("printcopys")){
					pushNumberString(String.valueOf(report.getPrintcopys()));
					continue;
				}
				String value = report.getDbmodel().getItemValue(r, colname);
				if (value == null) {
					throw new Exception("表达式错误没有找到列" + colname);
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("必须用{}包含列名");
				}
				pushNumberString(value);
			} else if (word.toLowerCase().equals("printcopy")) {
				// 第几份?
				String v = String.valueOf(report.getPrintcopy());
				pushNumberString(v);
				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("语法应为printcopy()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("语法应为printcopy()");
				if (en.hasMoreElements() == false)
					throw new Exception("语法应为printcopy()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("语法应为printcopy()");

			} else if (word.toLowerCase().equals("printcopys")) {
				// 共几份
				String v = String.valueOf(report.getPrintcopys());
				pushNumberString(v);
				// 后面两个()
				if (en.hasMoreElements() == false)
					throw new Exception("语法应为printcopys()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("语法应为printcopys()");
				if (en.hasMoreElements() == false)
					throw new Exception("语法应为printcopys()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("语法应为printcopys()");
			} else if (word.toLowerCase().equals("sum")) {
				// 算总单合计
				String v = calcSum(r, en, "master");
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
				// String v=calcAvg();
				// exprStack.push(v);
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
				PrintCalcer exprhelper = new PrintCalcer(report);
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
				processWord(word, en, r);
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
				return "";
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

		PrintCalcer helper = new PrintCalcer(report);
		expr1 = helper.calc(row, expr1);

		return Tocnmoney.toChinese(expr1);
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
		PrintCalcer calcer = new PrintCalcer(null);
		String s = "";
		try {
			// s = calcer.calc(0,"\"你好\"");
			//s = calcer.calc(0, "3==-3");
			//s = calcer.calc(0, "1+1==3 || 3==3 && 2+3/4>=2.75");
			s = calcer.calc(0, "1+2-3*4/5 != 1+2-3*4/5 || 1+2-3*4/5<0.6 && 3!=4");
			System.out.println(s);
			
			s = calcer.calc(0, "today()+\" \"+now()");
			System.out.println(s);
			s = calcer.calc(3, "getrow()");
			System.out.println(s);

			s = calcer.calc(3, "3+(-2)");
			System.out.println(s);

		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	@Override
	protected ExprBase recreate() {
		return new PrintCalcer(report);
		}
}
