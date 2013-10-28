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
 * ��CSteModel��CMdeModelȡ�����б��ʽ����
 * 
 * @author Administrator
 * 
 */
public class ExprCalcer extends ExprBase {

	/**
	 * CallerΪCSteModel��CMdeModel
	 */
	Object caller = null;

	public ExprCalcer(Object caller) {
		this.caller = caller;
	}

	@Override
    public void doCalcExpr(int r, String expr) throws Exception {
        // �����ʽ���зֽ⣬�ŵ�һ��ֵ��ջ���Ե����µķ�����
        /*
		 * E:=E+E|E-E|E*E|E/E|(E)|i i������0-9
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

            // ��һ����
        	// ��Ϊ��һ������
        	if(caller instanceof CSteModel){
        		// �������°�
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
                // ���ܵ��ϼ�
                String v = calcSum(r, en);
                valueStack.push(v);
            } else if (word.toLowerCase().equals("count")) {
            	// ��ȡ������
            	String v=en.nextElement();
            	if(!v.equals("(")){
            		throw new Exception("count����Ϊcount()");
            	}
            	// ��ȡ������
            	v=en.nextElement();
            	if(!v.equals(")")){
            		throw new Exception("count����Ϊcount()");
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

        // �����ˣ���Լ
        // System.out.println("---------before calc");
        // dumpStack();

        calcStack();
        // dumpֵ������
        // dumpStack();

        calcresult = (String) valueStack.pop();

    }

	String calcSum(int row, Enumeration en) throws Exception {
		// ������
		DBTableModel dbmodel = ((CSteModel) caller).getDBtableModel();
		String s;
		String syntax = "sum����Ϊsum(����)";
		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		s = (String) en.nextElement();
		if (s.equals("(") == false)
			throw new Exception(syntax);

		if (en.hasMoreElements() == false)
			throw new Exception(syntax);
		String columnname = (String) en.nextElement();

		/*
		 * ��Ҫ������ ExprCalcer calchelper = new ExprCalcer(caller); columnname =
		 * calchelper.calc(0, columnname);
		 */
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


	// �����ʽ�ֽ��һ�����Ĵ�



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
			// s = calcer.calc(0,"\"���\"");
			//s = calcer.calc(0,"if(1=2,   (2+(2-3))*4/5 , 1+(2-3)*4/5)");
			//s=calcer.calc(0,"round(\"\",2)");
			s=calcer.calc(0,"if(\"\"=3,\"\",\"\")");
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
		System.out.println("���="+s);
	}

	@Override
	protected ExprBase recreate() {
		return new ExprCalcer(caller);
	}
}
