package com.smart.platform.filesync;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 13:55:59
 * �ļ���Ϣ
 */
public class Fileinfo {
    /**
     * ·��.���·��
     */
    String path="";

    /**
     * �ϴθ���ʱ��
     */
    String lastmodifytime="";

    /**
     * ��С
     */
    String filesize="";

    String md5="";


    public Fileinfo(String path) {
        this.path = path;
    }


    public String getPath() {
        return path;
    }

    public String getLastmodifytime() {
        return lastmodifytime;
    }

    public void setLastmodifytime(String lastmodifytime) {
        this.lastmodifytime = lastmodifytime;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }


    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
