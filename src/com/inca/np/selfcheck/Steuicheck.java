package com.inca.np.selfcheck;

import java.io.PrintWriter;
import java.util.Enumeration;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.RequestDispatch;
import com.inca.np.server.RequestProcessIF;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.server.process.SteProcessor;


/**
 * 单表编辑界面检查
 * 
 * 从RequestDispatch中找出所有单表编辑服务.
 * 从中了出CStemodel.取得表格 form等进行分析.
 * 
 * @author Administrator
 *
 */
public class Steuicheck {
	
	public void doCheck(PrintWriter out){
/*		RequestDispatch disp=RequestDispatch.getInstance();
		Enumeration<RequestProcessIF> en=disp.getProcessors();
		while(en.hasMoreElements()){
			RequestProcessIF proc=en.nextElement();
			
			if(proc instanceof SteProcessor){
				SteProcessor steproc=(SteProcessor)proc;
				CSteModel stemodel=steproc.getStemodelInst();
				stemodel.setDbprocesstablename(steproc.getDbtablename());
				checkSte(stemodel,out);
			}else if( proc instanceof MdeProcessor){
				MdeProcessor mdeproc=(MdeProcessor)proc;
				CMdeModel mde=mdeproc.getMdeModelInst();
				mde.getMasterModel().setDbprocesstablename(mdeproc.getDbMastertablename());
				mde.getDetailModel().setDbprocesstablename(mdeproc.getDbDetailtablename());
				checkMde(mde,out);
			}
		}
*/	}
	
	public void checkSte(CSteModel stemodel,PrintWriter out){
		String s=stemodel.selfCheck();
		if(s!=null && s.length()>0){
			out.println(s);
		}
	}
	public void checkMde(CMdeModel mdemodel,PrintWriter out){
		String s=mdemodel.selfCheck();
		if(s!=null && s.length()>0){
			out.println(s);
		}
	}
	
	public static void main(String[] argv){
		Steuicheck c=new Steuicheck();
		c.doCheck(new PrintWriter(System.out));
	}
}
