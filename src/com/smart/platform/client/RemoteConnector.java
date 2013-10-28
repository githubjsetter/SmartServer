package com.smart.platform.client;

import org.apache.log4j.Category;

import com.smart.platform.client.connectpool.RemoteConnectpool;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-27 Time: 10:51:12
 * 连接远程http server的联接器 使用方法： <p/> RemoteConnector rmtcon=new RemoteConnector();
 * rmtcon.connect("http://www.somesite.com/myfile"); OutputStream out =
 * rmtcon.getOut(); out.write(data); <p/> rmtcon.get
 */
public class RemoteConnector {
	Category logger = Category.getInstance(RemoteConnector.class);

	public final static String ZIPCONTENTTYPE = "octet-stream";


	private static String authstring = "";

	public static void setAuthstring(String s) {
		authstring = s;
	}

	public static String getAuthstring() {
		return authstring;
	}
	
	/**
	 * 当前活动的opid;
	 */
	private static String activeopid="";
	

	public static String getActiveopid() {
		return activeopid;
	}

	public static void setActiveopid(String activeopid) {
		RemoteConnector.activeopid = activeopid;
	}

	public ServerResponse submitRequest(String strurl,
			ClientRequest clientrequest) throws Exception {
		RemoteConnectpool rmtcp=new RemoteConnectpool(strurl,clientrequest);
		ServerResponse resp=rmtcp.doSend();
		retrievedsize=rmtcp.getRetrievedsize();
		inflatedsize=rmtcp.getInflatedsize();
		return resp;
	}
/*
	public ServerResponse submitRequest(String strurl,
			ClientRequest clientrequest) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("发送请求到" + strurl + ",授权:" + authstring);
		if (clientrequest.getCommandcount() > 0) {
			CommandBase cmd = clientrequest.commandAt(0);
			if (cmd instanceof StringCommand) {
				sb.append(" 命令:" + ((StringCommand) cmd).getString());
			}
		}
		
		logger.debug(sb.toString());

		// RunopManager.setWaitcursor();
		RunopManager.infoMessage(sb.toString());
		if (clientrequest.getAuthstring() == null
				|| clientrequest.getAuthstring().length() == 0) {
			clientrequest.setAuthstring(authstring);
		}
		
		URL url = null;
		url = new URL(strurl);

		HttpURLConnection con = null;
		InputStreamWrapper in=null;
		InflatInputStreamWrapper zin=null;
		in = null;
		OutputStream out = null;
		try {
			con = (HttpURLConnection) url.openConnection();

			con.setConnectTimeout(10 * 1000);
			//con.setReadTimeout(60 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");

			con.addRequestProperty("Content-Type", ZIPCONTENTTYPE);
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

			String recmsg="接收请求成功,共收到"
				+ StringUtil.bytes2string(in.getTotalreadedsize()) + ",解压后"
				+ StringUtil.bytes2string(zin.getTotalreadedsize());
			logger.debug(recmsg);
			RunopManager.infoMessage(recmsg);
			return svrresp;
		} catch (Exception e) {
			logger.error("submitRequest " + strurl, e);
			if(con!=null && con.getContentType().startsWith("text/html")){
				ByteArrayOutputStream tmpout=new ByteArrayOutputStream();
				int contentlen=con.getContentLength();
				InputStream ein = con.getErrorStream();
				byte[] buf=new byte[1024];
				while(contentlen>0){
					int rd=ein.read(buf);
					if(rd<=0)break;
					tmpout.write(buf,0,rd);
					contentlen -= rd;
				}
				ein.close();
				String msg=new String(tmpout.toByteArray(),"utf-8");
				logger.error(strurl+" error:\n"+msg);
				RunopManager.errorMessage("接收数据出错,url=" + strurl + ":"
						+ e.getMessage()+msg);
				throw new Exception("接收数据出错,url=" + strurl + ":\n"
						+ e.getMessage()+"\n"+msg);
			}else{
			RunopManager.errorMessage("接收数据出错,url=" + strurl + ":"
					+ e.getMessage());
			}
			throw e;
		} finally {
			if(zin != null){
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
*/
	int retrievedsize = 0;
	int inflatedsize = 0;

	public int getRetrievedSize() {
		return retrievedsize;
	}

	public int getInflatSize() {
		return inflatedsize;
	}
}
