package com.smart.platform.gui.mde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Category;

import com.smart.platform.util.ZipHelper;
import com.smart.platform.util.ZxClassLoader;

public class ZxmdejavaDelegate  {
	public static ZxmdejavaDelegate loadZxfromzxzip(CMdeModel mdemodel){
		File zxzipfile=mdemodel.getZxzipfile();
		String opid=mdemodel.getOpid();
		String postfix="mde";
		String classname=mdemodel.getClass().getName();
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
					return (ZxmdejavaDelegate)clazz.newInstance();
				}
			}catch(Exception e){
				Category logger=Category.getInstance(ZxmdejavaDelegate.class);
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

	/**
	 * 下载细单
	 * @param masterrow 总单行
	 * @return 0 表示由专项处理了，不要再执行原来的下载了
	 */
	public int on_retrieveDetail(CMdeModel mdemodel,int masterrow) {
		return -1;
	}

	/**
	 * 保存前
	 * 
	 * @param mdemodel
	 * @return 非0表示不能保存
	 */
	public int on_beforesave(CMdeModel mdemodel) {

		return 0;
	}


	/**
	 * 保存后
	 * @param mdemodel
	 * @return
	 */
	public void on_save(CMdeModel mdemodel) {
	}


	/**
	 * 窗口关闭前
	 * 
	 * @param mdemodel
	 * @return 非0不能关
	 */
	public int on_beforeclose(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * 查询前
	 * 
	 * @param mdemodel
	 * @return 非0不能查询
	 */
	public int on_beforequery(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * 新增前
	 * 
	 * @param mdemodel
	 * @return 非0不能新增
	 */
	public int on_beforeNew(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * 修改前
	 * 
	 * @param mdemodel
	 * @param row
	 * @return 非0不能存
	 */
	public int on_beforemodify(CMdeModel mdemodel, int row) {

		return 0;
	}

	/**
	 * 新增细单前
	 * @param mdeModel
	 * @param row 总单行
	 * @return 非0不能新增
	 */
	public int on_beforenewdtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 修改细单前
	 * @param mdeModel
	 * @param row 细单行
	 * @return 非0不能修改
	 */
	public int on_beforemodifydtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
	 * 删除细单前
	 * @param mdeModel
	 * @param row 细单行
	 * @return 非0不能删
	 */
	public int on_beforedeldtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 修改总单细
	 * @param mdeModel
	 * @param row 总单行
	 * @return 非0不能改
	 */
	public int on_beforemodifymaster(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 处理命令
	 * @param mdemodel
	 * @param command
	 * @return -1 表示没有处理，继续运行。 返回0表示该命令处理了。
	 */
	public int on_actionPerformed(CMdeModel mdemodel,String command) {
		return -1;
	}
}
