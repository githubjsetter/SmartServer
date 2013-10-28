package com.smart.client.system.tabheadw;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Category;

/**
 * ���ڵ�root pane�Ĵ�tabҳ��title��
 * 
 * @author user
 * 
 */
public class TabbedTitlePane extends JPanel {
	Category logger = Category.getInstance(TabbedTitlePane.class);

	TRootpane rootpane = null;
	/**
	 * tabҳ���Ƶ�����
	 */
	Vector<Tabinfo> tabnameindexes = new Vector<Tabinfo>();
	int activeindex = 0;
	int lastactiveindex = 0;

	/**
	 * ����Ļ����ʾ����ʼ��ǩҳindex
	 */
	int drawstartindex = 0;

	/**
	 * ����Ļ����ʾ�Ľ�����ǩҳindex .��ʼΪ-1,��ʾû�м����.
	 */
	int drawendindex = -1;

	/**
	 * �̶��߶�47����
	 */
	int fixheight = 47;

	/**
	 * tabҳ��ʼ����λ��.
	 */
	int tabstarty = 14;

	/**
	 * �����仯
	 */
	private boolean tabcountcalced = false;
	/**
	 * �Ƿ��Ѽ���λ��.
	 */
	private boolean tabarranged = false;

	/**
	 * ����
	 */
	BufferedImage imgbackground = null;

	/**
	 * �رհ�ť
	 */
	BufferedImage imgclosebutton = null;

	/**
	 * �Ǽ���ҳ�ϵ�ʮ�ֹرհ�ť
	 */
	BufferedImage imgunactivecrossbutton = null;

	/**
	 * ����ҳ�ϵ�ʮ�ֹرհ�ť
	 */
	BufferedImage imgactivecrossbutton = null;

	/**
	 * �Ǽ���ҳ�����
	 */
	BufferedImage imgunactiveleft = null;

	/**
	 * �Ǽ���,�������������.
	 */
	BufferedImage imgpickleft = null;

	/**
	 * ����ҳ�����
	 */
	BufferedImage imgactiveleft = null;

	/**
	 * ��󻯰�ť
	 */
	BufferedImage imgmaxbutton = null;
	
	/**
	 * ��󻯰�ť,restore
	 */
	BufferedImage imgmaxbutton1 = null;

	/**
	 * ��С����ť
	 */
	BufferedImage imgminbutton = null;

	/**
	 * �Ӻ�ť
	 */
	BufferedImage imgplusbutton = null;
	
	/**
	 * �Ӻ�ť��mosue moveʱ
	 */
	BufferedImage imgpluspickbutton = null;
	
	/**
	 * +��ť����
	 */
	Rectangle plusbuttonrect = null;


	/**
	 * �Ǽ���ҳ���ұ�
	 */
	BufferedImage imgunactiveright = null;

	/**
	 * �Ǽ���,�������������.
	 */
	BufferedImage imgpickright = null;

	/**
	 * ����ҳ���ұ�
	 */
	BufferedImage imgactiveright = null;

	/**
	 * ��ǩҳ��font
	 */
	Font tabfont = new Font("����", Font.PLAIN, 12);

	/**
	 * ��λlogo��font
	 */
	Font companylogofont = new Font("����", Font.PLAIN, 12);

	Color activetextcolor = Color.BLACK;
	Color unactivetextcolor = new Color(64, 64, 64);

	Color activelinecolor = new Color(136, 171, 213);
	Color activefillcolor = Color.white;

	Color unactivelinecolor = new Color(60, 115, 185);
	Color unactivefillcolor = new Color(159, 196, 240);
	Color unpickfillcolor = new Color(188,213,244);
	Color buttonactivecolor = new Color(147, 191, 243);
	Color buttoncloseactivecolor = new Color(215, 72, 72);

	/**
	 * ��λlogo��ɫ
	 */
	Color companylogocolor = new Color(206, 225, 249);

	Vector<Tabcontentinfo> tabcontentinfos = null;

	/**
	 * ���ѡ�����Ϣ
	 */
	SelectInfo selectinfo = new SelectInfo();

	/**
	 * �ڱ�ǩ���ֺ͹ر�ť֮��ľ���.
	 */
	private int crosspaddingwidth = 10;

	private int textminiwdith = 84;

	public TabbedTitlePane() {
		loadImage();
		setSize(new Dimension(800, fixheight));
	}

