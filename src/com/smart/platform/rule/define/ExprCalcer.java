package com.smart.platform.rule.define;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DecimalHelper;
import com.smart.platform.util.ExprBase;

/**
 * 从CSteModel或CMdeModel取数进行表达式计算
 * 
 * @author Administrator
 * 
 */
public class ExprCalcer extends ExprBase {

	/**
	 * Caller为CSteModel或CMdeModel
	 */
	Object caller = null;

	public ExprCalcer(Object caller) {
		this.caller = caller;
	}

	@Override
    public void doCalcExpr(int r, String expr) throws Exception {
        // 将表达式进行分解，放到一个值的栈。自底向下的分析。
        /*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i是数字0-9
		 */
        if (expr == null || expr.length() == 0) return;
        expr = expr.trim();

        //logger.debug("expr="+expr);
        
        Vector<String> words = splitWord(expr);
        Enumeration<String> en = words.elements();
        while (en.hasMoreElements()) {
            String word = (String) en.nextElement();
            //logger.debug("word="+word);
    		//logger.debug("caller is "+caller.getClass().getName());

            // 是一列吗？
        	// 认为是一个列名
        	if(caller instanceof CSteModel){
        		// 我们来猜吧
        		CSteModel ste=(CSteModel)caller;
        		DBColumnDisplayInfo colinfo=ste.getDBColumnDisplayInfo(word);
        		if(colinfo!=null){
	        		String v="";
	        		if(r<ste.getRowCount()){
	        			v=ste.getItemValue(r,word);
	        		}
	        		if(colinfo.getColtype().equals("number") && v.length()==0){
	        			v="0";
	        		}
	        		super.pushNumberString(v);
	        		continue;
        		}
        	}else if(caller instanceof DBTableModel){
        		String v="";
        		DBTableModel dbmodel=(DBTableModel)caller;
        		int colindex=dbmodel.getColumnindex(word);
        		if(colindex>=0){
	        		if(r<dbmodel.getRowCount()){
	        			v=dbmodel.getItemValue(r,word);
	        		}
	        		super.pushNumberString(v);
	        		continue;
        		}
        	}
            
            
            if (word.toLowerCase().equals("sum")) {
                // 算总单合计
                String v = calcSum(r, en);
                valueStack.push(v);
            } else if (word.toLowerCase().equals("count")) {
            	// 读取左括号
            	String v=en.nextElement();
            	if(!v.equals("(")){
            		throw new Exception("count函数为count()");
            	}
            	// 读取右括号
            	v=en.nextElement();
            	if(!v.equals(")")){
            		throw new Exception("count函数为count()");
            	}
            	v=calcRowcount(((CSteModel)caller).getDBtableModel());
                valueStack.push(v);
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

	String calcSum(int row, Enumeration en) throws Exception {
		// 右括号
		DBTableModel dbmodel = ((CSteModel) caller).getDBtableModel();
		String s;
		String syntax = "sum函数为sum(列名)";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();

		/*
		 * 不要再算了 ExprCalcer calchelper = new ExprCalcer(caller); columnname =
		 * calchelper.calc(0, columnname);
		 */
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


	// 将表达式分解成一个个的词



	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		ste.getRootpanel();
		DBTableModel dbmodel = ste.getDBtableModel();
		dbmodel.appendRow();
		dbmodel.setItemValue(0, "goodsid", "");
		ste.setRow(0);
		ExprCalcer calcer = new ExprCalcer(ste);
		String s = "";
		try {
			// s = calcer.calc(0,"\"你好\"");
			//s = calcer.calc(0,"if(1=2,   (2+(2-3))*4/5 , 1+(2-3)*4/5)");
			//s=calcer.calc(0,"round(\"\",2)");
			s=calcer.calc(0,"if(\"\"=3,\"\",\"\")");
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
		System.out.println("结果="+s);
	}

	@Override
	protected ExprBase recreate() {
		return new ExprCalcer(caller);
	}
}
