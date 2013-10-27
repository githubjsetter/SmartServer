package com.inca.npbi.server;

import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;

/**
 * �ɱ���instance����Ԥ�����ݵ�dm
 * @author user
 *
 */
public class GetBIreportdm_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npbi:ȡ����Ԥ��dbtablemodel";
	Category logger = Category.getInstance(GetBIreportdm_dbprocessor.class);
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String instanceid=pcmd.getValue("instanceid");
		
		Connection con=null;
		try {
			con=getConnection();
			String sql="select reportid,npbi_instanceid from npbi_instance where instanceid=?";
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(instanceid);
			DBTableModel instancedm=sh.executeSelect(con, 0, 1);
			if(instancedm.getRowCount()==0){
				throw new Exception("�Ҳ���instanceid="+instanceid);
			}
			String reportid=instancedm.getItemValue(0, "reportid");
			String npbi_instanceid=instancedm.getItemValue(0, "npbi_instanceid");
			//��ѯ�еĶ���
			sql="select columnname,coltype,title,precision,scale from npbi_basetable_column where reportid=? order by columnid";
			sh=new SelectHelper(sql);
			sh.bindParam(reportid);
			DBTableModel coldm=sh.executeSelect(con, 0,1000);
			Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo lineno=new DBColumnDisplayInfo("�к�","�к�","�к�");
			lineno.setDbcolumn(false);
			cols.add(lineno);

			DBColumnDisplayInfo col=null;
			for(int r=0;r<coldm.getRowCount();r++){
				String colname=coldm.getItemValue(r, "columnname");
				String coltype=coldm.getItemValue(r, "coltype");
				String title=coldm.getItemValue(r, "title");
				col=new DBColumnDisplayInfo(colname,coltype,title);
				cols.add(col);
				if(coltype.equalsIgnoreCase("number")){
					int scale=0;
					try{
					scale=Integer.parseInt(coldm.getItemValue(r, "scale"));
					}catch(Exception e){}
					col.setNumberscale(scale);
					col.setCalcsum(true);
				}
			}
			col=new DBColumnDisplayInfo("npbi_lineno","number","�����к�");
			cols.add(col);
			col=new DBColumnDisplayInfo("npbi_instanceid","varchar","ʱ��ά��");
			cols.add(col);

			
			DBTableModel resultdm=new DBTableModel(cols);
			resp.addCommand(new StringCommand("+OK"));
			DataCommand respdcmd=new DataCommand();
			respdcmd.setDbmodel(resultdm);
			resp.addCommand(respdcmd);
			
			//��ѯ������
			sql="select basetablename from npbi_report_def where reportid=?";
			sh=new SelectHelper(sql);
			sh.bindParam(reportid);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			String basetablename=dm.getItemValue(0, "basetablename");
			ParamCommand resppcmd=new ParamCommand();
			resppcmd.addParam("npbi_instanceid",npbi_instanceid);
			resppcmd.addParam("basetablename",basetablename);
			resp.addCommand(resppcmd);
			
		} catch (Exception e) {
			logger.error("Error",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		} finally {
			if(con!=null){
				con.close();
			}
		}
		
		return 0;
	} 
}
