package com.inca.npserver.prod;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

public class LicensefileWriter {
	public void writer(File outf, Licenseinfo linfo) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter(outf));
		out.println("版权所有:" + linfo.getCopyright());
		out.println("授权用户:" + linfo.getAuthunit());
		out.println("授权产品:" + linfo.getProdname());
		String modules = "";
		Enumeration<String> en = linfo.getModules().elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			if (i > 0)
				modules = modules + ",";
			modules += en.nextElement();
		}
		out.println("授权模块清单:" + modules);

		SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");

		out.println("授权开始日期:" + dfmt.format(linfo.getStartdate().getTime()));
		out.println("授权结束日期:" + dfmt.format(linfo.getEnddate().getTime()));
		out.println("授权客户端用户数:" + linfo.getMaxclientuser());
		out.println("授权服务器IP地址:" + linfo.getServerip());
		out.println("数据签名:" + linfo.getDigitsign());
		out.close();
	}
}
