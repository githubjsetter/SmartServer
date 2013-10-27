package com.inca.np.gui.ste;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-28
 * Time: 17:05:18
 * HOV����
 */
public class Hovdefine {
    String hovclassname;
    String invokecolname;
    /**
     * ʹ�ñ��� ���༭|����ѯ|�༭��ѯ
     */
    String usecontext="�༭��ѯ";
    /**
     * keyΪhov���У�ֵΪ����ֵ����
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
    	if(usecontext.equals("���༭") || usecontext.equals("����ѯ")){
    		return usecontext;
    	}
    	usecontext="�༭��ѯ";
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
     * ��ѯhov��
     * @param dbcolname  hov��Ӧ��������
     * @return  hov��
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
