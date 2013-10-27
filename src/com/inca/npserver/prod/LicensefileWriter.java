package com.inca.npserver.prod;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

public class LicensefileWriter {
	public void writer(File outf, Licenseinfo linfo) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter(outf));
		out.println("��Ȩ����:" + linfo.getCopyright());
		out.println("��Ȩ�û�:" + linfo.getAuthunit());
		out.println("��Ȩ��Ʒ:" + linfo.getProdname());
		String modules = "";
		Enumeration<String> en = linfo.getModules().elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			if (i > 0)
				modules = modules + ",";
			modules += en.nextElement();
		}
		out.println("��Ȩģ���嵥:" + modules);

		SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");

		out.println("��Ȩ��ʼ����:" + dfmt.format(linfo.getStartdate().getTime()));
		out.println("��Ȩ��������:" + dfmt.format(linfo.getEnddate().getTime()));
		out.println("��Ȩ�ͻ����û���:" + linfo.getMaxclientuser());
		out.println("��Ȩ������IP��ַ:" + linfo.getServerip());
		out.println("����ǩ��:" + linfo.getDigitsign());
		out.close();
	}
}
