package com.smart.bi.client.design;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.smart.platform.gui.control.DBTableModel;

public class Rendertest {
/*
	public void test1() {
		TestdataFactory tdf = new TestdataFactory();
		DBTableModel dm = tdf.createDm();
		BITableV_def tablevdef = tdf.createVtable1();
		BITableV_Render render = new BITableV_Render(tablevdef);
		try {
			render.prepareData(dm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render.dumpPage();
	}

	// 按省分组
	public void test2() {
		TestdataFactory tdf = new TestdataFactory();
		DBTableModel dm = tdf.createDm();
		BITableV_def tablevdef = tdf.createVtable2();
		BITableV_Render render = new BITableV_Render(tablevdef);
		try {
			render.prepareData(dm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render.dumpPage();
	}

	// 按省市分组
	public void test3() {
		TestdataFactory tdf = new TestdataFactory();
		DBTableModel dm = tdf.createDm();
		BITableV_def tablevdef = tdf.createVtable3();
		BITableV_Render render = new BITableV_Render(tablevdef);
		try {
			render.prepareData(dm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render.dumpPage();
	}

	// 按省市货品分组
	public void test4() {
		TestdataFactory tdf = new TestdataFactory();
		DBTableModel dm = tdf.createDm();
		BITableV_def tablevdef = tdf.createVtable4();
		BITableV_Render render = new BITableV_Render(tablevdef);
		try {
			render.prepareData(dm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render.dumpPage();

		for (int p = 0; p < render.getPagecount(); p++) {
			BufferedImage img = new BufferedImage(640, 480,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 640, 480);

		
			render.draw(g2, p);

			try {
				ImageIO.write(img, "png", new File("tablev_" + p + ".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
*/
	public static void main(String[] args) {
		Rendertest t = new Rendertest();
		// t.test1();
		// t.test2();
		// t.test3();
//		t.test4();

	}
}
