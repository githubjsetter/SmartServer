package com.inca.np.util;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.DBColumnInfoStoreHelp;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 17:04:43
 * To change this template use File | Settings | File Templates.
 */
public class SteGeneralTool extends JDialog implements ActionListener{
    private JTextField textMasterViewname;
    private JTextField textMasterTablename;
    private JTextField textPackname;
    private JTextField textClassname;
    private JTextField textSvrcommand;
    private JTextField textTitle;
    private JTextField textOpname;
    private String opname;

    public SteGeneralTool() throws HeadlessException {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Container cp = this.getContentPane();
        BoxLayout boxl = new BoxLayout(cp,BoxLayout.Y_AXIS);
        cp.setLayout(boxl);

        JPanel jp = createJP();
        cp.add(jp);
        JLabel lb = new JLabel("功能名(中文）");
        setLabelUI(lb);
        jp.add(lb);
        textOpname = new JTextField("货品管理",40);
        jp.add(textOpname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("表名(中文）");
        setLabelUI(lb);
        jp.add(lb);
        textTitle = new JTextField("货品",40);
        jp.add(textTitle);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("表名");
        setLabelUI(lb);
        jp.add(lb);
        textMasterTablename = new JTextField("pub_goods",40);
        jp.add(textMasterTablename);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("视图名");
        setLabelUI(lb);
        jp.add(lb);
        textMasterViewname = new JTextField("pub_goods_v",40);
        jp.add(textMasterViewname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("输出包名");
        setLabelUI(lb);
        jp.add(lb);
        textPackname = new JTextField("com.inca.test",40);
        jp.add(textPackname);


        jp = createJP();
        cp.add(jp);
        lb = new JLabel("输出类名");
        setLabelUI(lb);
        jp.add(lb);
        textClassname = new JTextField("Pub_goods",40);
        jp.add(textClassname);

        jp = createJP();
        cp.add(jp);
        lb = new JLabel("服务器命令");
        setLabelUI(lb);
        jp.add(lb);
        textSvrcommand = new JTextField("保存货品",40);
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


    public void actionPerformed(ActionEvent e) {
        opname = textOpname.getText();
        String title=textTitle.getText();
        String mastertablename=textMasterTablename.getText();
        String masterviewname=textMasterViewname.getText();

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
            genMasterModel(con,outdir,title,mastertablename,masterviewname,packname,classname,svrcmd);
            genFrame(con,outdir,opname,packname,classname);
            genDbprocess(con,outdir,packname,classname,mastertablename);

            infoMessage("生成成功","输出目录:"+outdir.getPath());
        } catch (Exception e1) {
            e1.printStackTrace();
            errorMessage("生成失败",e1.getMessage());
        }

    }

    private void setLabelUI(JComponent comp){
        comp.setPreferredSize(new Dimension(120,27));
    }

    void genDbprocess(Connection con, File outdir, String packname, String classname, String mastertablename)throws Exception {
        String fullclassname=classname+"_dbprocess";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.println("package "+packname+";");
        out.println("import com.inca.np.server.process.SteProcessor;\n" +
                "import com.inca.np.gui.ste.CSteModel;");

        out.println("/*功能\""+opname+"\"应用服务器处理*/");

        out.println("public class "+fullclassname+" extends SteProcessor{");
        out.println("\tprotected CSteModel getSteModel() {");
        out.println("\t\treturn new "+classname+"_ste(null);");
        out.println("\t}");

        out.println("\tprotected String getTablename() {");
        out.println("\t\treturn \""+mastertablename+"\";");
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
        out.println("import com.inca.np.gui.ste.*;\n" +
                "import java.awt.*;");
        out.println();
        out.println("/*功能\""+opname+"\"Frame窗口*/");
        out.println("public class "+fullclassname+" extends Steframe{");
        out.println("\tpublic "+fullclassname+"() throws HeadlessException {");
        out.println("\t\tsuper(\""+opname+"\");");
        out.println("\t}");

        out.println();
        out.println("\tprotected CSteModel getStemodel() {");
        out.println("\t\treturn new "+classname+"_ste(this);");
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
                        String packname, String classname,String svrcmd) throws Exception{
        String fullclassname=classname+"_ste";
        File f=new File(outdir,fullclassname+".java");
        PrintWriter out = new PrintWriter(new FileWriter(f));

        out.println("package "+packname+";");
        out.println();
        out.println("import com.inca.np.gui.ste.CSteModel;\n" +
                "import com.inca.npx.ste.CSteModelAp;\n" +
                "import com.inca.np.gui.control.CFrame;\n" +
                "import com.inca.np.gui.control.DBColumnDisplayInfo;\n" +
                "import java.awt.*;");
        out.println();
        out.println("/*功能\""+opname+"\"单表编辑Model*/");
        out.println("public class "+fullclassname+" extends CSteModelAp{");
        out.println("\tpublic "+fullclassname+"(CFrame frame) throws HeadlessException {");
        out.println("\t\tsuper(frame, \""+title+"\");");
        genColumninfo(con,mastertablename,masterviewname,outdir,fullclassname);

        out.println("\t}");

        out.println();
        out.println("\tpublic String getTablename() {");
        out.println("\t\treturn \""+masterviewname+"\";");
        out.println("\t}");


        out.println();
        out.println("\tpublic String getSaveCommandString() {");
        String fullcmd=packname+"."+fullclassname+"."+svrcmd;
        out.println("\t\treturn \""+fullcmd+"\";");
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
            String title=getColumnnameCN(con,viewname,colname);
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

    public static String getColumnnameCN(Connection con,String table,String colname){
        String sql="select cntitle from sys_column_cn where upper(tablename)=upper(?) and " +
                " upper(colname)=upper(?)";

        PreparedStatement c1=null;

        try {
            c1 = con.prepareStatement(sql);
            c1.setString(1,table);
            c1.setString(2,colname);
            ResultSet rs = c1.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(c1!=null){
                try {
                    c1.close();
                } catch (SQLException e) {}

            }
        }

    }


    public static void main(String[] argv){
        SteGeneralTool dlg=new SteGeneralTool();
        dlg.pack();
        dlg.setVisible(true);
    }
}
