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
 * ��������
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
		if("����".equals(command)){
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
		col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("boxno","number","���");
		cols.add(col);

		col=new DBColumnDisplayInfo("col1","number","���/���");
		cols.add(col);

		col=new DBColumnDisplayInfo("col2","number","ǿѹ��");
		cols.add(col);
		col=new DBColumnDisplayInfo("col3","number","������");
		cols.add(col);
		col=new DBColumnDisplayInfo("col4","number","��������ֹ��");
		cols.add(col);
		col=new DBColumnDisplayInfo("col5","number","��֧��(ѹ��)");
		cols.add(col);
		col=new DBColumnDisplayInfo("col6","number","ǿ֧��(ѹ��)");
		cols.add(col);
		col=new DBColumnDisplayInfo("col7","number","����ֹӮ");
		cols.add(col);
		col=new DBColumnDisplayInfo("col8","number","�䶥/Ŀ���");
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
		
		JLabel lb=new JLabel("���");
		jp.add(lb);
		
		textStart = new CNumberTextField(0);
		jp.add(textStart);
		textStart.setText("1047");
		Dimension size=new Dimension(100,27);
		textStart.setPreferredSize(size);
		
		JButton btn=null;
		btn=new JButton("����");
		btn.setActionCommand("����");
		btn.addActionListener(this);
		jp.add(btn);
		
		
		return jp;
	}
	
	
}
