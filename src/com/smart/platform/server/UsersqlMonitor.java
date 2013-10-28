package com.smart.platform.server;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;

/**
 * 监控某个user的sql
 * @author user
 *
 */
public class UsersqlMonitor {
	Category logger=Category.getInstance(UsersqlMonitor.class);
	private static UsersqlMonitor inst=null;
	int maxkeepsize=300;
	
	public static UsersqlMonitor getInstance(){
		if(inst==null){
			inst=new UsersqlMonitor();
			if(DefaultNPParam.debug==1){
				inst.addUsermonitor("0");
			}
		}
		return inst;
	}
	
	private UsersqlMonitor(){
		
	}
	
	/**
	 * 人员sql信息
	 */
	HashMap<String, Vector<Sqlexecinfo>> usesqlinfomap=new HashMap<String, Vector<Sqlexecinfo>>();
	
	protected class Sqlexecinfo{
		public String sql;
		public String param;
		public long usetime;
		public String currentdate;
	}
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 增加某人的访问记录
	 * @param userid 用户ID
	 * @param logsql sql
	 * @param paramstring 参数
	 * @param usetime 运行时间
	 */
	public void addlog(String userid, String logsql, String paramstring,
			long usetime) {
		synchronized(usesqlinfomap){
			//logger.info("addlog,userid="+userid+",sql="+logsql);
			Vector<Sqlexecinfo> sqlexecinfos=usesqlinfomap.get(userid);
			if(sqlexecinfos==null){
				//logger.info("not monitor userid="+userid);
				return;
			}
			Sqlexecinfo info=new Sqlexecinfo();
			info.sql=logsql;
			info.param=paramstring;
			info.usetime=usetime;
			info.currentdate=sdf.format(new java.util.Date());
			sqlexecinfos.add(info);
			
			while(sqlexecinfos.size()>maxkeepsize){
				sqlexecinfos.remove(0);
			}
		}
	}
	
	/**
	 * 增加用户ID的监控
	 * @param userid
	 */
	public void addUsermonitor(String userid){
		synchronized(usesqlinfomap){
			Vector<Sqlexecinfo> sqlexecinfos=usesqlinfomap.get(userid);
			if(sqlexecinfos!=null)return;
			sqlexecinfos=new Vector<Sqlexecinfo>();
			usesqlinfomap.put(userid, sqlexecinfos);
		}
		
	}

	/**
	 * 去掉监控
	 * @param userid
	 */
	public void removeUsermonitor(String userid){
		synchronized(usesqlinfomap){
			usesqlinfomap.remove(userid);
		}
	}
	
	public DBTableModel getUsersqlinfos(String userid){
		DBTableModel dm=createmodel();
		synchronized(usesqlinfomap){
			Vector<Sqlexecinfo> sqlexecinfos =usesqlinfomap.get(userid);
			if(sqlexecinfos==null)return dm;
			Enumeration<Sqlexecinfo> en=sqlexecinfos.elements();
			while(en.hasMoreElements()){
				Sqlexecinfo info=en.nextElement();
				int r=dm.getRowCount();
				dm.appendRow();
				dm.setItemValue(r, "userid",userid);
				dm.setItemValue(r, "credate",info.currentdate);
				dm.setItemValue(r, "sql",info.sql);
				dm.setItemValue(r, "param",info.param);
				dm.setItemValue(r, "usetime",String.valueOf(info.usetime));
			}
			return dm;
		}
	}
	
	DBTableModel createmodel(){
		Vector<DBColumnDisplayInfo>cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		col=new DBColumnDisplayInfo("userid","number","用户ID");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("sql","varchar","sql");
		cols.add(col);

		col=new DBColumnDisplayInfo("param","varchar","调用参数");
		cols.add(col);

		col=new DBColumnDisplayInfo("credate","date","发生日期");
		cols.add(col);

		col=new DBColumnDisplayInfo("usetime","number","用时");
		cols.add(col);
		
		return new DBTableModel(cols);

	}
}
