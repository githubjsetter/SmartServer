package com.smart.bi.client.design;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;




import com.smart.platform.gui.control.SplitGroupInfo;

/**
 * ��ֱ��Ķ���
 * 
 * @author user
 * 
 */
public class BITableV_def {
	/**
	 * ��ͷ.
	 */
	public static int ROWTYPE_HEAD = 1;

	/**
	 * �����������
	 */
	public static int ROWTYPE_DATA = 2;

	/**
	 * ����ķ���
	 */
	public static int ROWTYPE_GROUP = 3;

	/**
	 * ��β
	 */
	public static int ROWTYPE_FOOT = 4;

	/**
	 * ȱʡ�и�
	 */
	public static int DEFAULT_ROWHEIGHT = 27;

	/**
	 * ȱʡ�п�
	 */
	public static int DEFAULT_COLWIDTH = 90;

	/**
	 * ����table������
	 */
	int rowcount = 0;

	/**
	 * ����table������
	 */
	int colcount = 0;

	/**
	 * ��Ԫ���Ϊ�г���������
	 */
	BICell cells[][] = null;

	/**
	 * ���ʱ���и�
	 */
	int rowheights[] = null;

	/**
	 * �п�
	 */
	int colwidths[] = null;

	/**
	 * ÿ�е�����
	 */
	int rowtypes[] = null;

	/**
	 * �̶�ÿҳ������.���С�ڵ���0,��ʾ�Զ��и�.
	 */
	int fixrowcountperpage = 0;

	/**
	 * ������ɫ
	 */
	Color bgcolor = Color.WHITE;

	/**
	 * ���������
	 */
	boolean drawgrid = true;
	Color gridcolor = Color.BLACK;
	int gridwidth = 1;

	Vector<TabledefineChangedIF> changlisteners=new Vector<TabledefineChangedIF>();

	Vector<Mergeinfo> mergeinfos=new Vector<Mergeinfo>();
	Category logger=Category.getInstance(BITableV_def.class);

	/**
	 * ȱʡ1��3��.
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
	 * ������Ϣ
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
		// ��������˷���,Ҫ�������ɷ������.
		// ��ɾ������
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
		// �ҵ�������.
		int lastdatarow = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_DATA) {
				lastdatarow = i;
			}
		}
		if (lastdatarow == -1) {
			System.err.println("����data��");
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
			//�ҵ��ʵ����в���
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
	 * ����������,��{colname},�Ҳ�������-1
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
	 * ���ط����ڴ�ֱ���е�index. ����level�Ǵ�С�����ŵ�,���ڴ�ֱ����,levelֵԽС������Խ����.
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
	 * ȡ�����.
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
	 * ����head��һ�е�cells;
	 * 
	 * @param headindex
	 *            ͷ�ĵڼ���.
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
	 * ������ͷ��
	 */
	public void newHeadline() {
		rowcount++;
		// �ҵ�λ��
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
	 * ����������
	 */
	public void newDataline() {
		rowcount++;
		// �ҵ�λ��
		int lastheadpos = -1;
		for (int i = 0; i < rowtypes.length; i++) {
			if (rowtypes[i] == ROWTYPE_HEAD) {
				lastheadpos = i;
			}
		}
		// �ҳ����������
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
	 * ���ӱ�β��.
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
		// ���ƺ����
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
	 * ��c֮ǰ������
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
	 * ɾ��
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
			return "������";
		} else if (rowtype == BITableV_def.ROWTYPE_FOOT) {
			return "��β";
		} else if (rowtype == BITableV_def.ROWTYPE_GROUP) {
			return "����";
		} else if (rowtype == BITableV_def.ROWTYPE_HEAD) {
			return "��ͷ";
		} else {
			return "bad rowtype " + rowtype;
		}

	}

	/**
	 * �����к�.
	 * 
	 * @param rowtype
	 *            ����
	 * @param index
	 *            ���͵ĵ�index��
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
	 * �ϲ�����
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
	 * ���Ӻϲ�
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
	 * �ǲ��Ǳ��ϲ��ĵ�Ԫ��
	 * @param row
	 * @param column
	 * @return
	 * 0- ���Ǳ��ϲ���
	 * 1- �Ǻϲ���,�������ͷ��.
	 * 2- �Ǻϲ���,�����Ǳ��ϲ���,���ɱ༭
	 */
	public int isMergecell(int row,int column){
		Enumeration<Mergeinfo>en=mergeinfos.elements();
		while(en.hasMoreElements()){
			Mergeinfo minfo=en.nextElement();
			if(row==minfo.startrow && column==minfo.startcolumn){
				//��ʼ��.
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
