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
 * 表达式实现
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
     * 计算表达式
     *
     * @param r    行号
     * @param expr 表达式
     * @return 用字符返回表达式
     * @throws Exception
     */
    public String calc(int r, String expr) throws Exception {
        exprStack = new Stack();
        calcresult = "";

        try {
            doCalcExpr(r, expr);
        } catch (Exception calce) {
            logger.error("表达式" + expr + "错误:" + calce.getMessage());
            throw calce;
        }
        return new String(calcresult);
    }

    void doCalcExpr(int r, String expr) throws Exception {
        //将表达式进行分解，放到一个值的栈。自底向下的分析。
        /*
           E:=E+E|E-E|E*E|E/E|(E)|i
           i是数字0-9
        */
        if (expr == null || expr.length() == 0) return;
        expr = expr.trim();

        Vector words = splitWord(expr);
        Enumeration en = words.elements();
        while (en.hasMoreElements()) {
            String word = (String) en.nextElement();

            if (word.charAt(0) == '"' || word.charAt(0) == '\'') {
                //是字符串
                word = word.substring(1, word.length() - 1);
                exprStack.push(word);
            } else if (isNumber(word)) {
                exprStack.push(word);
            } else if (word.toLowerCase().equals("summst")) {
                //算总单合计
                String v = calcSum(r, en, "master");
                exprStack.push(v);
            } else if (word.toLowerCase().equals("sumdtl")) {
                //算细单合计
                String v = calcSum(r, en, "detail");
                exprStack.push(v);
            } else if (word.toLowerCase().equals("avg")) {
                //算平均值
                //String v=calcAvg();
                //exprStack.push(v);
            } else if (word.toLowerCase().equals("getrow")) {
                //取当前行号
                String v = String.valueOf(r + 1);
                exprStack.push(v);

                //后面两个()
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
            } else if (word.toLowerCase().equals("rowcountmst")) {
                //取总单记录数
                DBTableModel dbmodel = report.getMasterDbmodel();
                String v = String.valueOf(String.valueOf(dbmodel.getRowCount()));
                exprStack.push(v);

                //后面两个()
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountmst语法为rowcountmst()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("rowcountmst语法为rowcountmst()");
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountmst语法为rowcountmst()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("rowcountmst语法为rowcountmst()");
            } else if (word.toLowerCase().equals("rowcountdtl")) {
                //取细单记录数
                DBTableModel dbmodel = report.getDbmodel();
                String v = String.valueOf(String.valueOf(dbmodel.getRowCount()));
                exprStack.push(v);

                //后面两个()
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountdtl语法为rowcountdtl()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("rowcountdtl语法为rowcountdtl()");
                if (en.hasMoreElements() == false)
                    throw new Exception("rowcountdtl语法为rowcountdtl()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("rowcountdtl语法为rowcountdtl()");
            } else if (word.toLowerCase().equals("username")) {
            	String v=ClientUserManager.getCurrentUser().getUsername();
                exprStack.push(v);
                //后面两个()
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
            } else if (word.toLowerCase().equals("getitemmst")) {
                //取一页的第一行的某列
                if (en.hasMoreElements() == false) throw new Exception("getitemmst语法为getitemmst(列名)");
                String v = (String) en.nextElement();
                if (v.equals("(") == false) throw new Exception("getitemmst语法为getitemmst(列名");
                if (en.hasMoreElements() == false) throw new Exception("getitemmst语法为getitemmst(列名");
                String colname = (String) en.nextElement();
                if (en.hasMoreElements() == false) throw new Exception("getitemmst语法为getitemmst(列名");
                v = (String) en.nextElement();
                if (v.equals(")") == false) throw new Exception("getitemmst语法为getitemmst(列名");


                //colname可能是表达式
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
                //取当前页号
/*
                PageInfo pageinfo = report.getCurrentPage();
                int pageno = 0;
                if (pageinfo != null)
                    pageno = pageinfo.getPageno();
*/

                int pageno = report.getPrintingpageno();
                String v = String.valueOf(pageno + 1);
                exprStack.push(v);

                //后面两个()
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
                //取页数

                String v = String.valueOf(report.getPagecount());
                exprStack.push(v);


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

            } else if (word.toLowerCase().equals("string")) {
                //String(expr1[,expr2]);
                //跳过一个(
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
                    System.out.println("getExpr返回分隔符错误:" + resultexpr.endexpr);
                }

                exprStack.push(v);


            } else if (word.toLowerCase().equals("today")) {
                //返回当前的日期
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                String v = formater.format(new java.util.Date());
                exprStack.push(v);

                //后面两个()
                if (en.hasMoreElements() == false)
                    throw new Exception("today语法为today()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("today语法为today()");
                if (en.hasMoreElements() == false)
                    throw new Exception("today语法为today()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("today语法为today()");


            } else if (word.toLowerCase().equals("now")) {
                //返回当前的时间
                SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
                String v = formater.format(new java.util.Date());
                exprStack.push(v);

                //后面两个()
                if (en.hasMoreElements() == false)
                    throw new Exception("now语法为now()");
                v = (String) en.nextElement();
                if (v.equals("(") == false)
                    throw new Exception("now语法为now()");
                if (en.hasMoreElements() == false)
                    throw new Exception("now语法为now()");
                v = (String) en.nextElement();
                if (v.equals(")") == false)
                    throw new Exception("now语法为now()");

            } else if (word.toLowerCase().equals("round")) {
                //处理round函数 round(expr1,expr2)
                //跳过(
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
                //处理isnull函数 isnull(expr)
                //跳过(
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
            } else if (word.toLowerCase().equals("if"))//2003/3//11 古雷 增加对IF函数的支持
            {
                //处理if函数 if(expr,value1,value2)
                //跳过(
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
                //处理long(expr)函数
                //跳过(
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
                //describe函数
                //跳过(
                en.nextElement();
                ResultExpr resultexpr = getExpr(en);
                String expr1 = resultexpr.expr;
                String v = doDescribe(expr1);
//			System.out.println("describe,expr ="+expr+" and  result v = "+v);
                exprStack.push(v);
            } else if (word.equals(":")) {
                //引用参数
                String paramname = (String) en.nextElement();
/*
                String v = (String) en.nextElement();
                String subname = (String) en.nextElement();

                if (v.equals(".") == false)
                    throw new Exception("参数应为:\"参数名\".\"参数子类\"形式");
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
                //对于操作符，看栈中的上一个操作符
                String lastop = getLastOp();
                if (lastop == null) {//空栈，压栈
                    exprStack.push(word);
                } else if (lastop.equals("+") || lastop.equals("-") ||
                        lastop.equals("*") || lastop.equals("/") || lastop.equals(")")) {
                    //先归约，再压
                    calcStack();
                    exprStack.push(word);
                } else if (lastop.equals("(") || lastop.equals(">") || lastop.equals("=") || lastop.equals("<")) {
                    //压栈
                    exprStack.push(word);
                } else
                    throw new Exception("内部错误，没有处理栈顶操作符为" + lastop);
            } else if (word.equals("*") || word.equals("/")) {
                String lastop = getLastOp();
                if (lastop == null) {//空栈，压栈
                    exprStack.push(word);
                } else if (lastop.equals("+") || lastop.equals("-") || lastop.equals("(")
                        || lastop.equals(">") || lastop.equals("=") || lastop.equals("<")) {
                    exprStack.push(word);
                } else if (lastop.equals("*") || lastop.equals("/") || lastop.equals(")")) {
                    calcStack();
                    exprStack.push(word);
                } else
                    throw new Exception("内部错误，没有处理栈顶操作符为" + lastop);
            }
            //逻辑表达式的支持
            else if (word.equals(">") || word.equals("=") || word.equals("<")) {
                String lastop = getLastOp();
                if (lastop == null) {//空栈，压栈
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
                throw new Exception("错误的表达式:" + expr);
            }

        }

        //结束了，归约
        //System.out.println("---------before calc");
        //dumpStack();

        calcStack();
        //dump值，调试
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

//将表达式分解成一个个的词
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
                //是分隔符
                String v = word.toString();
                if (v.length() > 0) {
                    result.add(v);
                    word = new StringBuffer();
                }
                word.append(c);
                result.add(word.toString());
                word = new StringBuffer();
            } else if (c == '"') {
                //如果是引号，一直找到另一个双引号
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


        //为了调试，dump result
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

//返回栈顶向下，最后个)或倒数第2个操作符
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
        public String endexpr;//结束符号，应为,或)
        public String expr;//分解出的表达式

        public ResultExpr(String endexpr, String expr) {
            this.endexpr = endexpr;
            this.expr = expr;
        }

    }

    /**
     * 读Enumeration，到)或,结束，返回表达式
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
     * 返回列的信息
     *
     * @param expr
     * @return
     * @throws Exception
     */
    String doDescribe(String expr) throws Exception {
        //去头尾的"
        expr = expr.trim();
        if (expr.charAt(0) == '"') expr = expr.substring(1, expr.length() - 1);
        //只能识别 <列名>.<位置> 位置为x,y,width,height
        int pos = expr.indexOf('.');
        if (pos < 0) {
            String errs = "describe错误表达式" + expr + "，没有找到\".\"";
            throw new Exception(errs);
        }

        String colname = expr.substring(0, pos);
        colname = colname.toLowerCase();
        colname = colname.trim();
        String posname = expr.substring(pos + 1);
        posname = posname.trim();
        posname = posname.toLowerCase();
        return "xxx";
        //找列名
/*
        TextReportColumn column = report.getBodyColumn(colname);

        //分析位置
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
            String errs = "describe错误表达式" + expr + "，没有找位置" + posname;
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
     * 对现在栈中的所有数据进行计算。
     *
     * @throws Exception
     */
    void calcStack() throws Exception {
        if (exprStack.size() == 1) return;
        while (doCalcStack()) {
            if (exprStack.size() == 1) return;
        }
    }



//对栈exprStack进行归约
    boolean doCalcStack() throws Exception {
        /*
           E:=E+E|E-E|E*E|E/E|(E)|i
           i是数字0-9
        */
        if (exprStack.size() == 1) return true;

        String left, op, right;

        //自栈顶进行分析
        right = (String) exprStack.pop();
        if (right.equals("+") || right.equals("-") || right.equals("*") || right.equals("/")) {
            exprStack.push(right);
            return false;
        } else if (right.equals(")")) {
            //检查是否符合(E)
            if (getLastOp().equals("(")) { //符合(E)
                op = (String) exprStack.pop();
                exprStack.pop();
                exprStack.push(op);
                return true;
            }
            //归约表达式
            calcStack();
            exprStack.push(")");

            return true;
        }

        //只有两个值，无法归约，返回
        if (exprStack.size() == 1) {
            exprStack.push(right);
            return false;
        }


        op = (String) exprStack.pop();
        //System.out.println("op="+op);
        //检查是否是(
        if (op.equals("(")) {
            exprStack.push(op);
            exprStack.push(right);
            return false;
        }

        left = (String) exprStack.pop();
        if (left == null) {
            System.out.println("少左括号");
            throw new Exception("少左括号");
        }



        //进行计算
        String result = calc(left, op, right);
        exprStack.push(result);


        return true;
    }


//对表达式进行值的计算
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
            throw new Exception("两个字符串只能用+,expr1=" + left + ",expr2=" + right);
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
                //throw new Exception("除数为0");
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
            //小数后最多留１０位
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
     * 进行合计计算
     *
     * @param en
     * @return
     * @throws Exception
     */
    String calcSum(int row, Enumeration en, String mdflag) throws Exception {
        //右括号
        DBTableModel dbmodel = null;
        if (mdflag.equals("master")) {
            dbmodel = report.getMasterDbmodel();
        } else {
            dbmodel = report.getDbmodel();
        }
        String s;
        String syntax = "sum函数为sum(列 for 域)或sum(列)，域=all或group 组号";
        if (en.hasMoreElements() == false) throw new Exception(syntax);
        s = (String) en.nextElement();
        if (s.equals("(") == false) throw new Exception(syntax);


        if (en.hasMoreElements() == false) throw new Exception(syntax);
        String columnname = (String) en.nextElement();

        ExprCalcer calchelper = new ExprCalcer(report);
        columnname = calchelper.calc(0, columnname);

        //检查下一个字符，应该是 for 或者 )
        if (en.hasMoreElements() == false) throw new Exception(syntax);
        s = (String) en.nextElement();
        s = s.toLowerCase();

        if (s.equals("for")) {
            if (en.hasMoreElements() == false) throw new Exception(syntax);
            s = (String) en.nextElement();
            //s应为all或group
            s = s.toLowerCase();
            if (s.equals("all")) {
                //算全部的合计
                String sum = calcDecimalSum(dbmodel, columnname);
                if (en.hasMoreElements() == false) throw new Exception(syntax);
                s = (String) en.nextElement();
                if (s.equals(")") == false) throw new Exception(syntax);
                return sum;
            } else if (s.equals("group")) {
                //算组，继续求组号
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
                //算页合计
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
            //算全部的合计
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
            //s = calcer.calc(0,"\"你好\"");
            s = calcer.calc(0, "round('',2");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(s);
    }
}
