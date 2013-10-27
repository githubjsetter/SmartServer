package com.inca.np.gui.control;

/**
 * ±íËÑË÷Æ÷
 * @author user
 *
 */
public class TableSearcher {
	CTable table;
	String searchtext="";
	int lastrow=-1;
	int lastcol=0;

	public TableSearcher(CTable table) {
		super();
		this.table = table;
	}

	/**
	 * ËÑË÷
	 * @param searchtext
	 * @return
	 */
	public int search(String searchtext){
		this.searchtext=searchtext;
		return doSearch(lastrow);
	}

	/**
	 * ËÑË÷ÏÂÒ»¸ö.
	 * @return
	 */
	public int searchNext(){
		return doSearch(lastrow+1);
	}
	
	public int getLastrow() {
		return lastrow;
	}

	public void setLastrow(int lastrow) {
		this.lastrow = lastrow;
	}

	private int doSearch(int startrow){
		if(startrow<0){
			startrow=0;
		}
		
		DBTableModel dbmodel=(DBTableModel)table.getModel();
		if(startrow>dbmodel.getRowCount()-1){
			startrow=0;
		}
		
		
		for(int row=startrow;row<dbmodel.getRowCount();row++){
			for(int c=0;c<table.getColumnCount();c++){
				int mindex=table.convertColumnIndexToModel(c);
				String v=dbmodel.getItemValue(row, mindex);
				if(v.toLowerCase().indexOf(searchtext.toLowerCase())>=0){
					lastrow=row;
					lastcol=c;
					return row;
				}
			}
		}
		
		if(startrow>0){
			for(int row=0;row<startrow && row<dbmodel.getRowCount();row++){
				for(int c=0;c<table.getColumnCount();c++){
					int mindex=table.convertColumnIndexToModel(c);
					String v=dbmodel.getItemValue(row, mindex);
					if(v.toLowerCase().indexOf(searchtext.toLowerCase())>=0){
						lastrow=row;
						lastcol=c;
						return row;
					}
				}
			}
		}
		
		return -1;
	}

	public int getLastcol() {
		return lastcol;
	}
}
