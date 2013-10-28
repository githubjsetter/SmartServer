package com.smart.server.pushplat.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.apache.log4j.Category;

import com.smart.bi.client.design.BIReportFrame;
import com.smart.client.system.Clientframe;
import com.smart.client.system.NpopManager;
import com.smart.extension.ap.Aphelper;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.MMdeFrame;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Opnode;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.MultisteFrame;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.SendHelper;
import com.smart.server.pushplat.common.Pushinfo;
import com.smart.server.pushplat.common.PushshowIF;

/**
 * 后台运行功能.
 * 
 * @author user
 * 
 */
public class BackgroundRunner {
	static Category logger = Category.getInstance(BackgroundRunner.class);
	public static PushshowIF shower = null;

	private static void runPushinfos() {
		Vector<Pushinfo> pushinfos = new Vector<Pushinfo>();
		for (int i = 0; rolepushdm != null && i < rolepushdm.getRowCount(); i++) {
			Pushinfo pushinfo = new Pushinfo();
			pushinfos.add(pushinfo);
			pushinfo.setPushid(rolepushdm.getItemValue(i, "pushid"));
			pushinfo.setPushname(rolepushdm.getItemValue(i, "pushname"));
			pushinfo.setGroupname(rolepushdm.getItemValue(i, "groupname"));
			pushinfo.setCallopid(rolepushdm.getItemValue(i, "callopid"));
			pushinfo.setCallopname(rolepushdm.getItemValue(i, "callopname"));
			pushinfo.setWheres(rolepushdm.getItemValue(i, "wheres"));
			pushinfo.setOtherwheres(rolepushdm.getItemValue(i, "otherwheres"));
			pushinfo.setLevel(Integer.parseInt(rolepushdm.getItemValue(i,
					"level")));
		}
		Pushinfo wfpushinfo = queryWorkflow();
		if (wfpushinfo != null) {
			pushinfos.add(wfpushinfo);
		}

		// 先按level排序
		LinkedList<Pushinfo> linklist = new LinkedList<Pushinfo>(pushinfos);
		Collections.sort(linklist, new PushinfoCompara());

		logger.debug("call shower.clear()");
		shower.clear();
		Iterator<Pushinfo> it = linklist.iterator();
		while (it.hasNext()) {
			Pushinfo pushinfo = it.next();
			if (pushinfo.getCallopid().equals("12")) {
				// 是工作流审批功能,不需要查询
				logger.debug("appendPushinfo 工作流审批");
				shower.appendPushinfo(pushinfo);
				continue;
			}
			runPushinfo(pushinfo);
		}
	}
	
	/**
	 * 功能ID和功能窗口对应关系
	 */
	static HashMap<String, COpframe> opidfrmmap=new HashMap<String, COpframe>();

	static private void runPushinfo(Pushinfo pushinfo) {
		Opnode opnode = NpopManager.getInst().getOpnode(pushinfo.getCallopid());
		if (opnode == null) {
			// 说明没有授权,返回
			return;
		}
		// 得到功能窗口
		COpframe frm = opidfrmmap.get(pushinfo.getCallopid());
		if(frm==null){
			frm =Clientframe.getClientframe().runOp(
				pushinfo.getCallopid(), true);
			if(frm==null)return;
			opidfrmmap.put(pushinfo.getCallopid(),frm);
		}
		CSteModel ste = null;
		if (frm instanceof Steframe) {
			ste = ((Steframe) frm).getCreatedStemodel();
		} else if (frm instanceof MdeFrame) {
			ste = ((MdeFrame) frm).getCreatedMdemodel().getMasterModel();
		} else if (frm instanceof MultisteFrame) {
			ste = ((MultisteFrame) frm).getCreatedStemodel();
		} else if (frm instanceof MMdeFrame) {
			ste = ((MMdeFrame) frm).getCreatedStemodel();
		} else if (frm instanceof BIReportFrame) {
		} else {
			logger.error("不明要运行的窗口类型" + frm);
			return;
		}
		String wheres = pushinfo.getFullwheres();
		wheres = Aphelper.filterApwheres(wheres);
		String sql = ste.getFinalquerysql(wheres);
		RemotesqlHelper sh = new RemotesqlHelper();
		try {
			DBTableModel dm = sh.doSelect(sql, 0, 100);
			logger.debug("查询结束,记录数为" + dm.getRowCount());
			if (dm.getRowCount() > 0) {
				pushinfo.setRowcount(dm.getRowCount());
				shower.appendPushinfo(pushinfo);
			}
			dm.freeMemory();
			dm=null;
		} catch (Exception e1) {
			logger.error("error", e1);
		} finally {
			if (frm instanceof Steframe) {
				ste = ((Steframe) frm).getCreatedStemodel();
				ste.reset();
			} else if (frm instanceof MdeFrame) {
				((MdeFrame) frm).getCreatedMdemodel().getDetailModel().reset();
				((MdeFrame) frm).getCreatedMdemodel().getMasterModel().reset();
			} else if (frm instanceof MultisteFrame) {
				ste = ((MultisteFrame) frm).getCreatedStemodel();
				ste.reset();
			} else if (frm instanceof MMdeFrame) {
				ste = ((MMdeFrame) frm).getCreatedStemodel();
				ste.reset();
			} else if (frm instanceof BIReportFrame) {
			} else {
				logger.error("不明要运行的窗口类型" + frm);
				return;
			}
			
		}
	}

