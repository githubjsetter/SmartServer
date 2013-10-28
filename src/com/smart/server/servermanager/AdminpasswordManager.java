package com.smart.server.servermanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Category;

import com.smart.server.server.sysproc.CurrentappHelper;

public class AdminpasswordManager {
	public static String getAdminpassword(){
		File dir=new File(CurrentappHelper.guessAppdir(),"npadmin");
		if(!dir.exists()){
			return "";
		}
		File f=new File(dir,"password");
		if(!f.exists())return "";
		BufferedReader rd=null;
		try {
			rd=new BufferedReader(new FileReader(f));
			return rd.readLine();
		} catch (Exception e) {
			Category logger=Category.getInstance(AdminpasswordManager.class);
			logger.error("error",e);
			return "";
		}finally{
			if(rd!=null){
				try {
					rd.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	public static void writePassword(String password)throws Exception{
		File dir=new File(CurrentappHelper.guessAppdir(),"npadmin");
		if(!dir.exists()){
			dir.mkdirs();
		}
		File f=new File(dir,"password");
		FileOutputStream fout=null;
		
		try {
			fout=new FileOutputStream(f);
			fout.write(password.getBytes());
		}finally{
			if(fout!=null){
				try {
					fout.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
}
