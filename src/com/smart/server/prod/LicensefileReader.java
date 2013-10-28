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
 * 读取授权文件
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

				if (name.equals("版权所有")) {
					linfo.setCopyright(value);
				} else if (name.equals("授权用户")) {
					linfo.setAuthunit(value);
				} else if (name.equals("授权产品")) {
					linfo.setProdname(value);
				} else if (name.equals("授权模块清单")) {
					String ss[] = value.split(",");
					for (int i = 0; i < ss.length; i++)
						linfo.getModules().add(ss[i]);
				} else if (name.equals("授权开始日期")) {
					SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
					Calendar startdate = Calendar.getInstance();
					startdate.setTime(dfmt.parse(value));
					linfo.setStartdate(startdate);
				} else if (name.equals("授权结束日期")) {
					SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
					Calendar enddate = Calendar.getInstance();
					enddate.setTime(dfmt.parse(value));
					linfo.setEnddate(enddate);
				} else if (name.equals("授权客户端用户数")) {
					linfo.setMaxclientuser(Integer.parseInt(value));
				} else if (name.equals("授权服务器IP地址")) {
					linfo.setServerip(value);
				} else if (name.equals("数据签名")) {
					linfo.setDigitsign(value);
					if(!checkSign(linfo)){
						this.errormsg="校验签名失败";
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
	 * 检查数据签名
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
