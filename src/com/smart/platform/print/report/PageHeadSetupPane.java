package com.smart.platform.print.report;

import com.smart.platform.gui.control.*;
import com.smart.platform.gui.ste.PrintSetupFrame;
import com.smart.platform.print.drawable.PLabelCell;
import com.smart.platform.print.drawable.PReport;
import com.smart.platform.print.drawable.PageHeadFoot;
import com.smart.platform.print.expr.ExprCalcer;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-24
 * Time: 14:02:45
 * 报表表头设计
 * <p/>
 * 定义报表表头
 * <p/>
 * 上部为表头的预览。下部为表格呈现各个报表部件
 */
public class PageHeadSetupPane extends JPanel {
    AccessableReport report=null;
    PageHeadFoot pagehead = null;
    private Drawpanel drawpanel;

    PrintSetupFrame frame = null;

    public PageHeadSetupPane(PrintSetupFrame frame,AccessableReport report, PageHeadFoot pagehead) {
        this.report = report;
        this.frame = frame;
        this.pagehead = pagehead;
        initPanel();
    }
    
    

    public void setReport(AccessableReport report) {
		this.report = report;
	}



	public void setPagehead(PageHeadFoot pagehead) {
		this.pagehead = pagehead;
	}



	private void initPanel() {
        setLayout(new BorderLayout());
        Toolpanel toolpanel = new Toolpanel();
        add(toolpanel, BorderLayout.NORTH);

        drawpanel = new Drawpanel();
        JScrollPane sp = new JScrollPane(drawpanel);
        add(sp, BorderLayout.CENTER);

    }

    public void fireReportchanged(PReport report) {
        this.report=report;
        this.pagehead = report.getPagehead();
        drawpanel.initComp();
    }


