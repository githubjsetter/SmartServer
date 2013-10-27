package com.inca.npbi.client.storer;

import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.poi.hpsf.ReadingNotSupportedException;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.SplitGroupInfo;
import com.inca.np.gui.control.SplitGroupInfo.Datacolumn;
import com.inca.npbi.client.design.BICell;
import com.inca.npbi.client.design.BIReportdsDefine;
import com.inca.npbi.client.design.BITableV_Render;
import com.inca.npbi.client.design.BITableV_def;
import com.inca.npbi.client.design.Chartdefine;
import com.inca.npbi.client.design.BITableV_def.Mergeinfo;
import com.inca.npbi.client.design.link.Linkinfo;
import com.inca.npbi.client.design.param.BIReportparamdefine;

public class BIReportStorage {
	public static void writeDS(PrintWriter out, BIReportdsDefine ds) {
		out.println("<datasource>");
		out.println("<sql>");
		out.println(ds.sql);
		out.println("</sql>");

		out.println("<columns>");
		Enumeration<DBColumnDisplayInfo> en = ds.datadm.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			out.print(colinfo.getColname() + ":");
			out.print(colinfo.getColtype() + ":");
			out.print(colinfo.getTitle());
			out.println();
		}
		out.println("</columns>");
		out.println("<params>");
		Enumeration<BIReportparamdefine> pen = ds.params.elements();
		while (pen.hasMoreElements()) {
			BIReportparamdefine p = pen.nextElement();
			out.println("<param>");
			out.println("<paramname>" + p.paramname + "</paramname>");
			out.println("<paramtype>" + p.paramtype + "</paramtype>");
			out.println("<title>" + p.title + "</title>");
			out.println("<numberwidth>" + p.numberwidth + "</numberwidth>");
			out.println("<mustinput>" + (p.mustinput ? "true" : "false")
					+ "</mustinput>");
			out.println("<autocond>");
			out.println(p.autocond);
			out.println("</autocond>");
			out.println("<hovclass>" + p.hovclass + "</hovclass>");
			out.println("<hovcols>" + p.hovcols + "</hovcols>");
			out.println("<initvalue>" + p.initvalue + "</initvalue>");
			out.println("</param>");
		}
		out.println("</params>");

