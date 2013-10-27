package com.inca.npserver.clientinstall;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ClientInstaller extends JFrame implements DownloadNotifyIF {
	private JTextArea textInfo;
	static int debug = 0;
	private File outdir;
	private JLabel lbprogress;
	String driverlabel = "c:";

	public ClientInstaller() throws HeadlessException {
		super("NPSERVER ��װ");
		initControl();

		Dimension scrsize = this.getToolkit().getScreenSize();
		Dimension size = this.getPreferredSize();

		int x = (int) ((scrsize.getWidth() - size.getWidth()) / 2);
		int y = (int) ((scrsize.getHeight() - size.getHeight()) / 2);
		this.setLocation(x, y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	void initControl() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		lbprogress = new JLabel("");
		lbprogress.setHorizontalAlignment(JLabel.HORIZONTAL);
		lbprogress.setPreferredSize(new Dimension(300, 27));
		cp.add(lbprogress, BorderLayout.NORTH);

		progbar = new JProgressBar(0, 100);
		cp.add(progbar, BorderLayout.CENTER);
		progbar.setStringPainted(true);

		textInfo = new JTextArea(15, 60);
		cp.add(new JScrollPane(textInfo), BorderLayout.SOUTH);
		textInfo.setEditable(false);
	}

	void showProgress(String s) {
		lbprogress.setText(s);
	}

	void showMsg(String s) {
		int l = textInfo.getText().length();
		textInfo.setSelectionStart(l);
		textInfo.setSelectionEnd(l);
		textInfo.replaceSelection(s + "\r\n");
	}

	void start() {
		String strcodebase = "http://127.0.0.1/npserver/";
		showMsg("��ʼnpserver��װ");

		if (debug == 1) {
			outdir = new File("c:\\npserver");
		}

		if (debug == 0) {
			try {
				BasicService bs = (BasicService) ServiceManager
						.lookup("javax.jnlp.BasicService");
				URL codebase = bs.getCodeBase();
				strcodebase = codebase.toString();
			} catch (UnavailableServiceException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
				String errmsg = "ServiceManager.lookup(\"javax.jnlp.BasicService\" ����:"
						+ e.getMessage();
				JOptionPane.showMessageDialog(null, errmsg, "�޷�������װ",
						JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		}

		// if (debug == 0) {
		ChooseDriverdlg driverdlg = new ChooseDriverdlg();
		driverdlg.pack();
		driverdlg.setVisible(true);
		if (driverdlg.ok == false) {
			System.exit(255);
		}
		String ss[] = { "C:", "D:" };
		driverlabel = ss[driverdlg.cbDriver.getSelectedIndex()];

		try {
			downloadZip(strcodebase, driverlabel);
		} catch (IOException e) {
			String errmsg = "����client.zip ����:" + e.getMessage();
			JOptionPane.showMessageDialog(null, errmsg, "�޷�������װ",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		// }

		showMsg("��������������");
		installStartcmd();

		showMsg("�����������");
		createShortcut();
		installServerip(strcodebase);

		showMsg("��װ���");
		JOptionPane.showMessageDialog(this, "��װ�ɹ�", "��װ�ɹ�",
				JOptionPane.INFORMATION_MESSAGE);

		System.exit(0);

	}

	/**
	 * ������ݷ�ʽ
	 */
	void createShortcut() {
		Properties prop = System.getProperties();
		String userhome = prop.getProperty("user.home");

		if (debug == 1) {
			userhome = "c:\\windows";
		}

		File srcfile = null;
		File outfile = null;
		File progdir = null;
		if (osname.indexOf("Windows 98") >= 0) {
			if (driverlabel.startsWith("D")) {
				srcfile = new File(outdir, "bin/npserver_d.pif.template");
			} else {
				srcfile = new File(outdir, "bin/npserver.pif.template");
			}
			outfile = new File(userhome + "\\Desktop\\����" + contextname
					+ ".pif");
			outfile.getParentFile().mkdirs();
			copyFile(srcfile, outfile);
			progdir = new File(userhome + "\\Start Menu\\Programs\\Ӣ�����");
			outfile = new File(progdir, "����" + contextname + ".pif");
			progdir.mkdirs();
			copyFile(srcfile, outfile);
		} else {
			if (driverlabel.startsWith("D")) {
				srcfile = new File(outdir, "bin/npserver_d.lnk.template");
			} else {
				srcfile = new File(outdir, "bin/npserver.lnk.template");
			}
			outfile = new File(userhome + "\\����\\����" + contextname + ".lnk");
			outfile.getParentFile().mkdirs();
			copyFile(srcfile, outfile);
			progdir = new File(userhome + "\\����ʼ���˵�\\����\\Ӣ�����");
			outfile = new File(progdir, "����" + contextname + ".lnk");
			progdir.mkdirs();
			copyFile(srcfile, outfile);
		}

		// ����
		String cmd = "explorer " + progdir.getPath();
		try {
			Runtime.getRuntime().exec(cmd, new String[0], outdir);
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}

	}

	private void copyFile(File srcfile, File outfile) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(srcfile);
			fout = new FileOutputStream(outfile);
			int buflen = 8192;
			byte buf[] = new byte[buflen];
			while (true) {
				int rd = fin.read(buf);
				if (rd <= 0)
					break;
				fout.write(buf, 0, rd);
			}
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		} finally {
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
				}
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
				}
		}
	}

	/**
	 * д��conf�ļ��е�serverip
	 * 
	 * @param strcodebase
	 */
	void installServerip(String strcodebase) {
		String prefix = "http://";
		String s = strcodebase.substring(prefix.length());
		int p = s.indexOf("/");
		String serverip = s.substring(0, p);

		File f = new File(outdir, "conf/serverip");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(serverip);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}

		p++;
		int p1 = s.indexOf("/", p);
		if (p1 < 0) {
			p1 = s.length();
		}
		String webcontext = s.substring(p, p1);

		f = new File(outdir, "conf/webcontext");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(webcontext);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}

	}

	String osname = "";
	private String contextname;
	private JProgressBar progbar;

	/**
	 * ��װnpserver.cmd //����npserver.cmd. ��Ҫԭ���ĵ�һ��,����һ�и�Ϊset JAVA=����java
	 */
	void installStartcmd() {
		Properties props = System.getProperties();
		String jrepath = props.getProperty("sun.boot.library.path") + "\\javaw";
		jrepath = jrepath.replaceAll("\\\\", "\\\\\\\\");

		File cmdfiletemplate = null;
		File cmdfile = null;
		osname = props.getProperty("os.name");
		if (debug == 1) {
			// osname="Windows 98";
			osname = "Windows 2000";
		}

		if (osname.indexOf("Windows 98") >= 0) {
			cmdfiletemplate = new File(outdir, "bin/npserver.bat.template");
			cmdfile = new File(outdir, contextname + ".bat");
		} else {
			cmdfiletemplate = new File(outdir, "bin/npserver.cmd.template");
			cmdfile = new File(outdir, contextname + ".cmd");
		}

		BufferedReader rd = null;
		PrintWriter writer = null;
		try {
			rd = new BufferedReader(new FileReader(cmdfiletemplate));
			writer = new PrintWriter(new FileWriter(cmdfile));
			String line;
			while ((line = rd.readLine()) != null) {
				line = line.replaceAll("\\$\\$JAVA", jrepath);
				writer.println(line);
			}
		} catch (Exception e) {
			String errmsg = e.getMessage();
			JOptionPane.showMessageDialog(null, errmsg, "�޷�������װ",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
				}
			}
			if (writer != null)
				writer.close();
		}

	}

	void downloadZip(String strcodebase, String diskdriver) throws IOException {
		// ����zip��
		String strurl = strcodebase + "npserver_c.zip";
		String s = strcodebase;
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		int p = s.lastIndexOf("/");
		contextname = s.substring(p + 1);
		outdir = new File(diskdriver + "/" + contextname);

		showMsg("��ʼ�ӷ��������������,��Ⱥ�.URL=" + strurl);
		URL u = new URL(strurl);
		MultithreadDownloader mdl = new MultithreadDownloader();
		File tmpfile = null;
		try {
			tmpfile = File.createTempFile("tmp", "zip");
			if (!mdl.download(u, "", tmpfile.getParentFile(), this)) {
				showMsg("����ʧ�ܣ��˳�");
				JOptionPane.showMessageDialog(null, "����ʧ�ܣ����Ժ����ԡ�ϵͳ�˳���", "����",
						JOptionPane.ERROR_MESSAGE);

				System.exit(-1);
			}
			System.out.println("download zip ok,start unzip");
			tmpfile.delete();
			tmpfile = mdl.getDownloadfile();

			showProgress("������������,��ʼ��װ,��Ⱥ�.....");
			showMsg("�������,��ʼ��װ");
			// ��zip�ļ�
			outdir.mkdirs();
			unzipFile(tmpfile, outdir);

		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = "��ѹ�ļ�" + tmpfile.getPath() + "����:" + e.getMessage();
			JOptionPane.showMessageDialog(null, errmsg, "�޷���װ",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} finally {
			if (tmpfile != null) {
				tmpfile.delete();
			}
		}

	}

	void writeZipfile(InputStream in, File outf) {
		int bufsize = 8192;
		FileOutputStream fout = null;
		try {
			File outdir = outf.getParentFile();
			boolean mkdirresult = outdir.mkdirs();
			if (!outdir.exists() && !mkdirresult) {
				String errmsg = "��Ŀ¼ʧ��:" + outf.getParentFile().getParentFile();
				JOptionPane.showMessageDialog(null, errmsg, "�޷���װ",
						JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
			fout = new FileOutputStream(outf);
			byte[] buffer = new byte[bufsize];
			while (true) {
				int rd = in.read(buffer, 0, bufsize);
				if (rd <= 0)
					break;
				fout.write(buffer, 0, rd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String errmsg = "��ѹ�ļ�" + outf.getPath() + "����:" + e.getMessage();
			JOptionPane.showMessageDialog(null, errmsg, "�޷���װ",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);

		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	void unzipFile(File srczipfile, File targetdir) throws Exception {

		ZipFile zipfile = new ZipFile(srczipfile);
		// logger.debug("zipfile="+srczipfile.getPath());
		Enumeration en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) en.nextElement();
			// logger.debug("entry name="+entry.getName());
			if (entry.isDirectory()) {
				File outf = new File(targetdir, entry.getName());
				// showMsg("���ڽ�ѹ" + outf.getPath());
				outf.mkdirs();
			} else {
				File outf = new File(targetdir, entry.getName());
				InputStream zin = null;
				try {
					zin = zipfile.getInputStream(entry);
					FileOutputStream fout = null;
					try {
						// logger.info("���ڽ�ѹ" + outf.getPath());
						outf.getParentFile().mkdirs();
						fout = new FileOutputStream(outf);
						int buflen = 102400;
						byte[] buffer = new byte[buflen];

						while (true) {
							int rd = zin.read(buffer);
							if (rd <= 0)
								break;
							fout.write(buffer, 0, rd);
						}
						fout.close();
						fout = null;
						outf.setLastModified(entry.getTime());

					} finally {
						if (fout != null)
							fout.close();

					}
				} finally {
					if (zin != null) {
						zin.close();
					}
				}
			}

		}

	}

	public static String bytespeed2string(int bytes, long ms) {
		StringBuffer sb = new StringBuffer();
		DecimalFormat fm = new DecimalFormat("0.0");
		double speed = (double) bytes / (ms / 1000.0);
		if (speed >= 1048576.0) {
			sb.append(fm.format(speed / 1048576.0) + "M");
		} else if (speed > 1024) {
			sb.append(fm.format(speed / 1024) + "K");
		} else {
			sb.append(fm.format(speed) + "B");
		}

		return sb.toString();
	}

	public static String bytes2string(int bytes) {
		StringBuffer sb = new StringBuffer();
		DecimalFormat fm = new DecimalFormat("0.0");
		double db = (double) bytes;
		if (db >= 1048576.0) {
			sb.append(fm.format(db / 1048576.0) + "M");
		} else if (db > 1024) {
			sb.append(fm.format(db / 1024) + "K");
		} else {
			sb.append(fm.format(db) + "B");
		}

		return sb.toString();
	}

	public void log(String logmsg) {
		// TODO Auto-generated method stub

	}

	public void notify(int totalsize, int downloadedsize, long usetimems) {
		// TODO Auto-generated method stub
		showProgress("������" + bytes2string(downloadedsize) + "/"
				+ bytes2string(totalsize) + "���ٶ�:"
				+ bytespeed2string(downloadedsize, usetimems));

		float percent = (float) downloadedsize / (float) totalsize * 100f;
		progbar.setValue((int) percent);

	}

	public void notify(ArrayList<Blockinfo> blocks) {
		// TODO Auto-generated method stub

	}

	class ChooseDriverdlg extends JDialog implements ActionListener {
		boolean ok = false;
		private JComboBox cbDriver;

		public ChooseDriverdlg() {
			super(ClientInstaller.this, "ѡ��װ��", true);
			setLayout(new BorderLayout());

			String ss[] = { "C����", "D����" };
			cbDriver = new JComboBox(ss);
			add(cbDriver, BorderLayout.CENTER);

			JPanel jp = new JPanel();
			add(jp, BorderLayout.SOUTH);

			JButton btn = new JButton("ȷ��");
			jp.add(btn);
			btn.setActionCommand("ȷ��");
			btn.addActionListener(this);

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

			Dimension size = this.getPreferredSize();

			double x = (screensize.getWidth() - size.getWidth()) / 2.0;
			double y = (screensize.getHeight() - size.getHeight()) / 2.0;

			this.setLocation((int) x, (int) y);

		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("ȷ��")) {
				ok = true;
				dispose();
			}
		}

	}

	public static void main(String[] argv) {
		ClientInstaller.debug = 0;
		ClientInstaller inst = new ClientInstaller();
		inst.pack();
		inst.setVisible(true);
		inst.start();
	}
}
