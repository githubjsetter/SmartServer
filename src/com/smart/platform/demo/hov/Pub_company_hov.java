package com.smart.platform.demo.hov;

import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.table.TableModel;
import javax.swing.*;

import java.util.Vector;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-9
 * Time: 14:56:04
 * To change this template use File | Settings | File Templates.
 */
public class Pub_company_hov extends CHovBase{
    public Pub_company_hov() throws HeadlessException {
        super();
    }

    public String getDefaultsql(){
        return "select companyid,companyname,companyopcode,companypinyin from pub_company" +
                " where nvl(selfflag,0)=0";
    }

    public Querycond getQuerycond(){
        Querycond querycond = new Querycond();

        DBColumnDisplayInfo colinfo=null;


        colinfo=new DBColumnDisplayInfo("companyopcode","varchar","操作码",false);
        colinfo.setUppercase(true);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("companypinyin","varchar","拼音",true);
        colinfo.setUppercase(true);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("companyid","number","单位ID",false);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("companyname","varchar","单位名称",true);
        querycond.add(new Querycondline(querycond,colinfo));

        return querycond;
    }

    protected TableModel createTablemodel() {
        Vector<DBColumnDisplayInfo> infos=new Vector<DBColumnDisplayInfo>();
        DBColumnDisplayInfo colinfo=new DBColumnDisplayInfo("companyid","number","单位ID",false);
        infos.add(colinfo);

        colinfo=new DBColumnDisplayInfo("companyopcode","varchar","操作码",false);
        infos.add(colinfo);

        colinfo=new DBColumnDisplayInfo("companypinyin","varchar","拼音",true);
        infos.add(colinfo);

        colinfo=new DBColumnDisplayInfo("companyname","varchar","单位名称",true);
        infos.add(colinfo);


        return new DBTableModel(infos);
    }

    public String getDesc() {
        return "选择单位";
    }

    public String[] getColumns() {
        return new String[]{"companyid","companyname","companyopcode","companypinyin"};
    }
    
    public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

    	Pub_company_hov hov=new Pub_company_hov();
    	hov.showDialog((Frame)null,"select company");
	}
}
