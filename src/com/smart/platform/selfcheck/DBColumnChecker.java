package com.smart.platform.selfcheck;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;

/**
 * 检查列的信息 1 次序 2 可编辑 3 hov触发
 * 
 * @author Administrator
 * 
 */
public class DBColumnChecker {

	public static Vector<Columnorderinfo> orderinfos=new Vector<Columnorderinfo>();
	static{
		orderinfos.add(new Goodscolumnorder());
		orderinfos.add(new Lotcolumnorder());
		orderinfos.add(new Batchcolumnorder());
		orderinfos.add(new Poscolumnorder());
		orderinfos.add(new Goodsdtlcolumnorder());
		orderinfos.add(new Goodsdtlcolumnorder1());
	}
	
	public static void checkOrder(Vector<DBColumnDisplayInfo> cols,
			Vector<SelfcheckError> errors) {
		Enumeration<Columnorderinfo> en=orderinfos.elements();
		while(en.hasMoreElements()){
			Columnorderinfo orderinfo=en.nextElement();
			checkOrder(cols, orderinfo, errors);
		}
	}

	/**
	 * 检查列序
	 * 
	 * @param cols
	 * @param goodscolumnorder
	 */
	static void checkOrder(Vector<DBColumnDisplayInfo> cols,
			Columnorderinfo orderinfo, Vector<SelfcheckError> errors) {
		int curweight = -1;
		int priorcolindex = -1;
		String priorcolname = null;
		int colindex = 0;
		Enumeration<DBColumnDisplayInfo> en = cols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname = colinfo.getColname();
			int weight = -1;
			for (int i = 0; orderinfo.colnames != null
					&& i < orderinfo.colnames.length; i++) {
				if (orderinfo.colnames[i].equalsIgnoreCase(colname)) {
					weight = i;
					break;
				}
			}

			if (weight >= 0 && weight < curweight) {
				StringBuffer sb = new StringBuffer();
				sb.append("列" + priorcolname + "," + colname + "次序错误,应该是");
				for (int i = 0; orderinfo.colnames != null
						&& i < orderinfo.colnames.length; i++) {
					sb.append(" " + orderinfo.colnames[i]);
				}
				SelfcheckError error = new SelfcheckError("UI0001",
						SelfcheckConstants.UI0001);
				errors.add(error);
				error.setMsg(sb.toString());
			}

			/*
			 * if(weight>=0 && priorcolindex>=0 && colindex-priorcolindex>1){
			 * StringBuffer sb=new StringBuffer();
			 * sb.append("列"+colname+"之前有别的列"); for (int i = 0;
			 * orderinfo.colnames != null && i < orderinfo.colnames.length; i++) {
			 * sb.append(" "+orderinfo.colnames[i]); } SelfcheckError error=new
			 * SelfcheckError("UI0001",SelfcheckConstants.UI0001);
			 * errors.add(error); error.setMsg(sb.toString()); }
			 */
			if (weight >= 0) {
				curweight = weight;
				priorcolname = colname;
				priorcolindex = colindex;
			}
			colindex++;

		}
	}

	static class Columnorderinfo {
		String[] colnames = null;
	}

	static class Goodscolumnorder extends Columnorderinfo {
		Goodscolumnorder() {
			colnames = new String[] { "goodsname", "goodstype", "goodsunit",
					"prodarea", "goodsid" };
		}
	}

	static class Lotcolumnorder extends Columnorderinfo {
		Lotcolumnorder() {
			colnames = new String[] { "lotno", "lotid" };
		}
	}

	static class Batchcolumnorder extends Columnorderinfo {
		Batchcolumnorder() {
			colnames = new String[] { "batchno", "batchid" };
		}
	}

	static class Poscolumnorder extends Columnorderinfo {
		Poscolumnorder() {
			colnames = new String[] { "posno", "posid" };
		}
	}

	static class Goodsdtlcolumnorder extends Columnorderinfo {
		Goodsdtlcolumnorder() {
			colnames = new String[] { "packname", "packsize", "goodsdetailid" };
		}
	}

	static class Goodsdtlcolumnorder1 extends Columnorderinfo {
		Goodsdtlcolumnorder1() {
			colnames = new String[] { "packname", "packsize", "goodsdtlid" };
		}
	}

	public static void main(String argv[]) {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		cols.add(new DBColumnDisplayInfo("c1", "varchar", "c1"));
		cols.add(new DBColumnDisplayInfo("goodsname", "varchar", "goodsname"));
		cols.add(new DBColumnDisplayInfo("goodstype", "varchar", "goodstype"));
		cols.add(new DBColumnDisplayInfo("goodsunit", "varchar", "goodsunit"));
		cols.add(new DBColumnDisplayInfo("prodarea", "varchar", "prodarea"));
		cols.add(new DBColumnDisplayInfo("goodsid", "number", "goodsid"));
		cols.add(new DBColumnDisplayInfo("posid", "number", "goodsid"));
		cols.add(new DBColumnDisplayInfo("posno", "number", "goodsid"));

		Vector<SelfcheckError> errors = new Vector<SelfcheckError>();

		DBColumnChecker.checkOrder(cols, errors);
		PrintWriter out = new PrintWriter(System.out);
		Enumeration<SelfcheckError> en = errors.elements();
		while (en.hasMoreElements()) {
			SelfcheckError error = en.nextElement();
			error.dump(out);
		}
		out.flush();
	}
}
