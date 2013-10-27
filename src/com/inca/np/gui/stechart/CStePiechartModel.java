package com.inca.np.gui.stechart;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBTableModel;

import java.awt.*;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-1
 * Time: 10:52:02
 * »­±ýÍ¼
 */
public abstract class CStePiechartModel extends CStechartModel{
    protected CStePiechartModel(CFrame frame, String title) throws HeadlessException {
        super(frame, title);
    }

    protected void createChart(){
        DefaultPieDataset dataset = bindPieCategoryDataset();

        JFreeChart chart = null;/*ChartFactory.createPieChart(this.getTitle(),
                dataset,true,false,false);*/

        chartcanvas.setChart(chart);
        chartcanvas.invalidate();
        chartcanvas.repaint();
    }

    DefaultPieDataset bindPieCategoryDataset() {
        DefaultPieDataset dataset=new DefaultPieDataset();
        
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
            dataset.setValue(title,dv);
        }
        return dataset;
    }

}
