package com.inca.npserver.prod;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.npserver.server.sysproc.CurrentappHelper;

public class LicenseManager {
	private LicenseManager() {
	}

	private static LicenseManager inst = null;

	public synchronized static LicenseManager getInst() {
		if (inst == null) {
			inst = new LicenseManager();
			inst.loadLicensefile();
			ModuleManager.getInst().loadModulefromDB();
		}
		return inst;
	}

	/**
	 * 许可信息.key为prodname
	 */
	HashMap<String, Licenseinfo> modulelicensemap = new HashMap<String, Licenseinfo>();
	Category logger=Category.getInstance(LicenseManager.class);

	public void reload(){
		synchronized (modulelicensemap) {
			modulelicensemap.clear();
			loadLicensefile();
		}
		ModuleManager.getInst().loadModulefromDB();
	}
	
	public File getLicensefileDir(){
		File appdir=CurrentappHelper.guessAppdir();
		File dir = new File(appdir,"WEB-INF/license");
		if(!dir.exists())dir.mkdirs();
		return dir;
	}
	
	void loadLicensefile() {
		File dir=getLicensefileDir();
		File[] fs = dir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File licensef = fs[i];
			if (licensef.isDirectory())
				continue;
			LicensefileReader lfr = new LicensefileReader();
			Licenseinfo linfo = lfr.readLicensefile(licensef);
			if (linfo == null)
				continue;
			Licenseinfo oldinfo=modulelicensemap.get(linfo.getProdname());
			if(oldinfo==null){
				modulelicensemap.put(linfo.getProdname(), linfo);
			}else{
				mergeLicenseinfo(oldinfo,linfo);
			}
			logger.info("加载授权产品:"+linfo.getProdname());
		}
	}
	
	/**
	 * 将info1的内容合并到info
	 * @param info
	 * @param info1
	 */
	void mergeLicenseinfo(Licenseinfo info,Licenseinfo info1){
		//合并modules
		info.getModules().addAll(info1.getModules());
		//开始日期取小 结束日期取大
		Calendar d1=info.getStartdate();
		Calendar d2=info.getStartdate();
		Calendar smalldt=d1.compareTo(d2)<0?d1:d2;
		info.setStartdate(smalldt);

		d1=info.getEnddate();
		d2=info.getEnddate();
		Calendar bigdate=d1.compareTo(d2)>00?d1:d2;
		info.setEnddate(bigdate);
		
		//客户端数取大
		int c1=info.getMaxclientuser();
		int c2=info.getMaxclientuser();
		int maxc=c1>c2?c1:c2;
		info.setMaxclientuser(maxc);

	}

	public Licenseinfo getLicense(String prodname) {
		Licenseinfo linfo = modulelicensemap.get(prodname);
		return linfo;
	}

	public Licenseinfo getLicense(String prodname, String modulename) {
		Licenseinfo linfo = modulelicensemap.get(prodname);
		if (linfo == null)
			return null;
		// 检查模块
		boolean match = false;
		Enumeration<String> en = linfo.getModules().elements();
		while (en.hasMoreElements()) {
			if (en.nextElement().equals(modulename)) {
				match = true;
				break;
			}
		}
		if (!match)
			return null;
		// 检查日期
		SimpleDateFormat dfmt=new SimpleDateFormat("yyyy-MM-dd");
		String now=dfmt.format(Calendar.getInstance().getTime());
		String startdate=dfmt.format(linfo.getStartdate().getTime());
		String enddate=dfmt.format(linfo.getEnddate().getTime());
		
		if(now.compareTo(startdate)<0 || now.compareTo(enddate)>0){
			return null;
		}
		return linfo;
		

	}
	
	public Vector<Licenseinfo> getLicenseinfos(){
		Vector<Licenseinfo> linfos=new Vector<Licenseinfo>();
		Iterator<Licenseinfo> it=modulelicensemap.values().iterator();
		while(it.hasNext()){
			linfos.add(it.next());
		}
		return linfos;
	}
	
	public int getMaxClient(){
		int max=0;
		Iterator<Licenseinfo> it=modulelicensemap.values().iterator();
		while(it.hasNext()){
			Licenseinfo linfo=it.next();
			max=Math.max(max,linfo.getMaxclientuser());
		}	
		return max;
	}
}
