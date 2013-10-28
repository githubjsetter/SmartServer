package com.smart.platform.print;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.peer.DialogPeer;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-10
 * Time: 17:57:36
 * To change this template use File | Settings | File Templates.
 */
public class Fonttest {




    public static void main(String[] argv){
        String msg = "ÀŒÃÂABCD1234";
        Font font =new Font("ÀŒÃÂ" ,Font.PLAIN,12);

        //JDialog
        //JFrame
        //BasicRootPaneUI
        //BasicLookAndFeel, MultiLookAndFeel
        //BasicLookAndFeel
        //UIManager
        //MetalLookAndFeel


        JDialog dlg = new JDialog((Frame)null,false);

        Container cp = dlg.getContentPane();
        JLabel lb = new JLabel(msg);

        lb.setFont(font);
        cp.add(lb);


        //BasicLabelUI

        dlg.pack();
        dlg.setVisible(true);
        


        BufferedImage img=new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        g.setFont(font);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,500,500);
        g.setColor(Color.BLACK);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(msg,40,240);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.drawString(msg,40,260);

        try {
            ImageIO.write(img,"png",new File("rpt.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
