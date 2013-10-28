package com.smart.platform.auth;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandFactory;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-26 Time: 16:37:56
 * �û���ǰ��Ϣ
 */
public class Userruninfo implements Comparator {
	/**
	 * �û�ID
	 */
	protected String userid = "";
	/**
	 * �û���
	 */
	private String username = "";
	/**
	 * ��֤��
	 */
	private String authstring = "";

	/**
	 * ��¼ʱ��
	 */
	private long logindatetime;

	/**
	 * ������ʱ��
	 */
	private long lastaccesstime;

	private String deptid = "";
	private String deptname = "";
	private String roleid = "";
	private String rolename = "";

	private String placepointid = "";
	private String placepointname = "";
	private String storageid = "";
	private String storagename = "";

	/**
	 * ���
	 */
	private String banci = "";

	private String sthouseid = "";
	private String sthousename = "";

	private int useday = 0;

	private String remoteip = "";

	/**
	 * ����û��ǲ��ǿ�����Ա
	 */
	private boolean develop = false;

	/**
	 * ���㵥Ԫ
	 */
	private String entryid = "";
	private String entryname = "";

	/**
	 * ����MAC��ַ
	 */
	private String mac = "";

	/**
	 * ��ǰ��Ĺ���
	 */
	private String activeopid = "";

	Category logger = Category.getInstance(Userruninfo.class);

	/**
	 * �û�����,���ڷ����������ݲ���
	 */
	private HashMap<String, Object> params = new HashMap<String, Object>();

	public String getEntryname() {
		return entryname;
	}

	public void setEntryname(String entryname) {
		this.entryname = entryname;
	}

	public String getActiveopid() {
		return activeopid;
	}

	public void setActiveopid(String activeopid) {
		this.activeopid = activeopid;
	}

	public boolean isDevelop() {
		return develop;
	}

