package com.inca.np.gui.runop;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-30
 * Time: 15:33:50
 * ��ʾϵͳ������Ϣ�Ĵ���
 *
 * �в�Ϊϵͳ������Ϣ
 *
 * �²�Ϊ����email.
 */
public class RunlogPanel extends JPanel {

    JTextArea textInfo=new JTextArea(60,30);
    private JTextField textEmail;

    public RunlogPanel() {
        this.setLayout(new BorderLayout());
        JScrollPane scrollp=new JScrollPane(textInfo);
        this.add(scrollp,BorderLayout.CENTER);
        textInfo.setEditable(false);

        Font font=new Font("����",Font.PLAIN,12);
        textInfo.setFont(font);


        JPanel bottompanel=createBottompanel();
        this.add(bottompanel,BorderLayout.SOUTH);
    }

    JPanel createBottompanel(){
        JPanel jp=new JPanel();
        JLabel lbemail=new JLabel("EMAIL��ַ:");
        jp.add(lbemail);

        textEmail = new JTextField(30);
        textEmail.setText("ngpcs@inca.com.cn");
        jp.add(textEmail);

        JButton btnSend=new JButton("����email");
        jp.add(btnSend);


        return jp;
    }

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void runMessage(Runmessage msg){

        int l = textInfo.getText().length();
        textInfo.setSelectionStart(l);
        textInfo.setSelectionEnd(l);

        textInfo.replaceSelection(df.format(new Date()));

        if(msg.getMsgtype().equals(Runmessage.MESSAGE_INFO)){
            textInfo.setSelectionColor(Color.BLACK);
        }else{
            textInfo.setSelectionColor(Color.RED);
        }

        StringBuffer sb=new StringBuffer();
        sb.append("   ");
        sb.append(msg.getMsg());
        sb.append("\r\n");

        l = textInfo.getText().length();
        textInfo.setSelectionStart(l);
        textInfo.setSelectionEnd(l);
        textInfo.replaceSelection(sb.toString());

    }
}
