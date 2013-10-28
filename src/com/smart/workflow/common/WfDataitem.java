package com.smart.workflow.common;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SelectHelper;
import com.smart.workflow.server.Dataitemcalcer;
import com.smart.workflow.server.WfDataitemManager;

/**
 * 自定义数据项
 * @author user
 *
 */
public class WfDataitem {
	
	/**
	 * 数据类型为数字
	 */
	public static String DATATYPE_NUMBER="number";
	
	/**
	 * 数据类型为字符串
	 */
	public static String DATATYPE_STRING="string";
	
	
	/**
	 * 数据项基表列名
	 */
	public static String COMEFROM_COLUMN="column";

	/**
	 * 数据项为java类实现
	 */
	public static String COMEFROM_JAVA="java";
	
	/**
	 * 数据项为select sql
	 */
	public static String COMEFROM_SQL="sql";
	
	/**
	 * 数据项ID
	 */
	String dataitemid="";
	
	/**
	 * 数据项名称
	 */
	
	String dataitemname;
	
	/**
	 * 流程ID
	 */
	
	String wfid="";
	
	/**
	 * 数据类型
	 */
	
	String datatype="";
	
	/**
	 * 来源.基表列; sql; java计算
	 */
	
	String comefrom="";
	
	/**
	 * 基表视图
	 */
	String columnname="";
	
	/**
	 * java类名
	 */
	String classname="";
	
	/**
	 * sql语句
	 */
	String sql="";
		
	
	Category logger=Category.getInstance(WfDataitem.class);
	
	/**
	 * 计算数据项.
	 * @return
	 */
	public String calcValue(Connection con,WfDataitemManager datamgr,Wfdefine wfdefine,String pkvalue) throws Exception{
		if(comefrom.equals(COMEFROM_COLUMN)){
			//说明是列的表达式
/*			DBTableModel basedbmodel=datamgr.getBasedbmodel();
			Dataitemcalcer exprcalcer = new Dataitemcalcer(basedbmodel);
			String result = exprcalcer.calc(0, columnname);
			return result;
*/
			return datamgr.calcExpr(con, wfdefine, pkvalue, columnname);
		}else if(comefrom.equals(COMEFROM_SQL)){
			//说明是select语句
			//先对sql进行计算
			String tmpsql="";
			try{
			tmpsql=datamgr.calcExpr(con, wfdefine, pkvalue, sql);
			}catch(Exception e){
				logger.error("error",e);
			}
			logger.debug("数据项sql:"+tmpsql);
			
			//下一步,进行查询,取0行第0列值
			SelectHelper sh=new SelectHelper(tmpsql);
			DBTableModel tmpdm=sh.executeSelect(con, 0, 1);
			if(tmpdm.getRowCount()==0)return "";
			return tmpdm.getItemValue(0, 0);
		}else if(comefrom.equals(COMEFROM_JAVA)){
			//如果是java,应该实例这个类,取值.
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
		if(dm.getRowCount()==0)throw new Exception("没有找到流程数据项dataitemid="+dataitemid);
		
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
