package com.smart.platform.util;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 14:39:20
 * 总单细目，辅助生成工具
 * 总单表名，视图名
 * 细单表名，视图名
 *
 * package名
 * 输出类名
 * 服务器命令
 */
public class MdeGeneralTool extends JDialog implements ActionListener{
    private JTextField textMasterViewname;
    private JTextField textMasterTablename;
    private JTextField textDetailTablename;
    private JTextField textDetailViewname;
    private JTextField textPackname;
    private JTextField textClassname;
    private JTextField textSvrcommand;
    private JTextField textTitle;
    private JTextField textDetailTitle;
    private JTextField textOpname;
    private JTextField textmcolname;
    private JTextField textdcolname;
    private String opname;

    public MdeGeneralTool() throws HeadlessException {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Container cp = this.getContentPane();
        BoxLayout boxl = new BoxLayout(cp,BoxLayout.Y_AXIS);
        cp.setLayout(boxl);

        JPanel jp = createJP();
        cp.add(jp);
        JLabel lb = new JLabel("功能名(中文）");
        setLabelUI(lb);
        jp.add(lb);
        textOpname = new JTextField("初始化录入保管帐",40);
        jp.add(textOpname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("总单表名(中文）");
        setLabelUI(lb);
        jp.add(lb);
        textTitle = new JTextField("保管帐出入库单",40);
        jp.add(textTitle);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("总单表名");
        setLabelUI(lb);
        jp.add(lb);
        textMasterTablename = new JTextField("bms_st_io_doc",40);
        jp.add(textMasterTablename);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("总单视图名");
        setLabelUI(lb);
        jp.add(lb);
        textMasterViewname = new JTextField("bms_st_io_doc_v",40);
        jp.add(textMasterViewname);


        jp = createJP();
        cp.add(jp);
        lb = new JLabel("总单关联列名");
        setLabelUI(lb);
        jp.add(lb);
        textmcolname = new JTextField("INOUTID",40);
        jp.add(textmcolname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("细单表名(中文）");
        setLabelUI(lb);
        jp.add(lb);
        textDetailTitle = new JTextField("保管帐出入库细单",40);
        jp.add(textDetailTitle);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("细单表名");
        setLabelUI(lb);
        jp.add(lb);
        textDetailTablename = new JTextField("bms_st_io_dtl",40);
        jp.add(textDetailTablename);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("细单视图名");
        setLabelUI(lb);
        jp.add(lb);
        textDetailViewname = new JTextField("bms_st_io_dtl_v",40);
        jp.add(textDetailViewname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("细单关联列名");
        setLabelUI(lb);
        jp.add(lb);
        textdcolname = new JTextField("INOUTID",40);
        jp.add(textdcolname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("输出包名");
        setLabelUI(lb);
        jp.add(lb);
        textPackname = new JTextField("com.inca.st.init",40);
        jp.add(textPackname);


        jp = createJP();
        cp.add(jp);
        lb = new JLabel("输出类名");
        setLabelUI(lb);
        jp.add(lb);
        textClassname = new JTextField("Stio_init",40);
        jp.add(textClassname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("服务器命令");
        setLabelUI(lb);
        jp.add(lb);
        textSvrcommand = new JTextField("保存初始化保管帐",40);
        jp.add(textSvrcommand);


        jp = createJP();
        cp.add(jp);
        JButton btn=new JButton("开始生成");
        setLabelUI(lb);
        btn.addActionListener(this);
        jp.add(btn);
    }

    private JPanel createJP() {
        JPanel jp;
        jp=new JPanel();
        BoxLayout boxlayout = new BoxLayout(jp,BoxLayout.X_AXIS);
        jp.setLayout(boxlayout);
        return jp;
    }

    public static void main(String[] argv){
        MdeGeneralTool dlg=new MdeGeneralTool();
        dlg.pack();
        dlg.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        opname = textOpname.getText();
        String title=textTitle.getText();
        String mastertablename=textMasterTablename.getText();
        String masterviewname=textMasterViewname.getText();
        String mcolname=textmcolname.getText();
        String detailtitle=textDetailTitle.getText();
        String detailtablename=textDetailTablename.getText();
        String detailviewname=textDetailViewname.getText();
        String dcolname=textdcolname.getText();

        String packname=textPackname.getText();
        String classname=textClassname.getText();
        String svrcmd = textSvrcommand.getText();

        File outdir=new File("自动生成");

        String path=outdir.getPath()+"/"+packname.replaceAll("\\.","/");
        outdir=new File(path);
        if(!outdir.exists() && !outdir.mkdirs()){
            errorMessage("错误","建目录失败"+outdir.getPath());
            return;
        }


        Connection con=null;
        try {
            con=getTestCon();
            genMasterModel(con,outdir,title,mastertablename,masterviewname,packname,classname);
            genDetailModel(con,outdir,detailtitle,detailtablename,detailviewname,packname,classname);
            genMdeModel(con,outdir,packname,classname,mcolname,dcolname,svrcmd);
            genFrame(con,outdir,opname,packname,classname);
            genDbprocess(con,outdir,packname,classname,mastertablename,detailtablename);

            infoMessage("生成成功","输出目录:"+outdir.getPath());
        } catch (Exception e1) {
            e1.printStackTrace();
            errorMessage("生成失败",e1.getMessage());
        }

    }

    private void setLabelUI(JComponent comp){
        comp.setPreferredSize(new Dimension(120,27));
    }

    void genDbprocess(Connection con, File outdir, String packname, String classname, String mastertablename,
                      String detailtablename)throws Exception {
        String fullclassname=classname+"_dbprocess";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.println("package "+packname+";");
        out.println("import com.inca.np.server.process.MdeProcessor;\n" +
                "import com.inca.np.gui.mde.CMdeModel;\n"+
                "import com.inca.npx.mde.CMdeModelAp;\n");

        out.println("/*功能\""+opname+"\"应用服务器处理*/");

        out.println("public class "+fullclassname+" extends MdeProcessor{");
        out.println("\tprotected CMdeModel getMdeModel() {");
        out.println("\t\treturn new "+classname+"_mde(null,\"\");");
        out.println("\t}");

        out.println("\tprotected String getMastertablename() {");
        out.println("\t\treturn \""+mastertablename+"\";");
        out.println("\t}");

        out.println("\tprotected String getDetailtablename() {");
        out.println("\t\treturn \""+detailtablename+"\";");
        out.println("\t}");

        out.println("}");

        out.close();
    }

    void genMdeModel(Connection con, File outdir, String packname, String classname, String mcolname,
                     String dcolname, String svrcmd)throws Exception {
        String fullclassname=classname+"_mde";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));

        out.println("package "+packname+";");
        out.println("import com.inca.np.gui.mde.CMdeModel;\n" +
                "import com.inca.np.gui.mde.CMasterModel;\n" +
                "import com.inca.np.gui.mde.CDetailModel;\n" +
                "import com.inca.npx.mde.CMdeModelAp;\n"+
                "import com.inca.np.gui.control.CFrame;");

        out.println();
        out.println("/*功能\""+opname+"\"总单细目Model*/");
        out.println("public class "+fullclassname+" extends CMdeModelAp{");
        out.println("\tpublic "+fullclassname+"(CFrame frame, String title) {");
        out.println("\t\tsuper(frame, title);");
        out.println("\t}");

        out.println("\tprotected CMasterModel createMastermodel() {");
        out.println("\t\treturn new "+classname+"_master(frame,this);");
        out.println("\t}");

        out.println("\tprotected CDetailModel createDetailmodel() {");
        out.println("\t\treturn new "+classname+"_detail(frame,this);");
        out.println("\t}");

        out.println("\tpublic String getMasterRelatecolname() {");
        out.println("\t\treturn \""+mcolname+"\";");
        out.println("\t}");

        out.println("\tpublic String getDetailRelatecolname() {");
        out.println("\t\treturn \""+dcolname+"\";");
        out.println("\t}");

        out.println("\tpublic String getSaveCommandString() {");
        String fullcmd=fullclassname+"."+svrcmd;
        out.println("\t\treturn \""+fullcmd+"\";");
        out.println("\t}");

        out.println("}");
        out.close();
    }

    void genFrame(Connection con, File outdir, String opname,String packname, String classname) throws Exception{
        String fullclassname=classname+"_frame";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));

        out.println("package "+packname+";");
        out.println();
        out.println("import com.inca.np.gui.mde.MdeFrame;\n" +
                "import com.inca.np.gui.mde.CMdeModel;\n" +
                "import java.awt.*;");
        out.println();
        out.println("/*功能\""+opname+"\"总单细目Frame窗口*/");
        out.println("public class "+fullclassname+" extends MdeFrame{");
        out.println("\tpublic "+fullclassname+"() throws HeadlessException {");
        out.println("\t\tsuper(\""+opname+"\");");
        out.println("\t}");

        out.println();
        out.println("\tprotected CMdeModel getMdeModel() {");
        out.println("\t\treturn new "+classname+"_mde(this,\""+opname+"\");");
        out.println("\t}");

        out.println();
        out.println("\tpublic static void main(String[] argv){");
        out.println("\t\t"+fullclassname+" w=new "+fullclassname+"();");
        out.println("\t\tw.pack();");
        out.println("\t\tw.setVisible(true);");
        out.println("\t}");
        out.println("}");

        out.close();

    }

    void genMasterModel(Connection con, File outdir, String title,String mastertablename, String masterviewname,
                        String packname, String classname) throws Exception{
        String fullclassname=classname+"_master";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));

