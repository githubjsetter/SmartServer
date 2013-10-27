package com.inca.np.util;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-25
 * Time: 13:45:12
 * To change this template use File | Settings | File Templates.
 */
public class DBHelper {
    public static String addWheres(String inputsql,String wheres){
        String sql="";
        if (wheres.length() > 0) {
            Pattern pat = Pattern.compile("\\bwhere\\b");
            Matcher m = pat.matcher(inputsql.toLowerCase());
            int wherep=-1;
            if(m.find()){
                wherep=m.start();
                wheres=" and "+wheres;
            }else{
                if(wheres.toLowerCase().trim().startsWith("where")){
                    //// ???? 两个where  ，这是个调用错误。
                }else{
                    wheres=" where "+wheres;
                }
            }
            pat = Pattern.compile("\\border\\b");
            m = pat.matcher(inputsql.toLowerCase());
            int orderp=-1;
            if(m.find()){
                orderp=m.start();
            }
            pat = Pattern.compile("\\bgroup\\b");
            m = pat.matcher(inputsql.toLowerCase());
            int groupp=-1;
            if(m.find()){
                groupp=m.start();
            }

            if(groupp>0){
               sql=inputsql.substring(0,groupp)+wheres+" "+inputsql.substring(groupp);
            }else if(orderp>0){
                sql=inputsql.substring(0,orderp)+wheres+" "+inputsql.substring(orderp);
            }else{
                sql=inputsql+" "+wheres;
            }
        }else{
            sql=inputsql;
        }

        return sql;
    }
    
    static SimpleDateFormat dtf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getDatetime(){
    	return dtf.format(new java.util.Date());
    }
    
    public static void executeSql(Connection con,String sql) throws Exception{
    	Statement c1=null;
    	
    	try{
    		c1=con.createStatement();
    		c1.execute(sql);
    	}finally{
    		if(c1!=null){
    			c1.close();
    		}
    	}
    }
    
    /**
     * 将s中的回车换为全角的空格
     * @param s
     * @return
     */
    public static String replaceEnter1(String s){
    	StringBuffer sb=new StringBuffer();
    	for(int i=0;i<s.length();i++){
    		char c=s.charAt(i);
    		if(c==0xd)continue;
    		if(c==0xa){
    			sb.append('　');
    		}else{
    			sb.append(c);
    		}
    	}
    	return sb.toString();
    }


    /**
     * 将全角空格换为回车
     * @param s
     * @return
     */
    public static String replaceEnter2(String s){
    	StringBuffer sb=new StringBuffer();
    	for(int i=0;i<s.length();i++){
    		char c=s.charAt(i);
    		if(c=='　'){
    			sb.append((char)0xa);
    		}else{
    			sb.append(c);
    		}
    	}
    	return sb.toString();
    }
}
