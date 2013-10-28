package com.smart.platform.rule.enginee;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.QuerylinkInfo;
import com.smart.platform.rule.define.CComboboxDropdownRule;
import com.smart.platform.rule.define.ForbidnewRule;
import com.smart.platform.rule.define.Initrule;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.define.TablecaneditRule;
import com.smart.platform.rule.define.TablemultiselectRule;

/**
 * ��������
 * 
 * @author Administrator
 * 
 */
public class Ruleenginee {

	Category logger = Category.getInstance(Ruleenginee.class);
	public static int debug = 0;
	/**
	 * �����
	 */
	Vector<Rulebase> ruletable = new Vector<Rulebase>();

	public Vector<Rulebase> getRuletable() {
		return ruletable;
	}

	public void setRuletable(Vector<Rulebase> ruletable) {
		this.ruletable = ruletable;
	}

	public Ruleenginee() {
		// ����һ��debug

		if (debug == 1) {
			CComboboxDropdownRule cbddlrule = new CComboboxDropdownRule();
			cbddlrule.setRuletype("��������ѡ��");
			cbddlrule.setExpr("usestatus:0:ͣ��:1:��ʽ");
			ruletable.add(cbddlrule);

			TablecaneditRule terule = new TablecaneditRule();
			terule.setRuletype("�����Ա༭");
			ruletable.add(terule);

			TablemultiselectRule tmrule = new TablemultiselectRule();
			tmrule.setRuletype("����ѡ");
			ruletable.add(tmrule);

			Initrule initrule = new Initrule();
			initrule.setRuletype("���ó�ֵ");
			// initrule.setExpr("credate:��ǰʱ��");
			initrule.setExpr("goodsid:��ǰ��ԱID");
			ruletable.add(initrule);

			ForbidnewRule fidnewrule = new ForbidnewRule();
			fidnewrule.setRuletype("��������");
			ruletable.add(fidnewrule);

		}
	}

	/**
	 * �������
	 * 
	 * @param caller
	 * @return 0 ���� -1 ��ʾ��ֹ
	 */
	public int process(Object caller, String ruletype) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					if (r.process(caller) < 0)
						return -1;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return 0;
	}

	/**
	 * �������
	 * 
	 * @param caller
	 * @return 0 ���� -1 ��ʾ��ֹ
	 */
	public int process(Object caller, String ruletype, int row) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					if (r.process(caller, row) < 0)
						return -1;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return 0;
	}

	/**
	 * ������ʽ
	 */
	public int process(Object caller, String ruletype, int row,
			String editingcolname) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					if (r.process(caller, row, editingcolname) < 0)
						return -1;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return 0;
	}

	/**
	 * �м��
	 * @param caller
	 * @param ruletype
	 * @param row
	 * @param editingcolname
	 * @return
	 */
	public String processRowcheck(Object caller, String ruletype, int row) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					return r.processRowcheck(caller, row);
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return "";
	}

	/**
	 * where����
	 * 
	 * @param caller
	 * @param ruletype
	 * @return
	 */
	public String processWhere(Object caller, String ruletype) {
		String wheres = null;
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					String s = r.processWheres(caller);
					if (s == null || s.length() == 0)
						continue;
					if (wheres == null) {
						wheres = s;
					} else {
						wheres += " and " + s;
					}
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return wheres;
	}

	public String processSort(Object caller, String ruletype) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					String s = r.processSort(caller);
					if (s == null || s.length() == 0)
						continue;
					return s;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return null;
	}

	public String processStoreproc(Object caller, String ruletype) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					String s = r.processStoreproc(caller);
					if (s == null || s.length() == 0)
						continue;
					return s;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return null;
	}

	public String processPrequerystoreproc(Object caller, String ruletype) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					String s = r.processPrequerystoreproc(caller);
					if (s == null || s.length() == 0)
						continue;
					return s;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return null;
	}

	public Vector<SplitGroupInfo> processGroup(Object caller, String ruletype) {
		Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					SplitGroupInfo gpinfo = r.processGroup(caller);
					groupinfos.add(gpinfo);
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		if (groupinfos.size() > 0) {
			return groupinfos;
		}
		return null;
	}

	/**
	 * ������ɫ
	 * 
	 * @param caller
	 * @param ruletype
	 * @param row
	 * @return
	 */
	public Color processColor(Object caller, String ruletype, int row) {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					Color c = r.processColor(caller, row);
					if (c != null)
						return c;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return null;
	}

	public DBTableModel processCrosstable(DBTableModel dbmodel, String[] dispcols,String ruletype)
			throws Exception {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					DBTableModel crossdbmodel = r.processCrosstable(dbmodel,dispcols);
					if (crossdbmodel != null)
						return crossdbmodel;
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		return null;
	}

	public Vector<QuerylinkInfo> processQuerylink(Object caller, String ruletype) {
		Vector<QuerylinkInfo> qlinfos = new Vector<QuerylinkInfo>();
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					QuerylinkInfo qlinfo = r.processQuerylink(caller);
					if (qlinfo != null) {
						qlinfos.add(qlinfo);
					}
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
		if (qlinfos.size() > 0)
			return qlinfos;
		return null;
	}

	public void processCalcColumn(CSteModel ste, String ruletype, int row)
			throws Exception {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					r.processCalcColumn(ste, row);
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
	}

	public void processItemvaluechanged(CSteModel ste, String ruletype,
			int row, String triggercolname, String colvalue) throws Exception {
		Enumeration<Rulebase> en = ruletable.elements();
		while (en.hasMoreElements()) {
			Rulebase r = en.nextElement();
			if (!r.isUse())
				continue;
			if (r.getRuletype().equals(ruletype)) {
				try {
					r.processItemvaluechanged(ste, row,triggercolname, colvalue);
				} catch (Exception e) {
					logger.error("ERROR", e);
					continue;
				}
			}
		}
	}

}
