package com.inca.np.print.drawable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-25
 * Time: 13:25:43
 * To change this template use File | Settings | File Templates.
 */
public class PDrawcellBase {
    protected Font font = new Font("宋体", Font.PLAIN, 9);
    protected static final int ALIGN_LEFT = JLabel.LEFT;
    protected static final int ALIGN_CENTER = JLabel.CENTER;
    protected static final int ALIGN_RIGHT = JLabel.RIGHT;
    protected int x, y;
    protected int width = 20;
    protected int height = 20;

    protected int align = ALIGN_LEFT;

    protected boolean visible=true;


    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void drawCell(Graphics2D g2, String value, int x, int y, int width, int height, int align) {
        //检查是否超宽了
        Graphics2D tmpg = (Graphics2D) g2.create(x, y, width, height);

        int tmpy = 0;
        while (value.length() > 0) {
            FontMetrics fm = tmpg.getFontMetrics();
            Rectangle2D rect = fm.getStringBounds(value, tmpg);
            if (rect.getWidth() > width) {
                //需要进行分解
                int charct = 1;
                for (charct = 1; charct < value.length(); charct++) {
                    Rectangle2D bounds = fm.getStringBounds(value.substring(0, charct), tmpg);
                    if (bounds.getWidth() > width) {
                        charct--;
                        break;
                    }
                }
                if (charct == 0) charct = 1;

                String subs = value.substring(0, charct);
                tmpg.drawString(subs, 0, tmpy + (float) rect.getHeight());
                value = value.substring(charct);

                tmpy += rect.getHeight();

            } else {
                if (align == ALIGN_LEFT) {
                    tmpg.drawString(value, 0, tmpy + (int) rect.getHeight());
                } else if (align == ALIGN_CENTER) {
                    //居中
                    int newx = (int) ((width - rect.getWidth()) / 2 + 0.5);
                    tmpg.drawString(value, newx, tmpy + (int) rect.getHeight());
                } else {
                    int newx = (int) (width - rect.getWidth() + 0.5);
                    tmpg.drawString(value, newx, tmpy + (int) rect.getHeight());
                }
                break;
            }
        }
    }

    /**
     * 输出到文件
     *
     * @param out
     */
    public void writeReport(PrintWriter out) {
        out.println("<cell>");
        out.println("x=" + x);
        out.println("y=" + y);
        out.println("width=" + width);
        out.println("height=" + height);
        out.println("align=" + align);
        out.println("visible=" + (visible?"true":"false"));
        out.println("font.name=" + font.getName());
        out.println("font.style=" + font.getStyle());
        out.println("font.size=" + font.getSize());
        writeOther(out);
        out.println("</cell>");
    }

    /**
     * 继承类重载
     *
     * @param out
     */
    protected void writeOther(PrintWriter out) {
    }


    /**
     * 读入
     *
     * @param reader
     */
    public void read(BufferedReader reader) throws Exception {
        String line;
        String fontname = "";
        int fontsize = 9;
        int fontstyle = 1;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("</cell")) {
                break;
            }

            if (line.startsWith("x=")) {
                x = Integer.parseInt(line.substring("x=".length()));
            } else if (line.startsWith("y=")) {
                y = Integer.parseInt(line.substring("y=".length()));
            } else if (line.startsWith("width=")) {
                width = Integer.parseInt(line.substring("width=".length()));
            } else if (line.startsWith("height=")) {
                height = Integer.parseInt(line.substring("height=".length()));
            } else if (line.startsWith("align=")) {
                align = Integer.parseInt(line.substring("align=".length()));
            } else if (line.startsWith("visible=")) {
                visible = line.substring("visible=".length()).equals("true");
            } else if (line.startsWith("font.name=")) {
                fontname = line.substring("font.name=".length());
            } else if (line.startsWith("font.style=")) {
                fontstyle = Integer.parseInt(line.substring("font.style=".length()));
            } else if (line.startsWith("font.size=")) {
                fontsize = Integer.parseInt(line.substring("font.size=".length()));
                readOther(reader);
            }

        }
        Font font = new Font(fontname, fontstyle, fontsize);
        this.setFont(font);
    }

    protected void readOther(BufferedReader in) throws Exception {
    }

}
