package com.inca.np.presstest;

import java.text.DecimalFormat;
import java.util.Enumeration;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SendHelper;

/**
 * 压力测试
 * @author Administrator
 *
 */
public class Presstester implements Runnable{
	Category logger=Category.getInstance(Presstester.class);
	Presstestunit testunit=null;
	int loopcount=1;
	int threadcount=1;
	long starttime=0;
	int successcount=0;
	int errorcount=0;
	
	
	
	public Presstestunit getTestunit() {
		return testunit;
	}

	public void setTestunit(Presstestunit testunit) {
		this.testunit = testunit;
	}

	public int getLoopcount() {
		return loopcount;
	}

	public void setLoopcount(int loopcount) {
		this.loopcount = loopcount;
	}

	public int getThreadcount() {
		return threadcount;
	}

	public void setThreadcount(int threadcount) {
		this.threadcount = threadcount;
	}

	public void doTest(){
		starttime=System.currentTimeMillis();
		successcount=errorcount=0;
		for(int i=0;i<threadcount;i++){
			Thread t=new Thread(this);
			t.start();
		}
	}
	
	public void dumpResult(){
		long t=System.currentTimeMillis()-starttime;
		int totalct=successcount + errorcount;
		double avgtime=(double)t/(double)totalct;
		DecimalFormat decfmt=new DecimalFormat("0.0");
		System.out.print("平均一次测试用时"+decfmt.format(avgtime));
		System.out.print("，成功"+successcount+"次");
		if(errorcount>0){
			System.out.print("，失败"+errorcount+"次");
		}
		System.out.print(",总用时"+t+"ms");
		System.out.println();
		
	}
	
	public void run(){
		for(int i=0;i<loopcount;i++){
			doTestone();
		}
		dumpResult();
	}
	
	void doTestone(){
		try {
			boolean ret=testunit.test();
			if(ret){
				successcount++;
			}else{
				errorcount++;
			}
		} catch (Exception e) {
			logger.error("error",e);
			errorcount++;
		}
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug = 0;
		DefaultNPParam.develop=0;
		DefaultNPParam.defaultappsvrurl = "http://192.9.200.1/npserver/clientrequest.do";
		DefaultNPParam.prodcontext="npserver";
		
		Presstester tester=new Presstester();
		tester.setLoopcount(10);
		tester.setThreadcount(10);
		
		//tester.setTestunit(new 	Sysddltestunit());
		tester.setTestunit(new GoodscountTestunit());
		//tester.setTestunit(new LongtimesqlTestunit());
		tester.doTest();
	}
}
