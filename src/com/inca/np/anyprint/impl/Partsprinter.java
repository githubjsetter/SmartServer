package com.inca.np.anyprint.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import sun.print.PeekGraphics;

public class Partsprinter implements Printable, Pageable {
	Parts parts = null;
	PageFormat pagefmt=null;

	public Partsprinter(Parts parts,PageFormat pagefmt){
		this.parts=parts;
		this.pagefmt=pagefmt;
	}
	
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		int pageno = pageIndex / parts.getHorizontalPagecount();
		int hpageno = pageIndex % parts.getHorizontalPagecount();
		Paper paper = pageFormat.getPaper();
		parts.setIsprinting(graphics instanceof PeekGraphics);
		return parts.printPage((Graphics2D) graphics, (int) paper.getWidth(),
				(int) paper.getHeight(), pageno, hpageno);

	}

	/**
	 * 送给打印机的页数等于页数乘以横向页数
	 */
	public int getNumberOfPages() {
		return parts.getPagecount() * parts.getHorizontalPagecount();
	}

	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		return pagefmt;
	}

	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {
		return this;
	}

}
