package com.smart.platform.gui.ste;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Category;

import com.smart.platform.util.ZipHelper;
import com.smart.platform.util.ZxClassLoader;

/**
 * 可进行控制点编程的专项开发基类
 * @author Administrator
 *
 */
public class ZxstejavaDelegate {
	/**
	 * CSteModel刚创建成功
	 * 
	 * @param model
	 */
	public void on_init(CSteModel model) {

	}

	/**
	 * 刚创建GUI控件
	 */
	public void on_initControl() {

	}

	/**
	 * 按右键
	 * 
	 * @param stemodel
	 * @param row
	 * @param col
	 */
	public void on_rclick(CSteModel stemodel, int row, int col) {

	}

	/**
	 * 双击
	 * 
	 * @param row
	 * @param col
	 */
	public void on_doubleclick(CSteModel steModel, int row, int col) {

	}

	/**
	 * 删除后
	 * 
	 * @param steModel
	 * @param row
	 */
	public void on_del(CSteModel steModel, int row) {

	}

	/**
	 * 删除前
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_beforedel(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * 修改后
	 * 
	 * @param steModel
	 * @param row
	 */
	public void on_modify(CSteModel steModel, int row) {
	}

	/**
	 * 修改前
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_beforemodify(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * 查询结束
	 * 
	 * @param steModel
	 */
	public void on_retrieved(CSteModel steModel) {

	}

	/**
	 * 值变化
	 * 
	 * @param steModel
	 * @param row
	 * @param colname
	 * @param value
	 */
	public void on_itemvaluechange(CSteModel steModel, int row,
			String colname, String value) {

	}

	/**
	 * 表的当前前变化了
	 * 
	 * @param steModel
	 * @param newrow
	 */
	public void on_tablerowchanged(CSteModel steModel, int newrow) {

	}

	/**
	 * 行检查
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_checkrow(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * 窗口关闭前
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforeclose(CSteModel steModel) {

		return 0;
	}

	/**
	 * 保存前
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforesave(CSteModel steModel) {

		return 0;
	}

	/**
	 * 查询前
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforequery(CSteModel steModel) {

		return 0;
	}

	/**
	 * 新增后
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_new(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * 新增前
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforeNew(CSteModel steModel) {

		return 0;
	}

	/**
	 * 当CSteModel选择hov成功返回
	 * 
	 * @param steModel
	 * @param row
	 * @param colname
	 * @return
	 */
	public int on_hov(CSteModel steModel, int row, String colname) {
		return 0;
	}
	
	/**
	 * 返回附加查询条件
	 * @return 返回附加查询条件
	 */
	public String getOtherWheres() {
		return "";
	}
	
	/**
	 * 处理命令
	 * @param steModel
	 * @param command
	 * @return -1 表示没有处理，继续运行。 返回0表示该命令处理了。
	 */
	public int on_actionPerformed(CSteModel steModel,String command) {
		return -1;
	}


	public static ZxstejavaDelegate loadZxfromzxzip(CSteModel stemodel){
		File zxzipfile=stemodel.getZxzipfile();
		String opid=stemodel.getOpid();
		String modelnameinzx=stemodel.getModelnameinzxzip();
		String postfix=modelnameinzx.substring(0,modelnameinzx.length() - ".model".length());
		String classname=stemodel.getClass().getName();
		int p=classname.lastIndexOf(".");
		String packname=classname.substring(0,p);
		
		String zxclassname="Delegate_"+opid+"_"+postfix;
		
		//先在专项文件中找
		if(zxzipfile!=null && zxzipfile.exists()){
			File tempclassfile=null;
			FileInputStream fin=null;
			try{
				tempclassfile=File.createTempFile("temp", ".class");
				ZipHelper.extractFile(zxzipfile, zxclassname+".class", tempclassfile);
				if(tempclassfile.exists() && tempclassfile.length()>10){
					//加载
					byte[] classbyte=new byte[(int)tempclassfile.length()];
					fin=new FileInputStream(tempclassfile);
					fin.read(classbyte);
					ZxClassLoader zxclsloader=new ZxClassLoader();
					Class clazz=zxclsloader.loadClass(packname+"."+zxclassname, classbyte);
					return (ZxstejavaDelegate)clazz.newInstance();
				}
			}catch(Exception e){
				Category logger=Category.getInstance(ZxstejavaDelegate.class);
				logger.error("error",e);
				return null;
			}finally{
				if(fin!=null)
					try {
						fin.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(tempclassfile!=null)tempclassfile.delete();
			}
		}
		return null;
	}
}
