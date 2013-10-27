package com.inca.adminclient.auth;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Category;

import com.inca.adminclient.gui.AdminClientframe;
import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.env.Configer;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CPassword;
import com.inca.np.gui.control.CPlainTextField;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MD5Helper;

/**
 * 登录管理
 * 
 * @author Administrator
 * 
 */
public class LoginDialog extends CDialog {
	private CPlainTextField textIp;

	private CPassword password;

	private Category logger = Category.getInstance(LoginDialog.class);

	public LoginDialog() {
		super((Frame) null, "PM系统管理登录", true);
		init();
		localCenter();
		loadConfigvalue();
	}

	void init() {
		Container cp = getContentPane();
		JPanel mainp = new JPanel();
		cp.add(mainp);
		mainp.setLayout(new BorderLayout());

		JPanel loginpane = createLoginpane();
		mainp.add(loginpane,BorderLayout.CENTER);
	}

	private void loadConfigvalue() {
		Configer config = new Configer(new File("conf/admin.properties"));
		String ip = config.get("ip");
		if (ip != null) {
			textIp.setText(ip);
		} else {
			File f = new File("conf/serverip");
			if (f.exists()) {
				try {
					BufferedReader rd = new BufferedReader(new FileReader(f));
					textIp.setText(rd.readLine());
					rd.close();
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
		}
		File f = new File("conf/webcontext");
		if (f.exists()) {
			try {
				BufferedReader rd = new BufferedReader(new FileReader(f));
				DefaultNPParam.prodcontext = rd.readLine();
				rd.close();
			} catch (Exception e) {
				logger.error("error", e);
			}
		}
		
	}

	JPanel createLoginpane() {
		JPanel gridjp = new JPanel();
		// gridjp.addMouseListener(new MouseHandle());

		Dimension textsize = new Dimension(114, 27);

		GridBagLayout g = new GridBagLayout();
		gridjp.setLayout(g);

		JLabel lb = new JLabel("服务器地址");
		lb.setHorizontalAlignment(JLabel.RIGHT);

		gridjp.add(lb, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		textIp = new CPlainTextField();
		textIp.setText("127.0.0.1");
		textIp.setPreferredSize(textsize);
		addEnterkeyTraver(textIp);
		gridjp.add(textIp, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

/*		
		lb = new JLabel("用户操作码");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		gridjp.add(lb, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		textUserid = new CUpperTextField();
		textUserid.setText("");
		textUserid.setPreferredSize(textsize);
		addEnterkeyTraver(textUserid);
		gridjp.add(textUserid, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));
*/
		lb = new JLabel("ADMIN密码");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		gridjp.add(lb, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		password = new CPassword(10);
		password.setPreferredSize(textsize);
		addEnterkeyTraver(password);
		gridjp.add(password, new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		JPanel toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());

		JButton btnok = new JButton("登录");
		btnok.setActionCommand("login");
		btnok.addActionListener(new ActionHandle());
		toolbar.add(btnok);
		addEnterkeyConfirm(btnok);

		JButton btncancel = new JButton("取消");
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(new ActionHandle());
		toolbar.add(btncancel);
		addEnterkeyTraver(btncancel);

/*		JButton btndownload = new JButton("重新下载");
		btndownload.setActionCommand("download");
		btndownload.addActionListener(new ActionHandle());
		toolbar.add(btndownload);
		addEnterkeyTraver(btndownload);
*/
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(gridjp, BorderLayout.CENTER);
		jp.add(toolbar, BorderLayout.SOUTH);

		return jp;

	}

	class ActionHandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("login")) {
				try {
					setWaitCursor();
					doLogin();
				} catch (Exception e1) {
					logger.error("login error", e1);
				} finally {
					setDefaultCursor();
				}
			} else if (cmd.equals("cancel")) {
				LoginDialog.this.dispose();
			} else if (cmd.equals("download")) {
				String shellcmd = "explorer http://" + textIp.getText()
						+ "/ngpcs/ngpcs.jnlp";
				try {
					Runtime.getRuntime().exec(shellcmd);
					Thread.sleep(3000);
					System.exit(0);
				} catch (Exception she) {
					logger.error(shellcmd + " ERROR", she);
				}
			}
		}

	}

	void doLogin() {
		StringCommand logincmd = new StringCommand("npserver:adminlogin");
		ParamCommand paramcmd = new ParamCommand();
		paramcmd.addParam("password", MD5Helper.MD5(new String(password
				.getPassword())));

		String url = "http://" + textIp.getText() + "/"
				+ DefaultNPParam.prodcontext + "/serveradmin.do";
		DefaultNPParam.defaultappsvrurl = url;

		Configer config = new Configer(new File("conf/admin.properties"));
		config.put("ip", textIp.getText());
		config.saveConfigfile();

		ClientRequest req = new ClientRequest();
		req.addCommand(logincmd);
		req.addCommand(paramcmd);

		ServerResponse svrresp = null;
		try {
			svrresp = AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("login error", e);
			errorMessage("登录失败",e.getMessage());
			return;
		}

		StringCommand cmd = (StringCommand) svrresp.commandAt(0);
		String s = cmd.getString();
		if (s.startsWith("+")) {

			// System.out.println(s);
			ParamCommand userinfocmd = (ParamCommand) svrresp.commandAt(1);
			Userruninfo userruninfo = new Userruninfo();
			userruninfo.setUserid(userinfocmd.getValue("userid"));
			userruninfo.setUsername(userinfocmd.getValue("username"));
			
			ClientUserManager.setCurrentUser(userruninfo);

			RemoteConnector.setAuthstring(userinfocmd.getValue("authstring"));
			
			dispose();
			AdminClientframe frm=new AdminClientframe();
			frm.pack();
			frm.setVisible(true);

		} else {
			errorMessage("错误", s);
			return;

		}

	}

	protected void errorMessage(String title, String msg) {
		JOptionPane.showMessageDialog(this, msg, title,
				JOptionPane.ERROR_MESSAGE);

	}

	void setWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	void setDefaultCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
