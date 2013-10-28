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
 * 所有规则的基类 20071123
 * 
 * @author wwh
 * 
 */
public abstract class Rulebase {
	/**
	 * 该规则处理类,能否处理某个规则类型
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
	 * 本实例实现的ruletype
	 */
	protected String ruletype;

	/**
	 * 本实列实现的表达式,由规则自行解释
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
	 * 处理规则.
	 * 
	 * @param caller
	 *            调用者,可以是CStemodel CMdeModel,也能是SteProcessor MdeProcssor
	 * @return
	 * @throws Exception
	 */
	public int process(Object caller) throws Exception {
		return 0;
	}

	/**
	 * 分组
	 * 
	 * @param caller
	 * @return 返回分组方法
	 * @throws Exception
	 */
	public SplitGroupInfo processGroup(Object caller) throws Exception {
		return null;
	}

	/**
	 * 
	 * @param caller
	 *            调用者,可以是CStemodel CMdeModel,也能是SteProcessor MdeProcssor
	 * @param colname
	 *            触发列名
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
	 * 查询条件
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processWheres(Object caller) throws Exception {
		return "";
	}

	/**
	 * 图形化设置规则
	 * 
	 * @param caller
	 *            调用者
	 * @return
	 */
	public abstract boolean setupUI(Object caller) throws Exception;

	public String processSort(Object caller) throws Exception {
		return "";
	}

	/**
	 * 处理存调过程调用。
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processStoreproc(Object caller) throws Exception {
		return "";
	}
	
	
	/**
	 * 行检查
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
	 * 查询前处理
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public String processPrequerystoreproc(Object caller) throws Exception {
		return "";
	}

	/**
	 * 处理交叉表
	 * 
	 * @param dbmodel
	 * @return
	 */
	public DBTableModel processCrosstable(DBTableModel dbmodel,String displaycols[])
			throws Exception {
		return null;
	}

	/**
	 * 级联查询
	 * 
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public QuerylinkInfo processQuerylink(Object caller) throws Exception {
		return null;
	}

	/**
	 * 计算列
	 * @param caller
	 * @param row 大于等于0表示一行。-1表示所有行。
	 * @throws Exception
	 */
	public void processCalcColumn(CSteModel caller,int row)
			throws Exception {
	}

	/**
	 * 是否启用
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
	 * 查找工具条
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
	 * 隐藏
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

		// 删除
		while (tb.getComponentCount() > insertindex) {
			tb.remove(insertindex);
		}

		tb.placeButton(title, tips, action);

		// 将原来的加回来
		Enumeration<JComponent> en = comptable.elements();
		while (en.hasMoreElements()) {
			tb.add(en.nextElement());
		}

	}

}
