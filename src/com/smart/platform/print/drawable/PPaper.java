package com.smart.platform.print.drawable;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaPrintableArea;
import java.awt.print.Paper;
import java.awt.print.PageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-10
 * Time: 18:55:16
 * To change this template use File | Settings | File Templates.
 */
public class PPaper extends Paper{
    public PPaper(PageFormat pf) {
        float x = (float)pf.getWidth();
        float y = (float)pf.getHeight();

        boolean m_landscape = pf.getOrientation() != PageFormat.PORTRAIT;
        MediaSizeName msn = MediaSize.findMedia (x/72, y/72, MediaSize.INCH);

        MediaSize ms = null;
        if (msn == null)
            msn = MediaSize.findMedia (y/72, x/72, MediaSize.INCH);	//	flip it
        if (msn != null)
            ms = MediaSize.getMediaSizeForName(msn);
        setMediaSize(ms, m_landscape);
        //	set size directly
        setSize(pf.getWidth(), pf.getHeight());
        setImageableArea(pf.getImageableX(), pf.getImageableY(),
            pf.getImageableWidth(), pf.getImageableHeight());


    }

    MediaSize m_mediaSize;
    boolean m_landscape;

    public void setMediaSize (MediaSize mediaSize, boolean landscape)
    {
        if (mediaSize == null)
            throw new IllegalArgumentException("MediaSize is null");
        m_mediaSize = mediaSize;
        m_landscape = landscape;

        //	Get Sise in Inch * 72
        double width = m_mediaSize.getX (MediaSize.INCH) * 72;
        double height = m_mediaSize.getY (MediaSize.INCH) * 72;
        //	Set Size
        setSize (width, height);
    }	//	setMediaSize

    public MediaPrintableArea getMediaPrintableArea()
    {
        MediaPrintableArea area = new MediaPrintableArea ((float)getImageableX()/72, (float)getImageableY()/72,
            (float)getImageableWidth()/72, (float)getImageableHeight()/72, MediaPrintableArea.INCH);
    //	log.fine( "CPaper.getMediaPrintableArea", area.toString(MediaPrintableArea.INCH, "\""));
        return area;
    }	//	getMediaPrintableArea


    public boolean isLandscape()
    {
        return m_landscape;
    }	//	isLandscape
}
