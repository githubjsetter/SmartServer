package com.smart.extension.ste;

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.log4j.Category;

import com.smart.extension.ap.Aphelper;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;

/**
 * 带授权的单表编辑
 * 
 * @author Administrator
 * 
 */
public abstract class CSteModelAp extends CSteModel implements ApIF {

	Category logger = Category.getInstance(CSteModelAp.class);
	protected HashMap<String, Apinfo> apmap = new HashMap<String, Apinfo>();
	/**
	 * 设置授权属性
	 */
	public static final String ACTION_SETAP = "setap";

	public CSteModelAp(){
		super();
	}
	public CSteModelAp(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		useap=true;
	}

	/**
	 * 开发授权属性,能否新增
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCannew() {
		return true;
	}

	/**
	 * 开发授权属性,能否删除
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCandelete() {
		return true;
	}

	/**
	 * 开发授权属性,能否查询
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCanquery() {
		return true;
	}

	/**
	 * 开发授权属性,能否修改
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCanmodify() {
		return true;
	}

	/**
	 * 开发授权属性,能否保存
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCansave() {
		return true;
	}

	@Override
	public void onstartRun() {
		// TODO Auto-generated method stub
		super.onstartRun();
		downloadAp(ClientUserManager.getCurrentUser().getRoleid());
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals(ACTION_SETAP)) {
			onSetupap();
			return 0;
		}
		return super.on_actionPerformed(command);
	}
	
	/**
	 * 返回frame窗口类名,用于定义在数据库中的功能定义
	 * @return
	 */
	protected String getApFramename(){
		return this.getParentFrame().getClass()
		.getName();
	}

	/**
	 * 从服务器下载授权属性
	 * 
	 * @throws Exception
	 */
	protected boolean downloadAp(String roleid) {
		// 下载授权属性,并进行设置
		ClientRequest req = new ClientRequest("np:查询授权属性");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("roleid",roleid);
		if(getOpid()==null || getOpid().length()==0)return false;
		paramcmd.addParam("opid", this.getOpid());
		//paramcmd.addParam("opclassname", getApFramename());

		DBTableModel apmodel = null;
		try {
			aploaded = false;
			ServerResponse svrresp = SendHelper.sendRequest(req);
			StringCommand respcmd = (StringCommand) svrresp.commandAt(0);
			if (!respcmd.getString().startsWith("+OK")) {
				errorMessage("下载授权属性错误", respcmd.getString());
				return false;
			}
			ParamCommand respparamcmd = (ParamCommand) svrresp.commandAt(1);
			String opid = respparamcmd.getValue("opid");
			setOpid(opid);
			DataCommand datacmd = (DataCommand) svrresp.commandAt(2);
			apmodel = datacmd.getDbmodel();
			aploaded = true;
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("下载授权属性错误", e.getMessage());
			getParentFrame().setVisible(false);
			onstopRun();
			getParentFrame().dispose();
			return false;
		}

		apmap.clear();
		for (int r = 0; r < apmodel.getRowCount(); r++) {
			String roleopid = apmodel.getItemValue(r, "roleopid");
			String apid = apmodel.getItemValue(r, "apid");
			String apname = apmodel.getItemValue(r, "apname");
			String aptype = apmodel.getItemValue(r, "aptype");
			String apvalue = apmodel.getItemValue(r, "apvalue");
			Apinfo apinfo = new Apinfo(apname, aptype);
			apinfo.setApvalue(apvalue);
			logger.info("download apname="+apinfo.getApname()+",aptype="+apinfo.getAptype()+",apvalue="+apinfo.getApvalue());
			apmap.put(apname, apinfo);
		}
		
		
		return true;
	}

	public void hideColumnAp(){
		//清掉不显示的
		boolean modified=false;
		for(int i=0;formcolumndisplayinfos !=null && i<formcolumndisplayinfos.size();i++){
			DBColumnDisplayInfo col=formcolumndisplayinfos.elementAt(i);
			if(col.isHide()){
				//如果开发时已定义为隐藏,不再处理
				continue;
			}
			String apvalue=getApvalue("hide_"+col.getColname().toLowerCase());
			if(apvalue!=null && apvalue.equals("true")){
				formcolumndisplayinfos.remove(i);
				querycolumns.removeElement(col.getColname());
				modified=true;
				i--;
			}
		}
		if(modified){
			table=null;
			recreateDBModel();
			recreateForm();
		}
	}
	
	protected boolean aploaded = false;

