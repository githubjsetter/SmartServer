package com.smart.platform.demo.ste;

import java.awt.event.ActionListener;

import com.smart.platform.gui.control.CStetoolbar;

public class Goodstoolbar extends CStetoolbar{
	public Goodstoolbar(ActionListener l) {
		super(l);
	}

	@Override
	protected void createOtherButton(ActionListener listener) {
		super.createOtherButton(listener);
		addButton("演示","演示一个扩充的服务",Pub_goods_ste.ACTION_DEMO);
	}

}
