package com.smart.adminclient.gui;


import com.smart.platform.gui.runop.Opgroup;
import com.smart.platform.gui.runop.Opnode;

public class Adminops {

	public static Opgroup createAdminOps() {
		Opgroup topgroup = new Opgroup("npadmin");

		/**
		 * ��ʼ������:
		 */
		Opgroup group = createProdGroup();
		topgroup.addSubgroup(group);

		group = createMonitorGroup();
		topgroup.addSubgroup(group);

		return topgroup;

	}

	/**
	 * ��Ʒ����
	 */
	private static Opgroup createProdGroup() {
		Opgroup group = new Opgroup("��������Ʒ����");
		Opnode opnode = null;
		boolean ok = true;
		opnode = new Opnode("", "���������ݿ����ӳ�����");
		opnode.setClassname("com.inca.adminclient.dbcp.Dbcpframe");
		group.addOpnode(opnode);

		opnode = new Opnode("", "��Ʒ��Ȩ����");
		opnode.setClassname("com.inca.adminclient.prodmanager.ProdmanagerFrame");
		group.addOpnode(opnode);

		opnode = new Opnode("", "����ģ�鰲װ��");
		opnode.setClassname("com.inca.adminclient.installjar.Installjarbuilder");
		group.addOpnode(opnode);
		
		opnode = new Opnode("", "��Ʒģ�鰲װ");
		opnode.setClassname("com.inca.adminclient.modulemgr.ModulemgrFrame");
		group.addOpnode(opnode);

		
		return group;

	}

	private static Opgroup createMonitorGroup() {
		Opgroup group = new Opgroup("NPServer���");
		Opnode opnode = null;
		opnode = new Opnode("", "��ѯ����������");
		opnode.setClassname("com.inca.adminclient.svrperform.Svrperform_frm");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ�ѵ�¼�û�");
		opnode.setClassname("com.inca.adminclient.serverinfo.Listlogin_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯsqlִ�����");
		opnode.setClassname("com.inca.adminclient.serverinfo.Sqlmonitor_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "����û�sql");
		opnode.setClassname("com.inca.adminclient.usersqlm.Usersqlm_frm");
		group.addOpnode(opnode);
		opnode = new Opnode("", "Զ�̲�ѯ");
		opnode.setClassname("com.inca.adminclient.remotesql.Remotesql_frame");
		group.addOpnode(opnode);
		//opnode = new Opnode("", "��ѯ������������־");
		//opnode.setClassname("com.inca.np.logger.Visitlogger_frame");
		//group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ��������־");
		opnode.setClassname("com.inca.adminclient.viewlog.ViewlogFrame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ���ݿ��������ռ�");
		opnode.setClassname("com.inca.adminclient.serverinfo.Tablespace_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ���ݿ��������Ϣ");
		opnode.setClassname("com.inca.adminclient.serverinfo.Serverinfo_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ���ݿ����������");
		opnode.setClassname("com.inca.adminclient.serverinfo.Session_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "��ѯ���ݿ����������");
		opnode.setClassname("com.inca.adminclient.serverinfo.Sessionlock_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "������ݿ�Fullscan");
		opnode.setClassname("com.inca.adminclient.fullscan.Fullscan_frame");
		group.addOpnode(opnode);

		return group;
	}
}
