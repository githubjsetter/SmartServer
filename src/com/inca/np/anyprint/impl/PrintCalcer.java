package com.inca.np.anyprint.impl;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.anyprint.impl.Parts.Splitpageinfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.ExprBase;

/**
 * ���ݴ�ӡʹ�õı��ʽ������.
 * @author user
 *
 */
public class PrintCalcer extends ExprBase{

	Parts report = null;

	public PrintCalcer(Parts caller) {
		this.report = caller;
	}


	public void doCalcExpr(int r, String expr) throws Exception {
		// �����ʽ���зֽ⣬�ŵ�һ��ֵ��ջ���Ե����µķ�����
		/*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i������0-9
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
					throw new Exception("���ʽ����û���ҵ���" + colname);
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("������{}��������");
				}
				pushNumberString(value);
			} else if (word.toLowerCase().equals("printcopy")) {
				// �ڼ���?
				String v = String.valueOf(report.getPrintcopy());
				pushNumberString(v);
				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("�﷨ӦΪprintcopy()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("�﷨ӦΪprintcopy()");
				if (en.hasMoreElements() == false)
					throw new Exception("�﷨ӦΪprintcopy()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("�﷨ӦΪprintcopy()");

			} else if (word.toLowerCase().equals("printcopys")) {
				// ������
				String v = String.valueOf(report.getPrintcopys());
				pushNumberString(v);
				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("�﷨ӦΪprintcopys()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("�﷨ӦΪprintcopys()");
				if (en.hasMoreElements() == false)
					throw new Exception("�﷨ӦΪprintcopys()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("�﷨ӦΪprintcopys()");
			} else if (word.toLowerCase().equals("sum")) {
				// ���ܵ��ϼ�
				String v = calcSum(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("pagesum")) {
				// ��ҳ�ϼ�
				String v = calcPageSum(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("tocn")) {
				// ���ܵ��ϼ�
				String v = toCn(r, en);
				pushNumberString(v);
			} else if (word.toLowerCase().equals("avg")) {
				// ��ƽ��ֵ
				// String v=calcAvg();
				// exprStack.push(v);
			} else if (word.toLowerCase().equals("getrow")) {
				// ȡ��ǰ�к�
				String v = String.valueOf(r + 1);
				pushNumberString(v);

				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("getrow�﷨Ϊgetrow()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("getrow�﷨Ϊgetrow()");
				if (en.hasMoreElements() == false)
					throw new Exception("getrow�﷨Ϊgetrow()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("getrow�﷨Ϊgetrow()");
			} else if (word.toLowerCase().equals("rowcount")) {
				// ȡ�ܵ���¼��
				DBTableModel dbmodel = report.getDbmodel();
				String v = calcRowcount(dbmodel);
				pushNumberString(v);

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
			} else if (word.toLowerCase().equals("getitem")) {
				// ȡһҳ�ĵ�һ�е�ĳ��
				if (en.hasMoreElements() == false)
					throw new Exception("getitem�﷨Ϊgetitem(����)");
				String v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("getitem�﷨Ϊgetitem(����");
				if (en.hasMoreElements() == false)
					throw new Exception("getitem�﷨Ϊgetitem(����");
				String colname = (String) en.nextElement();
				if (en.hasMoreElements() == false)
					throw new Exception("getitem�﷨Ϊgetitem(����");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("getitem�﷨Ϊgetitem(����");

				// colname�����Ǳ��ʽ
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
				// ȡ��ǰҳ��
				/*
				 * PageInfo pageinfo = report.getCurrentPage(); int pageno = 0;
				 * if (pageinfo != null) pageno = pageinfo.getPageno();
				 */

				int pageno = report.getPrintingpageno();
				String v = String.valueOf(pageno + 1);
				pushNumberString(v);

				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("page�﷨Ϊpage()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("page�﷨Ϊpage()");
				if (en.hasMoreElements() == false)
					throw new Exception("page�﷨Ϊpage()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("page�﷨Ϊpage()");

			} else if (word.toLowerCase().equals("pagecount")) {
				// ȡҳ��

				String v = String.valueOf(report.getPagecount());
				pushNumberString(v);

				if (en.hasMoreElements() == false)
					throw new Exception("pagecount�﷨Ϊpagecount()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("pagecount�﷨Ϊpagecount()");
				if (en.hasMoreElements() == false)
					throw new Exception("pagecount�﷨Ϊpagecount()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("pagecount�﷨Ϊpagecount()");

			} else {
				processWord(word, en, r);
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
	String calcSum(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
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

	String calcPageSum(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "pagesum����Ϊpagesum(��)";
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

		// ��ȫ���ĺϼ�
		int pageno = report.getPrintingpageno();
		String sum = calcPageDecimalSum(dbmodel, pageno, columnname);
		return sum;
	}

	String toCn(int row, Enumeration en) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
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
			// s = calcer.calc(0,"\"���\"");
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