	public TabbedTitlePane(TRootpane rootpane) {
		super();
		this.rootpane = rootpane;
		loadImage();
		setSize(new Dimension(800, fixheight));
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (!tabcountcalced) {
			calcTabcontentinfo(g2);
			tabcountcalced = true;
			tabarranged = false;
		}

		if (!tabarranged) {
			tabarranged = true;
			arrangeTab();
		}
		paintBackground(g2);
		paintButtons(g2);
		paintTabs(g2);
	}

	private void setStartx() {
		int x = 0;
		for (int i = drawstartindex; i <= drawendindex; i++) {
			tabcontentinfos.elementAt(i).startx = x;
			x += tabcontentinfos.elementAt(i).totalwidth;
			// ��������
			x -= imgactiveleft.getWidth();
		}
	}

	/**
	 * ����tabҳλ��. ������е�tabҳ����������������󳤶�,��ЩҪ����.���㿪ʼindex
	 */
	void arrangeTab() {
		// ���ܳ�.
		int totalwidth = 0;
		for (int i = 0; i < tabcontentinfos.size(); i++) {
			totalwidth += tabcontentinfos.elementAt(i).totalwidth;
			// ��������
			totalwidth -= imgactiveleft.getWidth();
		}

		int tabsmaxwidth = getWidth() - imgminbutton.getWidth()
				- imgmaxbutton.getWidth() - imgclosebutton.getWidth()
				- imgplusbutton.getWidth();

		if (totalwidth <= tabsmaxwidth) {
			drawstartindex = 0;
			drawendindex = tabcontentinfos.size() - 1;
			setStartx();
			return;
		}
		

		// ���һ:���activeindex<=lastactiveindex,����Ҫ��
		if (drawstartindex <= activeindex  && activeindex <= drawendindex
				&& drawendindex >= 0) {
			// drawstartindex��drawendindex����.
			setStartx();
			return;
		}

		/**
		 * ���ѡ����������ʾҳ�����,���ҳ��Ϊ��һ��ҳ.Ҫ�������ҳ
		 */
		int subwidth = 0;
		if (activeindex < drawstartindex || drawendindex < 0) {
			drawstartindex = activeindex;
			subwidth = 0;
			// ����Ϊ���һҳ.
			drawendindex = tabcontentinfos.size() - 1;
			for (int i = drawstartindex; i < tabcontentinfos.size(); i++) {
				if (subwidth + tabcontentinfos.elementAt(i).totalwidth > tabsmaxwidth) {
					drawendindex = i - 1;
					break;
				} else {
					subwidth += tabcontentinfos.elementAt(i).totalwidth;
					subwidth -= imgactiveleft.getWidth();
				}
			}
			setStartx();
			return;
		}

		// �������ڻҳ���ұ�,��ô���ҳ��Ϊ���һҳ,���㿪ʼҳ.
		if (activeindex > drawendindex) {
			drawendindex = activeindex;
			drawstartindex = 0;
			subwidth = 0;
			for (int i = drawendindex; i >= 0; i--) {
				if (subwidth + tabcontentinfos.elementAt(i).totalwidth > tabsmaxwidth) {
					drawstartindex = i + 1;
					break;
				} else {
					subwidth += tabcontentinfos.elementAt(i).totalwidth;
					subwidth -= imgactiveleft.getWidth();
				}
			}

			setStartx();
			return;
		}

		logger.debug("internal error");
		System.err.println("internal error");

		// ��Ҫ���ҵ����㿪ʼ��index

	}

	/**
	 * ����ǩҳ.
	 * 
	 * @param g2
	 */
	void paintTabs(Graphics2D g2) {
		// ����λ��.
		for (int i = drawendindex; i >= drawstartindex; i--) {
			Tabcontentinfo cinfo = tabcontentinfos.elementAt(i);
			paintTab(g2, i, cinfo.startx, false);
		}
		// ���Ż������
		Tabcontentinfo cinfo = tabcontentinfos.elementAt(activeindex);
		paintTab(g2, activeindex, cinfo.startx, true);

		// ���Ӻ�
		Tabcontentinfo lastinfo = tabcontentinfos.elementAt(drawendindex);
		int plusx = lastinfo.startx + lastinfo.totalwidth;
		int plusy = tabstarty + 4;

		plusbuttonrect = new Rectangle(plusx, plusy, imgplusbutton.getWidth(),
				imgplusbutton.getHeight());

		if(selectinfo.selecttype==SELECTTYPE_PLUSBUTTON){
			g2.drawImage(imgpluspickbutton, plusx, plusy, null);
		}else{
			g2.drawImage(imgplusbutton, plusx, plusy, null);
		}

	}

