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
	 * ������ʽ
	 * 
	 * @param r
	 *            �к�
	 * @param expr
	 *            ���ʽ
	 * @return ���ַ����ر��ʽ
	 * @throws Exception
	 */
	public String calc(int r, String expr) throws Exception {
		expr=expr.trim();
		if (expr.length() == 0)
			return "";
		
		//���û��"Ҳû��(),˵���Ǻ��ֱ���
		if(expr.indexOf("\"")<0 && expr.indexOf("(")<0 && expr.indexOf("{")<0){
			return expr;
		}
		
		valueStack = new Stack();
		opStack=new Stack<String>();
		calcresult = "";

		try {
			doCalcExpr(r, expr);
		} catch (Exception calce) {
			logger.error("���ʽ" + expr + "����:" + calce.getMessage());
			throw calce;
		}
		return new String(calcresult);
	}

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
				String value = null;
				value = report.getParameter(colname);
				if (value == null) {
					value = report.getDbmodel().getItemValue(r, colname);
					if (value == null) {
						throw new Exception("���ʽ����û���ҵ���" + colname);
					}
				}
				word = (String) en.nextElement();
				if (!word.equals("}")) {
					throw new Exception("������{}��������");
				}
				pushNumberString(value);
			} else if (word.toLowerCase().equals("sum")) {
				// ���ܵ��ϼ�
				String v = calcSum(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("max")) {
				// �����ֵ
				String v = calcMax(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("min")) {
				// ����Сֵ
				String v = calcMin(r, en, "master");
				pushNumberString(v);
			} else if (word.toLowerCase().equals("count")) {
				// �����
				String v = calcCount(r, en, "master");
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
				String v = calcAvg(r, en, "master");
				pushNumberString(v);
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
			} else if (word.toLowerCase().equals("username")) {
				String v = ClientUserManager.getCurrentUser().getUsername();
				pushNumberString(v);
				// ��������()
				if (en.hasMoreElements() == false)
					throw new Exception("username�﷨Ϊusername()");
				v = (String) en.nextElement();
				if (v.equals("(") == false)
					throw new Exception("username�﷨Ϊusername()");
				if (en.hasMoreElements() == false)
					throw new Exception("username�﷨Ϊusername()");
				v = (String) en.nextElement();
				if (v.equals(")") == false)
					throw new Exception("username�﷨Ϊusername()");
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
				return calcGroupDecimalSum(dbmodel, columnname, row);
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

	
	/**
	 * �����ֵ. ���е��л�����ڵ���ֵ�����ֵ.
	 * @param row
	 * @param en
	 * @param mdflag
	 * @return
	 * @throws Exception
	 */
	String calcMax(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "max����Ϊmax(�� for ��)��sum(��)����=all��group";
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
				String sum = calcDecimalMax(dbmodel, columnname);
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
				return calcGroupDecimalMax(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// ��ȫ���ĺϼ�
			String sum = calcDecimalMax(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String calcMin(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "min����Ϊmin(�� for ��)��sum(��)����=all��group";
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
				String sum = calcDecimalMin(dbmodel, columnname);
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
				return calcGroupDecimalMin(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// ��ȫ���ĺϼ�
			String sum = calcDecimalMin(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}
	String calcAvg(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "avg����Ϊmin(�� for ��)��sum(��)����=all��group";
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
				String sum = calcDecimalAvg(dbmodel, columnname);
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
				return calcGroupDecimalAvg(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// ��ȫ���ĺϼ�
			String sum = calcDecimalAvg(dbmodel, columnname);
			return sum;
		} else
			throw new Exception(syntax);
		return "";
	}

	String calcCount(int row, Enumeration en, String mdflag) throws Exception {
		// ������
		DBTableModel dbmodel = null;
		dbmodel = report.getDbmodel();
		String s;
		String syntax = "count����Ϊcount(�� for ��)��sum(��)����=all��group";
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
				String sum = calcDecimalCount(dbmodel, columnname);
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
				return calcGroupDecimalCount(dbmodel, columnname, row);
			}

		} else if (s.equals(")")) {
			// ��ȫ���ĺϼ�
			String sum = calcDecimalCount(dbmodel, columnname);
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
			// s = calcer.calc(0,"\"���\"");
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
