package com.inca.np.anyprint.impl;

import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.anyprint.CellpropDlg;
import com.inca.np.gui.control.CFrame;

/**
 * 辅助进行鼠标，键盘处理。 用于设计parts的Cell
 * 
 * @author Administrator
 * 
 */
public class PartsMouseHandle {
	/**
	 * 拖动
	 */
	static final String DRAG = "drag";

	// 表格列的次序.
	static final String DRAG_MOVECOLUMN = "drag_movecolumn";
	static final String DRAG_WEST = "dragwest";
	static final String DRAG_NORTH = "dragnorth";
	static final String DRAG_SOUTH = "dragsouth";
	static final String DRAG_EAST = "drageast";
	static final String DRAG_PART = "dragpart";
	Parts parts;
	Partbase activepart = null;
	int mempartheight = 0;

	/**
	 * 进入mosue拖动某元件状态
	 */
	boolean dragging = false;
	String dragmode = "";
	/**
	 * mouse move 使active
	 */
	Cellbase activecell = null;
	DrawableLine activeline = null;
	Rectangle activecellrect = new Rectangle();
	Point mouseclickpoint = null;

	/**
	 * mouse按下,成为selected
	 */
	Cellbase selectedcell = null;
	Partbase selectedpart = null;
	DrawableLine selectedline = null;
	/**
	 * 现在活动的part在整个pane的y.
	 */
	int partoffsety;

	/**
	 * 捕获drag的offset
	 */
	int dragoffset = 3;

	public PartsMouseHandle(Parts parts) {
		this.parts = parts;
	}

