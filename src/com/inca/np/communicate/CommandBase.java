package com.inca.np.communicate;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-26
 * Time: 17:09:43
 * 命令base
 */
public abstract class CommandBase {
    protected CommandHead commandhead;

    protected CommandBase(CommandHead commandhead) {
        this.commandhead = commandhead;
    }

    protected CommandBase() {
        commandhead=new CommandHead();
        this.commandhead.version="0.1";
    }

    /**
     * 将命令写入一个流
     * @param out
     * @throws Exception
     */
    public void write(OutputStream out)throws Exception{
        commandhead.write(out);
        writeData(out);
    }

    /**
     * 抽象函数,将数据部份写入一个流
     * @param out
     * @throws Exception
     */
    protected abstract void writeData(OutputStream out)throws Exception;

    /**
     * 读数据
     * @param in
     * @throws Exception
     */
    protected abstract void readData(InputStream in)throws Exception;

    /**
     * 判断两个命令是否相同.
     * @param othercommand
     * @return
     */
    public boolean equals(CommandBase othercommand){
        if(!commandhead.equals(othercommand.commandhead))return false;
        return true;
    }

}
