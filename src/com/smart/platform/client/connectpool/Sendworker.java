package com.smart.platform.client.connectpool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.DeflaterOutputStream;

import org.apache.log4j.Category;

import com.smart.platform.auth.RunopManager;
import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.InflatInputStreamWrapper;
import com.smart.platform.communicate.InputStreamWrapper;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.StringUtil;

/**
 * 发送一个任务的工作者
 * 
 * @author user
 * 
 */
public class Sendworker extends Thread {
	Category logger = Category.getInstance(Sendworker.class);

	String strurl;
	ClientRequest clientrequest;
	Object signobject;

	ServerResponse svrresp = null;
	Exception svrexception=null;

	boolean stoped = false;
	int id = 0;
	int respstatus=0;

	public Sendworker(int id, String strurl, ClientRequest clientrequest,
			Object signobject) {
		this.id = id;
		this.strurl = strurl;
		this.clientrequest = clientrequest;
		this.signobject = signobject;
	}

	public ServerResponse getSvrresp() {
		return svrresp;
	}

	public void run() {
		try {
			respstatus=0;
			svrresp = submitRequest(strurl, clientrequest);
			
			// for debug 
			//if(id==0){
			//	Thread.sleep(8000);
			//}
			
			if(svrresp.getCommandcount()>0 && svrresp.commandAt(0) instanceof StringCommand){
				if(svrresp.getCommand().equals("+OK:PROCESSING")){
					logger.debug("workerid="+id+" ,recv +OK:PROCESSING");
					respstatus=0;
					return;
				}
			}
			respstatus=1;
	
		} catch (Exception e) {
			respstatus=2;
			svrexception=e;
		}finally{
			synchronized(signobject){
				signobject.notifyAll();
			}
		}
	}

	public ServerResponse submitRequest(String strurl,
			ClientRequest clientrequest) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("发送请求到" + strurl + ",授权:" + clientrequest.getAuthstring());
		if (clientrequest.getCommandcount() > 0) {
			CommandBase cmd = clientrequest.commandAt(0);
			if (cmd instanceof StringCommand) {
				sb.append(" 命令:" + ((StringCommand) cmd).getString());
			}
		}

		logger.debug("workerid="+id+","+sb.toString());

		// RunopManager.setWaitcursor();
		RunopManager.infoMessage(sb.toString());

		URL url = null;
		url = new URL(strurl);

		HttpURLConnection con = null;
		InputStreamWrapper in = null;
		InflatInputStreamWrapper zin = null;
		in = null;
		OutputStream out = null;
		try {
			con = (HttpURLConnection) url.openConnection();

			con.setConnectTimeout(5 * 1000);
			// con.setReadTimeout(60 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");

			con.addRequestProperty("Content-Type",
					RemoteConnector.ZIPCONTENTTYPE);
			con.addRequestProperty("Connection", "close");
			// ///////////// !!!!!!!!!!!!!! 必须加msie
			// /////////////////////////////////
			// ///////////// !!!!!!!!!!!!!! 否则apache + tomcat5 会出错 20070328
			// /////////////////////////////////
			con.addRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT ");
			logger.info("开始connect " + strurl);
			out = con.getOutputStream();
			DeflaterOutputStream zout = new DeflaterOutputStream(out);
			clientrequest.writeData(zout);
			zout.finish();
			out.flush();

			in = new InputStreamWrapper(con.getInputStream());
			zin = new InflatInputStreamWrapper(in);
			ServerResponse svrresp = new ServerResponse();
			svrresp.readData(zin);
			DefaultNPParam.lastrecvsvrresptime = System.currentTimeMillis();
			DefaultNPParam.online = true;

			retrievedsize += in.getTotalreadedsize();
			inflatedsize += zin.getTotalreadedsize();

			String recmsg = "接收请求成功,共收到"
					+ StringUtil.bytes2string(in.getTotalreadedsize()) + ",解压后"
					+ StringUtil.bytes2string(zin.getTotalreadedsize());
			logger.debug("workerid="+id+","+recmsg);
			RunopManager.infoMessage(recmsg);
			return svrresp;
		} catch (Exception e) {
			logger.error("workerid="+id+" submitRequest " + strurl, e);
			if (con != null && con.getContentType()!=null && con.getContentType().startsWith("text/html")) {
				ByteArrayOutputStream tmpout = new ByteArrayOutputStream();
				int contentlen = con.getContentLength();
				InputStream ein = con.getErrorStream();
				byte[] buf = new byte[1024];
				while (contentlen > 0) {
					int rd = ein.read(buf);
					if (rd <= 0)
						break;
					tmpout.write(buf, 0, rd);
					contentlen -= rd;
				}
				ein.close();
				String msg = new String(tmpout.toByteArray(), "utf-8");
				logger.error("workerid="+id+","+strurl + " error:\n" + msg);
				RunopManager.errorMessage("workerid="+id+","+"接收数据出错,url=" + strurl + ":"
						+ e.getMessage() + msg);
				throw new Exception("workerid="+id+","+"接收数据出错,url=" + strurl + ":\n"
						+ e.getMessage() + "\n" + msg);
			} else {
				RunopManager.errorMessage("workerid="+id+","+"接收数据出错,url=" + strurl + ":"
						+ e.getMessage());
			}
			throw e;
		} finally {
			if (zin != null) {
				inflatedsize += zin.getTotalreadedsize();
				zin.close();
			}

			if (in != null) {
				retrievedsize += in.getTotalreadedsize();
				try {
					in.close();
				} catch (IOException e) {
					// nothing;
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// nothing;
				}
			}

			if (con != null) {
				con.disconnect();
			}
			// RunopManager.setDefaultcursor();

		}

	}

	int retrievedsize = 0;
	int inflatedsize = 0;

	public int getRetrievedSize() {
		return retrievedsize;
	}

	public int getInflatSize() {
		return inflatedsize;
	}


	public Exception getSvrexception() {
		return svrexception;
	}

	public boolean isStoped() {
		return stoped;
	}

	public void setStoped(boolean stoped) {
		this.stoped = stoped;
	}

	/**
	 * 返回状态.
	 * 0 未完成.
	 * 1 成功完成.
	 * 2 完成. 有异常
	 * @return
	 */
	public int getRespStatus(){
		return respstatus;
	}
}
