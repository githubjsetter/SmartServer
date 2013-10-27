package com.inca.npbi.client.design.param;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JTextField;

import com.inca.np.gui.control.COtherquerycontrol;
import com.inca.np.gui.control.CQueryDialog;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

/**
 * 查询条件输入窗口
 * @author user
 *
 */
public class BIQueryDlg extends CQueryDialog{
	Vector<BIReportparamdefine> params;
	public BIQueryDlg(Frame owner, String title,
			Vector<BIReportparamdefine> params) throws HeadlessException {
		super(owner, title, null);
		this.params=params;
		setSimplemode(true);
		initControl(buildQuerycond(params));
		
/*		for(int i=0;i<params.size();i++){
			BIReportparamdefine pdef=params.elementAt(i);
			if(textcontrols[i] instanceof JTextField){
				JTextField tc=(JTextField)textcontrols[i];
				tc.setText(pdef.getRealInitvalue());
			}
		}
*/
		setDefaultCloseOperation(CQueryDialog.HIDE_ON_CLOSE);
	}
	
	public Querycond buildQuerycond(Vector<BIReportparamdefine> params) {
		Querycond cond = new Querycond();
		Enumeration<BIReportparamdefine> en = params.elements();
		while (en.hasMoreElements()) {
			BIReportparamdefine pdefine = en.nextElement();
			DBColumnDisplayInfo colinfo = new DBColumnDisplayInfo(
					pdefine.paramname, pdefine.paramtype, pdefine.title);
			if(pdefine.hovclass.length()>0){
				Hovdefine hovdefine=new Hovdefine(pdefine.hovclass,pdefine.paramname);
				hovdefine.putColpair(pdefine.hovcols, pdefine.paramname);
				colinfo.setHovdefine(hovdefine);
			}
			Querycondline ql=new Querycondline(cond,colinfo);
			cond.add(ql);
		}
		return cond;
	}
	
	

	@Override
	public void onconfirm(boolean confirm) {
		if(confirm){
			//检查输入
			for(int i=0;i<params.size();i++){
				BIReportparamdefine pdef=params.elementAt(i);
				if(pdef.mustinput){
					if (textcontrols[i] instanceof JTextField) {
						JTextField text=(JTextField)textcontrols[i];
						if(text.getText().trim().length()==0){
							warnMessage("提示",params.elementAt(i).title+"必须输入");
							return;
						}
					}
				}
			}
		}
		super.onconfirm(confirm);
		bindInputvalue();
	}

	void bindInputvalue(){
		for(int i=0;i<params.size();i++){
			BIReportparamdefine pdef=params.elementAt(i);
			Querycondline ql=querycond.elementAt(i);
			pdef.setInputvalue(ql.getValue());
		}
	}

	/**
	 * 将params中的值设到编辑控件上
	 * @param params
	 */
	public void bindValue(Vector<BIReportparamdefine> params) {
		for(int i=0;i<params.size();i++){
			BIReportparamdefine pdef=params.elementAt(i);
			Querycondline ql=querycond.elementAt(i);
			ql.setValue(pdef.getInputvalue());
		}
	}

}
