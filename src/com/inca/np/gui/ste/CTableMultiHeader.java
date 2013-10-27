package com.inca.np.gui.ste;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class CTableMultiHeader extends JTableHeader{

	public CTableMultiHeader() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CTableMultiHeader(TableColumnModel cm) {
		super(cm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		Dimension oldsize=super.getPreferredSize();
		return new Dimension((int)oldsize.getWidth(),50);
		
	}

	@Override
	public boolean getReorderingAllowed() {
		//不允许移动列的次序
		return false;
	}

}
