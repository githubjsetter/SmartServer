package com.inca.np.util;

import java.awt.event.KeyEvent;

public class HotkeyUtils {
	static String vks[]={
		"нч","A","B","C","D","E","F","G","H","I",
		"J","K","L","M","N","O","P","Q","R",
		"S","T","U","V","W","X","Y","Z",
		"F1","F2","F3","F4","F5","F6","F7","F8","F9","F10","F11","F12",
	};

	public static String[] getKeynames(){
		return vks;
	}
	
	public static int getKeycode(String key){
		key=key.toUpperCase();
		if(key.length()==1 && key.compareTo("A")>=0 && key.compareTo("Z")<=0){
			char c=key.charAt(0);
			return KeyEvent.VK_A + (c-'A');
		}
		
		if(key.length()>1 && key.startsWith("F")){
			int index=Integer.parseInt(key.substring(1));
			return KeyEvent.VK_F1 + index - 1;
		}
		System.err.println("unknow keyname="+key);
		return -1;
	}
}
