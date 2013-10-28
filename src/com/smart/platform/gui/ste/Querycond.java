package com.smart.platform.gui.ste;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;

import com.smart.platform.filedb.FiledbSearchCond;
import com.smart.platform.gui.control.CFormatTextField;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.CQueryDialog;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.HovListener;
import com.smart.platform.gui.control.HovcondIF;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-30 Time: 10:55:18
 * ��ѯ��������һ��Querycondline���
 */
public class Querycond extends Vector<Querycondline> implements HovListener {
	HovcondIF hovcond = null;
	HovcondIF detailhovcond = null;
	
	
	CQueryDialog querydlg=null;
	
	/**
	 * �ܵ��򵥱����չ����
	 */
	String advwheres="";
	
	/**
	 * ϸ���Ļ�չ����
	 */
	String advdtlwheres="";

	/**
	 * ���캯��
	 */
	public Querycond() {
	}

	/**
	 * ���ز�ѯ��������
	 * 
	 * @return
	 */
	public int getCondlinecount() {
		return size();
	}

	public HovcondIF getHovcond(String mdflag) {
		if ("m".equals(mdflag))
			return hovcond;
		else
			return detailhovcond;
	}

	public void setHovcond(HovcondIF hovcond) {
		this.hovcond = hovcond;
	}

	public void setDetailHovcond(HovcondIF hovcond) {
		this.detailhovcond = hovcond;
	}

	/**
	 * ���ݶ����ѯ����,�ϳɵĲ�ѯwhere����
	 * 
	 * @return
	 */
	public String getWheres() {
		StringBuffer sb = new StringBuffer();

		Enumeration<Querycondline> en = elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			if (!"m".equals(condline.getMdflag()))
				continue;
			String wheres = condline.getWheres();
			if (wheres.length() > 0) {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				sb.append(wheres);
			}
		}
		
		if(advwheres.length()>0){
			if (sb.length() > 0) {
				sb.append(" and ");
			}
			sb.append(advwheres);
		}
		
