package com.smart.platform.rule.setup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SendHelper;

public class StoreproceditDlg extends CDialog {
	private JTextArea textSyntax;
	String storeprocname = null;
	boolean ok;

	public boolean isOk() {
		return ok;
	}

	public String getStoreprocname() {
		return storeprocname;
	}

	public StoreproceditDlg(Dialog parent) {
		super(parent, "编辑存储过程", true);
		init();
		this.setPreferredSize(new Dimension(800, 700));
		this.localCenter();
	}

	public StoreproceditDlg(Frame parent) {
		super(parent, "编辑存储过程", true);
		init();
		this.setPreferredSize(new Dimension(800, 700));
		this.localCenter();
	}

	public void editStoreproc(String procname) throws Exception {
		this.storeprocname = procname;
		loadSyntax();
	}

	public void createNew() {
		String syntax = "PROCEDURE zx_后处理 (p_docid in number,p_employeeid in number,p_roleid in number)\n";
		syntax += "AS\n";
		syntax += "BEGIN\n";
		syntax += "\t--输入代码\n";
		syntax += "END;\n";
		this.textSyntax.setText(syntax);
	}

	public void createNewprequery() {
		String syntax = "PROCEDURE zx_查询前处理 (p_wheres in varchar,p_employeeid in number,p_roleid in number,p_otherwheres out varchar)\n";
		syntax += "AS\n";
		syntax += "BEGIN\n";
		syntax += "\t--输入代码\n";
		syntax += "\tp_otherwheres:='';\n";
		syntax += "END;\n";
		this.textSyntax.setText(syntax);
	}

	void loadSyntax() throws Exception {
		String sql = "select text from user_source where name='"
				+ storeprocname + "' order by line";
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel dbmodel = sqlh.doSelect(sql, 0, 100000);
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String text = dbmodel.getItemValue(r, "text");
			sb.append(text);
		}
		textSyntax.setText(sb.toString());
	}

	void loadError() throws Exception {
		String sql = "select line,position,text from user_errors where name='"
				+ storeprocname + "' order by line";
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel dbmodel = sqlh.doSelect(sql, 0, 100000);
		errordbmodel.clearAll();
		errordbmodel.bindMemds(dbmodel);
		errortable.tableChanged(new TableModelEvent(errordbmodel));
		errortable.autoSize();
	}

	void init() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		cp.add(sp, BorderLayout.CENTER);

		sp.setDividerLocation(400);
		textSyntax = new JTextArea(30, 40);
		sp.setLeftComponent(new JScrollPane(textSyntax));

		JPanel jp = createBottompane();
		sp.setRightComponent(jp);
	}

	DBTableModel errordbmodel = null;
	private CTable errortable;

	JPanel createBottompane() {
		JPanel jp = new JPanel();

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jp.add(sp);
		errordbmodel = createErrordbmodel();
		errortable = new CTable(errordbmodel);
		JScrollPane tablesp = new JScrollPane(errortable);
		tablesp.setPreferredSize(new Dimension(600, 200));
		sp.setLeftComponent(tablesp);

		JPanel toolpane = new JPanel();
		sp.setRightComponent(toolpane);

		BoxLayout boxl = new BoxLayout(toolpane, BoxLayout.Y_AXIS);
		toolpane.setLayout(boxl);
		JButton btn;
		btn = new JButton("编译");
		btn.setActionCommand("compile");
		btn.addActionListener(this);
		toolpane.add(btn);

		btn = new JButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		toolpane.add(btn);

		btn = new JButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		toolpane.add(btn);

		return jp;
	}

	DBTableModel createErrordbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;

		col = new DBColumnDisplayInfo("line", "number", "行号");
		cols.add(col);
		col = new DBColumnDisplayInfo("position", "number", "列号");
		cols.add(col);
		col = new DBColumnDisplayInfo("text", "number", "错误");
		cols.add(col);

		return new DBTableModel(cols);
	}
	
	
	void onOk(){
		try {
			if(!compile()){
				JOptionPane.showMessageDialog(this, "编译失败");
			}
			ok=true;
			setVisible(false);
			this.dispose();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
	}
	void onCancel(){
		ok=false;
		setVisible(false);
		this.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("compile")) {
			try {
				compile();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
		}else if(cmd.equals("ok")){
			onOk();
		}else if(cmd.equals("cancel")){
			onCancel();
		}
	}

	boolean compile() throws Exception {
		ClientRequest req = new ClientRequest("npclient:createstoreproc");
		ParamCommand pcmd = new ParamCommand();
		String syntax = textSyntax.getText();
		//取名字
		Pattern wspattern=Pattern.compile("\\s");
		Matcher m=wspattern.matcher(syntax);
		if(!m.find()){
			JOptionPane.showMessageDialog(this, "语法错误!");
			return false;
		}
		int p=m.end();
		if(!m.find(p+1)){
			JOptionPane.showMessageDialog(this, "语法错误!");
			return false;
		}
		int p1=m.end();
		
		this.storeprocname=syntax.substring(p,p1).trim().toUpperCase();
		if(storeprocname.length()==0){
			JOptionPane.showMessageDialog(this, "语法错误!");
			return false;
		}
		
		
		pcmd.addParam("syntax", syntax);
		req.addCommand(pcmd);

		ServerResponse resp = SendHelper.sendRequest(req);
		StringCommand cmd0 = (StringCommand) resp.commandAt(0);
		String strcmd = cmd0.getString();
		if (!strcmd.startsWith("+OK")) {
			JOptionPane.showMessageDialog(this, strcmd);
			return false;
		}
		this.loadError();

		return true;

	}

	public static void main(String[] args) {
		StoreproceditDlg editdlg = new StoreproceditDlg((Frame) null);
		editdlg.pack();
		try {
			//editdlg.editStoreproc("ZX_PROC1");
			editdlg.createNew();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		editdlg.setVisible(true);
	}
}
