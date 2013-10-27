package com.inca.np.util;

import java.security.ProtectionDomain;

public class ZxClassLoader extends ClassLoader{

	public ZxClassLoader() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ZxClassLoader(ClassLoader parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	

	public Class loadClass(String classname,byte[] classtype) throws Exception{
		return super.defineClass(classname,classtype,0,classtype.length,null);
	}
}
