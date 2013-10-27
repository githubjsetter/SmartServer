package com.inca.np.demo.extend;

import java.awt.Frame;
import java.awt.HeadlessException;

import com.inca.np.gui.control.COtherquerycontrol;
import com.inca.np.gui.control.CQueryDialog;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Testsimplequery extends CQueryDialog {
	public Testsimplequery(Frame owner, String title,
			COtherquerycontrol otherquerycontrol) throws HeadlessException {

		super(owner, title, otherquerycontrol);
		setSimplemode(true);

		querycond = new SetUpAndDownCond();
		this.initControl(querycond);

	}

	@Override
	protected String getQuerybuttontext() {

		return "����(F8)";
	}

	class SetUpAndDownCond extends Querycond {
		SetUpAndDownCond() {

			DBColumnDisplayInfo datecol = new DBColumnDisplayInfo("enddate",
					"date", "��ֹ����");
			Querycondline qcl = new Querycondline(this, datecol);
			qcl.setValue("2008-05-01");
			add(qcl);
			
			datecol = new DBColumnDisplayInfo("startdate",
					"date", "��ʼ����");
			qcl = new Querycondline(this, datecol);
			add(qcl);
		}
	}
	
	public static void main(String[] args) {
		Testsimplequery dlg=new Testsimplequery(null,"test",null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
