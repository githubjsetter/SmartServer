package com.inca.np.filesync;

import com.inca.np.gui.control.*;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.StringUtil;
import com.inca.np.util.ZipHelper;
import com.inca.np.communicate.*;
import com.inca.np.client.RemoteConnector;
import com.inca.np.server.RequestDispatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-6
 * Time: 9:23:41
 * @deprecated
 */
public class UpdateFrame extends CFrame implements Runnable {
    private JTextField textURL;
    private JLabel lbstatus;
    private JButton btndownload;

    //File srcdir=new File("c:\\ngpcs");
    File srcdir=new File(".");

    Category logger=Category.getInstance(UpdateFrame.class);

    public UpdateFrame() throws HeadlessException {
        super("�����������");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initControl();
    }


    public void setVisible(boolean b) {
        super.setVisible(b);    //To change body of overridden methods use File | Settings | File Templates.
        if(b){
            Thread t=new Thread(this);
            t.start();
        }
    }

    void initControl() {
        Container cp = this.getContentPane();

        CFormlayout layout = new CFormlayout(2, 2);
        cp.setLayout(layout);


        Font font = new Font("����", Font.PLAIN, 12);
        JLabel lb = new JLabel(summary);
        lb.setFont(font);
        cp.add(lb);
        layout.addLayoutComponent(lb, new CFormlineBreak());

        lb = new JLabel("������URL");
        lb.setFont(font);
        lb.setPreferredSize(new Dimension(150, 27));
        cp.add(lb);

        textURL = new JTextField("http://127.0.0.1:80/np/clientrequest.do", 80);
        textURL.setFont(font);
        loadConfigvalue();
        cp.add(textURL);
        layout.addLayoutComponent(textURL, new CFormlineBreak());


        ActionHandle ah = new ActionHandle();
        JPanel bottompanel = new JPanel();
        bottompanel.setLayout(new BorderLayout());

        JPanel jp=new JPanel();
        btndownload = new JButton("���ظ���");
        btndownload.setActionCommand("download");
        btndownload.addActionListener(ah);
//        jp.add(btndownload);

        JButton btncancel = new JButton("ȡ��");
        btncancel.setActionCommand("cancel");
        btncancel.addActionListener(ah);
//        jp.add(btncancel);
        bottompanel.add(jp,BorderLayout.CENTER);


        lbstatus = new JLabel("");
        bottompanel.add(lbstatus,BorderLayout.SOUTH);

        cp.add(bottompanel);
        layout.addLayoutComponent(bottompanel, new CFormlineBreak());


        bottompanel.setPreferredSize(new Dimension(800, 50));
        this.setPreferredSize(new Dimension(800, 200));
        Dimension size = this.getPreferredSize();
        Dimension scrsize = this.getToolkit().getScreenSize();

        int x = ((int) (scrsize.getWidth() - size.getWidth())) / 2;
        int y = ((int) (scrsize.getHeight() - size.getHeight())) / 2;

        this.setLocation(x, y);

    }

    private void loadConfigvalue() {
        String ip = DefaultNPParam.defaultappsvrurl;
        if (ip != null) {
            textURL.setText(ip);
        }
    }


    public void run(){
        doDownload();
        btndownload.setEnabled(true);
        setStatus("���");
    }

    void setStatus(String msg){
        lbstatus.setText(msg);
    }

