package com.smart.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;





import com.smart.client.system.NpclientParam;
import com.smart.client.system.SystemexitThread;
import com.smart.platform.gui.control.CFrame;

public class Startnpclient  extends JFrame {
	public static JFrame startdlg = null;
	private URLClassLoader ucl;

	public static void main(String[] argv) {
		new NpclientParam();
		
/*		Properties prop = System.getProperties();
		prop.put("http.proxyHost","127.0.0.1"); 
        prop.put("http.proxyPort","1100");
*/	
		Startnpclient startd = new Startnpclient();
		startd.pack();
		startd.setVisible(true);
	}
	
	static String getVersion(String fn) {
		int p = fn.lastIndexOf(".");
		fn = fn.substring(0, p);
		p = fn.indexOf("-");
		if (p < 0)
			return "";
		return fn.substring(p + 1);
	}
	
	static File getNewestJar(String prefix){
		File targetfile=null;
		File dir=new File("lib");
		File[] fs=dir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			File f=fs[i];
			if(f.isDirectory())continue;
			if(!f.getName().startsWith(prefix)) continue;
			if(targetfile==null){
				targetfile=f;
			}else{
				String targetversion=getVersion(targetfile.getName());
				String version=getVersion(f.getName());
				if(version.compareTo(targetversion)>0){
					targetfile=f;
				}
			}
		}
		return targetfile;
	}
	

	public Startnpclient() throws HeadlessException {
		super("NP Client Starter");
		startdlg = this;

		Font font=new Font("宋体",Font.PLAIN,18);
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		StringBuffer sb=new StringBuffer();
		sb.append("正在启动NPCLIENT,请稍候...");
		JLabel lb=new JLabel(sb.toString());
		lb.setFont(font);
		cp.add(lb,BorderLayout.CENTER);

		font=new Font("宋体",Font.PLAIN,12);
		//lb=new JLabel("版本"+version);
		//lb.setFont(font);
		//cp.add(lb,BorderLayout.SOUTH);
		
		
		cp.setPreferredSize(new Dimension(280,120));
		Dimension scrsize = this.getToolkit().getScreenSize();
		Dimension size = cp.getPreferredSize();
		int x=(int)((scrsize.getWidth() - size.getWidth()) /2);
		int y=(int)((scrsize.getHeight() - size.getHeight())/2);
		
		this.setLocation(x,y);
		this.setDefaultCloseOperation(CFrame.DISPOSE_ON_CLOSE);

		
		StartThread t=new StartThread();
		t.start();
	}

	class StartThread extends Thread{
		public void run(){
			Runtime.getRuntime().addShutdownHook(new SystemexitThread());
			
			
			
			String clsname="com.smart.client.system.LoginDialog";
			try {
				Thread.sleep(100);
				Class dlgcls = Class.forName(clsname);
				//Object logindlg = dlgcls.newInstance();
				Method mainfunc = dlgcls.getMethod("startRun", null);
				mainfunc.invoke(new String[0],new String[0]);
			} catch (Exception ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
		}
	}
}
