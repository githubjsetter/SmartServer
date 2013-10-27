package com.inca.np.gui.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * 由配置文件生成hov
 * @author Administrator
 *
 */
public class HovgeneralFactory {
	public static void writeHov(CHovBase hov,File outf)throws Exception{
		PrintWriter out=null;
		try{
			out=new PrintWriter(new FileWriter(outf));
			writeHov(hov,outf);
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	/**
	 * 存到文件中
	 * @param hov
	 * @param out
	 * @throws Exception
	 */
	public static void writeHov(CHovBase hov,PrintWriter out)throws Exception{
		hov.writeHov(out);
	}
	
}