	void paintTab(Graphics2D g2, int index, int startx, boolean active) {
		BufferedImage imgl, imgr, imgcr;
		Color linecolor;
		Color fillcolor;
		Color textcolor;

		if (active) {
			imgl = imgactiveleft;
			imgr = imgactiveright;
			linecolor = activelinecolor;
			fillcolor = activefillcolor;
			textcolor = activetextcolor;
			imgcr = imgactivecrossbutton;
		} else {
			imgl = imgunactiveleft;
			imgr = imgunactiveright;
			linecolor = unactivelinecolor;
			fillcolor = unactivefillcolor;
			textcolor = unactivetextcolor;
			imgcr = imgunactivecrossbutton;
			
			if(selectinfo.selecttype==SELECTTYPE_TAB && index==selectinfo.selectindex){
				imgl = imgpickleft;
				imgr = imgpickright;
				fillcolor = unpickfillcolor;
				
			}
			
		}

		g2.setFont(tabfont);
		FontMetrics fm = g2.getFontMetrics();
		String title = tabnameindexes.elementAt(index).opname;
		int barcontentwidth = fm.charsWidth(title.toCharArray(), 0, title
				.length());
		if (barcontentwidth < textminiwdith) {
			barcontentwidth = textminiwdith;
		}

		barcontentwidth += crosspaddingwidth;
		// ��
		int x = startx;
		int starty = tabstarty;
		g2.drawImage(imgl, x, starty, null);
		x += imgl.getWidth();

		x += barcontentwidth;

		g2.setColor(linecolor);
		g2.drawLine(startx + imgl.getWidth(), starty, x, starty);

		g2.setColor(fillcolor);
		g2.fillRect(startx + imgl.getWidth(), starty + 1, barcontentwidth
				+ imgcr.getWidth(), imgl.getHeight() - 1);

		// ���ر�x
		// �ǲ��Ǳ�ѡ���?
		if (index == selectinfo.selectindex && selectinfo.tabcloseflag) {
			// ��Ϳ�Ϻ��
			g2.setColor(Color.red);
			g2.fillRect(x, starty + 8, imgcr.getWidth(), imgcr.getHeight());
		}
		g2.drawImage(imgcr, x, starty + 8, null);
		x += imgcr.getWidth();

		// �ұ�
		g2.drawImage(imgr, x, starty, null);

		// ����
		g2.setColor(textcolor);
		g2.drawString(title, startx + imgl.getWidth(), starty + fm.getAscent()
				+ 5);

		// ����,�������
		if (active) {
			// ��������׵�
			g2.setColor(Color.white);
			int y = starty + 25;
			int width = 18 * 2 + barcontentwidth + imgcr.getWidth();
			g2.fillRect(startx + 2, y, width - 4, 1);
			g2.fillRect(startx + 1, y + 1, width - 1, 1);

			g2.setColor(Color.black);

		}

	}

	void paintButtons(Graphics2D g2) {
		// ����С���ر�ť
		int x = getWidth() - imgminbutton.getWidth() - imgmaxbutton.getWidth()
				- imgclosebutton.getWidth();

		String companyname = "LOGO@TM";
		g2.setFont(companylogofont);
		g2.setColor(companylogocolor);
		FontMetrics fm = g2.getFontMetrics();
		int cnamelen = fm.charsWidth(companyname.toCharArray(), 0, companyname
				.length());
		g2.drawString(companyname, x - cnamelen - 3, fm.getAscent());

		int y = 1;
		if (selectinfo.selecttype == SELECTTYPE_MINBUTTON) {
			g2.setColor(buttonactivecolor);
			g2
					.fillRect(x, y, imgminbutton.getWidth(), imgminbutton
							.getHeight());
		}
		g2.drawImage(imgminbutton, x, y, null);
		x += imgminbutton.getWidth();
		if (selectinfo.selecttype == SELECTTYPE_MAXBUTTON) {
			g2.setColor(buttonactivecolor);
			g2
					.fillRect(x, y, imgmaxbutton.getWidth(), imgmaxbutton
							.getHeight());
		}
		
		Frame frame=getFrame();
		if((frame.getExtendedState() & Frame.MAXIMIZED_BOTH )!=0){
			g2.drawImage(imgmaxbutton1, x, y, null);
		}else{
			g2.drawImage(imgmaxbutton, x, y, null);
		}
		x += imgmaxbutton.getWidth();
		if (selectinfo.selecttype == SELECTTYPE_CLOSEBUTTON) {
			g2.setColor(buttoncloseactivecolor);
			g2.fillRect(x, y, imgclosebutton.getWidth(), imgclosebutton
					.getHeight());
		}
		g2.drawImage(imgclosebutton, x, y, null);
		x += imgclosebutton.getWidth();

	}

