package com.smart.platform.gui.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.smart.platform.gui.control.CTableHeaderRender.ContentPane;
import com.smart.platform.image.CIcon;
import com.smart.platform.image.IconFactory;

public class CTableMultiHeaderRender implements TableCellRenderer {

	Drawpanel drawpane = new Drawpanel();

	public CTableMultiHeaderRender() {
	}

	Font focusfont = null;

	public Component getTableCellRendererComponent(JTable jtable, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		CTable table = (CTable) jtable;
		drawpane.drawrect = table.getTableHeader().getHeaderRect(column);
		drawpane.text = value.toString();
		return drawpane;
	}

	class Drawpanel extends JLabel {
		Rectangle drawrect;
		String text = "";

		Drawpanel() {
			super("原来");
		}

		@Override
		public void paint(Graphics g) {
			Color oldc = g.getColor();
			g.setColor(Color.black);
/*			g.drawLine((int) drawrect.getWidth() - 1, 0, (int) drawrect
					.getWidth() - 1, (int) drawrect.getHeight());
*/
			FontMetrics fm = g.getFontMetrics();
			if (text.indexOf(":") < 0) {
				// 画中部吧
				Rectangle2D rect = fm.getStringBounds(text, g);
				int tx = (int) ((drawrect.getWidth() - rect.getWidth()) / 2.0);
				int ty = (int) ((drawrect.getHeight() + rect.getHeight()) / 2.0) + 2;

				g.drawString(text, tx, ty);
			} else {
				int p = text.indexOf(":");
				String prefix = text.substring(0, p);
				text = text.substring(p + 1);

				// 画横线
				int halfy = (int) (drawrect.getHeight() / 2.0);
				//g.drawLine(0, halfy, (int)drawrect.getWidth(), halfy);
				// text画下部
				Rectangle2D rect = fm.getStringBounds(text, g);
				int tx = (int) ((drawrect.getWidth() - rect.getWidth()) / 2.0);
				int ty = (int) ((drawrect.getHeight()/2 + rect.getHeight()) / 2.0) - 2;
				
				ty += halfy;
				g.drawString(text, tx, ty);
				

			}
			g.setColor(oldc);
		}

	}

	public void freeMemory() {
	}
}
