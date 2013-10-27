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
 * 服务器保存的结果.
 */
public class ResultCommand extends CommandBase{
    /**
     * 成功
     */
    public final static int RESULT_OK=0;

    /**
     * 失败
     */
    public final static int RESULT_FAILURE=1;

    /**
     * 部分成功
     */
    public final static int RESULT_PARTFAILURE=2;

    /**
     * 保存状态.0为成功
     */
    protected int result;

    /**
     * 保存结果信息,如果发生错误,记录错误原因
     */
    String message="";

    /**
     * 构造
     */
    public ResultCommand() {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_RESULT;
    }

    /**
     * 构造
     * @param result  状态
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
     * 构造
     * @param result  状态
     * @param message 错误原因 
     */
    public ResultCommand(int result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * 设置状态
     * @param result
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * 设置保存结果信息
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 返回状态
     * @return
     */
    public int getResult() {
        return result;
    }

    /**
     * 返回信息
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 结果记录向量
     */
    Vector<RecordTrunk> lineresults=new Vector<RecordTrunk>();

    /**
     * 增加一行结果记录
     * @param lineresult
     */
    public void addLineResult(RecordTrunk lineresult){
         lineresults.add(lineresult);
    }

    /**
     * 写数据到out
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
     * 由in创建
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
     * 返回结果记录数量
     * @return
     */
    public int getLineresultCount(){
        return lineresults.size();
    }

    /**
     * 返回第index个结果记录
     * @param index
     * @return
     */
    public RecordTrunk getLineresult(int index){
        if(index<0 || index>lineresults.size() - 1)return null;
        return lineresults.elementAt(index);
    }

    /**
     * 返回结果记录向量
     * @return
     */
    public Vector<RecordTrunk> getLineresults() {
        return lineresults;
    }

    /**
     * 将cmd中的结果记录补到本类结果记录向量后面
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
