package com.inca.np.filesync;

import com.inca.np.gui.control.*;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.StringUtil;
import com.inca.np.client.RemoteConnector;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.communicate.*;
import com.inca.np.server.RequestDispatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 16:43:14
 * ���ͱȽ��ļ�
 * <p/>
 * ����:
 * ����˵��
 * <p/>
 * ������IPַ:�˿�
 * <p/>
 * web-inf·��   �ı���
 * <p/>
 * �ϴ���ť
 */
public class UploaderFrame extends CFrame implements Runnable{
    private JTextField textURL;
    private JTextField textPath;
    private JLabel lbstatus;
    private JButton btnupload;

    public UploaderFrame() throws HeadlessException {
        super("������������� ������Աר��");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initControl();
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

        lb = new JLabel("����WEBӦ��λ��");
        lb.setFont(font);
        lb.setPreferredSize(new Dimension(150, 27));
        cp.add(lb);

        textPath = new JTextField("c:\\tomcat51\\webapps\\" + DefaultNPParam.prodcontext , 80);
        textPath.setFont(font);
        cp.add(textPath);
        layout.addLayoutComponent(textPath, new CFormlineBreak());

        lb = new JLabel("���±�ע");
        lb.setFont(font);
        lb.setPreferredSize(new Dimension(150, 27));
        cp.add(lb);

        textMemo = new JTextField("�������������",80);
		textMemo.setFont(font);
        cp.add(textMemo);
        layout.addLayoutComponent(textMemo, new CFormlineBreak());

        
        ActionHandle ah = new ActionHandle();
        JPanel bottompanel = new JPanel();
        bottompanel.setLayout(new BorderLayout());

        JPanel jp=new JPanel();
        btnupload = new JButton("�ϴ�����");
        btnupload.setActionCommand("upload");
        btnupload.addActionListener(ah);
        jp.add(btnupload);

        JButton btncancel = new JButton("ȡ��");
        btncancel.setActionCommand("cancel");
        btncancel.addActionListener(ah);
        jp.add(btncancel);
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
        doUpload();
        btnupload.setEnabled(true);
        setStatus("���");
    }

    void setStatus(String msg){
        lbstatus.setText(msg);
    }

