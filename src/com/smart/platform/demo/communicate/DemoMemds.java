package com.smart.platform.demo.communicate;

import com.smart.platform.communicate.CommandFactory;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.SqlCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.CommandServer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-1
 * Time: 9:42:02
 * To change this template use File | Settings | File Templates.
 */
public class DemoMemds extends JFrame implements ActionListener{
    private JButton buttonrun;
    private JTextArea textSql;
    private JTextArea textResult;
    private JTextField textspeed;

    Category logger=Category.getRoot();

    public static void main(String[] argv){
        DemoMemds frame=new DemoMemds();
        frame.init();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }

    public void init(){
        Container contentpane = getContentPane();

        GridBagLayout gridbaglayer = new GridBagLayout();
        contentpane.setLayout(gridbaglayer);
        GridBagConstraints gc = new GridBagConstraints();



        JLabel lb;
        lb=new JLabel("������select sql");
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(lb,gc);
        contentpane.add(lb);


        JTextArea textSql=new JTextArea(3,60);
        textSql.setText("select * from tab");
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(textSql,gc);
        this.textSql = textSql;
        contentpane.add(this.textSql);



        lb=new JLabel("��ѯ���");
        gridbaglayer.setConstraints(lb,gc);
        contentpane.add(lb);


        JTextArea textResult=new JTextArea(10,60);
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(textResult,gc);
        this.textResult = textResult;
        contentpane.add(this.textResult);

        //����
        JPanel toolbar = buildToolbar();
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(toolbar,gc);
        contentpane.add(toolbar);

    }

    JPanel buildToolbar(){

        GridBagLayout gridbaglayer = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();

        JPanel toolbar =new JPanel();
        toolbar.setLayout(gridbaglayer);


        JLabel lb=new JLabel("����(k)");
        gc.gridwidth=GridBagConstraints.RELATIVE;
        gc.weightx=1;
        gridbaglayer.setConstraints(lb,gc);
        toolbar.add(lb);



        JTextField textspeed=new JTextField(5);
        textspeed.setText("56");
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(textspeed,gc);
        this.textspeed = textspeed;
        toolbar.add(this.textspeed);


        buttonrun = new JButton("ִ��sql");
        buttonrun.addActionListener(this);
        gc.gridwidth=GridBagConstraints.REMAINDER;
        gridbaglayer.setConstraints(buttonrun,gc);
        toolbar.add(buttonrun);

        return toolbar;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==buttonrun){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            String sql=textSql.getText();
            if(!sql.trim().toLowerCase().startsWith("select")){
                errorMessage("sql���Ӧ��select��ʼ");
                return;
            }
            try {
                int speed;
                try {
                    speed=Integer.parseInt(textspeed.getText());
                } catch (Exception e1) {
                    speed=0;
                }
                doSendsql(sql,speed);
            } catch (Exception e1) {
                logger.error("run sql",e1);
                errorMessage(e1.getMessage());
            }
            finally{
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        }
    }

    void errorMessage(String msg){
        JOptionPane.showConfirmDialog(this,msg,"����",JOptionPane.ERROR_MESSAGE);
    }

    void clearResultmsg(){
        textResult.setText("");
    }

    void appendResult(String s){
        textResult.append(s);
    }

    CommandServer svr=new CommandServer();
    /**
     * ����
     * @param sql
     * @param maxspeed ����ٶ�k
     * @throws Exception
     */
    void doSendsql(String sql,int maxspeed)throws Exception{
        clearResultmsg();

        ByteArrayOutputStream bclientout = new ByteArrayOutputStream();
        SqlCommand sqlcmd=new SqlCommand(sql);
        sqlcmd.write(bclientout);

        ByteArrayInputStream bserverin = new ByteArrayInputStream(bclientout.toByteArray());
        ByteArrayOutputStream bserverout = new ByteArrayOutputStream();


        long t1=System.currentTimeMillis();
        svr.process(bserverin,bserverout);
        long t2=System.currentTimeMillis();


        byte[] data = bserverout.toByteArray();

        //��ʱ
        if(maxspeed>0) {
            int ms = data.length * 1000 / (maxspeed * 1024);

            this.appendResult("������Ҫʱ��"+ms+"����\r\n");

            Thread.sleep(ms);
        }


        ByteArrayInputStream bclientin = new ByteArrayInputStream(data);
        InflaterInputStream zipin = new InflaterInputStream(bclientin);
        ByteArrayOutputStream inflatout = new ByteArrayOutputStream();
        int buflen=10240;
        byte buf[]=new byte[buflen];
        int rd=0;
        while((rd = zipin.read(buf))>=0){
            inflatout.write(buf,0,rd);
        }
        int  decodedatalen=inflatout.toByteArray().length;


        bclientin = new ByteArrayInputStream(data);
        zipin = new InflaterInputStream(bclientin);
        DataCommand datacmd = (DataCommand) CommandFactory.readCommand(zipin);

        DBTableModel dbmodel = datacmd.getDbmodel();


        StringBuffer sb=new StringBuffer();
        sb.append("��ʱ:"+(t2-t1)+"ms,��������"+data.length+"�ֽ�,��ѹ��"+decodedatalen+"�ֽ�\r\n");
        sb.append("�յ���¼"+dbmodel.getRowCount()+"��\r\n");
        appendResult(sb.toString());


    }
}
