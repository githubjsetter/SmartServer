package com.smart.platform.gui.runop;

import java.io.*;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-8
 * Time: 10:23:00
 * 配置文件
 * <group>
 * groupname
 * <op>
 * id,name,classname
 * </op>
 * </group>
 *
 * <group>
 *     <group>
 *     </group>
 * </group>
 */
public class OpManager {
    public static Opgroup readOps(BufferedReader in) throws Exception{
        Opgroup topgroup=null;
        String line;
        while((line=in.readLine())!=null){
            line=line.trim();
            if(line.startsWith("<group>")){
                topgroup=new Opgroup("");
                readGroup(in,topgroup);
            }else if(line.startsWith("</group>")){
                break;
            }
        }
        return topgroup;
    }

    private static void readGroup(BufferedReader in, Opgroup topgroup) throws Exception{
        topgroup.groupname=in.readLine();

        String line;
        while((line=in.readLine())!=null){
            line=line.trim();
            if(line.startsWith("<group>")){
                Opgroup group = new Opgroup("");
                readGroup(in,group);
                topgroup.addSubgroup(group);
            }else if(line.startsWith("</group>")){
                break;
            }else if(line.startsWith("<op>")){
                Opnode opnode = readOp(in);
                topgroup.addOpnode(opnode);
            }

        }
    }

    private static Opnode readOp(BufferedReader in) throws Exception{
        String line=in.readLine();
        line=line.trim();

        String[] ss = line.split(",");
        String opid="",opname="",classname="";
        if(ss.length>0){
            opid=ss[0];
        }
        if(ss.length>1){
            opname=ss[1];
        }
        if(ss.length>2){
            classname=ss[2];
        }

        Opnode opnode=new Opnode(opid,opname);
        opnode.setClassname(classname);
        return opnode;
    }

    public static void saveOps(Opgroup topgroup,PrintWriter out)throws Exception{
        out.println("<group>");
        out.println(topgroup.groupname);
        //输出组
        Enumeration<Opgroup> en = topgroup.getSubgroups().elements();
        while (en.hasMoreElements()) {
            Opgroup subgroup = en.nextElement();
            saveOps(subgroup,out);
        }

        Enumeration<Opnode> en1 = topgroup.getOpnodes().elements();
        while (en1.hasMoreElements()) {
            Opnode opnode = en1.nextElement();
            out.println("<op>");
            out.println(opnode.opid+","+opnode.opname+","+opnode.classname);
            out.println("</op>");
        }
        out.println("</group>");
        out.flush();
    }

    public static void main(String[] argv){
        File outf=new File("ops.properties");


        try {
            BufferedReader in = new BufferedReader(new FileReader(outf));
            Opgroup topgroup = OpManager.readOps(in);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



/*
        Opgroup topgroup = Opgroup.createDemo();
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outf));
            OpManager.saveOps(topgroup,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
*/
    }
}
