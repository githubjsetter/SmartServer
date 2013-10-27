package com.inca.licensetool;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.inca.npserver.prod.LicensefileReader;
import com.inca.npserver.prod.LicensefileWriter;
import com.inca.npserver.prod.Licenseinfo;
import com.inca.npserver.prod.SignkeyGen;

public class Licensetool extends JDialog implements ActionListener {
	private JTextField textPath;
	private JTextField textCopyright;
	private JTextField textAuthunit;
	private JTextField textProdname;
	private JTextField textModules;
	private JTextField textStartdate;
	private JTextField textEnddate;
	private JTextField textClientcount;
	private JTextField textServerip;

	public Licensetool() {
		super((Frame) null, "制作授权文件", true);
		init();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");

	void init() {
		Container cp = this.getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();

		Insets inset = new Insets(2, 2, 2, 2);
		JLabel lb;
		lb = new JLabel("授权文件");
		c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textPath = new JTextField(30);
		c = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textPath, c);
		cp.add(textPath);

		JButton btn = new JButton("...");
		btn.setActionCommand("choosefile");
		btn.addActionListener(this);
		c = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(btn, c);
		cp.add(btn);

		// ///////
		lb = new JLabel("版权所有");
		c = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textCopyright = new JTextField("发证单位名称", 30);
		c = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textCopyright, c);
		cp.add(textCopyright);

		// ///////
		lb = new JLabel("授权单位");
		c = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textAuthunit = new JTextField(30);
		c = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textAuthunit, c);
		cp.add(textAuthunit);

		// ///////
		lb = new JLabel("授权产品");
		c = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textProdname = new JTextField(30);
		c = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textProdname, c);
		cp.add(textProdname);

		// ///////
		lb = new JLabel("授权模块");
		c = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textModules = new JTextField(30);
		c = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textModules, c);
		cp.add(textModules);

		// ///////
		Calendar now = Calendar.getInstance();
		String strdate = dfmt.format(now.getTime());
		lb = new JLabel("开始日期");
		c = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textStartdate = new JTextField(strdate, 30);
		c = new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textStartdate, c);
		cp.add(textStartdate);

		// ////////
		lb = new JLabel("结束日期");
		c = new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		now.add(Calendar.DAY_OF_YEAR, 30);
		strdate = dfmt.format(now.getTime());
		textEnddate = new JTextField(strdate, 30);
		c = new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textEnddate, c);
		cp.add(textEnddate);

		// ////////
		lb = new JLabel("客户端数");
		c = new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textClientcount = new JTextField("10", 30);
		c = new GridBagConstraints(1, 7, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textClientcount, c);
		cp.add(textClientcount);

		// ////////
		lb = new JLabel("服务器IP");
		c = new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(lb, c);
		cp.add(lb);

		textServerip = new JTextField("127.0.0.1", 30);
		c = new GridBagConstraints(1, 8, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(textServerip, c);
		cp.add(textServerip);

		// ///////////
		btn = new JButton("生成");
		btn.addActionListener(this);
		btn.setActionCommand("gen");
		c = new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(btn, c);
		cp.add(btn);

		btn = new JButton("关闭");
		btn.addActionListener(this);
		btn.setActionCommand("close");
		c = new GridBagConstraints(1, 9, 1, 1, 0, 0, GridBagConstraints.WEST,
				0, inset, 0, 0);
		g.setConstraints(btn, c);
		cp.add(btn);

	}

	JFileChooser jfc = null;

	void chooseFile() {
		if (jfc == null) {
			jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
			jfc.setFileFilter(new LicensefileFilter());
		}
		int ret = jfc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;

		File licensefile = jfc.getSelectedFile();

		LicensefileReader lfr = new LicensefileReader();
		Licenseinfo tmpinfo = lfr.readLicensefile(licensefile);
		if (tmpinfo == null) {
			JOptionPane.showMessageDialog(this, "读取文件失败" + lfr.getErrormsg());
			return;
		}
		linfo = tmpinfo;
		textPath.setText(licensefile.getAbsolutePath());
		// bind data
		textCopyright.setText(linfo.getCopyright());
		textAuthunit.setText(linfo.getAuthunit());
		textProdname.setText(linfo.getProdname());
		String modules = "";
		Enumeration<String> en = linfo.getModules().elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			if (i > 0)
				modules = modules + ",";
			modules += en.nextElement();
		}
		textModules.setText(modules);
		textStartdate.setText(dfmt.format(linfo.getStartdate().getTime()));
		textEnddate.setText(dfmt.format(linfo.getEnddate().getTime()));
		textClientcount.setText(String.valueOf(linfo.getMaxclientuser()));
		textServerip.setText(linfo.getServerip());

	}

	Licenseinfo linfo = null;

	class LicensefileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().indexOf("license") >= 0) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "授权文件license";
		}

	}

	void gen() {
		if(linfo==null){
			linfo=new Licenseinfo();
		}
		linfo.setCopyright(textCopyright.getText());
		linfo.setAuthunit(textAuthunit.getText());
		linfo.setProdname(textProdname.getText());

		String ss[] = textModules.getText().split(",");
		Vector<String> modules = new Vector<String>();
		for (int i = 0; i < ss.length; i++) {
			modules.add(ss[i]);
		}
		linfo.setModules(modules);

		try {
			java.util.Date dt = dfmt.parse(textStartdate.getText());
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			linfo.setStartdate(cal);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, "开始日期格式应为YYYY-MM-DD");
			return;
		}
		try {
			java.util.Date dt = dfmt.parse(textEnddate.getText());
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			linfo.setEnddate(cal);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, "结束日期格式应为YYYY-MM-DD");
			return;
		}

		int maxclient = 0;
		try {
			maxclient = Integer.parseInt(textClientcount.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "客户端数应为数字");
			return;
		}
		linfo.setMaxclientuser(maxclient);
		linfo.setServerip(this.textServerip.getText());
		
		//生成签名
		try {
			//进行签名
			InputStream fin=Licensetool.class.getResourceAsStream("privatekey");
			ByteArrayOutputStream bout=new ByteArrayOutputStream();
			int c;
			while((c=fin.read())>=0){
				bout.write(c);
			}
			fin.close();
			byte[] privatedata=bout.toByteArray();
			PKCS8EncodedKeySpec keyspec=new PKCS8EncodedKeySpec(privatedata);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
			PrivateKey privatekey = keyFactory.generatePrivate(keyspec);
			
			SignkeyGen.signLicenseinfo(linfo,privatekey);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "签名失败"+e.getMessage());
			return;
		}
		
		//写文件
		File f=new File(textPath.getText());
		LicensefileWriter lw=new LicensefileWriter();
		try {
			lw.writer(f, linfo);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "生成失败"+e.getMessage());
		}
		
		JOptionPane.showMessageDialog(this, "生成授权文件成功:"+f.getAbsolutePath());
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("choosefile")) {
			chooseFile();
		} else if (cmd.equals("gen")) {
			gen();
		} else if (cmd.equals("close")) {
			dispose();
		}
	}

	public static void main(String[] args) {
		new File("logs").mkdirs();
		Licensetool lt = new Licensetool();
		lt.pack();
		lt.setVisible(true);
	}
}
