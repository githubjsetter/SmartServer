package com.inca.np.util;

import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.log4j.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-8-6 Time: 11:07:03
 * To change this template use File | Settings | File Templates.
 */
public class ZipHelper {
	static Category logger = Category.getInstance(ZipHelper.class);

	public static void unzipFile(File srczipfile, File targetdir)
			throws Exception {

		ZipFile zipfile = new ZipFile(srczipfile);
		logger.debug("zipfile=" + srczipfile.getPath());
		Enumeration en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) en.nextElement();
			logger.debug("entry name=" + entry.getName());
			if (entry.isDirectory()) {
				File outf = new File(targetdir, entry.getName());
				outf.mkdirs();
			} else {
				File outf = new File(targetdir, entry.getName());
				InputStream zin = null;
				try {
					zin = zipfile.getInputStream(entry);
					FileOutputStream fout = null;
					try {
						logger.info("ÕýÔÚ½âÑ¹" + outf.getPath());
						outf.getParentFile().mkdirs();
						fout = new FileOutputStream(outf);
						int buflen = 102400;
						byte[] buffer = new byte[buflen];

						while (true) {
							int rd = zin.read(buffer);
							if (rd <= 0)
								break;
							fout.write(buffer, 0, rd);
						}
						fout.close();
						fout = null;
						outf.setLastModified(entry.getTime());

					} catch (Exception e) {
						logger.error("Ð´" + outf.getPath() + " ERROR", e);
					} finally {

						if (fout != null)
							fout.close();

					}
				} finally {
					if (zin != null) {
						zin.close();
					}
				}
			}

		}
		zipfile.close();

	}

	public static void writeZip(File zipfile, File fs[]) throws Exception {
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(zipfile);
			for (int i = 0; i < fs.length; i++) {
				File f = fs[i];
				put2zip(zout, f);
			}
		} finally {
			if (zout != null) {
				zout.close();
			}
		}
	}

	public static void writeZip(File zipfile, String name, File f)
			throws Exception {
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(zipfile);
			put2zip(zout, name, f);
		} finally {
			if (zout != null) {
				zout.close();
			}
		}
	}

	public static void put2zip(ZipOutputStream zout, File f) throws Exception {
		ZipEntry ze = new ZipEntry(f.getName());
		zout.putNextEntry(ze);
		byte[] buf = new byte[102400];
		int rd;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(f);
			while ((rd = fin.read(buf)) > 0) {
				zout.write(buf, 0, rd);
			}
			zout.closeEntry();
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}

	public static void put2zip(ZipOutputStream zout, String name, File f)
			throws Exception {
		ZipEntry ze = new ZipEntry(name);
		zout.putNextEntry(ze);
		byte[] buf = new byte[102400];
		int rd;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(f);
			while ((rd = fin.read(buf)) > 0) {
				zout.write(buf, 0, rd);
			}
			zout.closeEntry();
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}

	/**
	 * Ìæ»»
	 * 
	 * @param zipfile
	 * @param replacename
	 * @param replacefile
	 * @throws Exception
	 */
	public static void replaceZipfile(File zipfile, String replacename,
			File replacefile) throws Exception {
		File tempzipfile = null;
		ZipOutputStream zout = null;
		ZipFile orgfile = null;
		try {
			orgfile = new ZipFile(zipfile);
		} catch (Exception e) {
			orgfile = null;
		}
		byte[] buf = new byte[102400];
		int rd;
		InputStream in = null;
		boolean replaced = false;
		try {
			tempzipfile = File.createTempFile("temp", "zip");
			zout = new ZipOutputStream(tempzipfile);

			if (orgfile != null) {
				Enumeration<ZipEntry> en = orgfile.getEntries();
				while (en.hasMoreElements()) {
					ZipEntry ze = en.nextElement();
					if (ze.getName().equals(replacename)) {
						put2zip(zout, replacename, replacefile);
						replaced = true;
					} else {
						ZipEntry newze = new ZipEntry(ze.getName());
						zout.putNextEntry(newze);
						in = orgfile.getInputStream(ze);
						while ((rd = in.read(buf)) > 0) {
							zout.write(buf, 0, rd);
						}
						in.close();
						in = null;
					}
				}
				orgfile.close();
			}

			if (!replaced) {
				put2zip(zout, replacename, replacefile);
			}

			zout.close();
			zout = null;
			copyFile(tempzipfile, zipfile);

		} finally {
			if (in != null) {
				in.close();
			}
			if (zout != null) {
				zout.close();
			}
			if (tempzipfile != null) {
				tempzipfile.delete();
			}
		}

		// copyfile
	}

	public static void copyFile(File srcfile, File targetfile) throws Exception {
		byte[] buf = new byte[102400];
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(srcfile);
			targetfile.getParentFile().mkdirs();
			fout = new FileOutputStream(targetfile);
			int rd;
			while ((rd = fin.read(buf)) > 0) {
				fout.write(buf, 0, rd);
			}
		} finally {
			if (fin != null)
				fin.close();
			if (fout != null)
				fout.close();
		}

	}

	public static boolean extractFile(File zipfile, String targetname,
			File targetfile) throws Exception {
		ZipFile orgfile = new ZipFile(zipfile);

		byte[] buf = new byte[102400];
		int rd;
		Enumeration<ZipEntry> en = orgfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if (!ze.getName().equals(targetname))
				continue;

			InputStream fin = null;
			FileOutputStream fout = null;
			try {
				fin = orgfile.getInputStream(ze);
				fout = new FileOutputStream(targetfile);
				while ((rd = fin.read(buf)) > 0) {
					fout.write(buf, 0, rd);
				}
				orgfile.close();
				return true;
			} finally {
				if (fin != null) {
					fin.close();
				}
				if (fout != null) {
					fout.close();
				}
			}

		}
		orgfile.close();
		return false;

	}
}
