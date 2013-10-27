package com.inca.np.filedb;

import com.inca.np.communicate.RecordTrunk;

/**
 * 文件搜索条件
 * @author Administrator
 *
 */
public class FiledbSearchCond {
	public String colname="";
	public String op="";
	public String value="";
	public int colindex=-1;
	
	
	public FiledbSearchCond(){
		
	}
	
	public FiledbSearchCond(String colname, String op, String value) {
		super();
		this.colname = colname;
		this.op = op;
		this.value = value;
	}




	public boolean match(RecordTrunk rec) {
		String colvalue = (String)rec.elementAt(colindex);
		boolean ok = false;
		if (op.equals("=")) {
			if (colvalue.equals(value)) {
				ok = true;
			}
		} else if (op.equalsIgnoreCase("like")) {
			if (colvalue.startsWith(value)) {
				ok = true;
			}
		}
		return ok;
	}
}
