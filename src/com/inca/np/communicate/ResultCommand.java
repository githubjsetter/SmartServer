package com.inca.np.communicate;

import java.util.Vector;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-4
 * Time: 18:00:02
 * ����������Ľ��.
 */
public class ResultCommand extends CommandBase{
    /**
     * �ɹ�
     */
    public final static int RESULT_OK=0;

    /**
     * ʧ��
     */
    public final static int RESULT_FAILURE=1;

    /**
     * ���ֳɹ�
     */
    public final static int RESULT_PARTFAILURE=2;

    /**
     * ����״̬.0Ϊ�ɹ�
     */
    protected int result;

    /**
     * ��������Ϣ,�����������,��¼����ԭ��
     */
    String message="";

    /**
     * ����
     */
    public ResultCommand() {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_RESULT;
    }

    /**
     * ����
     * @param result  ״̬
     */
    public ResultCommand(int result) {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_RESULT;
        this.result = result;
    }

    public ResultCommand(CommandHead commandhead) {
        super(commandhead);
    }

    /**
     * ����
     * @param result  ״̬
     * @param message ����ԭ�� 
     */
    public ResultCommand(int result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * ����״̬
     * @param result
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * ���ñ�������Ϣ
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * ����״̬
     * @return
     */
    public int getResult() {
        return result;
    }

    /**
     * ������Ϣ
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * �����¼����
     */
    Vector<RecordTrunk> lineresults=new Vector<RecordTrunk>();

    /**
     * ����һ�н����¼
     * @param lineresult
     */
    public void addLineResult(RecordTrunk lineresult){
         lineresults.add(lineresult);
    }

    /**
     * д���ݵ�out
     */
    public void writeData(OutputStream out)throws Exception{
        CommandFactory.writeShort(result,out);
        CommandFactory.writeString(message,out);

        CommandFactory.writeShort(lineresults.size(),out);

        for(int i=0;i<lineresults.size();i++){
            RecordTrunk lineresult = (RecordTrunk)lineresults.elementAt(i);
            lineresult.writeData(out);
        }
    }

    /**
     * ��in����
     */
    public void readData(InputStream in)throws Exception{
        result = CommandFactory.readShort(in);
        message = CommandFactory.readString(in);

        int ct = CommandFactory.readShort(in);
        lineresults=new Vector<RecordTrunk>();
        for(int i=0;i<ct;i++){
            RecordTrunk result=RecordTrunk.readData(in);
            lineresults.add(result);
        }

    }

    /**
     * ���ؽ����¼����
     * @return
     */
    public int getLineresultCount(){
        return lineresults.size();
    }

    /**
     * ���ص�index�������¼
     * @param index
     * @return
     */
    public RecordTrunk getLineresult(int index){
        if(index<0 || index>lineresults.size() - 1)return null;
        return lineresults.elementAt(index);
    }

    /**
     * ���ؽ����¼����
     * @return
     */
    public Vector<RecordTrunk> getLineresults() {
        return lineresults;
    }

    /**
     * ��cmd�еĽ����¼������������¼��������
     * @param cmd
     */
    public void append(ResultCommand cmd){
        Enumeration<RecordTrunk> en = cmd.getLineresults().elements();
        while (en.hasMoreElements()) {
            RecordTrunk lr = en.nextElement();
            this.addLineResult(lr);
        }

    }
}
