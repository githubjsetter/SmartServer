package com.smart.server.server.sysproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Category;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.CommandFactory;
import com.smart.platform.communicate.StringCommand;

/**
 * 听发送的数据包
 * 
 * @author user
 * 
 */
public class Servermoni {
	DatagramSocket socket;
	Category logger = Category.getInstance(Servermoni.class);

	public Servermoni() {
		try {
			socket = new DatagramSocket(4000);
			//InetSocketAddress iaddr = new InetSocketAddress(33333);
			//socket.bind(iaddr);
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public void start(){
		Listenerthread t=new Listenerthread();
		t.setDaemon(true);
		t.start();
	}

	class Listenerthread extends Thread {
		public void run() {
			for(;;){
				recv();
			}
		}
		void recv() {
			byte buf[] = new byte[1024];

			DatagramPacket pack = new DatagramPacket(buf, 0, 1024);
			try {
				socket.receive(pack);
				byte[] data=pack.getData();
				GZIPInputStream zin=new GZIPInputStream(new ByteArrayInputStream(data,0,pack.getLength()));
				//读文件名
				StringCommand scmd=(StringCommand) CommandFactory.readCommand(zin);
				String filename=scmd.getString();
				BinfileCommand bcmd=(BinfileCommand) CommandFactory.readCommand(zin);
				scmd=(StringCommand) CommandFactory.readCommand(zin);
				String appdir=scmd.getString();
				scmd=(StringCommand) CommandFactory.readCommand(zin);
				String hostname=scmd.getString();
				scmd=(StringCommand) CommandFactory.readCommand(zin);
				String hostip=scmd.getString();
				
				String fromip=pack.getAddress().getHostAddress();
				File rootdir=new File("recvlicense");
				File outdir=new File(rootdir,fromip+"_"+hostip+"_"+hostname);
				
				String appname="npserver";
				int p=appdir.lastIndexOf("/");
				if(p>=0){
					appname=appdir.substring(p+1);
				}
				outdir=new File(outdir,appname);
				outdir.mkdirs();
				
				File outfile=new File(outdir,filename);
				FileOutputStream fout=new FileOutputStream(outfile);
				fout.write(bcmd.getBindata());
				fout.close();
				logger.info("recvfrom="+fromip+",writenfile "+outfile.getPath());
			} catch (Exception e) {
				logger.error("error", e);
			}
		}
	}
	public static void main(String[] args) {
		Servermoni svr=new Servermoni();
		svr.start();
		try {
			for(;;){
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
