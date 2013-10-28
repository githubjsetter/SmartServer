package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CButton;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CLabel;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 记录附件记录信息
 * 
 * @author Administrator
 * 
 */
public class FileinfoDialog extends CDialog {
	DBTableModel filedbmodel = null;
	String filegroupid = null;
	private DBTableModel tablemodel;
	private CTable table;
	DBTableModel stedbmodel;
	int sterow;
	boolean canmodify=true;

	public FileinfoDialog(Frame frame, DBTableModel stedbmodel, int sterow,
			String filegroupid, DBTableModel filedbmodel,boolean canmodify) {
		super(frame, "附件管理", true);
		this.filedbmodel = filedbmodel;
		this.filegroupid = filegroupid;
		this.stedbmodel = stedbmodel;
		this.sterow = sterow;
		this.canmodify=canmodify;

		initDialog();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		bindData();
		this.setHotkey();
	}

	void bindData() {
		if (filedbmodel == null)
			return;
		tablemodel.bindMemds(filedbmodel);
		table.tableChanged(new TableModelEvent(tablemodel));
		table.autoSize();

		if(stedbmodel!=null){
			RecordTrunk rec=stedbmodel.getRecordThunk(sterow);
			Enumeration<File> en=rec.getWantuploadfiles().elements();
			while(en.hasMoreElements()){
				listmodel.addElement(en.nextElement());
			}
		}
		
	}

	void initDialog() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		JTabbedPane tabbedpane = new JTabbedPane();
		cp.add(tabbedpane, BorderLayout.CENTER);
		tabbedpane.add(createUploadedPane(), "已上传文件");
		tabbedpane.add(createUploadpane(), "待上传文件");

		// 下部按钮
		JPanel jp = new JPanel();
		cp.add(jp, BorderLayout.SOUTH);
		JButton btn = new JButton("确定");
		jp.add(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);