	@Override
	public Dimension getPreferredSize() {
		int w = getWidth();
		if (w < 800) {
			w = 800;
		}
		return new Dimension(w, fixheight);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(800, fixheight);
	}

	void paintBackground(Graphics2D g2) {
		int count = getWidth() / imgbackground.getWidth() + 1;

		int x = 0;
		int i = 0;
		for (i = 0, x = 0; i < count; i++, x += imgbackground.getWidth()) {
			g2.drawImage(imgbackground, x, 0, imgbackground.getWidth(),
					imgbackground.getHeight(), null);
		}

	}

	void loadImage() {

		try {
			imgbackground = loadImage("bar.png");
			imgclosebutton = loadImage("closebutton.png");
			imgunactivecrossbutton = loadImage("cross.png");
			imgactivecrossbutton = loadImage("cross-white.png");
			imgunactiveleft = loadImage("left-1.png");
			imgpickleft = loadImage("left-2.png");
			imgactiveleft = loadImage("left-white.png");
			imgmaxbutton = loadImage("maxbutton.png");
			imgmaxbutton1 = loadImage("maxbutton-1.png");
			imgminbutton = loadImage("minibutton.png");
			imgplusbutton = loadImage("plus.png");
			imgpluspickbutton= loadImage("plus-1.png");
			imgunactiveright = loadImage("right-1.png");
			imgpickright = loadImage("right-2.png");
			imgactiveright = loadImage("right-white.png");
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	BufferedImage loadImage(String filename) {
		InputStream in = this.getClass().getResourceAsStream(filename);
		if (in == null) {
			return null;
		}
		try {
			BufferedImage image = ImageIO.read(in);
			return image;
		} catch (IOException e) {
			logger.error("error", e);
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * ����ÿ����ǩҳ�Ŀ�
	 */
	void calcTabcontentinfo(Graphics2D g2) {
		tabcontentinfos = new Vector<Tabcontentinfo>();
		g2.setFont(tabfont);
		FontMetrics fm = g2.getFontMetrics();
		for (int i = 0; i < tabnameindexes.size(); i++) {
			String title = tabnameindexes.elementAt(i).opname;
			int barcontentwidth = fm.charsWidth(title.toCharArray(), 0, title
					.length());
			if (barcontentwidth < textminiwdith) {
				barcontentwidth = textminiwdith;
			}

			Tabcontentinfo tabcinfo = new Tabcontentinfo();
			tabcinfo.index = i;
			tabcinfo.totalwidth = imgactiveleft.getWidth() + barcontentwidth
					+ crosspaddingwidth + imgactivecrossbutton.getWidth()
					+ imgactiveright.getWidth();
			tabcontentinfos.add(tabcinfo);
		}
	}

	/**
	 * ����ÿ����ǩҳ��С��Ϣ
	 * 
	 * @author user
	 * 
	 */
	class Tabcontentinfo {
		int index = 0;
		int startx = 0;
		int totalwidth = 0;
	}

	public int getActiveindex() {
		return activeindex;
	}

	public void setActiveindex(int activeindex) {
		this.activeindex = activeindex;
		this.tabarranged = false;
		revalidate();
		repaint();
	}

	public void addTabindex(String opid,String opname) {
		Tabinfo tabinfo=new Tabinfo();
		tabinfo.opid=opid;
		tabinfo.opname=opname;
		tabnameindexes.add(tabinfo);
		tabcountcalced = false;
		setActiveindex(tabnameindexes.size() - 1);
		revalidate();
		repaint();
	}

	public void docloseTab(int index) {
		if (index == 0) {
			// �˳�Ӧ��.
			return;
		}
		tabnameindexes.remove(index);
		this.tabcountcalced = false;
		if (activeindex >= tabnameindexes.size()) {
			activeindex = tabnameindexes.size() - 1;
		}
		if (drawendindex >= tabnameindexes.size()) {
			drawendindex = tabnameindexes.size() - 1;
		}
		revalidate();
		repaint();
	}

	void pickObject(MouseEvent me) {
		selectinfo.selecttype = SELECTTYPE_NONE;
		selectinfo.tabcloseflag = false;
		int buttonstartx = getWidth() - imgminbutton.getWidth()
				- imgmaxbutton.getWidth() - imgclosebutton.getWidth();

		int x = me.getX();
		int y = me.getY();
		
		//����ǲ��ǼӺ�?
		if(plusbuttonrect!=null && plusbuttonrect.contains(me.getPoint())){
			selectinfo.selecttype=SELECTTYPE_PLUSBUTTON;
			return;
		}
		
		if (y >= tabstarty && y <= tabstarty + imgactiveleft.getHeight()) {
			// �����ǰ��ڱ�ǩ��
			for (int i = drawstartindex; i <= drawendindex; i++) {
				Tabcontentinfo cinfo = tabcontentinfos.elementAt(i);
				if (x >= cinfo.startx + imgactiveleft.getWidth()
						&& x <= cinfo.startx + cinfo.totalwidth
								- imgactiveleft.getWidth()) {
					selectinfo.selecttype = SELECTTYPE_TAB;
					selectinfo.selectindex = i;
					selectinfo.tabcloseflag = false;
					// �᲻����close��ť��?
					int closex = cinfo.startx + cinfo.totalwidth
							- imgactiveleft.getWidth()
							- imgactivecrossbutton.getWidth();
					int closey = tabstarty + 8;
					if (x >= closex
							&& x <= closex + imgactivecrossbutton.getWidth()
							&& y >= closey
							&& y <= closey + imgactivecrossbutton.getHeight()) {
						// System.out.println("tab index=" + i + " close");
						selectinfo.tabcloseflag = true;
					}
					return;
				}
			}
		} else if (x >= buttonstartx && y >= 1
				&& y <= imgminbutton.getHeight() + 1) {
			// ��С���رհ�ť
			int tmpx = buttonstartx;
			if (x >= tmpx && x <= tmpx + imgminbutton.getWidth()) {
				selectinfo.selecttype = SELECTTYPE_MINBUTTON;
				return;
			}
			tmpx += imgminbutton.getWidth();
			if (x >= tmpx && x <= tmpx + imgmaxbutton.getWidth()) {
				selectinfo.selecttype = SELECTTYPE_MAXBUTTON;
				return;
			}
			tmpx += imgmaxbutton.getWidth();
			if (x >= tmpx && x <= tmpx + imgclosebutton.getWidth()) {
				selectinfo.selecttype = SELECTTYPE_CLOSEBUTTON;
				return;
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		pickObject(e);
		// System.out.println(selectinfo.selecttype);
		repaint();
	}

	static int SELECTTYPE_NONE = 0;
	static int SELECTTYPE_TAB = 1;
	static int SELECTTYPE_MINBUTTON = 2;
	static int SELECTTYPE_MAXBUTTON = 3;
	static int SELECTTYPE_CLOSEBUTTON = 4;
	static int SELECTTYPE_PLUSBUTTON = 5;

	class SelectInfo {
		int selecttype = SELECTTYPE_NONE;
		int selectindex = 0;
		boolean tabcloseflag = false;
	}

/*	public static void test_drawImage() {
		TabbedTitlePane tp = new TabbedTitlePane();
		tp.addTabindex("���Թ���0");
		tp.addTabindex("���Թ���1");

		BufferedImage fullimg = new BufferedImage(800, 47,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = fullimg.getGraphics();
		tp.setActiveindex(1);
		tp.paint(g);
		try {
			ImageIO.write(fullimg, "png", new File("test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tp.setActiveindex(0);
		fullimg = new BufferedImage(800, 47, BufferedImage.TYPE_INT_ARGB);
		g = fullimg.getGraphics();
		tp.paint(g);
		try {
			ImageIO.write(fullimg, "png", new File("test1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tp.addTabindex("���Թ���2");
		tp.addTabindex("���Թ���3");
		tp.addTabindex("���Թ���4");
		tp.addTabindex("���Թ���5");
		tp.addTabindex("���Թ���6");
		tp.addTabindex("���Թ���7");
		tp.addTabindex("���Թ���8");

		tp.setActiveindex(8);
		fullimg = new BufferedImage(800, 47, BufferedImage.TYPE_INT_ARGB);
		g = fullimg.getGraphics();
		tp.paint(g);
		try {
			ImageIO.write(fullimg, "png", new File("test2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tp.setActiveindex(7);
		fullimg = new BufferedImage(800, 47, BufferedImage.TYPE_INT_ARGB);
		g = fullimg.getGraphics();
		tp.paint(g);
		try {
			ImageIO.write(fullimg, "png", new File("test3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (true) {
			System.exit(0);
		}

	}
*/
	public static void main(String[] args) {
		JFrame frm = new JFrame("Test");
		Container cp = frm.getContentPane();

		TabbedTitlePane tp = new TabbedTitlePane();
		//tp.addTabindex("����");
		//tp.addTabindex("���Թ���1");
		//tp.addTabindex("���Թ���2");
		//tp.addTabindex("���Թ���3");

		cp.add(tp);

		frm.pack();
		frm.setVisible(true);
	}

	public void mousePressed(MouseEvent me) {
		pickObject(me);
	}

	public void mouseReleased(MouseEvent convertMouseEvent) {
	}

	/**
	 * ���ط�0��ʾ���пؼ�ѡ��
	 * 
	 * @param convertMouseEvent
	 * @return
	 */
	public int mouseDragged(MouseEvent convertMouseEvent) {
		if (selectinfo.selecttype == SELECTTYPE_NONE) {
			return 0;
		} else {
			return 1;
		}
	}

	public void mouseEntered(MouseEvent convertMouseEvent) {
	}

	public void mouseExited(MouseEvent convertMouseEvent) {
	}

	/**
	 * ����1��ʾ�������.
	 * @param me
	 * @return
	 */
	public int mouseClicked(MouseEvent me) {
		pickObject(me);
		logger.debug("selectinfo.selecttype = " + selectinfo.selecttype);
		if (selectinfo.selecttype == SELECTTYPE_NONE)
			return 0;
		else if (selectinfo.selecttype == SELECTTYPE_TAB) {
			if (!selectinfo.tabcloseflag) {
				// ����
				setActiveindex(selectinfo.selectindex);
				rootpane.onActiveIndex(selectinfo.selectindex);
			} else {
				// �ر����ҳ
				Tabinfo tabinfo=tabnameindexes.elementAt(selectinfo.selectindex);
				rootpane.onCloseIndex(tabinfo.opid,selectinfo.selectindex, activeindex);
				selectinfo.tabcloseflag=false;
			}
		} else if (selectinfo.selecttype == SELECTTYPE_MINBUTTON) {
			// ���ڼ�С��
			Frame frame = getFrame();
			frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
		} else if (selectinfo.selecttype == SELECTTYPE_MAXBUTTON) {
			// ���ڼ���
			Frame frame = getFrame();
			int state = frame.getExtendedState();
			if ((state & Frame.MAXIMIZED_BOTH) != 0) {
				if ((state & Frame.ICONIFIED) != 0) {
					frame.setExtendedState(state & ~Frame.ICONIFIED);
				} else {
					frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
				}
			} else {
				frame.setExtendedState(frame.getExtendedState()
						| Frame.MAXIMIZED_BOTH);
			}

		} else if (selectinfo.selecttype == SELECTTYPE_CLOSEBUTTON) {
			// �ر�
			Frame frame = getFrame();
			frame.dispatchEvent(new WindowEvent(frame,
					WindowEvent.WINDOW_CLOSING));
		} else if(selectinfo.selecttype == SELECTTYPE_PLUSBUTTON){
			//���˼Ӻ�,�����˵�.
			rootpane.onPopupmenu(me.getPoint());
		}
		revalidate();
		repaint();
		return 1;
	}

	private Frame getFrame() {
		Window w = rootpane.getWindow();
		return (Frame) w;
	}

	class Tabinfo{
		String opid;
		String opname;
	}
}