    class ActionHandle implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("upload")) {
                btnupload.setEnabled(false);
                Thread t=new Thread(UploaderFrame.this);
                t.start();
            } else if (cmd.equals("cancel")) {
                dispose();
            }

        }
    }

    void doUpload() {
        setStatus("��ʼ�Ѽ������ļ���Ϣ.......");
        String path = textPath.getText();
        FileinfoFinder ff = new FileinfoFinder();
        File startdir = new File(path);
        FileinfoDBmodel srcmodel = ff.searchFile(startdir);

        if (srcmodel.getRowCount() == 0) {
            errorMessage("����", "û���ҵ��ļ�,�����ļ�·�������");
            return;
        }

        ClientRequest req = new ClientRequest();
        req.addCommand(new StringCommand("np:comparafile"));

        ParamCommand paramcmd = new ParamCommand();
        paramcmd.addParam("uploadtype", "WEBAPP");
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
        DataCommand respdata = (DataCommand) svrresp.commandAt(1);
        DBTableModel targetmodel = respdata.getDbmodel();
        if(targetmodel.getRowCount()==0){
            infoMessage("��ʾ","û���ļ���Ҫ����");
            return;
        }

        setStatus("�յ���������Ӧ,��Ҫ����"+targetmodel.getRowCount()+"���ļ�,��ʼ���");

        //�Ƚ������ļ�
        FileZiper fzip = new FileZiper();
        File tmpfile = null;
        FileOutputStream fout = null;
        try {
            tmpfile = File.createTempFile("nptmp", ".zip");
            fout = new FileOutputStream(tmpfile);
            fzip.doZip(startdir, targetmodel, fout);
            fout.close();
            fout = null;

            String strsize = StringUtil.bytes2string((int) tmpfile.length());

            String confirmmsg = "Ҫ�ϴ�����" + targetmodel.getRowCount() +
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

            //��ȡ�ļ�,�ִη���
            sendFile(tmpfile);

            setStatus("�ϴ����³ɹ�");
            infoMessage("�ɹ�","�ϴ����³ɹ�!");

        } catch (Exception e) {
            errorMessage("����", e.getMessage());
            return;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
            if (tmpfile != null) {
                tmpfile.delete();
            }
        }



    }

    void sendFile(File tmpfile) throws Exception {
        FileInputStream fin = null;
        //ÿ�η���100K����
        int blocksize = 102400;
        try {
            fin = new FileInputStream(tmpfile);
            byte[] buffer = new byte[blocksize];

            int sentsize = 0;
            int filelen = (int) tmpfile.length();

            DecimalFormat decf=new DecimalFormat("0.00");
            while (sentsize < filelen) {
                int rdlen = fin.read(buffer);
                double rate = (double)sentsize / (double)tmpfile.length() * 100.0;
                setStatus("�������������������,�ѷ���"+
                        StringUtil.bytes2string(sentsize)+",���"+decf.format(rate)+"%");
                sendData(buffer, 0, rdlen, sentsize,(sentsize+rdlen)>=filelen);
                sentsize += rdlen;
            }
        } finally {
            if (fin != null) {
                fin.close();
            }
        }

    }

    void sendData(byte[] buffer, int offset, int len, int filepos,boolean finished) throws Exception {
        ClientRequest req = new ClientRequest();
        req.addCommand(new StringCommand("np:uploadfile"));

        ParamCommand paramcmd = new ParamCommand();
        req.addCommand(paramcmd);
        paramcmd.addParam("uploadtype", "WEBAPP");
        paramcmd.addParam("startpos", String.valueOf(filepos));
        paramcmd.addParam("length", String.valueOf(len));
        paramcmd.addParam("finished", finished?"true":"false");
        paramcmd.addParam("memo",textMemo.getText());

        BinfileCommand bincmd = new BinfileCommand(buffer, offset, len);
        req.addCommand(bincmd);

        RemoteConnector rmtconn = new RemoteConnector();
        String url = DefaultNPParam.defaultappsvrurl;
        ServerResponse svrresp = null;
        if (DefaultNPParam.debug == 1) {
            svrresp = RequestDispatch.getInstance().process(req);
        } else {
            svrresp = rmtconn.submitRequest(url, req);
        }

        StringCommand cmd0 = (StringCommand) svrresp.commandAt(0);
        String respstatus = cmd0.getString();
        if (respstatus.startsWith("-ERROR")) {
            throw new Exception(respstatus);
        }

    }


    public static void main(String[] argv) {
        new DefaultNPParam();
        UploaderFrame frm = new UploaderFrame();
        frm.pack();
        frm.setVisible(true);
    }


    class Confirmdialog extends CDialog {

        public int ret=-1;
        String confirmmsg;
        String paths;
        public Confirmdialog(String msg,String paths) throws HeadlessException {
            super(UploaderFrame.this, "��Ҫ�ϴ������ļ�,��ȷ��",true);
            this.confirmmsg=msg;
            this.paths=paths;
            initDialog();
            localScreenCenter();
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }


        protected void localScreenCenter() {
            Dimension screensize = UploaderFrame.this.getPreferredSize();
            Dimension size = this.getPreferredSize();
            double x=  (screensize.getWidth() - size.getWidth())/2.0;
            double y = (screensize.getHeight() - size.getHeight()) / 2.0;

            setLocation((int)x,(int)y);
        }

        void initDialog(){
            Container cp = this.getContentPane();
            cp.setLayout(new BorderLayout());

            JLabel lb=new JLabel(confirmmsg);
            cp.add(lb,BorderLayout.NORTH);

            TextArea text = new TextArea(30,80);
            cp.add(text,BorderLayout.CENTER);
            text.setText(paths);

            JPanel bottomjp=new JPanel();
            cp.add(bottomjp,BorderLayout.SOUTH);

            JButton btnconfirm=new JButton("ȷ���ϴ�");
            bottomjp.add(btnconfirm);
            btnconfirm.setActionCommand("confirm");
            btnconfirm.addActionListener(this);


            JButton btncancel=new JButton("ȡ���ϴ�");
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


            String summary = "�����ز��������tomcatĿ¼�µ�WEBӦ�ø��µ�Զ��" +
            "tomcat��������.�����з���,�ϴ���Ҫ����.";
			private JTextField textMemo;

}
