package com.smart.platform.env;

import org.apache.log4j.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-23
 * Time: 16:37:28
 * 文件管理的配置文件.
 */
public class Configer extends HashMap<String,String>{
    File configfile=null;


    public Configer(File configfile) {
        this.configfile = configfile;

        loadConfigfile();

    }

    Category logger=Category.getInstance(Configer.class);

    private void loadConfigfile() {
        BufferedReader rd=null;
        try {
            if(!configfile.exists()){
                return;
            }
            rd = new BufferedReader(new FileReader(configfile));
            String line=null;

            while((line=rd.readLine())!=null){
                int p=line.indexOf("=");
                if(p>0){
                    String name=line.substring(0,p).trim();
                    String value=line.substring(p+1).trim();
                    put(name,value);
                }
            }
        } catch (Exception e) {
            logger.error("read config",e);
        } finally {
            if(rd!=null){
                try {
                    rd.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void saveConfigfile(){
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(configfile));

            LinkedList linkedkey = new LinkedList(keySet());
            Collections.sort(linkedkey);

            Iterator it = linkedkey.iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                String value=get(name);
                out.println(name+"="+value);
            }
        } catch (Exception e) {
            logger.error("writer config");
        } finally {
            if(out!=null){
                out.close();
            }
        }
    }
    
}
