package com.smart.platform.server.process;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.*;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.rule.setup.RuleRepository;
import com.smart.platform.server.MdesaveIF;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.UpdateLogger;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.ZipHelper;
import com.smart.server.server.sysproc.CurrentappHelper;

import org.apache.log4j.Category;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 总单细目保存处理类
 */
public abstract class MdeProcessor extends RequestProcessorAdapter implements
		MdesaveIF {
	Category logger = Category.getInstance(MdeProcessor.class);

	private boolean zxinited=false;
	/**
	 * 总单细目CMdeModel
	 */
	protected CMdeModel mdemodel = getMdeModel();
	/**
	 * 处理函数
	 * @param userinfo 用户信息
	 * @param req 请求
	 * @param resp 响应
	 * 
	 */
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		CommandBase cmd = req.commandAt(0);
		if (!(cmd instanceof StringCommand)) {
			return -1;
		}

		StringCommand strcmd = (StringCommand) cmd;
		if (!strcmd.getString().equals(mdemodel.getSaveCommandString())) {
			return -1;
		}

		DataCommand cmd2 = (DataCommand) req.commandAt(1);
		DBTableModel masterdbmodel = cmd2.getDbmodel();

		DataCommand cmd3 = (DataCommand) req.commandAt(2);
		DBTableModel detaildbmodel = cmd3.getDbmodel();

		Connection con = null;

		try {
			con = getConnection();
			Vector<String> updatelogs = new Vector<String>();
			Vector<Vector<String>> alldetaillogs=new Vector<Vector<String>>();
			Vector<ResultCommand> results = new Vector<ResultCommand>();
			if (UpdateLogger.getInstance().isNeeduploadlog(con, getMastertablename())||
					UpdateLogger.getInstance().isNeeduploadlog(con, getDetailtablename())) {
				for (int r = 0; r < masterdbmodel.getRowCount(); r++) {
					String s = UpdateLogger.createLogstring(masterdbmodel, r);
					updatelogs.add(s);
					
					Vector<String> detaillogs=new Vector<String>();
					alldetaillogs.add(detaillogs);
					String tmppkid=masterdbmodel.getTmppkid(r);
					for(int j=0;j<detaildbmodel.getRowCount();j++){
						if(detaildbmodel.getRecordThunk(j).getRelatevalue().equals(tmppkid)){
							 s = UpdateLogger.createLogstring(detaildbmodel, j);
							 detaillogs.add(s);
						}
					}
				}
			}
			
			doSave(con, userinfo, masterdbmodel, detaildbmodel, results, true);
			if (UpdateLogger.getInstance().isNeeduploadlog(con, getMastertablename())||
					UpdateLogger.getInstance().isNeeduploadlog(con, getDetailtablename())) {
				ResultCommand mresult=results.elementAt(0);
				String pkcolname=masterdbmodel.getPkcolname();
				for(int r=0;r<mresult.getLineresultCount();r++){
					if(mresult.getLineresult(r).getSaveresult()==0){
						String pkvalue=masterdbmodel.getItemValue(r, pkcolname);
						UpdateLogger.addLog(con, getMastertablename(), userinfo.getUserid(),
								userinfo.getUsername(), updatelogs.elementAt(r),pkvalue);
						Vector<String> detaillogs=alldetaillogs.elementAt(r);
						String dtlpkcolname=detaildbmodel.getPkcolname();
						for(int j=0;j<detaillogs.size();j++){
							String dtlpkvalue=detaildbmodel.getItemValue(j, dtlpkcolname);
							UpdateLogger.addLog(con, getDetailtablename(), userinfo.getUserid(),
									userinfo.getUsername(),detaillogs.elementAt(j),dtlpkvalue);	
						}
					}
				}
			}			/**
			 * 统一提交
			 */
			con.commit();
			Enumeration<ResultCommand> en = results.elements();
			while (en.hasMoreElements()) {
				resp.addCommand(en.nextElement());
			}

		} catch (Exception e) {
			con.rollback();
			logger.error("save", e);
			resp.addCommand(new StringCommand("-ERROR保存失败:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return 0;
	}

	/**
	 * 保存函数
	 * @param con 连接
	 * @param userinfo 用户信息
	 * @param masterdbmodel 总单数据源
	 * @param detaildbmodel 细单数据源
	 * @param results  结果集
	 * @param commit true:一条总单和所有相关细单保存成功就提交；false不提交
	 * @throws Exception
	 */
	public void doSave(Connection con, Userruninfo userinfo,
			DBTableModel masterdbmodel, DBTableModel detaildbmodel,
			Vector<ResultCommand> results, boolean commit) throws Exception {
		DBModel2Jdbc.save2DB(con, userinfo, getMastertablename(), mdemodel
				.getMasterModel().getTablename(), masterdbmodel, mdemodel
				.getMasterRelatecolname(), getDetailtablename(), mdemodel
				.getDetailModel().getTablename(), detaildbmodel, mdemodel
				.getDetailRelatecolname(), this, results, commit);

		if (userinfo.isDevelop()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
			} catch (UnsupportedEncodingException e) {
				logger.error("error", e);
			}
			selfCheckMaster(con,userinfo, results.elementAt(0), out);
			selfCheckDetail(con,userinfo, results.elementAt(1), out);
			out.flush();
			String checks = new String(bout.toByteArray(), "gbk");
			if (checks.length() > 0) {
				throw new Exception(this.getClass().getName() + "自检查失败:"
						+ checks);
			}
		}
	}

	/**
	 * 返回CMdeModel。必须重载
	 * @return
	 */
	protected abstract CMdeModel getMdeModel();

	/**
	 * 返回CMdeModel实例
	 * @return
	 */
	public CMdeModel getMdeModelInst() {
		return mdemodel;
	}

	/**
	 * 返回总单表名。必须重载
	 * @return
	 */
	protected abstract String getMastertablename();

	/**
	 * 返回细单表名。必须重载。
	 * @return
	 */
	protected abstract String getDetailtablename();

	/**
	 * 返回总单表名
	 * @return
	 */
	public String getDbMastertablename() {
		return getMastertablename();
	}

	/**
	 * 返回细单表名
	 * @return
	 */
	public String getDbDetailtablename() {
		return getDetailtablename();
	}

	/**
	 * 保存总单前处理。对每个总单调用一次
	 * @param con 连接
	 * @param userruninfo 用户信息
	 * @param dbmodel 总单数据源
	 * @param row 数据源的行
	 */
	public void on_beforesavemaster(Connection con, Userruninfo userruninfo,
			DBTableModel dbmodel, int row) throws Exception {

	}

	/**
	 * 保存总单后处理。对每个总单调用一次
	 * @param con 连接
	 * @param userruninfo 用户信息
	 * @param masterdbmodel 总单数据源
	 * @param masterrow 总单数据源行
	 * @param detaildbmodel 保存成功后的细单数据源 
	 */
	public void on_aftersavemaster(Connection con, Userruninfo userruninfo,
			DBTableModel masterdbmodel, int masterrow,
			DBTableModel detaildbmodel) throws Exception {

		
		//是否有专项？
		String opid=userruninfo.getActiveopid();
		if(!zxinited && opid!=null && opid.length()>0){
			File zxzipfile=new File(CurrentappHelper.getClassesdir(),"专项开发/"+opid+".zip");
			if (zxzipfile.exists()){
				// 从zxfile中找出ste.model
				File tempfile = null;
				BufferedReader rd=null;
				try {
					tempfile = File.createTempFile("temp", ".model");
					if (ZipHelper.extractFile(zxzipfile, "ste.rule", tempfile)) {
						rd = null;
						rd = DBColumnInfoStoreHelp.getReaderFromFile(tempfile);
						Vector<Rulebase> rules = RuleRepository.loadRules(rd);
						rd.close();
						Ruleenginee ruleeng = new Ruleenginee();
						ruleeng.setRuletable(rules);
						mdemodel.setRuleeng(ruleeng);
						zxinited=true;
					}
				} catch (Exception e) {
					logger.error("e", e);
				} finally {
					if (tempfile != null) {
						tempfile.delete();
					}
				}
			}
		}
		
		String procname=mdemodel.getStoreprocname();
		if(procname==null || procname.length()==0) return;
		System.out.println("procname="+procname);
		
		DBTableModel dbmodel=mdemodel.getMasterModel().getDBtableModel();
		String pkcolname=null;
		Enumeration<DBColumnDisplayInfo> en=dbmodel.getDisplaycolumninfos().elements();
		while(en.hasMoreElements()){
			DBColumnDisplayInfo colinfo=en.nextElement();
			if(colinfo.isIspk()){
				pkcolname=colinfo.getColname();
				break;
			}
		}
		if(pkcolname==null){
			throw new Exception("没定义主键列，因此无法调用后处理存储过程"+procname);
		}

		CallableStatement call=null;
		try{
			String sql="{call "+procname+"(?,?,?)}";
			call=con.prepareCall(sql);
			call.setString(1, masterdbmodel.getItemValue(masterrow, pkcolname));
			call.setString(2, userruninfo.getUserid());
			call.setString(3, userruninfo.getRoleid());
			call.execute();
		}finally{
			if(call!=null){
				call.close();
			}
		}
		
	}

	/**
	 * 保存细单前处理
	 * @param con 连接
	 * @param userruninfo 用户信息
	 * @param dbmodel 数据源
	 * @param row 数据源行 
	 */
	public void on_beforesave(Connection con, Userruninfo userruninfo,
			DBTableModel dbmodel, int row) throws Exception {

	}

	/**
	 * 保存细单后处理
	 * @param con 连接
	 * @param userruninfo 用户信息
	 * @param saveddtldbmodel 保存后的细单数据源
	 * @param 细单数据源行 
	 */
	public void on_aftersave(Connection con, Userruninfo userruninfo,
			DBTableModel saveddtldbmodel, int row) throws Exception {

	}

	/**
	 * 自检总单
	 * @param runuserinfo
	 * @param resultcmd 
	 * @param out
	 */
	protected void selfCheckMaster(Connection con,Userruninfo runuserinfo,
			ResultCommand resultcmd, PrintWriter out) {
		for (int i = 0; i < resultcmd.getLineresultCount(); i++) {
			RecordTrunk dtlrec = resultcmd.getLineresult(i);
			if (dtlrec.getSaveresult() == 0) {
				// 检查出入库单
				DBTableModel dbmdel = this.getMdeModel().getDetailModel()
						.getDBtableModel().copyStruct();
				dbmdel.appendRecord(dtlrec);
				selfCheckOneMaster(con,runuserinfo, dbmdel, out);
			}

		}
	}

	/**
	 * 自检细单
	 * @param runuserinfo
	 * @param resultcmd
	 * @param out
	 */
	protected void selfCheckDetail(Connection con,Userruninfo runuserinfo,
			ResultCommand resultcmd, PrintWriter out) {
		for (int i = 0; i < resultcmd.getLineresultCount(); i++) {
			RecordTrunk dtlrec = resultcmd.getLineresult(i);
			if (dtlrec.getSaveresult() == 0) {
				// 检查出入库单
				DBTableModel dbmdel = this.getMdeModel().getDetailModel()
						.getDBtableModel().copyStruct();
				dbmdel.appendRecord(dtlrec);
				selfCheckOneDetail(con,runuserinfo, dbmdel, out);
			}

		}
	}

	/**
	 * 自检一行总单
	 * @param runuserinfo
	 * @param dbmodel 只有一行总单记录
	 * @param out
	 */
	protected void selfCheckOneMaster(Connection con,Userruninfo runuserinfo,
			DBTableModel dbmodel, PrintWriter out) {
	}

	/**
	 * 自检查一行细单
	 * @param runuserinfo
	 * @param dbmodel 只有一行细单记录
	 * @param out 
	 */
	protected void selfCheckOneDetail(Connection con,Userruninfo runuserinfo,
			DBTableModel dbmodel, PrintWriter out) {
	}

}
