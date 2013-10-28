package com.smart.client.system;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.runop.Opgroup;
import com.smart.platform.gui.runop.Opnode;

/**
 * 从服务器下载可运行的ops 根据GROUP进行分组.
 * 
 * @author Administrator
 * 
 */
public class NpopManager {
	static NpopManager inst = null;

	public DBTableModel getOpmodel() {
		return opmodel;
	}

	public static NpopManager getInst() {
		if (inst == null) {
			inst = new NpopManager();
		}
		return inst;
	}

	HashMap<String, Opnode> opidmap = new HashMap<String, Opnode>();
	private Opgroup topgroup;

	private NpopManager() {

	}

	DBTableModel opmodel = null;

	public void build(DBTableModel opmodel) {
		this.opmodel = opmodel;
		HashMap<String, Vector<Opnode>> groupmap = new HashMap<String, Vector<Opnode>>();
		Vector<String> groupnames = new Vector<String>();

		for (int r = 0; r < opmodel.getRowCount(); r++) {
			String opid = opmodel.getItemValue(r, "opid");
			String opcode = opmodel.getItemValue(r, "opcode");
			String opname = opmodel.getItemValue(r, "opname");
			String classname = opmodel.getItemValue(r, "classname");
			String prodname = opmodel.getItemValue(r, "prodname");
			String modulename = opmodel.getItemValue(r, "modulename");
			String groupname = opmodel.getItemValue(r, "groupname");

			Vector<Opnode> ops = groupmap.get(groupname);
			if (ops == null) {
				ops = new Vector<Opnode>();
				groupmap.put(groupname, ops);
				groupnames.add(groupname);
			}

			Opnode opnode = new Opnode(opid, opname);
			ops.add(opnode);
			opnode.setOpcode(opcode);
			opnode.setClassname(classname);
			opnode.setProdname(prodname);
			opnode.setModulename(modulename);
			opnode.setGroupname(groupname);
			opidmap.put(opid, opnode);
		}

		topgroup = new Opgroup("NP");
		Enumeration<String> en = groupnames.elements();
		while (en.hasMoreElements()) {
			String groupname = en.nextElement();
			String ss[] = groupname.split("-");
			Opgroup parentgroup = topgroup;
			for (int i = 0; i < ss.length; i++) {
				String subgroupname = ss[i];
				Opgroup targetnode = null;
				Enumeration<Opgroup> eng = parentgroup.getSubgroups()
						.elements();
				while (eng.hasMoreElements()) {
					Opgroup tmpg = eng.nextElement();
					if (tmpg.getGroupname().equals(subgroupname)) {
						targetnode = tmpg;
						break;
					}
				}

				if (targetnode == null) {
					targetnode = new Opgroup(subgroupname);
					parentgroup.addSubgroup(targetnode);
				}

				if (i == ss.length - 1) {
					Vector<Opnode> ops = groupmap.get(groupname);
					Enumeration<Opnode> en1 = ops.elements();
					while (en1.hasMoreElements()) {
						targetnode.addOpnode(en1.nextElement());
					}
				}
				parentgroup = targetnode;

			}
		}

	}

	public Opnode getOpnode(String opid) {
		return opidmap.get(opid);
	}

	public Opgroup getTopgroup() {
		return topgroup;
	}

	public void addOpinfo(String opid, Opnode opinfo) {
		opidmap.put(opid, opinfo);
	}

}
