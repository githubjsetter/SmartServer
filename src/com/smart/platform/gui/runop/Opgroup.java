package com.smart.platform.gui.runop;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-6
 * Time: 11:15:41
 * ���ܷ���
 */
public class Opgroup {
    String groupname;

    public String getGroupname() {
		return groupname;
	}

	public Opgroup(String groupname) {
        this.groupname = groupname;
    }


    Vector<Opnode> opnodes=new Vector<Opnode>();
    Vector<Opgroup> subgroups=new Vector<Opgroup>();

    public void addOpnode(Opnode opnode){
        opnodes.add(opnode);
    }

    public Vector<Opnode> getOpnodes() {
        return opnodes;
    }

    public void addSubgroup(Opgroup subgroup){
        subgroups.add(subgroup);
    }


    public Vector<Opgroup> getSubgroups() {
        return subgroups;
    }

    public String toString() {
        return groupname;
    }

    public static  Opgroup createDemo(){
        Opgroup group=new Opgroup("����");

        Opgroup subgroup=new Opgroup("����");
        group.addSubgroup(subgroup);

        Opnode opnode = new Opnode("1","��Ʒ����");
        opnode.setClassname("com.inca.np.demo.ste.Pub_goods_ste");
        subgroup.addOpnode(opnode);

        opnode = new Opnode("2","���ù���2");
        subgroup.addOpnode(opnode);


        subgroup=new Opgroup("�������ݹ���");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("��ʼ������");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("�������");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("����");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("���ڹ���");
        group.addSubgroup(subgroup);

        return group;
    }
}
