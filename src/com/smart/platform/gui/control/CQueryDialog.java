package com.smart.platform.gui.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;

import org.apache.log4j.Category;

import com.smart.client.system.Clientframe;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.gui.ste.Steframe;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-29 Time: 17:26:54
 * To change this template use File | Settings | File Templates.
 */
public class CQueryDialog extends CDialog {
	Category logger = Category.getInstance(CQueryDialog.class);

	final String ACTION_QUERY = "ȷ��";
	final String ACTION_CLEAR = "���";
	final String ACTION_CANCEL = "ȡ��";
	final String ACTION_ADVANCE = "�߼�";

	protected JComponent[] opcontrols = null;
	protected JComponent[] textcontrols = null;
	protected JComponent[] textcontrols1 = null;
	private JButton btnok;
	private COtherquerycontrol otherquerycontrol;

	/**
	 * ��Ʒ�����Զ��������
	 */
	JPanel otherquerypane;

	/**
	 * ��ѯ���� pane
	 */
	JScrollPane querypane;

	/**
	 * ������ pane
	 */
	JPanel toolbarpane;

	/**
	 * �߼�����pane
	 */
	Advpanel advpane;

	/**
	 * �Զ�������pane ��С
	 */
	Dimension sizeotherquerypane;

	/**
	 * ��ѯ����pane��С
	 */
	Dimension sizequerypane;

	/**
	 * ������pane��С
	 */
	Dimension sizetoolbarpane;

	/**
	 * �߼���������С
	 */
	Dimension sizeadvpane;

	/**
	 * �Ի�����
	 */
	int preferredwidth = 560;

	/**
	 * �Ի�������.
	 */
	int dlgmaxheight = 680;

	/**
	 * ��ѯ�����ܹ����߶�
	 */
	int maxquerypaneheight = 100;

	boolean showadv = false;

	/**
	 * ��ģʽ simplemode=true ����ʾ�������ؼ�,�ͷ�Χ�ĵڶ����༭��
	 */
	private boolean simplemode = false;

	public boolean isSimplemode() {
		return simplemode;
	}

	public void setSimplemode(boolean simplemode) {
		this.simplemode = simplemode;
	}

