package com.inca.np.gui.mde;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Category;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CDefaultProgress;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.CSteModelListener;
import com.inca.np.gui.ste.CSteModelListenerAdaptor;
import com.inca.np.gui.ste.SteActionListener;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.gui.ste.SysinfoAction;
import com.inca.npx.mde.CMdeModelAp;
import com.inca.npx.ste.CSteModelAp;

/**
 * �󲿵���༭���Ҳ�MDE
 * 
 * @author Administrator
 * 
 */
public abstract class MMdeFrame extends Steframe {
	protected CMdeModel mdemodel = null;
	Category logger = Category.getInstance(MMdeFrame.class);

	public MMdeFrame() {
		super();
		setDefaultCloseOperation(Steframe.DISPOSE_ON_CLOSE);
	}

	public MMdeFrame(String title) {
		super(title);
		setDefaultCloseOperation(Steframe.DISPOSE_ON_CLOSE);
	}

	/**
	 * �����ܵ�ϸĿ
	 * 
	 * @return
	 */
	protected abstract CMdeModel createMde();

	/**
	 * ȡ����༭����������
	 * 
	 * @return
	 */
	protected abstract String getStepkcolname();

	/**
	 * ȡ�ܵ�ϸĿ����������Ӧ��Ϊmde���ܵ����һ�С�
	 * 
	 * @return
	 */
	protected abstract String getMderelatecolname();

	/**
	 * ���졣center��splitpane
	 */
	@Override
	protected void initControl() {
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		removeUpdownhotkey(jsp);

		Container cp = getContentPane();
		cp.add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(getHorizontalsize());
		if (stemodel == null) {
			stemodel = getStemodel();
		}
		stemodel.setOpid(opid);

		if (mdemodel == null) {
			mdemodel = createMde();
			mdemodel.getMasterModel()
					.addActionListener(new MasterStelistener());
		}

		// mastersterootpane�ϲ��ǹ��������в���table,�²���״̬
		// �� table����jsp��

		// jsp�ұ�����һ��JSplitPane�� �ϲ�����masterste��steform
		JSplitPane jsp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		removeUpdownhotkey(jsp1);
		jsp.setRightComponent(jsp1);
		JPanel mastersterootpane = stemodel.getRootpanel();
		// �� mastersterootpane ���ҳ�toolbar��������NORTH
		CStetoolbar tb = getToolbar(mastersterootpane);
		if (tb != null) {
			cp.add(tb, BorderLayout.NORTH);
		}

		jsp.setLeftComponent(stemodel.getRootpanel());
		jsp1.setLeftComponent(mdemodel.getMasterModel().getRootpanel());
		jsp1.setRightComponent(mdemodel.getDetailModel().getRootpanel());

		stemodel.addActionListener(new Stelistener());

		jsp1.setDividerLocation(getVerticalsize());

		setHotkey();
		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);
		
