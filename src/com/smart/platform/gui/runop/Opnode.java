package com.smart.platform.gui.runop;

import com.smart.platform.gui.control.CFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-6
 * Time: 11:14:27
 * 一个功能结点
 */
public class Opnode {
    String opid,opname;
    Opgroup group=null;

    String classname="";
    String opcode,prodname,modulename,groupname;

    CFrame runningframe=null;
    
    public String getOpname() {
		return opname;
	}


	public Opnode(String opid, String opname) {
        this.opid = opid;
        this.opname = opname;
    }

    
    public String getOpid() {
		return opid;
	}


	public Opgroup getGroup() {
        return group;
    }

    public void setGroup(Opgroup group) {
        this.group = group;
    }

    public String toString() {
        return opname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

	public String getOpcode() {
		return opcode;
	}

	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	public String getModulename() {
		return modulename;
	}

	public void setModulename(String modulename) {
		this.modulename = modulename;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}


	public CFrame getRunningframe() {
		return runningframe;
	}


	public void setRunningframe(CFrame runningframe) {
		this.runningframe = runningframe;
	}
    
    
}
