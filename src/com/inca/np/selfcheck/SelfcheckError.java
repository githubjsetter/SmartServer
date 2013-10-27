package com.inca.np.selfcheck;

import java.io.PrintWriter;

public class SelfcheckError {
	String errorid;
	String errormessage;
	String msg="";
	public SelfcheckError(String errorid, String errormessage) {
		super();
		this.errorid = errorid;
		this.errormessage = errormessage;
	}
	public String getErrorid() {
		return errorid;
	}
	public void setErrorid(String errorid) {
		this.errorid = errorid;
	}
	public String getErrormessage() {
		return errormessage;
	}
	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void dump(PrintWriter out){
		out.println(errormessage+"\t"+msg);
	}
}