        out.println("package "+packname+";");
        out.println();
        out.println("import com.inca.np.gui.mde.CMasterModel;");
        out.println("import com.inca.np.gui.mde.CMdeModel;");
        out.println("import com.inca.np.gui.control.CFrame;");
        out.println("import com.inca.np.gui.control.DBColumnDisplayInfo;");
        out.println("import java.awt.*;");
        out.println();
        out.println("/*功能\""+opname+"\"总单Model*/");
        out.println("public class "+fullclassname+" extends CMasterModel{");
        out.println("\tpublic "+fullclassname+"(CFrame frame, CMdeModel mdemodel) throws HeadlessException {");
        out.println("\t\tsuper(frame, \""+title+"\", mdemodel);");

        genColumninfo(con,mastertablename,masterviewname,outdir,fullclassname);

        out.println("\t}");

        out.println();
        out.println("\tpublic String getTablename() {");
        out.println("\t\treturn \""+masterviewname+"\";");
        out.println("\t}");


        out.println();
        out.println("\tpublic String getSaveCommandString() {");
        out.println("\t\treturn null;");
        out.println("\t}");

        out.println("}");


        out.close();

    }

    void genDetailModel(Connection con, File outdir, String title,String detailtablename, String detailviewname,
                        String packname, String classname) throws Exception{
        String fullclassname=classname+"_detail";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));

        out.println("package "+packname+";");
        out.println();
        out.println("import com.inca.np.gui.mde.CDetailModel;");
        out.println("import com.inca.np.gui.mde.CMdeModel;");
        out.println("import com.inca.np.gui.control.CFrame;");
        out.println("import com.inca.np.gui.control.DBColumnDisplayInfo;");
        out.println("import java.awt.*;");
        out.println();
        out.println("/*功能\""+opname+"\"细单Model*/");
        out.println("public class "+fullclassname+" extends CDetailModel{");
        out.println("\tpublic "+fullclassname+"(CFrame frame, CMdeModel mdemodel) throws HeadlessException {");
        out.println("\t\tsuper(frame, \""+title+"\", mdemodel);");

        genColumninfo(con,detailtablename,detailviewname,outdir,fullclassname);

        out.println("\t}");

        out.println();
        out.println("\tpublic String getTablename() {");
        out.println("\t\treturn \""+detailviewname+"\";");
        out.println("\t}");


        out.println();
        out.println("\tpublic String getSaveCommandString() {");
        out.println("\t\treturn null;");
        out.println("\t}");

        out.println("}");


        out.close();

    }

    void genColumninfo(Connection con,String tablename,String viewname,File outdir,String fullclassname)throws Exception{
        HashMap tablecolmap=new HashMap();
        String sql="select cname,coltype from col where tname='"+tablename.toUpperCase()+"' " +
                " and cname not like 'ZXCOLUMN%' order by colno";
        PreparedStatement c1 = con.prepareStatement(sql);
        ResultSet rs = c1.executeQuery();
        while(rs.next()){
            String colname=rs.getString("cname").toLowerCase();
            String coltype=rs.getString("coltype").toLowerCase();
            if(coltype.equalsIgnoreCase("varchar2")){
                coltype="varchar";
            }

            DBColumnDisplayInfo dispinfo = new DBColumnDisplayInfo(colname,coltype,colname);
            tablecolmap.put(colname,dispinfo);
        }
        c1.close();

        Vector<DBColumnDisplayInfo> viewcols=new Vector<DBColumnDisplayInfo>();
        sql="select cname,coltype from col where tname='"+viewname.toUpperCase()+"' " +
                " and cname not like 'ZXCOLUMN%' order by colno";
        PreparedStatement c2 = con.prepareStatement(sql);
        rs = c2.executeQuery();
        int i=0;
        while(rs.next()){
            i++;
            String colname=rs.getString("cname").toLowerCase();
            String coltype=rs.getString("coltype").toLowerCase();
            if(coltype.equalsIgnoreCase("varchar2")){
                coltype="varchar";
            }

            boolean linebreak=i%3==0;
            String title=SteGeneralTool.getColumnnameCN(con,viewname,colname);
            if(title==null || title.length()==0){
                title=colname;
            }
            DBColumnDisplayInfo dispinfo = new DBColumnDisplayInfo(colname,coltype,title,linebreak);
            viewcols.add(dispinfo);

            if(tablecolmap.get(colname)==null){
                //视图有，但表没有
                dispinfo.setReadonly(true);
                dispinfo.setFocusable(false);
                dispinfo.setUpdateable(false);
            }
        }
        c2.close();

        if(viewcols.size()==0){
            throw new Exception("视图名未输入，或\""+viewname+"\"不存在。提示：视图名可以和表名一致");
        }
        if(tablecolmap.size()==0){
            throw new Exception("表名未输入，或\""+tablename+"\"不存在。");
        }


        //写到文件
        File f=new File(outdir,fullclassname+".model");
        DBColumnInfoStoreHelp.writeFile(viewcols,f);

    }


    protected void infoMessage(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg,
                title, JOptionPane.INFORMATION_MESSAGE);

    }

    protected void errorMessage(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg,
                title, JOptionPane.ERROR_MESSAGE);

    }


    String dbip=DefaultNPParam.debugdbip;
    String dbname=DefaultNPParam.debugdbsid;
    String dbuser=DefaultNPParam.debugdbusrname;
    String dbpass=DefaultNPParam.debugdbpasswd;

    private Connection getTestCon() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@"+dbip+":1521:"+dbname;

        Connection con = DriverManager.getConnection(url, dbuser, dbpass);
        con.setAutoCommit(false);
        return con;

    }
}
