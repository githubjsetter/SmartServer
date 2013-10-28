package com.smart.platform.anyprint.impl;

import java.util.Enumeration;

public class Bodypart extends Partbase {
	public void setHeight(int height) {
		super.setHeight(height);
		if (plantype.indexOf("±í¸ñ") >= 0) {
			Enumeration<Cellbase> en = cells.elements();
			while (en.hasMoreElements()) {
				en.nextElement().getRect().height = height;
			}
		}
	}

}
