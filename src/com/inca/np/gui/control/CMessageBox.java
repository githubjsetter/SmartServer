package com.inca.np.gui.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-10
 * Time: 13:36:37
 * 消息窗口
 */
public class CMessageBox extends CDialog{
    private JButton buttonok;


    public CMessageBox(Frame parent,String title,String message) throws HeadlessException {
        super(parent,title,true);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        JPanel centerpane=new JPanel();
        BoxLayout box = new BoxLayout(centerpane,BoxLayout.X_AXIS);
        centerpane.setLayout(box);

        JLabel lbmsg = new JLabel(message);
        centerpane.add(lbmsg);
        cp.add(centerpane,BorderLayout.CENTER);

        buttonok = new JButton("确定");
        buttonok.addActionListener(new DlgActionListener());
        cp.add(buttonok,BorderLayout.SOUTH);

        cp.setPreferredSize(new Dimension(300,60));
        center();
    }

    class DlgActionListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    public static void infoMessage(Frame parent,String title,String msg){
        final CMessageBox dlg = new CMessageBox(parent,title,msg);
        dlg.pack();
        Runnable run = new Runnable(){
            public void run(){
                dlg.buttonok.requestFocus();
            }
        };
        SwingUtilities.invokeLater(run);
        dlg.setVisible(true);
    }


    void center(){
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension size = this.getPreferredSize();

        double x=  (screensize.getWidth() - size.getWidth())/2.0;
        double y = (screensize.getHeight() - size.getHeight()) / 2.0;

        this.setLocation((int)x,(int)y);

    }

}
