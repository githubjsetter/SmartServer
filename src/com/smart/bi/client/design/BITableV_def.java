package com.smart.bi.client.design;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;




import com.smart.platform.gui.control.SplitGroupInfo;

/**
 * 垂直表的定义
 * 
 * @author user
 * 
 */
public class BITableV_def {
	/**
	 * 表头.
	 */
	public static int ROWTYPE_HEAD = 1;

	/**
	 * 表身的数据行
	 */
	public static int ROWTYPE_DATA = 2;

	/**
	 * 表身的分组
	 */
	public static int ROWTYPE_GROUP = 3;

	/**
	 * 表尾
	 */
	public static int ROWTYPE_FOOT = 4;

	/**
	 * 缺省行高
	 */
	public static int DEFAULT_ROWHEIGHT = 27;

	/**
	 * 缺省列宽
	 */
	public static int DEFAULT_COLWIDTH = 90;

	/**
	 * 定义table的行数
	 */
	int rowcount = 0;

	/**
	 * 定义table的列数
	 */
	int colcount = 0;

	/**
	 * 单元格存为行车、列向量
	 */
	BICell cells[][] = null;

	/**
	 * 设计时的行高
	 */
	int rowheights[] = null;

	/**
	 * 列宽
	 */
	int colwidths[] = null;

	/**
	 * 每行的性质
	 */
	int rowtypes[] = null;

	/**
	 * 固定每页多少行.如果小于等于0,表示自动行高.
	 */
	int fixrowcountperpage = 0;

	/**
	 * 背景颜色
	 */
	Color bgcolor = Color.WHITE;

	/**
	 * 画表格线吗
	 */
	boolean drawgrid = true;
	Color gridcolor = Color.BLACK;
	int gridwidth = 1;

	Vector<TabledefineChangedIF> changlisteners=new Vector<TabledefineChangedIF>();

	Vector<Mergeinfo> mergeinfos=new Vector<Mergeinfo>();
	Category logger=Category.getInstance(BITableV_def.class);

	/**
	 * 缺省1列3行.
	 */
	public BITableV_def() {
		super();
		rowcount = 3;
		colcount = 0;
		rowtypes = new int[rowcount];
		rowtypes[0] = ROWTYPE_HEAD;
		rowtypes[1] = ROWTYPE_DATA;
		rowtypes[2] = ROWTYPE_FOOT;

		rowheights = new int[rowcount];
		rowheights[0] = 33;
		rowheights[1] = 0;
		rowheights[2] = 33;

		colwidths = new int[colcount];

		cells = new BICell[rowcount][colcount];
		for (int r = 0; r < rowcount; r++) {
			for (int c = 0; c < colcount; c++) {
				cells[r][c] = new BICell();
			}
		}
	}

	/**
	 * 分组信息
	 */
	Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();

	public BICell[][] getCells() {
		return cells;
	}

	public int getRowcount() {
		return rowtypes.length;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}

	public int getColcount() {
		return colcount;
	}

	public void setColcount(int colcount) {
		this.colcount = colcount;
	}

	public int[] getRowheights() {
		return rowheights;
	}

	public int[] getColwidths() {
		return colwidths;
	}

	public int[] getRowtypes() {
		return rowtypes;
	}

	public int getFixrowcountperpage() {
		return fixrowcountperpage;
	}

	public void setFixrowsperpage(int fixrowsperpage) {
		this.fixrowcountperpage = fixrowsperpage;
	}

	public Vector<SplitGroupInfo> getGroupinfos() {
		return groupinfos;
	}

