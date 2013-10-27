package com.inca.npserver.install;

import java.util.Vector;


/**
 * ģ�鰲װ��Ϣ
 * @author Administrator
 *
 */
public class Installinfo {
	String prodname="";
	/**
	 * ģ����. ����
	 */
	String modulename="";
	
	/**
	 * ģ����,Ӣ��,�ڲ���
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
	 * ��װ�Ĺ�����Ϣ
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
	 * ��װ������Ϣ
	 * @author Administrator
	 *
	 */
	public static class Serviceinfo{
		public String command;
		public String classname;
	}

	/**
	 * ��װHOV��Ϣ
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