    class ActionHandle implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("add")) {
                PLabelCell labelcell = new PLabelCell(report,"新增");
                pagehead.addLabelcell(labelcell);
                labelcell.setX(10);
                labelcell.setY(10);
                labelcell.setWidth(100);
                labelcell.setHeight(27);

                //创建元件
                DragableLabel lb = new DragableLabel(labelcell.calcExpr());
                lb.setOpaque(false);
                lb.setBackground(backcolor);
                lb.setFont(labelcell.getFont());

                lb.setBorder(BorderFactory.createEtchedBorder());

                compmap.put(lb, labelcell);
                drawpanel.add(lb);
                drawpanel.doLayout();
                drawpanel.invalidate();
                drawpanel.repaint();
                setActiveComp(lb);
                PropsetupDialog dlg = new PropsetupDialog();
                dlg.pack();
                dlg.setVisible(true);

            } else if (e.getActionCommand().equals("modify")) {
                if (activecomp != null) {
                    PropsetupDialog dlg = new PropsetupDialog();
                    dlg.pack();
                    dlg.setVisible(true);
                }
            } else if (e.getActionCommand().equals("delete")) {
                if (activecomp != null) {
                	PLabelCell cell=compmap.get(activecomp);
                	pagehead.removeCell(cell);
                    compmap.remove(activecomp);
                    drawpanel.remove(activecomp);
                    drawpanel.doLayout();
                    drawpanel.invalidate();
                    drawpanel.repaint();
                    activecomp=null;
                }
            } else if (e.getActionCommand().equals("setheight")) {
                int h = 0;
                try {
                    h = Integer.parseInt(textHeadheight.getText());
                    pagehead.setHeight(h);
                    drawpanel.invalidate();
                    drawpanel.repaint();
                } catch (NumberFormatException e1) {

                }
            } else if (e.getActionCommand().equals("apply")) {
                //应用设置
                frame.fireReportChanged();

            }
        }
    }

    private CNumberTextField textHeadheight;

    /**
     * 上 部工具
     */
    class Toolpanel extends JPanel {
        ActionHandle listener = new ActionHandle();

        public Toolpanel() {
            setLayout(new BorderLayout());
            JPanel tb = new JPanel();
            tb.setLayout(new FlowLayout());
            add(tb, BorderLayout.NORTH);

            //宽度
            tb.add(new JLabel("宽度"));
            CNumberTextField textpagewidth = new CNumberTextField(0);
            textpagewidth.setPreferredSize(new Dimension(40, 27));
            tb.add(textpagewidth);
            textpagewidth.setText(String.valueOf(pagehead.getWidth()));
            textpagewidth.setEnabled(false);


            //高度
            tb.add(new JLabel("表头高"));
            textHeadheight = new CNumberTextField(0);
            textHeadheight.setPreferredSize(new Dimension(40, 27));
            tb.add(textHeadheight);
            textHeadheight.setText(String.valueOf(pagehead.getHeight()));

            JButton btnseth = new JButton("设定高度");
            tb.add(btnseth);
            btnseth.setActionCommand("setheight");
            btnseth.addActionListener(listener);

            JSeparator sep=new JSeparator(JSeparator.HORIZONTAL);
            sep.setPreferredSize(new Dimension(100,27));
            tb.add(sep);

            JButton btn = new JButton("增加元件");
            tb.add(btn);
            btn.setActionCommand("add");
            btn.addActionListener(listener);


            btn = new JButton("修改元件");
            tb.add(btn);
            btn.setActionCommand("modify");
            btn.addActionListener(listener);

            btn = new JButton("删除元件");
            tb.add(btn);
            btn.setActionCommand("delete");
            btn.addActionListener(listener);

            
            btn = new JButton("应用修改");
            tb.add(btn);
            btn.setActionCommand("apply");
            btn.addActionListener(listener);
        }

    }

    private void setFrameCursor(int type) {
        frame.setCursor(Cursor.getPredefinedCursor(type));
    }

    class Drawpanel extends JPanel implements MouseMotionListener {

        public Drawpanel() {

            setBackground(Color.LIGHT_GRAY);
            initComp();

            //创建layout
            pagelayout = new PageHeadLayout(compmap);
            this.setLayout(pagelayout);

            this.addMouseMotionListener(this);
        }

        private void initComp() {
            //创建元件
            compmap.clear();
            this.removeAll();
            Enumeration<PLabelCell> en = pagehead.getLabelcells().elements();
            while (en.hasMoreElements()) {
                PLabelCell labelcell = en.nextElement();
                //创建元件
                DragableLabel lb = new DragableLabel(labelcell.calcExpr());
                lb.setOpaque(false);
                lb.setBackground(backcolor);
                lb.setFont(labelcell.getFont());
                lb.setHorizontalAlignment(labelcell.getAlign());

                lb.setBorder(BorderFactory.createEtchedBorder());

                compmap.put(lb, labelcell);
                add(lb);
                if (activecomp == null) {
                    setActiveComp(lb);
                }
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Color oldcolor = g.getColor();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, pagehead.getWidth(), pagehead.getHeight());

            g.setColor(Color.BLACK);
            g.drawRect(1, 1, pagehead.getWidth(), pagehead.getHeight());

            g.setColor(oldcolor);
        }

        public void mouseDragged(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseMoved(MouseEvent e) {
            setFrameCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    HashMap<JComponent, PLabelCell> compmap = new HashMap<JComponent, PLabelCell>();
    JLabel activecomp = null;
    private int dragstartx = -1;
    private int dragstarty = -1;
    private PageHeadLayout pagelayout;

    private static String DRAGMODE_MOVE = "move";
    private static String DRAGMODE_RIGHT = "right";
    private static String DRAGMODE_BOTTOM = "bottom";
    private static String DRAGMODE_RIGHTBOTTOM = "rightbottom";
    private String dragmode = DRAGMODE_MOVE;

    private void setNewlocaltion(int x, int y) {
        JLabel dragcomp = activecomp;
        //重新定位。
        int offsetx = x - dragstartx;
        int offsety = y - dragstarty;

        //System.out.println("x="+x+",startx="+dragstartx+",offset="+offsetx);

        //重新定位
        PLabelCell cell = compmap.get(dragcomp);
        if (dragmode.equals(DRAGMODE_MOVE)) {
            cell.setX(cell.getX() + offsetx);
            cell.setY(cell.getY() + offsety);
        } else if (dragmode.equals(DRAGMODE_RIGHT)) {
            cell.setWidth(cell.getWidth() + offsetx);
        } else if (dragmode.equals(DRAGMODE_BOTTOM)) {
            cell.setHeight(cell.getHeight() + offsety);
        } else if (dragmode.equals(DRAGMODE_RIGHTBOTTOM)) {
            cell.setWidth(cell.getWidth() + offsetx);
            cell.setHeight(cell.getHeight() + offsety);
        }

        drawpanel.doLayout();
        invalidate();
        repaint();
    }


    Color backcolor = new Color(238, 238, 238);

    class DragableLabel extends JLabel implements DropTargetListener, DragSourceListener, DragGestureListener,
            Transferable, MouseListener, MouseMotionListener {


        DropTarget dropTarget = new DropTarget(this, this);
        DragSource dragSource = DragSource.getDefaultDragSource();
        DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();

        public DragableLabel(String text) {
            super(text);
            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
            addMouseListener(this);
            addMouseMotionListener(this);
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
/*
            Transferable transferable = dtde.getTransferable();
            try {
                String colname=(String) transferable.getTransferData(textPlainUnicodeFlavor);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
*/
        }

        public void dragEnter(DragSourceDragEvent dsde) {
/*
            dragstartx = (int) dsde.getX();
            dragstarty = (int) dsde.getY();
*/

        }

        public void dragOver(DragSourceDragEvent dsde) {
//            System.out.println("dragOver, cursor at screen :x="+dsde.getX()+",y="+dsde.getY());
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
//            System.out.println("dropActionChanged, cursor at screen :x="+dsde.getX()+",y="+dsde.getY());
        }

        public void dragExit(DragSourceEvent dse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
//            System.out.println("drop event, cursor at screen :x="+dsde.getX()+",y="+dsde.getY());
            setNewlocaltion(dsde.getX(), dsde.getY());
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            setActiveComp(this);
            //dge.startDrag(DragSource.DefaultCopyDrop, this, this);
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            if (dragmode.equals(DRAGMODE_MOVE)) {
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            } else if (dragmode.equals(DRAGMODE_RIGHTBOTTOM)) {
                cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            } else if (dragmode.equals(DRAGMODE_RIGHT)) {
                cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            } else if (dragmode.equals(DRAGMODE_BOTTOM)) {
                cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            }

            dge.startDrag(cursor, this, this);
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor flavors[] = {textPlainUnicodeFlavor};
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();
            if (flavor.equals(textPlainUnicodeFlavor)) {
                return true;
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            //return dbcolinfo.getColname();
            return "";
        }

        public void mouseClicked(MouseEvent e) {
            setActiveComp(this);
            if (e.getClickCount() > 1) {
                PropsetupDialog dlg = new PropsetupDialog();
                dlg.pack();
                dlg.setVisible(true);
            }
        }

        public void mousePressed(MouseEvent e) {
            //开始位置?
            Point locationOnScreen = this.getLocationOnScreen();
            dragstartx = (int) locationOnScreen.getX() + e.getX();
            dragstarty = (int) locationOnScreen.getY() + e.getY();
            //System.out.println("mosuepress,dragstartx="+dragstartx);
        }

        public void mouseReleased(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseEntered(MouseEvent e) {
            //frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void mouseExited(MouseEvent e) {
            setFrameCursor(Cursor.DEFAULT_CURSOR);
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            //如果移动到边界，就进入改变大小状态
            //System.out.println(e.getX()+","+e.getY());
            //相对于控件的坐标
            int x = e.getX();
            int y = e.getY();

            int catchsize = 3;
            boolean bcatchright = x >= getWidth() - catchsize && x <= getWidth() + catchsize;
            boolean bcatchbottom = y >= getHeight() - catchsize && y <= getHeight() + catchsize;

            if (bcatchright && bcatchbottom) {
                dragmode = DRAGMODE_RIGHTBOTTOM;
                setFrameCursor(Cursor.NW_RESIZE_CURSOR);
            } else if (bcatchright) {
                dragmode = DRAGMODE_RIGHT;
                setFrameCursor(Cursor.W_RESIZE_CURSOR);
            } else if (bcatchbottom) {
                dragmode = DRAGMODE_BOTTOM;
                setFrameCursor(Cursor.S_RESIZE_CURSOR);
            } else {
                dragmode = DRAGMODE_MOVE;
                setFrameCursor(Cursor.MOVE_CURSOR);
            }
        }
    }

    private void setActiveComp(JLabel comp) {
        if (activecomp != null) {
            activecomp.setOpaque(false);
            activecomp.repaint();
        }
        activecomp = comp;
        activecomp.setOpaque(true);
        activecomp.repaint();
    }


    class PropsetupDialog extends CDialog implements ActionListener {
        private JTextArea textExpr;
        private DBTableModel dbmodel;
        private CellinfoTable table;
        private JButton btnok;
        private FuncTable functable;
        private AstdbmodelColumntable astcoltable;
        private DbmodelColumntable dtlcoltable;

        public PropsetupDialog() throws HeadlessException {
            super((Frame) frame, "设置属性", true);
            initDialog();
            this.localScreenCenter();
        }

        void initDialog() {
            Container cp = this.getContentPane();
            BoxLayout box = new BoxLayout(cp, BoxLayout.Y_AXIS);
            cp.setLayout(box);

            cp.add(new JLabel("表达式"));
            textExpr = new JTextArea(4, 60);
            cp.add(textExpr);
            PLabelCell pLabelCell = compmap.get(activecomp);
            textExpr.setText(pLabelCell.getExpr());

            //增加用的工具条
            JPanel toolpane = createToolpane();
            cp.add(toolpane);



            //中部的属性
            cp.add(createProppanel(), BorderLayout.CENTER);

            //下部的工具
            cp.add(createBottompanel(), BorderLayout.SOUTH);


        }

        JPanel createToolpane() {
            JPanel jp = new JPanel();
            BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);


            jp.setLayout(box);
            JButton btn = new JButton("+");
            btn.setActionCommand("addadd");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton("-");
            btn.setActionCommand("addmin");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton("*");
            btn.setActionCommand("addmul");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton("/");
            btn.setActionCommand("adddiv");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton("(");
            btn.setActionCommand("add(");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton(")");
            btn.setActionCommand("add)");
            btn.addActionListener(this);
            jp.add(btn);

/*
            btn = new JButton("列");
            btn.setActionCommand("addcolumn");
            btn.addActionListener(this);
            jp.add(btn);

            btn = new JButton("函数");
            btn.setActionCommand("addfunction");
            btn.addActionListener(this);
            jp.add(btn);
*/

            btn = new JButton("检查表达式");
            btn.setActionCommand("checkexpr");
            btn.addActionListener(this);
            jp.add(btn);

            return jp;
        }

        //属性

        JPanel createProppanel() {
            JPanel jp = new JPanel();
            jp.setLayout(new BorderLayout());

            JTabbedPane tb=new JTabbedPane();
            jp.add(tb, BorderLayout.CENTER);

            Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
            cols.add(new DBColumnDisplayInfo("name", "varchar", "名称"));
            cols.add(new DBColumnDisplayInfo("value", "varchar", "属性"));
            dbmodel = new DBTableModel(cols);
            table = new CellinfoTable(dbmodel);
            //设置
            bindPropvalue(dbmodel);
            JScrollPane sp = new JScrollPane(table);
            tb.add("属性",sp);

            //可选择的函数
            Vector<DBColumnDisplayInfo> funccols=new Vector<DBColumnDisplayInfo>();
            funccols.add(new DBColumnDisplayInfo("func","varchar","函数"));
            funccols.add(new DBColumnDisplayInfo("desc","varchar","说明"));

            DBTableModel funcdbmodel = new DBTableModel(funccols);
            insertFuncvalue(funcdbmodel);

            functable = new FuncTable(funcdbmodel);
            sp = new JScrollPane(functable);
            tb.add("系统函数",sp);
            functable.autoSize();
            functable.addMouseListener(new FunctableMouseHandle());



            //辅助数据列
            Vector<DBColumnDisplayInfo> asttablecols=new Vector<DBColumnDisplayInfo>();
            asttablecols.add(new DBColumnDisplayInfo("func","varchar","函数"));
            asttablecols.add(new DBColumnDisplayInfo("desc","varchar","列名"));
            DBTableModel astdbmodel = new DBTableModel(asttablecols);
            astcoltable = new AstdbmodelColumntable(astdbmodel);
            astcoltable.addMouseListener(new AsttableMouseHandle());

            sp = new JScrollPane(astcoltable);
            tb.add("总单数据列",sp);
            loadAstcol(astdbmodel);
            astcoltable.autoSize();


            //数据列
            DBTableModel dtldbmodel = new DBTableModel(asttablecols);
            dtlcoltable = new DbmodelColumntable(dtldbmodel);
            dtlcoltable.addMouseListener(new DtlcoltableMouseHandle());

            sp = new JScrollPane(dtlcoltable);
            tb.add("细单（单表)数据列",sp);
            loadDetailcol(dtldbmodel);
            dtlcoltable.autoSize();


            return jp;
        }

        void loadAstcol(DBTableModel astdbmodel){
            DBTableModel reportdbmodel = report.getMasterDbmodel();
            if(astdbmodel==null){
                return;
            }

            Enumeration<DBColumnDisplayInfo> en = reportdbmodel.getDisplaycolumninfos().elements();
            while (en.hasMoreElements()){
                DBColumnDisplayInfo colinfo =  en.nextElement();
                astdbmodel.appendRow();
                int row=astdbmodel.getRowCount()-1;
                astdbmodel.setItemValue(row,"func","\""+colinfo.getColname()+"\"");
                astdbmodel.setItemValue(row,"desc",colinfo.getTitle());
            }
        }

        void loadDetailcol(DBTableModel dtldbmodel){
            DBTableModel reportdbmodel = report.getDbmodel();
            if(dtldbmodel==null){
                return;
            }

            Enumeration<DBColumnDisplayInfo> en = reportdbmodel.getDisplaycolumninfos().elements();
            while (en.hasMoreElements()){
                DBColumnDisplayInfo colinfo =  en.nextElement();
                dtldbmodel.appendRow();
                int row=dtldbmodel.getRowCount()-1;
                dtldbmodel.setItemValue(row,"func","\""+colinfo.getColname()+"\"");
                dtldbmodel.setItemValue(row,"desc",colinfo.getTitle());
            }
        }


        /**
         * 点击了显示函数的table上的一列
         */
        class FunctableMouseHandle implements MouseListener{
            public void mouseClicked(MouseEvent e) {
                int row = functable.rowAtPoint(e.getPoint());
                if(row>=0){
                    String funcname=(String)functable.getValueAt(row,0);
                    replaceExpr(funcname);
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        }


        class DtlcoltableMouseHandle implements MouseListener{
            public void mouseClicked(MouseEvent e) {
                int row = dtlcoltable.rowAtPoint(e.getPoint());
                if(row>=0){
                    String funcname=(String)dtlcoltable.getValueAt(row,0);
                    replaceExpr(funcname);
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        }

        class AsttableMouseHandle implements MouseListener{
            public void mouseClicked(MouseEvent e) {
                int row = astcoltable.rowAtPoint(e.getPoint());
                if(row>=0){
                    String funcname=(String)astcoltable.getValueAt(row,0);
                    replaceExpr(funcname);
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        }
        private void insertFuncvalue(DBTableModel dbmodel) {
            String funcnamedescs[]={
                "today()","日期",
                "now()","时间",
                "username()","制单人",
                "pageno()","页码",
                "pagecount()","页数",
                "getitemmst(总单字段名)","取总单字段值",
                "getitemdtl(细单字段名)","取细单字段",
                "rowcountmst()","总单记录数",
                "rowcountdtl()","细单记录数",
                "summst(总单列名)","总单列合计",
                "sumdtl(细单列名)","细单列合计",
            };

            for(int i=0;i<funcnamedescs.length;i+=2){
                dbmodel.appendRow();
                int r=dbmodel.getRowCount()-1;
                dbmodel.setItemValue(r,"func",funcnamedescs[i]);
                dbmodel.setItemValue(r,"desc",funcnamedescs[i+1]);
            }

            table.addRowSelectionInterval(0,0);
        }



        private void bindPropvalue(DBTableModel dbmodel) {
            dbmodel.appendRow();
            int row = dbmodel.getRowCount() - 1;
            Font font = activecomp.getFont();
            dbmodel.setItemValue(row, "name", "字体");
            dbmodel.setItemValue(row, "value", font.getName());

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "字体大小");
            dbmodel.setItemValue(row, "value", String.valueOf(font.getSize()));

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "风格");
            String strstyle = "普通";
            int style = font.getStyle();
            if ((style & Font.BOLD) != 0) {
                strstyle = "加粗";
            } else if ((style & Font.ITALIC) != 0) {
                strstyle = "倾斜";
            }
            dbmodel.setItemValue(row, "value", strstyle);

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "对齐");
            int align = activecomp.getHorizontalAlignment();
            String stralign = "";
            if (align == JLabel.LEFT) {
                stralign = "左对齐";
            } else if (align == JLabel.CENTER) {
                stralign = "居中";
            } else if (align == JLabel.RIGHT) {
                stralign = "右对齐";
            }else{
                stralign = "左对齐";
            }
            dbmodel.setItemValue(row, "value", stralign);


            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "X");
            dbmodel.setItemValue(row, "value", String.valueOf(activecomp.getX()));

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "Y");
            dbmodel.setItemValue(row, "value", String.valueOf(activecomp.getY()));

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "宽");
            dbmodel.setItemValue(row, "value", String.valueOf(activecomp.getWidth()));

            dbmodel.appendRow();
            row = dbmodel.getRowCount() - 1;
            dbmodel.setItemValue(row, "name", "高");
            dbmodel.setItemValue(row, "value", String.valueOf(activecomp.getHeight()));
        }

        private void revserseBindPropvalue() {
            TableCellEditor cellEditor = table.getCellEditor();
            if(cellEditor!=null){
                cellEditor.stopCellEditing();
            }

            for (int r = 0; r < table.getRowCount(); r++) {
                dbmodel.setItemValue(r, "value", (String) table.getValueAt(r, 1));
            }

            PLabelCell labelcell = compmap.get(activecomp);
            String fontname = dbmodel.getItemValue(0, "value");
            int fontsize = activecomp.getFont().getSize();
            try {
                fontsize = Integer.parseInt(dbmodel.getItemValue(1, "value"));
            } catch (NumberFormatException e) {
            }
            String strstyle = dbmodel.getItemValue(2, "value");
            int style = Font.PLAIN;
            if (strstyle.equals("加粗")) {
                style = Font.BOLD;
            } else if (strstyle.equals("倾斜")) {
                style = Font.ITALIC;
            }
            Font newfont = new Font(fontname, style, fontsize);
            labelcell.setFont(newfont);
            activecomp.setFont(newfont);

            int align = JLabel.CENTER;
            String stralign = dbmodel.getItemValue(3, "value");
            if (stralign.equals("左对齐")) {
                align = JLabel.LEFT;
            } else if (stralign.equals("右对齐")) {
                align = JLabel.RIGHT;
            }
            activecomp.setHorizontalAlignment(align);
            labelcell.setAlign(align);


            int x = activecomp.getX();
            try {
                x = Integer.parseInt(dbmodel.getItemValue(4, "value"));
            } catch (NumberFormatException e) {
            }
            int y = activecomp.getX();
            try {
                y = Integer.parseInt(dbmodel.getItemValue(5, "value"));
            } catch (NumberFormatException e) {
            }
            int w = activecomp.getX();
            try {
                w = Integer.parseInt(dbmodel.getItemValue(6, "value"));
            } catch (NumberFormatException e) {
            }
            int h = activecomp.getX();
            try {
                h = Integer.parseInt(dbmodel.getItemValue(7, "value"));
            } catch (NumberFormatException e) {
            }
            labelcell.setX(x);
            labelcell.setY(y);
            labelcell.setWidth(w);
            labelcell.setHeight(h);
            activecomp.setLocation(x, y);
            activecomp.setSize(w, h);

            labelcell.setExpr(textExpr.getText());
            activecomp.setText(labelcell.calcExpr());
        }


        JPanel createBottompanel() {
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout());

            btnok = new JButton("确定");
            btnok.setActionCommand("ok");
            btnok.addActionListener(this);
            jp.add(btnok);

            JButton btncanel = new JButton("取消");
            btncanel.setActionCommand("cancel");
            btncanel.addActionListener(this);
            jp.add(btncanel);

            return jp;
        }

        class FuncTable extends CTable{
            public FuncTable(TableModel dm) {
                super(dm);
                setReadonly(true);
            }
        }

        class AstdbmodelColumntable extends CTable {
            public AstdbmodelColumntable(DBTableModel dbmodel) {
                super(dbmodel);
                setReadonly(true);
            }
        }

        class DbmodelColumntable extends CTable {
            public DbmodelColumntable(DBTableModel dbmodel) {
                super(dbmodel);
                setReadonly(true);
            }
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("ok")) {

                //让光标离开现在正在编辑的表格项
                btnok.requestFocus();
                revserseBindPropvalue();

                drawpanel.doLayout();
                //刷新
                PageHeadSetupPane.this.invalidate();
                PageHeadSetupPane.this.repaint();

                this.dispose();
            } else if (cmd.equals("cancel")) {
                this.dispose();
            } else if( cmd.equals("addfunction")){
                SysfuncDialog dlg=new SysfuncDialog(PropsetupDialog.this);
                dlg.pack();
                dlg.setVisible(true);

            } else if(cmd.equals("addadd")){
                replaceExpr("+");
            } else if(cmd.equals("addmin")){
                replaceExpr("-");
            } else if(cmd.equals("addmul")){
                replaceExpr("*");
            } else if(cmd.equals("adddiv")){
                replaceExpr("/");
            } else if(cmd.equals("(")){
                replaceExpr("(");
            } else if(cmd.equals(")")){
                replaceExpr(")");
            } else if(cmd.equals("checkexpr")){
            	checkexpr();
            }
            	
        }
        void replaceExpr(String s){
            textExpr.replaceSelection(s);
        }
        /**
         * 检查用户输入的表达式是不是对的
         */
        void checkexpr(){
        	String expr=textExpr.getText();
        	ExprCalcer calc=new ExprCalcer(report);
        	try {
				calc.calc(0,expr);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "表达式错误"+e.getMessage());
			}
        }
    }
    


    class CellinfoTable extends CTable {
        private DefaultCellEditor fontnameeditor;
        private DefaultCellEditor fontstyleeditor;
        private DefaultCellEditor aligneditor;

        public CellinfoTable(DBTableModel dbmodel) {
            super(dbmodel);

            String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            JComboBox cbfontname = new JComboBox(fontnames);
            fontnameeditor = new DefaultCellEditor(cbfontname);

            String[] fontstyles = {"普通", "加粗", "倾斜"};
            JComboBox cbfontstyles = new JComboBox(fontstyles);
            fontstyleeditor = new DefaultCellEditor(cbfontstyles);

            String aligns[] = {"左对齐", "居中", "右对齐"};
            JComboBox cbalign = new JComboBox(aligns);
            aligneditor = new DefaultCellEditor(cbalign);

        }

        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }


        public TableCellEditor getCellEditor(int row, int col) {
            if (row == 0 && col == 1) {
                return fontnameeditor;
            } else if (row == 2 && col == 1) {
                return fontstyleeditor;
            } else if (row == 3 && col == 1) {
                return aligneditor;
            }
            return super.getCellEditor(row, col);
        }
    }


}
