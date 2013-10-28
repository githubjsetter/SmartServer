package com.smart.adminclient.remotesql;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ResultCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.SqlCommand;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListenerAdaptor;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;

public class Remotesql_frame extends Steframe implements ActionListener {

	JButton btnquery = null;
	JButton btnsave = null;
	private boolean updateable=false;

	public Remotesql_frame() throws HeadlessException {
		super("远程SQL查询");
	}
	
	




	@Override
	protected CSteModel getStemodel() {
		// TODO Auto-generated method stub
		Remotesql_ste ste = new Remotesql_ste(this, "远程sql查询");
		ste.addActionListener(new StemodelListener());
		return ste;
	}

	/**
	 * 用Split分为上下. 上面是输入sql语句的textarea 下面是table
	 */
	@Override
	protected void initControl() {
		stemodel = getStemodel();

		Container cp = getContentPane();
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		JComponent jcp = (JComponent) cp;
		InputMap map = jcp
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(vkf8, CSteModel.ACTION_QUERY);
		jcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new MyAction(CSteModel.ACTION_QUERY));

		cp.setLayout(new BorderLayout());
		cp.add(new RemotesqlToolbar(), BorderLayout.NORTH);

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setDividerLocation(200);
		sp.setRightComponent(stemodel.getRootpanel());
		map = sp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(vkf8, CSteModel.ACTION_QUERY);
		sp.getActionMap().put(CSteModel.ACTION_QUERY,
				new MyAction(CSteModel.ACTION_QUERY));
		dumpKeyevent(sp, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		JPanel sqlpanel = createSqlpanel();
		sp.setLeftComponent(sqlpanel);

		cp.add(sp, BorderLayout.CENTER);

		setHotkey();
		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);
		stemodel.onstartRun();

	}

	class MyAction extends AbstractAction {
		public MyAction(String name) {
			super(name);
			super.putValue(super.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(CSteModel.ACTION_QUERY)) {
				doQuery();
			}
		}

	}

	JTextArea textsql;

	protected JPanel createSqlpanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		textsql = new JTextArea("Select * from tab where rownum<=10");
		InputMap inputmap = textsql
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		inputmap.put(vkf8, CSteModel.ACTION_QUERY);
		textsql.getActionMap().put(CSteModel.ACTION_QUERY,
				new MyAction(CSteModel.ACTION_QUERY));

		jp.add(new JScrollPane(textsql), BorderLayout.CENTER);

		return jp;
	}

	class RemotesqlToolbar extends JToolBar {

		public RemotesqlToolbar() {
			super();
			btnquery = new JButton("查询F8");
			btnquery.setFocusable(false);
			btnquery.setActionCommand(CSteModel.ACTION_QUERY);
			btnquery.addActionListener(Remotesql_frame.this);
			add(btnquery);

			btnsave = new JButton("保存F9");
			btnsave.setFocusable(false);
			btnsave.setActionCommand(CSteModel.ACTION_SAVE);
			btnsave.addActionListener(Remotesql_frame.this);
			add(btnsave);
}

	}

	String sql;
	boolean querying = false;
	DBTableModel resultdbmodel = null;
	private String tablename;

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CSteModel.ACTION_QUERY)) {
			if (!querying) {
				doQuery();
			} else {
				querying = false;
			}
		}
	}

	protected void doQuery() {
		// 由sql进行查询
		sql = textsql.getText();
		if (!sql.toLowerCase().trim().startsWith("select")) {
			warnMessage("提示", "请输入select开始的sql");
			return;
		}
		onRetrievestart();
		Thread t = new Thread(new QueryThread());
		t.start();
	}

	protected void onRetrievestart() {
		updateable=false;
		btnsave.setEnabled(false);
		resultdbmodel = null;
		stemodel.setStatusmessage("开始查询....");
		btnquery.setText("中止查询");
		querying = true;
	}

	protected void onRetrieveend() {
		btnquery.setText("查询F8");
		stemodel.setStatusmessage("查询结束");
		querying = false;
		if (resultdbmodel != null) {
			for (int r = 0; r < resultdbmodel.getRowCount(); r++) {
				// 补行号
				RecordTrunk rec = resultdbmodel.getRecordThunk(r);
				rec.insertElementAt("", 0);
				rec.getdbValues().insertElementAt("", 0);
			}

			Vector<DBColumnDisplayInfo> colinfos = resultdbmodel
					.getDisplaycolumninfos();
			Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo col = en.nextElement();
				col.setTitle(col.getColname());
				if(updateable){
					col.setReadonly(false);
				}else{
					col.setReadonly(true);
				}
			}
			colinfos.insertElementAt(new CLinenoDisplayinfo(), 0);
			stemodel.setFormcolumndisplayinfos(colinfos);
			stemodel.setTable(null);
			stemodel.recreateDBModel();
			stemodel.setDBtableModel(resultdbmodel);
			stemodel.setStatusmessage("查询结束,共查询到" + resultdbmodel.getRowCount()
					+ "条记录");
		}
		
		if(updateable){
			btnsave.setEnabled(true);
			stemodel.setTableeditable(updateable);
		}else{
			stemodel.setTableeditable(false);
		}
	}
	
	protected void doSave(){
		if(!updateable){
			return;
		}
		
		//保存
		if(stemodel.getTable().getCellEditor()!=null){
			stemodel.getTable().getCellEditor().stopCellEditing();
		}
		
		ClientRequest req=new ClientRequest("generalsave");
		ParamCommand paramcmd=new ParamCommand();
		paramcmd.addParam("tablename",tablename);
		req.addCommand(paramcmd);
		
		DBTableModel savemodel=stemodel.getModifiedDbmodel();
		DataCommand datacmd=new DataCommand();
		datacmd.setDbmodel(savemodel);
		req.addCommand(datacmd);
		
		ServerResponse resp=null;
		//发送
		try
		{
			resp=SendHelper.sendRequest(req);
		}catch(Exception e){
			//logger.error("ERROR",e);
			errorMessage("错误",e.getMessage());
			return;
		}
		
		StringCommand resp0=(StringCommand)resp.commandAt(0);
		if(resp0.getString().startsWith("+OK")==false){
			errorMessage("错误",resp0.getString());
			return;
		}
		
		//设置返回结果
		ResultCommand resp1=(ResultCommand)resp.commandAt(1);
		stemodel.setLineresults(resp1.getLineresults());

		stemodel.tableChanged();
		
	}

	class QueryThread implements Runnable {
		public void run() {
			doRetrieve(sql);
		}
	}

	DBTableModel doRetrieve(String sql) {
		RemotesqlHelper helper = new RemotesqlHelper();
		int startrow = 0;
		DBTableModel dbmodel = null;
		do {
			try {
				ClientRequest req = new ClientRequest("selectwithpk");
				SqlCommand sqlcmd = new SqlCommand(sql);
				sqlcmd.setStartrow(startrow);
				sqlcmd.setMaxrowcount(DefaultNPParam.fetchmaxrow);

				req.addCommand(sqlcmd);
				ServerResponse resp = SendHelper.sendRequest(req);
				StringCommand respcmd0 = (StringCommand) resp.commandAt(0);
				if (!respcmd0.getString().startsWith("+OK")) {
					errorMessage("查询失败", respcmd0.getString());
					return null;
				}
				// 数据
				DataCommand respcmd1 = (DataCommand) resp.commandAt(1);
				dbmodel = respcmd1.getDbmodel();

				// 如果paramcmd中有tablename,说明可更新
				if (startrow == 0) {
					ParamCommand respcmd2 = (ParamCommand) resp.commandAt(2);
					tablename = respcmd2.getValue("tablename");
					if (tablename != null && tablename.length() > 0) {
						// 说明可以更新
						updateable=true;
					}
				}

			} catch (Exception e) {
				errorMessage("查询失败", e.getMessage());
				onRetrieveend();
				return resultdbmodel;
			}
			if (resultdbmodel == null) {
				resultdbmodel = dbmodel;
			} else {
				resultdbmodel.bindMemds(dbmodel);
			}
			startrow += dbmodel.getRowCount();
			stemodel.setStatusmessage("已查询到" + resultdbmodel.getRowCount()
					+ "条记录");
		} while (dbmodel.hasmore() && querying);
		onRetrieveend();
		return resultdbmodel;
	}

	class StemodelListener extends CSteModelListenerAdaptor {

		@Override
		public int on_beforequery() {
			doQuery();
			return -1;
		}

		@Override
		public int on_beforesave() {
			doSave();
			return -1;
		}
		
		
	}

	public static void dumpKeyevent(JComponent comp, int cond) {
		KeyStroke[] keys = comp.getInputMap(cond).allKeys();
		for (int i = 0; i < keys.length; i++) {
			KeyStroke key = keys[i];
			Object action = comp.getInputMap(cond).get(key);
			System.out.println(key + "==>" + action);
		}
	}

	public static void main(String[] argv) {
		DefaultNPParam.develop = 1;
		DefaultNPParam.debug = 1;
		Remotesql_frame frm = new Remotesql_frame();
		frm.pack();
		frm.setVisible(true);
	}
}
