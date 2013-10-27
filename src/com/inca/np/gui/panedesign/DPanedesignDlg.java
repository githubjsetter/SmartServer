package com.inca.np.gui.panedesign;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.inca.np.demo.ste.Pub_goods_frame;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CFormlayout;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steform;
import com.inca.np.util.DefaultNPParam;

/**
 * 将一个Dpanel传给这个dlg. dlg显示Dpanel,并可以移动cpanel上的component位置.
 * 
 */
public class DPanedesignDlg extends CDialog {
	DPanel dpane = null;

	public DPanedesignDlg(Frame owner, DPanel dpane) throws HeadlessException {
		super(owner, "窗口设计", true);
		this.dpane = dpane;
		init();
		setHotkey();
	}

	public DPanedesignDlg(Dialog owner, DPanel dpane) throws HeadlessException {
		super(owner, "窗口设计", true);
		this.dpane = dpane;
		init();
		setHotkey();
	}

	void init() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel tb = createToolbar();
		cp.add(tb, BorderLayout.NORTH);

		designpane = new DPanedesignPane(dpane);
		JScrollPane jsp = new JScrollPane(designpane);
		cp.add(jsp, BorderLayout.CENTER);
		
		cp.add(createBottom(),BorderLayout.SOUTH);
		
	}
	
	JPanel createBottom(){
		JPanel jp=new JPanel();
		JButton btn;
		btn=new JButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jp.add(btn);

		btn=new JButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		
		return jp;
}

	DPanedesignPane designpane;

	/**
	 * 生成上部的工具条.
	 * 
	 * @return
	 */
	JPanel createToolbar() {
		JPanel tb = new JPanel();
		CFormlayout layout = new CFormlayout(1, 1);
		tb.setLayout(layout);

		JButton btn = null;
		btn = new JButton("加分组框");
		btn.setActionCommand("加分组框");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		btn = new JButton("全选");
		btn.setActionCommand("全选");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		btn = new JButton("横向上边对齐");
		btn.setActionCommand("横向上边对齐");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		btn = new JButton("横向下边对齐");
		btn.setActionCommand("横向下边对齐");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		btn = new JButton("垂直左对齐");
		btn.setActionCommand("垂直左对齐");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		
		btn = new JButton("垂直右对齐");
		btn.setActionCommand("垂直右对齐");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);


		btn = new JButton("设置同高");
		btn.setActionCommand("设置同高");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		btn = new JButton("设置同宽");
		btn.setActionCommand("设置同宽");
		btn.addActionListener(this);
		tb.add(btn);
		layout.addLayoutComponent(btn, null);

		
		return tb;
	}

	/**
	 * 窗口本身大小.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		dim.height-=20;
		
		return dim;
	}

	boolean ok=false;
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("横向上边对齐")) {
			// 水平上对齐
			designpane.alignHorizontaltop();
		} else if (e.getActionCommand().equals("横向下边对齐")) {
			designpane.alignHorizontalbottom();
		} else if (e.getActionCommand().equals("垂直左对齐")) {
			// 垂直对齐
			designpane.alignVerticalleft();
		} else if (e.getActionCommand().equals("垂直右对齐")) {
			designpane.alignVerticalright();
		} else if (e.getActionCommand().equals("设置同高")) {
			designpane.sameHeight();
		} else if (e.getActionCommand().equals("设置同宽")) {
			designpane.sameWidth();
		} else if (e.getActionCommand().equals("全选")) {
			// 全选
			designpane.selectAll();
		}else if(e.getActionCommand().equals("ok")){
			ok=true;
			dispose();
		}else if(e.getActionCommand().equals("cancel")){
			ok=false;
			dispose();
		}else if(e.getActionCommand().equals("加分组框")){
			addTitleborder();
		}
	}

	public boolean isOk(){
		return ok;
	}
	
	void setHotkey(){
		Container cp=getContentPane();
		InputMap im =((JComponent)cp).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am=((JComponent)cp).getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,false), "moveright");
		am.put("moveright",new KeyMoveAction("moveright"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,false), "moveleft");
		am.put("moveleft",new KeyMoveAction("moveleft"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0,false), "moveup");
		am.put("moveup",new KeyMoveAction("moveup"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0,false), "movedown");
		am.put("movedown",new KeyMoveAction("movedown"));

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,Event.SHIFT_MASK,false), "moveright1");
		am.put("moveright1",new KeyMoveAction("moveright1"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,Event.SHIFT_MASK,false), "moveleft1");
		am.put("moveleft1",new KeyMoveAction("moveleft1"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,Event.SHIFT_MASK,false), "moveup1");
		am.put("moveup1",new KeyMoveAction("moveup1"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,Event.SHIFT_MASK,false), "movedown1");
		am.put("movedown1",new KeyMoveAction("movedown1"));

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK,false), "selectall");
		am.put("selectall",new KeyMoveAction("selectall"));

	}
	
	class KeyMoveAction extends AbstractAction{

		public KeyMoveAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			if(cmd.equals("moveright")){
				moveRight();
			}else if(cmd.equals("moveleft")){
				moveLeft();
			}else if(cmd.equals("moveup")){
				moveUp();
			}else if(cmd.equals("movedown")){
				moveDown();
			}else if(cmd.equals("moveright1")){
				moveRight1();
			}else if(cmd.equals("moveleft1")){
				moveLeft1();
			}else if(cmd.equals("moveup1")){
				moveUp1();
			}else if(cmd.equals("movedown1")){
				moveDown1();
			}else if(cmd.equals("selectall")){
				designpane.selectAll();
			}
		}
		
	}
	
	void moveLeft(){
		designpane.batchMoveleft();
	}
	void moveRight(){
		designpane.batchMoveright();
	}
	void moveUp(){
		designpane.batchMoveup();
	}
	void moveDown(){
		designpane.batchMovedown();
	}
	void moveLeft1(){
		designpane.batchMoveleft1();
	}
	void moveRight1(){
		designpane.batchMoveright1();
	}
	void moveUp1(){
		designpane.batchMoveup1();
	}
	void moveDown1(){
		designpane.batchMovedown1();
	}

	void addTitleborder(){
		Titleborderpane titleborderpane=new Titleborderpane("分组");
		designpane.addTitleborder(titleborderpane);
		
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Pub_goods_frame frm=new Pub_goods_frame();
		frm.pack();
		CSteModel ste=frm.getCreatedStemodel();
		Steform steform = ste.getSteform();

		class Democ extends DPanel {
		}

		Democ democ = new Democ();
		democ.addPanel(steform);

		DPanedesignDlg dlg = new DPanedesignDlg((Frame) null, democ);
		dlg.pack();
		dlg.setVisible(true);
	}

}
