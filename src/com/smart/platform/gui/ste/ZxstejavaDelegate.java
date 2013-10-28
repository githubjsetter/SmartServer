package com.smart.platform.gui.ste;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Category;

import com.smart.platform.util.ZipHelper;
import com.smart.platform.util.ZxClassLoader;

/**
 * �ɽ��п��Ƶ��̵�ר�������
 * @author Administrator
 *
 */
public class ZxstejavaDelegate {
	/**
	 * CSteModel�մ����ɹ�
	 * 
	 * @param model
	 */
	public void on_init(CSteModel model) {

	}

	/**
	 * �մ���GUI�ؼ�
	 */
	public void on_initControl() {

	}

	/**
	 * ���Ҽ�
	 * 
	 * @param stemodel
	 * @param row
	 * @param col
	 */
	public void on_rclick(CSteModel stemodel, int row, int col) {

	}

	/**
	 * ˫��
	 * 
	 * @param row
	 * @param col
	 */
	public void on_doubleclick(CSteModel steModel, int row, int col) {

	}

	/**
	 * ɾ����
	 * 
	 * @param steModel
	 * @param row
	 */
	public void on_del(CSteModel steModel, int row) {

	}

	/**
	 * ɾ��ǰ
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_beforedel(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * �޸ĺ�
	 * 
	 * @param steModel
	 * @param row
	 */
	public void on_modify(CSteModel steModel, int row) {
	}

	/**
	 * �޸�ǰ
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_beforemodify(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * ��ѯ����
	 * 
	 * @param steModel
	 */
	public void on_retrieved(CSteModel steModel) {

	}

	/**
	 * ֵ�仯
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
	 * ��ĵ�ǰǰ�仯��
	 * 
	 * @param steModel
	 * @param newrow
	 */
	public void on_tablerowchanged(CSteModel steModel, int newrow) {

	}

	/**
	 * �м��
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_checkrow(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * ���ڹر�ǰ
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforeclose(CSteModel steModel) {

		return 0;
	}

	/**
	 * ����ǰ
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforesave(CSteModel steModel) {

		return 0;
	}

	/**
	 * ��ѯǰ
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforequery(CSteModel steModel) {

		return 0;
	}

	/**
	 * ������
	 * 
	 * @param steModel
	 * @param row
	 * @return
	 */
	public int on_new(CSteModel steModel, int row) {

		return 0;
	}

	/**
	 * ����ǰ
	 * 
	 * @param steModel
	 * @return
	 */
	public int on_beforeNew(CSteModel steModel) {

		return 0;
	}

	/**
	 * ��CSteModelѡ��hov�ɹ�����
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
	 * ���ظ��Ӳ�ѯ����
	 * @return ���ظ��Ӳ�ѯ����
	 */
	public String getOtherWheres() {
		return "";
	}
	
	/**
	 * ��������
	 * @param steModel
	 * @param command
	 * @return -1 ��ʾû�д����������С� ����0��ʾ��������ˡ�
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
		
		//����ר���ļ�����
		if(zxzipfile!=null && zxzipfile.exists()){
			File tempclassfile=null;
			FileInputStream fin=null;
			try{
				tempclassfile=File.createTempFile("temp", ".class");
				ZipHelper.extractFile(zxzipfile, zxclassname+".class", tempclassfile);
				if(tempclassfile.exists() && tempclassfile.length()>10){
					//����
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
