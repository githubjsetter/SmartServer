package com.smart.platform.demo.mde;

import javax.swing.SwingUtilities;

import org.apache.log4j.Category;

import com.smart.extension.mde.CMdeModelAp;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/*功能"货品和货品明细"总单细目Model*/
public class Goodsdtl_mde extends CMdeModelAp {
	public Goodsdtl_mde(CFrame frame, String title) {
		super(frame, title);
		setResetbeforenew(true);
		setSaveimmdiate(true);
		
		//testMultithread1();
	}

	protected CMasterModel createMastermodel() {
		return new Goodsdtl_master(frame, this);
	}

	protected CDetailModel createDetailmodel() {
		return new Goodsdtl_detail(frame, this);
	}

	public String getMasterRelatecolname() {
		return "goodsid";
	}

	public String getDetailRelatecolname() {
		return "goodsid";
	}

	public String getSaveCommandString() {
		return "Goodsdtl_mde.保存货品和明细";
	}

	@Override
	protected boolean isAllownodetail() {
		return true;
	}

	@Override
	protected int on_actionPerformed(String command) {
		if ("多线程".equals(command)) {
			//testMultithread();
			synchronized (Goodsdtl_mde.this) {
				Goodsdtl_mde.this.notifyAll();
			}
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	Category logger = Category.getInstance(Goodsdtl_mde.class);
	
	void testMultithread1(){
		class Tests implements Runnable{
			public void run(){
				//Object o=new Object();
				logger.debug("begin wait");
				
				synchronized (Goodsdtl_mde.this) {
					try {
						Goodsdtl_mde.this.wait();
						logger.debug("等到了");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		Thread t=new Thread(new Tests());
		t.start();
	}

	/**
	 * 多线程测试.一个线程不停地设置总单为卡片和表格. 另一个打开cdefaultprogress,不停地更新
	 */
	void testMultithread() {
		/*
		 * for(int i=0;i<100;i++){ logger.debug("on set row="+(i+1));
		 * getMasterModel().setRow(1); getMasterModel().doHideform();
		 * getMasterModel().showForm(); logger.debug("on finish set
		 * row="+(i+1)); } if(true)return;
		 */

		Backthread1 t1 = new Backthread1();
		new Thread(t1).start();

		CDefaultProgress prog = new CDefaultProgress(getParentFrame());
		prog.testMulti("", "Start show");

		for (int t = 0; t < 100; t++) {
			SaveThread sthread = new SaveThread(prog);
			new Thread(sthread).start();
			prog.show();
			/*
			 * try { Thread.sleep(100); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */logger.debug("prog return");
		}

	}

	class Backthread1 implements Runnable {
		public void run() {
			for (;;) {

				for (int i = 0; i < getMasterModel().getRowCount(); i++) {
					try {
						logger.debug("begin set row " + i);
						// getMasterModel().setRow(i);
						logger.debug("begin doHideform");
						getMasterModel().doHideform();

						Thread.sleep(100);
						logger.debug("begin showForm");
						getMasterModel().showForm();
						Thread.sleep(100);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	class SaveThread implements Runnable {
		CDefaultProgress prog;

		public SaveThread(CDefaultProgress prog) {
			this.prog = prog;
		}

		public void run() {
			for (;;) {
				prog.testMulti("title", "消息");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
}