	public void setDevelop(boolean develop) {
		this.develop = develop;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthstring() {
		return authstring;
	}

	public void setAuthstring(String authstring) {
		this.authstring = authstring;
	}

	public long getLogindatetime() {
		return logindatetime;
	}

	public void setLogindatetime(long logindatetime) {
		this.logindatetime = logindatetime;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getPlacepointid() {
		if (placepointid != null && placepointid.length() > 0) {
			return placepointid;
		}

		// ���������ѯ
		ClientRequest req = new ClientRequest("npclient:getplacepointid");
		SendHelper sh = new SendHelper();
		try {
			ServerResponse resp = sh.sendRequest(req);
			if (!resp.getCommand().startsWith("+OK")) {
				logger.error(resp.getCommand());
				errorMessage(resp.getCommand());
				return placepointid;
			}
			DataCommand dcmd = (DataCommand) resp.commandAt(1);
			DBTableModel dbmodel = dcmd.getDbmodel();
			if (dbmodel.getRowCount() == 0) {
				errorMessage("û���ҵ���ǰ��Ա��Ӧ���ŵ꣬�޷���������");
				return placepointid;
			}

			if (dbmodel.getRowCount() == 1 ) {
				placepointid = dbmodel.getItemValue(0, "placepointid");
				placepointname = dbmodel.getItemValue(0, "placepointname");
				storageid = dbmodel.getItemValue(0, "storageid");
				storagename = dbmodel.getItemValue(0, "storagename");
				sthouseid = dbmodel.getItemValue(0, "sthouseid");
				setPlacepointid(placepointid);
				synPlacepointid();
				return placepointid;
			}

			if (dbmodel.getRowCount() > 1 && !DefaultNPParam.runonserver) {
				dbmodel.getColumninfo("placepointid").setTitle("�ŵ�ID");
				dbmodel.getColumninfo("placepointname").setTitle("�ŵ�");
				dbmodel.getColumninfo("storageid").setTitle("������ID");
				dbmodel.getColumninfo("storagename").setTitle("������");
				dbmodel.getColumninfo("sthouseid").setTitle("�ַ�ID");
				JFrame frm = null;
				Window w = KeyboardFocusManager
						.getCurrentKeyboardFocusManager().getActiveWindow();
				if (w != null && w instanceof JFrame) {
					frm = (JFrame) w;
				}

				SelectentryDlg dlg = new SelectentryDlg(frm, "ѡ���ŵ�", dbmodel);
				dlg.pack();
				dlg.setVisible(true);
				if (dlg.isOk()) {
					int row = dlg.getSelectrow();
					placepointid = dbmodel.getItemValue(row, "placepointid");
					placepointname = dbmodel
							.getItemValue(row, "placepointname");
					storageid = dbmodel.getItemValue(row, "storageid");
					storagename = dbmodel.getItemValue(row, "storagename");
					sthouseid = dbmodel.getItemValue(row, "sthouseid");
					setPlacepointid(placepointid);
					synPlacepointid();
					return placepointid;
				}
			}

		} catch (Exception e) {
			logger.error("error", e);
			return placepointid;
		}

		errorMessage("û���ҵ���ǰ��Ա��Ӧ���ŵ꣬�޷���������");
		return placepointid;
	}

	
	void errorMessage(String msg){
		if(DefaultNPParam.runonserver)return;
		JOptionPane.showMessageDialog(null, msg);
	}
	
	public void setPlacepointid(String placepointid) {
		this.placepointid = placepointid;
		
	}

	/**
	 * �������ͬ��placepointid
	 */
	public void synPlacepointid() {
		//֪ͨ��������
		if(DefaultNPParam.runonserver){
			return;
		}
		
		ClientRequest req=new ClientRequest("npclient:setplacepointid");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("placepointid",placepointid);
		pcmd.addParam("storageid",getStorageid());
		pcmd.addParam("sthouseid",getSthouseid());
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String respcmd=resp.getCommand();
			if(!respcmd.startsWith("+OK")){
				errorMessage(respcmd);
			}
		} catch (Exception e) {
			logger.error("error",e);
		}
	}

	/**
	 * �������ͬ��entryid
	 */
	public void synEntryid(){
		//֪ͨ��������
		if(DefaultNPParam.runonserver){
			return;
		}
		
		ClientRequest req=new ClientRequest("npclient:setentryid");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("entryid",entryid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String respcmd=resp.getCommand();
			if(!respcmd.startsWith("+OK")){
				errorMessage(respcmd);
			}
		} catch (Exception e) {
			logger.error("error",e);
		}
	}

	public String getPlacepointname() {
		return placepointname;
	}

	public void setPlacepointname(String placepointname) {
		this.placepointname = placepointname;
	}

	public String getStorageid() {
		return storageid;
	}

	public void setStorageid(String storageid) {
		this.storageid = storageid;
	}

	public String getStoragename() {
		return storagename;
	}

	public void setStoragename(String storagename) {
		this.storagename = storagename;
	}

	public String getBanci() {
		return banci;
	}

	public void setBanci(String banci) {
		this.banci = banci;
	}

	public String getSthouseid() {
		return sthouseid;
	}

	public void setSthouseid(String sthouseid) {
		this.sthouseid = sthouseid;
	}

	public String getSthousename() {
		return sthousename;
	}

	public void setSthousename(String sthousename) {
		this.sthousename = sthousename;
	}

	public int getUseday() {
		return useday;
	}

	public void setUseday(int useday) {
		this.useday = useday;
	}

	public long getLastaccesstime() {
		return lastaccesstime;
	}

	public void setLastaccesstime(long lastaccesstime) {
		this.lastaccesstime = lastaccesstime;
	}

	public String getRemoteip() {
		return remoteip;
	}

	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}

