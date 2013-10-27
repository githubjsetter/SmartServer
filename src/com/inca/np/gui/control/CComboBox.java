package com.inca.np.gui.control;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Category;

import com.inca.np.communicate.RemoteDdlHelper;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 10:08:44
 * To change this template use File | Settings | File Templates.
 */
public class CComboBox extends JComboBox {

	Category logger = Category.getInstance(CComboBox.class);

	public CComboBox(CComboBoxModel aModel) {
		super(aModel);
		this.dataModel = aModel;
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	/**
	 * Creates a <code>JComboBox</code> that contains the elements in the
	 * specified array. By default the first item in the array (and therefore
	 * the data model) becomes selected.
	 * 
	 * @param items
	 *            an array of objects to insert into the combo box
	 * @see DefaultComboBoxModel
	 */
	public CComboBox(final Object items[]) {
		super(items);
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	/**
	 * Creates a <code>JComboBox</code> that contains the elements in the
	 * specified array. By default the first item in the array (and therefore
	 * the data model) becomes selected.
	 * 
	 * @param items
	 *            an array of objects to insert into the combo box
	 * @param key
	 *            set selected if exists
	 * @see DefaultComboBoxModel
	 */
	public CComboBox(final Object items[], String key) {
		this(items);
		if (key == null)
			return;
		for (int i = 0; i < 0; i++) {
			Object item = items[i];
			if (item == null)
				continue;
			boolean found = false;
		}
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	/**
	 * Creates a <code>JComboBox</code> that contains the elements in the
	 * specified Vector. By default the first item in the vector and therefore
	 * the data model) becomes selected.
	 * 
	 * @param items
	 *            an array of vectors to insert into the combo box
	 * @see DefaultComboBoxModel
	 */
	public CComboBox(Vector items) {
		super(items);
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	/**
	 * Creates a <code>JComboBox</code> with a default data model. The default
	 * data model is an empty list of objects. Use <code>addItem</code> to add
	 * items. By default the first item in the data model becomes selected.
	 * 
	 * @see DefaultComboBoxModel
	 */
	public CComboBox() {
		super();
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	// CComboBoxModel ccbModel = null;

	public void setModel(CComboBoxModel ccbModel) {
		// 查询现在值,来确定设置的index
		String curv = this.getValue();
		if (curv == null)
			curv = "";
		super.setModel(ccbModel);
		if (ccbModel.getSize() > 0) {
			int targetindex = 0;
			for (int r = 0; r < ccbModel.getSize(); r++) {
				if (curv.equals(ccbModel.getKeyvalue(r))) {
					targetindex = r;
					break;
				}
			}
			setSelectedIndex(targetindex);
			/*
			 * 在这里触发修改是不对的,没必要 fireItemStateChanged(new ItemEvent(this,
			 * ItemEvent.ITEM_STATE_CHANGED, selectedItemReminder,
			 * ItemEvent.SELECTED));
			 */

			// 20071012 强制选一
			if (this.getSelectedIndex() < 0) {
				this.setSelectedIndex(0);
			}
		}

	}

	/** ********************************************************************** */

	/**
	 * 取当前值.如果model是CComboBoxModel,取CComboBoxModel的keyvalue
	 * 
	 * @return
	 */
	public String getValue() {
		int index = this.getSelectedIndex();
		if (index < 0) {
			return null;
		}

		if (dataModel != null) {
			return ((CComboBoxModel) dataModel).getKeyvalue(index);
		} else {
			return (String) getItemAt(index);
		}
	}

	public void setValue(String v) {
		if (v == null || v.trim().length() == 0) {
			if (getItemCount() > 0) {
				setSelectedIndex(0);
			}
			return;
		}
		ComboBoxModel cbmodel = this.getModel();
		if (cbmodel instanceof CComboBoxModel) {
			CComboBoxModel m = (CComboBoxModel) cbmodel;
			int index = m.getKeyIndex(v);
			if (index >= 0) {
				// int size = dataModel.getSize();
				if (getSelectedIndex() != index)
					setSelectedIndex(index);
			} else {
				m.insertElementAt(v, 0);
			}
		} else {
			DefaultComboBoxModel defmodel = (DefaultComboBoxModel) cbmodel;
			for (int i = 0; i < defmodel.getSize(); i++) {
				String modelvalue = (String) defmodel.getElementAt(i);
				if (modelvalue.equals(v)) {
					this.setSelectedIndex(i);
					return;
				}
			}
			((DefaultComboBoxModel) cbmodel).insertElementAt(v, 0);
			this.setSelectedIndex(0);
		}
	}

	public void setSystemddl(String keyword, String keycolname,
			String valuecolname) {
		RemoteDdlHelper rmthlp = new RemoteDdlHelper();//
		try {
			rmthlp.doSelect(keyword);
		} catch (Exception e) {
			logger.error("ERROR", e);
			return;
		}
		DBTableModel dbmodel = rmthlp.getDdlmodel();
		CComboBoxModel ccbmodel = new CComboBoxModel(dbmodel, keycolname,
				valuecolname);
		this.setModel(ccbmodel);
	}
	
	

	/*
	 * @Override public boolean hasFocus() { // TODO Auto-generated method stub
	 * //return true; return super.hasFocus(); }
	 */

	boolean settingvalue=false;
	@Override
	public void setSelectedItem(Object anObject) {
		settingvalue=true;
		super.setSelectedItem(anObject);
		settingvalue=false;
	}
	
	public boolean isSettingvalue(){
		return settingvalue;
	}



	Color activebgcolor = null;

	@Override
	public Color getBackground() {
		if (hasFocus()) {
			if (activebgcolor == null) {
				Color cc = UIManager.getColor("List.selectionBackground");
				activebgcolor = new Color(cc.getRed(), cc.getGreen(), cc
						.getBlue());
			}
			return activebgcolor;
		} else {
			return super.getBackground();
		}
	}

	@Override
	public void addActionListener(ActionListener l) {
		// TODO Auto-generated method stub
		super.addActionListener(l);
	}

} // CComboBox
