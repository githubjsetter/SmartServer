package com.smart.platform.rule.define;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.QuerylinkInfo;
import com.smart.platform.gui.tbar.TBar;

/**
 * ���й���Ļ��� 20071123
 * 
 * @author wwh
 * 
 */
public abstract class Rulebase {
	/**
	 * �ù�������,�ܷ���ĳ����������
	 * 
	 * @param ruletype
	 * @return
	 */
	public static boolean canProcessruletype(String ruletype) {
		return false;
	}

	public static String[] getRuleypes() {
		return null;
	}

	/**
	 * ��ʵ��ʵ�ֵ�ruletype
	 */
	protected String ruletype;

	/**
	 * ��ʵ��ʵ�ֵı��ʽ,�ɹ������н���
	 */
	protected String expr = "";

	public String getRuletype() {
		return ruletype;
	}

	public void setRuletype(String ruletype) {
		this.ruletype = ruletype;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	/**
	 * �������.
	 * 
	 * @param caller
	 *            ������,������CStemodel CMdeModel,Ҳ����SteProcessor MdeProcssor
	 * @return
	 * @throws Exception
	 */
	public int process(Object caller) throws Exception {
		return 0;
	}

	/**
	 * ����
	 * 
	 * @param caller
	 * @return ���ط��鷽��
	 * @throws Exception
	 */
	public SplitGroupInfo processGroup(Object caller) throws Exception {
		return null;
	}

	/**
	 * 
	 * @param caller
	 *            ������,������CStemodel CMdeModel,Ҳ����SteProcessor MdeProcssor
	 * @param colname
	 *            ��������
	 * @return
	 * @throws Exception
	 */
	public int process(Object caller, int row, String colname) throws Exception {
		return 0;
	}

	public int process(Object caller, int row) throws Exception {
		return 0;
	}

	public Color processColor(Object caller, int row) throws Exception {
		return null;
	}

	/**
	 * ��ѯ����
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processWheres(Object caller) throws Exception {
		return "";
	}

	/**
	 * ͼ�λ����ù���
	 * 
	 * @param caller
	 *            ������
	 * @return
	 */
	public abstract boolean setupUI(Object caller) throws Exception;

	public String processSort(Object caller) throws Exception {
		return "";
	}

	/**
	 * ���������̵��á�
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processStoreproc(Object caller) throws Exception {
		return "";
	}
	
	
	/**
	 * �м��
	 * @param caller
	 * @param row
	 * @return
	 * @throws Exception
	 */
	public String processRowcheck(Object caller, int row)
		throws Exception {
		return "";
	}
	


	/**
	 * ��ѯǰ����
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processPrequerystoreproc(Object caller) throws Exception {
		return "";
	}

	/**
	 * �������
	 * 
	 * @param dbmodel
	 * @return
	 */
	public DBTableModel processCrosstable(DBTableModel dbmodel,String displaycols[])
			throws Exception {
		return null;
	}

	/**
	 * ������ѯ
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public QuerylinkInfo processQuerylink(Object caller) throws Exception {
		return null;
	}

	/**
	 * ������
	 * @param caller
	 * @param row ���ڵ���0��ʾһ�С�-1��ʾ�����С�
	 * @throws Exception
	 */
	public void processCalcColumn(CSteModel caller,int row)
			throws Exception {
	}

	/**
	 * �Ƿ�����
	 */
	boolean use = true;

	public boolean isUse() {
		return use;
	}

	public void processItemvaluechanged(Object caller,int row,String column,String value) throws Exception {
	}
	public void setUse(boolean use) {
		this.use = use;
	}

	/**
	 * ���ҹ�����
	 */
	CStetoolbar  searchToolbar(JPanel rootpanel){
		Component[] comps=rootpanel.getComponents();
		for(int i=0;i<comps.length;i++){
			Component o=comps[i];
			if(o instanceof CStetoolbar){
				return (CStetoolbar)o;
			}else if(o instanceof JPanel){
				JPanel jp=(JPanel)o;
				Component[] comps1=jp.getComponents();
				for(int j=0;j<comps1.length;j++){
					if(comps1[j] instanceof CStetoolbar){
						return (CStetoolbar)comps1[j];
					}
				}
			}
			
		}
		return null;
	}

	/**
	 * ����
	 * @param toolbar
	 * @param hideaction
	 */

	protected void hideButton(JComponent toolbar,String hideaction){
		Component comps[]=toolbar.getComponents();
		for(int i=0;i<comps.length;i++){
			if(comps[i] instanceof JButton){
				JButton btn=(JButton)comps[i];
				if(btn.getActionCommand().equals(hideaction)){
					btn.setVisible(false);
				}
			}
		}
	}

	protected void addButton(CStetoolbar tb, String title, String tips,
			String action) {
		int insertindex = -1;
		for (int i = 0; i < tb.getComponentCount(); i++) {
			JComponent comp = (JComponent) tb.getComponent(i);
			if (comp instanceof CToolbar.Separator) {
				insertindex = i;
				break;
			}
		}
		if (insertindex < 0)
			return;

		Vector<JComponent> comptable = new Vector<JComponent>();
		for (int i = insertindex; i < tb.getComponentCount(); i++) {
			JComponent comp = (JComponent) tb.getComponent(i);
			comptable.add(comp);
		}

		// ɾ��
		while (tb.getComponentCount() > insertindex) {
			tb.remove(insertindex);
		}

		tb.placeButton(title, tips, action);

		// ��ԭ���ļӻ���
		Enumeration<JComponent> en = comptable.elements();
		while (en.hasMoreElements()) {
			tb.add(en.nextElement());
		}

	}

}