    class ActionHandle implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("download")) {
                btndownload.setEnabled(false);
                Thread t=new Thread(UpdateFrame.this);
                t.start();
            } else if (cmd.equals("cancel")) {
                dispose();
            }

        }
    }

    void doDownload() {
/*    	
        setStatus("��ʼ�Ѽ������ļ���Ϣ.......");
        FileinfoFinder ff = new FileinfoFinder();
        logger.info("��ʼ�ռ�����"+srcdir.getPath()+"�ļ��嵥");
        FileinfoDBmodel srcmodel = ff.searchFile(srcdir);

        if (srcmodel.getRowCount() == 0) {
            errorMessage("����", "û���ҵ��ļ�,�����ļ�·�������");
            return;
        }

        ClientRequest req = new ClientRequest();
        req.addCommand(new StringCommand("np:download"));

        ParamCommand paramcmd = new ParamCommand();
        paramcmd.addParam("downloadtype", "CLIENT");
        req.addCommand(paramcmd);

        DataCommand datacmd = new DataCommand();
        datacmd.setDbmodel(srcmodel);
        req.addCommand(datacmd);

        setStatus("��ʼ����������ͱ����ļ���Ϣ.........");
        RemoteConnector rmtconn = new RemoteConnector();
        String url = textURL.getText();
        ServerResponse svrresp = null;
        try {
            if (DefaultNPParam.debug == 1) {
                svrresp = RequestDispatch.getInstance().process(req);
            } else {
                svrresp = rmtconn.submitRequest(url, req);
            }

            StringCommand cmd0 = (StringCommand) svrresp.commandAt(0);
            String respstatus = cmd0.getString();
            if (respstatus.startsWith("-ERROR")) {
                errorMessage("����������", respstatus);
                return;
            }


        } catch (Exception e) {
            errorMessage("����", e.getMessage());
            return;
        }

        //��������
        DataCommand respdata = (DataCommand) svrresp.commandAt(2);
        DBTableModel targetmodel = respdata.getDbmodel();
        if(targetmodel.getRowCount()==0){
            infoMessage("��ʾ","û���ļ���Ҫ����");
            this.dispose();
            return;
        }
        ParamCommand respparam = (ParamCommand) svrresp.commandAt(1);
        String filename = respparam.getValue("filename");
        int filesize = Integer.parseInt(respparam.getValue("filesize"));
        String strsize = StringUtil.bytes2string(filesize);

        String confirmmsg = "Ҫ����" + targetmodel.getRowCount() +
                "���ļ�,���ѹ���ļ���С" + strsize + ",������?";

        ArrayList ars=new ArrayList();
        StringBuffer sb=new StringBuffer();
        for(int r=0;r<targetmodel.getRowCount();r++){
            ars.add(targetmodel.getItemValue(r,"path"));
        }
        Collections.sort(ars);
        for(int i=0;i<ars.size();i++){
            sb.append(ars.get(i)+"\r\n");
        }

        Confirmdialog dlg=new Confirmdialog(confirmmsg,sb.toString());
        dlg.pack();
        dlg.setVisible(true);
        if(dlg.ret!=0){
            return;
        }

        setStatus("�յ���������Ӧ,��Ҫ����"+targetmodel.getRowCount()+"���ļ�,��ʼ����");



        downloadFile(filename);

        System.exit(0);
        */
    }

    void downloadFile(String filename) {
        File tmpfile=null;

        try {
            tmpfile = File.createTempFile("download", "zip");

            int startpos=0;
            while(true){
                ClientRequest req = new ClientRequest();
                req.addCommand(new StringCommand("np:download"));
                ParamCommand param = new ParamCommand();
                req.addCommand(param);
                param.addParam("downloadtype","FILE");
                param.addParam("filename",filename);
                param.addParam("startpos",String.valueOf(startpos));

                RemoteConnector rmtconn = new RemoteConnector();
                String url = textURL.getText();
                ServerResponse svrresp = null;
                try {
                    if (DefaultNPParam.debug == 1) {
                        svrresp = RequestDispatch.getInstance().process(req);
                    } else {
                        svrresp = rmtconn.submitRequest(url, req);
                    }

                    StringCommand cmd0 = (StringCommand) svrresp.commandAt(0);
                    String respstatus = cmd0.getString();
                    if (respstatus.startsWith("-ERROR")) {
                        errorMessage("����������", respstatus);
                        return;
                    }
                } catch (Exception e) {
                    errorMessage("����", e.getMessage());
                    return;
                }

                ParamCommand respparam= (ParamCommand) svrresp.commandAt(1);
                boolean hasmore=respparam.getValue("hasmore").equals("true");

                BinfileCommand filecmd= (BinfileCommand) svrresp.commandAt(2);
                FileOutputStream fout = null;
                try {
                    if(startpos==0){
                    fout = new FileOutputStream(tmpfile);
                    }else{
                        fout = new FileOutputStream(tmpfile,true);
                    }
                    byte[] data = filecmd.getBindata();
                    fout.write(data);
                    startpos+=data.length;
                    setStatus("�ѽ����ļ�"+StringUtil.bytes2string(startpos));
                } finally {
                    if(fout!=null){
                        fout.close();
                    }
                }
                if(!hasmore){
                    break;
                }
            }


            //�ļ����سɹ�,��ѹ
            setStatus("�������,��ʼ���±����ļ�......");
            ZipHelper.unzipFile(tmpfile,srcdir);
            setStatus("�������");
            infoMessage("���³ɹ�","ȫ���ļ������ز���װ�ɹ�,��ȷ����������,��ʹ������Ч.");

        } catch (Exception e) {
            logger.error("error",e);
            errorMessage("����ʧ��",e.getMessage());
            return;
        } finally {
            if(tmpfile!=null)tmpfile.delete();
        }
    }


    class Confirmdialog extends CDialog {

        public int ret=-1;
        String confirmmsg;
        String paths;
        public Confirmdialog(String msg,String paths) throws HeadlessException {
            super(UpdateFrame.this, "��Ҫ���������ļ�,��ȷ��",true);
            this.confirmmsg=msg;
            this.paths=paths;
            initDialog();
            localScreenCenter();
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }


/*        protected void localScreenCenter() {
            Dimension screensize = UpdateFrame.this.getPreferredSize();
            Dimension size = this.getPreferredSize();
            double x=  (screensize.getWidth() - size.getWidth())/2.0;
            double y = (screensize.getHeight() - size.getHeight()) / 2.0;

            setLocation((int)x,(int)y);
        }
*/
        void initDialog(){
            Container cp = this.getContentPane();
            cp.setLayout(new BorderLayout());

            JLabel lb=new JLabel(confirmmsg);
            cp.add(lb,BorderLayout.NORTH);

            TextArea text = new TextArea(30,80);
            cp.add(text,BorderLayout.CENTER);
            text.setText(paths);
            text.setEnabled(false);

            JPanel bottomjp=new JPanel();
            cp.add(bottomjp,BorderLayout.SOUTH);

            JButton btnconfirm=new JButton("���ظ��� ");
            bottomjp.add(btnconfirm);
            btnconfirm.setActionCommand("confirm");
            btnconfirm.addActionListener(this);


            JButton btncancel=new JButton("ȡ��");
            bottomjp.add(btncancel);
            btncancel.setActionCommand("cancel");
            btncancel.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if(cmd.equals("confirm")){
                ret=0;
            }else if(cmd.equals("cancel")){
                ret=-1;
            }
            dispose();
        }
    }

    public static void main(String[] argv) {
        new DefaultNPParam();
        UpdateFrame frm = new UpdateFrame();
        frm.pack();
        frm.setVisible(true);
    }



            String summary = "�������Ƿ������µĸ���,���Զ�����";
}
