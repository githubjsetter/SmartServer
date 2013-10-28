package com.smart.platform.gui.ste;

import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.smart.client.system.Clientframe;
import com.smart.platform.gui.control.CFrame;

/**
 * 功能窗口基类
 * 
 * @author Administrator
 * 
 */
public class COpframe extends CFrame {

	boolean closed=false;
	public COpframe() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
	}

	public COpframe(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public COpframe(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	public COpframe(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	protected String opid;

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	@Override
	public void pack() {
		//pack要在服务器被调用.不能用swingutil.invokelaster
		super.pack();
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	/**
	 * 是否是外部人员使用的功能? 外部功能需要重载本函数返回false;
	 * @return  true:是外部人员使用的功能. false:缺省值.不是外部功能
	 */
	public boolean isExternal(){
		return false;
	}
}


