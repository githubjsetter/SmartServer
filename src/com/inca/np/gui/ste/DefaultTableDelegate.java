package com.inca.np.gui.ste;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.log4j.Category;

import com.inca.np.util.SpecialProjectManager;

/**
 * Ĭ�ϵ�ר���༭table�������.
 * ��zxclassĿ¼��,"����.table"�ļ�
 * @author Administrator
 *
 */
public class DefaultTableDelegate extends CSteModel.TableDelegate{

	Category logger=Category.getInstance(DefaultTableDelegate.class);
	/**
	 * ��ȡ����ļ�
	 * @deprecated
	 */
	public String[] on_setTableColumns(CSteModel stemodel,String[] tablecolumns) {
		return null;
/*		 File zxfile=SpecialProjectManager.getZxfile(stemodel, ".table");
		 if(!zxfile.exists())return null;
		 
		 //��ȡ�ļ�
		 String line=null;
		 BufferedReader reader = null;
		 try {
			reader = new BufferedReader(new FileReader(zxfile));
			line = reader.readLine();
		} catch (Exception e) {
			logger.error("ERROR",e);
			return null;
		}
		
		if(line==null)
		 return null;
		String newcols[]=line.split(",");
		
		ArrayList<String> ar=new ArrayList<String>();
		for(int i=0;i<newcols.length;i++){
			String newcol=newcols[i];
			
			for(int j=0;j<tablecolumns.length;j++){
				if(tablecolumns[j].equalsIgnoreCase(newcol)){
					ar.add(newcol);
					break;
				}
			}
		}
		
		//����Ʒ���ӵ���
		for(int j=0;j<tablecolumns.length;j++){
			String colname=tablecolumns[j];
			boolean has=false;
			for(int i=0;i<ar.size();i++){
				if(ar.get(i).equalsIgnoreCase(colname)){
					has=true;
					break;
				}
			}
			if(!has){
				ar.add(colname);
			}
		}
		
		String resultcols[]=new String[ar.size()];
		resultcols = ar.toArray(resultcols);
		return resultcols;
*/		
	}
	

}
