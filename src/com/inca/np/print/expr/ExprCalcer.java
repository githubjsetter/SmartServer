package com.inca.np.print.expr;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.print.report.AccessableReport;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-25
 * Time: 17:44:02
 * ���ʽʵ��
 */
public class ExprCalcer {

    AccessableReport report = null;

    public ExprCalcer(AccessableReport caller) {
        this.report = caller;
    }


    String calcresult;
    Stack exprStack;

    Category logger = Category.getInstance(ExprCalcer.class);

    /**
     * ������ʽ
     *
     * @param r    �к�
     * @param expr ���ʽ
     * @return ���ַ����ر��ʽ
     * @throws Exception
     */
    public String calc(int r, String expr) throws Exception {
        exprStack = new Stack();
        calcresult = "";

        try {
            doCalcExpr(r, expr);
        } catch (Exception calce) {
            logger.error("���ʽ" + expr + "����:" + calce.getMessage());
            throw calce;
        }
        return new String(calcresult);
    }

    void doCalcExpr(int r, String expr) throws Exception {
        //�����ʽ���зֽ⣬�ŵ�һ��ֵ��ջ���Ե����µķ�����
        /*
           E:=E+E|E-E|E*E|E/E|(E)|i
           i������0-9
        */
        if (expr == null || expr.length() == 0) return;
        expr = expr.trim();

        Vector words = splitWord(expr);
        Enumeration en = words.elements();
        while (en.hasMoreElements()) {
            String word = (String) en.nextElement();

            if (word.charAt(0) == '"' || word.charAt(0) == '\'') {
                //���ַ���
                word = word.substring(1, word.length() - 1);
                exprStack.push(word);
            } else if (isNumber(word)) {
                exprStack.push(word);
            } else if (word.toLowerCase().equals("summst")) {
                //���ܵ��ϼ�
                String v = calcSum(r, en, "master");
                exprStack.push(v);
            } else if (word.toLowerCase().equals("sumdtl")) {
                //��ϸ���ϼ�
                String v = calcSum(r, en, "detail");
                exprStack.push(v);
            } else if (word.toLowerCase().equals("avg")) {
                //��ƽ��ֵ
                //String v=calcAvg();
                //exprStack.push(v);
            } else if (word.toLowerCase().equals("getrow")) {
                //ȡ��ǰ�к�
                String v = String.valueOf(r + 1);
                exprStack.push(v);

                //��������()
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
            } else if (word.toLowerCase().equals("rowcountmst")) {
                //ȡ�ܵ���¼��
                DBTableModel dbmodel = report.getMasterDbmodel();
                String v = String.valueOf(String.valueOf(dbmodel.getRowCount()));
                exprStack.push(v);

                //��������()
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountmst�﷨Ϊrowcountmst()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("rowcountmst�﷨Ϊrowcountmst()");
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountmst�﷨Ϊrowcountmst()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("rowcountmst�﷨Ϊrowcountmst()");
            } else if (word.toLowerCase().equals("rowcountdtl")) {
                //ȡϸ����¼��
                DBTableModel dbmodel = report.getDbmodel();
                String v = String.valueOf(String.valueOf(dbmodel.getRowCount()));
                exprStack.push(v);

                //��������()
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountdtl�﷨Ϊrowcountdtl()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("rowcountdtl�﷨Ϊrowcountdtl()");
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountdtl�﷨Ϊrowcountdtl()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("rowcountdtl�﷨Ϊrowcountdtl()");
            } else if (word.toLowerCase().equals("username")) {
            	String v=ClientUserManager.getCurrentUser().getUsername();
                exprStack.push(v);
                //��������()
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
            } else if (word.toLowerCase().equals("getitemmst")) {
                //ȡһҳ�ĵ�һ�е�ĳ��
                if (en.hasMoreElements() == false) throw new Exception("getitemmst�﷨Ϊgetitemmst(����)");
                String v = (String) en.nextElement();
                if (v.equals("(") == false) throw new Exception("getitemmst�﷨Ϊgetitemmst(����");
                if (en.hasMoreElements() == false) throw new Exception("getitemmst�﷨Ϊgetitemmst(����");
                String colname = (String) en.nextElement();
                if (en.hasMoreElements() == false) throw new Exception("getitemmst�﷨Ϊgetitemmst(����");
                v = (String) en.nextElement();
                if (v.equals(")") == false) throw new Exception("getitemmst�﷨Ϊgetitemmst(����");


                //colname�����Ǳ��ʽ
                ExprCalcer exprhelper = new ExprCalcer(report);
                colname = exprhelper.calc(0, colname);

                DBTableModel dbmodel = report.getMasterDbmodel();
                int row = report.getMasterdbmodelrow();
                if(row<0)row=0;
                if (row >= 0 && row < dbmodel.getRowCount()) {
                    v = dbmodel.getItemValue(row, colname);
                } else {
                    v = "";
                }
                exprStack.push(v);

            } else if (word.toLowerCase().equals("page") || word.toLowerCase().equals("pageno")) {
                //ȡ��ǰҳ��
/*
                PageInfo pageinfo = report.getCurrentPage();
                int pageno = 0;
                if (pageinfo != null)
                    pageno = pageinfo.getPageno();
*/

                int pageno = report.getPrintingpageno();
                String v = String.valueOf(pageno + 1);
                exprStack.push(v);

                //��������()
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
                //ȡҳ��

                String v = String.valueOf(report.getPagecount());
                exprStack.push(v);


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

            } else if (word.toLowerCase().equals("string")) {
                //String(expr1[,expr2]);
                //����һ��(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;

                String v = "";
                ExprCalcer helper = new ExprCalcer(report);
                v = helper.calc(r, expr1);
                if (resultexpr.endexpr.equals(")")) {
                } else if (resultexpr.endexpr.equals(",")) {
                    resultexpr = getExpr(en);
                } else {
                    System.out.println("getExpr���طָ�������:" + resultexpr.endexpr);
                }

                exprStack.push(v);


            } else if (word.toLowerCase().equals("today")) {
                //���ص�ǰ������
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                String v = formater.format(new java.util.Date());
                exprStack.push(v);

                //��������()
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
                //���ص�ǰ��ʱ��
                SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
                String v = formater.format(new java.util.Date());
                exprStack.push(v);

                //��������()
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
                //����round���� round(expr1,expr2)
                //����(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;

                ExprCalcer helper = new ExprCalcer(report);
                expr1 = helper.calc(r, expr1);

                resultexpr = getExpr(en);
                String expr2 = resultexpr.expr;
                expr2 = helper.calc(r, expr2);

                String v = doRound(expr1, expr2);
                exprStack.push(v);
            } else if (word.toLowerCase().equals("abs")) {
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;

                ExprCalcer helper = new ExprCalcer(report);
                expr1 = helper.calc(r, expr1);

                BigDecimal value = new BigDecimal(expr1);
                value = value.abs();
                String v = value.toString();
                exprStack.push(v);
            } else if (word.toLowerCase().equals("isnull")) {
                //����isnull���� isnull(expr)
                //����(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;

                ExprCalcer helper = new ExprCalcer(report);
                expr1 = helper.calc(r, expr1);
                String v;
                if (expr1.length() == 0)
                    v = "1";
                else
                    v = "0";
                exprStack.push(v);
            } else if (word.toLowerCase().equals("if"))//2003/3//11 ���� ���Ӷ�IF������֧��
            {
                //����if���� if(expr,value1,value2)
                //����(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;

                ExprCalcer helper = new ExprCalcer(report);
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
                exprStack.push(v);
            } else if (word.toLowerCase().equals("long")) {
                //����long(expr)����
                //����(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;
                ExprCalcer helper = new ExprCalcer(report);
                expr1 = helper.calc(r, expr1);
                String v = "";
                try {
                    long ll = Long.parseLong(expr1);
                    v = String.valueOf(ll);
                } catch (Exception elong) {
                    v = "0";
                }
                exprStack.push(v);
            } else if (word.toLowerCase().equals("describe") || word.toLowerCase().equals("desc")) {
                //describe����
                //����(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;
                String v = doDescribe(expr1);
//			System.out.println("describe,expr ="+expr+" and  result v = "+v);
                exprStack.push(v);
            } else if (word.equals(":")) {
                //���ò���
                String paramname = (String) en.nextElement();
/*
                String v = (String) en.nextElement();
                String subname = (String) en.nextElement();

                if (v.equals(".") == false)
                    throw new Exception("����ӦΪ:\"������\".\"��������\"��ʽ");
                paramname = paramname + "." + subname;
*/
                String value = report.getParam(paramname);
                if (value == null) value = "";
                exprStack.push(value);
/*
            } else if (isColumn(word)) {
                String v = report.getData(r, word);
                if (v == null || v.length() == 0) v = "0";
                exprStack.push(v);
*/
            } else if (word.equals("+") || word.equals("-")) {
                //���ڲ���������ջ�е���һ��������
                String lastop = getLastOp();
                if (lastop == null) {//��ջ��ѹջ
                    exprStack.push(word);
                } else if (lastop.equals("+") || lastop.equals("-") ||
                        lastop.equals("*") || lastop.equals("/") || lastop.equals(")")) {
                    //�ȹ�Լ����ѹ
                    calcStack();
                    exprStack.push(word);
                } else if (lastop.equals("(") || lastop.equals(">") || lastop.equals("=") || lastop.equals("<")) {
                    //ѹջ
                    exprStack.push(word);
                } else
                    throw new Exception("�ڲ�����û�д���ջ��������Ϊ" + lastop);
            } else if (word.equals("*") || word.equals("/")) {
                String lastop = getLastOp();
                if (lastop == null) {//��ջ��ѹջ
                    exprStack.push(word);
                } else if (lastop.equals("+") || lastop.equals("-") || lastop.equals("(")
                        || lastop.equals(">") || lastop.equals("=") || lastop.equals("<")) {
                    exprStack.push(word);
                } else if (lastop.equals("*") || lastop.equals("/") || lastop.equals(")")) {
                    calcStack();
                    exprStack.push(word);
                } else
                    throw new Exception("�ڲ�����û�д���ջ��������Ϊ" + lastop);
            }
            //�߼����ʽ��֧��
            else if (word.equals(">") || word.equals("=") || word.equals("<")) {
                String lastop = getLastOp();
                if (lastop == null) {//��ջ��ѹջ
                    exprStack.push(word);
                } else if (lastop.equals("(")) {
                    exprStack.push(word);
                } else if (lastop.equals("+") || lastop.equals("-") || lastop.equals("*") || lastop.equals("/")
                        || lastop.equals(")") || lastop.equals(">") || lastop.equals("=") || lastop.equals("<")) {
                    calcStack();
                    exprStack.push(word);
                }
            } else if (word.equals("(")) {
                exprStack.push(word);
            } else if (word.equals(")")) {
                exprStack.push(word);
                calcStack();
            } else {
                throw new Exception("����ı��ʽ:" + expr);
            }

        }

        //�����ˣ���Լ
        //System.out.println("---------before calc");
        //dumpStack();

        calcStack();
        //dumpֵ������
        //dumpStack();

        calcresult = (String) exprStack.pop();

    }

