package com.inca.npworkflow.server;

import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.anyprint.impl.Tocnmoney;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.ExprBase;

public class Dataitemcalcer extends ExprBase{

	DBTableModel basedbmodel = null;

	public Dataitemcalcer(DBTableModel basedbmodel) {
		this.basedbmodel = basedbmodel;
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
				String value = basedbmodel.getItemValue(r, colname);
				if (value == null) {
					throw new Exception("表达式错误没有找到列" + colname);
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("必须用{}包含列名");
				}
        		super.pushNumberString(value);
			} else if (word.toLowerCase().equals("rowcount")) {
				// 取总单记录数
				DBTableModel dbmodel = basedbmodel;
				String v = String
						.valueOf(String.valueOf(dbmodel.getRowCount()));
        		super.pushNumberString(v);

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
			} else if (word.toLowerCase().equals("sum")) {
				// 算总单合计
				String v = calcSum(r, en, "master");
        		super.pushNumberString(v);
			} else if (word.toLowerCase().equals("tocn")) {
				// 算总单合计
				String v = toCn(r, en);
        		super.pushNumberString(v);
			} else if (word.toLowerCase().equals("avg")) {
				// 算平均值
				// String v=calcAvg();
				// exprStack.push(v);
			} else if (word.equalsIgnoreCase("select")
					|| word.equalsIgnoreCase("update")) {
				calcresult = expr;
				return;
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
	protected String calcSum(int row, Enumeration en, String mdflag) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = basedbmodel;
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

	String toCn(int row, Enumeration en) throws Exception {
		// 右括号
		DBTableModel dbmodel = null;
		dbmodel = basedbmodel;
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

		Dataitemcalcer helper = new Dataitemcalcer(basedbmodel);
		expr1 = helper.calc(row, expr1);

		return Tocnmoney.toChinese(expr1);
	}


	@Override
	protected ExprBase recreate() {
		return new Dataitemcalcer(basedbmodel);
	}
	
	public static void main(String[] argv) {
		Dataitemcalcer calcer = new Dataitemcalcer(null);
		String s = "";
		try {
			// s = calcer.calc(0,"\"你好\"");
			// s = calcer.calc(0, "64+(2+-5)");
			// s = calcer.calc(0, "1+2-3*4/5");
			//s = calcer.calc(0, "1+(2-3)*4/5");
			//s = calcer.calc(0, "(1+(2-3))*4/5");
			//s = calcer.calc(0, "1>2");
			//s = calcer.calc(0, "-123.34");
			
			//s = calcer.calc(0, "2=3");
			
			//s = calcer.calc(0,"\"你好\"+\"sdf\"");
			//s = calcer.calc(0,"103>1000 || 84=84");
			//s = calcer.calc(0,"if(1=2,   (2+(2-3))*4/5 , 1+(2-3)*4/5)");
			//s = calcer.calc(0, "if(4+2*(3-10)/5==1.2,'true','false')");
			
			//s = calcer.calc(0, "1+1==2 || 2==3 && 2+3/4=3");
			//s = calcer.calc(0, "1+2-3*4/5 != 1+2-3*4/5 || 1+2-3*4/5<=0.6 && 3!=4");
			
			//s = calcer.calc(0, "-1+2-3*4/5");		
			//s = calcer.calc(0, "today()+\" \"+now()");
			//s = calcer.calc(3, "getrow()");
			//s = calcer.calc(0, "(21200>20000 and 2<2 ) or (-2<-1 and 2<4)");
			//s = calcer.calc(0, "7*-7");
			//s = calcer.calc(0, "21200>20000&&2<2");
			//s = calcer.calc(0, "-(1-3)*7/2");
			
			//s = calcer.calc(0, "-(3+0.14)*-2.1");
			// s = calcer.calc(0, "if(4+2*(3-10)/5==1.2000-0,'true','false')");
			
			
			s = calcer.calc(0, "round(5.37,1) + 3");
			
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
		System.out.println(s);
	}

}
