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
 * 传输数据的命令
 */
public class DataCommand extends CommandBase{

	/**
	 * 数据源
	 */
    DBTableModel dbmodel=null;
    
    /**
     * 构造
     */
    public DataCommand() {
        super();
        commandhead.commandtype=CommandHead.COMMANDTYPE_DATA;
    }

    /**
     * 构造
     * @param commandhead  命令头
     */
    public DataCommand(CommandHead commandhead) {
        super(commandhead);
    }

    /**
     * 写数据
     */
    protected void writeData(OutputStream out) throws Exception {
        dbmodel.writeData(out);
    }

    /**
     * 读数据
     */
    protected void readData(InputStream in) throws Exception {
        dbmodel=new DBTableModel();
        dbmodel.readData(in);
    }

    /**
     * 返回数据源dbmodel
     * @return
     */
    public DBTableModel getDbmodel() {
        return dbmodel;
    }

    /**
     * 设置数据源dbmodel
     * @param dbmodel
     */
    public void setDbmodel(DBTableModel dbmodel) {
        this.dbmodel = dbmodel;
    }
}
