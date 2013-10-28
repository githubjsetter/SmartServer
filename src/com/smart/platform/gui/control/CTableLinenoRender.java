package com.smart.platform.gui.control;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.image.IconFactory;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.*;

import org.apache.log4j.Category;

import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-2 Time: 13:00:04
 * To change this template use File | Settings | File Templates.
 */
public class CTableLinenoRender implements TableCellRenderer {

	DBTableModel model = null;
	NormalLineno normalcomp = new NormalLineno();
	OkLineno okcomp = new OkLineno();
	ErrorLineno errorcomp = new ErrorLineno();
	
	Font font=new Font("宋体",Font.PLAIN,12);
	Font fontbold=new Font("宋体",Font.BOLD,12);

	public CTableLinenoRender(DBTableModel model) {
		this.model = model;
	}

	// Category logger=Category.getInstance(CTableLinenoRender.class);
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		int currow=-1;
		if(table instanceof CTable){
			currow=((CTable)table).getRow();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(row + 1));
		if(currow==row){
			//sb.append("当前");
		}
		sb.append(" ");
		int status = model.getdbStatus(row);
		if (status == RecordTrunk.DBSTATUS_DELETE) {
			sb.append("删除");
		} else if (status == RecordTrunk.DBSTATUS_MODIFIED) {
			sb.append("修改");
		} else if (status == RecordTrunk.DBSTATUS_NEW) {
			sb.append("新增");
		}

		RecordTrunk rec = model.getRecordThunk(row);
		if (rec.getSumflag() == RecordTrunk.SUMFLAG_SUMMARY) {
			// logger.debug("model="+model+",row="+row+",rec="+rec+",is sum");
			if(rec.getGroupname().length()>0){
				normalcomp.lb.setText("组合计");
			}else{
				normalcomp.lb.setText("合计");
			}
			normalcomp.lb.setHorizontalAlignment(JLabel.LEFT);
			normalcomp.lb.setFont(fontbold);
			
			normalcomp.lbimg.setIcon(IconFactory.ictransp);
			return normalcomp;
		} else if (model.getResultMessage(row).length() > 0) {
			if (model.getResult(row) == 0) {
				sb.append(model.getResultMessage(row));
				okcomp.lberror.setText(sb.toString());
				if(currow==row){
					okcomp.lbimg.setIcon(IconFactory.icdetail);
				}else{
					okcomp.lbimg.setIcon(IconFactory.ictransp);
				}
				return okcomp;
			} else {
				errorcomp.lb.setText(sb.toString());
				errorcomp.lberror.setText(model.getResultMessage(row));
				if(currow==row){
					errorcomp.lbimg.setIcon(IconFactory.icdetail);
				}else{
					errorcomp.lbimg.setIcon(IconFactory.ictransp);
				}
				return errorcomp;
			}
		} else {
			normalcomp.lb.setText(sb.toString());
			normalcomp.lb.setHorizontalAlignment(JLabel.LEFT);
			normalcomp.lb.setFont(font);
			if(currow==row && row<table.getRowCount()-1){
				normalcomp.lbimg.setIcon(IconFactory.icdetail);
			}else{
				normalcomp.lbimg.setIcon(IconFactory.ictransp);
			}
			return normalcomp;
		}
	}

	class NormalLineno extends JPanel {
		JLabel lb = new JLabel("");
		JLabel lbimg=new JLabel(IconFactory.ictransp);

		public NormalLineno() {
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
			this.setLayout(boxLayout);
			add(lbimg);
			add(lb);
		}
	}

	class OkLineno extends NormalLineno {
		JLabel lberror = new JLabel("");
		public OkLineno() {
			super();
			add(new JLabel(IconFactory.icok16));
			lberror.setForeground(Color.BLACK);
			add(lberror);
		}
	}

	class ErrorLineno extends NormalLineno {
		JLabel lberror = new JLabel("");

		public ErrorLineno() {
			super();
			add(new JLabel(IconFactory.icerror16));
			lberror.setForeground(Color.RED);
			add(lberror);
		}
	}

	public void freeMemory() {
		model = null;
		if (normalcomp != null) {
			normalcomp.lb = null;
			normalcomp = null;
		}
		if (okcomp != null) {
			okcomp = null;
		}

		if (errorcomp != null) {
			errorcomp.lberror = null;
			errorcomp = null;
		}
	}

}
