package com.inca.np.demo.game;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.CSteModel;

/**
 * 数独STE
 * 
 * @author user
 * 
 */
public class Uniquedig_ste extends CSteModel {

	File savefile=null;
	JFileChooser jfc=null;
	
	public Uniquedig_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected void loadDBColumnInfos() {
		formcolumndisplayinfos=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("行号", "行号", "行号");
		formcolumndisplayinfos.add(col);

		for (int i = 1; i <= 9; i++) {
			col = new DBColumnDisplayInfo("col" + i, "varchar", "col" + i);
			formcolumndisplayinfos.add(col);
		}
	}
	
	void initRows(){
		dbmodel.clearAll();
		for(int i=0;i<9;i++){
			dbmodel.appendRow();
		}
		table.setRowHeight(35);
	}


	@Override
	protected int on_actionPerformed(String command) {
		if("start".equals(command)){
			doStart();
			return 0;
		}else if("think".equals(command)){
			doThink();
		}else if("load".equals(command)){
			load();
		}
		return super.on_actionPerformed(command);
	}

	void doStart(){
		initRows();
		//loadGame1();
		tableChanged();
	}
	
	void loadGame1(){
		dbmodel.setItemValue(0, "col6","7");
		dbmodel.setItemValue(0, "col7","8");
		
		dbmodel.setItemValue(1, "col1", "3");
		dbmodel.setItemValue(1, "col4", "8");
		dbmodel.setItemValue(1, "col9", "6");

		dbmodel.setItemValue(2, "col1", "8");
		dbmodel.setItemValue(2, "col4", "4");
		dbmodel.setItemValue(2, "col5", "5");
		dbmodel.setItemValue(2, "col7", "9");
		dbmodel.setItemValue(2, "col8", "3");

		dbmodel.setItemValue(3, "col1", "5");
		dbmodel.setItemValue(3, "col8", "7");
		dbmodel.setItemValue(3, "col9", "9");
		
		dbmodel.setItemValue(4, "col3", "8");
		dbmodel.setItemValue(4, "col7", "1");

		dbmodel.setItemValue(5, "col1", "9");
		dbmodel.setItemValue(5, "col2", "2");
		dbmodel.setItemValue(5, "col9", "8");
		
		dbmodel.setItemValue(6, "col2", "4");
		dbmodel.setItemValue(6, "col3", "6");
		dbmodel.setItemValue(6, "col5", "8");
		dbmodel.setItemValue(6, "col6", "9");
		dbmodel.setItemValue(6, "col9", "1");

		dbmodel.setItemValue(7, "col1", "1");
		dbmodel.setItemValue(7, "col6", "2");
		dbmodel.setItemValue(7, "col9", "4");

		dbmodel.setItemValue(8, "col3", "3");
		dbmodel.setItemValue(8, "col4", "6");

	}
	
	void doThink(){
		table.stopEdit();
		for(int r=0;r<9;r++){
			for(int c=1;c<=9;c++){
				think(r,c);
			}
		}
		tableChanged();
	}
	
	void think(int r,int c){
		String s=getConfirmvalue(r,c);
		if(s!=null)return;
		
		HashMap<String, String> cannotmap=new HashMap<String, String>();
		thinkRow(r,cannotmap);
		thinkCol(c,cannotmap);
		think9(r,c,cannotmap);
		
		StringBuffer sb=new StringBuffer();
		for(int d=1;d<=9;d++){
			s=String.valueOf(d);
			if(cannotmap.get(s)!=null)continue;
			if(sb.length()>0)sb.append(",");
			sb.append(s);
		}
		if(sb.length()==0){
			dbmodel.setItemValue(r, c, "XXX");
		}else{
			dbmodel.setItemValue(r, c, sb.toString());
		}
	}

	private void think9(int r, int c,HashMap<String, String> cannotmap) {
		int r1,r2;
		int c1,c2;
		r1=r/3*3;
		r2=r1+3;
		c1=(c-1)/3*3+1;
		c2=c1+3;
		
		for(int i=r1;i<r2;i++){
			for(int j=c1;j<c2;j++){
				//System.out.println(i+","+j);
				String s=getConfirmvalue(i, j);
				if(s!=null){
					cannotmap.put(s, s);
				}
			}
		}
		
	}
	
	private void thinkRow(int r, HashMap<String, String> cannotmap) {
		for(int c=1;c<=9;c++){
			String s=getConfirmvalue(r, c);
			if(s!=null){
				cannotmap.put(s, s);
			}
		}
	}

	private void thinkCol(int c, HashMap<String, String> cannotmap) {
		for(int r=0;r<9;r++){
			String s=getConfirmvalue(r, c);
			if(s!=null){
				cannotmap.put(s, s);
			}
		}
	}

	String getConfirmvalue(int row,int col){
		String s=dbmodel.getItemValue(row, col);
		if(s.length()==1)return s;
		return null;
	}

	@Override
	public int doSave() {
		if(jfc==null){
			jfc=new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
		}
		if(savefile!=null){
			jfc.setSelectedFile(savefile);
		}
		int ret=jfc.showDialog(getParentFrame(), "选择文件");
		if(ret!=JFileChooser.APPROVE_OPTION)return -1;
		savefile=jfc.getSelectedFile();
		
		try{
		PrintWriter out=new PrintWriter(new FileWriter(savefile));
		for(int r=0;r<9;r++){
			StringBuffer sb=new StringBuffer();
			for(int c=1;c<=9;c++){
				if(c>1)sb.append(":");
				sb.append(dbmodel.getItemValue(r, c));
			}
			out.println(sb.toString());
		}
		out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return 0;
	}
	
	void load(){
		if(jfc==null){
			jfc=new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
		}
		if(savefile!=null){
			jfc.setSelectedFile(savefile);
		}
		int ret=jfc.showDialog(getParentFrame(), "选择文件");
		if(ret!=JFileChooser.APPROVE_OPTION)return ;
		savefile=jfc.getSelectedFile();
		
		BufferedReader rd=null;
		try{
			String line;
			int r=0;
			rd=new BufferedReader(new FileReader(savefile));
			while((line=rd.readLine())!=null){
				String ss[]=line.split(":");
				for(int c=0;c<ss.length;c++){
					dbmodel.setItemValue(r, "col"+String.valueOf(c+1),ss[c]);
				}
				r++;
			}
			rd.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		tableChanged();
	}
}
