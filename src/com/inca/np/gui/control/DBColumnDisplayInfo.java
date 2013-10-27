package com.inca.np.gui.control;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Category;

import com.inca.np.communicate.CommandFactory;
import com.inca.np.communicate.RemoteDdlHelper;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.gui.ste.Steform;
import com.inca.np.gui.ste.SteformUIManager;
import com.inca.np.image.CIcon;
import com.inca.np.image.IconFactory;
import com.inca.np.util.DecimalHelper;
import com.inca.np.util.DefaultNPParam;
import com.inca.npclient.download.DownloadManager;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 16:09:18
 * һ�����ݵĶ���
 */
public class DBColumnDisplayInfo {
	public final static String COLTYPE_NUMBER = "number";
	public final static String COLTYPE_VARCHAR = "varchar";
	public final static String COLTYPE_DATE = "date";

	public final static String EDITCOMP_TEXTFIELD = "textfield";
	public final static String EDITCOMP_COMBOBOX = "combobox";
	public final static String EDITCOMP_TEXTAREA = "textarea";
	public final static String EDITCOMP_CHECKBOX = "checkbox";

	/**
	 * ���ݿ�����
	 */
	String colname = "";

	/**
	 * ���ݿ�����,ȡֵΪCOLTYPE_xxxx
	 */
	String coltype = "";
	/**
	 * ������
	 */
	String title = "";

	/**
	 * �Ƿ�����
	 */
	boolean ispk = false;

	/**
	 * �༭GUI �ؼ�
	 */
	JComponent editorcomp = null;

	/**
	 * ���ڲ�ѯbetween����
	 */
	JComponent editorcomp1 = null;

	/**
	 * �༭������,ȱʡΪEDITCOMP_TEXTFIELD
	 */
	String editcomptype = EDITCOMP_TEXTFIELD;

	/**
	 * �ı����
	 */
	int editorheight = 27;

	/**
	 * �ı����
	 */
	int editorwidth = 120;
	
	/**
	 * �����ı��ĳ���.
	 */
	int labelwidth=80;

	/**
	 * �Ƿ��ڿ�Ƭ����������
	 */
	boolean linebreak = false;

	/**
	 * �Ƿ��д
	 */
	boolean uppercase = false;

	/**
	 * �Ƿ�ֻ��
	 */
	boolean readonly = false;

	/**
	 * ���������Ƿ�ʱ��
	 */
	boolean withtime = false;

	/**
	 * �Ƿ������ݿ�ɸ��µ���
	 */
	boolean updateable = true;

	/**
	 * �Ƿ����ַ������༭���� yyyy-mm��ʽ
	 */
	boolean yearmonth = false;
	/**
	 * �Ƿ������ݿ��У����кš������С��������ݿ���
	 */
	boolean dbcolumn = true;

	/**
	 * �Ƿ���Ϊ��ѯ����
	 */
	boolean queryable = true;

	/**
	 * ����С�����λ
	 */
	int numberscale = 0;

	/**
	 * ���к�
	 */
	String seqname = "";

	/**
	 * �ܷ�۽�
	 */
	boolean focusable = true;
	/**
	 * �����õ�HOV
	 */
	private CHovBase hov = null;

	/**
	 * ��Ƭ��ʾ�����JLabel
	 */
	private CLabel label;

	/**
	 * ��ֵ
	 */
	String initvalue = "";

	/**
	 * �Ƿ�����.���Ϊtrue,����Դ���и���,������ʾ.
	 */
	private boolean hide = false;

	/**
	 * �м��
	 */
	RowcheckInfo rowcheck = new RowcheckInfo();

	/**
	 * HOV����
	 */
	Hovdefine hovdefine = null;

	/**
	 * ����ccombobox���͵ľ�̬����Դ
	 */
	DBTableModel cbdbmodel = null;

	/**
	 * ��Ƭ���ڵ�icon
	 */
	private JLabel lbicon;

	/**
	 * �Ƿ���ʾ�ڿ�Ƭ������
	 */
	private boolean placeonform = false;
	
	/**
	 * �Ƿ���ʾ�ڲ�ѯ�Ի�����
	 */
	private boolean placeonquery = false;

	/**
	 * �Ƿ�Ҫ�Զ�����ϼ��еĺϼơ�
	 */
	private boolean calcsum = false;

	/**
	 * ��ʾ��ʽ
	 */
	String numberdisplayformat = "";

	/**
	 * ����Ĳ�ѯ����?
	 */
	boolean querymust = false;

	/*
	 * �Ƿ��Ǹ�����־ boolean fileflag =false;
	 */

	/**
	 * ���ֶ������ʾ�ڱ����,�п���tablecolumnwidthȷ��. ���tablecolumnwidth=-1,��ʾ�Զ������п�
	 */
	int tablecolumnwidth = -1;

	/**
	 * �Ƿ��ڿ�Ƭ���ڲ���ʾ�ı���ǩ
	 */
	boolean hidetitleoncard = false;

	/**
	 * �Ӳ�ѯ���õĹ���ID
	 */
	String subqueryopid="";

