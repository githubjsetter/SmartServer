package com.smart.platform.communicate;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:30:20
 * ´«ÊäsqlµÄÃüÁî
 */
public class SqlCommand extends CommandBase{
    String sql;
    int startrow=0;
    int maxrowcount=100;



    public SqlCommand(CommandHead commandhead) {
        super(commandhead);
    }

    public SqlCommand(String sql) {
        super();
        commandhead.commandtype=CommandHead.COMMANDTYPE_SQL;
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    protected void writeData(OutputStream out) throws Exception {
        CommandFactory.writeString(sql,out);
        CommandFactory.writeShort(startrow,out);
        CommandFactory.writeShort(maxrowcount,out);
    }

    protected void readData(InputStream in) throws Exception {
        sql=CommandFactory.readString(in);
        startrow=CommandFactory.readShort(in);
        maxrowcount=CommandFactory.readShort(in);
    }

    public boolean equals(SqlCommand other){
        if(!super.equals(other))return false;
        if(!sql.equals(other.sql))return false;

        if(startrow!=other.startrow)return false;
        if(maxrowcount!=other.maxrowcount)return false;
        return true;
    }

    public int getStartrow() {
        return startrow;
    }

    public void setStartrow(int startrow) {
        this.startrow = startrow;
    }

    public int getMaxrowcount() {
        return maxrowcount;
    }

    public void setMaxrowcount(int maxrowcount) {
        this.maxrowcount = maxrowcount;
    }
}