	public void resetGrouprows(){
		// 如果设置了分组,要重新生成分组的行.
		// 先删除所有
		boolean found = false;
		do {
			found = false;
			for (int i = 0; i < rowtypes.length; i++) {
				if (rowtypes[i] == ROWTYPE_GROUP) {
					try{
					deleteRow(i);
					}catch(Exception ee){
						logger.error("error",ee);
						return;
					}
					found = true;
					break;
				}
			}
		} while (found);
		// 找到数据行.
		int lastdatarow = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_DATA) {
				lastdatarow = i;
			}
		}
		if (lastdatarow == -1) {
			System.err.println("定义data先");
			return;
		}

		int ct=0;
		for (int i=groupinfos.size()-1; i>=0;i--) {
			SplitGroupInfo groupinfo =groupinfos.elementAt(i);
			int insertpos = lastdatarow + 1 + ct;
			ct++;
			rowcount++;
			insertRow(insertpos);
			rowtypes[insertpos] = ROWTYPE_GROUP;
			rowheights[insertpos] = DEFAULT_ROWHEIGHT;
			BICell[] linecells=getCells()[insertpos];
			//找到适当的行插入
			int c=getPreferredcolumn(groupinfo.getGroupcolumn());
			if(c>=0){
				linecells[c].setExpr("\""+groupinfo.getTitle()+"\"");
				linecells[c].setAlign(BICell.ALIGN_LEFT);
				linecells[c].setBold(true);
			}
			Enumeration<String> endatacolname=groupinfo.getDatacolumnname();
			while(endatacolname.hasMoreElements()){
				String cname=endatacolname.nextElement();
				
				c=getPreferredcolumn(cname);
				if(c>=0){
					linecells[c].setExpr("sum({"+cname+"} for group)");
					linecells[c].setAlign(BICell.ALIGN_RIGHT);
					linecells[c].setBold(true);
				}
			}
			
		}

	}
	
	public void setGroupinfos(Vector<SplitGroupInfo> groupinfos) {
		this.groupinfos = groupinfos;
	}
	
	/**
	 * 在数据行中,找{colname},找不到返回-1
	 * @param colname
	 * @return
	 */
	int getPreferredcolumn(String colname){
		colname="{"+colname+"}";
		for(int r=0;r<cells.length;r++){
			if(rowtypes[r]!=ROWTYPE_DATA)continue;
			BICell[] linecells=cells[r];
			for(int c=0;c<linecells.length;c++){
				if(linecells[c].getExpr().equalsIgnoreCase(colname)){
					return c;
				}
			}
		}
		return -1;
	}
	

	/**
	 * 返回分组在垂直表中的index. 分组level是从小到大排的,而在垂直表中,level值越小的排在越下面.
	 * 
	 * @param groupindex
	 * @return
	 */
	public int getIndexIntabledefByGroup(int grouplevel) {
		int tmpindex = 0;
		for (int i = getRowcount() - 1; i >= 0; i--) {
			if (rowtypes[i] != ROWTYPE_GROUP)
				continue;

			if (grouplevel == tmpindex) {
				return i;
			}
			tmpindex++;
		}
		return -1;
	}

	/**
	 * 取最大宽度.
	 * 
	 * @return
	 */
	public int getRenderwidth() {
		int w = 0;
		if (drawgrid) {
			w += gridwidth;
		}
		for (int i = 0; i < colwidths.length; i++) {
			w += colwidths[i];
			if (drawgrid) {
				w += gridwidth;
			}
		}
		return w;
	}

	public Color getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(Color bgcolor) {
		this.bgcolor = bgcolor;
	}

	public boolean isDrawgrid() {
		return drawgrid;
	}

	public void setDrawgrid(boolean drawgrid) {
		this.drawgrid = drawgrid;
	}

	public Color getGridcolor() {
		return gridcolor;
	}

	public void setGridcolor(Color gridcolor) {
		this.gridcolor = gridcolor;
	}

	public int getGridwidth() {
		return gridwidth;
	}

	public void setGridwidth(int gridwidth) {
		this.gridwidth = gridwidth;
	}

	/**
	 * 返回head的一行的cells;
	 * 
	 * @param headindex
	 *            头的第几行.
	 * @return
	 */
	public BICell[] getHeadcells(int headindex) {
		int tmp = 0;
		for (int i = 0; i < rowtypes.length; i++) {
			if (ROWTYPE_HEAD != rowtypes[i])
				continue;
			if (tmp == headindex) {
				return cells[tmp];
			}
			tmp++;
		}
		return null;
	}

	/**
	 * 新增表头行
	 */
	public void newHeadline() {
		rowcount++;
		// 找到位置
		int lastheadpos = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_HEAD) {
				lastheadpos = i;
			}
		}
		int insertpos;
		if (lastheadpos == -1) {
			insertpos = 0;
		} else {
			insertpos = lastheadpos + 1;
		}
		insertRow(insertpos);
		rowtypes[insertpos] = ROWTYPE_HEAD;
		rowheights[insertpos] = DEFAULT_ROWHEIGHT;
	}

	/**
	 * 增加数据行
	 */
	public void newDataline() {
		rowcount++;
		// 找到位置
		int lastheadpos = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_HEAD) {
				lastheadpos = i;
			}
		}
		// 找出最近数据行
		int lastdatapos = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_DATA) {
				lastdatapos = i;
			}
		}

		int insertpos;
		if (lastdatapos < 0) {
			if (lastheadpos == -1) {
				insertpos = 0;
			} else {
				insertpos = lastheadpos + 1;
			}
		} else {
			insertpos = lastdatapos + 1;
		}

		insertRow(insertpos);
		rowtypes[insertpos] = ROWTYPE_DATA;
		rowheights[insertpos] = DEFAULT_ROWHEIGHT;
	}

	/**
	 * 增加表尾行.
	 */
	public void newFootline() {
		int insertpos = rowcount;
		rowcount++;

		insertRow(insertpos);
		rowtypes[insertpos] = ROWTYPE_FOOT;
		rowheights[insertpos] = DEFAULT_ROWHEIGHT;
	}

	void insertRow(int pos) {
		rowtypes = insert(rowtypes, pos);
		rowheights = insert(rowheights, pos);
		insertCellsRow(cells, pos);
	}

	int[] insert(int[] datas, int pos) {
		int[] tmp = new int[datas.length + 1];

		System.arraycopy(datas, 0, tmp, 0, pos);
		// 复制后面的
		int len = datas.length - pos;
		if (len > 0) {
			System.arraycopy(datas, pos, tmp, pos + 1, len);
		}
		return tmp;
	}

	void insertCellsRow(BICell cells[][], int pos) {
		BICell tmp[][] = new BICell[cells.length + 1][colcount];
		for (int row = 0; row < pos; row++) {
			tmp[row] = cells[row];
		}
		for (int c = 0; c < colcount; c++) {
			tmp[pos][c] = new BICell();
		}

		for (int row = pos; row < cells.length; row++) {
			tmp[row + 1] = cells[row];
		}
		this.cells = tmp;
	}

	public void insertColumn(int c) {
		colwidths = insert(colwidths, c);
		colwidths[c] = DEFAULT_COLWIDTH;
		insertCellsCol(c);
		colcount++;
	}

	/**
	 * 在c之前插入列
	 * 
	 * @param c
	 */
	void insertCellsCol(int c) {
		for (int row = 0; row < cells.length; row++) {
			BICell lines[] = cells[row];
			BICell tmp[] = new BICell[colcount + 1];
			System.arraycopy(lines, 0, tmp, 0, c);
			tmp[c] = new BICell();
			int len = lines.length - c;
			if (len > 0) {
				System.arraycopy(lines, c, tmp, c + 1, len);
			}
			cells[row] = tmp;
		}
	}

	public void deleteRow(int row) {
		rowtypes = delete(rowtypes, row);
		rowheights = delete(rowheights, row);
		deleteCellsrow(row);
		rowcount--;
	}

	public void deleteColumn(int col) {
		colwidths = delete(colwidths, col);
		deleteCellscol(col);
		colcount--;
	}

	void deleteCellscol(int c) {
		for (int row = 0; row < cells.length; row++) {
			BICell lines[] = cells[row];
			BICell tmp[] = new BICell[colcount - 1];
			System.arraycopy(lines, 0, tmp, 0, c);
			int len = lines.length - c - 1;
			if (len > 0) {
				System.arraycopy(lines, c + 1, tmp, c, len);
			}
			cells[row] = tmp;
		}

	}

	void deleteCellsrow(int pos) {
		BICell tmp[][] = new BICell[cells.length - 1][colcount];
		for (int row = 0; row < pos; row++) {
			tmp[row] = cells[row];
		}

		for (int row = pos + 1; row < cells.length; row++) {
			tmp[row - 1] = cells[row];
		}
		this.cells = tmp;

	}

	/**
	 * 删除
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	int[] delete(int[] data, int pos) {
		int[] tmp = new int[data.length - 1];
		System.arraycopy(data, 0, tmp, 0, pos);
		int len = data.length - pos - 1;
		if (len > 0) {
			System.arraycopy(data, pos + 1, tmp, pos, len);
		}
		return tmp;
	}

	public static String getRowtypeString(int rowtype) {
		if (rowtype == BITableV_def.ROWTYPE_DATA) {
			return "数据行";
		} else if (rowtype == BITableV_def.ROWTYPE_FOOT) {
			return "表尾";
		} else if (rowtype == BITableV_def.ROWTYPE_GROUP) {
			return "分组";
		} else if (rowtype == BITableV_def.ROWTYPE_HEAD) {
			return "表头";
		} else {
			return "bad rowtype " + rowtype;
		}

	}

	/**
	 * 返回行号.
	 * 
	 * @param rowtype
	 *            类型
	 * @param index
	 *            类型的第index个
	 * @return
	 */
	public int getRowByRowtype(int rowtype, int index) {
		int tmpindex = 0;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] != rowtype)
				continue;
			if (tmpindex == index) {
				return i;
			}
			tmpindex++;
		}
		return -1;
	}

	public void fireDefinechanged(){
		Enumeration<TabledefineChangedIF> en=changlisteners.elements();
		while(en.hasMoreElements()){
			en.nextElement().onTabledefineChanged();
		}
	}
	
	public void addChanglistener(TabledefineChangedIF listener){
		changlisteners.add(listener);
	}

	public void setCells(BICell[][] cells) {
		this.cells = cells;
	}

	public void setRowheights(int[] rowheights) {
		this.rowheights = rowheights;
	}

	public void setColwidths(int[] colwidths) {
		this.colwidths = colwidths;
	}

	public void setRowtypes(int[] rowtypes) {
		this.rowtypes = rowtypes;
	}
	
	public void reset(){
		
		rowcount = 3;
		colcount = 0;
		rowtypes = new int[rowcount];
		rowtypes[0] = ROWTYPE_HEAD;
		rowtypes[1] = ROWTYPE_DATA;
		rowtypes[2] = ROWTYPE_FOOT;

		rowheights = new int[rowcount];
		rowheights[0] = 33;
		rowheights[1] = 0;
		rowheights[2] = 33;

		colwidths = new int[colcount];

		cells = new BICell[rowcount][colcount];
		for (int r = 0; r < rowcount; r++) {
			for (int c = 0; c < colcount; c++) {
				cells[r][c] = new BICell();
			}
		}
		groupinfos.clear();
		mergeinfos.clear();
	}
	
	/**
	 * 合并区域
	 * @author user
	 *
	 */
	public class Mergeinfo{
		public int startrow=-1;
		public int startcolumn=-1;
		public int rowcount=1;
		public int columncount=1;
	}

	/**
	 * 增加合并
	 * @param row1
	 * @param i
	 * @param col1
	 * @param j
	 */
	public void addMerge(int row1, int rowcount, int col1, int colcount) {
		Mergeinfo merinfo=new Mergeinfo();
		merinfo.startrow=row1;
		merinfo.rowcount=rowcount;
		merinfo.startcolumn=col1;
		merinfo.columncount=colcount;
		mergeinfos.add(merinfo);
	}
	
	/**
	 * 是不是被合并的单元格
	 * @param row
	 * @param column
	 * @return
	 * 0- 不是被合并的
	 * 1- 是合并的,并且是最开头的.
	 * 2- 是合并的,并且是被合并的,不可编辑
	 */
	public int isMergecell(int row,int column){
		Enumeration<Mergeinfo>en=mergeinfos.elements();
		while(en.hasMoreElements()){
			Mergeinfo minfo=en.nextElement();
			if(row==minfo.startrow && column==minfo.startcolumn){
				//开始的.
				return 1;
			}else if(row>=minfo.startrow && row<minfo.startrow+minfo.rowcount&&
					column>=minfo.startcolumn && column<minfo.startcolumn+minfo.columncount){
				return 2;
			}
		}
		return 0;
	}

	public Mergeinfo getMergeinfo(int row,int column){
		Enumeration<Mergeinfo>en=mergeinfos.elements();
		while(en.hasMoreElements()){
			Mergeinfo minfo=en.nextElement();
			if(row==minfo.startrow && column==minfo.startcolumn){
				return minfo;
			}else if(row>=minfo.startrow && row<minfo.startrow+minfo.rowcount&&
					column>=minfo.startcolumn && column<minfo.startcolumn+minfo.columncount){
				return minfo;
			}
		}
		return null;
	}


	public Vector<Mergeinfo> getMergeinfos() {
		return mergeinfos;
	}
	
}
