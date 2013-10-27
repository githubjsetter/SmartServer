package com.inca.np.selfcheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.util.DefaultNPParam;

/**
 * 视图列不可编辑
 * @author Administrator
 *
 */
public class DBColumnEditableChecker {
	public static void checkEditable(String tablename,String viewname,Vector<DBColumnDisplayInfo> cols,
			Vector<SelfcheckError> errors) {
		HashMap<String,String> viewcolmap=getViewcol(tablename,viewname);
		if(viewcolmap==null)return;
		Enumeration<DBColumnDisplayInfo> en = cols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname=colinfo.getColname().toLowerCase();
			if(viewcolmap.get(colname)!=null && !colinfo.isReadonly() && !colinfo.isHide()
					&& colinfo.getHovdefine()==null){
				SelfcheckError error=new SelfcheckError("UI0002",SelfcheckConstants.UI0002);
				errors.add(error);
				error.setMsg("列"+colinfo.getColname()+"是视图列,应该隐藏或只读或选HOV");
			}
			if(!colinfo.isReadonly() && !colinfo.isHide()
					&& colname.endsWith("id")){
				SelfcheckError error=new SelfcheckError("UI0007",SelfcheckConstants.UI0007);
				errors.add(error);
				error.setMsg("列"+colinfo.getColname()+"是ID列,必须只读或隐藏");
			}
		}
	}

	/**
	 * 找出表和视图的列, 删除视图中表列,这样只剩下视图列了
	 * @param tablename
	 * @param viewname
	 * @return
	 */
	static HashMap<String,String> getViewcol(String tablename,String viewname){
        HashMap tablecolmap=new HashMap();
        HashMap viewcolmap=new HashMap();
		Connection con=null;
		try
		{
			con=getTestCon();
	        String sql="select cname from col where tname='"+tablename.toUpperCase()+"' " +
	                " and cname not like 'ZXCOLUMN%' order by colno";
	        PreparedStatement c1 = con.prepareStatement(sql);
	        ResultSet rs = c1.executeQuery();
	        while(rs.next()){
	            String colname=rs.getString("cname").toLowerCase();
	            tablecolmap.put(colname, colname);
	        }
	        c1.close();

	        Vector<DBColumnDisplayInfo> viewcols=new Vector<DBColumnDisplayInfo>();
	        sql="select cname from col where tname='"+viewname.toUpperCase()+"' " +
	                " and cname not like 'ZXCOLUMN%' order by colno";
	        PreparedStatement c2 = con.prepareStatement(sql);
	        rs = c2.executeQuery();
	        int i=0;
	        while(rs.next()){
	            i++;
	            String colname=rs.getString("cname").toLowerCase();
	            viewcolmap.put(colname, colname);
	        }
	        c2.close();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Iterator<String> it=tablecolmap.keySet().iterator();
		while(it.hasNext()){
			String cname=it.next();
			viewcolmap.remove(cname);
		}
		return viewcolmap;
	}
	

    static String dbip=DefaultNPParam.debugdbip;
    static String dbname=DefaultNPParam.debugdbsid;
    static String dbuser=DefaultNPParam.debugdbusrname;
    static String dbpass=DefaultNPParam.debugdbpasswd;

    static Connection getTestCon() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@"+dbip+":1521:"+dbname;

        Connection con = DriverManager.getConnection(url, dbuser, dbpass);
        con.setAutoCommit(false);
        return con;

    }
}
