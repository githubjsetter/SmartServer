package com.inca.np.image;

import org.apache.log4j.Category;

import javax.swing.*;
import javax.imageio.ImageIO;

import com.inca.np.gui.control.*;

import java.io.InputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-29
 * Time: 10:45:21
 * To change this template use File | Settings | File Templates.
 */
public class CIcon extends ImageIcon{
    Category logger = Category.getInstance(CIcon.class);


    //public static final CIcon insertcol=new CIcon("insertcol.gif");

    protected CIcon(String filename) {
        InputStream in = this.getClass().getResourceAsStream(filename);
        if(in==null){
            return;
        }
        try {
            BufferedImage image = ImageIO.read(in);
            super.setImage(image);
        } catch (IOException e) {
            logger.error("load icon",e);
            return;
        } finally{
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] argv){
        CStetoolbar stetb=new CStetoolbar(null);
        CIcon icon=new CIcon("undo.gif");
    }
}

