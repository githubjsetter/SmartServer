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
		addButton("��ʾ","��ʾһ������ķ���",Pub_goods_ste.ACTION_DEMO);
	}

}
