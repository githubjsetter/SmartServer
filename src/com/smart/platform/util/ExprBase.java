package com.smart.platform.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.DBTableModel;

/**
 * ���ʽ�������
 * 
 * @author user
 * 
 */
public abstract class ExprBase {
	protected String calcresult;
	/**
	 * ��ֵջ
	 */
	protected Stack<String> valueStack;

	/**
	 * ������ջ
	 */
	protected Stack<String> opStack;

	protected String memop = "";
	//protected String sign = "";
	protected Category logger = Category.getInstance(ExprBase.class);

	public String calc(int r, String expr) throws Exception {
		expr=expr.trim();
		if (expr.length() == 0)
			return "";
		valueStack = new Stack();
		opStack = new Stack<String>();
		calcresult = "";
		// logger.debug("��ʼ����:"+expr);
		try {
			doCalcExpr(r, expr);
		} catch (Exception calce) {
			logger.error("���ʽ" + expr + "����:" + calce.getMessage(), calce);
			throw calce;
		}
		// logger.debug("���:"+expr+"="+calcresult);
		return new String(calcresult);
	}

	public abstract void doCalcExpr(int r, String expr) throws Exception;

	protected void processWord(String word, Enumeration<String> en, int r)
			throws Exception {
		
		if (isMathOperation(word) || isComparaOperation(word)
				|| isLogicOperation(word)) {
			if (word.equals("+") || word.equals("-")) {
				if (memop.length() > 0 || valueStack.size() == 0) {
					//sign = word;
					//push -1 *
					valueStack.push("-1");
					opStack.push("*");
					return;
				}
			}
			pushOperation(word);
		} else if (word.charAt(0) == '"' || word.charAt(0) == '\'') {
			// ���ַ���
			word = word.substring(1, word.length() - 1);
			pushNumberString(word);
		} else if (isNumber(word)) {
			pushNumberString(word);
		} else if (word.equals("(")) {
			int ct = 1;
			StringBuffer sb = new StringBuffer();
			while (ct > 0) {
				String s = en.nextElement();
				if (s.equals(")")) {
					ct--;
				} else if (s.equals("(")) {
					ct++;
				}
				if (ct == 0) {
					break;
				} else {
					sb.append(" ");
					sb.append(s);
				}
			}

			ExprBase helper = recreate();
			String tmpexpr = helper.calc(r, sb.toString());
			pushNumberString(tmpexpr);
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

		} else if (word.toLowerCase().equals("string")) {
			// String(expr1[,expr2]);
			// ����һ��(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			String v = "";
			ExprBase helper = recreate();
			v = helper.calc(r, expr1);
			if (resultexpr.endexpr.equals(")")) {
			} else if (resultexpr.endexpr.equals(",")) {
				resultexpr = getExpr(en);
			} else {
				System.out.println("getExpr���طָ�������:" + resultexpr.endexpr);
			}

			pushNumberString(v);

		} else if (word.toLowerCase().equals("today")) {
			// ���ص�ǰ������
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			String v = formater.format(new java.util.Date());
			pushNumberString(v);

			// ��������()
			if (en.hasMoreElements() == false)
				throw new Exception("today�﷨Ϊtoday()");
			v = (String) en.nextElement();
			if (v.equals("(") == false)
				throw new Exception("today�﷨Ϊtoday()");
			if (en.hasMoreElements() == false)
				throw new Exception("today�﷨Ϊtoday()");
			v = (String) en.nextElement();
			if (v.equals(")") == false)
				throw new Exception("today�﷨Ϊtoday()");

		} else if (word.toLowerCase().equals("now")) {
			// ���ص�ǰ��ʱ��
			SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
			String v = formater.format(new java.util.Date());
			pushNumberString(v);

			// ��������()
			if (en.hasMoreElements() == false)
				throw new Exception("now�﷨Ϊnow()");
			v = (String) en.nextElement();
			if (v.equals("(") == false)
				throw new Exception("now�﷨Ϊnow()");
			if (en.hasMoreElements() == false)
				throw new Exception("now�﷨Ϊnow()");
			v = (String) en.nextElement();
			if (v.equals(")") == false)
				throw new Exception("now�﷨Ϊnow()");

		} else if (word.toLowerCase().equals("round")) {
			// ����round���� round(expr1,expr2)
			// ����(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			ExprBase helper = recreate();
			expr1 = helper.calc(r, expr1);

			resultexpr = getExpr(en);
			String expr2 = resultexpr.expr;
			expr2 = helper.calc(r, expr2);

			String v = doRound(expr1, expr2);
			pushNumberString(v);

		} else if (word.toLowerCase().equals("abs")) {
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			ExprBase helper = recreate();
			expr1 = helper.calc(r, expr1);

			BigDecimal value = new BigDecimal(expr1);
			value = value.abs();
			String v = value.toString();
			pushNumberString(v);

		} else if (word.toLowerCase().equals("isnull")) {
			// ����isnull���� isnull(expr)
			// ����(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			ExprBase helper = recreate();
			expr1 = helper.calc(r, expr1);
			String v;
			if (expr1.length() == 0)
				v = "1";
			else
				v = "0";
			pushNumberString(v);

		} else if (word.toLowerCase().equals("if"))
		// ���Ӷ�IF������֧��
		{
			// ����if���� if(expr,value1,value2)
			// ����(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			ExprBase helper = recreate();	
			expr1 = helper.calc(r, expr1);

			resultexpr = getExpr(en);
			String expr2 = resultexpr.expr;
			expr2 = helper.calc(r, expr2);

			resultexpr = getExpr(en);
			String expr3 = resultexpr.expr;
			expr3 = helper.calc(r, expr3);

			String v;
			if (new Long(expr1).longValue() == 1)
				v = expr2;
			else
				v = expr3;
			pushNumberString(v);
		} else if (word.toLowerCase().equals("long")) {
			// ����long(expr)����
			// ����(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;
			ExprBase helper = recreate();
			expr1 = helper.calc(r, expr1);
			String v = "";
			try {
				long ll = Long.parseLong(expr1);
				v = String.valueOf(ll);
			} catch (Exception elong) {
				v = "0";
			}
			pushNumberString(v);

		} else if (word.toLowerCase().equals("describe")
				|| word.toLowerCase().equals("desc")) {
			// describe����
			// ����(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;
			String v = doDescribe(expr1);
			// System.out.println("describe,expr ="+expr+" and result v =
			// "+v);
			pushNumberString(v);
		} else if (word.toLowerCase().equals("string")) {
			// String(expr1[,expr2]);
			// ����һ��(
			en.nextElement();
			ResultExpr resultexpr = getExpr(en);
			String expr1 = resultexpr.expr;

			String v = "";
			ExprBase helper = recreate();
			v = helper.calc(r, expr1);
			if (resultexpr.endexpr.equals(")")) {
			} else if (resultexpr.endexpr.equals(",")) {
				resultexpr = getExpr(en);
			} else {
				System.out.println("getExpr���طָ�������:" + resultexpr.endexpr);
			}

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
		}else{
			throw new Exception("�޷�����"+word);
		}

	}