	public String getEntryid() {
		if (entryid != null && entryid.length() > 0)
			return entryid;
		// �ӷ�������ѯ
		ClientRequest req = new ClientRequest("npclient:getentry");
		try {
			ServerResponse resp = SendHelper.sendRequest(req);
			String respcmd = resp.getCommand();
			if (respcmd.startsWith("+OK")) {
				DataCommand dcmd = (DataCommand) resp.commandAt(1);
				DBTableModel dbmodel = dcmd.getDbmodel();
				if (dbmodel.getRowCount() == 1) {
					entryid = dbmodel.getItemValue(0, "entryid");
					entryname = dbmodel.getItemValue(0, "entryname");
					synEntryid();
					return entryid;
				}

				if (dbmodel.getRowCount() > 1) {
					dbmodel.getDisplaycolumninfos().elementAt(0).setTitle(
							"���㵥ԪID");
					dbmodel.getDisplaycolumninfos().elementAt(1).setTitle(
							"���㵥Ԫ");
					JFrame frm = null;
					Window w = KeyboardFocusManager
							.getCurrentKeyboardFocusManager().getActiveWindow();
					if (w != null && w instanceof JFrame) {
						frm = (JFrame) w;
					}
					SelectentryDlg dlg = new SelectentryDlg(null, "ѡ����㵥Ԫ",
							dbmodel);
					dlg.pack();
					dlg.setVisible(true);
					if (dlg.isOk()) {
						int row = dlg.getSelectrow();
						if (row >= 0) {
							entryid = dbmodel.getItemValue(row, "entryid");
							entryname = dbmodel.getItemValue(row, "entryname");
							synEntryid();
							return entryid;
						}
					}
				}
			} else {
				logger.error("ȡ���㵥Ԫʧ��:" + respcmd);
			}
		} catch (Exception e) {
			logger.error("error", e);
			entryid = "";
		}

		return entryid;
	}

	public void setEntryid(String entryid) {
		this.entryid = entryid;
	}

	public void writeData(OutputStream out) throws Exception {
		CommandFactory.writeString(userid, out);
		CommandFactory.writeString(username, out);
		CommandFactory.writeString(authstring, out);
		CommandFactory.writeString(String.valueOf(logindatetime), out);
		CommandFactory.writeString(deptid, out);
		CommandFactory.writeString(deptname, out);
		CommandFactory.writeString(roleid, out);
		CommandFactory.writeString(rolename, out);
		CommandFactory.writeString(placepointid, out);
		CommandFactory.writeString(placepointname, out);
		CommandFactory.writeString(storageid, out);
		CommandFactory.writeString(storagename, out);
		CommandFactory.writeString(sthouseid, out);
		CommandFactory.writeString(sthousename, out);
		CommandFactory.writeShort(useday, out);
		CommandFactory.writeString(String.valueOf(lastaccesstime), out);
		CommandFactory.writeString(entryid, out);
		CommandFactory.writeString(entryname, out);
	}

	public void readData(InputStream in) throws Exception {
		userid = CommandFactory.readString(in);
		username = CommandFactory.readString(in);
		authstring = CommandFactory.readString(in);
		String strtime = CommandFactory.readString(in);
		try {
			logindatetime = Long.parseLong(strtime);
		} catch (NumberFormatException e) {
		}
		deptid = CommandFactory.readString(in);
		deptname = CommandFactory.readString(in);
		roleid = CommandFactory.readString(in);
		rolename = CommandFactory.readString(in);
		placepointid = CommandFactory.readString(in);
		placepointname = CommandFactory.readString(in);
		storageid = CommandFactory.readString(in);
		storagename = CommandFactory.readString(in);
		sthouseid = CommandFactory.readString(in);
		sthousename = CommandFactory.readString(in);
		useday = CommandFactory.readShort(in);
		try {
			lastaccesstime = Long.parseLong(CommandFactory.readString(in));
		} catch (NumberFormatException e) {
		}
		entryid = CommandFactory.readString(in);
		entryname = CommandFactory.readString(in);
	}

	public void dump(Category logger) {
		logger.info("userid=" + userid + ",placepointid=" + placepointid
				+ ",storageid=" + storageid+",entryid="+entryid);
	}

	/**
	 * �Ƚ�.��������ʱ�䵹��
	 */
	public int compare(Object o1, Object o2) {
		Userruninfo u1 = (Userruninfo) o1;
		Userruninfo u2 = (Userruninfo) o2;
		if (u2.getLastaccesstime() > u1.getLastaccesstime()) {
			return 1;
		} else if (u2.getLastaccesstime() < u1.getLastaccesstime()) {
			return -1;
		} else {
			return 0;
		}
	}

	public void putParam(String paramname, Object paramvalue) {
		params.put(paramname, paramvalue);
	}

	public Object getParam(String paramname) {
		return params.get(paramname);
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * ���ⲿ��Ա��?
	 * @return
	 */
	public boolean isExternal(){
		String flag=(String) getParam("external");
		if(flag!=null && flag.equals("true")){
			return true;
		}
		return false;
	}
	
	/**
	 * �������ⲿ��Ա
	 */
	public void setExternal(boolean external){
		putParam("external", external?"true":"false");
	}
}
