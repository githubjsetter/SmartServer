package com.smart.platform.gui.control;



import com.smart.client.sort.PinyinComparator;
import com.smart.platform.communicate.RecordTrunk;

import java.util.Comparator;
import java.util.Vector;
import java.util.Collections;
import java.io.Serializable;
import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 13:53:25
 * To change this template use File | Settings | File Templates.
 */
public class MSort implements Comparator
{
    int sortcol;
    boolean asc;
    DBTableModel model;
    private String coltype;

    public MSort(DBTableModel model,int sortcol, boolean asc) {
        this.model=model;
        this.sortcol = sortcol;
        this.asc = asc;

        coltype = model.getColumnDBType(sortcol);
    }

    public void sort(){
        Vector recs = model.getDataVector();
        Collections.sort(recs,this);
    }

    PinyinComparator pystringcomparator=new PinyinComparator();
    public int compare(Object o, Object o1) {
        RecordTrunk rec1=(RecordTrunk)o;
        RecordTrunk rec2=(RecordTrunk)o1;

        String v1=rec1.getValueAt(sortcol);
        String v2=rec2.getValueAt(sortcol);

        if(coltype.equalsIgnoreCase("number")){
            if(v1.length()==0)v1="0";
            if(v2.length()==0)v2="0";
            v1=v1.replaceAll(",","");
            v2=v2.replaceAll(",","");
            BigDecimal decimal1 = new BigDecimal(v1);
            //decimal1.setScale(6);
            BigDecimal decimal2 = new BigDecimal(v2);
            //decimal2.setScale(6);

            if(asc){
                return decimal1.compareTo(decimal2);
            }else{
                return decimal2.compareTo(decimal1);
            }
        }else{
            if(asc){
                //return v1.compareTo(v2);
            	return pystringcomparator.compare(v1, v2);
            }else{
                //return v2.compareTo(v1);
            	return pystringcomparator.compare(v2, v1);
            }
        }
    }
}	//	MSort
