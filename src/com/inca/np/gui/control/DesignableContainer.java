package com.inca.np.gui.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.inca.np.gui.panedesign.Titleborderpane;

/**
 * 可以进行控件位置调整的container.
 * @author user
 *
 */
public class DesignableContainer  extends JPanel {
	CDialogUIManager uimanager=null;
	
	public DesignableContainer() {
		super();
	}
	
	public void setUIManager(CDialogUIManager uimanager){
		this.uimanager=uimanager;
		setLayout(uimanager);
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		super.setLayout(uimanager);
	}

	@Override
	public Component add(Component comp) {
		if(comp instanceof Titleborderpane){
			//same as common jcomponet
		}else if (comp instanceof JPanel) {
			addPanel(0, 0, (JPanel) comp);
			return null;
		}
		super.add(comp);
		//System.out.println(getComponentCount());
		return comp;
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		add(comp);
	}

	@Override
	public void add(Component comp, Object constraints) {
		add(comp);
	}

	@Override
	public Component add(String name, Component comp) {
		add(comp);
		return comp;
	}

	void addPanel(int startx, int starty, JPanel pane) {
		ArrayList<Component> arcp=new ArrayList<Component>();
		
		for (int i = 0; i < pane.getComponentCount(); i++) {
			Component comp = pane.getComponent(i);
			if (comp instanceof JLabel && comp.getName() != null
					&& comp.getName().equals("lbicon")) {
				continue;
			}
			if (comp instanceof JTextArea || comp instanceof CTextArea
					|| comp instanceof JComboBox || comp instanceof JCheckBox
					|| comp instanceof JComboBox || comp instanceof JLabel
					|| comp instanceof JTextField
					|| comp instanceof JButton
					|| comp instanceof Titleborderpane || comp instanceof JScrollPane) {
				arcp.add(comp);
			} else if (comp instanceof JPanel) {
				Rectangle rect = comp.getBounds();
				addPanel(startx + rect.x, starty + rect.y, (JPanel) comp);
			} else {
				// logger.error("cann't add " + comp.getClass().getName());
			}
		}
		
		for(int i=0;i<arcp.size();i++){
			DesignableContainer.this.add(arcp.get(i));				
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (uimanager != null && uimanager.isUseformUI()) {
			return uimanager.getPreferredSize();
		} else {
			return super.getPreferredSize();
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
}