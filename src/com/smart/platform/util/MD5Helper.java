package com.smart.platform.util;

import org.apache.log4j.Category;

import java.security.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-7
 * Time: 10:27:14
 * To change this template use File | Settings | File Templates.
 */
public class MD5Helper {


    public static String MD5(String s) {
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();

            StringBuffer sb = new StringBuffer(md.length * 2);
            for (int i = 0; i < md.length; i++) {
                int b = md[i];
                if (b < 0) b += 256;
                if (b < 16) sb.append("0");
                sb.append(Integer.toHexString(b));
            }

            return new String(sb.toString());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String MD5(ByteBuffer s) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(s);
            byte[] md = mdTemp.digest();

            StringBuffer sb = new StringBuffer(md.length * 2);
            for (int i = 0; i < md.length; i++) {
                int b = md[i];
                if (b < 0) b += 256;
                if (b < 16) sb.append("0");
                sb.append(Integer.toHexString(b));
            }

            return new String(sb.toString());
        }
        catch (Exception e) {
            return null;
        }
    }

    static Category logger=Category.getInstance(MD5Helper.class);

    public static String MD5(File f){
        FileInputStream in=null;
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            in = new FileInputStream(f);
            int buflen=102400;
            byte[] buf=new byte[buflen];
            int rd;
            while((rd=in.read(buf))>0){
                mdTemp.update(buf,0,rd);
            }
            byte[] md = mdTemp.digest();

            StringBuffer sb = new StringBuffer(md.length * 2);
            for (int i = 0; i < md.length; i++) {
                int b = md[i];
                if (b < 0) b += 256;
                if (b < 16) sb.append("0");
                sb.append(Integer.toHexString(b));
            }

            return new String(sb.toString());
            
        } catch (Exception e) {
            logger.error("error",e);
            return "";
        } finally {

            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

        }
    	
    }
    
/*    public static String MD5(File f) {
        MappedByteBuffer bytebuffer = null;
        FileChannel fchannel=null;
        FileInputStream in=null;
        try {
            in = new FileInputStream(f);
            fchannel = in.getChannel();
            bytebuffer = fchannel.map(FileChannel.MapMode.READ_ONLY, 0, fchannel.size());
            return MD5(bytebuffer);
        } catch (Exception e) {
            logger.error("error",e);
            return "";
        } finally {
            if(fchannel!=null){
                try {
                    fchannel.close();
                } catch (IOException e) {
                }
            }

            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

            //让内存管理器好收回内存.
            bytebuffer=null;
        }
    }
*/    
    

    public static void main(String[] args) {
        //System.out.println(MD5Helper.MD5("abcdefghijklmnopqrstuvwxyz"));
        File f=new File("md5.txt");
        String md5=MD5(f);
        System.out.println(md5);
        String md51=MD5(f);
        
        System.out.println(md51);
    }
}

