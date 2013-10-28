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
	 * ����ϸ��
	 * @param masterrow �ܵ���
	 * @return 0 ��ʾ��ר����ˣ���Ҫ��ִ��ԭ����������
	 */
	public int on_retrieveDetail(CMdeModel mdemodel,int masterrow) {
		return -1;
	}

	/**
	 * ����ǰ
	 * 
	 * @param mdemodel
	 * @return ��0��ʾ���ܱ���
	 */
	public int on_beforesave(CMdeModel mdemodel) {

		return 0;
	}


	/**
	 * �����
	 * @param mdemodel
	 * @return
	 */
	public void on_save(CMdeModel mdemodel) {
	}


	/**
	 * ���ڹر�ǰ
	 * 
	 * @param mdemodel
	 * @return ��0���ܹ�
	 */
	public int on_beforeclose(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * ��ѯǰ
	 * 
	 * @param mdemodel
	 * @return ��0���ܲ�ѯ
	 */
	public int on_beforequery(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * ����ǰ
	 * 
	 * @param mdemodel
	 * @return ��0��������
	 */
	public int on_beforeNew(CMdeModel mdemodel) {

		return 0;
	}

	/**
	 * �޸�ǰ
	 * 
	 * @param mdemodel
	 * @param row
	 * @return ��0���ܴ�
	 */
	public int on_beforemodify(CMdeModel mdemodel, int row) {

		return 0;
	}

	/**
	 * ����ϸ��ǰ
	 * @param mdeModel
	 * @param row �ܵ���
	 * @return ��0��������
	 */
	public int on_beforenewdtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * �޸�ϸ��ǰ
	 * @param mdeModel
	 * @param row ϸ����
	 * @return ��0�����޸�
	 */
	public int on_beforemodifydtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
	 * ɾ��ϸ��ǰ
	 * @param mdeModel
	 * @param row ϸ����
	 * @return ��0����ɾ
	 */
	public int on_beforedeldtl(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * �޸��ܵ�ϸ
	 * @param mdeModel
	 * @param row �ܵ���
	 * @return ��0���ܸ�
	 */
	public int on_beforemodifymaster(CMdeModel mdeModel, int row) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * ��������
	 * @param mdemodel
	 * @param command
	 * @return -1 ��ʾû�д����������С� ����0��ʾ��������ˡ�
	 */
	public int on_actionPerformed(CMdeModel mdemodel,String command) {
		return -1;
	}
}
