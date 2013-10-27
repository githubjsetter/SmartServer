package com.inca.np.gui.design;

import com.inca.np.gui.control.*;
import com.inca.np.image.IconFactory;
import com.inca.np.image.CIcon;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Enumeration;
import java.util.Vector;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-27
 * Time: 16:32:32
 * To change this template use File | Settings | File Templates.
 */
public class ColumnMove {
    Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo=null;
    DesignFrame frame=null;
    private final static String NEWROW="插入换行";
    private final static String NEWCOL="新增一列";

    public ColumnMove(DesignFrame frame,Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo) {
        this.formdbcolumndisplayinfo = formdbcolumndisplayinfo;
        formdbcolumndisplayinfo.lastElement().setLinebreak(true);
        this.frame=frame;
    }

    public void createFormMovePanel(JPanel cp){
        cp.removeAll();
        CFormlayout formlayout = new CFormlayout(1,1);
        cp.setLayout(formlayout);
        
        JLabel lbhelp=new JLabel("可拖动列.双击列换行.");
        cp.add(lbhelp);
        formlayout.addLayoutComponent(lbhelp, new CFormlineBreak());

        JPanel tmppanel=new JPanel();
        int compct=0;
        Enumeration en = formdbcolumndisplayinfo.elements();
        while (en.hasMoreElements()) {
            DBColumnDisplayInfo colinfo = (DBColumnDisplayInfo) en.nextElement();
            if(colinfo.getColname().equals("行号")){
                compct++;
                continue;
            }

            if(colinfo.isHide()){
                continue;
            }
            DragablePanel dragpanel=new DragablePanel(colinfo);
            dragpanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            colinfo.placeOnForm(tmppanel,formlayout,null);
            JComponent editorcomp = colinfo.getEditComponent();
            CLabel editorlabel = colinfo.getLabel();

            DragableLabel draglabel=new DragableLabel(colinfo,NEWROW);
            draglabel.setOpaque(true);
            draglabel.setText(editorlabel.getText());
            Dimension labelsize = editorlabel.getPreferredSize();
            labelsize.setSize(labelsize.getWidth(),20);
            draglabel.setPreferredSize(labelsize);
            dragpanel.setLayout(formlayout);
            
            JCheckBox cbvisible=new JCheckBox();
            dragpanel.add(cbvisible);
            cbvisible.setSelected(!colinfo.isHidetitleoncard());
            cbvisible.addChangeListener(new Changehandler(colinfo));
            
            dragpanel.add(draglabel);
            dragpanel.add(editorcomp);

            cp.add(dragpanel);
            if(colinfo.isLinebreak()){
                DBColumnDisplayInfo newlineinfo = new DBColumnDisplayInfo(NEWCOL+String.valueOf(compct),NEWCOL);
                DragablePanel colpanel = new DragablePanel(newlineinfo);
                colpanel.setBackground(backcolor);
                //colpanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                colpanel.setPreferredSize(new Dimension(60,30));

                CIcon insertcolicon = IconFactory.icinsertcol;
                JLabel lb=new JLabel(insertcolicon);
                lb.setOpaque(true);
                Dimension lbsize = lb.getPreferredSize();
                lb.setPreferredSize(new Dimension((int)lbsize.getWidth(),20));
                colpanel.add(lb);

                cp.add(colpanel);
                formlayout.addLayoutComponent(colpanel, new CFormlineBreak());
            }
            compct++;
        }

        DBColumnDisplayInfo newlineinfo = new DBColumnDisplayInfo(NEWROW,NEWROW);
        DragablePanel dragpanel=new DragablePanel(newlineinfo);
        dragpanel.setBackground(backcolor);
        dragpanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        dragpanel.setPreferredSize(new Dimension(160,30));

        DragableLabel lb=new DragableLabel(newlineinfo,NEWROW);
        lb.setOpaque(true);
        lb.setHorizontalAlignment(JLabel.CENTER);
        lb.setPreferredSize(new Dimension(150,20));
        dragpanel.add(lb);

        cp.add(dragpanel);
        formlayout.addLayoutComponent(dragpanel, new CFormlineBreak());
    }



