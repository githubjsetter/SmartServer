package com.smart.platform.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import com.l2fprod.util.OS;
import com.smart.platform.client.PingThread;
import com.smart.platform.env.Configer;
import com.smart.platform.filesync.UpdateFrame;
import com.smart.platform.gui.runop.Opgroup;
import com.smart.platform.gui.ste.Hovdesc;
import com.smart.server.pushplat.client.BackgroundRunner;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 11:31:51
 * ��ƽ̨ȱʡ�Ĳ�������ʼ��ʱ���á�
 */
public class DefaultNPParam {
    public static int debug = 1;
    public static String npversion = "5.5.02";
    
    /**
     * ����״̬����ʾ�뿪���йص��ϴ��� ������ƵȲ˵�
     */
    public static int develop = 0;

    public static String defaultappsvrurl = "http://218.247.157.239/np/clientrequest.do";

/*    public static String debugdbip = "192.9.200.1";
    public static String debugdbsid = "data";
    public static String debugdbusrname = "xjxty";
    public static String debugdbpasswd = "xjxty";
*/
    
    public static String debugdbip = "192.9.200.89";
    public static String debugdbsid = "pb";
    public static String debugdbusrname = "nbms";
    public static String debugdbpasswd = "nbms";

    public static JDialog logindlg=null;
    /**
     * �Ƿ��Ƿ���������.
     */
    public static boolean isservletapp=false;

    public static Vector<Hovdesc> hovdescs = new Vector<Hovdesc>();


    /**
     * ���һ��ping�õ���������ʱ��
     */
    public static long lastsvrtime=0;

    /**
     * ���һ���յ���������Ӧʱ��
     */
    public static long lastrecvsvrresptime=0;

    /**
     * һ��ping��Ҫ��ms��
     */
    public static long pingtime=0;

    /**
     * �Ƿ�����
     */
    public static boolean online=false;

/*
    public static UpdateFrame updateframe=null;
    public static void doUpdate(){
        if(updateframe==null){
            updateframe=new UpdateFrame();
            updateframe.pack();
        }

        if(!updateframe.isVisible()){
            updateframe.setVisible(true);
        }
    }
*/
    static {
/*        hovdescs.add(new Pub_company_hov());
        hovdescs.add(new Pub_goods_hov());
        hovdescs.add(new Depthov());
        hovdescs.add(new Op_hov());
        hovdescs.add(new Employee_hov());
        hovdescs.add(new Role_hov());
*/        //hovdescs.add(new SelecttableHov());
    }

    public static Hovdesc getHovbyDescname(String descname) {
        Enumeration<Hovdesc> en = hovdescs.elements();
        while (en.hasMoreElements()) {
            Hovdesc desc = en.nextElement();
            if (desc.getDesc().equals(descname)) {
                return desc;
            }
        }
        return null;
    }

    public static Hovdesc getHovbyClassname(String classname) {
        Enumeration<Hovdesc> en = hovdescs.elements();
        while (en.hasMoreElements()) {
            Hovdesc desc = en.nextElement();
            if (desc.getClassname().equals(classname)) {
                return desc;
            }
        }
        return null;
    }

    public static String prodcontext = "np";

    private static Configer config = null;

    public static Configer getConfig() {
        if (config == null) {
            File configfile = new File("conf/" + prodcontext + ".properties");
            config = new Configer(configfile);
        }

        return config;
    }

    public static Opgroup topopgroup = null;


    static String themepackname="roueBluethemepack";
    static {
        boolean themeexists=new File("skin/"+themepackname+".zip").exists();
        if(themeexists){
            initTheme();
        }
    }

    private static PingThread pingthread=null;
    public static void startPingthread(){
        if(debug==1 || pingthread!=null )return;
        pingthread=new PingThread();
        Thread t=new Thread(pingthread);
        t.setDaemon(true);
        t.start();
        
    }


    private static void initTheme(){
        String clsname = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
        try {
            String xmlpath = "file:skin\\roueBluethemepack\\skinlf-themepack.xml";
            URL url=new URL(xmlpath);
            //SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePackDefinition(url));


            Skin skin = SkinLookAndFeel.loadThemePack("skin/"+themepackname+".zip");
            SkinLookAndFeel.setSkin(skin);


            UIManager.setLookAndFeel(clsname);
        } catch (Exception e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            if (OS.isOneDotFourOrMore()) {
                java.lang.reflect.Method method =
                        JFrame.class.getMethod("setDefaultLookAndFeelDecorated",
                                new Class[]{boolean.class});
                method.invoke(null, new Object[]{Boolean.TRUE});

                method =
                        JDialog.class.getMethod("setDefaultLookAndFeelDecorated",
                                new Class[]{boolean.class});
                method.invoke(null, new Object[]{Boolean.TRUE});
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
    
    public static String[] nosumcolumns={
    	"sex",
    };

    /**
     * ϵͳ��ȷ����.
     */
    public static boolean systemrunning=true;
    
    /**
     * �ܵ�ϸĿ��ѯϸ��,��ʱ�ĺ����� add by wwh 20070920
     */
    public static int mderetrievedtldeplay=400;
    
    /**
     * ÿ�δӷ�����ȡ������?
     */
    public static int fetchmaxrow=1000;

	/**
	 * �Ƿ���J2EE������������
	 */
	public static boolean runonserver=false;
    
	
	/**
	 * ���ű��Ƿ���pub_dept
	 */
	public static boolean depttable_use_pub_dept=false;
	
	/**
	 * ��̬���صĻ�,��Ҫ�µ�CLASSLOADER
	 */
	public static ClassLoader classloader=ClassLoader.getSystemClassLoader();
	
	/**
	 * ÿ������32k
	 */
	public static int binfileblocksize= 32768;
	
	static {
		//2008-7-17 �Ӵ��󲶻�
		Thread.setDefaultUncaughtExceptionHandler(new DefaulterrorHandle());
	}
}
