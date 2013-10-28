package com.smart.platform.print;

import sun.awt.windows.WPrinterJob;

import javax.print.*;
import javax.print.attribute.standard.*;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.Attribute;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 9:31:52
 * To change this template use File | Settings | File Templates.
 */
public class PrintHelper {
    public static void lookupService() {
/*
        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream("print.txt");
        } catch (FileNotFoundException ffne) {
        }
        if (psStream == null) {
            return;
        }
*/

//        DocFlavor psInFormat = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        aset.add(new Copies(1));
//        MediaSize A4 = new MediaSize(210, 297, Size2DSyntax.MM, MediaSizeName.ISO_A4);
//        aset.add(A4);
//        aset.add(Sides.DUPLEX);
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (int i = 0; i < services.length; i++) {
            PrintService pservice = services[i];
            System.out.println(pservice.getName());
        }

        if (services.length > 0) {
            PrinterJob job = PrinterJob.getPrinterJob();
            try {
                job.setPrintService(services[0]);
                job.setCopies(1);
                job.setJobName("test print");
                //Doc myDoc = new SimpleDoc(psStream, psInFormat, null);


                TestPrintDoc doc = new TestPrintDoc();
                job.setPageable(doc);
                job.setPrintable(doc);


                //¶Ô»°¿ò
                //job.printDialog(prats);

                HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();
                prats.add(new Copies(1));
                prats.add(OrientationRequested.PORTRAIT);

                //PageFormat curpageformat = job.pageDialog(prats);
                job.printDialog(prats);
                MediaSizeName sizenameattr = (MediaSizeName) prats.get(Media.class);
                MediaSize mediasize = MediaSize.getMediaSizeForName(sizenameattr);
                float[] size = mediasize.getSize(MediaSize.INCH);

                float pw = size[0] * 72;
                float ph = size[1] * 72;
                float px=12;
                float py=12;
                pw = pw - 2 * px;
                ph = ph - 2 * py;

                MediaPrintableArea printarea = new MediaPrintableArea(px/72,py/72,pw/72,ph/72,MediaPrintableArea.INCH);
                prats.add(printarea);





//                job.print(prats);
            } catch (PrinterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

    }


    public static void main(String[] argv) {
        PrintHelper.lookupService();
    }
}
