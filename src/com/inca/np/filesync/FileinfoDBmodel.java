package com.inca.np.filesync;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.DBColumnDisplayInfo;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 14:04:20
 * To change this template use File | Settings | File Templates.
 */
public class FileinfoDBmodel extends DBTableModel {

    static Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
    static{
        cols.add(new DBColumnDisplayInfo("path","varchar","�ļ�·��"));
        cols.add(new DBColumnDisplayInfo("lastmodifytime","varchar","�ļ�·��"));
        cols.add(new DBColumnDisplayInfo("filesize","varchar","��С"));
        cols.add(new DBColumnDisplayInfo("md5","varchar","md5"));
    }

    public FileinfoDBmodel() {
        super(cols);
    }
}
