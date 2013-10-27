package com.inca.np.gui.stechart;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CScrollPane;
import com.inca.np.gui.control.CStatusbar;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-31
 * Time: 14:55:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class CStechartModel extends CSteModel {

    /**
     * ͼ�εĻ���
     */
    protected ChartPanel chartcanvas = new ChartPanel();

    protected CStechartModel(CFrame frame, String title) throws HeadlessException {
        super(frame, title);
    }


    /**
     * �����ط�ͬste,����tablescrollpane�в�Ҫ�ű���,����һ������
     */
    protected void initControl() {
        rootpanel.setLayout(new BorderLayout());

        toolbar = createToolbar();
        rootpanel.add(toolbar, BorderLayout.NORTH);

        tablescrollpane = new CScrollPane();
        //tablescrollpane.addMouseListener(new ScrollpaneListener());
        rootpanel.add(tablescrollpane, BorderLayout.CENTER);

        recreateDBModel();

        chartcanvas.setPreferredSize(new Dimension(800,600));
        tablescrollpane.setViewportView(chartcanvas);

        form = createForm();
        //SwingUtilities.get

/*        steformwindow = createFormwindow();
        steformwindow.pack();
//        FocusTraversalPolicy focuspolicy = steformwindow.getFocusTraversalPolicy();
        steformwindow.setVisible(false);
*/
        //����״̬��
        statusbar = new CStatusbar();
        rootpanel.add(statusbar, BorderLayout.SOUTH);
        statusbar.setStatus("����");


        controlinited = true;
    }


    /**
     * ��ѯ��ɺ�,�����������
     */
    protected void on_retrieved() {
        super.on_retrieved();
        createChart();

    }

    protected void createChart(){
        CategoryDataset dataset = bindCategoryDataset();

/*
        JFreeChart chart = ChartFactory.createLineChart3D(
                super.getTitle(), // ͼ�����
                getCategoryTitle(), // Ŀ¼�����ʾ��ǩ
                getValueTitle(), // ��ֵ�����ʾ��ǩ
                dataset, // ���ݼ�
                PlotOrientation.VERTICAL, // ͼ����ˮƽ����ֱ
                true,     // �Ƿ���ʾͼ��(���ڼ򵥵���״ͼ������false)
                false,     // �Ƿ����ɹ���
                false     // �Ƿ�����URL����
                );
*/

        JFreeChart chart = ChartFactory.createBarChart3D(
                super.getTitle(), // ͼ�����
                getCategoryTitle(), // Ŀ¼�����ʾ��ǩ
                getValueTitle(), // ��ֵ�����ʾ��ǩ
                dataset, // ���ݼ�
                PlotOrientation.VERTICAL, // ͼ����ˮƽ����ֱ
                true,     // �Ƿ���ʾͼ��(���ڼ򵥵���״ͼ������false)
                false,     // �Ƿ����ɹ���
                false     // �Ƿ�����URL����
        );


        chartcanvas.setChart(chart);
        chartcanvas.invalidate();
        chartcanvas.repaint();
    }

    protected CategoryDataset bindCategoryDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
/*
        dataset.addValue(100, "����", "Ʒһ");
        dataset.addValue(100, "�Ϻ�", "Ʒһ");
        dataset.addValue(100, "����", "Ʒһ");
        dataset.addValue(200, "����", "Ʒ��");
        dataset.addValue(200, "�Ϻ�", "Ʒ��");
        dataset.addValue(200, "����", "Ʒ��");
        dataset.addValue(300, "����", "Ʒ��");
        dataset.addValue(300, "�Ϻ�", "Ʒ��");
        dataset.addValue(300, "����", "Ʒ��");
        dataset.addValue(400, "����", "Ʒ��");
        dataset.addValue(400, "�Ϻ�", "Ʒ��");
        dataset.addValue(400, "����", "Ʒ��");
        dataset.addValue(500, "����", "Ʒ��");
        dataset.addValue(500, "�Ϻ�", "Ʒ��");
        dataset.addValue(500, "����", "Ʒ��");
*/

        DBTableModel dbmodel = this.getDBtableModel();
        String categorycolname=getCategoryColname();
        String valuecolname=getValueColname();
        for(int r=0;r<dbmodel.getRowCount();r++){
            String v=dbmodel.getItemValue(r,valuecolname);
            double dv=0;
            try{
                dv=Double.parseDouble(v);
            }catch(Exception e){}

            String title=dbmodel.getItemValue(r,categorycolname);

            dataset.addValue(dv,this.getCategoryTitle(),title);
        }
        return dataset;

    }

    /**
     * dbmodel ͼ��ֵ��(����)����������
     * @return
     */
    protected abstract String getValueColname() ;

    /**
     * ��������ϵ��������
     * @return
     */
    protected abstract String getValueTitle() ;


    /**
     * dbmodel ͼ�α�����(����)����������
     * @return
     */
    protected abstract  String getCategoryColname();

    /**
     * ������ϵ��������
     * @return
     */
    protected abstract String getCategoryTitle() ;


    public void setDBtableModel(DBTableModel dbmodel) {
        super.setDBtableModel(dbmodel);

        //���ù�������Դ��ǿ���ػ�
        this.on_retrieved();
    }
}
