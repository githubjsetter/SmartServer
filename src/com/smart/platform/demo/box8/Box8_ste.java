package com.smart.platform.demo.box8;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 八卦箱体
 * @author user
 *
 */
public class Box8_ste extends CSteModel{

	public Box8_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if("生成".equals(command)){
			genBox8();
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	/*
	@Override
	protected void loadDBColumnInfos() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		col=new DBColumnDisplayInfo("行号","行号","行号");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("boxno","number","箱号");
		cols.add(col);

		col=new DBColumnDisplayInfo("col1","number","箱底/买点");
		cols.add(col);

		col=new DBColumnDisplayInfo("col2","number","强压力");
		cols.add(col);
		col=new DBColumnDisplayInfo("col3","number","黑马线");
		cols.add(col);
		col=new DBColumnDisplayInfo("col4","number","中轴逃命止损");
		cols.add(col);
		col=new DBColumnDisplayInfo("col5","number","弱支撑(压力)");
		cols.add(col);
		col=new DBColumnDisplayInfo("col6","number","强支撑(压力)");
		cols.add(col);
		col=new DBColumnDisplayInfo("col7","number","出货止赢");
		cols.add(col);
		col=new DBColumnDisplayInfo("col8","number","箱顶/目标价");
		cols.add(col);

		this.formcolumndisplayinfos=cols;
		
	}
*/
	
	int start=1334;
	int boxno=0;
	int cur=0;
	int step=5;
	private CNumberTextField textStart;
	void genBox8(){
		try{
			start=Integer.parseInt(textStart.getText());
		}catch(Exception e){
			start=326;
		}
		dbmodel.clearAll();
		cur=start;
		for(boxno=0;boxno<40;boxno++){
			genOnebox();
		}
		
		tableChanged();
		table.autoSize();
	}
	
	void genOnebox(){
		dbmodel.appendRow();
		dbmodel.setItemValue(boxno, "boxno", String.valueOf(boxno+1));

		cur += boxno * step;
		dbmodel.setItemValue(boxno, "col1",String.valueOf(cur));

		for(int c=2;c<=8;c++){
			cur += (boxno+1) * step;
			dbmodel.setItemValue(boxno, "col"+c,String.valueOf(cur));
		}
		dbmodel.setdbStatus(boxno, RecordTrunk.DBSTATUS_SAVED);
	}

	@Override
	protected JPanel createSecondtoolbar() {
		JPanel jp=new JPanel();
		CFormlayout layout=new CFormlayout(2,2);
		jp.setLayout(layout);
		
		JLabel lb=new JLabel("起点");
		jp.add(lb);
		
		textStart = new CNumberTextField(0);
		jp.add(textStart);
		textStart.setText("1047");
		Dimension size=new Dimension(100,27);
		textStart.setPreferredSize(size);
		
		JButton btn=null;
		btn=new JButton("生成");
		btn.setActionCommand("生成");
		btn.addActionListener(this);
		jp.add(btn);
		
		
		return jp;
	}
	
	
}
