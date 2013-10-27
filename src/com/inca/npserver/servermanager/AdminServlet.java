package com.inca.npserver.servermanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
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
import com.inca.np.util.DefaultNPParam;
import com.inca.npserver.prod.ModuleManager;

/**
 * 管理Servlet
 * 
 * @author Administrator
 * 
 */
public class AdminServlet extends HttpServlet {
	Category logger = Category.getInstance(AdminServlet.class);

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		DefaultNPParam.runonserver = true;
		DefaultNPParam.debug = 0;
		DefaultNPParam.develop = 0;
		ModuleManager.getInst();

		try {
			InitialContext ic = new InitialContext();
			Object o = ic.lookup("java:comp/env/depttable_use_pub_dept");
			logger.info("env java:comp/env/depttable_use_pub_dept=" + o);
			if (o != null) {
				Integer ideptuse = (Integer) o;
				DefaultNPParam.depttable_use_pub_dept = ideptuse.intValue() == 1;

			}
		} catch (NameNotFoundException ne) {
		} catch (Exception e) {
			logger.error("ERROR", e);
		}

		logger.info("!!!!!!!!!admin running on server !!!!");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InflaterInputStream zin = buildInputStream(req);
		ClientRequest clientreq = new ClientRequest();
		try {
			clientreq.readData(zin);
		} catch (Exception e) {
			logger.error("读上行数据失败", e);
			return;
		} finally {
			zin.close();
			zin = null;
		}

		// logger.info("recv request ok,it has "+clientreq.getCommandcount()+"
		// cmd");
		clientreq.setRemoteip(req.getRemoteAddr());
		clientreq.setContextname(getServletContext().getServletContextName());

		AdminRequestDispatcher disp = AdminRequestDispatcher.getInstance();
		ServerResponse svrresp = disp.process(clientreq);

		resp.setContentType(RemoteConnector.ZIPCONTENTTYPE);
		ServletOutputStream out = resp.getOutputStream();
		logger.debug("begin writer zout");
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
			logger.debug("writern zout");
		}

	}

	InflaterInputStream buildInputStream(HttpServletRequest req)
			throws IOException {
		int len = req.getContentLength();
		// logger.info("上行信息contentlength="+len);
		ServletInputStream in = req.getInputStream();
		InflaterInputStream zin = new InflaterInputStream(in);
		return zin;
	}

	InflaterInputStream buildInputStreamWithBuffer(HttpServletRequest req)
			throws IOException {

		// ///////////// !!!!!!!!!!!!!! 必须取content length
		// /////////////////////////////////
		// ///////////// !!!!!!!!!!!!!! 否则apache + tomcat5 会出错 20070328
		// /////////////////////////////////

		int len = req.getContentLength();

		// logger.info("上行信息contentlength="+len);

		String contenttype = req.getContentType();

		ServletInputStream in = req.getInputStream();

		ByteArrayOutputStream borgout = new ByteArrayOutputStream();
		int c;

		// logger.info("开始读上行....");
		for (int r = 0; r < len; r++) {
			try {
				c = in.read();
				if (c < 0) {
					r--;
					continue;
				}
			} catch (IOException e) {
				logger.error("应读" + len + "，实读" + r, e);
				r--;
				continue;
			}
			borgout.write(c);
		}

		byte[] orgdata = borgout.toByteArray();
		// logger.info("读上行完成，实读"+orgdata.length+"字节");

		ByteArrayInputStream tmpin = new ByteArrayInputStream(orgdata);
		InflaterInputStream zin = new InflaterInputStream(tmpin);

		return zin;
	}
}
