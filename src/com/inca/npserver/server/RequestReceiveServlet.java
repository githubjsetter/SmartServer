package com.inca.npserver.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.util.DefaultNPParam;
import com.inca.npbi.server.Dsengine;
import com.inca.npserver.timer.TimerManager;
import com.inca.npworkflow.server.WfEngine;

public class RequestReceiveServlet extends HttpServlet {
	Category logger = Category.getInstance(RequestReceiveServlet.class);

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		DefaultNPParam.runonserver = true;
		DefaultNPParam.debug = 0;
		DefaultNPParam.develop = 0;
		Server.getInstance();

		// �����Զ��߳�.
		TimerManager tm = TimerManager.getInstance();
		tm.loadTimer();

		// ����������
		WfEngine.getInstance();

		// BI��������
		Dsengine.getInstance();
		logger.info("!!!!!!!!!running on server !!!!");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InflaterInputStream zin = buildInputStream(req);
		ClientRequest clientreq = new ClientRequest();
		try {
			clientreq.readData(zin);
		} catch (Exception e) {
			logger.error("����������ʧ��", e);
			return;
		} finally {
			zin.close();
			zin = null;
		}

		// logger.info("recv request ok,it has "+clientreq.getCommandcount()+" cmd");
		clientreq.setRemoteip(req.getRemoteAddr());
		clientreq.setContextname(getServletContext().getServletContextName());
		String msgid = clientreq.getContextvalue("msgid");
		logger.debug("receive msgid=" + msgid);

		/**
		 * �Ƿ��ҵ�ԭ������Ϣ,��������?
		 */
		boolean sendoldmsg=false;
		ServerResponse svrresp=null;
		if (msgid != null && msgid.length() > 0) {
			synchronized (msgtable) {
				if (msgtable.contains(msgid)) {
					svrresp = msgidrespmap.get(msgid);
					logger.debug("msgid=" + msgid
							+ ", already processed, svrresp=" + svrresp
							+ ",send and return");
					sendoldmsg=true;
					if (svrresp != null) {
					} else {
						svrresp = new ServerResponse();
						svrresp.addCommand(new StringCommand("+OK:PROCESSING"));
					}
				}else{
					msgtable.add(msgid);
				}
				while (msgtable.size() > MAXMSGCOUNT) {
					msgidrespmap.remove(msgtable.elementAt(0));
					msgtable.remove(0);
				}
			}
		}
		
		if(sendoldmsg && svrresp!=null){
			resp.setContentType(RemoteConnector.ZIPCONTENTTYPE);
			ServletOutputStream out = resp.getOutputStream();
			DeflaterOutputStream zout = new DeflaterOutputStream(out);
			try {
				logger.debug("send old svrresp");
				svrresp.writeData(zout);
				zout.finish();
				logger.debug("sent old svrresp");
			} catch (Exception e) {
				logger.error("send server response error", e);
				throw new IOException(e.getMessage());
			} finally {
				zout.close();
				zout = null;
			}
			return;
		}

		Server disp = Server.getInstance();
		svrresp = disp.process(clientreq);

		synchronized (msgtable) {
			msgidrespmap.put(msgid, svrresp);
		}

		resp.setContentType(RemoteConnector.ZIPCONTENTTYPE);
		ServletOutputStream out = resp.getOutputStream();
		// logger.debug("begin writer zout");
		DeflaterOutputStream zout = new DeflaterOutputStream(out);
		try {
			svrresp.writeData(zout);
			zout.finish();
		} catch (Exception e) {
			logger.error("send server response error", e);
			throw new IOException(e.getMessage());
		} finally {
			zout.close();
			zout = null;
			// logger.debug("writern zout");
		}

	}
	
	

	InflaterInputStream buildInputStream(HttpServletRequest req)
			throws IOException {
		int len = req.getContentLength();
		// logger.info("������Ϣcontentlength="+len);
		ServletInputStream in = req.getInputStream();
		InflaterInputStream zin = new InflaterInputStream(in);
		return zin;
	}

	InflaterInputStream buildInputStreamWithBuffer(HttpServletRequest req)
			throws IOException {

		// ///////////// !!!!!!!!!!!!!! ����ȡcontent length
		// /////////////////////////////////
		// ///////////// !!!!!!!!!!!!!! ����apache + tomcat5 ����� 20070328
		// /////////////////////////////////

		int len = req.getContentLength();

		// logger.info("������Ϣcontentlength="+len);

		String contenttype = req.getContentType();

		ServletInputStream in = req.getInputStream();

		ByteArrayOutputStream borgout = new ByteArrayOutputStream();
		int c;

		// logger.info("��ʼ������....");
		for (int r = 0; r < len; r++) {
			try {
				c = in.read();
				if (c < 0) {
					r--;
					continue;
				}
			} catch (IOException e) {
				logger.error("Ӧ��" + len + "��ʵ��" + r, e);
				r--;
				continue;
			}
			borgout.write(c);
		}

		byte[] orgdata = borgout.toByteArray();
		// logger.info("��������ɣ�ʵ��"+orgdata.length+"�ֽ�");

		ByteArrayInputStream tmpin = new ByteArrayInputStream(orgdata);
		InflaterInputStream zin = new InflaterInputStream(tmpin);

		return zin;
	}

	/**
	 * ��ౣ�������ķ���,500��.
	 */
	int MAXMSGCOUNT = 500;
	Vector<String> msgtable = new Vector<String>();
	HashMap<String, ServerResponse> msgidrespmap = new HashMap<String, ServerResponse>();
}
