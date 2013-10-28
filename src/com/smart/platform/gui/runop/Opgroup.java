package com.smart.platform.gui.runop;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-6
 * Time: 11:15:41
 * 功能分组
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
        Opgroup group=new Opgroup("功能");

        Opgroup subgroup=new Opgroup("常用");
        group.addSubgroup(subgroup);

        Opnode opnode = new Opnode("1","货品管理");
        opnode.setClassname("com.inca.np.demo.ste.Pub_goods_ste");
        subgroup.addOpnode(opnode);

        opnode = new Opnode("2","常用功能2");
        subgroup.addOpnode(opnode);


        subgroup=new Opgroup("基础数据管理");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("初始化功能");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("请货配送");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("零售");
        group.addSubgroup(subgroup);

        subgroup=new Opgroup("库内管理");
        group.addSubgroup(subgroup);

        return group;
    }
}
