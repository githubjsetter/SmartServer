package com.inca.np.anyprint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

import com.barcodelib.barcode.Linear;

public class BarcodeCreator {
	/**
	 * »­EAN13ÌõÂë
	 * 
	 * @param g
	 * @param rect
	 * @param value
	 * @throws Exception
	 */
	public static void drawEan13bar(Graphics2D gtarget, Rectangle rect,
			String value) throws Exception {

		EAN13Bean bean = new EAN13Bean();

		int dpi = 150;
		// bean.setModuleWidth(UnitConv.in2mm(2f / (float)dpi)); // makes the
		// narrow bar
		bean.setModuleWidth(1);
		bean.setBarHeight(40.0);
		bean.setFontSize(10.0);
		bean.setQuietZone(10.0);
		bean.doQuietZone(true);

		BarcodeDimension dim = bean.calcDimensions(value);
		int offset = 3;
		Rectangle rect1 = (Rectangle) rect.clone();
		rect1.x = rect.x + offset;
		rect1.y = rect.y + offset;
		rect1.width = rect.width - 2 * offset;
		rect1.height = rect.height - 2 * offset;

		int width = (int) dim.getWidth(0) + 20;
		int height = (int) dim.getHeight(0);
		double rx = (double) rect1.width / (double) width;
		double ry = (double) rect1.height / (double) height;

		// BufferedImage imgtext = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2d = imgtext.createGraphics();

		// g2d.setColor(Color.WHITE);
		// g2d.fillRect(0, 0, width, height);

		// g2d.setColor(Color.BLACK);

		Graphics2D gtmp = (Graphics2D) gtarget.create(rect1.x, rect1.y,
				rect1.width, rect1.height);
		gtmp.scale(rx, ry);
		bean.generateBarcode(new Java2DCanvasProvider(gtmp, 0), value);
		gtmp.dispose();
		/*
		 * g2d.dispose(); ImageIO.write(imgtext, "png", new File("bar.png"));
		 * 
		 * 
		 * double xscale=(double)rect.width / dim.getWidth(); double
		 * yscale=(double)rect.height / dim.getHeight();
		 * 
		 * AffineTransform oldaft=((Graphics2D)g).getTransform();
		 * AffineTransform aft=new AffineTransform();
		 * aft.translate(rect.x,rect.y); aft.scale(xscale, yscale);
		 * 
		 * ((Graphics2D)g).setTransform(aft);
		 * g.drawImage(imgtext,0,0,(int)dim.getWidth
		 * (),(int)dim.getHeight(),null); ((Graphics2D)g).setTransform(oldaft);
		 */
	}

	public static void drawCode128(Graphics2D gtarget, Rectangle rect,
			String value) throws Exception {

		Code128Bean bean = new Code128Bean();

		int dpi = 150;
		// bean.setModuleWidth(UnitConv.in2mm(2f / (float)dpi)); // makes the
		// narrow bar
		bean.setModuleWidth(1);
		bean.setBarHeight(40.0);
		bean.setFontSize(10.0);
		bean.setQuietZone(10.0);
		bean.doQuietZone(true);

		BarcodeDimension dim = bean.calcDimensions(value);
		int offset = 3;
		Rectangle rect1 = (Rectangle) rect.clone();
		rect1.x = rect.x + offset;
		rect1.y = rect.y + offset;
		rect1.width = rect.width - 2 * offset;
		rect1.height = rect.height - 2 * offset;

		int width = (int) dim.getWidth(0) + 20;
		int height = (int) dim.getHeight(0);
		double rx = (double) rect1.width / (double) width;
		double ry = (double) rect1.height / (double) height;

		// BufferedImage imgtext = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2d = imgtext.createGraphics();

		// g2d.setColor(Color.WHITE);
		// g2d.fillRect(0, 0, width, height);

		// g2d.setColor(Color.BLACK);

		Graphics2D gtmp = (Graphics2D) gtarget.create(rect1.x, rect1.y,
				rect1.width, rect1.height);
		gtmp.scale(rx, ry);
		bean.generateBarcode(new Java2DCanvasProvider(gtmp, 0), value);
		gtmp.dispose();
		/*
		 * g2d.dispose(); ImageIO.write(imgtext, "png", new File("bar.png"));
		 * 
		 * 
		 * double xscale=(double)rect.width / dim.getWidth(); double
		 * yscale=(double)rect.height / dim.getHeight();
		 * 
		 * AffineTransform oldaft=((Graphics2D)g).getTransform();
		 * AffineTransform aft=new AffineTransform();
		 * aft.translate(rect.x,rect.y); aft.scale(xscale, yscale);
		 * 
		 * ((Graphics2D)g).setTransform(aft);
		 * g.drawImage(imgtext,0,0,(int)dim.getWidth
		 * (),(int)dim.getHeight(),null); ((Graphics2D)g).setTransform(oldaft);
		 */
	}