	protected void pushNumberString(String word) {
		valueStack.push(word);
		//sign = "";
	}

	/**
	 * ѹ�����ջ
	 * 
	 * @param word
	 */
	protected void pushOperation(String op) throws Exception {
		for (;;) {
			if (opStack.isEmpty()) {
				opStack.push(op);
				break;
			}
			String lastop = opStack.get(opStack.size() - 1);
			int lastoplevel = getOperationLevel(lastop);
			int level = getOperationLevel(op);
			// �������󣬾�ѹ�룮
			if (level > lastoplevel) {
				opStack.push(op);
				break;
			} else {
				// ����һ��
				int oldsize=valueStack.size();
				doCalcStack();
				int newsize=valueStack.size();
				if(oldsize==newsize){
					break;
				}
			}
		}
	}

	protected boolean isLogicOperation(String s) {
		return s.equalsIgnoreCase("and") || s.equals("&&")
				|| s.equalsIgnoreCase("or") || s.equals("||");
	}

	protected boolean isOperation(String s) {
		return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")
				|| s.equals("(") || s.equals(">") || s.equals(">=")
				|| s.equals("=") || s.equals("<") || s.equals("<=")
				|| s.equals("==") || s.equals("!=")
				|| s.equalsIgnoreCase("and") || s.equals("&&")
				|| s.equalsIgnoreCase("or") || s.equals("||");
	}

	protected boolean isMathOperation(String s) {
		return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
	}