		out.println("</datasource>");
	}

	public static Vector<BIReportdsDefine> readDs(BufferedReader rd)
			throws Exception {
		Vector<BIReportdsDefine> dss = new Vector<BIReportdsDefine>();
		String line = "";
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<datasource>")) {
				BIReportdsDefine ds = readaDs(rd);
				if (ds != null) {
					dss.add(ds);
				}
			}
		}
		return dss;
	}

	private static BIReportdsDefine readaDs(BufferedReader rd) throws Exception {
		BIReportdsDefine ds = new BIReportdsDefine();
		String line = "";
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<sql>")) {
				ds.sql = readMultiline(rd, "</sql>");
				if (ds.sql.trim().length() == 0)
					return null;
			} else if (line.startsWith("<columns>")) {
				String s = readMultiline(rd, "</columns>");
				String lines[] = s.split("\n");
				Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
				for (int i = 0; i < lines.length; i++) {
					String colstr = lines[i];
					String tmps[] = colstr.split(":");
					String colname = tmps[0];
					String coltype = tmps[1];
					String title = colname;
					if (tmps.length >= 3)
						title = tmps[2];
					cols.add(new DBColumnDisplayInfo(colname, coltype, title));
				}
				ds.datadm = new DBTableModel(cols);
			} else if (line.startsWith("<params")) {
				readParams(rd, ds);
			} else if (line.startsWith("</datasource>")) {
				break;
			}
		}
		return ds;
	}

	private static void readParams(BufferedReader rd, BIReportdsDefine ds)
			throws Exception {
		String line = "";
		ds.params = new Vector<BIReportparamdefine>();
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</params>")) {
				break;
			} else if (line.startsWith("<param>")) {
				readParam(rd, ds);
			}
		}
	}

	private static void readParam(BufferedReader rd, BIReportdsDefine ds)
			throws Exception {
		String line = "";
		BIReportparamdefine p = null;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</param>")) {
				break;
			} else if (line.startsWith("<paramname>")) {
				p = new BIReportparamdefine();
				ds.params.add(p);
				p.paramname = getValue(line);
			} else if (line.startsWith("<paramtype>")) {
				p.paramtype = getValue(line);
			} else if (line.startsWith("<title>")) {
				p.title = getValue(line);
			} else if (line.startsWith("<numberwidth>")) {
				p.numberwidth = getIntValue(line);
			} else if (line.startsWith("<mustinput>")) {
				p.mustinput = getValue(line).equals("true");
			} else if (line.startsWith("<hovclass>")) {
				p.hovclass = getValue(line);
			} else if (line.startsWith("<hovcols>")) {
				p.hovcols = getValue(line);
			} else if (line.startsWith("<initvalue>")) {
				p.initvalue = getValue(line);
			} else if (line.startsWith("<autocond>")) {
				p.autocond = readMultiline(rd, "</autocond>");
			}
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

	public static void writeTablevdefine(PrintWriter out, BITableV_def tabledef) {
		out.println("<tablev>");
		out
				.println("<columncount>" + tabledef.getColcount()
						+ "</columncount>");
		out.print("<colwidths>");
		int colwidth[] = tabledef.getColwidths();
		for (int i = 0; i < colwidth.length; i++) {
			if (i > 0)
				out.print(":");
			out.print(colwidth[i]);
		}
		out.println("</colwidths>");

		out.println("<rowcount>" + tabledef.getRowcount() + "</rowcount>");
		out.print("<rowheights>");
		int rowheights[] = tabledef.getRowheights();
		for (int i = 0; i < rowheights.length; i++) {
			if (i > 0)
				out.print(":");
			out.print(rowheights[i]);
		}
		out.println("</rowheights>");

		out.print("<rowtypes>");
		int rowtypes[] = tabledef.getRowtypes();
		for (int i = 0; i < rowheights.length; i++) {
			if (i > 0)
				out.print(":");
			out.print(rowtypes[i]);
		}
		out.println("</rowtypes>");

		out.println("<fixrowcountperpage>" + tabledef.getFixrowcountperpage()
				+ "</fixrowcountperpage>");
		out.println("<drawgrid>" + (tabledef.isDrawgrid() ? "true" : "false")
				+ "</drawgrid>");
		out.println("<gridwidth>" + tabledef.getGridwidth() + "</gridwidth>");

		// 输出cell
		BICell cells[][] = tabledef.getCells();
		for (int i = 0; i < cells.length; i++) {
			BICell linecells[] = cells[i];
			for (int j = 0; j < linecells.length; j++) {
				BICell bicell = linecells[j];
				writeCell(out, bicell);
			}
		}

		// 输出分组
		out.println("<groupinfos>");
		Enumeration<SplitGroupInfo> grpen = tabledef.getGroupinfos().elements();
		while (grpen.hasMoreElements()) {
			SplitGroupInfo grp = grpen.nextElement();
			writeSplitgroup(out, grp);
		}
		out.println("</groupinfos>");

		// 输出合并
		out.println("<mergeinfos>");
		Enumeration<Mergeinfo> enminfo = tabledef.getMergeinfos().elements();
		while (enminfo.hasMoreElements()) {
			Mergeinfo minfo = enminfo.nextElement();
			out.println("<mergeinfo>" + minfo.startrow + ":" + minfo.rowcount
					+ ":" + minfo.startcolumn + ":" + minfo.columncount
					+ "</mergeinfo>");
		}
		out.println("</mergeinfos>");

		out.println("</tablev>");
	}

	private static void writeSplitgroup(PrintWriter out, SplitGroupInfo grp) {
		// 输出分组列
		out.println("<groupinfo>");
		out.print("<groupcolumn>");
		Enumeration<String> en = grp.getGroupGroupcolumns();
		int i = 0;
		while (en.hasMoreElements()) {
			if (i > 0)
				out.print(":");
			out.print(en.nextElement());
			i++;
		}
		out.println("</groupcolumn>");
		// 数据列
		out.println("<datacolumn>");
		Enumeration<Datacolumn> gen = grp.getDatacolumn();
		while (gen.hasMoreElements()) {
			Datacolumn dc = gen.nextElement();
			out.println(dc.getColname() + ":" + dc.getMethod());
		}
		out.println("</datacolumn>");
		out.println("<title>" + grp.getTitle() + "</title>");
		out.println("</groupinfo>");
	}

	private static SplitGroupInfo readGroupinfo(BufferedReader rd,
			BITableV_def tabledef) throws Exception {
		String line;
		Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();
		SplitGroupInfo groupinfo = null;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</groupinfo>")) {
				break;
			} else if (line.startsWith("<groupcolumn>")) {
				String[] ss = getValue(line).split(":");
				groupinfo = new SplitGroupInfo();
				for (int i = 0; i < ss.length; i++)
					groupinfo.addGroupColumn(ss[i]);
				groupinfos.add(groupinfo);
			} else if (line.startsWith("<datacolumn>")) {
				String lines[] = readMultiline(rd, "</datacolumn>").split("\n");
				for (int i = 0; i < lines.length; i++) {
					String ss[] = lines[i].split(":");
					groupinfo.addDataColumn(ss[0], ss[1]);
				}
			} else if (line.startsWith("<title>")) {
				groupinfo.setTitle(getValue(line));
			}
		}
		return groupinfos.elementAt(0);
	}

	private static void readMergeinfo(String line, BITableV_def tabledef)
			throws Exception {
		String ss[] = line.split(":");
		int startrow = Integer.parseInt(ss[0]);
		int rowcount = Integer.parseInt(ss[1]);
		int startcol = Integer.parseInt(ss[2]);
		int colcount = Integer.parseInt(ss[3]);
		tabledef.addMerge(startrow, rowcount, startcol, colcount);
	}

	public static void readTablevdefine(BufferedReader rd, BITableV_def tabledef)
			throws Exception {
		String line;
		Vector<BICell> cells = new Vector<BICell>();
		Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<columncount>")) {
				tabledef.setColcount(getIntValue(line));
			} else if (line.startsWith("<colwidths>")) {
				String ss[] = getValue(line).split(":");
				int colwidths[] = new int[ss.length];
				for (int i = 0; i < ss.length; i++) {
					try {
						colwidths[i] = Integer.parseInt(ss[i]);
					} catch (Exception e) {
						colwidths[i] = 30;
					}
				}
				tabledef.setColwidths(colwidths);
			} else if (line.startsWith("<rowcount>")) {
				tabledef.setRowcount(getIntValue(line));
			} else if (line.startsWith("<rowheights>")) {
				String ss[] = getValue(line).split(":");
				int rowheights[] = new int[ss.length];
				for (int i = 0; i < ss.length; i++) {
					rowheights[i] = Integer.parseInt(ss[i]);
				}
				tabledef.setRowheights(rowheights);
			} else if (line.startsWith("<rowtypes>")) {
				String ss[] = getValue(line).split(":");
				int rowtypes[] = new int[ss.length];
				for (int i = 0; i < ss.length; i++) {
					rowtypes[i] = Integer.parseInt(ss[i]);
				}
				tabledef.setRowtypes(rowtypes);
			} else if (line.startsWith("<fixrowcountperpage>")) {
				tabledef.setFixrowsperpage(getIntValue(line));
			} else if (line.startsWith("<drawgrid>")) {
				tabledef.setDrawgrid(getValue(line).equals("true"));
			} else if (line.startsWith("<gridwidth>")) {
				tabledef.setGridwidth(getIntValue(line));
			} else if (line.startsWith("<bicell>")) {
				BICell cell = readBicell(rd);
				cells.add(cell);
			} else if (line.startsWith("<groupinfo>")) {
				SplitGroupInfo grpinfo = readGroupinfo(rd, tabledef);
				grpinfo.setLevel(groupinfos.size());
				groupinfos.add(grpinfo);
			} else if (line.startsWith("<mergeinfo>")) {
				readMergeinfo(getValue(line), tabledef);
			} else if (line.startsWith("</tablev>")) {
				break;
			}
		}
		// 设置cell
		BICell cellar[][] = new BICell[tabledef.getRowcount()][tabledef
				.getColcount()];
		for (int r = 0; r < tabledef.getRowcount(); r++) {
			for (int c = 0; c < tabledef.getColcount(); c++) {
				cellar[r][c] = cells.elementAt(r * tabledef.getColcount() + c);
			}
		}
		tabledef.setCells(cellar);
		tabledef.setGroupinfos(groupinfos);
	}

	private static void writeCell(PrintWriter out, BICell bicell) {
		out.println("<bicell>");
		writeCellImpl(out, bicell);
		out.println("</bicell>");
	}

	private static void writeCellImpl(PrintWriter out, BICell bicell) {
		out.println("<expr>");
		out.println(bicell.getExpr());
		out.println("</expr>");

		out.println("<align>" + bicell.getAlign() + "</align>");
		out.println("<valign>" + bicell.getValign() + "</valign>");
		out.println("<repeat>" + bicell.getRepeat() + "</repeat>");
		out.println("<fontname>" + bicell.getFontname() + "</fontname>");
		out.println("<fontsize>" + bicell.getFontsize() + "</fontsize>");
		out
				.println("<bold>" + (bicell.isBold() ? "true" : "false")
						+ "</bold>");
		out.println("<italic>" + (bicell.isItalic() ? "true" : "false")
				+ "</italic>");
		out.println("<format>" + bicell.getFormat() + "</format>");
		out.println("<xpadding>" + bicell.getXpadding() + "</xpadding>");
		out.println("<ypadding>" + bicell.getYpadding() + "</ypadding>");
		out.println("<size>" + bicell.getSize().width + ":"
				+ bicell.getSize().height + "</size>");
		
		out.println("<links>");
		Enumeration<Linkinfo>en=bicell.getLinkinfos().elements();
		while(en.hasMoreElements()){
			Linkinfo linkinfo=en.nextElement();
			out.println("<link>");
			out.println("<linkname>"+linkinfo.getLinkname()+"</linkname>");
			out.println("<callopid>"+linkinfo.getCallopid()+"</callopid>");
			out.println("<callopname>"+linkinfo.getCallopname()+"</callopname>");
			out.println("<callcond>");
			out.println(linkinfo.getCallcond());
			out.println("</callcond>");
			out.println("</link>");
		}
		out.println("</links>");
	}

	public static void writeFreeCell(PrintWriter out, BICell bicell) {
		out.println("<freebicell>");
		writeCellImpl(out, bicell);
		out.println("</freebicell>");
	}

	public static BICell readFreecell(BufferedReader rd) throws Exception {
		BICell cell = new BICell();
		readBicellImpl(rd, cell, "</freebicell>");
		return cell;
	}

	private static void readBicellImpl(BufferedReader rd, BICell cell,
			String endflag) throws Exception {
		// 读表达式.
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<expr>")) {
				cell.setExpr(readMultiline(rd, "</expr>"));
			} else if (line.startsWith("<align>")) {
				cell.setAlign(getIntValue(line));
			} else if (line.startsWith("<valign>")) {
				cell.setValign(getIntValue(line));
			} else if (line.startsWith("<repeat>")) {
				cell.setRepeat(getValue(line));
			} else if (line.startsWith("<fontname>")) {
				cell.setFontname(getValue(line));
			} else if (line.startsWith("<fontsize>")) {
				cell.setFontsize(getIntValue(line));
			} else if (line.startsWith("<format>")) {
				cell.setFormat(getValue(line));
			} else if (line.startsWith("<xpadding>")) {
				cell.setXpadding(getIntValue(line));
			} else if (line.startsWith("<ypadding>")) {
				cell.setYpadding(getIntValue(line));
			} else if (line.startsWith("<bold>")) {
				cell.setBold(getValue(line).equals("true"));
			} else if (line.startsWith("<italic>")) {
				cell.setItalic(getValue(line).equals("true"));
			} else if (line.startsWith("<size>")) {
				String ss[] = getValue(line).split(":");
				int w = Integer.parseInt(ss[0]);
				int h = Integer.parseInt(ss[1]);
				cell.setSize(new Dimension(w, h));
			} else if (line.startsWith("<links>")) {
				readLinks(rd,cell);
			} else if (line.startsWith(endflag)) {
				break;
			}
		}
	}
	private static void readLinks(BufferedReader rd,BICell bicell)  throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if(line.startsWith("<link>")){
				Linkinfo linkinfo=new Linkinfo();
				bicell.addLinkinfo(linkinfo);
				readLink(rd,linkinfo);
			}else if(line.startsWith("</links>")){
				break;
			}
		}
	}
	private static void readLink(BufferedReader rd,Linkinfo linkinfo)  throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if(line.startsWith("</link>")){
				break;
			}else if(line.startsWith("<linkname>")){
				linkinfo.setLinkname(getValue(line));
			}else if(line.startsWith("<callopid>")){
				linkinfo.setCallopid(getValue(line));
			}else if(line.startsWith("<callopname>")){
				linkinfo.setCallopname(getValue(line));
			}else if(line.startsWith("<callcond>")){
				linkinfo.setCallcond(readMultiline(rd, "</callcond>"));
			}
		}
	}
	private static BICell readBicell(BufferedReader rd) throws Exception {
		BICell cell = new BICell();
		readBicellImpl(rd, cell, "</bicell>");
		return cell;
	}

	/**
	 * 取得尖括号之间的值
	 * 
	 * @param line
	 * @return
	 */
	public static String getValue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p + 1);
		return line.substring(p + 1, p1);
	}

	public static int getIntValue(String line) {
		String s = getValue(line);
		int i;
		try {
			i = Integer.parseInt(s);
			return i;
		} catch (Exception e) {
			return 0;
		}
	}

	public static void writeChart(PrintWriter out, Chartdefine chartdefine) {
		out.println("<chart>");
		out.println("<dsdefineindex>" + chartdefine.dsdefineindex
				+ "</dsdefineindex>");
		out.println("<charttype>" + chartdefine.charttype + "</charttype>");
		out.println("<dimension>" + chartdefine.dimension + "</dimension>");
		out.println("<maxrowcount>" + chartdefine.maxrowcount
				+ "</maxrowcount>");
		out.println("<title>" + chartdefine.title + "</title>");
		out.println("<x1column>" + chartdefine.x1column + "</x1column>");
		out.println("<xtitle>" + chartdefine.xtitle + "</xtitle>");
		out.println("<x1fontrotation>" + chartdefine.x1fontrotation
				+ "</x1fontrotation>");
		out.println("<ytitle>" + chartdefine.ytitle + "</ytitle>");
		out.println("<yfontrotation>" + chartdefine.yfontrotation
				+ "</yfontrotation>");
		out.println("<y1column>" + chartdefine.y1column + "</y1column>");
		out.println("<y1title>" + chartdefine.y1title + "</y1title>");
		out.println("<y1op>" + chartdefine.y1op + "</y1op>");
		out.println("<y2column>" + chartdefine.y2column + "</y2column>");
		out.println("<y2title>" + chartdefine.y2title + "</y2title>");
		out.println("<y3column>" + chartdefine.y3column + "</y3column>");
		out.println("<y3title>" + chartdefine.y3title + "</y3title>");
		out
				.println("<showlegend>"
						+ (chartdefine.showlegend ? "true" : "false")
						+ "</showlegend>");
		out.println("<sortcolumn>" + chartdefine.sortcolumn + "</sortcolumn>");
		out.println("<sortdesc>" + (chartdefine.sortdesc ? "true" : "false")
				+ "</sortdesc>");
		out.println("<colortype>" + chartdefine.colortype + "</colortype>");
		out.println("<showdata>" + chartdefine.showdata + "</showdata>");

		// font
		out.println("<titlefont>");
		writeFont(out, chartdefine.titlefont);
		out.println("</titlefont>");

		out.println("<xfont>");
		writeFont(out, chartdefine.xfont);
		out.println("</xfont>");

		out.println("<x1font>");
		writeFont(out, chartdefine.x1font);
		out.println("</x1font>");

		out.println("<yfont>");
		writeFont(out, chartdefine.yfont);
		out.println("</yfont>");

		out.println("<y1font>");
		writeFont(out, chartdefine.y1font);
		out.println("</y1font>");

		out.println("</chart>");
	}

	public static void readChart(BufferedReader rd, Chartdefine chartdefine)
			throws Exception {
		String line = "";
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</chart>")) {
				break;
			} else if (line.startsWith("<charttype>")) {
				chartdefine.charttype = getIntValue(line);
			} else if (line.startsWith("<dimension>")) {
				chartdefine.dimension = getIntValue(line);
			} else if (line.startsWith("<maxrowcount>")) {
				chartdefine.maxrowcount = getIntValue(line);
			} else if (line.startsWith("<dsdefineindex>")) {
				chartdefine.dsdefineindex = getIntValue(line);
			} else if (line.startsWith("<title>")) {
				chartdefine.title = getValue(line);
			} else if (line.startsWith("<x1column>")) {
				chartdefine.x1column = getValue(line);
			} else if (line.startsWith("<xtitle>")) {
				chartdefine.xtitle = getValue(line);
			} else if (line.startsWith("<x1fontrotation>")) {
				chartdefine.x1fontrotation = getIntValue(line);
			} else if (line.startsWith("<ytitle>")) {
				chartdefine.ytitle = getValue(line);
			} else if (line.startsWith("<yfontrotation>")) {
				chartdefine.yfontrotation = getIntValue(line);
			} else if (line.startsWith("<y1column>")) {
				chartdefine.y1column = getValue(line);
			} else if (line.startsWith("<y1title>")) {
				chartdefine.y1title = getValue(line);
			} else if (line.startsWith("<y1op>")) {
				chartdefine.y1op = getIntValue(line);
			} else if (line.startsWith("<y2column>")) {
				chartdefine.y2column = getValue(line);
			} else if (line.startsWith("<y2title>")) {
				chartdefine.y2title = getValue(line);
			} else if (line.startsWith("<y3column>")) {
				chartdefine.y3column = getValue(line);
			} else if (line.startsWith("<y3title>")) {
				chartdefine.y3title = getValue(line);
			} else if (line.startsWith("<showlegend>")) {
				chartdefine.showlegend = getValue(line).equals("true");
			} else if (line.startsWith("<sortcolumn>")) {
				chartdefine.sortcolumn = getValue(line);
			} else if (line.startsWith("<sortdesc>")) {
				chartdefine.sortdesc = getValue(line).equals("true");
			} else if (line.startsWith("<colortype>")) {
				chartdefine.colortype = getIntValue(line);
			} else if (line.startsWith("<showdata>")) {
				chartdefine.showdata = getIntValue(line);
			} else if (line.startsWith("<titlefont>")) {
				chartdefine.titlefont = readFont(rd);
				rd.readLine();// 读入</titlefont>
			} else if (line.startsWith("<xfont>")) {
				chartdefine.xfont = readFont(rd);
				rd.readLine();// 读入</xfont>
			} else if (line.startsWith("<x1font>")) {
				chartdefine.x1font = readFont(rd);
				rd.readLine();// 读入</x1font>
			} else if (line.startsWith("<yfont>")) {
				chartdefine.yfont = readFont(rd);
				rd.readLine();// 读入</yfont>
			} else if (line.startsWith("<y1font>")) {
				chartdefine.yfont = readFont(rd);
				rd.readLine();// 读入</y1font>
			}
		}
	}

	static void writeFont(PrintWriter out, Font font) {
		out.println("<font>");
		out.println("<fontname>" + font.getName() + "</fontname>");
		out.println("<fontsize>" + font.getSize() + "</fontsize>");
		out.println("<bold>"
				+ ((font.getStyle() & Font.BOLD) != 0 ? "true" : "false")
				+ "</fontsize>");
		out.println("<italic>"
				+ ((font.getStyle() & Font.ITALIC) != 0 ? "true" : "false")
				+ "</italic>");
		out.println("</font>");
	}

	static Font readFont(BufferedReader rd) throws Exception {
		String line;
		String fontname = "";
		int fontsize = 12;
		int style = 0;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</font>")) {
				break;
			} else if (line.startsWith("<fontname>")) {
				fontname = getValue(line);
			} else if (line.startsWith("<fontsize>")) {
				fontsize = getIntValue(line);
			} else if (line.startsWith("<bold>")) {
				if ("true".equals(getValue(line))) {
					style |= Font.BOLD;
				}
			} else if (line.startsWith("<italic>")) {
				if ("true".equals(getValue(line))) {
					style |= Font.ITALIC;
				}
			}
		}
		return new Font(fontname, style, fontsize);
	}

}
