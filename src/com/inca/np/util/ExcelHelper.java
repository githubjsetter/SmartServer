package com.inca.np.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 将DBTableModel写入excel
 * 
 * @author Administrator
 * 
 */
public class ExcelHelper {
	public static void writeExcel(File outf, String sheetname,
			CTable table) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetname);
		bindData(workbook,sheet, table,0,table.getRowCount()-1);

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(outf);
			workbook.write(fout);
		} finally {
			if (fout != null)
				fout.close();
		}

	}

	public static void bindData(HSSFWorkbook workbook ,HSSFSheet sheet, CTable table,
			int startrow,int endrow) {
		DBTableModel dbmodel=(DBTableModel)table.getModel();
		HSSFFont titlefont=workbook.createFont();
		titlefont.setFontName("宋体");
		titlefont.setBoldweight((short)700);
		
		HSSFCellStyle stylecenter=workbook.createCellStyle();
		stylecenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle styleright=workbook.createCellStyle();
		styleright.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		HSSFCellStyle styledate=workbook.createCellStyle();
		styledate.setDataFormat((short)0x16);

		
		
		int row = 0;
		HSSFRow excelrow = sheet.createRow(row);
		// 插入标题行
		ArrayList<DBColumnDisplayInfo> ar = new ArrayList<DBColumnDisplayInfo>();
		short c = 0;
		HSSFCell cell;
		TableColumnModel tm=table.getColumnModel();
		Vector<DBColumnDisplayInfo> cols = dbmodel.getDisplaycolumninfos();
		for(int tc=0;tc<tm.getColumnCount();tc++){
			TableColumn tcol=tm.getColumn(tc);
			DBColumnDisplayInfo colinfo = cols.elementAt(tcol.getModelIndex());
			if (colinfo.getColtype().equals("行号"))
				continue;
			//都显示
			//if (colinfo.isHide())
			//	continue;
			cell = excelrow.createCell(c++);
			cell.setCellStyle(stylecenter);
			HSSFRichTextString rstr=new HSSFRichTextString(colinfo.getTitle());
			rstr.applyFont(titlefont);
			cell.setCellValue(rstr);
			ar.add(colinfo);
			
		}
		
		DBColumnDisplayInfo colinfos[] = new DBColumnDisplayInfo[ar.size()];
		ar.toArray(colinfos);

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		for (c = 0; c < colinfos.length; c++) {
			row=1;
			String colname = colinfos[c].getColname();
			int mi = dbmodel.getColumnindex(colname);
			if (colinfos[c].getColtype().equalsIgnoreCase("varchar")) {
				for (int r = startrow; r <=endrow; r++) {
					excelrow = sheet.createRow(row++);
					String v = dbmodel.getItemValue(r, mi);
					if(colinfos[c].getEditcomptype().equals(DBColumnDisplayInfo.EDITCOMP_COMBOBOX)){
						String cbv=colinfos[c].getComboboxValue(v);
						if(cbv!=null){
							cell = excelrow.createCell(c);
							cell.setCellValue(new HSSFRichTextString(cbv));
							continue;
						}
					}

					cell = excelrow.createCell(c);
					cell.setCellValue(new HSSFRichTextString(v));
				}
			} else if (colinfos[c].getColtype().equalsIgnoreCase("date")) {
				for (int r = startrow; r <=endrow; r++) {
					excelrow = sheet.createRow(row++);
					String v = dbmodel.getItemValue(r, mi);
					if (v == null || v.length() == 0)
						continue;
					if (v.length() == 10) {
						try {
							Date dt = (Date) df2.parseObject(v);
							cal.setTime(dt);
							cell = excelrow.createCell(c);
							cell.setCellValue(cal);
							cell.setCellStyle(styledate);
						} catch (Exception e) {
						}
					} else {
						try {
							Date dt = (Date) df1.parseObject(v);
							cal.setTime(dt);
							cell = excelrow.createCell(c);
							cell.setCellValue(cal);
							cell.setCellStyle(styledate);
						} catch (Exception e) {
						}
					}
				}
			} else {
				// number
				for (int r = startrow; r <=endrow; r++) {
					excelrow = sheet.createRow(row++);
					String v = dbmodel.getItemValue(r, mi);
					if(colinfos[c].getEditcomptype().equals(DBColumnDisplayInfo.EDITCOMP_COMBOBOX)){
						String cbv=colinfos[c].getComboboxValue(v);
						if(cbv!=null){
							cell = excelrow.createCell(c);
							cell.setCellValue(new HSSFRichTextString(cbv));
							continue;
						}
					}
					double dv = 0;
					try {
						dv = Double.parseDouble(v);
						cell = excelrow.createCell(c);
						cell.setCellValue(dv);
						cell.setCellStyle(styleright);
					} catch (Exception e) {
					}
				}
			}

		}

	}
	
	public static void main(String[] args) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("test");
		HSSFRow hssfrow=sheet.createRow(0);
		HSSFCell cell=hssfrow.createCell((short)0);
		cell.setCellValue(new HSSFRichTextString("01234"));
		

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(new File("test.xls"));
			workbook.write(fout);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}
}
