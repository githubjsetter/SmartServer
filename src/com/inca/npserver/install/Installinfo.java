package com.inca.npserver.install;

import java.util.Vector;


/**
 * 模块安装信息
 * @author Administrator
 *
 */
public class Installinfo {
	String prodname="";
	/**
	 * 模块名. 中文
	 */
	String modulename="";
	
	/**
	 * 模块名,英文,内部名
	 */
	String moduleengname="";
	String version="";

	Vector<Opinfo> opinfos=new Vector<Opinfo>();
	public void addOpinfo(Opinfo opinfo){
		opinfos.add(opinfo);
	}

	Vector<Hovinfo> hovinfos=new Vector<Hovinfo>();
	public void addHovinfo(Hovinfo hovinfo){
		hovinfos.add(hovinfo);
	}

	Vector<Serviceinfo> serviceinfos=new Vector<Serviceinfo>();
	public void addServiceinfo(Serviceinfo serviceinfo){
		serviceinfos.add(serviceinfo);
	}
	
	
	
	/**
	 * 安装的功能信息
	 * @author Administrator
	 *
	 */
	public static class Opinfo{
		public String opid="";
		public String opcode="";
		public String opname="";
		public String classname="";
		public String groupname="";
		public String sortno="";
	}

	/**
	 * 安装服务信息
	 * @author Administrator
	 *
	 */
	public static class Serviceinfo{
		public String command;
		public String classname;
	}

	/**
	 * 安装HOV信息
	 * @author Administrator
	 *
	 */
	public static class Hovinfo{
		public String hovname;
		public String classname;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public String getModuleengname() {
		return moduleengname;
	}

	public void setModuleengname(String moduleengname) {
		this.moduleengname = moduleengname;
	}

	public String getModulename() {
		return modulename;
	}

	public void setModulename(String modulename) {
		this.modulename = modulename;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Vector<Opinfo> getOpinfos() {
		return opinfos;
	}

	public Vector<Hovinfo> getHovinfos() {
		return hovinfos;
	}

	public Vector<Serviceinfo> getServiceinfos() {
		return serviceinfos;
	}

	public void setOpinfos(Vector<Opinfo> opinfos) {
		this.opinfos = opinfos;
	}

	public void setHovinfos(Vector<Hovinfo> hovinfos) {
		this.hovinfos = hovinfos;
	}

	public void setServiceinfos(Vector<Serviceinfo> serviceinfos) {
		this.serviceinfos = serviceinfos;
	}
}