		stemodel.onstartRun();
	}

	CStetoolbar getToolbar(JPanel mastersterootpane) {
		CStetoolbar tb = null;
		for (int i = 0; i < mastersterootpane.getComponentCount(); i++) {
			Component jcomp = mastersterootpane.getComponent(i);
			if (jcomp instanceof CStetoolbar) {
				tb = (CStetoolbar) jcomp;
				mastersterootpane.remove(jcomp);
				break;
			} else if (jcomp instanceof JPanel) {
				CStetoolbar tmptb = getToolbar((JPanel) jcomp);
				if (tmptb != null)
					return tmptb;
			}
		}
		return tb;
	}

	@Override
	public void pack() {
		// TODO Auto-generated method stub
		super.pack();
	}

	protected int memrow = -1;

	boolean retrievemdeing = false;

	/**
	 * ��steֵ��mde
	 */
	protected void retrieveMde() {
		logger.debug("begin retrieveMde");
		if (retrievemdeing)
			return;
		retrievemdeing = true;
		try {
			setWaitcursor();
			int row = stemodel.getRow();
			if (row < 0)
				return;
			if (row == memrow) {
				retrievemdeing = false;
				return;
			}
			if (stemodel.getdbStatus(row) == RecordTrunk.DBSTATUS_NEW) {
				mdemodel.reset();
				retrievemdeing = false;
				return;
			}

			int ct = 0;
			stemodel.commitEdit();
			mdemodel.getMasterModel().commitEdit();
			mdemodel.getDetailModel().commitEdit();
			ct += stemodel.getModifiedDbmodel().getRowCount();
			ct += mdemodel.getModifiedRowCount();

			if (ct != 0) {
				// �ȱ���
				logger.debug("saveAll()");
				saveAll();
			}

			memrow = row;
			String stepkcol = this.getStepkcolname();
			String pkcolvalue = stemodel.getItemValue(row, stepkcol);
			if (pkcolvalue == null)
				pkcolvalue = "";
			String mdecol = this.getMderelatecolname();
			String wheres = mdecol + "='" + pkcolvalue + "'";
			mdemodel.setUsequerythread(false);
			mdemodel.getMasterModel().setUsequerythread(false);
			logger.debug("doQuery()");
			mdemodel.getMasterModel().doQuery(wheres);
			retrievemdeing = false;
		} finally {
			retrievemdeing = false;
			logger.debug("retrieveMde() finish");
			setDefaultcursor();
		}

	}

	protected class Stelistener extends CSteModelListenerAdaptor {
		public int on_beforesave() {
			saveAll();
			// ����-1����Ҫ�ٱ�����
			return -1;
		}

		public void on_click(int row, int col) {
			retrieveMde();
		}

		@Override
		public void on_retrievestart() {
			memrow = -1;
		}

		@Override
		public void on_close() {
			mdemodel.onstopRun();
			mdemodel.getParentFrame().dispose();
			dispose();
		}

		@Override
		public void on_retrieved() {
			super.on_retrieved();
			// �����ѯ��û�м�¼,�����mde
			if (stemodel.getRowCount() == 0) {
				mdemodel.reset();
			}
		}

		@Override
		public int on_beforenew() {
			stemodel.commitEdit();
			mdemodel.getMasterModel().commitEdit();
			mdemodel.getDetailModel().commitEdit();

			boolean modified = false;
			int row = stemodel.getRow();
			if (row >= 0) {
				int status = stemodel.getdbStatus(row);

				modified = status != RecordTrunk.DBSTATUS_SAVED;
			}
			if (!modified) {
				int mrow = mdemodel.getMasterModel().getRow();
				if (mrow >= 0) {
					int status = mdemodel.getMasterModel().getdbStatus(mrow);
					modified = status != RecordTrunk.DBSTATUS_SAVED;
				}
			}

			if (modified) {
				try {
					setWaitcursor();
					saveAll();
				} finally {
					setDefaultcursor();
				}

			}
			return super.on_beforenew();
		}

	}

	/**
	 * MDE��master�ļ���
	 * 
	 * @author Administrator
	 * 
	 */
	protected class MasterStelistener extends CSteModelListenerAdaptor {

		@Override
		public int on_beforenew() {
			// ���stemodel��������Ҫ�ȱ���
			int row = stemodel.getRow();
			if (row < 0) {
				// errorMessage("��ʾ", "���ȱ�����ߴ��ڵ�"+stemodel.getTitle()+"��¼������");
				stemodel.doNew();
				return -1;
			}
			int status = stemodel.getdbStatus(row);
			if (status == RecordTrunk.DBSTATUS_NEW
					|| status == RecordTrunk.DBSTATUS_DELETE) {
				// errorMessage("��ʾ", "���ȱ�����ߴ��ڵ�"+stemodel.getTitle()+"��¼������");
				// return -1;
				// �Զ�����stemodel
				try {
					setWaitcursor();
					if (!stemodel.savetoserver(null)) {
						errorMessage("����", stemodel.getTitle() + "����ʧ�ܣ���������");
						return -1;
					}
					if (stemodel.getErrorcount() == 0) {
					} else {
						errorMessage("����", stemodel.getTitle() + "����ʧ�ܣ���������");
						return -1;
					}
				} finally {
					setDefaultcursor();
				}

			}
			return super.on_beforenew();
		}

		/**
		 * ����stemodel������ֵ
		 */
		@Override
		public void on_new(int row) {
			super.on_new(row);
			int mrow = stemodel.getRow();
			String masterv = stemodel.getItemValue(mrow, getStepkcolname());
			mdemodel.getMasterModel().setItemValue(row, getMderelatecolname(),
					masterv);
		}

	}

	class Watchthread extends Thread {
		public void run() {
			while (true) {
				whereFocus();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void whereFocus() {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		Component focusc = kfm.getFocusOwner();
		System.out.print(focusc);
		if (focusc instanceof CTable) {
			CTable tb = (CTable) focusc;
			DBTableModel dbmodel = (DBTableModel) tb.getModel();
			System.out.print(" colct=" + tb.getColumnCount() + ",v="
					+ dbmodel.getItemValue(0, 1));
		}

		System.out.println();
	}

	@Override
	protected void setHotkey() {

		// F8 ��ѯ
		JComponent compcp = (JComponent) getContentPane();
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf8, CSteModel.ACTION_QUERY);
		compcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new SteActionListener(CSteModel.ACTION_QUERY, stemodel));

		// F9 ����
		KeyStroke vkf9 = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf9, CSteModel.ACTION_SAVE);
		compcp.getActionMap().put(CSteModel.ACTION_SAVE,
				new SteActionListener(CSteModel.ACTION_SAVE, stemodel));

		// ////////////////// MDE model /////////////////////////////////////

		// Ctrl+N ����
		KeyStroke vkctrln = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrln,
				CMdeModel.ACTION_NEW);
		compcp.getActionMap().put(CMdeModel.ACTION_NEW,
				new SteActionListener(CMdeModel.ACTION_NEW, mdemodel));

		// ɾ��
		KeyStroke vkctrld = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrld,
				CMdeModel.ACTION_DEL);
		compcp.getActionMap().put(CMdeModel.ACTION_DEL,
				new SteActionListener(CMdeModel.ACTION_DEL, mdemodel));

		// f2 �༭
		KeyStroke vkf2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf2,
				CMdeModel.ACTION_MODIFY);
		compcp.getActionMap().put(CMdeModel.ACTION_MODIFY,
				new SteActionListener(CMdeModel.ACTION_MODIFY, mdemodel));

		// undo
		KeyStroke vkctrlz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlz,
				CMdeModel.ACTION_UNDO);
		compcp.getActionMap().put(CMdeModel.ACTION_UNDO,
				new SteActionListener(CMdeModel.ACTION_UNDO, mdemodel));

		// undodtl
		KeyStroke vkctrlshitz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlshitz,
				CMdeModel.ACTION_UNDODTL);
		compcp.getActionMap().put(CMdeModel.ACTION_UNDODTL,
				new SteActionListener(CMdeModel.ACTION_UNDODTL, mdemodel));
		// /////////////////ϸ��
		KeyStroke vkctrli = KeyStroke.getKeyStroke(KeyEvent.VK_I,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrli,
				CMdeModel.ACTION_NEWDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_NEWDTL,
				new SteActionListener(CMdeModel.ACTION_NEWDTL, mdemodel));
		KeyStroke vkins = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
				0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkins,
				CMdeModel.ACTION_NEWDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_NEWDTL,
				new SteActionListener(CMdeModel.ACTION_NEWDTL, mdemodel));

		// ɾ��
		KeyStroke vkctrldel = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrldel,
				CMdeModel.ACTION_DELDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_DELDTL,
				new SteActionListener(CMdeModel.ACTION_DELDTL, mdemodel));

		// �޸�ϸ��F3
		KeyStroke vkf3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf3,
				CMdeModel.ACTION_MODIFYDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_MODIFYDTL,
				new SteActionListener(CMdeModel.ACTION_MODIFYDTL, mdemodel));

		// /////////////////����
		KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlw,
				CMdeModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CMdeModel.ACTION_HIDEFORM,
				new SteActionListener(CMdeModel.ACTION_HIDEFORM, mdemodel));

		// ��ӡ
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlp,
				CMdeModel.ACTION_PRINT);
		compcp.getActionMap().put(CMdeModel.ACTION_PRINT,
				new SteActionListener(CMdeModel.ACTION_PRINT, mdemodel));
		
		//�˳�
		KeyStroke altx = KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altx,
				CMdeModel.ACTION_EXIT);
		compcp.getActionMap().put(CMdeModel.ACTION_EXIT,
				new SteActionListener(CMdeModel.ACTION_EXIT, mdemodel));
		
    	SysinfoAction.installHotkey((JComponent) getContentPane());

	}

	/**
	 * ˮƽ�ָ�λ��
	 * 
	 * @return
	 */
	protected int getHorizontalsize() {
		return 200;
	}

	/**
	 * ��ֱ�ָ�λ��
	 * 
	 * @return
	 */
	protected int getVerticalsize() {
		return 300;
	}

	private void removeUpdownhotkey(JComponent jp) {
		KeyStroke removekeys[] = {
				//��Ҫ�������¼�
				//KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
				//KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false) };

		for (int i = 0; i < removekeys.length; i++) {
			jp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					removekeys[i], "nouse");
		}
	}

	public void saveAll() {
		int ct = 0;
		stemodel.commitEdit();
		mdemodel.getMasterModel().commitEdit();
		mdemodel.getDetailModel().commitEdit();
		ct += stemodel.getModifiedDbmodel().getRowCount();
		ct += mdemodel.getModifiedRowCount();

		if (ct == 0) {
			warnMessage("��ʾ", "û��������Ҫ����");
			return;
		}
		CDefaultProgress progress = new CDefaultProgress(this);
		Savethread savet = new Savethread(progress);
		// ��Ҫ���߳�.
		savet.run();
		progress.show();

	}

	class Savethread extends Thread {
		CDefaultProgress progress = null;

		Savethread(CDefaultProgress progress) {
			this.progress = progress;
		}

		public void run() {
			savetoserver(progress);
		}
	}

	protected void savetoserver(CDefaultProgress progress) {
		// �����ȱ���ϸ���ٴ��ܵ�������ȴ��ܵ��� �ڱ����ǰһ�䣬ϸ����¼ȫ���ˡ�
		/*
		 * Enumeration<Detailsteinfo> en = dtlsteinfos.elements(); while
		 * (en.hasMoreElements()) { Detailsteinfo dtlinfo = en.nextElement(); if
		 * (dtlinfo.getSte().getDBtableModel().getModifiedData() .getRowCount() >
		 * 0 && dtlinfo.getSte().on_beforesave() == 0) {
		 * progress.appendMessage("���ڱ���" + dtlinfo.getSte().getTitle() +
		 * ",�ȴ���������Ӧ......"); dtlinfo.getSte().savetoserver(null); if
		 * (dtlinfo.getSte().getErrorcount() == 0) {
		 * progress.appendMessage("����ɹ�"); } else {
		 * progress.appendMessage("ʧ�ܼ�¼" + dtlinfo.getSte().getErrorcount() +
		 * "��"); } } }
		 * 
		 */if (stemodel.getModifiedDbmodel().getRowCount() > 0
				&& stemodel.on_beforesave() == 0) {
			progress.appendMessage("���ڱ���" + stemodel.getTitle()
					+ ",�ȴ���������Ӧ......");
			stemodel.savetoserver(null);
			if (stemodel.getErrorcount() == 0) {
				progress.appendMessage("����ɹ�");
			} else {
				progress.appendMessage("ʧ�ܼ�¼" + stemodel.getErrorcount() + "��");
			}
		}

		if (mdemodel.getModifiedRowCount() > 0) {
			progress.appendMessage("���ڱ���"
					+ mdemodel.getMasterModel().getTitle() + ",�ȴ���������Ӧ......");
			mdemodel.savetoserver(progress);
		}

		if (progress != null) {
			progress.messageBox("��ʾ", "�������");
		}
	}

	protected void requeryMde() {
		memrow = -1;
		retrieveMde();
	}

	public CMdeModel getMdemodel() {
		return mdemodel;
	}

	public void setupAp(String roleid) {
		if (!(stemodel instanceof CSteModelAp)) {
			warnMessage("��ʾ", "�����ܲ���������Ȩ����");
			return;
		}
		((CSteModelAp) stemodel).setupAp(roleid);
	}

	@Override
	public void setOpid(String opid) {
		super.setOpid(opid);
	}

}
