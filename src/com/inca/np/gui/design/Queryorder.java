package com.inca.np.gui.design;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.inca.np.gui.control.CFormlayout;
import com.inca.np.gui.control.CFormlineBreak;
import com.inca.np.gui.control.CNumberTextField;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.npworkflow.client.CallopHov;

public class Queryorder {
	JFrame frame = null;
	CSteModel stemodel = null;

	public Queryorder(JFrame frame, CSteModel stemodel) {
		this.frame = frame;
		this.stemodel = stemodel;
	}

	Vector<DragableLabel> dragpanes = new Vector<DragableLabel>();

	JScrollPane jsp;

	public void createQueryMovePanel(JScrollPane jsp) {
		this.jsp = jsp;
		CFormlayout formlayout = new CFormlayout(12, 4);
		JPanel tmppanel = new JPanel();
		tmppanel.setLayout(formlayout);
		jsp.setViewportView(tmppanel);
		dragpanes.clear();

		Vector<DBColumnDisplayInfo> canquerycols = stemodel
				.getCanquerycolinfos();
		int compct = 0;
		Enumeration<String> en = stemodel.getQuerycolumns().elements();
		HashMap<String, String> map = new HashMap<String, String>();
		while (en.hasMoreElements()) {
			String colname = en.nextElement();
			int p = colname.indexOf(",");
			if(p>0){
				colname=colname.substring(0,p);
			}
			if (colname.equals("行号"))
				continue;
			map.put(colname, colname);
			DBColumnDisplayInfo colinfo = getCanquerycol(canquerycols, colname);
			if (colinfo == null) {
				continue;
			}
			boolean queryable = colinfo.isQueryable();
			DragableLabel draglabel = new DragableLabel(colinfo);
			dragpanes.add(draglabel);
			draglabel.cb.setSelected(true);
			draglabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			formlayout.addLayoutComponent(draglabel, new CFormlineBreak());
			draglabel.setLayout(formlayout);
			tmppanel.add(draglabel);
		}

		Enumeration<DBColumnDisplayInfo> en1 = stemodel.getCanquerycolinfos()
				.elements();
		while (en1.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en1.nextElement();
			String colname = colinfo.getColname();
			if (colinfo.getColtype().equals("行号"))
				continue;
			if (map.get(colname) != null)
				continue;
			DragableLabel draglabel = new DragableLabel(colinfo);
			draglabel.cb.setSelected(false);
			dragpanes.add(draglabel);
			draglabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			formlayout.addLayoutComponent(draglabel, new CFormlineBreak());
			draglabel.setLayout(formlayout);
			tmppanel.add(draglabel);
		}

	}

