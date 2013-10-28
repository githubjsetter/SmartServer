package com.smart.platform.print.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import org.apache.log4j.Category;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.print.report.AccessableReport;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-9
 * Time: 19:45:17
 * 报表定义
 */
public class PReport implements Printable, Pageable,AccessableReport {
	Category logger=Category.getInstance(PReport.class);
    /**
     * 主数据源
     */
    DBTableModel dbmodel = null;

    /**
     * 辅助数据源
     */
    DBTableModel masterdbmodel = null;

    int masterdbmodelrow=-1;

    /**
     * 页
     */
    protected PPage ppage = new PPage(this);


    /**
     * 简化处理,设置缺省的标题
     */
    private String defaulttitle = "";

    int printingpageno = 0;

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        printingpageno = pageIndex;

        Graphics2D g2 = (Graphics2D) graphics;
        if (pageFormat != null) {

            int imgw = (int) (pageFormat.getPaper().getImageableWidth());
            int imgh = (int) (pageFormat.getPaper().getImageableHeight());
            ppage.setWidth(imgw);
            ppage.setHeight(imgh);

            int imgx = (int)pageFormat.getPaper().getImageableX();
            int imgy = (int)pageFormat.getPaper().getImageableY();
            g2 = (Graphics2D)g2.create((int)imgx,(int)imgy,imgw,imgh);

        } else {
            ppage.setWidth(559);
            ppage.setHeight(806);
            //如果不给pageformat，不要设置inited
        }

        prepareData(g2);

        if (pageIndex < 0 || pageIndex >= pagelines.size()) {
            return Printable.NO_SUCH_PAGE;
        }

        //调用页打印

        g2.setColor(Color.BLACK);


        PagelineInfo pagelineInfo = pagelines.elementAt(pageIndex);
        ppage.paint(g2, dbmodel, masterdbmodel, pagelineInfo.getStartrow(), pagelineInfo.getEndrow(),
                pagelineInfo.getColumns());



/*
        BufferedImage img=g2.getDeviceConfiguration().createCompatibleImage(100,100);
        Graphics2D tmpg = (Graphics2D) img.getGraphics();
        tmpg.setBackground(Color.WHITE);
        tmpg.fillRect(0,0,100,100);
        tmpg.setColor(Color.BLACK);
        tmpg.setRenderingHints(g2.getRenderingHints());
        tmpg.drawString("图片上的文字",30,30);
        g2.drawImage(img,200,200,null);


        g2.drawLine(0,0,200,200);
        Font font=new Font("宋体",Font.PLAIN,20);
        g2.setFont(font);
        g2.drawString("测试打印",100,100);
*/

        //BasicLabelUI