	/**
	 * ����
	 * 
	 * @param colname
	 *            ����
	 * @param coltype
	 *            ������,ȡֵCOLTYPE_xxxx
	 */
	public DBColumnDisplayInfo(String colname, String coltype) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
	}

	/**
	 * ����
	 * 
	 * @param colname
	 *            ����
	 * @param coltype
	 *            ������,ȡֵCOLTYPE_xxxx
	 * @param title
	 *            ����
	 */
	public DBColumnDisplayInfo(String colname, String coltype, String title) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
		this.title = title;

		label = new CLabel(title);
		// label.setPreferredSize(new Dimension(80, 27));
		label.setPreferredSize(new Dimension(labelwidth, 27));
	}

	/**
	 * ����
	 * 
	 * @param colname
	 *            ����
	 * @param coltype
	 *            ������,ȡֵCOLTYPE_xxxx
	 * @param title
	 *            ����
	 * @param linebreak
	 *            �ڿ�Ƭ�������Ƿ�����
	 */
	public DBColumnDisplayInfo(String colname, String coltype, String title,
			boolean linebreak) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
		this.title = title;
		this.linebreak = linebreak;

		label = new CLabel(title);
		label.setPreferredSize(new Dimension(labelwidth, 27));
	}

	/**
	 * ���ر༭����,ȡֵEDITCOMP_xxxx
	 * 
	 * @return
	 */
	public String getEditcomptype() {
		return editcomptype;
	}

	/**
	 * ���ñ༭����,ȡֵEDITCOMP_xxxx
	 * 
	 * @param editcomptype
	 */
	public void setEditcomptype(String editcomptype) {
		this.editcomptype = editcomptype;
		if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
			cbdbmodel = createCbdbmodel();
		}
	}

	/**
	 * �����Ƿ��д
	 * 
	 * @return
	 */
	public boolean isUppercase() {
		return uppercase;
	}

	/**
	 * �����Ƿ�ɾ۽�
	 * 
	 * @return
	 */
	public boolean isFocusable() {
		return focusable;
	}

	/**
	 * �����Ƿ�ɾ۽�
	 * 
	 * @param focusable
	 */
	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	/**
	 * �����ܷ��д
	 * 
	 * @param uppercase
	 */
	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	/**
	 * �����Ƿ��ڿ�Ƭ����������
	 * 
	 * @return
	 */
	public boolean isLinebreak() {
		return linebreak;
	}

	/**
	 * �����Ƿ�����
	 * 
	 * @param linebreak
	 */
	public void setLinebreak(boolean linebreak) {
		this.linebreak = linebreak;
	}

	/**
	 * ����С��λ��
	 * 
	 * @return
	 */
	public int getNumberscale() {
		return numberscale;
	}

	/**
	 * ����С��λ��
	 * 
	 * @param numberscale
	 */
	public void setNumberscale(int numberscale) {
		this.numberscale = numberscale;
	}

	/**
	 * �����Ƿ�ֻ��
	 * 
	 * @return
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * �����Ƿ�ֻ��
	 * 
	 * @param readonly
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
		if (editorcomp != null) {
			setEnable(!readonly);
		}
	}

	/**
	 * �����Ƿ��ʱ��
	 * 
	 * @return
	 */
	public boolean isWithtime() {
		return withtime;
	}

	/**
	 * �����Ƿ��ʱ��
	 * 
	 * @param withtime
	 */
	public void setWithtime(boolean withtime) {
		this.withtime = withtime;
	}

	/**
	 * �����Ƿ�ʹ��HOV
	 * 
	 * @return
	 */
	public boolean isUsehov() {
		if (hovdefine == null)
			return false;
		if (placeonform) {
			if (hovdefine.getUsecontext().indexOf("�༭") < 0)
				return false;
		}
		if (placeonquery) {
			if (hovdefine.getUsecontext().indexOf("��ѯ") < 0)
				return false;
		}
		return true;
	}

	/**
	 * ����HOV����
	 * 
	 * @return
	 */
	public Hovdefine getHovdefine() {
		return hovdefine;
	}

	/**
	 * �����Ƿ�����
	 * 
	 * @return
	 */
	public boolean isHide() {
		return hide;
	}

	/**
	 * �����Ƿ�����
	 * 
	 * @param hide
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * �Ƿ�yyyy-mm��ʽ
	 * 
	 * @return
	 */
	public boolean isYearmonth() {
		return yearmonth;
	}

	/**
	 * �����Ƿ�ʹ��yyyy-mm��ʽ
	 * 
	 * @param yearmonth
	 */
	public void setYearmonth(boolean yearmonth) {
		this.yearmonth = yearmonth;
	}

	Category logger = Category.getInstance(DBColumnDisplayInfo.class);

	/**
	 * ����hov����,�����ݶ��崴��hov
	 * 
	 * @param hovdefine
	 */
	public void setHovdefine(Hovdefine hovdefine) {
		this.hovdefine = hovdefine;
		if (hovdefine == null) {
			hov = null;
			return;
		}

		if (!DefaultNPParam.runonserver) {
			String classname = hovdefine.getHovclassname();
			try {
				if (classname.startsWith("hovgeneral_")) {
					String hovname = classname
							.substring("hovgeneral_".length());
					hov = DownloadManager.getInst().downloadZxhov(hovname);
				} else {
					hov = DownloadManager.getInst().downloadProdhov(classname);
				}
			} catch (Exception e) {
				logger.error("����hov��ʧ��" + hovdefine.getHovclassname(), e);
				return;
			}
		}

	}

	/**
	 * ���س�ֵ
	 * 
	 * @return
	 */
	public String getInitvalue() {
		return initvalue;
	}

	/**
	 * ���ó�ֵ
	 * 
	 * @param initvalue
	 */
	public void setInitvalue(String initvalue) {
		this.initvalue = initvalue;
	}

	/**
	 * �����м��
	 * 
	 * @return
	 */
	public RowcheckInfo getRowcheck() {
		return rowcheck;
	}

	/**
	 * �����м��
	 * 
	 * @param rowcheck
	 */
	public void setRowcheck(RowcheckInfo rowcheck) {
		this.rowcheck = rowcheck;
	}

	public DBTableModel getCbdbmodel() {
		return cbdbmodel;
	}

	/**
	 * ȡ����ѡ��ĳ��key��Ӧ��ֵ
	 * 
	 * @param key
	 * @return
	 */
	public String getComboboxValue(String key) {
		if (cbdbmodel == null)
			return null;
		for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
			if (cbdbmodel.getItemValue(r, "key").equals(key)) {
				return cbdbmodel.getItemValue(r, "value");
			}
		}
		return null;
	}

	public String getComboboxKey(String value) {
		if (cbdbmodel == null)
			return null;
		for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
			if (cbdbmodel.getItemValue(r, "value").equals(value)) {
				return cbdbmodel.getItemValue(r, "key");
			}
		}
		return null;
	}

	/**
	 * �����༭�ؼ�
	 * 
	 * @return
	 */
	public JComponent createComp() {
		JComponent editorcomp = null;
		if (coltype.equalsIgnoreCase("number")) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				if (cbdbmodel != null) {
					editorcomp = new CComboBox(new CComboBoxModel(cbdbmodel,
							"key", "value"));
				} else {
					editorcomp = new CComboBox();
				}
			} else if (editcomptype.equals(EDITCOMP_CHECKBOX)) {
				editorcomp = new CCheckBox();
			} else {
				editorcomp = new CNumberTextField(numberscale);
				((CFormatTextField) editorcomp).setEditorname(colname);
			}
		} else if (coltype.equalsIgnoreCase("date")) {
			if (withtime) {
				editorcomp = new CDatetimeTextField();
				((CFormatTextField) editorcomp).setEditorname(colname);
			} else {
				editorcomp = new CDateTextField();
				((CFormatTextField) editorcomp).setEditorname(colname);
			}
		} else if (coltype.equalsIgnoreCase("varchar")) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				if (cbdbmodel != null) {
					editorcomp = new CComboBox(new CComboBoxModel(cbdbmodel,
							"key", "value"));
				} else {
					editorcomp = new CComboBox();
				}
			} else if (editcomptype.equals(EDITCOMP_CHECKBOX)) {
				editorcomp = new CCheckBox();
			} else if (editcomptype.equals(EDITCOMP_TEXTAREA)) {
				editorcomp = new CTextArea();
			} else {
				if (yearmonth) {
					editorcomp = new CYMTextField();
					((CYMTextField) editorcomp).setEditorname(colname);
				} else if (uppercase) {
					editorcomp = new CUpperTextField();
					((CFormatTextField) editorcomp).setEditorname(colname);
				} else {
					editorcomp = new CPlainTextField();
					((CFormatTextField) editorcomp).setEditorname(colname);
				}
			}
		} else if (coltype.equals("�к�")) {
			editorcomp = new CTextField();
		} else {
			// ����coltype,��blob
			editorcomp = new CTextField();
		}

		if (editorcomp instanceof CTextArea) {
			editorcomp.setPreferredSize(new Dimension(editorwidth * 5 + 50,
					editorheight));
		} else {
			editorcomp
					.setPreferredSize(new Dimension(editorwidth, editorheight));
		}

		// �ܷ�۽�
		if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setKeyfocusable(focusable);
		}

		// ����۽�
		editorcomp.addFocusListener(new FocusEventHandle());

		// �������
		editorcomp.addKeyListener(new KeyEventHandle());

		//��������
		editorcomp.setName(colname);
		return editorcomp;
	}

	/**
	 * �������ڱ༭ sql in(�о�)�ı༭��
	 * 
	 * @return
	 */
	public CPlainTextField createQueryinComp() {
		CPlainTextField editorcomp = null;
		editorcomp = new CPlainTextField();
		((CFormatTextField) editorcomp).setEditorname(colname);

		editorcomp.setPreferredSize(new Dimension(editorwidth, editorheight));

		((CFormatTextField) editorcomp).setKeyfocusable(true);

		// ����۽�
		editorcomp.addFocusListener(new FocusEventHandle());

		// �������
		editorcomp.addKeyListener(new KeyEventHandle());

		return editorcomp;
	}

	/**
	 * ����hov,������������ѡhov
	 * 
	 * @param value
	 *            ����ֵ
	 * @param oldvalue
	 *            ԭֵ
	 * @param otherwheres
	 *            ����select where����
	 */
	public void invokeHov(String value, String oldvalue, String otherwheres) {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}
		if (!hovdataselected) {
			showHovWindow();

			// ���ǵ�����hov���У���hov�����ǿ��ܲ�ͬ�ģ���Ҫת��
			String hovcolname = hovdefine.getHovcolname(getColname());
			if (hovcolname == null) {
				logger.error("��hov�У��Ҳ���" + getColname() + "��Ӧ��hov��");
				hovcolname = getColname();
			}
			hov.doSelectHov(hovcolname, value, new HovHandle(), otherwheres);
		} else {
			DBTableModel resultdb = hov.getResult();
			confirmHov(resultdb);
		}
	}

	/**
	 * hovȷ��
	 * 
	 * @return trueѡ��hov�ɹ�,falseû�гɹ�
	 */
	public boolean confirmHov() {
		if (!hov.isVisible()) {
			// ���û�д�hov��˵�������õ�ֵ������true
			return true;
		}
		DBTableModel resultdb = hov.getResult();
		return confirmHov(resultdb);
	}

	/**
	 * ȷ����hov��ѡ��.
	 * 
	 * @param resultdb
	 * @return trueѡ��ɹ�,falseѡ��ʧ��
	 */
	private boolean confirmHov(DBTableModel resultdb) {
		if (resultdb != null) {
			hov.hide();
			hovdataselected = true;
			// editorcomp.transferFocus();
			hovlistener.on_hov(DBColumnDisplayInfo.this, resultdb);
			// ȷ�Ϻ�hovdataselectedҪ��Ϊfalse
			hovdataselected = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * HOV������
	 * 
	 * @author Administrator
	 * 
	 */
	class HovHandle implements HovListener {
		// ˫��hov��¼ʱ��CHovBase����
		public void on_hov(DBColumnDisplayInfo dispinfo, DBTableModel resultdb) {
			confirmHov(resultdb);
		}

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
		}

	}

	/**
	 * ���̼�����.�������¼�,��hov�ĵ�ǰ�����ϻ����¹��� esc���ر�
	 * 
	 * @author Administrator
	 * 
	 */
	class KeyEventHandle implements KeyListener {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			if (!isUsehov())
				return;
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				hov.nextRow();
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				hov.priorRow();
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// �ر�hov
				hov.hide();
				hovlistener.lostFocus(DBColumnDisplayInfo.this);
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	/**
	 * ��ʾhov��������
	 */
	private void showHovWindow() {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}

		if (!hov.isVisible()) {
			Component p = null;
			for (p = editorcomp.getParent(); p != null
					&& !(p instanceof Window); p = p.getParent())
				;
			hov.show((Window) p, editorcomp);

			Runnable r = new Runnable() {
				public void run() {
					if (comp1firehov) {
						editorcomp1.requestFocus();
					} else {
						editorcomp.requestFocus();
					}
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}
	
	/**
	 * �Ƿ���ʾhov��?
	 * @return
	 */
	public boolean isHovwindowvisible(){
		if(hov==null)return false;
		return hov.isVisible();
	}

	/**
	 * HOV�����Ƿ�ѡ��
	 */
	private boolean hovdataselected = false;

	/**
	 * Focus������
	 * 
	 * @author Administrator
	 * 
	 */
	class FocusEventHandle implements FocusListener {
		public void focusGained(FocusEvent e) {
			if (hovlistener == null) {
				return;
			}
			hovlistener.gainFocus(DBColumnDisplayInfo.this);
			if (isUsehov()) {
				// �򿪴���
				hovdataselected = false;
			}
		}

		public void focusLost(FocusEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
			// 2007 08 18 ��Ҫ���������hovlistener.lostFocus
		}
	}

	/**
	 * �ⲿ��HOV������
	 */
	private HovListener hovlistener = null;

	/**
	 * ���༭�ؼ�����Form��
	 * 
	 * @param parent
	 * @param layout
	 * @param hovlistener
	 */
	public void placeOnForm(JPanel parent, CFormlayout layout,
			HovListener hovlistener) {
		if (isHide()) {
			return;
		}
		placeonform = true;

		this.hovlistener = hovlistener;
		if (editorcomp == null) {
			editorcomp = createComp();
			addEnterkeyTraver(editorcomp);
		}

		if (this.getColtype().equals("�к�")) {
			return;
		}

		if (rowcheck != null && rowcheck.getChecktype().equals("�ǿ�")) {
			label = new CLabel(title + "*");
		} else {
			label = new CLabel(title);
		}
		label.setName(colname+"_title");
		if (!isHidetitleoncard()) {
			label.setPreferredSize(new Dimension(labelwidth, 27));
			parent.add(label);
		}

		if (getColname().equalsIgnoreCase("filegroupid")) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new FileMouseListener());
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else if (isUsehov() && hovdefine.getUsecontext().indexOf("�༭") >= 0) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			if (isReadonly()) {
				lbicon = new JLabel(IconFactory.ictransp);
			} else {
				lbicon = new JLabel(IconFactory.iccalendar16);
			}
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else if (editorcomp instanceof CDatetimeTextField
				|| editorcomp instanceof CDateTextField) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			if (isReadonly()) {
				lbicon = new JLabel(IconFactory.ictransp);
			} else {
				lbicon = new JLabel(IconFactory.iccalendar16);
			}
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new DatetimeMouseListener(editorcomp));
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			parent.add(jp);
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			CIcon transimg = IconFactory.ictransp;
			lbicon = new JLabel(transimg);
			lbicon.setName("lbicon");
			
			if(parent.getName()!=null && parent.getName().equals("stecenterform")){
				LayoutManager lm = parent.getLayout();
				if(lm instanceof SteformUIManager){
					SteformUIManager formuim=(SteformUIManager)lm;
					if(formuim.isUseformUI()){
						//һ��Ҫȥ�����icon. �����������ɵ�����ʽ��, ���ܽ������༭�ı�������һ��
					}else{
						jp.add(lbicon);
					}
				}else{
					jp.add(lbicon);
				}
			}else{
				jp.add(lbicon);
			}

			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		}

	}

	/**
	 * ���༭�ؼ����ڲ�ѯ������
	 * 
	 * @param parent
	 * @param layout
	 * @param hovlistener
	 * @param simplemode
	 */
	public void placeOnQuerypanel(JPanel parent, CFormlayout layout,
			HovListener hovlistener, boolean simplemode) {
		this.hovlistener = hovlistener;
		placeonquery = true;
		if (editorcomp == null) {
			editorcomp = createComp();
		}
		if (editorcomp1 == null) {
			editorcomp1 = createComp();
		}

		if (this.getColtype().equals("�к�")) {
			return;
		}

		if (this.getColtype().equals(COLTYPE_NUMBER)) {
			if (editorcomp instanceof CNumberTextField) {
				CNumberTextField ntf = (CNumberTextField) editorcomp;
				// ntf.setAllowcomma(true);
			}
		}

		/*
		 * 20080701 if (!queryable) { return; }
		 */
		label = new CLabel(title);
		label.setPreferredSize(new Dimension(labelwidth, 27));
		// parent.add(label);

		if (isUsehov() && hovdefine.getUsecontext().indexOf("��ѯ") >= 0) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());
			if (!simplemode) {
				jp.add(editorcomp1);
				lbicon = new JLabel(IconFactory.iccalendar16);
				jp.add(lbicon);
				lbicon.addMouseListener(new HovMouseListener1());
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());
		} else if (this.editcomptype.equals(EDITCOMP_COMBOBOX)) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			editorcomp = createQueryinComp();
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());

		} else if (editorcomp instanceof CDatetimeTextField
				|| editorcomp instanceof CDateTextField) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			JLabel lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new DatetimeMouseListener(editorcomp));

			if (!simplemode) {
				jp.add(editorcomp1);
				lbicon = new JLabel(IconFactory.iccalendar16);
				jp.add(lbicon);
				lbicon.addMouseListener(new DatetimeMouseListener(editorcomp1));
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());
		} else {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			CIcon transimg = IconFactory.ictransp;
			JLabel lbicon = new JLabel(transimg);
			jp.add(lbicon);

			// ��between����
			if (!simplemode && !editcomptype.equals(EDITCOMP_CHECKBOX)) {
				jp.add(editorcomp1);
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());

		}

		if (editorcomp instanceof CTextArea) {
			editorcomp
					.setPreferredSize(new Dimension(editorwidth, editorheight));
			editorcomp1.setPreferredSize(new Dimension(editorwidth,
					editorheight));
		}
	}

	/**
	 * ��������ѡ����
	 */
	public void showDatedialog() {
		JComponent comp = comp1firehov ? editorcomp1 : editorcomp;

		if (!((CFormatTextField) comp).isCanedit()) {
			return;
		}

		if (comp instanceof CDatetimeTextField) {
			CDatetimeTextField text = (CDatetimeTextField) comp;
			CCalendar dlg = new CCalendar(null, "ѡ����");
			dlg.pack();
			dlg.setVisible(true);
			Timestamp rettimestamp = dlg.getTimestamp();
			if (rettimestamp != null) {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String strv = df.format(rettimestamp);
				strv = strv.substring(0, 10) + " 00:00:00";
				text.setText(strv);
				try {
					text.commitEdit();
				} catch (ParseException e1) {
				}
			}

		} else if (comp instanceof CDateTextField) {
			CDateTextField text = (CDateTextField) comp;
			CCalendar dlg = new CCalendar(null, "ѡ����");
			dlg.pack();
			dlg.setVisible(true);
			Timestamp rettimestamp = dlg.getTimestamp();
			if (rettimestamp != null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String strv = df.format(rettimestamp);
				text.setText(strv);
				try {
					text.commitEdit();
				} catch (ParseException e1) {
				}
			}
		}
	}

	/**
	 * ���������ֶε���������
	 * 
	 * @author Administrator
	 * 
	 */
	class DatetimeMouseListener implements MouseListener {
		private JComponent ecomp;

		public DatetimeMouseListener(JComponent editorcomp) {
			this.ecomp = editorcomp;
		}

		public void mouseClicked(MouseEvent e) {
			Runnable r = new Runnable() {
				public void run() {
					ecomp.requestFocus();
					comp1firehov = ecomp == editorcomp1;
					showDatedialog();
				}
			};
			SwingUtilities.invokeLater(r);
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	/**
	 * ��ʾ��ͳHOV�Ի���
	 */
	public void showHovDialog() {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}

		if (hovlistener == null)
			return;
		if (hovdefine == null) {
			return;
		}

		if (DBColumnDisplayInfo.this.isReadonly())
			return;
		if (editorcomp instanceof CFormatTextField) {
			if (!((CFormatTextField) editorcomp).isCanedit())
				return;
		}

		// �����mde �� multi hov,��Ϊ����listener��invokehov
		if (hov instanceof CMultiHov || hov instanceof CMdeHov
				|| hov instanceof CStehovEx || hov instanceof CMdehovEx) {
			hovcondif.invokeMultimdehov(colname);
			return;
		}

		// ��ʾhov
		Component p = null;
		for (p = editorcomp.getParent(); p != null && !(p instanceof Dialog); p = p
				.getParent())
			;
		if (p == null) {
			for (p = editorcomp.getParent(); p != null && !(p instanceof Frame); p = p
					.getParent())
				;
		}

		
		String otherwheres = "";
		if (hovcondif != null) {
			otherwheres = hovcondif.getHovOtherWheres(colname);
			if (otherwheres == null)
				otherwheres = "";
			hov.setOtherwheres(otherwheres);
		}

		DBTableModel resultdb = null;
		if (p instanceof Dialog) {
			resultdb = hov.showDialog((Dialog) p, hov.getDesc(), "", "",
					otherwheres);
		} else {
			resultdb = hov.showDialog((Frame) p, hov.getDesc(), "", "",
					otherwheres);
		}
		if (resultdb != null) {
			confirmHov(resultdb);
		}
	}

	/**
	 * �����˱༭�ؼ����Сͼ�ꡣ
	 */
	class HovMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				// ��ʾ
				editorcomp.requestFocus();
				selectComboboxData();
			} else {
				comp1firehov = false;
				Runnable r = new Runnable() {
					public void run() {
						editorcomp.requestFocus();
						showHovDialog();
					}
				};
				SwingUtilities.invokeLater(r);

			}
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/**
	 * ����˸���
	 * 
	 * @author Administrator
	 * 
	 */
	class FileMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			hovlistener.on_hov(DBColumnDisplayInfo.this, null);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/**
	 * ����һ���Ի�����,ѡ
	 */
	public void selectComboboxData() {
		// TODO Auto-generated method stub
		JDialog parentdlg = null;

		Component tmpcomp = editorcomp.getParent();
		while ((tmpcomp = tmpcomp.getParent()) != null) {
			if (tmpcomp instanceof JDialog) {
				parentdlg = (JDialog) tmpcomp;
				break;
			}
		}

		String curvalue = ((CFormatTextField) editorcomp).getText();
		CQueryinDlg dlg = new CQueryinDlg(parentdlg, title, this, cbdbmodel,
				curvalue);
		dlg.pack();
		Dimension dlgsize = dlg.getPreferredSize();
		// ����λ��.����༭��������Ļ��λ��,�����Ƿ��ϱ߻����±�
		Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
		Point compscrpos = editorcomp.getLocationOnScreen();
		if (compscrpos.getY() >= scrsize.getHeight() / 2) {
			// �����ϲ�,����λ��
			int scrx = (int) compscrpos.getX();
			int scry = (int) compscrpos.getY()
					- (int) dlg.getPreferredSize().getHeight();
			if (scry < 0) {
				// ̫����,Ӧ�ò��ᷢ��,�ݲ�����
				Dimension newsize = new Dimension((int) dlgsize.getWidth(),
						(int) dlgsize.getHeight() + scry);
				dlg.setPreferredSize(newsize);
				dlg.setSize(newsize);
				scry = 0;
			}
			dlg.setLocation(scrx, scry);
		} else {
			// �����²�
			int scrx = (int) compscrpos.getX();
			int scry = (int) compscrpos.getY()
					+ (int) editorcomp.getPreferredSize().getHeight();
			if (scry + dlgsize.getHeight() > scrsize.getHeight()) {
				// ̫����,Ӧ�ò��ᷢ��,�ݲ�����
				Dimension newsize = new Dimension((int) dlgsize.getWidth(),
						(int) scrsize.getHeight() - scry - 25);
				dlg.setSize(newsize);
				dlg.setPreferredSize(newsize);
			}
			dlg.setLocation(scrx, scry);
		}

		dlg.setVisible(true);
		if (!dlg.ok) {
			return;
		}
		((CFormatTextField) editorcomp).setText(dlg.getResult());
		try {
			((CFormatTextField) editorcomp).commitEdit();
		} catch (Exception e) {

		}
	}

	/**
	 * ��ѯ�����ұߵı༭�򴥷���hov
	 */
	private boolean comp1firehov = false;

	/**
	 * ��ѯ������Χ,�ұߵ�����򴥷���hov
	 * 
	 * @author Administrator
	 * 
	 */
	class HovMouseListener1 implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			comp1firehov = true;
			Runnable r = new Runnable() {
				public void run() {
					editorcomp1.requestFocus();
					showHovDialog();
				}
			};
			SwingUtilities.invokeLater(r);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/*
	 * class QueryHovMouseListener implements MouseListener { public void
	 * mouseClicked(MouseEvent e) { }
	 * 
	 * public void mousePressed(MouseEvent e) { if (queryhovlistener == null)
	 * return; queryhovlistener.on_hov(DBColumnDisplayInfo.this,
	 * DBColumnDisplayInfo.this.getValue());
	 * 
	 * public void mouseReleased(MouseEvent e) { }
	 * 
	 * public void mouseEntered(MouseEvent e) { }
	 * 
	 * public void mouseExited(MouseEvent e) { } }
	 */

	/**
	 * ��������
	 */
	public String getColname() {
		return colname;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}

	/**
	 * ����������
	 * 
	 * @return
	 */
	public String getColtype() {
		return coltype;
	}

	/**
	 * ���ر���
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * ���ñ���
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * �����Ƿ�������
	 * 
	 * @return
	 */
	public boolean isIspk() {
		return ispk;
	}

	/**
	 * �����Ƿ�������
	 * 
	 * @param ispk
	 */
	public void setIspk(boolean ispk) {
		this.ispk = ispk;
	}

	/**
	 * ���ûس�������
	 * 
	 * @param comp
	 */
	protected void addEnterkeyTraver(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.add(enterkey);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);
	}

	/**
	 * ���ñ༭�ؼ���ǰֵ
	 * 
	 * @param v
	 */
	public void setValue(String v) {
		if (editorcomp instanceof CTextField) {
			((CTextField) editorcomp).setValue(v);
		} else if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setValue(v);
		} else if (editorcomp instanceof CComboBox) {
			((CComboBox) editorcomp).setValue(v);
		} else if (editorcomp instanceof CCheckBox) {
			((CCheckBox) editorcomp).setValue(v);
		} else if (editorcomp instanceof CTextArea) {
			((CTextArea) editorcomp).setText(v);
		}
	}

	/**
	 * ���ر༭�ؼ���ǰֵ
	 * 
	 * @return
	 */
	public String getValue() {
		if (editorcomp instanceof CTextField) {
			return (String) ((CTextField) editorcomp).getValue();
		} else if (editorcomp instanceof CFormatTextField) {
			return (String) ((CFormatTextField) editorcomp).getValue();
		} else if (editorcomp instanceof CComboBox) {
			return ((CComboBox) editorcomp).getValue();
		} else if (editorcomp instanceof CCheckBox) {
			return ((CCheckBox) editorcomp).getValue();
		} else if (editorcomp instanceof CTextArea) {
			return ((CTextArea) editorcomp).getText();
		} else {
			return "";
		}
	}

	/**
	 * ���ر༭�ؼ�
	 * 
	 * @return
	 */
	public JComponent getEditComponent() {
		if (editorcomp == null) {
			editorcomp = createComp();
			addEnterkeyTraver(editorcomp);
		}
		return editorcomp;

	}

	public JComponent getEditComponentwithoutKeytraver() {
		if (editorcomp == null) {
			editorcomp = createComp();
		}
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = editorcomp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		editorcomp.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, hasset);

		return editorcomp;

	}

	/**
	 * ���ز�ѯ������Χ�༭�ؼ�
	 * 
	 * @return
	 */
	public JComponent getEditComponent1() {
		if (editorcomp1 == null) {
			editorcomp1 = createComp();
		}
		return editorcomp1;

	}

	/**
	 * ���ñ༭�ؼ��ɱ༭
	 * 
	 * @param flag
	 */
	public void setEnable(boolean flag) {
		if (editorcomp == null) {
			editorcomp = createComp();
		}

		if (this.getColname().equals("stopcode")) {
			int m;
			m = 3;
		}

		if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setCanedit(flag);
		} else if (editorcomp instanceof CTextArea) {
			((CTextArea) editorcomp).setCanedit(flag);
		} else {
			editorcomp.setEnabled(flag);
		}

		if (!flag) {
			editorcomp.setBackground(new Color(0xf0, 0xf0, 0xf0));
		} else {
			editorcomp.setBackground(new Color(0xff, 0xff, 0xff));
		}

	}

	/**
	 * �����Ƿ������ݿ���
	 * 
	 * @return
	 */
	public boolean isDbcolumn() {
		return dbcolumn;
	}

	/**
	 * �����Ƿ������ݿ���
	 * 
	 * @param dbcolumn
	 */
	public void setDbcolumn(boolean dbcolumn) {
		this.dbcolumn = dbcolumn;
	}

	/**
	 * �����ܷ���Ϊ��ѯ����
	 * 
	 * @return
	 */
	public boolean isQueryable() {
		if (!isDbcolumn())
			return false;
		return queryable;
	}

	/**
	 * �����ܷ���Ϊ��ѯ����
	 * 
	 * @param queryable
	 */
	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}

	/**
	 * �����Ƿ�����ݿ����
	 * 
	 * @return
	 */
	public boolean isUpdateable() {
		return updateable;
	}

	/**
	 * �����Ƿ�����ݿ����
	 * 
	 * @param updateable
	 */
	public void setUpdateable(boolean updateable) {
		this.updateable = updateable;
	}

	/**
	 * ȡ���ݿ����к�����
	 * 
	 * @return
	 */
	public String getSeqname() {
		return seqname;
	}

	/**
	 * �������ݿ����к�����
	 * 
	 * @param seqname
	 */
	public void setSeqname(String seqname) {
		this.seqname = seqname;
	}

	/**
	 * ����label
	 * 
	 * @return
	 */
	public CLabel getLabel() {
		return label;
	}

	/**
	 * ������д��out
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeData(OutputStream out) throws Exception {
		CommandFactory.writeString(colname, out);
		CommandFactory.writeString(coltype, out);
		CommandFactory.writeString(title, out);
		CommandFactory.writeString(seqname, out);
		CommandFactory.writeShort(ispk ? 1 : 0, out);
		CommandFactory.writeShort(editorheight, out);
		CommandFactory.writeShort(editorwidth, out);
		CommandFactory.writeShort(linebreak ? 1 : 0, out);
		CommandFactory.writeShort(uppercase ? 1 : 0, out);
		CommandFactory.writeShort(readonly ? 1 : 0, out);
		CommandFactory.writeShort(withtime ? 1 : 0, out);
		CommandFactory.writeShort(updateable ? 1 : 0, out);
		CommandFactory.writeShort(dbcolumn ? 1 : 0, out);
		CommandFactory.writeShort(queryable ? 1 : 0, out);
		CommandFactory.writeShort(numberscale, out);
	}

	/**
	 * ��out����
	 * 
	 * @param in
	 * @throws Exception
	 */
	public void readData(InputStream in) throws Exception {
		colname = CommandFactory.readString(in);
		coltype = CommandFactory.readString(in);
		title = CommandFactory.readString(in);
		seqname = CommandFactory.readString(in);
		ispk = CommandFactory.readShort(in) == 1;
		editorheight = CommandFactory.readShort(in);
		editorwidth = CommandFactory.readShort(in);
		linebreak = CommandFactory.readShort(in) == 1;
		uppercase = CommandFactory.readShort(in) == 1;
		readonly = CommandFactory.readShort(in) == 1;
		withtime = CommandFactory.readShort(in) == 1;
		updateable = CommandFactory.readShort(in) == 1;
		dbcolumn = CommandFactory.readShort(in) == 1;
		queryable = CommandFactory.readShort(in) == 1;
		numberscale = CommandFactory.readShort(in);
	}

	public static String ROWCHECK_NOTNULL = "�ǿ�";

	/**
	 * �м����
	 * 
	 * @author Administrator
	 * 
	 */
	public class RowcheckInfo {
		String checktype = "";

		public RowcheckInfo() {
		}

		/**
		 * ��ʾ��Ϣ
		 */
		String infomessage = "";

		public String getChecktype() {
			if (checktype == null)
				checktype = "";
			return checktype;
		}

		public void setChecktype(String checktype) {
			if (checktype == null)
				checktype = "";
			this.checktype = checktype;
		}

		public String getInfomessage() {
			return infomessage;
		}

		public void setInfomessage(String infomessage) {
			this.infomessage = infomessage;
		}
	}

	/**
	 * ����hov
	 * 
	 * @return
	 */
	public CHovBase getHov() {
		return hov;
	}

	/**
	 * ���ڱ༭������EDITCOMP_COMBOBOX��,�������ֺ�ֵ��.
	 * 
	 * @param key
	 *            �ڲ�ֵ
	 * @param value
	 *            �ⲿ��ʾ
	 */
	public void addComboxBoxItem(String key, String value) {
		if (cbdbmodel == null) {
			cbdbmodel = createCbdbmodel();
		}

		int row = cbdbmodel.getRowCount();
		cbdbmodel.appendRow();
		cbdbmodel.setItemValue(row, "key", key);
		cbdbmodel.setItemValue(row, "value", value);
	}

	/**
	 * ��������ѡ�������Դ
	 * 
	 * @return
	 */
	DBTableModel createCbdbmodel() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		colinfos.add(new DBColumnDisplayInfo("key", "varchar", "key"));
		colinfos.add(new DBColumnDisplayInfo("value", "varchar", "value"));
		return new DBTableModel(colinfos);
	}

	public boolean isHidetitleoncard() {
		return hidetitleoncard;
	}

	public void setHidetitleoncard(boolean hidetitleoncard) {
		this.hidetitleoncard = hidetitleoncard;
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public DBColumnDisplayInfo copy() {
		DBColumnDisplayInfo n = new DBColumnDisplayInfo(colname, coltype,
				title, linebreak);
		n.ispk = ispk;
		n.editorcomp = null;
		n.editcomptype = editcomptype;
		n.editorheight = editorheight;
		n.editorwidth = editorwidth;
		n.linebreak = linebreak;
		n.uppercase = uppercase;
		n.readonly = readonly;
		n.withtime = withtime;
		n.updateable = updateable;
		n.queryable = queryable;
		n.numberscale = numberscale;
		n.seqname = seqname;
		n.focusable = focusable;
		n.hov = hov;
		n.label = null;
		n.initvalue = null;
		n.yearmonth = yearmonth;

		n.hovdefine = hovdefine; // /?????
		n.numberdisplayformat = numberdisplayformat;
		n.querymust = querymust;
		n.tablecolumnwidth = tablecolumnwidth;
		n.hidetitleoncard = hidetitleoncard;
		n.subqueryopid = subqueryopid;

		if (cbdbmodel != null) {
			for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
				String key = cbdbmodel.getItemValue(r, "key");
				String value = cbdbmodel.getItemValue(r, "value");
				n.addComboxBoxItem(key, value);
			}
		}

		return n;
	}

	/**
	 * ���ظ�ʽ���������.��Ҫ��number���͵��ֶ�С��λ����format
	 * 
	 * @param value
	 * @return
	 */
	public String getFormatvalue(String value) {
		if (value == null || value.length() == 0)
			return value;

		if (getColtype().equals(COLTYPE_NUMBER)) {
			// ����Ѿ�format����.����ȥ��������format
			value = value.replaceAll(",", "");
			if (numberdisplayformat != null && numberdisplayformat.length() > 0) {
				DecimalFormat df = new DecimalFormat(numberdisplayformat);
				return df.format(DecimalHelper.toDec(value));
			}

			if (getNumberscale() == 0) {
				return value;
			}
			BigDecimal bdec = null;
			try {
				value = value.replaceAll(",", "");
				bdec = new BigDecimal(value);
				bdec = bdec
						.setScale(getNumberscale(), BigDecimal.ROUND_HALF_UP);
				value = bdec.toPlainString();

				if (getNumberscale() > 0) {
					value = formatDig3(value);
				}

				return value;
			} catch (Exception ne) {
				return value;
			}
		} else if (getColtype().equals(COLTYPE_DATE)) {
			if (withtime) {
				if (value.length() == 10) {
					return value + " 00:00:00";
				}
			} else {
				if (value.length() > 10) {
					return value.substring(0, 10);
				}
			}
			return value;
		} else {
			return value;
		}
	}

	/**
	 * С����ǰ����λһ��һ������
	 * 
	 * @param s
	 */
	protected String formatDig3(String s) {
		int p = s.indexOf(".");
		if (p < 0)
			return s;
		String digs = s.substring(0, p);
		char firstc = digs.charAt(0);
		StringBuffer resultsb = new StringBuffer();
		if (firstc == '+' || firstc == '-') {
			resultsb.append(firstc);
			digs = digs.substring(1);
		}

		int index = 0;
		if (digs.length() > 3) {
			int morect = digs.length() % 3;
			if (morect > 0) {
				for (int i = 0; i < morect; i++) {
					resultsb.append(digs.charAt(index++));
				}
				resultsb.append(",");
			}
		}

		for (int i = 1; index < digs.length(); i++) {
			resultsb.append(digs.charAt(index++));
			if (i % 3 == 0 && index != digs.length()) {
				resultsb.append(",");
			}
		}

		resultsb.append(s.substring(p));
		return resultsb.toString();
	}

	/**
	 * ����ϵͳѡ���ֵ�
	 * 
	 * @param keyword
	 *            ϵͳѡ���ֵ�ؼ���
	 */
	public void setSystemddl(final String keyword) {
		JComponent editcomp = this.getEditComponent();
		if (!(editcomp instanceof CComboBox)) {
			logger.error(this.getColname()
					+ "�ı༭�ؼ�����CComboBox,���ܵ���setSystemddl");
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				RemoteDdlHelper rmthlp = new RemoteDdlHelper();//
				try {
					rmthlp.doSelect(keyword);
				} catch (Exception e) {
					logger.error("ERROR", e);
					return;
				}

				DBTableModel dbmodel = rmthlp.getDdlmodel();
				for (int i = 0; dbmodel != null && i < dbmodel.getRowCount(); i++) {
					String key = dbmodel.getItemValue(i, "ddlid");
					String value = dbmodel.getItemValue(i, "ddlname");
					addComboxBoxItem(key, value);
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

	}

	/**
	 * ��sql��������ѡ������Դ
	 * 
	 * @param sql
	 *            select ���
	 * @param keycolname
	 *            ��ֵ����
	 * @param valuecolname
	 *            ��ʾֵ����
	 */
	public void setSqlDdl(final String sql, final String keycolname,
			final String valuecolname) {
		if (DefaultNPParam.runonserver)
			return;

		JComponent editcomp = this.getEditComponent();
		if (!(editcomp instanceof CComboBox)) {
			logger.error(this.getColname() + "�ı༭�ؼ�����CComboBox,���ܵ���setSqlDdl");
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				RemotesqlHelper sqlhelper = new RemotesqlHelper();
				DBTableModel dbmodel = null;
				try {
					dbmodel = sqlhelper.doSelect(sql, 0, 100);
				} catch (Exception e) {
					logger.error("ERROR", e);
					return;
				}

				for (int i = 0; dbmodel != null && i < dbmodel.getRowCount(); i++) {
					String key = dbmodel.getItemValue(i, keycolname);
					String value = dbmodel.getItemValue(i, valuecolname);
					addComboxBoxItem(key, value);
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

	}

	/**
	 * �ڲ�ʹ��
	 * 
	 * @return
	 */
	public boolean isComp1firehov() {
		return comp1firehov;
	}

	/**
	 * �ڲ�ʹ��
	 * 
	 * @param comp1firehov
	 */
	public void setComp1firehov(boolean comp1firehov) {
		this.comp1firehov = comp1firehov;
	}

	/**
	 * HOV���õ����������ӿ�
	 */
	private HovcondIF hovcondif = null;

	/**
	 * ����HOV���õ����������ӿ�
	 * 
	 * @return
	 */
	public HovcondIF getHovcondif() {
		return hovcondif;
	}

	/**
	 * ����HOV���õ����������ӿ�
	 * 
	 * @param hovcondif
	 */
	public void setHovcondif(HovcondIF hovcondif) {
		this.hovcondif = hovcondif;
	}

	/*
	 * �����Ƿ����ļ���־ public boolean isFileflag() { return fileflag; }
	 * 
	 * public void setFileflag(boolean fileflag) { this.fileflag = fileflag; }
	 */

	/**
	 * �ͷ��ڴ�
	 */
	public void freeMemory() {
		if (editorcomp != null) {
			if (editorcomp instanceof CFormatTextField) {
				((CFormatTextField) editorcomp).freeMemory();
			}
			editorcomp = null;
		}
		if (editorcomp1 != null) {
			if (editorcomp1 instanceof CFormatTextField) {
				((CFormatTextField) editorcomp1).freeMemory();
			}
			editorcomp1 = null;
		}
		if (label != null) {
			label = null;
		}
		if (lbicon != null) {
			lbicon = null;
		}
		if (hov != null) {
			hov.freeMemory();
			hov = null;
		}
		if (rowcheck != null) {
			rowcheck = null;
		}

		if (hovdefine != null) {
			hovdefine = null;
		}

		if (cbdbmodel != null) {
			cbdbmodel.freeMemory();
			cbdbmodel = null;
		}
		hovlistener = null;
	}

	public static String dbcoltype2coltype(String dbcoltype) {
		if (dbcoltype.equals("�к�"))
			return dbcoltype;
		dbcoltype = dbcoltype.toLowerCase();
		if (dbcoltype.startsWith("number")) {
			return DBColumnDisplayInfo.COLTYPE_NUMBER;
		} else if (dbcoltype.startsWith("date")) {
			return DBColumnDisplayInfo.COLTYPE_DATE;
		} else {
			return DBColumnDisplayInfo.COLTYPE_VARCHAR;
		}

	}

	/**
	 * �Ƿ��ǽ���������
	 */
	private boolean crossdata = false;

	public boolean isCrossdata() {
		return crossdata;
	}

	public void setCrossdata(boolean crossdata) {
		this.crossdata = crossdata;
	}

	public void setHovlistener(HovListener hovlistener) {
		this.hovlistener = hovlistener;
	}

	public boolean isCalcsum() {
		return calcsum;
	}

	public void setCalcsum(boolean calcsum) {
		this.calcsum = calcsum;
	}

	public boolean isCanedit() {
		JComponent jcomp = getEditComponent();
		if (jcomp instanceof CFormatTextField) {
			boolean canedit = ((CFormatTextField) jcomp).isCanedit();
			if (!canedit)
				return false;
		} else if (jcomp instanceof CTextArea) {
			boolean canedit = ((CTextArea) jcomp).isCanedit();
			if (!canedit)
				return false;
		} else {
			boolean canedit = jcomp.isEnabled();
			if (!canedit)
				return false;
		}
		return true;
	}

	public String getNumberDisplayformat() {
		return numberdisplayformat;
	}

	public void setNumberDisplayformat(String displayformat) {
		this.numberdisplayformat = displayformat;
	}

	public void setHov(CHovBase hov) {
		this.hov = hov;
	}

	public boolean isQuerymust() {
		return querymust;
	}

	public void setQuerymust(boolean querymust) {
		this.querymust = querymust;
	}

	public int getTablecolumnwidth() {
		return tablecolumnwidth;
	}

	public void setTablecolumnwidth(int tablecolumnwidth) {
		this.tablecolumnwidth = tablecolumnwidth;
	}

	public static void main(String[] args) {
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("test", "number");
		col.setNumberscale(2);
		String s = col.getFormatvalue("-1230.67190");
		System.out.println(s);
	}

	public String getSubqueryopid() {
		return subqueryopid;
	}

	public void setSubqueryopid(String subqueryopid) {
		this.subqueryopid = subqueryopid;
	}

	
}
