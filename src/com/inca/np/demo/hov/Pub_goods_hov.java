package com.inca.np.demo.hov;

import java.awt.Color;
import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.filedb.FiledbManager;
import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-19
 * Time: 9:10:31
 * To change this template use File | Settings | File Templates.
 */
public class Pub_goods_hov extends CHovBase{
    public Pub_goods_hov() throws HeadlessException {
        super();
    }

    public String getDefaultsql(){
        return "select goodsid,opcode,goodspinyin,goodsname,goodstype,prodarea,goodsunit from pub_goods" +
                " order by opcode";
    }

    public Querycond getQuerycond(){
        Querycond querycond = new Querycond();

        DBColumnDisplayInfo colinfo=null;


        colinfo=new DBColumnDisplayInfo("opcode","varchar","操作码",false);
        colinfo.setUppercase(true);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("goodspinyin","varchar","拼音",true);
        colinfo.setUppercase(true);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("goodsname","varchar","品名",false);
        querycond.add(new Querycondline(querycond,colinfo));

        colinfo=new DBColumnDisplayInfo("goodsid","number","货品ID",true);
        querycond.add(new Querycondline(querycond,colinfo));

        return querycond;
    }

    protected TableModel createTablemodel() {
        Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
        DBColumnDisplayInfo editor = new DBColumnDisplayInfo("goodsid", "number", "货品ID", false);
        editor.setIspk(true);
        editor.setSeqname("PUB_GOODS_GOODSID_SEQ");
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
        editor.setUppercase(true);
        tablecolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("goodspinyin", "varchar", "拼音", true);
        editor.setUppercase(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("goodsname", "varchar", "品名", false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("goodstype", "varchar", "规格", true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("prodarea", "varchar", "产地", false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("goodsunit", "varchar", "单位", true);
        tablecolumndisplayinfos.add(editor);


        return new DBTableModel(tablecolumndisplayinfos);
    }

    public String getDesc() {
        return "选择货品";
    }

    public String[] getColumns() {
        return new String[]{"goodsid","opcode","goodspinyin","goodsname","goodstype",
                            "prodarea","goodsunit"};
    }
    
    

    @Override
	protected Color getCellColor(int row, DBColumnDisplayInfo colinfo) {
		// TODO Auto-generated method stub
		return Color.red;
	}

	public static void main(String argv[]){
        FiledbManager filedb = FiledbManager.getInstance();
        /*************下载数据********************/
        String sql="select goodsid,goodsname,opcode,goodstype,goodsunit,prodarea from pub_goods";
        try {
            //filedb.downloadData(sql,"pub_goods");
            //DBTableModel goodsmodel = FiledbManager.loadDatafile("pub_goods");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

        
        Pub_goods_hov hov=new Pub_goods_hov();
        hov.setUsefile(false);
        hov.setFilename("pub_goods");
        
    	DBTableModel hovresult=hov.showDialog(null,"选货品");
    	if(hovresult==null){
    		System.err.println("cancel hov");
    		return;
    	}
    	System.out.println("共选中"+hovresult.getRowCount()+"条记录");
    }
}
