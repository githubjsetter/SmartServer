package com.inca.npserver.prod;

import java.io.File;

public class Testlicensefile {
	public static void main(String[] args) {
		LicensefileReader rd=new LicensefileReader();
		Licenseinfo linfo=rd.readLicensefile(new File("testdata/license1"));
		if(linfo!=null){
			System.out.println("read finished");
		}else{
			System.err.println("ERROR:"+rd.getErrormsg());
		}
		
	}
}
