package com.inca.np.gui.ste;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-28
 * Time: 17:05:18
 * HOV定义
 */
public class Hovdefine {
    String hovclassname;
    String invokecolname;
    /**
     * 使用背景 仅编辑|仅查询|编辑查询
     */
    String usecontext="编辑查询";
    /**
     * key为hov的列，值为被赋值的列
     */
    HashMap<String,String> colpairmap=new HashMap<String,String>();

    public Hovdefine(String hovclassname, String invokecolname) {
        this.hovclassname = hovclassname;
        this.invokecolname = invokecolname;
    }

    public String getHovclassname() {
        return hovclassname;
    }

    public String getInvokecolname() {
        return invokecolname;
    }

    public HashMap<String,String> getColpairmap() {
        return colpairmap;
    }

    public void putColpair(String hovcolname, String dbcolname) {
        colpairmap.put(hovcolname,dbcolname);
    }

    
    
    public String getUsecontext() {
    	if(usecontext.equals("仅编辑") || usecontext.equals("仅查询")){
    		return usecontext;
    	}
    	usecontext="编辑查询";
		return usecontext;
	}

	public void setUsecontext(String usecontext) {
		this.usecontext = usecontext;
	}

	public String getColpairString(){
        StringBuffer sb=new StringBuffer();
        Iterator<String> it = colpairmap.keySet().iterator();
        while (it.hasNext()) {
            String hovcolname = it.next();
            String dbcolname=colpairmap.get(hovcolname);
            sb.append("("+hovcolname+","+dbcolname+")");
        }
        return sb.toString();
    }

    /**
     * 查询hov列
     * @param dbcolname  hov对应的数据列
     * @return  hov列
     */
    public String getHovcolname(String dbcolname){
        Iterator<String> it = colpairmap.keySet().iterator();
        while (it.hasNext()) {
            String hovcolname = it.next();
            String tmpname=colpairmap.get(hovcolname);
            if(tmpname.equalsIgnoreCase(dbcolname)){
                return hovcolname;
            }
        }

        return null;
    }
}
