package com.smart.bi.client.design;

import java.awt.Font;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;

public class TestdataFactory {
	
	/**
	 * 生成 省 市 品种 数量 金额
	 * @return
	 */
	public DBTableModel createDm(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("province","varchar","省");
		cols.add(col);
		col=new DBColumnDisplayInfo("city","varchar","市");
		cols.add(col);
		col=new DBColumnDisplayInfo("goodsname","varchar","品种");
		cols.add(col);
		col=new DBColumnDisplayInfo("goodsqty","number","数量");
		cols.add(col);
		col=new DBColumnDisplayInfo("total_line","number","金额");
		cols.add(col);
		
		DBTableModel dm=new DBTableModel(cols);
		//加入数据
		int row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "河北");
		dm.setItemValue(row, "city", "石家庄");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");
		

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "河北");
		dm.setItemValue(row, "city", "石家庄");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "2");
		dm.setItemValue(row, "total_line", "200");

		
		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "河北");
		dm.setItemValue(row, "city", "石家庄");
		dm.setItemValue(row, "goodsname", "G2");
		dm.setItemValue(row, "goodsqty", "2");
		dm.setItemValue(row, "total_line", "200");
		
		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "河北");
		dm.setItemValue(row, "city", "唐山");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");
		

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "山西");
		dm.setItemValue(row, "city", "太原");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "山西");
		dm.setItemValue(row, "city", "大同");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");

		return dm;
	}
	
	/**
	 * 生成垂直表定义1.
	 * 表头一行
	 * 记录一行.
	 * 合计一行.
	 * @return
	 */
	public BITableV_def createVtable1(){
		BITableV_def tablevdef=new BITableV_def();
		int rowcount=3;
		int colcount=5;
		tablevdef.rowcount=rowcount;
		tablevdef.cells=new BICell[rowcount][colcount];
		tablevdef.colwidths=new int[colcount];
		for(int i=0;i<tablevdef.colwidths.length;i++){
			tablevdef.colwidths[i]=20;
		}
		
		tablevdef.fixrowcountperpage=3;
		
		tablevdef.rowheights=new int[rowcount];
		tablevdef.rowheights[0]=40;
		tablevdef.rowheights[1]=27;
		tablevdef.rowheights[2]=40;
		
		tablevdef.rowtypes=new int[rowcount];
		tablevdef.rowtypes[0]=BITableV_def.ROWTYPE_HEAD;
		tablevdef.rowtypes[1]=BITableV_def.ROWTYPE_DATA;
		tablevdef.rowtypes[2]=BITableV_def.ROWTYPE_FOOT;
		
		
		tablevdef.groupinfos=new Vector<SplitGroupInfo>();
		
		return tablevdef;
	}

	/**
	 * 生成垂直表定义2.
	 * 表头一行
	 * 记录一行.
	 * 分组一行.
	 * 合计一行.
	 * @return
	 */
	public BITableV_def createVtable2(){
		BITableV_def tablevdef=new BITableV_def();
		int rowcount=4;
		int colcount=5;
		tablevdef.rowcount=rowcount;
		tablevdef.cells=new BICell[rowcount][colcount];
		tablevdef.colwidths=new int[colcount];
		for(int i=0;i<tablevdef.colwidths.length;i++){
			tablevdef.colwidths[i]=20;
		}
		
		tablevdef.fixrowcountperpage=5;
		
		tablevdef.rowheights=new int[rowcount];
		tablevdef.rowheights[0]=40;
		tablevdef.rowheights[1]=27;
		tablevdef.rowheights[2]=30;
		tablevdef.rowheights[2]=40;
		
		tablevdef.rowtypes=new int[rowcount];
		tablevdef.rowtypes[0]=BITableV_def.ROWTYPE_HEAD;
		tablevdef.rowtypes[1]=BITableV_def.ROWTYPE_DATA;
		tablevdef.rowtypes[2]=BITableV_def.ROWTYPE_GROUP;
		tablevdef.rowtypes[3]=BITableV_def.ROWTYPE_FOOT;
		
		
		tablevdef.groupinfos=new Vector<SplitGroupInfo>();
		
		//按省分组
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		tablevdef.groupinfos.add(group);
		
		
		return tablevdef;
	}


	/**
	 * 生成垂直表定义3.
	 * 表头一行
	 * 记录一行.
	 * 分组一行.
	 * 分组一行.
	 * 合计一行.
	 * @return
	 */
	public BITableV_def createVtable3(){
		BITableV_def tablevdef=new BITableV_def();
		int rowcount=5;
		int colcount=5;
		tablevdef.rowcount=rowcount;
		tablevdef.cells=new BICell[rowcount][colcount];
		tablevdef.colwidths=new int[colcount];
		for(int i=0;i<tablevdef.colwidths.length;i++){
			tablevdef.colwidths[i]=20;
		}
		
		tablevdef.fixrowcountperpage=5;
		
		tablevdef.rowheights=new int[rowcount];
		tablevdef.rowheights[0]=40;
		tablevdef.rowheights[1]=27;
		tablevdef.rowheights[2]=30;
		tablevdef.rowheights[3]=30;
		tablevdef.rowheights[4]=40;
		
		tablevdef.rowtypes=new int[rowcount];
		tablevdef.rowtypes[0]=BITableV_def.ROWTYPE_HEAD;
		tablevdef.rowtypes[1]=BITableV_def.ROWTYPE_DATA;
		tablevdef.rowtypes[2]=BITableV_def.ROWTYPE_GROUP;
		tablevdef.rowtypes[3]=BITableV_def.ROWTYPE_GROUP;
		tablevdef.rowtypes[4]=BITableV_def.ROWTYPE_FOOT;
		
		
		tablevdef.groupinfos=new Vector<SplitGroupInfo>();
		
		//按省分组
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		tablevdef.groupinfos.add(group);

		//按市
		group=new SplitGroupInfo();
		group.addGroupColumn("city");
		group.setTitle("group:1:city");
		tablevdef.groupinfos.add(group);

		
		return tablevdef;
	}


	/**
	 * 生成垂直表定义4.
	 * 表头一行
	 * 表头一行
	 * 记录一行.
	 * 记录一行.
	 * 分组一行.
	 * 分组一行.
	 * 分组一行.
	 * 合计一行.
	 * @return
	 */
	public BITableV_def createVtable4(){
		BITableV_def tablevdef=new BITableV_def();
		int rowcount=8;
		int colcount=5;
		tablevdef.rowcount=rowcount;
		Font font=new Font("宋体",Font.PLAIN,14);
		Font font1=new Font("宋体",Font.BOLD,16);
		tablevdef.cells=new BICell[rowcount][colcount];
		for(int r=0;r<rowcount;r++){
			for(int c=0;c<colcount;c++){
				tablevdef.cells[r][c]=new BICell();
				tablevdef.cells[r][c].setYpadding(10);
			}
		}
		tablevdef.cells[0][0].setExpr("\"省\"");
		tablevdef.cells[0][1].setExpr("\"市\"");
		tablevdef.cells[0][2].setExpr("\"货品\"");
		tablevdef.cells[1][3].setExpr("\"数量\"");
		tablevdef.cells[1][4].setExpr("\"金额\"");

		
		tablevdef.cells[2][0].setExpr("{province}");
		tablevdef.cells[2][1].setExpr("{city}");
		tablevdef.cells[2][2].setExpr("{goodsname}");
		tablevdef.cells[3][3].setExpr("{goodsqty}");
		tablevdef.cells[3][4].setExpr("{total_line}");

		tablevdef.cells[4][2].setExpr("\"货品小计\"");
		tablevdef.cells[4][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[4][4].setExpr("sum({total_line} for group)");
		
		tablevdef.cells[5][1].setExpr("\"市小计\"");
		tablevdef.cells[5][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[5][4].setExpr("sum({total_line} for group)");

		tablevdef.cells[6][0].setExpr("\"省小计\"");
		tablevdef.cells[6][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[6][4].setExpr("sum({total_line} for group)");

		tablevdef.cells[7][0].setExpr("\"合计\"");
		tablevdef.cells[7][3].setExpr("sum({goodsqty})");
		tablevdef.cells[7][4].setExpr("sum({total_line})");
		tablevdef.cells[7][4].setFormat("0.00");
		
		tablevdef.colwidths=new int[colcount];
		for(int i=0;i<tablevdef.colwidths.length;i++){
			tablevdef.colwidths[i]=90;
		}
		
		tablevdef.fixrowcountperpage=14;
		
		tablevdef.rowheights=new int[rowcount];
		tablevdef.rowheights[0]=40;
		tablevdef.rowheights[1]=40;

		tablevdef.rowheights[2]=27;
		tablevdef.rowheights[3]=27;
		
		
		tablevdef.rowheights[4]=30;
		tablevdef.rowheights[5]=30;
		tablevdef.rowheights[6]=30;
		
		
		tablevdef.rowheights[7]=40;
		
		tablevdef.rowtypes=new int[rowcount];
		tablevdef.rowtypes[0]=BITableV_def.ROWTYPE_HEAD;
		tablevdef.rowtypes[1]=BITableV_def.ROWTYPE_HEAD;

		tablevdef.rowtypes[2]=BITableV_def.ROWTYPE_DATA;
		tablevdef.rowtypes[3]=BITableV_def.ROWTYPE_DATA;
		
		tablevdef.rowtypes[4]=BITableV_def.ROWTYPE_GROUP;
		tablevdef.rowtypes[5]=BITableV_def.ROWTYPE_GROUP;
		tablevdef.rowtypes[6]=BITableV_def.ROWTYPE_GROUP;

		tablevdef.rowtypes[7]=BITableV_def.ROWTYPE_FOOT;
		
		
		tablevdef.groupinfos=new Vector<SplitGroupInfo>();
		
		//按省分组
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);

		//按市
		group=new SplitGroupInfo();
		group.addGroupColumn("city");
		group.setTitle("group:1:city");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);


		//按货品
		group=new SplitGroupInfo();
		group.addGroupColumn("goodsname");
		group.setTitle("group:2:goodsname");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);

		return tablevdef;
	}
}
