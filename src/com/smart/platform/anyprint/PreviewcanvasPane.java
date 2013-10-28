package com.smart.platform.anyprint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

import javax.print.attribute.standard.MediaSize;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.impl.Parts;

/**
 * preview canvas
 * 
 * @author Administrator
 * 
 */
public class PreviewcanvasPane extends JPanel implements ActionListener {

	AnyprintFrame frm = null;
	Printplan plan = null;
	Category logger=Category.getInstance(PreviewcanvasPane.class);
	double scalerate=1.0;
	
	/**
	 * 纸张间距
	 */
	int paperoffset=1;

	public PreviewcanvasPane(AnyprintFrame frm, Printplan plan) {
		this.frm = frm;
		this.plan = plan;

		// 按A4纸设置
		MediaSize size = MediaSize.ISO.A4;
		float floatsize[] = size.getSize(MediaSize.INCH);


	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(plan.getDefaultinputparam().length()==0){
			return;
		}
		
/*		if(plan.getDefaultinputparam().length()==0){
			InputparmDlg dlg=new InputparmDlg(frm,plan);
			dlg.pack();
			dlg.setVisible(true);
			if(dlg.isOk()){
				plan.setInputparam(plan.getDefaultinputparam());
			}
		}
*/		
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldtran=g2.getTransform();
		AffineTransform newtran=new AffineTransform(oldtran);
		newtran.scale(scalerate, scalerate);
		g2.setTransform(newtran);
		draw(g2);
		g2.setTransform(oldtran);
	}

	private int pageno=0;
	public void draw(Graphics2D g2) {
		int width = getPaperwidthPixel();
		int height = getPaperheightPixel();


		Parts parts = plan.getParts();
		try {
			//plan.setInputparam(plan.getDefaultinputparam());
			//parts.prepareData(plan.getDbmodel(),plan.getSplitcolumns());
			int hpageno = 0;
			int x=0;
			for (hpageno = 0; hpageno < parts.getHorizontalPagecount(); hpageno++) {
			//for (hpageno = 0; hpageno <1; hpageno++) {
				Graphics2D partsg=(Graphics2D)g2.create(x,0,width,height);
				//AffineTransform at=partsg.getTransform();
				Color oldc = partsg.getColor();
				partsg.setColor(Color.WHITE);
				partsg.fillRect(0, 0, width, height);

				// ////debug line
				//partsg.setColor(Color.black);
				//partsg.drawLine(0, 0, 100, 100);
				//partsg.drawString("head", 0, 10);
				// ///end of debug

				partsg.setColor(oldc);
				parts.setIsprinting(false);
				parts.printPage(partsg, width, height, pageno, hpageno);
				x+=width+paperoffset;
			}
		} catch (Exception e) {
			logger.error("error",e);
			//JOptionPane.showMessageDialog(frm, "数据源有错误:" + e.getMessage());
			return;
		} finally {
			frm.setDefaultcursor();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		int w=(getPaperwidthPixel() +paperoffset) * plan.getParts().getHorizontalPagecount();
		return new Dimension((int)((double)w*scalerate), (int)((double)getPaperheightPixel()*scalerate));
	}

	public int getPaperwidthPixel() {
		return plan.getParts().getPaperwidthPixel();
	}

	public int getPaperheightPixel() {
		return plan.getParts().getPaperheightPixel();
	}

	public void showPage(int pageno) {
		this.pageno=pageno;
		repaint();
	}

	public double getScalerate() {
		return scalerate;
	}

	public void setScalerate(double scalerate) {
		this.scalerate = scalerate;
	}

}
