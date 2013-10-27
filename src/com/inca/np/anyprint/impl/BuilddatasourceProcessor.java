package com.inca.np.anyprint.impl;

import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.anyprint.Datasource;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;

/**
 * 
 * @author Administrator
 * 
 */
public class BuilddatasourceProcessor extends RequestProcessorAdapter {
	Category logger = Category.getInstance(BuilddatasourceProcessor.class);
	String COMMAND = "npclient:builddatasource";
	String COMMAND1 = "npclient:fetchdatasource";

	/**
	 * ����datacommand,ÿ��Ϊһ��datasource�� ����datasource���õ�һ���޼�¼��datatablemodel����
	 */
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		if (!cmd.getString().equals(COMMAND)
				&& !cmd.getString().equals(COMMAND1))
			return -1;

		DataCommand dcmd = (DataCommand) req.commandAt(1);
		DBTableModel dbmodel = dcmd.getDbmodel();


		Connection con = null;
		try {
			con = getConnection();
			if (req.getCommand().equals(COMMAND)) {
				DBTableModel newdbmodel = buildDatamodel(con, dbmodel);
				resp.addCommand(new StringCommand("+OK"));
				DataCommand respdcmd = new DataCommand();
				respdcmd.setDbmodel(newdbmodel);
				resp.addCommand(respdcmd);
			} else if (req.getCommand().equals(COMMAND1)) {
				// ��ѯ����
				ParamCommand paracmd = (ParamCommand) req.commandAt(2);
				String inputparam = paracmd.getValue("inputparam");
				DBTableModel newdbmodel = fetchData(con, dbmodel, inputparam);
				resp.addCommand(new StringCommand("+OK"));
				DataCommand respdcmd = new DataCommand();
				respdcmd.setDbmodel(newdbmodel);
				resp.addCommand(respdcmd);
			}
		} catch (Exception e) {
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return 0;
	}

	DBTableModel buildDatamodel(Connection con, DBTableModel dsdbmodel)
			throws Exception {
		Vector<DBColumnDisplayInfo> allcols = new Vector<DBColumnDisplayInfo>();
		for (int r = 0; r < dsdbmodel.getRowCount(); r++) {
			Datasource ds = new Datasource(dsdbmodel.getItemValue(r, "sql"),
					dsdbmodel.getItemValue(r, "type"));
			ds.setViewname(dsdbmodel.getItemValue(r, "viewname"));
			allcols.addAll(getCols(con, ds));
		}
		DBTableModel newdbmodel = new DBTableModel(allcols);
		return newdbmodel;

	}

	DBTableModel fetchData(Connection con, DBTableModel dsdbmodel,String inputparam)
			throws Exception {
		DBTableModel dbmodel = buildDatamodel(con,dsdbmodel);
		dbmodel.clearAll();
		String sql = dsdbmodel.getItemValue(0, "sql");
		sql = bindInputparam(sql, inputparam);
		DBTableModel tmpdbmodel = null;
		SelectHelper sh=new SelectHelper(sql);
		logger.debug(sql);
		tmpdbmodel=sh.executeSelect(con, 0, 10000);
		logger.debug("��ѯ��"+tmpdbmodel.getRowCount()+"����¼");
		dbmodel.bindMemds(tmpdbmodel);
		// ��ѯ��������Դ
		DBTableModel targetdbmodel=new DBTableModel(dbmodel.getDisplaycolumninfos());
		
		for (int row = 0; row < dbmodel.getRowCount(); row++) {
			DBTableModel tmptarget=expendRows(con,dbmodel,row,dsdbmodel,inputparam);
			targetdbmodel.appendDbmodel(tmptarget);			
		}
		return targetdbmodel;
	}

	/**
	 * ��dbmodel�е��и��Ƶ�targetdbmodel�У�����dsdbmodel�е�����Դ����չ��
	 * @param dbmodeldsdbmodelz
	 * @param row
	 * @param targetdbmodel
	 * @param dsdbmodel
	 * @return ����һ����¼չ���ļ�¼
	 * @throws Exception
	 */
	DBTableModel expendRows(Connection con,DBTableModel dbmodel, int row,
			 DBTableModel dsdbmodel,String inputparam) throws Exception{
		DBTableModel targetdbmodel=new DBTableModel(dbmodel.getDisplaycolumninfos());
		RecordTrunk samplerec=dbmodel.getRecordThunk(row);
		targetdbmodel.appendRecord(samplerec.copy());
		
		//��ÿ������Դ����չ��
		String sql="";
		for (int i = 1; i < dsdbmodel.getRowCount(); i++) {
			for(int r=0;r<targetdbmodel.getRowCount();r++){
				sql=dsdbmodel.getItemValue(i, "sql");
				sql = bindMaindsParam(sql, targetdbmodel, r, inputparam);
				SelectHelper sh=new SelectHelper(sql);
				logger.debug(sql);
				DBTableModel tmpdbmodel = sh.executeSelect(con, 0, 10000);
				logger.debug("��ѯ��"+tmpdbmodel.getRowCount()+"����¼");
				
				for (int subr = 0; subr < tmpdbmodel.getRowCount(); subr++) {
					if (subr > 0) {
						// ��ʱ��Ҫ��dbmodel��row���油��һ��
						targetdbmodel.insertRecord(targetdbmodel.getRecordThunk(r).copy(),r+1);
						r++;
					}
					for (int c = 0; c < tmpdbmodel.getColumnCount(); c++) {
						String cname = tmpdbmodel.getDBColumnName(c);
						targetdbmodel.setItemValue(r, cname, tmpdbmodel
								.getItemValue(subr, c));
					}
				}
			}
		}		
		
		return targetdbmodel;
	}

	Vector<DBColumnDisplayInfo> getCols(Connection con, Datasource ds)
			throws Exception {
		// ����sql���С�
		String sql = ds.getSql();
		sql = Datasource.replaceParamtonull(sql);
		SelectHelper sh = new SelectHelper(sql);
		DBTableModel tmpdbmodel = sh.executeSelect(con, 0, 1);
		return tmpdbmodel.getDisplaycolumninfos();
	}
	String bindInputparam(String sql, String inputparam) {
		return sql.replaceAll("\\{��ڲ���\\}", inputparam);
	}

	String bindMaindsParam(String sql, DBTableModel mastermodel, int row,
			String inputparam) throws Exception {
		String s = sql;
		StringBuffer sb = new StringBuffer();
		int p = 0;
		for (;;) {
			p = s.indexOf("{", p);
			if (p < 0)
				break;
			int p1 = s.indexOf("}", p);
			if (p1 < 0)
				break;
			String colname = s.substring(p + 1, p1);
			sb.append(s.subSequence(0, p));
			if (colname.equals("��ڲ���")) {
				sb.append(inputparam);
			} else {
				String v = mastermodel.getItemValue(row, colname);
				if (v == null) {
					throw new Exception("��������Դ�Ҳ���������Դ����" + colname + "��ԭʼsqlΪ"
							+ sql);
				}
				sb.append("'" + v + "'");
			}
			s = s.substring(p1 + 1);
		}
		sb.append(s);
		return sb.toString();

	}

}