        return Printable.PAGE_EXISTS;
    }

    public int getPrintingpageno() {
        return printingpageno;
    }

    public String getDefaulttitle() {
        return defaulttitle;
    }

    public void setDefaulttitle(String defaulttitle) {
        this.defaulttitle = defaulttitle;
    }

    public int getNumberOfPages() {
        if(pagelines==null){
            return 0;
        }
        return pagelines.size();
    }

    public void setDbmodel(DBTableModel dbmodel) {
        this.dbmodel = dbmodel;
    }

    public PageHeadFoot getPagehead(){
        return ppage.getPhead();
    }

    public PageHeadFoot getPagefoot(){
        return ppage.getPfoot();
    }

    public int getMasterdbmodelrow() {
        return masterdbmodelrow;
    }

    /**
     * 取查询条件的参数
     * @param paramname
     * @return
     */
    public String getParam(String paramname) {
        return paramname;
    }

    public void setMasterdbmodel(DBTableModel masterdbmodel,int masterdbmodelrow) {
        this.masterdbmodel = masterdbmodel;
        this.masterdbmodelrow=masterdbmodelrow;
    }

    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        if (pageIndex < 0 || pageIndex >= getNumberOfPages()) {
            throw  new IndexOutOfBoundsException("只有" + getNumberOfPages() + "页");
        }
        return new ReportPageFormat();
    }

    public DBTableModel getDbmodel() {
        return dbmodel;
    }

    public DBTableModel getMasterDbmodel() {
        return masterdbmodel;
    }

    public PPage getPage() {
        return ppage;
    }

    public void setPage(PPage page) {
        ppage=page;
    }

    class ReportPageFormat extends PageFormat {
        public double getWidth() {
            return ppage.getWidth();
        }

        public double getHeight() {
            return ppage.getHeight();
        }

        public double getImageableX() {
            return 0;
        }

        public double getImageableY() {
            return 0;
        }

        public double getImageableWidth() {
            return ppage.getWidth();
        }

        public double getImageableHeight() {
            return ppage.getHeight();
        }

        public Paper getPaper() {
            return new PPaper(this);
        }

    }

    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        if (pageIndex < 0 || pageIndex >= pagelines.size()) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }


    /**
     * 计算数据.
     */
    protected void prepareData(Graphics2D g2) {
        //要算出有多少页.
    	String sortexpr=getSortexpr();
    	if(sortexpr!=null && sortexpr.length()>0){
    		DBTableModel dbmodel=getDbmodel();
    		try {
				dbmodel.sort(sortexpr);
			} catch (Exception e) {
				logger.error("ERROR",e);
			}
    	}
        splitpage(g2);
    }


    /**
     * 计算各个页的起始行和结束行
     * 结果在pagelines中.
     */
    protected void splitpage(Graphics2D g2) { pagelines = ppage.splitpage(g2, dbmodel);
    }
    
    public int getPagecount(){
    	if(pagelines==null)
    		return 0;
    	return  pagelines.size();
    }


    private Vector<PagelineInfo> pagelines = null;

    public void setPbody(PageBody pbody) {
        ppage.setPbody(pbody);
    }

    public void createDebugReport() {
        //int pwidth = 594;
        //int pheight = 841;
        this.defaulttitle = "货品管理";
        ppage.setDefaultTitle(defaulttitle);
        int linehight = 11;
        PDataline pline = new PDataline();
        pline.setWithborder(true);
        pline.setBorderwidth(1);
        pline.setHeight(linehight);
        pline.setX(20);

        PageBody pbody = new PageBody(this);
        pbody.setDataline(pline);
        pbody.setLineTitleheight(40);
        ppage.setPbody(pbody);

        Vector<PColumnCell> columns = new Vector<PColumnCell>();
        PColumnCell pcolcell = new PColumnCell("goodsid", "货品ID", 40);
        pcolcell.setFreeze(true);
        columns.add(pcolcell);
        pcolcell = new PColumnCell("opcode", "操作码(这是货品的操作码)", 60);
        pcolcell.setFreeze(true);
        columns.add(pcolcell);

        pcolcell = new PColumnCell("goodsname", "品名", 150);
        columns.add(pcolcell);
        pcolcell = new PColumnCell("goodstype", "规格", 150);
        columns.add(pcolcell);
        pbody.setDatalineColumns(columns);


        RemotesqlHelper rmtsql = new RemotesqlHelper();
        String sql = "select goodsid,opcode,goodsname,goodstype from pub_goods order by goodsid";
        try {
            dbmodel = rmtsql.doSelect(sql, 0, 130);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }

/*

        BufferedImage img = new BufferedImage(pwidth, pheight, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pwidth, pheight);

        try {
            prepareData();
            print(g, null, pageno);
        } catch (PrinterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }



        try {
            ImageIO.write(img, "png", new File("rpt.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
*/

    }


    void testFont() {
        String msg = "这是宋体囊";

        int pwidth = 1000;
        int pheight = 1000;

/*
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for(int i=0;i<fonts.length;i++){
            System.out.println(fonts[i]);
        }
*/

        BufferedImage img = new BufferedImage(pwidth, pheight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        double rate = 1;
        AffineTransform transform = new AffineTransform(rate, 0.0,
                0.0, rate,
                0.0, 0.0);
        g.setTransform(transform);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pwidth, pheight);

        Font font = new Font("宋体", Font.PLAIN, 30);

        g.setFont(font);
        Rectangle2D rect = font.getStringBounds(msg, g.getFontRenderContext());

        //Font font = g.getFont();
        LineMetrics linemertics = font.getLineMetrics(msg, g.getFontRenderContext());
        float fonth = linemertics.getHeight();


        g.setColor(Color.BLACK);
        g.drawLine(0, (int) -rect.getY(), pwidth, (int) -rect.getY());
        g.drawString(msg, 0, fonth);

        try {
            ImageIO.write(img, "png", new File("rpt.png"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public BufferedImage createReportImage(int pageindex,PageFormat pageformat) throws Exception {
        Paper paper = pageformat.getPaper();
        int pwidth = (int)paper.getWidth();
        ppage.setWidth(pwidth);
        int pheight = (int)paper.getHeight();
        ppage.setHeight(pheight);

        logger.info("createReportImage pwidth="+pwidth+",pheight="+pheight);
        BufferedImage img = new BufferedImage(pwidth, pheight, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pwidth, pheight);
        print(g, pageformat, pageindex);
        return img;

    }


    public void sendPrinter(String jobname) throws Exception {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService service = services[0];
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(service);
        job.setCopies(1);
        job.setJobName(jobname);

        //先设pageable
        job.setPageable(this);
        job.setPrintable(this);


        HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();
        prats.add(new Copies(1));
        //	Set Orientation
        prats.add(OrientationRequested.PORTRAIT);

        job.printDialog(prats);

        //reset print area
        MediaSizeName sizenameattr = (MediaSizeName) prats.get(Media.class);
        MediaSize mediasize = MediaSize.getMediaSizeForName(sizenameattr);
        float[] size = mediasize.getSize(MediaSize.INCH);

        float pw = size[0] * 72;
        float ph = size[1] * 72;
        float px=12;
        float py=12;
        pw = pw - 2 * px;
        ph = ph - 2 * py;

        MediaPrintableArea printarea = new MediaPrintableArea(px/72,py/72,pw/72,ph/72,MediaPrintableArea.INCH);
        prats.add(printarea);

        job.print(prats);

    }

    public String getSortexpr(){
        return ppage.getSortexpr();
    }
    public void setSortexpr(String s){
    	ppage.setSortexpr(s);
    }

    /**
     * 生成图片
     * public BufferedImage createReportImage() {
     * int pwidth = 491;
     * int pheight = 800;
     * ppage.setWidth(pwidth);
     * ppage.setHeight(pheight);
     * BufferedImage img = new BufferedImage(pwidth, pheight, BufferedImage.TYPE_INT_RGB);
     * Graphics g = img.getGraphics();
     * g.setColor(Color.WHITE);
     * g.fillRect(0, 0, pwidth, pheight);
     * try {
     * print(g, null, 0);
     * ImageIO.write(img, "png", new File("rpt.png"));
     * <p/>
     * return img;
     * } catch (Exception e) {
     * e.printStackTrace();
     * return null;
     * }
     * }
     */


    public static void main(String argv[]) {
        PReport report = new PReport();


        report.createDebugReport();




/*
        report.createReportImage();
        if(true)return;
*/




        try {
            report.sendPrinter("打印测试");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
