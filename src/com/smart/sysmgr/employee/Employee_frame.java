package com.smart.sysmgr.employee;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

/*����"���Ź���"Frame����*/
public class Employee_frame extends Steframe{
	public Employee_frame() throws HeadlessException {
		super("��Ա����");
	}

	protected CSteModel getStemodel() {
		return new Employee_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Employee_frame w=new Employee_frame();
		w.pack();
		w.setVisible(true);
	}
}