	/**
	 * ���ز�������.��Խ��Խ��.Խ��Խ����
	 * 
	 * @param s
	 * @return
	 */
	protected int getOperationLevel(String s) {
		if (s.equals("(") || s.equals(")")) {
			return 10;
		} else if (s.equals("*") || s.equals("/")) {
			return 9;
		} else if (s.equals("+") || s.equals("-")) {
			return 8;
		} else if (isComparaOperation(s)) {
			return 7;
		} else if (s.equals("&&") || s.equalsIgnoreCase("and")) {
			return 6;
		} else if (s.equals("||") || s.equalsIgnoreCase("or")) {
			return 5;
		}
		System.err.println("getOperationLevel unknown " + s);
		return 0;
	}

	protected boolean isComparaOperation(String s) {
		return s.equals(">") || s.equals(">=") || s.equals("=")
				|| s.equals("<") || s.equals("<=") || s.equals("==")
				|| s.equals("!=");
	}

	protected void clearStack() {
		valueStack.removeAllElements();
	}

	// ����ջ�����£�����)������2��������
	protected String getLastOp() {
		int len = valueStack.size();
		if (len < 1)
			return null;
		return (String) valueStack.elementAt(len - 1);
	}

	protected boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected class ResultExpr {
		public String endexpr;// �������ţ�ӦΪ,��)
		public String expr;// �ֽ���ı��ʽ

		public ResultExpr(String endexpr, String expr) {
			this.endexpr = endexpr;
			this.expr = expr;
		}

	}

	protected boolean isspace(char c) {
		if (c == ' ' || c == '\t')
			return true;
		return false;
	}

	protected boolean isSeparator(char c) {
		if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*'
				|| c == '/' || c == ',' || /* c == '.' || */c == ':'
				|| c == '>' || c == '=' || c == '<' || c == '{' || c == '}'
				|| c == '!'  || c == '&' || c == '|')
			return true;