	/**
	 * 取授权属性定义
	 * 
	 * @param apname
	 * @return Apinfo
	 */
	public Apinfo getApinfo(String apname) {
		if (!aploaded) {
			return null;
		}
		return apmap.get(apname);
	}

	/**
	 * 取授权属性值
	 * 
	 * @param apname
	 * @return
	 */
	public String getApvalue(String apname) {
		if (!aploaded) {
			if (apname.equals(Apinfo.APNAME_WHERES))
				return "";
			return "false";
		}
		Apinfo apinfo = apmap.get(apname);
		if (apinfo == null)
			return "";
		return apinfo.getApvalue();
	}

	/**
	 * 设置授权属性
	 */
	protected void onSetupap() {
		if(	setupAp(ClientUserManager.getCurrentUser().getRoleid())){
		infoMessage("成功", "授权属性保存成功");
		}
	}

	/**
	 * 保存授权属性
	 * 
	 * @param aps
	 * @return true成功
	 */
	protected boolean saveAp(Vector<Apinfo> aps,String roleid) {
		ApinfoDbmodel apmodel = new ApinfoDbmodel();
		Enumeration<Apinfo> en = aps.elements();
		while (en.hasMoreElements()) {
			Apinfo apinfo = en.nextElement();
			apmodel.appendRow();
			int r = apmodel.getRowCount() - 1;
			apmodel.setItemValue(r, "apname", apinfo.getApname());
			apmodel.setItemValue(r, "aptype", apinfo.getAptype());
			apmodel.setItemValue(r, "apvalue", apinfo.getApvalue());
		}

		ClientRequest req = new ClientRequest("np:保存授权属性");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("opid", getOpid());
		paramcmd.addParam("roleid",roleid);

		DataCommand datacmd = new DataCommand();
		req.addCommand(datacmd);
		datacmd.setDbmodel(apmodel);

		// 提交保存
		try {
			ServerResponse svrresp = SendHelper.sendRequest(req);
			StringCommand respcmd = (StringCommand) svrresp.commandAt(0);
			if (!respcmd.getString().startsWith("+OK")) {
				errorMessage("错误", respcmd.getString());
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("错误", e.getMessage());
			return false;
		}
	}


	@Override
	protected CStetoolbar createToolbar() {
		// TODO Auto-generated method stub
		CStetoolbarAp stetoolbar=new CStetoolbarAp(this);
		Enumeration<String> en=printplans.elements();
		while(en.hasMoreElements()){
			String planname=en.nextElement();
			stetoolbar.addPrintmenu(planname,"DOCPRINT_"+planname);
		}
		return stetoolbar;
}

	/**
	 * 授权属性,能否新增
	 * 
	 * @return true 可以
	 */
	public boolean isApCannew() {
		if (!isDevelopCannew())
			return false;
		return getApvalue(Apinfo.APNAME_FORBIDNEW).equals("true") ? false
				: true;
	}

	/**
	 * 授权属性,能否删除
	 * 
	 * @return true 可以
	 */
	public boolean isApCandelete() {
		if (!isDevelopCandelete())
			return false;
		return getApvalue(Apinfo.APNAME_FORBIDDELETE).equals("true") ? false
				: true;
	}

	/**
	 * 授权属性,能否查询
	 * 
	 * @return true 可以
	 */
	public boolean isApCanquery() {
		if (!isDevelopCanquery())
			return false;
		return getApvalue(Apinfo.APNAME_FORBIDQUERY).equals("true") ? false
				: true;
	}

	/**
	 * 授权属性,能否修改
	 * 
	 * @return true 可以
	 */
	public boolean isApCanmodify() {
		if (!isDevelopCanmodify())
			return false;
		return getApvalue(Apinfo.APNAME_FORBIDMODIFY).equals("true") ? false
				: true;
	}

	/**
	 * 授权属性,能否保存
	 * 
	 * @return true 可以
	 */
	public boolean isApCansave() {
		if (!isDevelopCansave())
			return false;
		return getApvalue(Apinfo.APNAME_FORBIDSAVE).equals("true") ? false
				: true;
	}

	/**
	 * 授权属性,是否只能删改自已的
	 * 
	 * @return true 只能改自已的. 缺省是false
	 */
	public boolean isApModifyselfonly() {
		return getApvalue(Apinfo.APNAME_MODIFYSELFONLY).equals("true") ? true
				: false;
	}

	@Override
	protected int on_beforedel(int row) {
		if (!isApCandelete()) {
			setStatusmessage("你没有删除的授权");
			return -1;
		}
		if (isApModifyselfonly()) {
			// TODO:如果只能改自已的,还要处理
		}
		return super.on_beforedel(row);
	}

	@Override
	protected int on_beforemodify(int row) {
		if (!isApCanmodify()) {
			setStatusmessage("你没有修改的授权");
			return -1;
		}

		if (isApModifyselfonly()) {
			// TODO:如果只能改自已的,还要处理
		}

		return super.on_beforemodify(row);
	}

	@Override
	protected int on_beforeNew() {
		if (!isApCannew()) {
			setStatusmessage("你没有新增的授权");
			return -1;
		}
		return super.on_beforeNew();
	}

	@Override
	protected int on_beforequery() {
		if (!isApCanquery()) {
			setStatusmessage("你没有查询的授权");
			return -1;
		}
		return super.on_beforequery();
	}

	@Override
	public int on_beforesave() {
		if (!isApCansave()) {
			setStatusmessage("你没有保存的授权");
			return -1;
		}
		return super.on_beforesave();
	}

	@Override
	public String buildSelectSql(String wheres) {
		String sql = super.buildSelectSql(wheres);
		// 加上授权
		String apwhere = this.getApvalue(Apinfo.APNAME_WHERES);
		if (apwhere == null)
			apwhere = "";
		apwhere = Aphelper.filterApwheres(apwhere);
		if(apwhere.length()>0){
			apwhere="("+apwhere+")";
		}
		sql = DBHelper.addWheres(sql, apwhere);
		return sql;
	}



	public boolean setupAp(String roleid) {
		if (!downloadAp(roleid)) {
			errorMessage("不能设置", "下载授权属性失败,不能设置");
			return false;
		}

		ApsetupDialog dlg = new ApsetupDialog(this.getParentFrame(),
				getTitle(), this);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;

		// 保存授权属性
		Vector<Apinfo> aps = dlg.getApinfos();
		if (!saveAp(aps,roleid)) {
			return false;
		}
		if (!downloadAp(roleid)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 返回参数类型的授权属性
	 */
	public Vector<Apinfo> getParamapinfos(){
		return new Vector<Apinfo>();
	}
	
	@Override
	protected int on_beforeexport() {
		String v=getApvalue(Apinfo.APNAME_FORBIDEXPORT);
		if(v!=null && v.equals("true")){
			warnMessage("提示", "你没有导出授权");
			return -1;
		}
		return super.on_beforeexport();
	}


	/**
	 * 返回自动打印方案名
	 * 
	 * @return
	 */
	public String getAutoprintplan() {
		String v=getApvalue(Apinfo.APNAME_AUTOPRINTPLAN);
		if(v==null)v="";
		return v;
	}

	/**
	 * 设置自动打印方案
	 * 
	 * @param planname
	 * @return
	 */
	public void setAutoprintplan(String planname) {
		Apinfo apinfo = apmap.get(Apinfo.APNAME_AUTOPRINTPLAN);
		if (apinfo == null){
			apinfo=new Apinfo(Apinfo.APNAME_AUTOPRINTPLAN,Apinfo.APTYPE_PARAM);
			apmap.put(Apinfo.APNAME_AUTOPRINTPLAN,apinfo);
		}
		apinfo.setApvalue(planname);
	}


	/**
	 * 自动打印。查询授权属性"autoprintplan";，如果定义了，就用这个方案
	 * @param row
	 */
	public void autoDocprint(int row){
		Apinfo apinfo=getApinfo(Apinfo.APNAME_AUTOPRINTPLAN);
		String planname=apinfo.getApvalue();
		if(planname==null || planname.length()==0)return;
		docPrint(planname, row);
	}

	@Override
	protected boolean isColumneditable(int row, String colname) {
		boolean ret=super.isColumneditable(row, colname);
		if(ret==false)return ret;
		
		//授权是否禁止
		String apvalue=getApvalue("forbidedit_"+colname.toLowerCase());
		if(apvalue!=null && apvalue.equals("true")){
			return false;
		}
		return ret;
	}
	
	public void bindDataSetEnable(int row) {
		//如果有禁止,要设为readonly
		Enumeration<DBColumnDisplayInfo>en=formcolumndisplayinfos.elements();
		while(en.hasMoreElements()){
			DBColumnDisplayInfo col=en.nextElement();
			String apvalue=getApvalue("forbidedit_"+col.getColname().toLowerCase());
			if(apvalue!=null && apvalue.equals("true")){
				col.setReadonly(true);
			}
		}
		super.bindDataSetEnable(row);

	}
	
	@Override
	protected boolean isForbidReprint(){
		String s=getApvalue("forbidreprint");
		return s!=null && s.equals("true");
	}

}
