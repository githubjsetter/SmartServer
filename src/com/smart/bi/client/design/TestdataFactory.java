package com.smart.bi.client.design;

import java.awt.Font;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;

public class TestdataFactory {
	
	/**
	 * ���� ʡ �� Ʒ�� ���� ���
	 * @return
	 */
	public DBTableModel createDm(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("province","varchar","ʡ");
		cols.add(col);
		col=new DBColumnDisplayInfo("city","varchar","��");
		cols.add(col);
		col=new DBColumnDisplayInfo("goodsname","varchar","Ʒ��");
		cols.add(col);
		col=new DBColumnDisplayInfo("goodsqty","number","����");
		cols.add(col);
		col=new DBColumnDisplayInfo("total_line","number","���");
		cols.add(col);
		
		DBTableModel dm=new DBTableModel(cols);
		//��������
		int row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "�ӱ�");
		dm.setItemValue(row, "city", "ʯ��ׯ");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");
		

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "�ӱ�");
		dm.setItemValue(row, "city", "ʯ��ׯ");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "2");
		dm.setItemValue(row, "total_line", "200");

		
		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "�ӱ�");
		dm.setItemValue(row, "city", "ʯ��ׯ");
		dm.setItemValue(row, "goodsname", "G2");
		dm.setItemValue(row, "goodsqty", "2");
		dm.setItemValue(row, "total_line", "200");
		
		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "�ӱ�");
		dm.setItemValue(row, "city", "��ɽ");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");
		

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "ɽ��");
		dm.setItemValue(row, "city", "̫ԭ");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");

		row=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(row, "province", "ɽ��");
		dm.setItemValue(row, "city", "��ͬ");
		dm.setItemValue(row, "goodsname", "G1");
		dm.setItemValue(row, "goodsqty", "1");
		dm.setItemValue(row, "total_line", "100");

		return dm;
	}
	
	/**
	 * ���ɴ�ֱ����1.
	 * ��ͷһ��
	 * ��¼һ��.
	 * �ϼ�һ��.
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
	 * ���ɴ�ֱ����2.
	 * ��ͷһ��
	 * ��¼һ��.
	 * ����һ��.
	 * �ϼ�һ��.
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
		
		//��ʡ����
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		tablevdef.groupinfos.add(group);
		
		
		return tablevdef;
	}


	/**
	 * ���ɴ�ֱ����3.
	 * ��ͷһ��
	 * ��¼һ��.
	 * ����һ��.
	 * ����һ��.
	 * �ϼ�һ��.
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
		
		//��ʡ����
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		tablevdef.groupinfos.add(group);

		//����
		group=new SplitGroupInfo();
		group.addGroupColumn("city");
		group.setTitle("group:1:city");
		tablevdef.groupinfos.add(group);

		
		return tablevdef;
	}


	/**
	 * ���ɴ�ֱ����4.
	 * ��ͷһ��
	 * ��ͷһ��
	 * ��¼һ��.
	 * ��¼һ��.
	 * ����һ��.
	 * ����һ��.
	 * ����һ��.
	 * �ϼ�һ��.
	 * @return
	 */
	public BITableV_def createVtable4(){
		BITableV_def tablevdef=new BITableV_def();
		int rowcount=8;
		int colcount=5;
		tablevdef.rowcount=rowcount;
		Font font=new Font("����",Font.PLAIN,14);
		Font font1=new Font("����",Font.BOLD,16);
		tablevdef.cells=new BICell[rowcount][colcount];
		for(int r=0;r<rowcount;r++){
			for(int c=0;c<colcount;c++){
				tablevdef.cells[r][c]=new BICell();
				tablevdef.cells[r][c].setYpadding(10);
			}
		}
		tablevdef.cells[0][0].setExpr("\"ʡ\"");
		tablevdef.cells[0][1].setExpr("\"��\"");
		tablevdef.cells[0][2].setExpr("\"��Ʒ\"");
		tablevdef.cells[1][3].setExpr("\"����\"");
		tablevdef.cells[1][4].setExpr("\"���\"");

		
		tablevdef.cells[2][0].setExpr("{province}");
		tablevdef.cells[2][1].setExpr("{city}");
		tablevdef.cells[2][2].setExpr("{goodsname}");
		tablevdef.cells[3][3].setExpr("{goodsqty}");
		tablevdef.cells[3][4].setExpr("{total_line}");

		tablevdef.cells[4][2].setExpr("\"��ƷС��\"");
		tablevdef.cells[4][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[4][4].setExpr("sum({total_line} for group)");
		
		tablevdef.cells[5][1].setExpr("\"��С��\"");
		tablevdef.cells[5][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[5][4].setExpr("sum({total_line} for group)");

		tablevdef.cells[6][0].setExpr("\"ʡС��\"");
		tablevdef.cells[6][3].setExpr("sum({goodsqty} for group)");
		tablevdef.cells[6][4].setExpr("sum({total_line} for group)");

		tablevdef.cells[7][0].setExpr("\"�ϼ�\"");
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
		
		//��ʡ����
		SplitGroupInfo group=null;
		group=new SplitGroupInfo();
		group.addGroupColumn("province");
		group.setTitle("group:0:province");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);

		//����
		group=new SplitGroupInfo();
		group.addGroupColumn("city");
		group.setTitle("group:1:city");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);


		//����Ʒ
		group=new SplitGroupInfo();
		group.addGroupColumn("goodsname");
		group.setTitle("group:2:goodsname");
		group.addDataColumn("goodsqty", "sum");
		group.addDataColumn("total_line", "sum");
		tablevdef.groupinfos.add(group);

		return tablevdef;
	}
}
