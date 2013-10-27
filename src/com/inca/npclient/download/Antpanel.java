package com.inca.npclient.download;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import com.inca.npserver.clientinstall.Blockinfo;

public class Antpanel extends javax.swing.JPanel {

	/**
	 * Ô²È¦Ö±¾¶
	 */
	int antsize = 10;

	ArrayList<Blockinfo> blocks = null;
	Color colotable[] = new Color[10];

	Antpanel() {
		colotable[0] = new Color(153, 153, 255);
		colotable[1] = new Color(153, 51, 102);
		colotable[2] = new Color(255, 0, 255);
		colotable[3] = new Color(204, 255, 255);
		colotable[4] = new Color(88, 0, 88);
		colotable[5] = new Color(255, 128, 128);
		colotable[6] = new Color(0, 102, 204);
		colotable[7] = new Color(204, 204, 255);
		colotable[8] = new Color(0, 0, 128);
		colotable[9] = new Color(255, 255, 204);
	}

	public void setBlocks(ArrayList<Blockinfo> blocks) {
		this.blocks = blocks;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (blocks == null)
			return;
		Dimension size = this.getSize();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());
		int xct = (int) size.getWidth() / antsize - 1;

		int x = 0;
		int y = 0;
		int row = 0;
		int column = 0;
		Iterator<Blockinfo> en = blocks.iterator();
		while (en.hasNext()) {
			Blockinfo block = en.next();
			Color curcolor = null;
			curcolor = colotable[(block.getThreadindex() - 1)
					% colotable.length];
			g.setColor(curcolor);
			if (block.isFinished()) {
				g.fillArc(x, y, antsize, antsize, 0, 360);
			} else {
				g.drawArc(x, y, antsize, antsize, 0, 360);
			}
			x += antsize;
			column++;
			if (column == xct) {
				row++;
				y += antsize;
				x = 0;
				column = 0;
			}

		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 40);
	}

}
