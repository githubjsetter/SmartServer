package com.smart.platform.gui.control;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.smart.client.sort.PinyinComparator;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.util.DecimalHelper;

/**
 * 按多列排列
 * @author Administrator
 *
 */

public class MSort1 implements Comparator
{
    boolean asc;
    DBTableModel model;
    String[] sortcolumns=null;

    public MSort1(DBTableModel model,String[] sortcolumns, boolean asc) {
        this.model=model;
        this.sortcolumns = sortcolumns;
        this.asc = asc;

    }

    public void sort(){
        Vector recs = model.getDataVector();
        Collections.sort(recs,this);
    }

    public int compare(Object o, Object o1) {
        RecordTrunk rec1=(RecordTrunk)o;
        RecordTrunk rec2=(RecordTrunk)o1;
        
        int ret=doCompare(rec1,rec2);
        if(ret==0)return 0;
        if(asc){
        	return ret;
        }else{
        	return -ret;
        }

    }
    
    protected int doCompare(RecordTrunk rec1,RecordTrunk rec2){
    	if(sortcolumns==null)return 0;
    	for(int i=0;i<sortcolumns.length;i++){
    		String colname=sortcolumns[i];
    		int c=compare(rec1,rec2,colname);
    		if(c!=0){
    			return c;
    		}
    	}
    	return 0;
    }

    PinyinComparator pystringcomparator=new PinyinComparator();
	int compare(RecordTrunk rec1, RecordTrunk rec2, String colname) {
		int cindex=model.getColumnindex(colname);
		String v1=(String)rec1.elementAt(cindex);
		String v2=(String)rec2.elementAt(cindex);
		
		String coltype=model.getColumnDBType(colname);
		int ret;
		if(coltype.equals("number")){
			BigDecimal d1=DecimalHelper.toDec(v1);
			BigDecimal d2=DecimalHelper.toDec(v2);
			ret=d1.compareTo(d2);
		}else{
			//ret=v1.compareTo(v2);
			ret=pystringcomparator.compare(v1, v2);
		}
		if(ret==0)return 0;
		return ret>0?1:-1;
	}
    
    
}	
