package com.smart.adminclient.remotesql;

import java.awt.HeadlessException;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 
 * @author Administrator
 *Զ�̲�ѯselect.��select��䶯̬���ɷ��ؽ��
 */
public class Remotesql_ste extends CSteModel{

	public Remotesql_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected CStetoolbar createToolbar() {
		return null;
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		// ����Ƿ�����, ���ܸ�������
		if(getdbStatus(row)!=RecordTrunk.DBSTATUS_NEW){
			DBColumnDisplayInfo colinfo=getDBColumnDisplayInfo(colname);
			if(colinfo.isIspk()){
				warnMessage("��ʾ","�����޸�������ֵ");
				int colindex=getDBtableModel().getColumnindex(colname);
				RecordTrunk rec=getDBtableModel().getRecordThunk(row);
				String dbvalue=rec.getdbValueAt(colindex);
				setItemValue(row,colname,dbvalue);
				return;
			}
		}
		
		super.on_itemvaluechange(row, colname, value);
	}

	
	
}
