package com.smart.workflow.client;
import com.smart.extension.mde.CMdeModelAp;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.util.SendHelper;

/*����"����ʵ��"�ܵ�ϸĿModel*/
public class Wfinst_mde extends CMdeModelAp{
	public Wfinst_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Wfinst_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Wfinst_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "wfinstanceid";
	}
	public String getDetailRelatecolname() {
		return "wfinstanceid";
	}
	public String getSaveCommandString() {
		return "Wfinst_mde.��������ʵ��";
	}
	@Override
	protected int on_beforemodifymaster(int row) {
		return 0;
	}
	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("������������")){
			restartWf();
			return 0;
		}
		return super.on_actionPerformed(command);
	}
	
	/**
	 * ������������
	 */
	void restartWf() {
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ","��ѡ��һ��ʵ��");
			return;
		}
		
		String wfinstanceid=getMasterModel().getItemValue(row, "wfinstanceid");
		ClientRequest req=new ClientRequest("Wfinst_mde.������������ʵ��");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("wfinstanceid",wfinstanceid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String resps=resp.getCommand();
			if(!resps.startsWith("+OK")){
				errorMessage("����",resps);
				return;
			}
			infoMessage("��ʾ","�����������������");
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
			return;
		}
		
	}
	
}
