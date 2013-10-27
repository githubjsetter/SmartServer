package com.inca.adminclient.installjar;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.inca.np.gui.control.CFrame;
import com.inca.npserver.install.Installinfo;

/**
 * 制作安装文件 installinfo 其它JAR文件
 * 
 * 
 * @author Administrator
 * 
 */
public class Installjarbuilder extends CFrame {
	public Installjarbuilder() {
		super("模块安装文件制作");
		init();
	}

	int maxfilecount = 5;
	JTextField textPaths[] = new JTextField[maxfilecount];
	private JTextField textInstallinfo;

	void init() {
		Container cp = this.getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();
		/*
		 * public GridBagConstraints(int gridx, int gridy, int gridwidth, int
		 * gridheight, double weightx, double weighty, int anchor, int fill,
		 * Insets insets, int ipadx, int ipady) {
		 * 
		 */
		Insets inset = new Insets(2, 2, 2, 2);
		JLabel lb;
		lb = new JLabel("安装信息文件");
		c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textInstallinfo = new JTextField("conf/installinfo", 40);
		textInstallinfo.setEditable(false);
		c = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textInstallinfo, c);
		cp.add(textInstallinfo);

		JButton btn;
		btn = new JButton("...");
		btn.setActionCommand("choose installinfo");
		btn.addActionListener(this);
		c = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(btn, c);
		cp.add(btn);

		btn = new JButton("edit");
		btn.setActionCommand("edit installinfo");
		btn.addActionListener(this);
		c = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(btn, c);
		cp.add(btn);

		for (int i = 0; i < maxfilecount; i++) {
			lb = new JLabel("jar file " + (i + 1));
			c = new GridBagConstraints(0, i + 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, 0, inset, 0, 0);
			g.setConstraints(lb, c);
			cp.add(lb);

			textPaths[i] = new JTextField(40);
			textPaths[i].setEditable(false);
			c = new GridBagConstraints(1, i + 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, 0, inset, 0, 0);
			g.setConstraints(textPaths[i], c);
			cp.add(textPaths[i]);

			btn = new JButton("...");
			btn.setActionCommand("jarfile" + i);
			btn.addActionListener(this);
			c = new GridBagConstraints(2, i + 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, 0, inset, 0, 0);
			g.setConstraints(btn, c);
			cp.add(btn);
		}

		JPanel bottomjp = new JPanel();
		btn = new JButton("生成");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		bottomjp.add(btn);

		btn = new JButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		bottomjp.add(btn);

		int ypos = 1 + maxfilecount;
		c = new GridBagConstraints(1, ypos, 2, 1, 0, 0,
				GridBagConstraints.WEST, 0, inset, 0, 0);
		g.setConstraints(bottomjp, c);
		cp.add(bottomjp);
		// bottomjp.setPreferredSize(new Dimension(260,40));

	}

	void editInstall() {
		File installinfofile = new File(this.textInstallinfo.getText());
		InstallinfoEditor iie = new InstallinfoEditor(installinfofile);
		iie.pack();
		iie.setVisible(true);
	}

	JFileChooser jfc=null;
	void chooseInstallinfo() {
		if(jfc==null){
			jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
		}
		jfc.setFileFilter(new InstallinfoFilter());
		int ret = jfc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;

		File f = jfc.getSelectedFile();
		textInstallinfo.setText(f.getAbsolutePath());
	}

	void chooseJarfile(int index) {
		if(jfc==null){
			jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
		}
		jfc.setFileFilter(new JarFilter());
		int ret = jfc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;

		File f = jfc.getSelectedFile();

		textPaths[index].setText(f.getAbsolutePath());
	}

	void doZip() throws Exception {
		File installinfofile = new File(this.textInstallinfo.getText());
		InstallinfoEditor iie = new InstallinfoEditor(installinfofile);
		Installinfo iinfo=iie.getInstallinfo();
		String moduleengname=iinfo.getModuleengname();
		if(moduleengname==null || moduleengname.length()==0){
			JOptionPane.showMessageDialog(this, "请填写完整模块信息");
			return;
		}

		File outdir=new File("release");
		outdir.mkdirs();
		
		
		File outf = new File(outdir,moduleengname+"_install.zip");

		OutputStream fout = new FileOutputStream(outf);

		ZipOutputStream zipout = new ZipOutputStream(fout);
		File f = new File(this.textInstallinfo.getText());
		addFile(zipout, f);
		if(!f.exists()){
			JOptionPane.showMessageDialog(this, "找不到文件"+f.getAbsolutePath());
			return;
		}
		
		for(int i=0;i<maxfilecount;i++){
			String filename=textPaths[i].getText();
			if(filename.length()==0)continue;
			f=new File(filename);
			if(!f.exists()){
				JOptionPane.showMessageDialog(this, "找不到文件"+f.getAbsolutePath());
				return;
			}
			addFile(zipout, f);
		}
		
		zipout.close();
		
		JOptionPane.showMessageDialog(this, "制作安装包成功,输出文件"+outf.getAbsolutePath());
	}

	private void addFile(ZipOutputStream zipout, File f) throws Exception {
		ZipEntry entry = new ZipEntry(f.getName());
		zipout.putNextEntry(entry);
		int buflen = 10240;
		byte[] buf = new byte[buflen];
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int rd;
			while ((rd = in.read(buf)) > 0) {
				zipout.write(buf, 0, rd);
			}
			zipout.closeEntry();
		} finally {
			if (in != null)
				in.close();
		}

	}

	void onOk() {
		try {
			doZip();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return;
		}
		this.dispose();
	}

	void onCancel() {
		this.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("choose installinfo")) {
			chooseInstallinfo();
		} else if (cmd.equals("edit installinfo")) {
			editInstall();
		} else if (cmd.startsWith("jarfile")) {
			int fileindex = Integer.parseInt(cmd.substring("jarfile".length()));
			chooseJarfile(fileindex);
		} else if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}

	class InstallinfoFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().equals("installinfo")) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "安装信息文件installinfo";
		}

	}

	class JarFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().toLowerCase().endsWith(".jar")) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "JAR 文件(*.jar)";
		}

	}

/*	public static void main(String[] args) {
		Installjarbuilder frm = new Installjarbuilder();
		frm.pack();
		frm.setVisible(true);
	}
*/}
