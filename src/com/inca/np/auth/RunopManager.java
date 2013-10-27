package com.inca.np.auth;

import com.inca.adminclient.gui.AdminClientframe;
import com.inca.np.gui.ste.CModelBase;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.runop.Runmessage;
import com.inca.npclient.system.Clientframe;

import javax.swing.*;

import java.awt.Cursor;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-7-10 Time: 11:42:20
 * To change this template use File | Settings | File Templates.
 */
public class RunopManager {
	/**
	 * 已运行的功能。
	 */
	private static Vector<CModelBase> opmodels = new Vector<CModelBase>();

	private static CFrame mainmenuframe = null;

	private static CModelBase activemodel = null;

	public static void setMainmenuframe(CFrame mainmenuframe) {
		RunopManager.mainmenuframe = mainmenuframe;
	}

	/**
	 * 功能开始运行
	 * 
	 * @param model
	 */
	public static void startRun(CModelBase model) {
		opmodels.add(model);
		activemodel = model;
		onRunopchanged();
	}

	/**
	 * 取当前活动的功能
	 * 
	 * @return
	 */
	public static CModelBase getActivemodel() {
		return activemodel;
	}

	/**
	 * 功能结束
	 * 
	 * @param model
	 */
	public static void stopRun(CModelBase model) {
		opmodels.removeElement(model);
		onRunopchanged();
	}

	public static String[] getOpnames() {
		ArrayList ar = new ArrayList();
		Enumeration<CModelBase> en = opmodels.elements();
		while (en.hasMoreElements()) {
			CModelBase opmodel = en.nextElement();
			ar.add(opmodel.getTitle());
		}

		String opnames[] = new String[ar.size()];
		ar.toArray(opnames);
		return opnames;
	}

/*	public static void activeOp(String opname) {
		Enumeration<CModelBase> en = opmodels.elements();
		while (en.hasMoreElements()) {
			CModelBase opmodel = en.nextElement();
			if (opmodel.getTitle().equals(opname)) {
				opmodel.getParentFrame().requestFocus();
			}
		}
	}
*/
/*	public static void selectOp() {
		if (mainmenuframe != null) {
			mainmenuframe.requestFocus();
		}
	}
*/
	public static void onRunopchanged() {
		String[] opnames = getOpnames();
		Enumeration<CModelBase> en = opmodels.elements();
		while (en.hasMoreElements()) {
			CModelBase opmodel = en.nextElement();
			opmodel.onRunopschanged(opnames);
		}
	}

	public static int mousex = 0;
	public static int mousey = 0;

	/**
	 * 显示总体的调试信息
	 * 
	 * @param msg
	 */
	public static void infoMessage(String msg) {
		if (mainmenuframe == null) {
			return;
		}

		Runmessage runmessage = new Runmessage(msg);
		if(mainmenuframe instanceof Clientframe){
			((Clientframe)mainmenuframe).runMessage(runmessage);
		}else if(mainmenuframe instanceof AdminClientframe){
			((AdminClientframe)mainmenuframe).runMessage(runmessage);
		}
	}

	public static void errorMessage(String msg) {
		if (mainmenuframe == null) {
			return;
		}

		Runmessage runmessage = new Runmessage(msg, Runmessage.MESSAGE_ERROR);
		if(mainmenuframe instanceof Clientframe){
			((Clientframe)mainmenuframe).runMessage(runmessage);
		}else if(mainmenuframe instanceof AdminClientframe){
			((AdminClientframe)mainmenuframe).runMessage(runmessage);
		}
	}

	public static void setWaitcursor() {
		if (activemodel == null)
			return;
		if (activemodel instanceof CSteModel) {
			((CSteModel) activemodel).getParentFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}

	public static void setDefaultcursor() {
		if (activemodel == null)
			return;
		if (activemodel instanceof CSteModel) {
			((CSteModel) activemodel).getParentFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
