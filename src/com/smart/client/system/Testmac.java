package com.smart.client.system;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testmac {
	 public static void main(String[] args) {
		String s="00-0A-E6-E6-19-64";
		
		Pattern pat=Pattern.compile("[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]");
		Matcher m=pat.matcher(s);
		
		if(m.find()){
			System.out.println(m.start()+" "+m.end());
		}else{
			System.err.println("not found");
		}
		
		
	}
}