	static Pushinfo queryWorkflow() {
		ClientRequest req = new ClientRequest("npserver:查询待审批");
		ServerResponse resp;
		try {
			resp = SendHelper.sendRequest(req);
			DataCommand dcmd = (DataCommand) resp.commandAt(1);
			DBTableModel dm = dcmd.getDbmodel();
			logger.debug("查询待审批返回dm.rowcount=" + dm.getRowCount());
			if (dm.getRowCount() == 0) {
				return null;
			}
			Pushinfo pushinfo = new Pushinfo();
			pushinfo.setPushid("0");
			pushinfo.setPushname("审批流程待审批");
			pushinfo.setGroupname("审批");
			pushinfo.setCallopid("12");
			pushinfo.setCallopname("审批");
			pushinfo.setWheres("");
			pushinfo.setOtherwheres("");
			pushinfo.setLevel(Pushinfo.LEVEL_URGENT);
			pushinfo.setRowcount(dm.getRowCount());
			return pushinfo;
		} catch (Exception e) {
			logger.error("error", e);
		}
		return null;

	}

	static class PushinfoCompara implements Comparator<Pushinfo> {

		public int compare(Pushinfo o1, Pushinfo o2) {
			if (o1.getLevel() > o2.getLevel())
				return 1;
			else if (o1.getLevel() < o2.getLevel())
				return -1;
			else {
				return o1.getGroupname().compareTo(o2.getGroupname());
			}
		}

	}

	static class ScanThread extends Thread {
		PushshowIF shower;

		public ScanThread(PushshowIF shower) {
			super();
			this.shower = shower;
		}

		public void run() {
			for (;;) {
				shower.updateStatus("开始处理推送任务");
				downloadRolepush();
				runPushinfos();
				waitTime();
			}
		}

		/**
		 * 等下载时间
		 */
		void waitTime() {
			long starttime = System.currentTimeMillis();
			int minute = shower.getMinute();
			long targettime = starttime + (long) minute * 60L * 1000L;
			while (System.currentTimeMillis() < targettime) {
				if (shower.isNotifystartimmediate()) {
					shower.resetNotifystartimmediate();
					break;
				}
				showRemaintime(targettime - System.currentTimeMillis());
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void showRemaintime(long l) {
			l = l / 1000l;
			long min = l / 60l;
			long sec = l % 60l;
			StringBuffer sb = new StringBuffer();
			sb.append("离下次推送还有");
			if (min > 0) {
				sb.append(min);
				sb.append("分");
			}
			sb.append(sec);
			sb.append("秒");
			shower.updateStatus(sb.toString());
		}

	}

	static DBTableModel rolepushdm = null;

	static protected void downloadRolepush() {
		ClientRequest req = new ClientRequest("npserver:下载角色推送");
		try {
			ServerResponse resp = SendHelper.sendRequest(req);
			DataCommand dcmd = (DataCommand) resp.commandAt(1);
			if(rolepushdm!=null && rolepushdm.getRowCount()>0){
				rolepushdm.freeMemory();
				rolepushdm=null;
			}
			rolepushdm = dcmd.getDbmodel();
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	public static void runThread(PushshowIF shower) {
		BackgroundRunner.shower = shower;
		ScanThread t = new ScanThread(shower);
		t.setDaemon(true);
		// 比正常的normal(5)要低
		t.setPriority(3);
		t.start();
	}
}
