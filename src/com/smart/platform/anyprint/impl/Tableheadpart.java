package com.smart.platform.anyprint.impl;

import java.util.Enumeration;

public class Tableheadpart extends Partbase {

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		if (getPlantype().indexOf("±í¸ñ") >= 0) {
			Enumeration<Cellbase> en = cells.elements();
			while (en.hasMoreElements()) {
				en.nextElement().getRect().height = height;
			}
		}
	}

}
