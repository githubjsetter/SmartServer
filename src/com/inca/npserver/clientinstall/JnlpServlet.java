package com.inca.npserver.clientinstall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JnlpServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requrl = req.getRequestURL().toString();
		String prefix = "http://";
		requrl = requrl.substring(prefix.length());
		int p = requrl.indexOf("/");
		String ip = requrl.substring(0, p);
		// System.out.println("requrl="+requrl+",ip="+ip);

		String s = "npserver.jnlp";
		String realpath = this.getServletContext().getRealPath(s);
		// System.out.println("realpath="+realpath);

		InputStream in = new FileInputStream(new File(realpath));
		StringBuffer sb = new StringBuffer();
		InputStreamReader rd = new InputStreamReader(in, "utf-8");
		int buflen = 10240;
		char buffer[] = new char[buflen];
		while (true) {
			int rded = rd.read(buffer);
			if (rded <= 0)
				break;
			sb.append(buffer, 0, rded);
		}
		in.close();

		String xml = sb.toString();
		xml = xml.replaceAll("\\$\\$codebase", ip);
		// System.out.println(xml);

		resp.setContentType("application/x-java-jnlp-file");
		resp.getOutputStream().write(xml.getBytes("utf-8"));
	}
}
