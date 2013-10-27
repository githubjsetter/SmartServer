package com.inca.np.gui.control;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.inca.np.gui.control.CQueryDialog.Cboplistener;
import com.inca.np.gui.control.CQueryDialog.HovKeyAction;
import com.inca.np.gui.control.CQueryDialog.HovKeyAction1;
import com.inca.np.gui.control.CQueryDialog.QueryActionListener;
import com.inca.np.gui.control.CQueryDialog.Updownhandler;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class CQueryDialogbackup  extends CDialog {
	final String ACTION_QUERY = "确定";
	final String ACTION_CLEAR = "清除";
	final String ACTION_CANCEL = "取消";

	protected JComponent[] opcontrols = null;
	protected JComponent[] textcontrols = null;
	protected JComponent[] textcontrols1 = null;
	private JButton btnok;
	private COtherquerycontrol otherquerycontrol;
	/**
	 * 简单模式 simplemode=true 不显示操作符控件,和范围的第二个编辑框
	 */
	private boolean simplemode = false;

	public boolean isSimplemode() {
		return simplemode;
	}

	public void setSimplemode(boolean simplemode) {
		this.simplemode = simplemode;
	}

	public CQueryDialogbackup(Frame owner, String title,
			COtherquerycontrol otherquerycontrol) throws HeadlessException {
		super(owner, title, true);
		this.otherquerycontrol = otherquerycontrol;
		Container c = getContentPane();
		if (c instanceof JPanel) {
			JPanel panel = (JPanel) c;
			KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
					false);
			panel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					vkenter, ACTION_QUERY);
			panel.getActionMap().put(ACTION_QUERY,
					new QueryActionListener(ACTION_QUERY));

			KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			panel.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					vkesc, ACTION_CANCEL);
			panel.getActionMap().put(ACTION_CANCEL,
					new QueryActionListener(ACTION_CANCEL));

		}

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	protected Querycond querycond = null;

	public void initControl(Querycond querycond) {
		this.querycond = querycond;

		Container cp = this.getContentPane();
		BoxLayout boxLayout = new BoxLayout(cp, BoxLayout.Y_AXIS);
		cp.setLayout(boxLayout);

		if (otherquerycontrol != null) {
			JPanel otherquerypanel = otherquerycontrol.getOtherquerypanel();
			if (otherquerypanel != null) {
				cp.add(otherquerypanel);
			}
		}

		JPanel querypanel = buildQuerypanel(querycond);
		KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		JComponent jcp=(JComponent)getContentPane();
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkup, "priorcond");
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkdown, "nextcond");
		jcp.getActionMap().put("priorcond", new Updownhandler("priorcond"));
		jcp.getActionMap().put("nextcond", new Updownhandler("nextcond"));
		
		JScrollPane scrollpane = new JScrollPane(querypanel);
		scrollpane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(vkup, "nouse");
		scrollpane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(vkdown, "nouse");

		cp.add(new JLabel(" "));
		cp.add(scrollpane);
		JPanel bottompane = buildBottompane();
		cp.add(bottompane);

		localScreenCenter();

	}

	protected String getQuerybuttontext() {
		return "查询(回车)";
	}

	protected JPanel buildBottompane() {
		JPanel panel = new JPanel();
		btnok = new JButton(getQuerybuttontext());
		btnok.setActionCommand(ACTION_QUERY);

		KeyStroke keyenter = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		btnok.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(keyenter,
				ACTION_QUERY);
		btnok.getActionMap().put(ACTION_QUERY,
				new QueryActionListener(ACTION_QUERY));
		btnok.addActionListener(this);

		JButton btnclear = new JButton("清除C");
		btnclear.setMnemonic('c');
		btnclear.setActionCommand(ACTION_CLEAR);
		btnclear.addActionListener(this);

		JButton btncancel = new JButton("取消(ESC)");
		btncancel.setActionCommand(ACTION_CANCEL);
		KeyStroke keyesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		btncancel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(keyesc,
				ACTION_CANCEL);
		btncancel.getActionMap().put(ACTION_CANCEL,
				new QueryActionListener(ACTION_CANCEL));
		btncancel.addActionListener(this);

		panel.add(btnok);
		panel.add(btnclear);
		panel.add(btncancel);

		return panel;

	}

	protected JPanel buildQuerypanel(Querycond querycond) {
		opcontrols = new JComponent[querycond.size()];
		textcontrols = new JComponent[querycond.size()];
		textcontrols1 = new JComponent[querycond.size()];

		JPanel querypanel = new JPanel();
		CFormlayout formlayout = new CFormlayout(3, 5);
		querypanel.setLayout(formlayout);

		int i = 0;
		Enumeration<Querycondline> en = querycond.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			String title = condline.getTitle();
			String op = condline.getOp();
			String[] ops = op.split(",");

			JLabel lb = null;
			if (!simplemode) {
				querypanel.add(condline.getCbUse());
			} else {
				condline.getCbUse().setSelected(true);
				lb = new JLabel(condline.getTitle());
				querypanel.add(lb);
				lb.setPreferredSize(new Dimension(110, 27));
			}
			condline.getCbUse().setPreferredSize(new Dimension(120, 27));

			/*
			 * JLabel lb = new JLabel(title); lb.setPreferredSize(new
			 * Dimension(80, 27)); querypanel.add(lb);
			 */
			JComboBox cb = new JComboBox(ops);
			cb.addItemListener(new Cboplistener());
			Dimension preferredSize = cb.getPreferredSize();
			preferredSize.setSize(64, 27.0);
			cb.setPreferredSize(preferredSize);
			if (!simplemode) {
				querypanel.add(cb);
			}
			opcontrols[i] = cb;

			// 一定要调用placeComponent
			condline.placeComponent(querypanel, formlayout, simplemode);

			JComponent text = condline.getCondEditcomp();
			text.setEnabled(true);
			preferredSize = text.getPreferredSize();
			preferredSize.setSize(preferredSize.getWidth(), 27.0);
			text.setPreferredSize(preferredSize);

			textcontrols[i] = text;

			JComponent text1 = condline.getCondEditcomp1();
			if (condline.getColtype().startsWith("date")) {
				text1.setEnabled(true);
			} else {
				text1.setEnabled(false);
			}

			preferredSize = text1.getPreferredSize();
			preferredSize.setSize(preferredSize.getWidth(), 27.0);
			text1.setPreferredSize(preferredSize);
			textcontrols1[i] = text1;

			setQueryHotkey(condline.getDbcolumndisplayinfo());
			setQueryHotkey1(condline.getDbcolumndisplayinfo());

			i++;
		}

		return querypanel;
	}

	static String SHOWHOVDIALOG = "showhov";

	void setQueryHotkey(DBColumnDisplayInfo colinfo) {
		// F12键选hov
		JComponent comp = colinfo.getEditComponent();
		KeyStroke keyf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
		comp.getInputMap(JComponent.WHEN_FOCUSED).put(keyf12, SHOWHOVDIALOG);
		comp.getActionMap().put(SHOWHOVDIALOG,
				new HovKeyAction(SHOWHOVDIALOG, colinfo));
	}

	void setQueryHotkey1(DBColumnDisplayInfo colinfo) {
		// F12键选hov
		JComponent comp = colinfo.getEditComponent1();
		KeyStroke keyf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
		comp.getInputMap(JComponent.WHEN_FOCUSED).put(keyf12, SHOWHOVDIALOG);
		comp.getActionMap().put(SHOWHOVDIALOG,
				new HovKeyAction1(SHOWHOVDIALOG, colinfo));
	}

	class HovKeyAction extends AbstractAction {
		DBColumnDisplayInfo colinfo = null;

		public HovKeyAction(String name, DBColumnDisplayInfo colinfo) {
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			this.colinfo = colinfo;
		}

		public void actionPerformed(ActionEvent e) {
			// String cmd = e.getActionCommand();
			// 当前激活的元件是事件的触发者. by wwh 20070727
			if (colinfo.isUsehov()) {
				colinfo.setComp1firehov(false);
				colinfo.showHovDialog();
			} else if (colinfo.getColtype().equalsIgnoreCase("date")) {
				colinfo.setComp1firehov(false);
				colinfo.showDatedialog();
			} else if (colinfo.getEditcomptype().equals(
					DBColumnDisplayInfo.EDITCOMP_COMBOBOX)) {
				colinfo.selectComboboxData();
			}
		}
	}

	class HovKeyAction1 extends AbstractAction {
		DBColumnDisplayInfo colinfo = null;

		public HovKeyAction1(String name, DBColumnDisplayInfo colinfo) {
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			this.colinfo = colinfo;
		}

		public void actionPerformed(ActionEvent e) {
			// String cmd = e.getActionCommand();
			// 当前激活的元件是事件的触发者. by wwh 20070727
			if (colinfo.isUsehov()) {
				colinfo.setComp1firehov(true);
				colinfo.showHovDialog();
			} else if (colinfo.getColtype().equalsIgnoreCase("date")) {
				colinfo.setComp1firehov(true);
				colinfo.showDatedialog();
			}
		}
	}

	/**
	 * 如果选中的是范围,editcomp1 设置为可编辑. 否则禁止编辑
	 */
	class Cboplistener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			int i;
			for (i = 0; i < opcontrols.length; i++) {
				if (cb == opcontrols[i]) {
					Querycondline ql = querycond.elementAt(i);
					ql.setOpvalue((String) cb.getSelectedItem());
					break;
				}
			}
			JComponent editcomp1 = textcontrols1[i];

			String selectitem = (String) cb.getSelectedItem();
			if (selectitem.equals("范围")) {
				editcomp1.setEnabled(true);
			} else {
				editcomp1.setEnabled(false);
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(ACTION_QUERY)) {
			onconfirm(true);
		} else if (command.equals(ACTION_CANCEL)) {
			onconfirm(false);
		} else if (command.equals(ACTION_CLEAR)) {
			onclear();
		}
	}

	void onclear() {
		// 重新设置查询值
		Enumeration<Querycondline> en = querycond.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = en.nextElement();
			condline.clearControl();
		}
	}

	class QueryActionListener extends AbstractAction {
		public QueryActionListener(String actioname) {
			super(actioname);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, actioname);
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println(e.getActionCommand());
			String command = e.getActionCommand();
			if (command.equals(ACTION_QUERY)) {
				onconfirm(true);
			} else if (command.equals(ACTION_CANCEL)) {
				onconfirm(false);
			}
		}
	}

	boolean confirm = false;

	public void onconfirm(boolean confirm) {
		this.confirm = confirm;

		if (confirm) {
			int i = 0;
			// 检查必填
			Enumeration en = querycond.elements();
			for (i=0;en.hasMoreElements();i++) {
				Querycondline condline = (Querycondline) en.nextElement();
				if(!condline.getDbcolumndisplayinfo().isQuerymust()){
					continue;
				}
				condline.getCbUse().setSelected(true);
				if (textcontrols[i] instanceof JTextField) {
					JTextField textfield = (JTextField) textcontrols[i];
					String inputvalue = textfield.getText().trim();
					if (inputvalue.length() == 0 || inputvalue.startsWith("0000-00-00")) {
						warnMessage("提示","必须输入\""+condline.getDbcolumndisplayinfo().getTitle()+"\"条件");
						textcontrols[i].requestFocus();
						return;
					}					
				} else if (textcontrols[i] instanceof CComboBox) {
					CComboBox cbvalue = (CComboBox) textcontrols[i];
					String inputvalue = "";
					if (cbvalue.getSelectedItem() != null) {
						inputvalue = cbvalue.getSelectedItem().toString();
					}
					if (inputvalue.length() == 0) {
						warnMessage("提示","必须输入\""+condline.getDbcolumndisplayinfo().getTitle()+"\"条件");
						textcontrols[i].requestFocus();
						return;
						
					}
				}		
			}			
			i=0;
			en = querycond.elements();
			while (en.hasMoreElements()) {
				Querycondline condline = (Querycondline) en.nextElement();
				if (textcontrols[i] instanceof JTextField) {
					JTextField textfield = (JTextField) textcontrols[i];
					String inputvalue = textfield.getText().trim();
					if (inputvalue.length() > 0) {
						// 设置值
						condline.setValue(inputvalue);
						if (opcontrols[i] instanceof JComboBox) {
							JComboBox cb = (JComboBox) opcontrols[i];
							String opvalue = cb.getSelectedItem().toString();
							condline.setOpvalue(opvalue);
						} // instance of JComboBox
					} // value len > 0
				} else if (textcontrols[i] instanceof CComboBox) {
					CComboBox cbvalue = (CComboBox) textcontrols[i];
					String inputvalue = "";
					if (cbvalue.getSelectedItem() != null) {
						inputvalue = cbvalue.getSelectedItem().toString();
					}
					if (inputvalue.length() > 0) {
						// 设置值
						condline.setValue(inputvalue);
						if (opcontrols[i] instanceof JComboBox) {
							JComboBox cb = (JComboBox) opcontrols[i];
							String opvalue = cb.getSelectedItem().toString();
							condline.setOpvalue(opvalue);
						} // instance of JComboBox
					} // value len > 0
				}
				i++;
			}// while
		}

		super.setVisible(false);
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setVisible(boolean b) {
		if (b) {
			confirm = false;
		}
		// onclear();
		super.setVisible(b); // To change body of overridden methods use File
		// | Settings | File Templates.
	}

	protected void postPack() {
		if (textcontrols.length > 0) {
			textcontrols[0].requestFocus();
		}
	}

	class Updownhandler extends AbstractAction{

		public Updownhandler(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY,name);
		}

		public void actionPerformed(ActionEvent actionevent) {
			if(textcontrols.length==0)return;
			int curindex=-1;
			for(int i=0;i<textcontrols.length;i++){
				//System.out.println("i="+i+","+textcontrols[i].isFocusOwner());
				if(textcontrols[i].isFocusOwner()){
					curindex=i;
					break;
				}
			}
			for(int i=0;i<textcontrols1.length;i++){
				//System.out.println("i="+i+","+textcontrols[i].isFocusOwner());
				if(textcontrols1[i].isFocusOwner()){
					curindex=i;
					break;
				}
			}
			
			if(curindex<0){
				textcontrols[0].requestFocus();
			}else{
				Querycondline ql=querycond.elementAt(curindex);
				if(ql.getDbcolumndisplayinfo().isHovwindowvisible()){
					//如果打开了hov窗口不能移动
					return;
				}
			}
			
			if(actionevent.getActionCommand().equals("nextcond")){
				if(curindex<0){
					curindex=0;
				}else{
					curindex++;
				}
				if(curindex<textcontrols.length){
					textcontrols[curindex].requestFocus();
				}
			}
			
			if(actionevent.getActionCommand().equals("priorcond")){
				curindex--;
				if(curindex<0){
					curindex=0;
				}
				if(curindex<textcontrols.length){
					textcontrols[curindex].requestFocus();
				}
			}

		}
		
	}
	
	public void freeMemory() {
		opcontrols = null;
		textcontrols = null;
		textcontrols1 = null;
		btnok = null;
		otherquerycontrol = null;

		if (querycond != null) {
			querycond.freeMemory();
			querycond = null;
		}
	}
}
