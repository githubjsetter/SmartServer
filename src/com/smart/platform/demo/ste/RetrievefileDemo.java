package com.smart.platform.demo.ste;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.RecordFileDownloader;
import com.smart.platform.gui.ste.RecordfileUploader;

public class RetrievefileDemo {

	/**
	 * �г��ļ��嵥
	 * 
	 * @param filegroupid
	 * @throws Exception
	 */
	public void browerFile(String filegroupid) throws Exception {
		RecordfileUploader rfu = new RecordfileUploader();
		DBTableModel filedbmodel = rfu.browserFilegroup(filegroupid);

		for (int row = 0; row < filedbmodel.getRowCount(); row++) {
			String filename = filedbmodel.getItemValue(row, "filename");
			String filesize = filedbmodel.getItemValue(row, "filesize");
			String modifydate = filedbmodel.getItemValue(row, "modifydate");
		}
	}

	public boolean downloadFile(String filegroupid, String filename, File outf)
			throws Exception {
		RecordFileDownloader rfd = new RecordFileDownloader();
		if (!rfd.downloadFile(filegroupid, filename, outf)) {

			return false;
		}
		return true;

	}

	public void downloadFile(String filegroupid) throws Exception {
		RecordfileUploader rfu = new RecordfileUploader();
		DBTableModel filedbmodel = rfu.browserFilegroup(filegroupid);

		for (int row = 0; row < filedbmodel.getRowCount(); row++) {
			String filename = filedbmodel.getItemValue(row, "filename");
			String filesize = filedbmodel.getItemValue(row, "filesize");
			String modifydate = filedbmodel.getItemValue(row, "modifydate");

			// �ҵ�ͼƬ�ļ�
			String tmps = filename.toLowerCase();
			if (tmps.endsWith(".png") || tmps.endsWith(".jpg")
					|| tmps.endsWith(".jpeg") || tmps.endsWith(".gif")) {
				File dir = new File("���ص��ļ�");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				
				//�����ļ�
				File outf = new File(dir, filename);
				downloadFile(filegroupid, filename, outf);

				//��ȡͼƬ�ļ�,תΪBufferedImage
				BufferedImage img = ImageIO.read(outf);
				// �����豸��.����б�Ҫ,��AffineTransformOp��ͼƬ��С
				// g2.drawImage(img..........)

			}

		}

	}

	public static void main(String[] args) {
		// ��dbtablemodel��ȡ��filegropuid
		// filegroupid = dbmodel.getItemValue(row,"filegroupid");
		String filegroupid = "84";
		RetrievefileDemo demo = new RetrievefileDemo();
		try {
			demo.downloadFile(filegroupid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