    Color backcolor= new Color(238,238,238);
    class DragableLabel extends JLabel implements DropTargetListener, DragSourceListener, DragGestureListener,
            Transferable,MouseListener{

        DBColumnDisplayInfo dbcolinfo=null;

        DropTarget dropTarget = new DropTarget(this, this);
        DragSource dragSource = DragSource.getDefaultDragSource();
        DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();

        public DragableLabel(DBColumnDisplayInfo dbcolinfo,String text) {
            super(text);
            this.dbcolinfo = dbcolinfo;
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
            addMouseListener(this);
        }

        public DragableLabel(DBColumnDisplayInfo dbcolinfo,Icon icon) {
            super(icon);
            this.dbcolinfo = dbcolinfo;
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
            addMouseListener(this);
        }


        public void dragEnter(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DropTargetEvent dte) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            try {
                String colname=(String) transferable.getTransferData(textPlainUnicodeFlavor);
                //System.out.println("move "+colname+" before "+this.dbcolinfo.getExpr());
                if(!colname.equals(dbcolinfo.getColname())){
                    moveFormcolOrder(colname,dbcolinfo.getColname());
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void dragEnter(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragOver(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DragSourceEvent dse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            dge.startDrag(DragSource.DefaultCopyDrop,this,this);
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor flavors[] = {textPlainUnicodeFlavor};
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();
            if(flavor.equals(textPlainUnicodeFlavor)){
                return true;
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return dbcolinfo.getColname();
        }

        public void mouseClicked(MouseEvent e) {
        	if(e.getClickCount()>1){
        		int targetindex = getFormColIndex(dbcolinfo.getColname());
        		targetindex--;
        		if(targetindex<=0)return;
        		formdbcolumndisplayinfo.elementAt(targetindex).setLinebreak(true);
        		frame.fireColumnOrderMoved();
        	}
        }

        public void mousePressed(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseReleased(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseEntered(MouseEvent e) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void mouseExited(MouseEvent e) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    class DragablePanel extends JPanel implements DropTargetListener, DragSourceListener, DragGestureListener,
            Transferable{

        DBColumnDisplayInfo dbcolinfo=null;

        DropTarget dropTarget = new DropTarget(this, this);
        DragSource dragSource = DragSource.getDefaultDragSource();
        DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();

        public DragablePanel(DBColumnDisplayInfo dbcolinfo) {
            this.dbcolinfo = dbcolinfo;
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
            this.setOpaque(true);
            setBackground(backcolor);
        }



        public void dragEnter(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DropTargetEvent dte) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            try {
                String colname=(String) transferable.getTransferData(textPlainUnicodeFlavor);
                //System.out.println("move "+colname+" before "+this.dbcolinfo.getExpr());
                if(!colname.equals(dbcolinfo.getColname())){
                    moveFormcolOrder(colname,dbcolinfo.getColname());
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void dragEnter(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragOver(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DragSourceEvent dse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            dge.startDrag(DragSource.DefaultCopyDrop,this,this);
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor flavors[] = {textPlainUnicodeFlavor};
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();
            if(flavor.equals(textPlainUnicodeFlavor)){
                return true;
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return dbcolinfo.getColname();
        }
    }

    void moveFormcolOrder(String colname,String insertatcolname){
        if(colname.startsWith(NEWCOL)){
            return;
        }
        if(colname.equals(NEWROW)){
            if(insertatcolname.equals(NEWROW) || insertatcolname.startsWith(NEWCOL)){
                return;
            }
            int insertindex = getFormColIndex(insertatcolname);
            int priorindex = insertindex-1;
            if(priorindex<0){
                return;
            }
            DBColumnDisplayInfo priorcomp = formdbcolumndisplayinfo.get(priorindex);
            priorcomp.setLinebreak(true);
            frame.drawAll();
            return;
        }
        int curindex = getFormColIndex(colname);

        DBColumnDisplayInfo curinfo = formdbcolumndisplayinfo.get(curindex);



        if(curindex>0){
            //如果前一个不是换行，要换一次行
            int priorindex = curindex-1;
            DBColumnDisplayInfo priorcomp = formdbcolumndisplayinfo.get(priorindex);
            if(curinfo.isLinebreak() &&  !priorcomp.isLinebreak()){
                priorcomp.setLinebreak(true);
            }
        }

        if(insertatcolname.startsWith(NEWCOL)){
            int insertindex = Integer.parseInt(insertatcolname.substring(NEWCOL.length()));
            DBColumnDisplayInfo insertcomp = formdbcolumndisplayinfo.get(insertindex);
            assert insertcomp.isLinebreak();
            insertcomp.setLinebreak(false);
            curinfo.setLinebreak(true);

            formdbcolumndisplayinfo.insertElementAt(curinfo,insertindex+1);
            if(curindex<insertindex){
                formdbcolumndisplayinfo.removeElementAt(curindex);
            }else{
                formdbcolumndisplayinfo.removeElementAt(curindex+1);
            }
            frame.drawAll();
            return;
        }
        int insertindex = getFormColIndex(insertatcolname);

        if(insertatcolname.equals(NEWROW)){
            formdbcolumndisplayinfo.add(curinfo);
            curinfo.setLinebreak(true);
            formdbcolumndisplayinfo.removeElementAt(curindex);
            frame.drawAll();
            return;
        }

        //插入到前面
        curinfo.setLinebreak(false);
        formdbcolumndisplayinfo.insertElementAt(curinfo,insertindex);

        //删除现在的
        if(insertindex>curindex){
             formdbcolumndisplayinfo.removeElementAt(curindex);
        }else{
            formdbcolumndisplayinfo.removeElementAt(curindex + 1);
        }

        frame.fireColumnOrderMoved();
    }

    int getFormColIndex(String colname){
        int i=0;
        Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo.elements();
        while (en.hasMoreElements()) {
            DBColumnDisplayInfo colinfo = en.nextElement();
            if(colinfo.getColname().equals(colname)){
                return i;
            }
            i++;
        }
        return -1;
    }
    
    class Changehandler implements ChangeListener{
    	DBColumnDisplayInfo dbcol;

		public Changehandler(DBColumnDisplayInfo dbcol) {
			super();
			this.dbcol = dbcol;
		}

		public void stateChanged(ChangeEvent changeevent) {
			//System.out.println(changeevent);
			JCheckBox cb=(JCheckBox)changeevent.getSource();
			boolean bs=cb.isSelected();
			dbcol.setHidetitleoncard(!bs);
		}
    	
    }
}
