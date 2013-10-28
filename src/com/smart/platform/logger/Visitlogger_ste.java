package com.smart.platform.logger;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CQueryStemodel;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*功能"访问日志查询"单表编辑Model*/
public class Visitlogger_ste extends CQueryStemodel {
	public Visitlogger_ste(CFrame frame) throws HeadlessException {
		super(frame, "访问日志");
		DBColumnDisplayInfo colinfo = this.getDBColumnDisplayInfo("result");
		colinfo.addComboxBoxItem("0", "正常");
		colinfo.addComboxBoxItem("-1", "错误");
	}

	public String getTablename() {
		return "np_log_v";
	}

	public String getSaveCommandString() {
		return "保存访问日志";
	}

	

	@Override
	protected String getSqlOrderby() {
		return " order by seqid desc";
	}

	@Override
	public void on_doubleclick(int row, int col) {
		if (row < 0)
			return;
		String remoteip = this.getItemValue(row, "remoteip");
		setWaitCursor();
		whereisIp(remoteip);
		setDefaultCursor();
	}

	void whereisIp(String ip) {
		String ss[] = ip.split("\\.");
		if (ss.length != 4) {
			return;
		}

		OutputStream out=null;
		InputStream in=null;
		try {
			URL u = new URL("http://ip.qq.com/cgi-bin/searchip");
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);

			out = con.getOutputStream();
			StringBuffer sb = new StringBuffer();
			sb.append("searchip1=" + ss[0]);
			sb.append("&searchip2=" + ss[1]);
			sb.append("&searchip3=" + ss[2]);
			sb.append("&searchip4=" + ss[3]);
			out.write(sb.toString().getBytes());
			out.flush();

			in = con.getInputStream();
			int buflen = 10240;
			byte[] buf = new byte[buflen];

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int rd;
			while ((rd = in.read(buf)) > 0) {
				bout.write(buf, 0, rd);
			}

			in.close();
			out.close();

			String s = new String(bout.toByteArray());
			// System.out.println(s);
			s = parseIpcomefrom(ip, s);
			if (s.length() == 0) {
				infoMessage("没有找到", "没有找到" + ip + "的相关信息");
			} else {
				infoMessage("信息", s);
			}

		} catch (Exception e) {
			errorMessage("错误", e.getMessage());
			return;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	String parseIpcomefrom(String ip, String html) {
		if (html.indexOf("没有记录") >= 0) {
			return "";
		}

		int p = html.indexOf("该IP所在地区");
		if (p < 0) {
			return "";
		}

		p = html.indexOf("\"top\">", p + 1);
		if (p < 0) {
			return "";
		}
		p += "\"top\">".length();

		int p1 = html.indexOf("</td>", p);
		if (p1 < 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("IP:" + ip + "\r\n");
		sb.append("城市:" + html.substring(p, p1) + "\r\n");

		p = html.indexOf("所属运营商", p);
		if (p < 0) {
			return sb.toString();
		}

		p = html.indexOf("\"top\">", p + 1);
		if (p < 0) {
			return sb.toString();
		}
		p += "\"top\">".length();

		p1 = html.indexOf("</td>", p);
		if (p1 < 0) {
			return sb.toString();
		}
		sb.append("运营商:" + html.substring(p, p1) + "\r\n");
		return sb.toString();

	}

/*	public static void main(String argv[]) {
		Visitlogger_ste app = new Visitlogger_ste(null);
		app.whereisIp("211.136.107.111");
	}
*/}