	public static void drawDataMatrix(Graphics2D gtarget, Rectangle rect,
			String value) throws Exception {

		DataMatrixBean bean = new DataMatrixBean();

		int dpi = 150;
		// bean.setModuleWidth(UnitConv.in2mm(2f / (float)dpi)); // makes the
		// narrow bar
		bean.setModuleWidth(1);
		bean.setBarHeight(40.0);
		bean.setFontSize(10.0);
		bean.setQuietZone(10.0);
		bean.doQuietZone(true);

		BarcodeDimension dim = bean.calcDimensions(value);
		int offset = 3;
		Rectangle rect1 = (Rectangle) rect.clone();
		rect1.x = rect.x + offset;
		rect1.y = rect.y + offset;
		rect1.width = rect.width - 2 * offset;
		rect1.height = rect.height - 2 * offset;

		int width = (int) dim.getWidth(0) + 20;
		int height = (int) dim.getHeight(0);
		double rx = (double) rect1.width / (double) width;
		double ry = (double) rect1.height / (double) height;

		// BufferedImage imgtext = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2d = imgtext.createGraphics();

		// g2d.setColor(Color.WHITE);
		// g2d.fillRect(0, 0, width, height);

		// g2d.setColor(Color.BLACK);

		Graphics2D gtmp = (Graphics2D) gtarget.create(rect1.x, rect1.y,
				rect1.width, rect1.height);
		gtmp.scale(rx, ry);
		bean.generateBarcode(new Java2DCanvasProvider(gtmp, 0), value);
		gtmp.dispose();
		/*
		 * g2d.dispose(); ImageIO.write(imgtext, "png", new File("bar.png"));
		 * 
		 * 
		 * double xscale=(double)rect.width / dim.getWidth(); double
		 * yscale=(double)rect.height / dim.getHeight();
		 * 
		 * AffineTransform oldaft=((Graphics2D)g).getTransform();
		 * AffineTransform aft=new AffineTransform();
		 * aft.translate(rect.x,rect.y); aft.scale(xscale, yscale);
		 * 
		 * ((Graphics2D)g).setTransform(aft);
		 * g.drawImage(imgtext,0,0,(int)dim.getWidth
		 * (),(int)dim.getHeight(),null); ((Graphics2D)g).setTransform(oldaft);
		 */
	}

	public static void drawPdf417(Graphics2D gtarget, Rectangle rect,
			String value) throws Exception {

		PDF417Bean bean = new PDF417Bean();

		int dpi = 150;
		// bean.setModuleWidth(UnitConv.in2mm(2f / (float)dpi)); // makes the
		// narrow bar
		bean.setModuleWidth(1);
		bean.setBarHeight(40.0);
		bean.setFontSize(10.0);
		bean.setQuietZone(10.0);
		bean.doQuietZone(true);

		BarcodeDimension dim = bean.calcDimensions(value);
		int offset = 3;
		Rectangle rect1 = (Rectangle) rect.clone();
		rect1.x = rect.x + offset;
		rect1.y = rect.y + offset;
		rect1.width = rect.width - 2 * offset;
		rect1.height = rect.height - 2 * offset;

		int width = (int) dim.getWidth(0) + 20;
		int height = (int) dim.getHeight(0);
		double rx = (double) rect1.width / (double) width;
		double ry = (double) rect1.height / (double) height;

		// BufferedImage imgtext = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2d = imgtext.createGraphics();

		// g2d.setColor(Color.WHITE);
		// g2d.fillRect(0, 0, width, height);

		// g2d.setColor(Color.BLACK);

		Graphics2D gtmp = (Graphics2D) gtarget.create(rect1.x, rect1.y,
				rect1.width, rect1.height);
		//gtmp.scale(rx, ry);
		bean.generateBarcode(new Java2DCanvasProvider(gtmp, 0), value);
		gtmp.dispose();
		/*
		 * g2d.dispose(); ImageIO.write(imgtext, "png", new File("bar.png"));
		 * 	
		 * 
		 * double xscale=(double)rect.width / dim.getWidth(); double
		 * yscale=(double)rect.height / dim.getHeight();
		 * 
		 * AffineTransform oldaft=((Graphics2D)g).getTransform();
		 * AffineTransform aft=new AffineTransform();
		 * aft.translate(rect.x,rect.y); aft.scale(xscale, yscale);
		 * 
		 * ((Graphics2D)g).setTransform(aft);
		 * g.drawImage(imgtext,0,0,(int)dim.getWidth
		 * (),(int)dim.getHeight(),null); ((Graphics2D)g).setTransform(oldaft);
		 */
	}

	public static void drawEANUCC128(Graphics2D gtarget, Rectangle rect,
			String value) throws Exception {
		Linear linear = new Linear();
		linear.setData(value);
		linear.setType(Linear.EAN128);
		try {
			BufferedImage img = linear.renderBarcode();
			int w = img.getWidth();
			int h = img.getHeight();
			int offseth = (int) ((float) h * 0.15f);
			BufferedImage img1=new BufferedImage(w,h-offseth,BufferedImage.TYPE_INT_RGB);
			img1.getGraphics().drawImage(img,0,-offseth,w,h,null);
			
			//ImageIO.write(img1,"png",new File("img1.png"));
			
			w=img1.getWidth();
			h=img1.getHeight();
			
			double rx=(double)rect.width / (double)w;
			double ry=(double)rect.height / (double)h;
			
			Graphics2D gtmp = (Graphics2D) gtarget.create(rect.x, rect.y,
					rect.width, rect.height);
			gtmp.scale(rx,ry);
			gtmp.drawImage(img1, 0,0,w,h,null);
			gtmp.dispose();

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		BufferedImage img = new BufferedImage(600, 150,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 600, 150);

		g.setColor(Color.black);
		Rectangle rect = new Rectangle();
		rect.x = 80;
		rect.y = 10;
		rect.width = 300;
		rect.height = 120;
		try {
			// drawCode128(g,rect,"6901028071468");
			drawEANUCC128(g, rect, "(401)69353464 000000000001 1 z");
			ImageIO.write(img, "png", new File("bar1.png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
