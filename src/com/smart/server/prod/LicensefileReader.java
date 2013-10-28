package com.smart.server.prod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Category;

/**
 * ��ȡ��Ȩ�ļ�
 * 
 * @author Administrator
 * 
 */
public class LicensefileReader {
	String errormsg;
	Category logger=Category.getInstance(LicensefileReader.class);

	public String getErrormsg() {
		return errormsg;
	}

	public Licenseinfo readLicensefile(File f) {
		Licenseinfo linfo = new Licenseinfo();

		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "gbk"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				int p = line.indexOf(":");
				if (p < 0)
					continue;
				String name = line.substring(0, p);
				String value = line.substring(p + 1);

				if (name.equals("��Ȩ����")) {
					linfo.setCopyright(value);
				} else if (name.equals("��Ȩ�û�")) {
					linfo.setAuthunit(value);
				} else if (name.equals("��Ȩ��Ʒ")) {
					linfo.setProdname(value);
				} else if (name.equals("��Ȩģ���嵥")) {
					String ss[] = value.split(",");
					for (int i = 0; i < ss.length; i++)
						linfo.getModules().add(ss[i]);
				} else if (name.equals("��Ȩ��ʼ����")) {
					SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
					Calendar startdate = Calendar.getInstance();
					startdate.setTime(dfmt.parse(value));
					linfo.setStartdate(startdate);
				} else if (name.equals("��Ȩ��������")) {
					SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
					Calendar enddate = Calendar.getInstance();
					enddate.setTime(dfmt.parse(value));
					linfo.setEnddate(enddate);
				} else if (name.equals("��Ȩ�ͻ����û���")) {
					linfo.setMaxclientuser(Integer.parseInt(value));
				} else if (name.equals("��Ȩ������IP��ַ")) {
					linfo.setServerip(value);
				} else if (name.equals("����ǩ��")) {
					linfo.setDigitsign(value);
					if(!checkSign(linfo)){
						this.errormsg="У��ǩ��ʧ��";
						return null;
					}
				}
			}
		} catch (Exception e) {
			logger.error("ERROR",e);
			this.errormsg = e.getMessage();
			return null;
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
				}
			}
		}

		return linfo;
	}
	
	/**
	 * �������ǩ��
	 * @return
	 */
	boolean checkSign(Licenseinfo linfo){
		try {
			return SignkeyGen.verifySign(linfo);
		} catch (Exception e) {
			logger.error("ERROR",e);
			return false;
		}
	}
}
