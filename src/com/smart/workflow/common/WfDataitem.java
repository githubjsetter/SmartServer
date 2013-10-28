package com.smart.workflow.common;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SelectHelper;
import com.smart.workflow.server.Dataitemcalcer;
import com.smart.workflow.server.WfDataitemManager;

/**
 * �Զ���������
 * @author user
 *
 */
public class WfDataitem {
	
	/**
	 * ��������Ϊ����
	 */
	public static String DATATYPE_NUMBER="number";
	
	/**
	 * ��������Ϊ�ַ���
	 */
	public static String DATATYPE_STRING="string";
	
	
	/**
	 * �������������
	 */
	public static String COMEFROM_COLUMN="column";

	/**
	 * ������Ϊjava��ʵ��
	 */
	public static String COMEFROM_JAVA="java";
	
	/**
	 * ������Ϊselect sql
	 */
	public static String COMEFROM_SQL="sql";
	
	/**
	 * ������ID
	 */
	String dataitemid="";
	
	/**
	 * ����������
	 */
	
	String dataitemname;
	
	/**
	 * ����ID
	 */
	
	String wfid="";
	
	/**
	 * ��������
	 */
	
	String datatype="";
	
	/**
	 * ��Դ.������; sql; java����
	 */
	
	String comefrom="";
	
	/**
	 * ������ͼ
	 */
	String columnname="";
	
	/**
	 * java����
	 */
	String classname="";
	
	/**
	 * sql���
	 */
	String sql="";
		
	
	Category logger=Category.getInstance(WfDataitem.class);
	
	/**
	 * ����������.
	 * @return
	 */
	public String calcValue(Connection con,WfDataitemManager datamgr,Wfdefine wfdefine,String pkvalue) throws Exception{
		if(comefrom.equals(COMEFROM_COLUMN)){
			//˵�����еı��ʽ
/*			DBTableModel basedbmodel=datamgr.getBasedbmodel();
			Dataitemcalcer exprcalcer = new Dataitemcalcer(basedbmodel);
			String result = exprcalcer.calc(0, columnname);
			return result;
*/
			return datamgr.calcExpr(con, wfdefine, pkvalue, columnname);
		}else if(comefrom.equals(COMEFROM_SQL)){
			//˵����select���
			//�ȶ�sql���м���
			String tmpsql="";
			try{
			tmpsql=datamgr.calcExpr(con, wfdefine, pkvalue, sql);
			}catch(Exception e){
				logger.error("error",e);
			}
			logger.debug("������sql:"+tmpsql);
			
			//��һ��,���в�ѯ,ȡ0�е�0��ֵ
			SelectHelper sh=new SelectHelper(tmpsql);
			DBTableModel tmpdm=sh.executeSelect(con, 0, 1);
			if(tmpdm.getRowCount()==0)return "";
			return tmpdm.getItemValue(0, 0);
		}else if(comefrom.equals(COMEFROM_JAVA)){
			//�����java,Ӧ��ʵ�������,ȡֵ.
			Class clazz = Class.forName(classname);
			WfDataitem javaitem=(WfDataitem)clazz.newInstance();
			return javaitem.calcValue(con, datamgr, wfdefine, pkvalue);
		}
		return "unkown comefrom="+comefrom;
	}
	
	public static WfDataitem loadFromDB(Connection con,String dataitemid)throws Exception{
		WfDataitem dataitem=new WfDataitem();
		String sql="select * from np_wf_dataitem where dataitemid=?";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(dataitemid);
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		if(dm.getRowCount()==0)throw new Exception("û���ҵ�����������dataitemid="+dataitemid);
		
		dataitem.dataitemid=dm.getItemValue(0, "dataitemid");
		dataitem.wfid=dm.getItemValue(0, "wfid");
		dataitem.datatype=dm.getItemValue(0, "datatype");
		dataitem.comefrom=dm.getItemValue(0, "comefrom");
		dataitem.dataitemname=dm.getItemValue(0, "dataitemname");
		dataitem.columnname=dm.getItemValue(0, "columnname");
		dataitem.sql=dm.getItemValue(0, "sql");
		dataitem.classname=dm.getItemValue(0, "classname");
		return dataitem;
	}

	public String getDataitemname() {
		return dataitemname;
	}

	public String getDatatype() {
		return datatype;
	}

	public String getDataitemid() {
		return dataitemid;
	}

	public String getColumnname() {
		return columnname;
	}

	public String getClassname() {
		return classname;
	}

	public String getComefrom() {
		return comefrom;
	}
	
}
