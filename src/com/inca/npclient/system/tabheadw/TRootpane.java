package com.inca.npclient.system.tabheadw;

import java.awt.Point;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.RootPaneUI;

import com.inca.npclient.system.Clientframe;

public class TRootpane extends JRootPane {


	private TRootpaneUI trootui;

	public TRootpane() {
		super();
	}

	public void init() {
		setWindowDecorationStyle(FRAME);
	}

	@Override
	public void updateUI() {
		setUI((RootPaneUI) UIManager.getUI(this));
		trootui = new TRootpaneUI();
		setUI(trootui);

	}

	public Window getWindow() {
		return SwingUtilities.getWindowAncestor(this);
	}

	/**
	 * 激活了某页
	 * @param index
	 */
	public void onActiveIndex(int index){
		Window w=getWindow();
		if(w instanceof Clientframe){
			Clientframe cf=(Clientframe)w;
			cf.onActiveIndex(index);
		}
	}
	
	/**
	 * 关了某页
	 * @param closeindex  关闭哪页
	 * @param activeindex 现在激活是的哪页
	 */
	public void onCloseIndex(String opid,int closeindex,int activeindex){
		Window w=getWindow();
		if(w instanceof Clientframe){
			Clientframe cf=(Clientframe)w;
			cf.onCloseIndex(opid,closeindex,activeindex);
		}
	}

	public void addTab(String opid,String opname) {
		trootui.getTitlepane().addTabindex(opid,opname);
	}

	public void closeTab(int closeindex) {
		trootui.getTitlepane().docloseTab(closeindex);
	}
	
	public void setActiveindex(int activeindex) {
		trootui.getTitlepane().setActiveindex(activeindex);
	}

	public void onPopupmenu(Point point) {
		Window w=getWindow();
		if(w instanceof Clientframe){
			Clientframe cf=(Clientframe)w;
			cf.onPopupmenu(point);
		}
	}
	
	public TabbedTitlePane getTitlepane(){
		return trootui.getTitlepane();
	}
}