		return false;
	}

	protected void calcStack() throws Exception {
		if (valueStack.size() <= 1)
			return;

		for (; valueStack.size() > 1;) {
			int beforect = valueStack.size();
			if (!doCalcStack()) {
				break;
			}
			int endct = valueStack.size();
			if (beforect == endct) {
				break;
			}
		}
	}

	// ��ջexprStack���й�Լ
	protected boolean doCalcStack() throws Exception {
		/*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i������0-9
		 */
		if (valueStack.size() == 1)
			return true;

		if (opStack.isEmpty())
			return true;

		String left, op, right;
		op = opStack.pop();

		// ��ջ�����з���
		right = (String) valueStack.pop();
		left = (String) valueStack.pop();
		String result = calc(left, op, right);
		valueStack.push(result);

		return true;
	}

	// �Ա��ʽ����ֵ�ļ���
	protected String calc(String left, String op, String right)
			throws Exception {
		if (isNumber(left) && isNumber(right)
				&& left.charAt(left.length() - 1) != '.'
				&& right.charAt(right.length() - 1) != '.')
			return calcNumber(left, op, right);

		if (op.equals("+")) {
			return left + right;
		}else if(op.equals("=")){
			return left.equals(right)?"1":"0";
		}else if(op.equals(">")){
			return left.compareTo(right)>0?"1":"0";
		}else if(op.equals(">=")){
			return left.compareTo(right)>=0?"1":"0";
		}else if(op.equals("<")){
			return left.compareTo(right)<0?"1":"0";
		}else if(op.equals("<=")){
			return left.compareTo(right)<=0?"1":"0";
		}else if(op.equals("<>")){
			return left.compareTo(right)!=0?"1":"0";
		}else if(op.equals("!=")){
			return left.compareTo(right)!=0?"1":"0";
		}
		throw new Exception("�����ַ�������������,expr1=" + left + ",expr2=" + right+",op="+op);
	}

	protected String calcNumber(String left, String op, String right)
			throws Exception {
		if (left == null || left.length() == 0 || left.equals(" ")) {
			left = "0";
		}
		if (right == null || right.length() == 0 || right.equals(" ")) {
			right = "0";
		}

		BigDecimal d1 = DecimalHelper.toDec(left);
		BigDecimal d2 = DecimalHelper.toDec(right);
		BigDecimal zero = DecimalHelper.toDec("0");
		BigDecimal one = DecimalHelper.toDec("1");
		double result = 0;
		if (op.equals("+"))
			return DecimalHelper.removeZero(DecimalHelper.add(left, right, 10));
		else if (op.equals("-"))
			return DecimalHelper.removeZero(DecimalHelper.sub(left, right, 10));
		else if (op.equals("*"))
			return DecimalHelper.removeZero(DecimalHelper
					.multi(left, right, 10));
		else if (op.equals("/")) {
			if (d2.compareTo(new BigDecimal("0")) == 0) {
				// throw new Exception("����Ϊ0");
				return " ";
			}
			return DecimalHelper.removeZero(DecimalHelper.divide(left, right,
					10));
		} else if (op.equals(">")) {
			if (d1.compareTo(d2) > 0)
				result = 1;
			else
				result = 0;
		} else if (op.equals("=") || op.equals("==")) {
			if (d1.compareTo(d2) == 0)
				result = 1;
			else
				result = 0;
		} else if (op.equals("<")) {
			if (d1.compareTo(d2) < 0)
				result = 1;
			else
				result = 0;
		} else if (op.equals("!=")) {
			result = d1.compareTo(d2) != 0 ? 1 : 0;
		} else if (op.equals(">=")) {
			if (d1.compareTo(d2) >= 0)
				result = 1;
			else
				result = 0;
		} else if (op.equals("<=")) {
			if (d1.compareTo(d2) <= 0)
				result = 1;
			else
				result = 0;
		} else if (op.equals("and") || op.equals("&&")) {
			if (d1.compareTo(one) == 0 && d2.compareTo(one) == 0)
				result = 1;
			else
				result = 0;

		} else if (op.equals("or") || op.equals("||")) {
			if (d1.compareTo(one) == 0 || d2.compareTo(one) == 0)
				result = 1;
			else
				result = 0;

		} else {
			throw new Exception("���ܴ��������" + op);
		}

		String strresult = new java.math.BigDecimal(result).toString();
		if (strresult.startsWith("."))
			strresult = "0" + strresult;
		return strresult;

	}

	/**
	 * ��Enumeration����)��,���������ر��ʽ
	 */
	protected ResultExpr getExpr(Enumeration en) {
		String expr = "";
		String endexpr = "";
		int kh = 0;
		while (en.hasMoreElements()) {
			String v = (String) en.nextElement();
			if (v.equals("("))
				kh++;
			else if (v.equals(",")) {
				if (kh == 0) {
					endexpr = v;
					break;
				}
			} else if (v.equals(")")) {
				if (kh == 0) {
					endexpr = v;
					break;
				}
				kh--;
			}
			expr += v + " ";
		}
		return new ResultExpr(endexpr, expr);
	}

	/**
	 * �����е���Ϣ
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected String doDescribe(String expr) throws Exception {
		// ȥͷβ��"
		expr = expr.trim();
		if (expr.charAt(0) == '"')
			expr = expr.substring(1, expr.length() - 1);
		// ֻ��ʶ�� <����>.<λ��> λ��Ϊx,y,width,height
		int pos = expr.indexOf('.');
		if (pos < 0) {
			String errs = "describe������ʽ" + expr + "��û���ҵ�\".\"";
			throw new Exception(errs);
		}

		String colname = expr.substring(0, pos);
		colname = colname.toLowerCase();
		colname = colname.trim();
		String posname = expr.substring(pos + 1);
		posname = posname.trim();
		posname = posname.toLowerCase();
		return "xxx";
		// ������
		/*
		 * TextReportColumn column = report.getBodyColumn(colname);
		 * 
		 * //����λ�� if (posname.equals("x")) { return
		 * String.valueOf(column.getX()); } else if (posname.equals("width")) {
		 * return String.valueOf(column.getWidth()); } else if
		 * (posname.equals("height")) { return
		 * String.valueOf(report.getRowHeight()); } else if
		 * (posname.equals("format")) { return column.getFormat(); } else if
		 * (posname.equals("align")) { return column.getAlign(); } else { String
		 * errs = "describe������ʽ" + expr + "��û����λ��" + posname; throw new
		 * Exception(errs); }
		 */
	}

	protected String doRound(String expr1, String expr2) {
		if(expr1.length()==0)return "";
		double v = Double.parseDouble(expr1.trim());
		int n = Integer.parseInt(expr2.trim());
		StringBuffer sb = new StringBuffer("0");
		for (int i = 0; i < n; i++) {
			if (i == 0)
				sb.append(".");
			sb.append("0");
		}

		DecimalFormat df = new DecimalFormat(sb.toString());
		return df.format(v);
	}

	protected Vector splitWord(String expr) {
		Vector result = new Vector();
		int i;
		StringBuffer word = new StringBuffer();
		for (i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i);
			if (isspace(c)) {
				String v = word.toString();
				if (v.length() > 0) {
					result.add(v);
					word = new StringBuffer();
				}
				continue;
			} else if (isSeparator(c)) {
				// �Ƿָ���
				String v = word.toString();
				if (v.length() > 0) {
					result.add(v);
					word = new StringBuffer();
				}

				word.append(c);
				if (c == '>' || c == '<' || c == '!' || c == '=') {
					if (i < expr.length() - 1) {
						char nextc = expr.charAt(i + 1);
						if (nextc == '=') {
							word.append(nextc);
							i++;
						}
					}
				}else if(c=='&'){
					if (i < expr.length() - 1) {
						char nextc = expr.charAt(i + 1);
						if (nextc == '&') {
							word.append(nextc);
							i++;
						}
					}
				}else if(c=='|'){
					if (i < expr.length() - 1) {
						char nextc = expr.charAt(i + 1);
						if (nextc == '|') {
							word.append(nextc);
							i++;
						}
					}
				}
				result.add(word.toString());
				word = new StringBuffer();
			} else if (c == '"') {
				// ��������ţ�һֱ�ҵ���һ��˫����
				word.append(c);
				i++;
				for (; i < expr.length(); i++) {
					c = expr.charAt(i);
					word.append(c);
					if (c == '"') {
						result.add(word.toString());
						word = new StringBuffer();
						break;
					}
				}

			} else {
				word.append(c);
			}
		}

		String v = word.toString();
		if (v.length() > 0) {
			result.add(v);
		}

		// Ϊ�˵��ԣ�dump result
		/*
		 * System.out.println("-------------- dump result --------------s");
		 * Enumeration e = result.elements(); while (e.hasMoreElements()) {
		 * String s = (String)e.nextElement(); System.out.println("result is
		 * "+s); }
		 */
		return result;
	}

	protected String calcDecimalSum(DBTableModel dbmodel, String columnname) {
		return dbmodel.sum(columnname).toPlainString();
	}

	protected String calcDecimalMax(DBTableModel dbmodel, String columnname) {
		if(dbmodel.getRowCount()==0)return "";
		String max=dbmodel.getItemValue(0, columnname);
		for(int r=1;r<dbmodel.getRowCount();r++){
			String v=dbmodel.getItemValue(r, columnname);
			if(DecimalHelper.comparaDecimal(v, max)>0){
				max=v;
			}
		}
		return max;
	}
	protected String calcDecimalMin(DBTableModel dbmodel, String columnname) {
		if(dbmodel.getRowCount()==0)return "";
		String min=dbmodel.getItemValue(0, columnname);
		for(int r=1;r<dbmodel.getRowCount();r++){
			String v=dbmodel.getItemValue(r, columnname);
			if(DecimalHelper.comparaDecimal(v, min)<0){
				min=v;
			}
		}
		return min;
	}
	protected String calcDecimalCount(DBTableModel dbmodel, String columnname) {
		return String.valueOf(dbmodel.getRowCount());
	}
	protected String calcDecimalAvg(DBTableModel dbmodel, String columnname) {
		int rowcount=dbmodel.getRowCount();
		if(rowcount==0)return "";
		String sum=calcDecimalSum(dbmodel,columnname);
		String avg=DecimalHelper.divide(sum, String.valueOf(rowcount),20);
		avg=DecimalHelper.trimZero(avg);
		return avg;
	}

	protected abstract ExprBase recreate();

	protected void pushDBItemvalue(int row, DBTableModel dbmodel, String colname,
			Enumeration<String> en) throws Exception {
		String v=dbmodel.getItemValue(row, colname);
		pushNumberString(v);
	}
	
	
	protected String calcRowcount(DBTableModel dm){
		int ct=0;
		for(int i=0;i<dm.getRowCount();i++){
			RecordTrunk rec=dm.getRecordThunk(i);
			if(rec.getSumflag()==RecordTrunk.SUMFLAG_RECORD){
				ct++;
			}
		}
		return String.valueOf(ct);
	}
	
}