    String doRound(String expr1, String expr2) {
        double v = Double.parseDouble(expr1);
        int n = Integer.parseInt(expr2);
        StringBuffer sb = new StringBuffer("0");
        for (int i = 0; i < n; i++) {
            if (i == 0) sb.append(".");
            sb.append("0");
        }

        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(v);
    }

//�����ʽ�ֽ��һ�����Ĵ�
    Vector splitWord(String expr) {
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
                //�Ƿָ���
                String v = word.toString();
                if (v.length() > 0) {
                    result.add(v);
                    word = new StringBuffer();
                }
                word.append(c);
                result.add(word.toString());
                word = new StringBuffer();
            } else if (c == '"') {
                //��������ţ�һֱ�ҵ���һ��˫����
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


        //Ϊ�˵��ԣ�dump result
/*
	System.out.println("-------------- dump result --------------s");
	Enumeration e = result.elements();
	while (e.hasMoreElements())
	{
		String s = (String)e.nextElement();
		System.out.println("result is "+s);
	}
*/
        return result;
    }

    boolean isspace(char c) {
        if (c == ' ' || c == '\t') return true;
        return false;
    }

    boolean isSeparator(char c) {
        if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/' || c == ',' || /*c == '.' ||*/ c == ':' || c == '>' || c == '=' || c == '<')
            return true;

        return false;
    }


