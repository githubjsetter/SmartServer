package com.inca.np.communicate;

import com.inca.np.gui.control.DBTableModel;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:30:36
 * �������ݵ�����
 */
public class DataCommand extends CommandBase{

	/**
	 * ����Դ
	 */
    DBTableModel dbmodel=null;
    
    /**
     * ����
     */
    public DataCommand() {
        super();
        commandhead.commandtype=CommandHead.COMMANDTYPE_DATA;
    }

    /**
     * ����
     * @param commandhead  ����ͷ
     */
    public DataCommand(CommandHead commandhead) {
        super(commandhead);
    }

    /**
     * д����
     */
    protected void writeData(OutputStream out) throws Exception {
        dbmodel.writeData(out);
    }

    /**
     * ������
     */
    protected void readData(InputStream in) throws Exception {
        dbmodel=new DBTableModel();
        dbmodel.readData(in);
    }

    /**
     * ��������Դdbmodel
     * @return
     */
    public DBTableModel getDbmodel() {
        return dbmodel;
    }

    /**
     * ��������Դdbmodel
     * @param dbmodel
     */
    public void setDbmodel(DBTableModel dbmodel) {
        this.dbmodel = dbmodel;
    }
}
