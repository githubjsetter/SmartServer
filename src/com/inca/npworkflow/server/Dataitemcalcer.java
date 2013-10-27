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
	 * ������ʽ
	 * 
	 * @param r
	 *            �к�
	 * @param expr
	 *            ���ʽ
	 * @return ���ַ����ر��ʽ
	 * @throws Exception
	 */

	@Override
	public void doCalcExpr(int r, String expr) throws Exception {
		// �����ʽ���зֽ⣬�ŵ�һ��ֵ��ջ���Ե����µķ�����
		/*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i������0-9
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
					throw new Exception("���ʽ����û���ҵ���" + colname);
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("������{}��������");
				}
        		super.pushNumberString(value);
			} else if (word.toLowerCase().equals("rowcount")) {
				// ȡ�ܵ���¼��
				DBTableModel dbmodel = basedbmodel;
				String v = String
						.valueOf(String.valueOf(dbmodel.getRowCount()));
        		super.pushNumberString(v);

				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("rowcountmst�﷨Ϊrowcount()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("rowcountmst�﷨Ϊrowcountm()");
				if (en.hasMoreElements() == false)
					throw new Exception("rowcountmst�﷨Ϊrowcount()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("rowcountmst�﷨Ϊrowcount()");
			} else if (word.toLowerCase().equals("sum")) {
				// ���ܵ��ϼ�
				String v = calcSum(r, en, "master");
        		super.pushNumberString(v);
			} else if (word.toLowerCase().equals("tocn")) {
				// ���ܵ��ϼ�
				String v = toCn(r, en);
        		super.pushNumberString(v);
			} else if (word.toLowerCase().equals("avg")) {
				// ��ƽ��ֵ
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

		// �����ˣ���Լ
		// System.out.println("---------before calc");
		// dumpStack();

		calcStack();
		// dumpֵ������
		// dumpStack();

		calcresult = (String) valueStack.pop();

	}


	/**
	 * ���кϼƼ���
	 * 
	 * @param en
	 * @return
	 * @throws Exception
	 */
	protected String calcSum(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = basedbmodel;
		String s;
		String syntax = "sum����Ϊsum(�� for ��)��sum(��)����=all��group ���";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();
		if (columnname.equals("{")) {
			// ��Ϊ��������{}��������
			columnname = (String) en.nextElement();
			en.nextElement();
		}

		// �����һ���ַ���Ӧ���� for ���� )
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		s = s.toLowerCase();

		if (s.equals("for")) {
			if (en.hasMoreElements() == false)
				throw new Exception(syntax);
			s = (String) en.nextElement();
			// sӦΪall��group
			s = s.toLowerCase();
			if (s.equals("all")) {
				// ��ȫ���ĺϼ�
				String sum = calcDecimalSum(dbmodel, columnname);
				if (en.hasMoreElements() == false)
					throw new Exception(syntax);
				s = (String) en.nextElement();
				if (s.equals(")") == false)
					throw new Exception(syntax);
				return sum;
			} else if (s.equals("group")) {
				// ���飬���������
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
				// ��ҳ�ϼ�
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
			// ��ȫ���ĺϼ�
			String sum = calcDecimalSum(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String toCn(int row, Enumeration en) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = basedbmodel;
		String s;
		String syntax = "tocn(���ʽ)";
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
			// s = calcer.calc(0,"\"���\"");
			// s = calcer.calc(0, "64+(2+-5)");
			// s = calcer.calc(0, "1+2-3*4/5");
			//s = calcer.calc(0, "1+(2-3)*4/5");
			//s = calcer.calc(0, "(1+(2-3))*4/5");
			//s = calcer.calc(0, "1>2");
			//s = calcer.calc(0, "-123.34");
			
			//s = calcer.calc(0, "2=3");
			
			//s = calcer.calc(0,"\"���\"+\"sdf\"");
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