    void clearStack() {
        exprStack.removeAllElements();
    }

//����ջ�����£�����)������2��������
    String getLastOp() {
        int len = exprStack.size();
        if (len < 2) return null;
        String v = (String) exprStack.elementAt(len - 1);
        if (v.equals(")")) return v;
        return (String) exprStack.elementAt(len - 2);
    }

    boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    class ResultExpr {
        public String endexpr;//�������ţ�ӦΪ,��)
        public String expr;//�ֽ���ı��ʽ

        public ResultExpr(String endexpr, String expr) {
            this.endexpr = endexpr;
            this.expr = expr;
        }

    }

    /**
     * ��Enumeration����)��,���������ر��ʽ
     */
    ResultExpr getExpr(Enumeration en) {
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
    String doDescribe(String expr) throws Exception {
        //ȥͷβ��"
        expr = expr.trim();
        if (expr.charAt(0) == '"') expr = expr.substring(1, expr.length() - 1);
        //ֻ��ʶ�� <����>.<λ��> λ��Ϊx,y,width,height
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
        //������
/*
        TextReportColumn column = report.getBodyColumn(colname);

        //����λ��
        if (posname.equals("x")) {
            return String.valueOf(column.getX());
        } else if (posname.equals("width")) {
            return String.valueOf(column.getWidth());
        } else if (posname.equals("height")) {
            return String.valueOf(report.getRowHeight());
        } else if (posname.equals("format")) {
            return column.getFormat();
        } else if (posname.equals("align")) {
            return column.getAlign();
        } else {
            String errs = "describe������ʽ" + expr + "��û����λ��" + posname;
            throw new Exception(errs);
        }
*/
    }

    boolean isColumn(String colname) {
/*
        try {
            report.getBodyColumn(colname);
            return true;
        } catch (Exception e) {
            return false;
        }
*/
        return false;
    }

    /**
     * ������ջ�е��������ݽ��м��㡣
     *
     * @throws Exception
     */
    void calcStack() throws Exception {
        if (exprStack.size() == 1) return;
        while (doCalcStack()) {
            if (exprStack.size() == 1) return;
        }
    }



//��ջexprStack���й�Լ
    boolean doCalcStack() throws Exception {
        /*
           E:=E+E|E-E|E*E|E/E|(E)|i
           i������0-9
        */
        if (exprStack.size() == 1) return true;

        String left, op, right;

        //��ջ�����з���
        right = (String) exprStack.pop();
        if (right.equals("+") || right.equals("-") || right.equals("*") || right.equals("/")) {
            exprStack.push(right);
            return false;
        } else if (right.equals(")")) {
            //����Ƿ����(E)
            if (getLastOp().equals("(")) { //����(E)
                op = (String) exprStack.pop();
                exprStack.pop();
                exprStack.push(op);
                return true;
            }
            //��Լ���ʽ
            calcStack();
            exprStack.push(")");

            return true;
        }

        //ֻ������ֵ���޷���Լ������
        if (exprStack.size() == 1) {
            exprStack.push(right);
            return false;
        }


        op = (String) exprStack.pop();
        //System.out.println("op="+op);
        //����Ƿ���(
        if (op.equals("(")) {
            exprStack.push(op);
            exprStack.push(right);
            return false;
        }

        left = (String) exprStack.pop();
        if (left == null) {
            System.out.println("��������");
            throw new Exception("��������");
        }



        //���м���
        String result = calc(left, op, right);
        exprStack.push(result);


        return true;
    }


//�Ա��ʽ����ֵ�ļ���
    String calc(String left, String op, String right) throws Exception {
        if (left == null || left.length() == 0 || left.equals(" ")) {
            left = "0";
        }
        if (right == null || right.length() == 0 || right.equals(" ")) {
            right = "0";
        }

        //Logger.logRun("left="+left+",op="+op+",right="+right);

        if (isNumber(left) && isNumber(right) &&
                left.charAt(left.length() - 1) != '.' &&
                right.charAt(right.length() - 1) != '.')
            return calcNumber(left, op, right);

        if (op.equals("+") == false) {
            throw new Exception("�����ַ���ֻ����+,expr1=" + left + ",expr2=" + right);
        }
        return left + right;
    }

    String calcNumber(String left, String op, String right) throws Exception {
        double d1 = Double.parseDouble(left);
        double d2 = Double.parseDouble(right);
        double result = 0;
        if (op.equals("+"))
            result = d1 + d2;
        else if (op.equals("-"))
            result = d1 - d2;
        else if (op.equals("*"))
            result = d1 * d2;
        else if (op.equals("/")) {
            if (d2 == 0) {
                //throw new Exception("����Ϊ0");
                return " ";
            }
            result = d1 / d2;
        } else if (op.equals(">")) {
            if (d1 > d2)
                result = 1;
            else
                result = 0;
        } else if (op.equals("=")) {
            if (d1 == d2)
                result = 1;
            else
                result = 0;
        } else if (op.equals("<")) {
            if (d1 < d2)
                result = 1;
            else
                result = 0;
        }


        String strresult = new java.math.BigDecimal(result).toString();
        if (strresult.startsWith(".")) strresult = "0" + strresult;
        return strresult;

    }

    String chunkZero(String s) {
        int p = s.indexOf(".");
        if (p < 0) return s;

        int afterzero = s.length() - p - 1;
        if (afterzero > 10) {
            //С�������������λ
            s = s.substring(0, p + 10 + 1);
        }

        int p1 = s.length() - 1;
        for (; p1 > p; p1--) {
            char c = s.charAt(p1);
            if (c != '0') break;
        }
        s = s.substring(0, p1 + 1);
        if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * ���кϼƼ���
     *
     * @param en
     * @return
     * @throws Exception
     */
    String calcSum(int row, Enumeration en, String mdflag) throws Exception {
        //������
        DBTableModel dbmodel = null;
        if (mdflag.equals("master")) {
            dbmodel = report.getMasterDbmodel();
        } else {
            dbmodel = report.getDbmodel();
        }
        String s;
        String syntax = "sum����Ϊsum(�� for ��)��sum(��)����=all��group ���";
        if (en.hasMoreElements() == false) throw new Exception(syntax);
        s = (String) en.nextElement();
        if (s.equals("(") == false) throw new Exception(syntax);


        if (en.hasMoreElements() == false) throw new Exception(syntax);
        String columnname = (String) en.nextElement();

        ExprCalcer calchelper = new ExprCalcer(report);
        columnname = calchelper.calc(0, columnname);

        //�����һ���ַ���Ӧ���� for ���� )
        if (en.hasMoreElements() == false) throw new Exception(syntax);
        s = (String) en.nextElement();
        s = s.toLowerCase();

        if (s.equals("for")) {
            if (en.hasMoreElements() == false) throw new Exception(syntax);
            s = (String) en.nextElement();
            //sӦΪall��group
            s = s.toLowerCase();
            if (s.equals("all")) {
                //��ȫ���ĺϼ�
                String sum = calcDecimalSum(dbmodel, columnname);
                if (en.hasMoreElements() == false) throw new Exception(syntax);
                s = (String) en.nextElement();
                if (s.equals(")") == false) throw new Exception(syntax);
                return sum;
            } else if (s.equals("group")) {
                //���飬���������
                String groupno = "";
                if (en.hasMoreElements()) {
                    groupno = (String) en.nextElement();
                    if (groupno.equals(")")) {
                        //groupno = String.valueOf(report.getCurrentGroupNo());
                    } else {
                        if (en.hasMoreElements() == false) throw new Exception(syntax);
                        s = (String) en.nextElement();
                        if (s.equals(")") == false) throw new Exception(syntax);
                    }
                }
                return "";
                //String sum = report.calcGroupSum(row, Integer.parseInt(groupno), columnname);
                //return sum;
            } else if (s.equals("page")) {
                //��ҳ�ϼ�
/*
                PageInfo pageinfo = report.getCurrentPage();
                String sum = "";
                if (pageinfo != null)
                    report.calcPageSum(pageinfo.getStartrow(), pageinfo.getEndrow(), columnname);
                if (en.hasMoreElements() == false) throw new Exception(syntax);
                s = (String) en.nextElement();
                if (s.equals(")") == false) throw new Exception(syntax);
                return sum;
*/
                return "";
            }

        } else if (s.equals(")")) {
            //��ȫ���ĺϼ�
            String sum = calcDecimalSum(dbmodel, columnname);
            return sum;
        } else
            throw new Exception(syntax);
        return "";
    }

    String calcDecimalSum(DBTableModel dbmodel, String columnname) {
        BigDecimal sum = new BigDecimal(0);
        for (int r = 0; r < dbmodel.getRowCount(); r++) {
            BigDecimal v = new BigDecimal(dbmodel.getItemValue(r, columnname));
            sum = sum.add(v);
        }
        return sum.toPlainString();
    }

    public static void main(String[] argv) {
        ExprCalcer calcer = new ExprCalcer(null);
        String s = "";
        try {
            //s = calcer.calc(0,"\"���\"");
            s = calcer.calc(0, "round('',2");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(s);
    }
}
