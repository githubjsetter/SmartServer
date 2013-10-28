package com.smart.platform.gui.mde;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.apache.log4j.Category;

import com.smart.extension.mde.CMdeModelAp;
import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.SysinfoAction;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-11 Time: 17:09:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class MdeFrame extends COpframe {
	protected CMdeModel mdemodel = null;

	Category logger = Category.getInstance(MdeFrame.class);

	protected JSplitPane splitpane;

	public MdeFrame() throws HeadlessException {
		super();
	}

	public MdeFrame(String title) throws HeadlessException {
		super(title);
	}

	protected abstract CMdeModel getMdeModel();

    public CMdeModel createMdemodel(){
    	return mdemodel = getMdeModel();
    }

	
	protected void initControl() {
		mdemodel = getMdeModel();

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				mdemodel.getMasterModel().getRootpanel(), mdemodel
						.getDetailModel().getRootpanel());
		splitpane.setBorder(BorderFactory.createEmptyBorder());
		splitpane.setDividerLocation(getDividerLocation());
		InputMap inputMap = splitpane
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap = inputMap.getParent();
		inputMap.clear();

		cp.add(splitpane, BorderLayout.CENTER);

		setHotkey();

		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);

		mdemodel.onstartRun();
	}

	protected void setHotkey() {
		MdeControlFactory.setHotkey((JComponent) getContentPane(), mdemodel);
    	SysinfoAction.installHotkey((JComponent) getContentPane());
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (0 != mdemodel.on_beforeclose()) {
				return;
			}
			mdemodel.onstopRun();
		}
		super.processWindowEvent(e);
	}

	public void setupAp(String roleid) {
		if (!(mdemodel instanceof CMdeModelAp)){
    		warnMessage("提示","本功能不能设置授权属性");
			return;
		}
		((CMdeModelAp) mdemodel).setupAp(roleid);
	}

	public CMdeModel getCreatedMdemodel() {
		return mdemodel;
	}

	public void setOpid(String opid) {
		this.opid=opid;
		initControl();
	}
	
	public String getOpid(){
		return opid;
	}

	@Override
	public void pack() {
		if(mdemodel==null){
			initControl();
		}
		super.pack();

		if( mdemodel instanceof CMdeModelAp){
			((CMdeModelAp)mdemodel).hideColumnAp();
		}
}

	/**
	 * 返回分隔的高度
	 * @return
	 */
	protected int getDividerLocation(){
		return 300;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	

    @Override
    public void dispose() {
		super.dispose();
    	mdemodel=null;
	}

}
