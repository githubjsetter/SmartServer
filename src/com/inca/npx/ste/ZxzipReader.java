package com.inca.npx.ste;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZxzipReader {
	Zxconfig zxconfig = null;

	public Zxconfig getZxconfig() {
		return zxconfig;
	}

	public void readZxzip(File f) throws Exception {
		ZipFile zipfile = new ZipFile(f);
		readConfig(zipfile);
	}

	void readConfig(ZipFile filzipfilee) throws Exception {
		Enumeration<ZipEntry> en = filzipfilee.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if (ze.getName().equals("config")) {
				InputStream in = filzipfilee.getInputStream(ze);
				readConfig(in);
				in.close();
			}
		}

	}

	void readConfig(InputStream in) throws Exception {
		zxconfig = new Zxconfig();
		BufferedReader rd = new BufferedReader(new InputStreamReader(in, "gbk"));
		String line;
		while ((line = rd.readLine()) != null) {
			int p = line.indexOf(":");
			if (p < 0)
				continue;
			String name = line.substring(0, p);
			String value = line.substring(p + 1);

			if (name.equals("opid")) {
				zxconfig.opid = value;
			} else if (name.equals("optype")) {
				zxconfig.optype = value;
			} else if (name.equals("opcode")) {
				zxconfig.opcode = value;
			} else if (name.equals("opname")) {
				zxconfig.opname = value;
			} else if (name.equals("groupname")) {
				zxconfig.groupname = value;
			} else if (name.equals("tablename")) {
				zxconfig.tablename = value;
			} else if (name.equals("viewname")) {
				zxconfig.viewname = value;
			} else if (name.equals("pkcolname")) {
				zxconfig.pkcolname = value;
			} else if (name.equals("tablename1")) {
				zxconfig.tablename1 = value;
			} else if (name.equals("viewname1")) {
				zxconfig.viewname1 = value;
			} else if (name.equals("pkcolname1")) {
				zxconfig.pkcolname1 = value;
			} else if (name.equals("fkcolname1")) {
				zxconfig.fkcolname1 = value;
			}
		}

	}
}
