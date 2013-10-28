package com.smart.platform.gui.design;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-27
 * Time: 13:44:23
 * ���ڽ���form��grid���õ�Frame
 */
public class DesignFrame extends JFrame implements ActionListener {
    CSteModel stemodel = null;
    private JPanel formMovePanel;
    ColumnMove columnmover = null;
    ProperSetup proppage = null;
    HovSetup hovsetup = null;
    InitvalueSetup initsetup = null;
    RowcheckSetup rowchecksetup = null;
    Tablecolumnmove tablecolumnmove=null;
    Queryorder queryorder=null;

    private JPanel proppane;
    private JTabbedPane tabbedpane;
    private JPanel hovsetupPanel;
    private JPanel initPanel;
    private JPanel rowcheckPanel;
    private JPanel tablecolumnmovepanel;
    JScrollPane queryorderpanel;

    public CSteModel getStemodel() {
		return stemodel;
	}


	public DesignFrame() {
        super("�༭�������");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension scrsize=getToolkit().getScreenSize();
        Dimension size=new Dimension((int)scrsize.getWidth(),(int)scrsize.getHeight()-100);
        setPreferredSize(size);
    }

    private boolean initcontrol = false;

    private void initControl() {
        Container cp = this.getContentPane();

        cp.setLayout(new BorderLayout());
        tabbedpane = new JTabbedPane();
        cp.add(tabbedpane, BorderLayout.CENTER);
        tabbedpane.addChangeListener(new ChangEventHandle());

        proppane = new JPanel();
        tabbedpane.add("�ֶ�����", proppane);

        formMovePanel = new JPanel();
        JScrollPane scrollpane = new JScrollPane(formMovePanel);
        tabbedpane.add("�ֶ�˳��", scrollpane);
        
        tablecolumnmovepanel=new JPanel();
        tabbedpane.add("���˳��", tablecolumnmovepanel);

        queryorderpanel=new JScrollPane();
        tabbedpane.add("��ѯ˳��", queryorderpanel);
        
        hovsetupPanel = new JPanel();
        tabbedpane.add("HOV����", hovsetupPanel);

        initPanel = new JPanel();
        tabbedpane.add("��ֵ�趨", initPanel);

        rowcheckPanel = new JPanel();
        tabbedpane.add("�м��", rowcheckPanel);

        JPanel bottompanel = new JPanel();
        cp.add(bottompanel, BorderLayout.SOUTH);

        JButton buttonok = new JButton("����");
        buttonok.addActionListener(this);
        bottompanel.add(buttonok);


        pack();
        setVisible(true);

    }


    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("����")) {
            //����
            proppage.reversebindData();
            stemodel.recreateTable();
            hovsetup.reversebindData();
            initsetup.reverseBinddata();
            rowchecksetup.reverseBinddata();
            tablecolumnmove.reverseBinddata();
            queryorder.reverseBind();

            this.dispose();

            stemodel.recreateDBModel();
            stemodel.setTableautosized(false);
            stemodel.recreateTable();
            stemodel.fireDBColumnChanged();
        }
    }

    private int lasttabbedpaneselected = -1;

    private class ChangEventHandle implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            if (proppage != null) {
                proppage.reversebindData();
                //Ϊ���ⷴ������ר��hov���ڸı�tabҳʱ�����ٵ���hovsetup��reversebinddata()
                hovsetup.reversebindData();
                initsetup.reverseBinddata();
                rowchecksetup.reverseBinddata();
                tablecolumnmove.reverseBinddata();
                queryorder.reverseBind();
                drawAll();
            }
            lasttabbedpaneselected = tabbedpane.getSelectedIndex();
        }
    }


    public void doDesign(CSteModel stemodel) {
        this.stemodel = stemodel;
        drawAll();
    }

    public void fireColumnOrderMoved(){
        proppage.createPropPane(proppane);
        hovsetup.createHovPane(hovsetupPanel);
        initsetup.createInitPane(initPanel);
        rowchecksetup.createRowcheckPane(rowcheckPanel);
        columnmover.createFormMovePanel(formMovePanel);
        invalidate();
        repaint();
    }

    public void drawAll() {
        if (!initcontrol) {
            initControl();
            initcontrol = true;
        }
        if (columnmover == null) {
            columnmover = new ColumnMove(this, stemodel.getFormcolumndisplayinfos());
        }
        columnmover.createFormMovePanel(formMovePanel);

        if (proppage == null) {
            proppage = new ProperSetup(this, stemodel.getFormcolumndisplayinfos(),stemodel);
        }
        proppage.createPropPane(proppane);

        if (hovsetup == null) {
            hovsetup = new HovSetup(this, stemodel);
        }
        hovsetup.createHovPane(hovsetupPanel);

        if (initsetup == null) {
            initsetup = new InitvalueSetup(this, stemodel.getFormcolumndisplayinfos());
        }
        initsetup.createInitPane(initPanel);

        if (rowchecksetup == null) {
            rowchecksetup = new RowcheckSetup(this, stemodel.getFormcolumndisplayinfos());
        }
        rowchecksetup.createRowcheckPane(rowcheckPanel);

        
        if (tablecolumnmove == null) {
        	tablecolumnmove = new Tablecolumnmove(this, stemodel.getFormcolumndisplayinfos(),stemodel);
        }
        tablecolumnmove.createPanel(tablecolumnmovepanel);
        
        if (queryorder == null) {
        	queryorder = new Queryorder(this, stemodel);
        }
        queryorder.createQueryMovePanel(queryorderpanel);
        
        
        
        invalidate();
        repaint();
    }
    
    


    public static void main(String argv[]) {
/*
        Pub_goods_model goodsmodel = new Pub_goods_model(null);
        try {
            DBColumnInfoStoreHelp.writeFile(goodsmodel,new File("c:\\a\\pubgoods.model"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
*/

        Pub_goods_ste goodsmodel = new Pub_goods_ste(null);
        try {
            DBColumnInfoStoreHelp.readFile(goodsmodel, new File("c:\\a\\pubgoods.model"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = goodsmodel.getFormcolumndisplayinfos();
        DesignFrame frm = new DesignFrame();
        frm.doDesign(goodsmodel);
    }
}