		return sb.toString();
	}

	/**
	 * ȡϸ���е�����
	 * 
	 * @return
	 */
	public String getDetailWheres() {
		StringBuffer sb = new StringBuffer();

		Enumeration<Querycondline> en = elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			if (!"d".equals(condline.getMdflag()))
				continue;
			String wheres = condline.getWheres();
			if (wheres.length() > 0) {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				sb.append(wheres);
			}
		}
		if(advdtlwheres.length()>0){
			if (sb.length() > 0) {
				sb.append(" and ");
			}
			sb.append(advdtlwheres);
		}

		return sb.toString();
	}

	/**
	 * ��������HOV�Ĳ�ѯ����
	 * 
	 * @return
	 */
	public String getHovWheres() {
		StringBuffer sb = new StringBuffer();

		Enumeration<Querycondline> en = elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			String wheres = condline.getHovWheres();
			if (wheres.length() > 0) {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				sb.append(wheres);
			}
		}
		return sb.toString();
	}

	/**
	 * ���������ļ���ѯ������
	 * 
	 * @return
	 */
	public FiledbSearchCond[] getFiledbCond() {
		ArrayList<FiledbSearchCond> conds = new ArrayList<FiledbSearchCond>();
		Enumeration<Querycondline> en = elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			FiledbSearchCond filedbcond = condline.getFiledbCond();
			if (filedbcond != null) {
				conds.add(filedbcond);
			}
		}

		FiledbSearchCond ar[] = new FiledbSearchCond[conds.size()];
		conds.toArray(ar);
		return ar;
	}

	/**
	 * ������ȡ��ѯ��������
	 * 
	 * @param colname
	 * @return
	 */

	public Querycondline getQuerycondline(String colname) {
		Enumeration<Querycondline> en = this.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = en.nextElement();
			if (condline.getColname().equalsIgnoreCase(colname)) {
				return condline;
			}
		}
		return null;
	}

	/**
	 * HOVѡ�񷵻�.
	 * 
	 * @param dispinfo
	 *            �ж���
	 * @param result
	 *            hov���ؽ��.ͨ��ֻ��һ����¼
	 */
	public void on_hov(DBColumnDisplayInfo dispinfo, DBTableModel result) {
		Hovdefine hovdef = dispinfo.getHovdefine();

		Iterator<String> it = hovdef.colpairmap.keySet().iterator();
		// ����dispinfo
		while (it.hasNext()) {
			String hovcolname = it.next();
			String dbcolname = hovdef.colpairmap.get(hovcolname);
			String hovvalue = null;
			if (dbcolname.equalsIgnoreCase(dispinfo.getColname())) {
				hovvalue = result.getItemValue(0, hovcolname);

				Enumeration<Querycondline> en = this.elements();
				while (en.hasMoreElements()) {
					Querycondline ql = en.nextElement();
					if (ql.getColname().equals(dispinfo.getColname())) {
						JComponent comp = null;
						if (dispinfo.isComp1firehov()) {
							comp = ql.getDbcolumndisplayinfo()
									.getEditComponent1();
						} else {
							comp = ql.getDbcolumndisplayinfo()
									.getEditComponent();
						}

						if (comp instanceof CFormatTextField) {
							if (ql.getOpvalue().equals("�о�in")) {
								String oldvalue = (String) ((CFormatTextField) comp)
										.getValue();
								if (oldvalue == null)
									oldvalue = "";
									hovvalue = mergeString(oldvalue,hovvalue);

							}
							((CFormatTextField) comp).setValue(hovvalue);
							ql.getCbUse().setSelected(true);
						}
						break;
					}
				}
				break;
			}
		}

		// ID��Ҳ����

		it = hovdef.colpairmap.keySet().iterator();
		while (it.hasNext()) {
			String hovcolname = it.next();
			if(hovcolname.equalsIgnoreCase(dispinfo.getColname())){
				continue;
			}
			if (!hovcolname.toLowerCase().endsWith("id")) {
				continue;
			}
			String dbcolname = hovdef.colpairmap.get(hovcolname);
			if (dbcolname == null || dbcolname.length() == 0) {
				continue;
			}

			Enumeration<Querycondline> en = this.elements();
			while (en.hasMoreElements()) {
				Querycondline ql = en.nextElement();
				if (dbcolname.equalsIgnoreCase(ql.getColname())) {
					JComponent comp = null;
					if (dispinfo.isComp1firehov()) {
						comp = ql.getDbcolumndisplayinfo().getEditComponent1();
					} else {
						comp = ql.getDbcolumndisplayinfo().getEditComponent();
					}

					// System.out.println("set hov value,
					// comp="+comp+",colname="+ql.getColname()+",hovalue="+hovvalue);
					if (comp instanceof CFormatTextField) {
						String hovvalue = result.getItemValue(0, hovcolname);
						if (ql.getOpvalue().equals("�о�in")) {
							String oldvalue = (String) ((CFormatTextField) comp)
									.getValue();
							if (oldvalue == null)
								oldvalue = "";
								hovvalue = mergeString(oldvalue,hovvalue);

						}

						
						((CFormatTextField) comp).setValue(hovvalue);
						ql.getCbUse().setSelected(true);
					}
				}
			}
		}
		dispinfo.getHov().hide();
	}

	/**
	 * ĳ��ѯ�����༭�ؼ��õ���focus
	 */
	public void gainFocus(DBColumnDisplayInfo dispinfo) {
		Enumeration<Querycondline> en = this.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = en.nextElement();
			if (condline.getDbcolumndisplayinfo() != dispinfo) {
				condline.hideHov();
			}
		}
	}

	/**
	 * ĳ��ѯ�����༭�ؼ�ʧȥ��focus
	 */
	public void lostFocus(DBColumnDisplayInfo dispinfo) {

	}

	/**
	 * �ͷ��ڴ�
	 */
	public void freeMemory() {
		Enumeration<Querycondline> en = this.elements();
		while (en.hasMoreElements()) {
			Querycondline cl = en.nextElement();
			cl.freeMemory();
		}
		this.removeAllElements();
	}

	/**
	 * ������в�ѯ�����༭�ؼ�
	 */
	public void clearControl() {
		Enumeration<Querycondline> en = this.elements();
		while (en.hasMoreElements()) {
			Querycondline cl = en.nextElement();
			cl.clearControl();
		}
	}
	
	String mergeString(String s,String news){
		String ss[]=s.split(",");
		for(int i=0;i<ss.length;i++){
			if(news.equals(ss[i])){
				return s;
			}
		}
		if(s.length()>0){
			s+=",";
		}
		return s+news;
	}


	public void setQuerydlg(CQueryDialog querydlg) {
		this.querydlg = querydlg;
	}

	public String getAdvwheres() {
		return advwheres;
	}

	public void setAdvwheres(String advwheres) {
		this.advwheres = advwheres;
	}

	public String getAdvdtlwheres() {
		return advdtlwheres;
	}

	public void setAdvdtlwheres(String advdtlwheres) {
		this.advdtlwheres = advdtlwheres;
	}

	
}
