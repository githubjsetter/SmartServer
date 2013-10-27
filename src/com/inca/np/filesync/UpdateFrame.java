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
        super("软件在线升级");
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


        Font font = new Font("宋体", Font.PLAIN, 12);
        JLabel lb = new JLabel(summary);
        lb.setFont(font);
        cp.add(lb);
        layout.addLayoutComponent(lb, new CFormlineBreak());

        lb = new JLabel("服务器URL");
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
        btndownload = new JButton("下载更新");
        btndownload.setActionCommand("download");
        btndownload.addActionListener(ah);
//        jp.add(btndownload);

        JButton btncancel = new JButton("取消");
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
        setStatus("完成");
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
        setStatus("开始搜集本地文件信息.......");
        FileinfoFinder ff = new FileinfoFinder();
        logger.info("开始收集本地"+srcdir.getPath()+"文件清单");
        FileinfoDBmodel srcmodel = ff.searchFile(srcdir);

        if (srcmodel.getRowCount() == 0) {
            errorMessage("错误", "没有找到文件,本地文件路径填错了");
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

        setStatus("开始向服务器发送本地文件信息.........");
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
                errorMessage("服务器错误", respstatus);
                return;
            }


        } catch (Exception e) {
            errorMessage("错误", e.getMessage());
            return;
        }

        //处理数据
        DataCommand respdata = (DataCommand) svrresp.commandAt(2);
        DBTableModel targetmodel = respdata.getDbmodel();
        if(targetmodel.getRowCount()==0){
            infoMessage("提示","没有文件需要更新");
            this.dispose();
            return;
        }
        ParamCommand respparam = (ParamCommand) svrresp.commandAt(1);
        String filename = respparam.getValue("filename");
        int filesize = Integer.parseInt(respparam.getValue("filesize"));
        String strsize = StringUtil.bytes2string(filesize);

        String confirmmsg = "要下载" + targetmodel.getRowCount() +
                "个文件,打包压缩文件大小" + strsize + ",继续吗?";

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

        setStatus("收到服务器响应,需要更新"+targetmodel.getRowCount()+"个文件,开始接收");



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
                        errorMessage("服务器错误", respstatus);
                        return;
                    }
                } catch (Exception e) {
                    errorMessage("错误", e.getMessage());
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
                    setStatus("已接收文件"+StringUtil.bytes2string(startpos));
                } finally {
                    if(fout!=null){
                        fout.close();
                    }
                }
                if(!hasmore){
                    break;
                }
            }


            //文件下载成功,解压
            setStatus("接收完成,开始更新本地文件......");
            ZipHelper.unzipFile(tmpfile,srcdir);
            setStatus("更新完成");
            infoMessage("更新成功","全部文件已下载并安装成功,按确定重新启动,以使更新生效.");

        } catch (Exception e) {
            logger.error("error",e);
            errorMessage("下载失败",e.getMessage());
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
            super(UpdateFrame.this, "需要下载下列文件,请确认",true);
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

            JButton btnconfirm=new JButton("下载更新 ");
            bottomjp.add(btnconfirm);
            btnconfirm.setActionCommand("confirm");
            btnconfirm.addActionListener(this);


            JButton btncancel=new JButton("取消");
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



            String summary = "检查软件是否有最新的更新,并自动升级";
}
