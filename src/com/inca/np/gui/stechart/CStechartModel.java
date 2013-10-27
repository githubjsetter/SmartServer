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
     * 图形的画布
     */
    protected ChartPanel chartcanvas = new ChartPanel();

    protected CStechartModel(CFrame frame, String title) throws HeadlessException {
        super(frame, title);
    }


    /**
     * 其它地方同ste,但是tablescrollpane中不要放表了,而是一个画布
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
        //生成状态条
        statusbar = new CStatusbar();
        rootpanel.add(statusbar, BorderLayout.SOUTH);
        statusbar.setStatus("就绪");


        controlinited = true;
    }


    /**
     * 查询完成后,在这里绑定数据
     */
    protected void on_retrieved() {
        super.on_retrieved();
        createChart();

    }

    protected void createChart(){
        CategoryDataset dataset = bindCategoryDataset();

/*
        JFreeChart chart = ChartFactory.createLineChart3D(
                super.getTitle(), // 图表标题
                getCategoryTitle(), // 目录轴的显示标签
                getValueTitle(), // 数值轴的显示标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向：水平、垂直
                true,     // 是否显示图例(对于简单的柱状图必须是false)
                false,     // 是否生成工具
                false     // 是否生成URL链接
                );
*/

        JFreeChart chart = ChartFactory.createBarChart3D(
                super.getTitle(), // 图表标题
                getCategoryTitle(), // 目录轴的显示标签
                getValueTitle(), // 数值轴的显示标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向：水平、垂直
                true,     // 是否显示图例(对于简单的柱状图必须是false)
                false,     // 是否生成工具
                false     // 是否生成URL链接
        );


        chartcanvas.setChart(chart);
        chartcanvas.invalidate();
        chartcanvas.repaint();
    }

    protected CategoryDataset bindCategoryDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
/*
        dataset.addValue(100, "北京", "品一");
        dataset.addValue(100, "上海", "品一");
        dataset.addValue(100, "广州", "品一");
        dataset.addValue(200, "北京", "品二");
        dataset.addValue(200, "上海", "品二");
        dataset.addValue(200, "广州", "品二");
        dataset.addValue(300, "北京", "品三");
        dataset.addValue(300, "上海", "品三");
        dataset.addValue(300, "广州", "品三");
        dataset.addValue(400, "北京", "品四");
        dataset.addValue(400, "上海", "品四");
        dataset.addValue(400, "广州", "品四");
        dataset.addValue(500, "北京", "品五");
        dataset.addValue(500, "上海", "品五");
        dataset.addValue(500, "广州", "品五");
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
     * dbmodel 图形值列(纵轴)的数据列名
     * @return
     */
    protected abstract String getValueColname() ;

    /**
     * 返回纵轴系列中文名
     * @return
     */
    protected abstract String getValueTitle() ;


    /**
     * dbmodel 图形标题列(横轴)的数据列名
     * @return
     */
    protected abstract  String getCategoryColname();

    /**
     * 返回轴系列中文名
     * @return
     */
    protected abstract String getCategoryTitle() ;


    public void setDBtableModel(DBTableModel dbmodel) {
        super.setDBtableModel(dbmodel);

        //设置共享数据源后强制重画
        this.on_retrieved();
    }
}
