package com.inca.npserver.pushplat.common;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * 推送信息
 * 
 * @author user
 * 
 */
public class Pushinfo {
	public static int LEVEL_MOST_URGENT = 1;
	public static int LEVEL_URGENT = 2;
	public static int LEVEL_NORMAL = 3;
	String pushid = "";
	String pushname = "";
	String groupname = "";
	int level = LEVEL_NORMAL;
	String callopid = "";
	String callopname = "";
	String wheres = "";
	
	/**
	 * 授权附加的查询条件.
	 */
	String otherwheres="";
	
	int rowcount=0;

	public void write(PrintWriter out) {
		out.println("<push>");
		out.println("<pushid>" + pushid + "</pushid>");
		out.println("<pushname>" + pushname + "</pushname>");
		out.println("<groupname>" + groupname + "</groupname>");
		out.println("<level>" + level + "</level>");
		out.println("<callopid>" + callopid + "</callopid>");
		out.println("<callopname>" + callopname + "</callopname>");
		out.println("<wheres>");
		out.println(wheres);
		out.println("</wheres>");
		out.println("</push>");
	}

	public static Vector<Pushinfo> readPushinfos(BufferedReader read)
			throws Exception {
		Vector<Pushinfo> infos = new Vector<Pushinfo>();

		String line = "";
		while ((line = read.readLine()) != null) {
			if (line.startsWith("</pushs>")) {
				break;
			} else if (line.startsWith("<push>")) {
				Pushinfo info = readPushinfo(read);
				infos.add(info);
			}
		}
		return infos;
	}

	public static Pushinfo readPushinfo(BufferedReader read) throws Exception {
		Pushinfo pushinfo = new Pushinfo();
		String line = "";
		while ((line = read.readLine()) != null) {
			if (line.startsWith("</push>")) {
				break;
			} else if (line.startsWith("<pushid>")) {
				pushinfo.pushid = getValue(line);
			} else if (line.startsWith("<pushname>")) {
				pushinfo.pushname = getValue(line);
			} else if (line.startsWith("<groupname>")) {
				pushinfo.groupname = getValue(line);
			} else if (line.startsWith("<callopid>")) {
				pushinfo.callopid = getValue(line);
			} else if (line.startsWith("<callopname>")) {
				pushinfo.callopname = getValue(line);
			} else if (line.startsWith("<level>")) {
				pushinfo.level = getIntValue(line);
			} else if (line.startsWith("<wheres>")) {
				pushinfo.wheres = readMultiline(read, "</wheres>");
			}
		}
		return pushinfo;
	}

	public static String getValue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p + 1);
		return line.substring(p + 1, p1);
	}

	private static int getIntValue(String line) {
		String s = getValue(line);
		int i;
		try {
			i = Integer.parseInt(s);
			return i;
		} catch (Exception e) {
			return 0;
		}
	}

	private static String readMultiline(BufferedReader rd, String endflag)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			if (line.startsWith(endflag)) {
				break;
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(line);
		}
		return sb.toString();
	}

	public String getPushid() {
		return pushid;
	}

	public void setPushid(String pushid) {
		this.pushid = pushid;
	}

	public String getPushname() {
		return pushname;
	}

	public void setPushname(String pushname) {
		this.pushname = pushname;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCallopid() {
		return callopid;
	}

	public void setCallopid(String callopid) {
		this.callopid = callopid;
	}

	public String getCallopname() {
		return callopname;
	}

	public void setCallopname(String callopname) {
		this.callopname = callopname;
	}

	public String getWheres() {
		return wheres;
	}

	public void setWheres(String wheres) {
		this.wheres = wheres;
	}

	public String getOtherwheres() {
		return otherwheres;
	}

	public void setOtherwheres(String otherwheres) {
		this.otherwheres = otherwheres;
	}

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}

	public String getFullwheres(){
		StringBuffer sb=new StringBuffer();
		sb.append(wheres);
		if (getOtherwheres().length() > 0) {
			if (sb.length() > 0) {
				sb.append( " and " );
			}
			sb.append(getOtherwheres());
		}

		return sb.toString();
	}
}