	public CQueryDialog(Frame owner, String title,
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
		querycond.setQuerydlg(this);

		Container cp = this.getContentPane();
		BoxLayout boxLayout = new BoxLayout(cp, BoxLayout.Y_AXIS);
		// cp.setLayout(boxLayout);
		cp.setLayout(new Dlglayout());

		if (otherquerycontrol != null) {
			otherquerypane = otherquerycontrol.getOtherquerypanel();
			if (otherquerypane != null) {
				cp.add(otherquerypane);
			}
		}

		JPanel jpallqueryp = buildQuerypanel(querycond);
		maxquerypaneheight = jpallqueryp.getMaximumSize().height;
		jpallqueryp.setPreferredSize(new Dimension(preferredwidth - 40,
				maxquerypaneheight));
		jpallqueryp.setMaximumSize(new Dimension(preferredwidth - 40,
				maxquerypaneheight));
		// jpallqueryp.setMinimumSize(jpallqueryp.getPreferredSize());
		querypane = new JScrollPane(jpallqueryp);

		KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		JComponent jcp = (JComponent) getContentPane();
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkup, "priorcond");
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkdown, "nextcond");
		jcp.getActionMap().put("priorcond", new Updownhandler("priorcond"));
		jcp.getActionMap().put("nextcond", new Updownhandler("nextcond"));

		querypane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(vkup, "nouse");
		querypane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(vkdown, "nouse");

		cp.add(new JLabel(" "));
		cp.add(querypane);
		toolbarpane = buildBottompane();
		cp.add(toolbarpane);

		// �Զ����ѯ��С
		sizeotherquerypane = new Dimension(preferredwidth, 30);
		sizequerypane = new Dimension(preferredwidth - 20, 300);
		sizetoolbarpane = new Dimension(preferredwidth, 30);
		sizeadvpane = new Dimension(preferredwidth, 160);

		advpane = new Advpanel();
		cp.add(advpane);
		localCenter();
	}

	protected String getQuerybuttontext() {
		return "��ѯ(�س�)";
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

		JButton btnadv = new JButton("�߼�A");
		btnadv.setMnemonic('a');
		btnadv.setActionCommand(ACTION_ADVANCE);
		btnadv.addActionListener(this);

		JButton btnclear = new JButton("���C");
		btnclear.setMnemonic('c');
		btnclear.setActionCommand(ACTION_CLEAR);
		btnclear.addActionListener(this);

		JButton btncancel = new JButton("ȡ��(ESC)");
		btncancel.setActionCommand(ACTION_CANCEL);
		KeyStroke keyesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		btncancel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(keyesc,
				ACTION_CANCEL);
		btncancel.getActionMap().put(ACTION_CANCEL,
				new QueryActionListener(ACTION_CANCEL));
		btncancel.addActionListener(this);

		panel.add(btnok);
		panel.add(btnadv);
		panel.add(btnclear);
		panel.add(btncancel);

		return panel;

	}

	protected JPanel buildQuerypanel(Querycond querycond) {
		opcontrols = new JComponent[querycond.size()];
		textcontrols = new JComponent[querycond.size()];
		textcontrols1 = new JComponent[querycond.size()];

		JPanel querypanel = new JPanel();
		CFormlayout formlayout = new CFormlayout(2, 2);
		querypanel.setLayout(formlayout);

		int i = 0;
		Enumeration<Querycondline> en = querycond.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			String title = condline.getTitle();
			String op = condline.getOp();
			String[] ops = op.split(",");

			JLabel lb = null;
			JButton btnsubquery = null;
			if (!simplemode) {
				querypanel.add(condline.getCbUse());
				// ������Ӳ�ѯ,�Ӹ���ť
				if (condline.getDbcolumndisplayinfo().getSubqueryopid()
						.length() > 0) {
					btnsubquery = new JButton(condline.getTitle());
					btnsubquery.setHorizontalAlignment(SwingConstants.LEFT);
					btnsubquery.setMargin(new Insets(1, 1, 1, 1));
					btnsubquery
							.addActionListener(new SubqueryHandler(condline));
					querypanel.add(btnsubquery);
					condline.getCbUse().setPreferredSize(new Dimension(15, 27));
					btnsubquery.setPreferredSize(new Dimension(105-2, 27));
				} else {
					condline.getCbUse()
							.setPreferredSize(new Dimension(120, 27));
				}
			} else {
				condline.getCbUse().setSelected(true);
				lb = new JLabel(condline.getTitle());
				querypanel.add(lb);
				lb.setPreferredSize(new Dimension(110, 27));
				condline.getCbUse().setPreferredSize(new Dimension(120, 27));
			}

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

			// һ��Ҫ����placeComponent
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
		// F12��ѡhov
		JComponent comp = colinfo.getEditComponent();
		KeyStroke keyf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
		comp.getInputMap(JComponent.WHEN_FOCUSED).put(keyf12, SHOWHOVDIALOG);
		comp.getActionMap().put(SHOWHOVDIALOG,
				new HovKeyAction(SHOWHOVDIALOG, colinfo));
	}

	void setQueryHotkey1(DBColumnDisplayInfo colinfo) {
		// F12��ѡhov
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
			// ��ǰ�����Ԫ�����¼��Ĵ�����. by wwh 20070727
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
			// ��ǰ�����Ԫ�����¼��Ĵ�����. by wwh 20070727
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
	 * ���ѡ�е��Ƿ�Χ,editcomp1 ����Ϊ�ɱ༭. �����ֹ�༭
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
			if (selectitem.equals("��Χ")) {
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
		} else if (command.equals(ACTION_ADVANCE)) {
			onAdvance();
		} else {
			super.actionPerformed(e);
		}
	}

	void onclear() {
		// �������ò�ѯֵ
		Enumeration<Querycondline> en = querycond.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = en.nextElement();
			condline.clearControl();
		}
		advpane.clear();
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
		advpane.table.stopEdit();
		if (confirm) {
			int i = 0;
			// ������
			Enumeration en = querycond.elements();
			for (i = 0; en.hasMoreElements(); i++) {
				Querycondline condline = (Querycondline) en.nextElement();
				if (!condline.getDbcolumndisplayinfo().isQuerymust()) {
					continue;
				}
				condline.getCbUse().setSelected(true);
				if (textcontrols[i] instanceof JTextField) {
					JTextField textfield = (JTextField) textcontrols[i];
					String inputvalue = textfield.getText().trim();
					if (inputvalue.length() == 0
							|| inputvalue.startsWith("0000-00-00")) {
						warnMessage("��ʾ", "��������\""
								+ condline.getDbcolumndisplayinfo().getTitle()
								+ "\"����");
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
						warnMessage("��ʾ", "��������\""
								+ condline.getDbcolumndisplayinfo().getTitle()
								+ "\"����");
						textcontrols[i].requestFocus();
						return;

					}
				}
			}
			i = 0;
			en = querycond.elements();
			while (en.hasMoreElements()) {
				Querycondline condline = (Querycondline) en.nextElement();
				if (textcontrols[i] instanceof JTextField) {
					JTextField textfield = (JTextField) textcontrols[i];
					String inputvalue = textfield.getText().trim();
					if (inputvalue.length() > 0) {
						// ����ֵ
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
						// ����ֵ
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

		// ������չ����
		DBTableModel dbmodel = advpane.dbmodel;
		StringBuffer sb = new StringBuffer();
		StringBuffer sbdtl = new StringBuffer();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String advtype = dbmodel.getItemValue(r, "advtype");
			String advwheres = dbmodel.getItemValue(r, "advwheres");
			if (advwheres.trim().length() == 0) {
				continue;
			}
			if (advtype.startsWith("ϸ��")) {
				if (sbdtl.length() > 0) {
					sbdtl.append(" and ");
				}
				sbdtl.append(advwheres);
			} else {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				sb.append(advwheres);
			}
		}
		
		querycond.setAdvwheres(sb.toString());
		querycond.setAdvdtlwheres(sbdtl.toString());

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

	class Updownhandler extends AbstractAction {

		public Updownhandler(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent actionevent) {
			if (textcontrols.length == 0)
				return;
			int curindex = -1;
			for (int i = 0; i < textcontrols.length; i++) {
				// System.out.println("i="+i+","+textcontrols[i].isFocusOwner());
				if (textcontrols[i].isFocusOwner()) {
					curindex = i;
					break;
				}
			}
			for (int i = 0; i < textcontrols1.length; i++) {
				// System.out.println("i="+i+","+textcontrols[i].isFocusOwner());
				if (textcontrols1[i].isFocusOwner()) {
					curindex = i;
					break;
				}
			}

			if (curindex < 0) {
				textcontrols[0].requestFocus();
			} else {
				Querycondline ql = querycond.elementAt(curindex);
				if (ql.getDbcolumndisplayinfo().isHovwindowvisible()) {
					// �������hov���ڲ����ƶ�
					return;
				}
			}

			if (actionevent.getActionCommand().equals("nextcond")) {
				if (curindex < 0) {
					curindex = 0;
				} else {
					curindex++;
				}
				if (curindex < textcontrols.length) {
					textcontrols[curindex].requestFocus();
				}
			}

			if (actionevent.getActionCommand().equals("priorcond")) {
				curindex--;
				if (curindex < 0) {
					curindex = 0;
				}
				if (curindex < textcontrols.length) {
					textcontrols[curindex].requestFocus();
				}
			}

		}

	}

	/**
	 * �Ƿ���ʾ�߼�
	 * 
	 * @return
	 */
	boolean isShowadv() {
		return showadv;
	}

	/**
	 * ÿ��pane����һ������. �ֱ��� ��չ otherquerypane, querypane toolbarpane�� ��������freepane
	 * 
	 * @author user
	 * 
	 */
	class Dlglayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void layoutContainer(Container parent) {
			// ���ø���λ��
			int y = 0;
			int startx = 10;

			int maxh = parent.getHeight();
			int querypaneh = maxh;

			if (otherquerypane != null) {
				querypaneh -= sizeotherquerypane.height;
			}
			querypaneh -= sizetoolbarpane.height;

			if (isShowadv()) {
				querypaneh -= sizeadvpane.height;
			}


			if (querypaneh > maxquerypaneheight) {
				querypaneh = maxquerypaneheight;
			}
			sizequerypane.height = querypaneh;
			//

			if (otherquerypane != null) {
				otherquerypane.setBounds(startx, y, sizeotherquerypane.width,
						sizeotherquerypane.height);
				y += sizeotherquerypane.height;
			}

			querypane.setBounds(startx, y, sizequerypane.width,
					sizequerypane.height);
			y += sizequerypane.height;

			toolbarpane.setBounds(startx, y, sizetoolbarpane.width,
					sizetoolbarpane.height);
			y += sizetoolbarpane.height;

			if (isShowadv()) {
				advpane.setBounds(startx, y, sizeadvpane.width,
						sizeadvpane.height);
				y += sizeadvpane.height;
			}

			// }
		}

		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		public Dimension preferredLayoutSize(Container parent) {
			int y = 0;
			int startx = 10;
			if (otherquerypane != null) {
				y += sizeotherquerypane.height;
			}

			y += sizequerypane.height;

			y += sizetoolbarpane.height;

			if (isShowadv()) {
				y += sizeadvpane.height;
			}

			return new Dimension(preferredwidth, y);
		}

		public void removeLayoutComponent(Component comp) {
		}

	}

	@Override
	public Dimension getPreferredSize() {
		int h = 0;

		if (otherquerypane != null) {
			h += sizeotherquerypane.height;
		}
		h += sizetoolbarpane.height;

		if (isShowadv()) {
			h += sizeadvpane.height;
		}

		h += maxquerypaneheight + 40;

		if (h > 640) {
			h = 640;
		}

		return new Dimension(preferredwidth, h);

	}

	/**
	 * ���ضԻ����������С
	 */
	@Override
	public Dimension getMaximumSize() {
		int h = 0;

		if (otherquerypane != null) {
			h += sizeotherquerypane.height;
		}
		h += sizetoolbarpane.height;

		if (isShowadv()) {
			h += sizeadvpane.height;
		}

		h += maxquerypaneheight ;

		if (h > dlgmaxheight) {
			h = dlgmaxheight;
		}

		return new Dimension(preferredwidth, h);
	}

	void onAdvance() {
		showadv = !showadv;
		Dimension size = getMaximumSize();
		setSize(size);
		advpane.setVisible(showadv);
		invalidate();
		validate();
		localCenter();
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


	class SubqueryHandler extends AbstractAction {
		Querycondline querycondline = null;

		public SubqueryHandler(Querycondline querycondline) {
			this.querycondline = querycondline;
		}

		public void actionPerformed(ActionEvent e) {
			// ������ѯ���������в�ѯ
			doSubquery(querycondline, querycondline.getDbcolumndisplayinfo()
					.getSubqueryopid());
		}
	}

	void doSubquery(Querycondline orgquerycondline, String callopid) {
		String subquerywheres = "";
		COpframe frm = Clientframe.getClientframe()
				.runOp(callopid, true, false);
		if (frm == null) {
			logger.error("���ù���opid=" + callopid + "ʧ��,�޷�ʹ����չ����");
			return;
		}
		CSteModel stemodel = null;
		if (frm instanceof Steframe) {
			stemodel = ((Steframe) frm).getCreatedStemodel();
		} else if (frm instanceof MdeFrame) {
			stemodel = ((MdeFrame) frm).getCreatedMdemodel().getMasterModel();
		} else {
			logger.error("�޷������� " + frm.getClass().getName());
			return;
		}
		
		String wheres= stemodel.doQueryreturnWheres();
		if(wheres == null || wheres.length()==0){
			CMessageDialog.warnMessage(this, "��ʾ", "�Ӳ�ѯ����Ϊ��,��������һ������");
			subquerywheres = "";
			return;
		}

		String pkcolname=stemodel.getDBtableModel().getPkcolname();
		StringBuffer sb=new StringBuffer();
		sb.append("select ");
		sb.append(pkcolname + " from ");
		sb.append(stemodel.getTablename() + " where " + wheres);
		subquerywheres = sb.toString();

		subquerywheres = orgquerycondline.getColname() + " in ("
				+ subquerywheres + ")";

		String advtype = "�Ӳ�ѯ";
		if (orgquerycondline.getMdflag().equals("d")) {
			advtype = "ϸ���Ӳ�ѯ";
		}
		advpane.addAdvCond(advtype, subquerywheres);
		showadv = false;
		onAdvance();
		btnok.requestFocus();
	}

	public Querycond getQuerycond() {
		return querycond;
	}

	/**
	 * �߼���ѯ����panel.�в�Ϊһ��table. �²��ǹ�����,�� ���� ɾ��������ť
	 * 
	 * @author user
	 * 
	 */
	class Advpanel extends JPanel implements ActionListener {
		DBTableModel dbmodel = null;
		CEditableTable table = null;

		Advpanel() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createTitledBorder("�߼�����"));
			dbmodel = new DBTableModel(createCols());
			Sumdbmodel sumdm = new Sumdbmodel(dbmodel);

			table = new CEditableTable(sumdm);
			JScrollPane jsp = new JScrollPane(table);
			Dimension tablesize = new Dimension(preferredwidth,
					sizeadvpane.height - 20);
			jsp.setPreferredSize(tablesize);

			add(jsp, BorderLayout.CENTER);

			JPanel tb = new JPanel();
			add(tb, BorderLayout.SOUTH);

			Dimension btnsize = new Dimension(100, 20);
			JButton btn;
			btn = new JButton("��������");
			tb.add(btn);
			btn.setActionCommand("��������");
			btn.addActionListener(this);
			btn.setPreferredSize(btnsize);

			btn = new JButton("ɾ������");
			tb.add(btn);
			btn.setActionCommand("ɾ������");
			btn.addActionListener(this);
			btn.setPreferredSize(btnsize);
		}

		public void clear() {
			table.stopEdit();
			dbmodel.clearAll();
			table.tableChanged(new TableModelEvent(table.getModel()));
		}

		public void addAdvCond(String type, String wheres) {
			table.stopEdit();
			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "advtype", type);
			dbmodel.setItemValue(r, "advwheres", wheres);
			table.tableChanged(new TableModelEvent(table.getModel()));
			table.getSelectionModel().setSelectionInterval(r, r);
		}

		Vector<DBColumnDisplayInfo> createCols() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("advtype",
					DBColumnDisplayInfo.COLTYPE_VARCHAR, "��������");
			cols.add(col);
			col.setTablecolumnwidth(75);
			col.setReadonly(true);

			col = new DBColumnDisplayInfo("advwheres",
					DBColumnDisplayInfo.COLTYPE_VARCHAR, "����");
			cols.add(col);
			col.setTablecolumnwidth(600);

			return cols;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("��������")) {
				addAdvCond("�Զ���", "");
			} else if (e.getActionCommand().equals("ɾ������")) {
				int r = table.getRow();
				if (r >= 0 && r < dbmodel.getRowCount()) {
					table.stopEdit();
					dbmodel.removeRow(r);
					table.tableChanged(new TableModelEvent(table.getModel()));
				}
			}
		}
	}

}
