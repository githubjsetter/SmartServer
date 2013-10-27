package com.inca.np.selfcheck;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.ste.Hovdesc;
import com.inca.np.util.DefaultNPParam;


/**
 * 检查HOV
 * @author Administrator
 *
 */
public abstract class Hovcheck {
	//列举所有的hov,并检查设置
	public void doCheck(PrintWriter out){
		Enumeration<Hovdesc> en = DefaultNPParam.hovdescs.elements();
		while(en.hasMoreElements()){
			Hovdesc hovdesc=en.nextElement();
			String className=hovdesc.getClassname();
			CHovBase hov=null;
			try{
				Class<CHovBase> cls=(Class<CHovBase>) Class.forName(className);
				hov = cls.newInstance();
			}catch(Exception e){
				out.println("加载"+className+"失败:"+e.getMessage());
				continue;
			}
			
			//检查hov
			checkHov(hov,out);
		}
	}

	public void checkHov(CHovBase hov,PrintWriter out){
		String s=hov.selfCheck();
		if(s!=null && s.length()>0){
			out.println(s);
		}
	}
}
