package com.smart.platform.demo.ste;

import java.awt.Color;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.demo.extend.DemoClient;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-11 Time: 17:24:24
 * To change this template use File | Settings | File Templates.
 */
public class Pub_goods_ste extends CSteModel /* CStePiechartModel *//*
																	 * CQueryStemodel
																	 * CSteModelAp
																	 */{
	public static final String ACTION_DEMO = "demo1";

	// Ruleenginee ruleeng = new Ruleenginee();

	public Pub_goods_ste(Steframe owner) throws HeadlessException {
		super(owner, "货品管理卡片");
		// setShowformonly(true);

		DBColumnDisplayInfo colinfo = this
				.getDBColumnDisplayInfo("filegroupid");
		if(colinfo==null){
			colinfo=new DBColumnDisplayInfo("filegroupid","number");
			formcolumndisplayinfos.add(colinfo);
		}
		colinfo.setDbcolumn(false);
		colinfo.setQueryable(false);
		colinfo.setUpdateable(false);

		// setSort(new String[] { "credate", "goodsid" }, false);

	}

	public String getTablename() {
		return "pub_goods_v";
	}

	protected String getOtherWheres() {
		return "1=1";
	}

	/*
	 * protected Color getCellColor(int row, int col) { if(row==0){ return
	 * Color.red; } return null; }
	 */

	public String getSaveCommandString() {
		return "demo.ste.Pub_goods_ste.保存货品";
	}

	public JPanel getOtherquerypanel() {
		JPanel jp = new JPanel();
		jp.add(new JLabel("你好"));

		return jp;
	}

	protected String getValueColname() {
		return "goodsid";
	}

	protected String getValueTitle() {
		return "货品ID";
	}

/*	@Override
	protected int on_beforemodify(int row) {
		return -1;
	}*/

	protected String getCategoryColname() {
		return "goodsname";
	}

	protected String getCategoryTitle() {
		return "品名";
	}

	protected int on_actionPerformed(String command) {
		if (command.equals(ACTION_DEMO)) {
			demo1();
			return 0;
		}
		if (command.equals("test")) {
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	void demo1() {
		DemoClient dc = new DemoClient();
		dc.demo1(this.getParentFrame());
	}

/*	@Override
	public String isCreateFieldgroup(String colname) {
		if (colname.equalsIgnoreCase("opcode")) {
			return "货品信息";
		} else if (colname.equalsIgnoreCase("FACTORYOPCODE")) {
			return "厂家信息";
		} else if (colname.equalsIgnoreCase("credate")) {
			return "其它信息";
		}
		return "";
	}
*/
	@Override
	public String getHovOtherWheres(int row, String colname) {
		if (colname.equalsIgnoreCase("factoryopcode")) {
			return "rownum<10";
		}
		return "";
	}
/*
	@Override
	protected String getEditablecolumns(int row) {
		return "goodsid,opcode,goodsname";
	}
*/
	@Override
	protected int on_new(int row) {
		setItemValue(row, "usestatus", "5");
		return super.on_new(row);
	}

//	@Override
	/**
	 * 返回单元格颜色demo
	protected Color getCellbgcolor(int row, int col) {
		Color colors[]={
				Color.red,Color.green	
		};
		
		int i=row%2;
		int j=col%2;
		return colors[(i+j)%2];
		
	}
	 */
	
	
}