	public void onMousemove(Point p) {
		Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getActiveWindow();
		if (!(w instanceof CFrame))
			return;
		CFrame frm = (CFrame) KeyboardFocusManager
				.getCurrentKeyboardFocusManager().getActiveWindow();
		int y = 0;
		int y1 = 0, y2;
		Partbase part = null;
		for (int i = 0; i < parts.getPartcount(); i++) {
			y2 = y1 + parts.getPart(i).getHeight();
			if (p.y >= y1 && p.y < y2) {
				part = parts.getPart(i);
				y = p.y - y1;
				selectedpart = part;
				break;
			}
			y1 = y2 + 1;
		}
		// System.out.println("selectedpart="+selectedpart);

		// System.out.println(part);
		if (part == null || part.getPlantype().indexOf("表格") >= 0
				&& part == parts.getTablehead()) {
			// 表头不调。
			frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			selectedpart = part;
			dragmode = "";
			return;
		}

		// 是否选中横竖线?

		Point newp = new Point(p.x, y);
		int capturemode = 0;
		if (selectedline != null) {
			capturemode = selectedline.isMouseenter(newp);
		}
		if (capturemode > 0) {
			setCursor(selectedline);
			return;
		}

		// 看是part中的哪个元件
		boolean foundselected = false;
		Enumeration<Cellbase> en = part.getCells().elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			Rectangle rect = cell.getRect();
			if (p.x >= rect.x && p.x <= rect.x + rect.width && y >= rect.y
					&& y <= rect.y + rect.height) {
				selectedpart = part;
				selectedcell = cell;
				foundselected = true;
				// System.out.println("selectedcell " + selectedcell + "
				// active");
				if (selectedcell == activecell) {
					Rectangle cellr = cell.getRect();
					activecell = cell;
					activecellrect.x = cellr.x;
					activecellrect.y = cellr.y;
					activecellrect.width = cellr.width;
					activecellrect.height = cellr.height;
					partoffsety = y1;
					// 分辨靠进哪个边，设置dragmodel
					if (p.x >= rect.x && p.x <= rect.x + dragoffset) {
						dragmode = DRAG_WEST;
						if (activepart == parts.getBody()
								&& parts.getPlantype().indexOf("表格") >= 0
								&& activecell instanceof Columncell) {
							//dragmode=DRAG_MOVECOLUMN;
							// 如果是数据列，只能调第一列
							Columncell firstcell = parts.getBody()
									.getFirstcolumncell();
							if (firstcell != null && firstcell != activecell) {
								dragmode = DRAG_WEST;
							}
						}
					} else if (p.x <= rect.x + rect.width
							&& p.x >= rect.x + rect.width - dragoffset) {
						dragmode = DRAG_EAST;
					} else if (y >= rect.y && y <= rect.y + dragoffset) {
						dragmode = DRAG_NORTH;
					} else if (y >= rect.y + rect.height - dragoffset
							&& y <= rect.y + rect.height) {
						dragmode = DRAG_SOUTH;
					} else {
						dragmode = DRAG;
					}
					break;
				}
			}
		}
		// System.out.println("foundselected="+foundselected);
		if (!foundselected) {
			dragmode = "";
			if (selectedcell != null) {
				selectedcell = null;
			}
			// 是不是要调整part的大小呢？
			y = 0;
			for (int i = 0; i < parts.getPartcount(); i++) {
				y = y + parts.getPart(i).getHeight();
				if (p.y >= y - dragoffset && p.y <= y + dragoffset) {
					// 说明要调整part的大小了。
					frm.setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					dragmode = DRAG_PART;
					selectedpart = parts.getPart(i);
					return;
				}
			}

			frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			if (dragmode.equals(DRAG_WEST) || dragmode.equals(DRAG_EAST)) {
				frm.setCursor(Cursor
						.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (dragmode.equals(DRAG_NORTH)
					|| dragmode.equals(DRAG_SOUTH)) {

				frm.setCursor(Cursor
						.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if(dragmode.equals(DRAG_MOVECOLUMN)){
				frm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else {
				frm.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		}
		// System.out.println("drag mode=" + dragmode);
	}

	public void onDrag(Point p) {
		if (selectedline != null) {
			// 调整线.
			dragging = true;
			if (selectedline.linetype.equals(DrawableLine.LINETYPE_HORIZONTAL)) {
				int offsetx = p.x - mouseclickpoint.x;
				if (selectedline.capturemode == 1) {
					selectedline.p1.x += offsetx;
				} else if (selectedline.capturemode == 2) {
					selectedline.p2.x += offsetx;
				} else {
					selectedline.p1.x += offsetx;
					selectedline.p2.x += offsetx;
					int offsety = p.y - mouseclickpoint.y;
					selectedline.p1.y += offsety;
					selectedline.p2.y += offsety;
				}
			} else if (selectedline.linetype
					.equals(DrawableLine.LINETYPE_VERTICAL)) {
				int offsety = p.y - mouseclickpoint.y;
				if (selectedline.capturemode == 1) {
					selectedline.p1.y += offsety;
				} else if (selectedline.capturemode == 2) {
					selectedline.p2.y += offsety;
				} else {
					selectedline.p1.y += offsety;
					selectedline.p2.y += offsety;
					int offsetx = p.x - mouseclickpoint.x;
					selectedline.p1.x += offsetx;
					selectedline.p2.x += offsetx;
				}
			}
			mouseclickpoint = p;
			return;
		}

		if (dragmode.equals(DRAG_PART)) {
			int newh = mempartheight + (p.y - mouseclickpoint.y);
			if (newh < 0)
				newh = 0;
			activepart.setHeight(newh);
			parts.setDatadirty(true);
			return;
		}

		// 对当前的元件进行调整
		if (activecell == null)
			return;
		// System.out.println("dragmode="+dragmode);
		if (dragmode.equals(DRAG_MOVECOLUMN)) {
			CFrame frm = (CFrame) KeyboardFocusManager
			.getCurrentKeyboardFocusManager().getActiveWindow();
			frm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			//System.out.println("move columnning");
			//移动列,要判断现在在哪列?
			Vector<Cellbase> cells=parts.getBody().getCells();
			if(cells.size()==0)return;
			int x=cells.elementAt(0).getRect().x;
			int activeindex=-1;
			for(int i=0;i<cells.size();i++){
				if(activecell == cells.elementAt(i)){
					activeindex=i;
					break;
				}
			}
			System.out.print("activeindex="+activeindex+"  ");
			if(activeindex<0)return;
			
			int newselectedindex=-1;
			for(int i=0;i<cells.size();i++){
				x = cells.get(i).getRect().x;
				if(p.x>x && p.x<x+cells.get(i).getRect().width){
					newselectedindex=i;
					break;
				}
			}
			System.out.println("newselectedindex="+newselectedindex);
			if(newselectedindex<0 || activeindex == newselectedindex)return;
			
			//将 activeindex 列插到 newselectedindex
			if(newselectedindex < activeindex){
				cells.elementAt(activeindex).getRect().x=
					cells.elementAt(newselectedindex).getRect().x;
				cells.insertElementAt(cells.elementAt(activeindex), newselectedindex);
				cells.remove(activeindex+1);
			}else{
				cells.elementAt(newselectedindex).getRect().x=
					cells.elementAt(activeindex).getRect().x;
				cells.insertElementAt(cells.elementAt(activeindex), newselectedindex+1);
				cells.remove(activeindex);
			}
			parts.alignTable();
			
		} else if (dragmode.equals(DRAG)) {
			// 如果是表格方式,可能需要调整左边单元格的宽度
			// System.out.println("activepart="+activepart);
			if (activepart == parts.getBody()
					&& parts.getPlantype().indexOf("表格") >= 0) {
				if (activecell instanceof Columncell) {
					Columncell firstcell = parts.getBody().getFirstcolumncell();
					if (activecell != firstcell) {
						// 调左边的宽
						Cellbase leftcell = parts.getBody().getLeftcell(
								activecell);
						Rectangle cellr = leftcell.getRect();
						cellr.width += p.x - mouseclickpoint.x;
						parts.onCellsizechanged();
						mouseclickpoint = p;
						return;
					}
				}
			} else if (activepart == parts.getTablehead()) {
				return;

			}

			Rectangle cellr = activecell.getRect();
			cellr.x = activecellrect.x + (p.x - mouseclickpoint.x);
			cellr.x = cellr.x < 0 ? 0 : cellr.x;
			cellr.y = activecellrect.y + (p.y - mouseclickpoint.y);
			if (cellr.y + cellr.height > activepart.getHeight()) {
				cellr.y = activepart.getHeight() - cellr.height;
			}
			cellr.y = cellr.y <= 0 ? 0 : cellr.y;
			if (activepart == parts.getBody()
					&& parts.getPlantype().indexOf("表格") >= 0
					&& activecell instanceof Columncell) {
				parts.onCellsizechanged();
			}

		} else if (dragmode.equals(DRAG_WEST)) {
			// 在表格状态下，只有第一列才能调左边位置
			if (activepart == parts.getBody()
					&& parts.getPlantype().indexOf("表格") >= 0
					&& activecell instanceof Columncell) {
				// 如果是数据列，只能调第一列
				Columncell firstcell = parts.getBody().getFirstcolumncell();
				if (firstcell != null && firstcell != activecell) {
					return;
				}
			}

			boolean adjustwidth = true;
			if (activepart == parts.getBody()
					&& parts.getPlantype().indexOf("表格") >= 0) {
				Columncell firstcell = parts.getBody().getFirstcolumncell();
				if (activecell == firstcell) {
					adjustwidth = false;
				}
			}
			Rectangle cellr = activecell.getRect();
			int rightx = cellr.x + cellr.width;
			cellr.x = activecellrect.x + (p.x - mouseclickpoint.x);
			cellr.x = cellr.x < 0 ? 0 : cellr.x;
			if (adjustwidth)
				cellr.width = rightx - cellr.x;
			parts.onCellsizechanged();

		} else if (dragmode.equals(DRAG_EAST)) {
			Rectangle cellr = activecell.getRect();
			int rightx = activecellrect.x + activecellrect.width;
			rightx += (p.x - mouseclickpoint.x);
			cellr.width = rightx - cellr.x;
			parts.onCellsizechanged();

		} else if (dragmode.equals(DRAG_NORTH)) {
			Rectangle cellr = activecell.getRect();
			int bottomy = cellr.y + cellr.height;
			cellr.y = activecellrect.y + (p.y - mouseclickpoint.y);
			cellr.y = cellr.y < 0 ? 0 : cellr.y;
			cellr.height = bottomy - cellr.y;

		} else if (dragmode.equals(DRAG_SOUTH)) {
			Rectangle cellr = activecell.getRect();
			int bottomy = activecellrect.y + activecellrect.height;
			bottomy += (p.y - mouseclickpoint.y);
			bottomy = bottomy > activepart.getHeight() ? activepart.getHeight()
					: bottomy;
			cellr.height = bottomy - cellr.y;

		}
	}

	public void onMousePressed(Point p) {
		int y1 = 0, y2;
		Partbase part = null;
		int y = 0;
		for (int i = 0; i < parts.getPartcount(); i++) {
			y2 = y1 + parts.getPart(i).getHeight();
			if (p.y >= y1 && p.y < y2) {
				part = parts.getPart(i);
				y = p.y - y1;
				break;
			}
			y1 = y2 + 1;
		}

		if (selectedpart != null) {
			Point newp = new Point(p.x, y);
			if (selectedline != null) {
				if (selectedline.isMouseenter(newp) > 0) {
					dragging = true;
					mouseclickpoint = p;
					activecell = null;
					selectedcell = null;
					return;
				}
			}
			Enumeration<DrawableLine> en1 = selectedpart.getLines().elements();
			while (en1.hasMoreElements()) {
				DrawableLine line = en1.nextElement();
				int capturemode = line.isMouseenter(newp);
				// System.out.println("capturemode="+capturemode);
				if (capturemode > 0) {
					selectedpart = activepart = part;
					selectedcell = null;
					activecell = null;
					activeline = line;
					dragging = true;
					mouseclickpoint = p;
					selectedline = activeline;
					setCursor(selectedline);
					return;
				}
			}
		}

		selectedline = null;

		if (selectedcell == null) {
			if (activecell != null) {
				activecell.setActive(false);
				activecell = null;
			}

			if (dragmode.equals(DRAG_PART)) {
				dragging = true;
				mouseclickpoint = p;
				activepart = selectedpart;
				mempartheight = activepart.getHeight();

				if (activecell != null) {
					activecell.setActive(false);
					activecell = null;
				}

				return;
			}

			return;
		}

		if (activecell != null && activecell != selectedcell) {
			// 点中了另一个cell
			activecell.setActive(false);
		}
		activecell = selectedcell;
		activecell.setActive(true);
		activepart = selectedpart;

		if (activecell != null && !dragging) {
			dragging = true;
			Rectangle rect = activecellrect;
			if (p.x >= rect.x && p.x <= rect.x + dragoffset) {
				dragmode = DRAG_WEST;

				if (activepart == parts.getBody()
						&& parts.getPlantype().indexOf("表格") >= 0
						&& activecell instanceof Columncell) {
					Columncell firstcell = parts.getBody().getFirstcolumncell();
					if (firstcell != null && firstcell != activecell) {
						dragmode = DRAG;
					}

				}
			} else if (p.x <= rect.x + rect.width
					&& p.x >= rect.x + rect.width - dragoffset) {
				dragmode = DRAG_EAST;
			} else if (y >= rect.y && y <= rect.y + dragoffset) {
				dragmode = DRAG_NORTH;
			} else if (y >= rect.y + rect.height - dragoffset
					&& y <= rect.y + rect.height) {
				dragmode = DRAG_SOUTH;
			} else {
				if (activepart == parts.getBody()
						&& parts.getPlantype().indexOf("表格") >= 0
						&& activecell instanceof Columncell) {
					dragmode = DRAG_MOVECOLUMN;
				} else {

					dragmode = DRAG;
				}
			}
			 System.out.println("mouse press activecell=" + activecell
			 + ",dragmode=" + dragmode);

			mouseclickpoint = p;
			activecellrect.x = activecell.getRect().x;
			activecellrect.y = activecell.getRect().y;
			activecellrect.width = activecell.getRect().width;
			activecellrect.height = activecell.getRect().height;
		}
	}

	public void onMouseReleased(Point point) {
		dragging = false;
		if (activepart != null && dragmode.equals(DRAG)
				&& activepart == parts.getBody()
				&& parts.getPlantype().indexOf("表格") >= 0) {
			// 移动了数据列,看x坐标是否移动到别的cell上了
			int cellindex = parts.getBody().getCellindex(activecell);
			Enumeration<Cellbase> en = parts.getBody().getCells().elements();
			int insertindex = -1;
			for (int i = 0; en.hasMoreElements(); i++) {
				Cellbase tmpc = en.nextElement();
				if (tmpc == activecell)
					continue;
				if (activecell.getRect().x < tmpc.getRect().x
						+ tmpc.getRect().width / 2) {
					// 插入到前面
					insertindex = i;
					break;
				}
			}
			if (insertindex >= 0 && insertindex != cellindex) {
				// 插在前面
				if (insertindex == 0) {
					// 如果放在最前面，要设定x
					activecell.getRect().x = parts.getBody().getCells()
							.elementAt(insertindex).getRect().x;
				}
				parts.getBody().getCells().remove(activecell);
				if (insertindex < cellindex) {
					parts.getBody().getCells().insertElementAt(activecell,
							insertindex);
				} else {
					parts.getBody().getCells().insertElementAt(activecell,
							insertindex - 1);
				}
				parts.onCellsizechanged();
			}
		}
	}

	public boolean needRepaint() {
		return activecell != null;
	}

	public Partbase getActivepart() {
		return activepart;
	}

	public Cellbase getActivecell() {
		return activecell;
	}

	public void delActivecell() {
		if (selectedline != null) {
			activepart.getLines().remove(selectedline);
			selectedline = activeline = null;
			return;
		}
		if (activecell == null) {
			return;
		}
		activepart.getCells().remove(activecell);
	}

	public void moveLeft() {
		if (activecell == null)
			return;
		if (dragmode.equals("")) {
			dragmode = DRAG;
		}
		if (dragmode.equals(DRAG)) {
			activecell.getRect().x--;
			if (activecell.getRect().x < 0)
				activecell.getRect().x = 0;
		} else if (dragmode.equals(DRAG_EAST)) {
			activecell.getRect().width--;
		} else if (dragmode.equals(DRAG_WEST)) {
			activecell.getRect().x--;
			if (activecell.getRect().x < 0) {
				activecell.getRect().x = 0;
			} else {
				activecell.getRect().width++;
			}
		}
	}

	public void moveRight() {
		if (activecell == null)
			return;
		if (dragmode.equals("")) {
			dragmode = DRAG;
		}
		if (dragmode.equals(DRAG)) {
			activecell.getRect().x++;
		} else if (dragmode.equals(DRAG_EAST)) {
			activecell.getRect().width++;
		} else if (dragmode.equals(DRAG_WEST)) {
			activecell.getRect().x++;
			activecell.getRect().width--;
		}
	}

	public void moveUp() {
		if (activecell == null)
			return;
		if (dragmode.equals("")) {
			dragmode = DRAG;
		}
		if (dragmode.equals(DRAG)) {
			activecell.getRect().y--;
			if (activecell.getRect().y < 0)
				activecell.getRect().y = 0;
		} else if (dragmode.equals(DRAG_SOUTH)) {
			activecell.getRect().height--;
		} else if (dragmode.equals(DRAG_NORTH)) {
			activecell.getRect().y--;
			activecell.getRect().height++;
		}
	}

	public void moveDown() {
		if (activecell == null)
			return;
		if (dragmode.equals("")) {
			dragmode = DRAG;
		}
		if (dragmode.equals(DRAG)) {
			activecell.getRect().y++;
		} else if (dragmode.equals(DRAG_SOUTH)) {
			activecell.getRect().height++;
		} else if (dragmode.equals(DRAG_NORTH)) {
			activecell.getRect().y++;
			activecell.getRect().height--;
		}
	}

	void setCursor(DrawableLine line) {
		CFrame frm = (CFrame) KeyboardFocusManager
				.getCurrentKeyboardFocusManager().getActiveWindow();
		int capturemode = line.capturemode;
		if (capturemode > 0) {
			if (capturemode == 1 || capturemode == 2) {
				if (selectedline.linetype
						.equals(DrawableLine.LINETYPE_HORIZONTAL)) {
					dragmode = DRAG_WEST;
					frm.setCursor(Cursor
							.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				} else if (selectedline.linetype
						.equals(DrawableLine.LINETYPE_VERTICAL)) {
					dragmode = DRAG_NORTH;
					frm.setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				} else {

				}
			} else {
				dragmode = DRAG;
				frm.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			return;

		} else {
			dragmode = "";
			frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}
}