	DBColumnDisplayInfo getCanquerycol(
			Vector<DBColumnDisplayInfo> canquerycols, String colname) {
		Enumeration<DBColumnDisplayInfo> en = canquerycols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equalsIgnoreCase(colname))
				return colinfo;
		}
		return null;
	}

	void insertBefore(String fromcolname, String tocolname) {
		int delindex = findIndex(fromcolname);
		DragableLabel lbfrom = dragpanes.elementAt(delindex);
		dragpanes.remove(delindex);

		int insertindex = findIndex(tocolname);
		dragpanes.insertElementAt(lbfrom, insertindex);

		this.reverseBind();
		createQueryMovePanel(jsp);
	}

	int findIndex(String colname) {
		Enumeration<DragableLabel> en = dragpanes.elements();
		int i = 0;
		for (i = 0; en.hasMoreElements(); i++) {
			DragableLabel lb = en.nextElement();
			if (lb.getColname().equals(colname)) {
				return i;
			}
		}
		return -1;
	}

	public void reverseBind() {
		Vector<String> querycols = new Vector<String>();
		Enumeration<DragableLabel> en = dragpanes.elements();
		int i = 0;
		for (i = 0; en.hasMoreElements(); i++) {
			DragableLabel lb = en.nextElement();
			if (lb.isQueryable()) {
				querycols.add(lb.getColname());
			}

			if (lb.isQuerymust()) {
				stemodel.addQuerymustcol(lb.getColname());
			} else {
				stemodel.removeQuerymustcol(lb.getColname());
			}
			DBColumnDisplayInfo colinfo = stemodel.getDBColumnDisplayInfo(lb
					.getColname());
			if (colinfo != null) {
				colinfo.setQueryable(lb.isQueryable());
			}
			String callopid = lb.textCallopid.getText();
			if (callopid.length() > 0) {
				colinfo = stemodel.getDBColumnDisplayInfo(lb.getColname());
				if (colinfo == null) {
					if (stemodel instanceof CMasterModel) {
						colinfo = ((CMasterModel) stemodel).getMdemodel()
								.getDetailModel().getDBColumnDisplayInfo(
										lb.getColname());
					}
				}
				if(colinfo!=null){
					colinfo.setSubqueryopid(callopid);
				}
			}
		}
		stemodel.setQuerycolumns(querycols);
	}

	class DragableLabel extends JPanel implements DropTargetListener,
			DragSourceListener, DragGestureListener, Transferable,
			MouseListener {
		DBColumnDisplayInfo colinfo;
		JCheckBox cb = null;
		JCheckBox cbquerymust = null;
		// 调用功能ID
		CNumberTextField textCallopid = new CNumberTextField(0);
		DropTarget dropTarget = new DropTarget(this, this);
		DragSource dragSource = DragSource.getDefaultDragSource();
		DataFlavor textPlainUnicodeFlavor = DataFlavor
				.getTextPlainUnicodeFlavor();

		public DragableLabel(DBColumnDisplayInfo colinfo) {
			this.colinfo = colinfo;
			this.setLayout(new FlowLayout());
			cb = new JCheckBox();
			add(cb);
			cb.setSelected(colinfo.isQueryable());
			JLabel lb = new JLabel(colinfo.getTitle());
			add(lb);
			lb.setPreferredSize(new Dimension(100, 27));
			cbquerymust = new JCheckBox("必填条件");
			add(cbquerymust);
			cbquerymust.setSelected(stemodel.isQuerymustcol(colinfo
					.getColname()));

			lb = new JLabel("子查询调用功能ID");
			add(lb);

			textCallopid.setPreferredSize(new Dimension(100, 27));
			textCallopid.setText(colinfo.getSubqueryopid());
			add(textCallopid);
			
			JButton btnhov=new JButton("...");
			btnhov.setMargin(new Insets(1, 1, 1, 1));
			btnhov.addActionListener(new OphovHandler(textCallopid));
			add(btnhov);
			

			dragSource.createDefaultDragGestureRecognizer(this,
					DnDConstants.ACTION_MOVE, this);
			addMouseListener(this);
		}

		public String getColname() {
			return colinfo.getColname();
		}

		public boolean isQueryable() {
			return cb.isSelected();
		}

		public boolean isQuerymust() {
			return cbquerymust.isSelected();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(550, 30);
		}

		public void dragEnter(DropTargetDragEvent dtde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragOver(DropTargetDragEvent dtde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragExit(DropTargetEvent dte) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void drop(DropTargetDropEvent dtde) {
			Transferable transferable = dtde.getTransferable();
			try {
				String mvcolname = (String) transferable
						.getTransferData(textPlainUnicodeFlavor);
				System.out.println(mvcolname + "==>" + colinfo.getColname());
				insertBefore(mvcolname, colinfo.getColname());
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			} catch (IOException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			}
		}

		public void dragEnter(DragSourceDragEvent dsde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragOver(DragSourceDragEvent dsde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dropActionChanged(DragSourceDragEvent dsde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragExit(DragSourceEvent dse) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragDropEnd(DragSourceDropEvent dsde) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dragGestureRecognized(DragGestureEvent dge) {
			dge.startDrag(DragSource.DefaultCopyDrop, this, this);
		}

		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor flavors[] = { textPlainUnicodeFlavor };
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			DataFlavor textPlainUnicodeFlavor = DataFlavor
					.getTextPlainUnicodeFlavor();
			if (flavor.equals(textPlainUnicodeFlavor)) {
				return true;
			}
			return false; // To change body of implemented methods use File |
			// Settings | File Templates.
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			return colinfo.getColname();
		}

		public void mouseClicked(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mousePressed(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		public void mouseExited(MouseEvent e) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	class OphovHandler extends AbstractAction{
		JTextField textCallopid=null;

		public OphovHandler(JTextField textCallopid) {
			super();
			this.textCallopid = textCallopid;
		}

		public void actionPerformed(ActionEvent e) {
			CallopHov ophov=new CallopHov();
			DBTableModel dm=ophov.showDialog((Frame)null,"选择子查询对应功能");
			if(dm==null)return;
			String opid=dm.getItemValue(0, "opid");
			textCallopid.setText(opid);
		
		}
		
	}
}
