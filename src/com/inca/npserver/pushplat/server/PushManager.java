package com.inca.npserver.pushplat.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.DBTableModel;
import com.inca.npserver.pushplat.common.Pushdbmodel;
import com.inca.npserver.pushplat.common.Pushinfo;
import com.inca.npserver.server.sysproc.CurrentappHelper;

public class PushManager {
	static Category logger=Category.getInstance(PushManager.class);
	public static Vector<Pushinfo> getAllpushinfo(){
		Vector<Pushinfo>  infos=new Vector<Pushinfo>();
		File dir=new File(CurrentappHelper.getClassesdir(), "push");
		File fs[]=dir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			if(fs[i].isDirectory())continue;
			if(!fs[i].getName().endsWith(".nppush"))continue;
			BufferedReader rd=null;
			try{
				rd=new BufferedReader(new FileReader(fs[i]));
				infos.addAll(Pushinfo.readPushinfos(rd));
			}catch(Exception e ){
				logger.error("error",e);
			}finally{
				if(rd!=null){
					try {
						rd.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		return infos;
	}
	
	public static DBTableModel toDbmodel(Vector<Pushinfo> infos){
		Pushdbmodel dbmodel=new Pushdbmodel();
		Enumeration<Pushinfo> en = infos.elements();
		while (en.hasMoreElements()) {
			Pushinfo info = en.nextElement();
			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "pushid", info.getPushid());
			dbmodel.setItemValue(r, "pushname", info.getPushname());
			dbmodel.setItemValue(r, "groupname", info.getGroupname());
			dbmodel.setItemValue(r, "level", String
					.valueOf(info.getLevel()));
			dbmodel.setItemValue(r, "callopid", info.getCallopid());
			dbmodel.setItemValue(r, "callopname", info.getCallopname());
			dbmodel.setItemValue(r, "wheres", info.getWheres());
			dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_SAVED);
		}
		return dbmodel;
	}
}
