package com.inca.np.selfcheck;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.ste.Hovdesc;
import com.inca.np.util.DefaultNPParam;


/**
 * ���HOV
 * @author Administrator
 *
 */
public abstract class Hovcheck {
	//�о����е�hov,���������
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
				out.println("����"+className+"ʧ��:"+e.getMessage());
				continue;
			}
			
			//���hov
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