		btn = new JButton("关闭");
		jp.add(btn);
		btn.setActionCommand("close");
		btn.addActionListener(this);
		

	}

	/**
	 * 生成已上传文件信息
	 * 
	 * @return
	 */
	JPanel createUploadedPane() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		JToolBar tb = createToolbar();
		jp.add(tb, BorderLayout.NORTH);
		tablemodel = createFiledbmodel();
		table = new CTable(tablemodel);
		table.setReadonly(true);
		table.setAutoResizeMode(CTable.AUTO_RESIZE_OFF);
		// table.getSelectionModel().addListSelectionListener(new
		// TableSelectListener());
		table.addMouseListener(new TablemouseHandle());
		jp.add(new JScrollPane(table), BorderLayout.CENTER);
		return jp;

	}

	private JList listFiles;
	JFileChooser jfc = new JFileChooser();
	private DefaultListModel listmodel;

	/**
	 * 生成要上传文件
	 * 
	 * @return
	 */
	JPanel createUploadpane() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		listmodel = new DefaultListModel();
		listFiles = new JList(listmodel);
		jp.add(listFiles);

		jp.add(new JScrollPane(listFiles), BorderLayout.CENTER);

		JPanel jpbtm = new JPanel();
		jp.add(jpbtm, BorderLayout.SOUTH);
		JButton btn = new JButton("增加文件");
		btn.setActionCommand("增加文件");
		btn.addActionListener(this);
		jpbtm.add(btn);

		btn = new JButton("取消文件");
		btn.setActionCommand("取消文件");
		btn.addActionListener(this);
		jpbtm.add(btn);

		return jp;
	}

	DBTableModel createFiledbmodel() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("filename",
				"varchar", "已上传文件名");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("filesize", "number", "文件大小");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("modifydate", "date", "上传日期");
		colinfos.add(col);

		return new DBTableModel(colinfos);
	}

	JToolBar createToolbar() {
		JToolBar tb = new JToolBar();
		JButton btn = new JButton("下载文件");
		btn.addActionListener(this);
		btn.setActionCommand("下载文件");
		btn.setFocusable(false);
		tb.add(btn);
		
		btn = new JButton("删除文件");
		if(!canmodify){
			btn.setEnabled(false);
		}
		btn.addActionListener(this);
		btn.setActionCommand("删除文件");
		btn.setFocusable(false);
		tb.add(btn);

		return tb;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("下载文件")) {
			int row = table.getRow();
			if (row >= 0) {
				// 取文件名,下载
				progdlg = new CProgressDialog(FileinfoDialog.this, "下载文件");
				progdlg.pack();
				DownloadThread t = new DownloadThread(row);
				t.start();
				progdlg.setVisible(true);
				if (progdlg.ok == false) {
					t.stopDownload();
				}
			}
		} else if (cmd.equals("close")) {
			onCancel();
		}else if(cmd.equals("ok")){
			onOk();
		} else if (cmd.equals("增加文件")) {
			selectFile();
		}else if(cmd.equals("删除文件")){
			deleteFile();
		}
	}
	
	void deleteFile(){
		int row = table.getRow();
		if(row<0){
			return;
		}
		DBTableModel dm=(DBTableModel)table.getModel();
		String filename=dm.getItemValue(row, "filename");
		try {
			RecordFileDownloader.deleteFile(filegroupid, filename);
		} catch (Exception e) {
			errorMessage("错误", e.getMessage());
			return;
		}
		dm.removeRow(row);
		table.tableChanged(new TableModelEvent(dm));
	}
	
	void onOk(){
		Vector<File> ftable=new Vector<File>(); 
		for(int i=0;i<listmodel.size();i++){
			ftable.add((File)listmodel.elementAt(i));
		}
		stedbmodel.getRecordThunk(sterow).setWantuploadfile(ftable);
		dispose();
		
	}
	void onCancel(){
		dispose();
	}

	void selectFile() {
		jfc.setMultiSelectionEnabled(true);
		jfc.setCurrentDirectory(new File("."));
		int ret = jfc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File[] selectedfs = jfc.getSelectedFiles();
		for (int i = 0; selectedfs != null && i < selectedfs.length; i++) {
			listmodel.addElement(selectedfs[i]);
		}
	}

	class TablemouseHandle implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					// 取文件名,下载
					progdlg = new CProgressDialog(FileinfoDialog.this, "下载文件");
					progdlg.pack();
					DownloadThread t = new DownloadThread(row);
					t.start();
					progdlg.setVisible(true);
					if (progdlg.ok == false) {
						t.stopDownload();
					}
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	private CProgressDialog progdlg;

	class TableSelectListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			int row = dm.getAnchorSelectionIndex();
			// 取文件名,下载
			progdlg = new CProgressDialog(FileinfoDialog.this, "下载文件");
			progdlg.pack();
			DownloadThread t = new DownloadThread(row);
			t.start();
			progdlg.setIndeterminate(true);
			progdlg.setVisible(true);
		}
	}

	class DownloadThread extends Thread {
		int row;
		RecordFileDownloader rfd = new RecordFileDownloader();

		DownloadThread(int row) {
			this.row = row;
		}

		public void stopDownload() {
			rfd.stopDownload();
		}

		public void run() {
			try {
				downloadFile(row);
			} finally {
				progdlg.ok = true;
				progdlg.dispose();
			}
		}

		void downloadFile(int row) {
			String fn = tablemodel.getItemValue(row, "filename");
			File dir = new File("下载的文件");
			dir.mkdirs();
			File outf = new File(dir, fn);
			try {
				if (!rfd.downloadFile(filegroupid, fn, outf)) {
					JOptionPane.showMessageDialog(FileinfoDialog.this,"下载失败");
					return;
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(FileinfoDialog.this, e1
						.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);

			}
			ExportinfoDlg dlg = new ExportinfoDlg(outf);
			dlg.pack();
			dlg.setVisible(true);
		}
	}

	/**
	 * 下载信息
	 * 
	 * @author Administrator
	 * 
	 */
	class ExportinfoDlg extends CDialog {
		File outf = null;

		public ExportinfoDlg(File outf) {
			super(FileinfoDialog.this, "下载成功", true);
			this.outf = outf;
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			CLabel lb = new CLabel("下载成功, 文件:" + outf.getAbsolutePath());
			cp.add(lb, BorderLayout.CENTER);

			JPanel bottomp = new JPanel();
			cp.add(bottomp, BorderLayout.SOUTH);

			CButton btn = new CButton("关闭");
			btn.setActionCommand("close");
			btn.addActionListener(this);
			bottomp.add(btn);

			btn = new CButton("打开目录");
			btn.setActionCommand("opendir");
			btn.addActionListener(this);
			bottomp.add(btn);

			localCenter();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("close")) {
				dispose();
			} else if (e.getActionCommand().equals("opendir")) {
				String cmd = "explorer \""
						+ outf.getParentFile().getAbsolutePath() + "\"";
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();
			}
		}
	}

	protected void setHotkey(){
		JComponent jcp=(JComponent)this.getContentPane();
		InputMap im=jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "ok");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "cancel");
		jcp.getActionMap().put("ok",new DlgAction("ok"));
		jcp.getActionMap().put("cancel",new DlgAction("cancel"));
	}
	class DlgAction extends AbstractAction{
		DlgAction(String cmd){
			super(cmd);
			putValue(AbstractAction.ACTION_COMMAND_KEY,cmd);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("ok")){
				onOk();
			}else if(e.getActionCommand().equals("cancel")){
				onCancel();
			}
		}
	}
	

	public static void main(String[] argv) {
		RecordfileUploader rfu = new RecordfileUploader();

		try {
			String filegroupid = "3";
			DBTableModel filedbmodel = rfu.browserFilegroup(filegroupid);
			FileinfoDialog dlg = new FileinfoDialog(null, null, 0, filegroupid,
					filedbmodel,false);
			dlg.pack();
			dlg.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
