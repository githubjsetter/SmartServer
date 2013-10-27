package com.inca.npx.ste;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;

/**
 * 操作Ap的接口
 * 
 * @author Administrator
 * 
 */
public interface ApIF {

	/**
	 * 开发授权属性,能否新增
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCannew();

	/**
	 * 开发授权属性,能否删除
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCandelete();

	/**
	 * 开发授权属性,能否查询
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCanquery();

	/**
	 * 开发授权属性,能否修改
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCanmodify();

	/**
	 * 开发授权属性,能否保存
	 * 
	 * @return true 可以
	 */
	public boolean isDevelopCansave();

	/**
	 * 取授权属性定义
	 * 
	 * @param apname
	 * @return Apinfo
	 */
	public Apinfo getApinfo(String apname);

	/**
	 * 取授权属性值
	 * 
	 * @param apname
	 * @return
	 */
	public String getApvalue(String apname);

	/**
	 * 返回可设置的参数类型的授权属性的定义
	 * 
	 * @return
	 */
	public Vector<Apinfo> getParamapinfos();

	/**
	 * 返回所有支持的打印方案
	 * 
	 * @return
	 */
	public Vector<String> getPrintplans();

	/**
	 * 设置所有支持的打印方案
	 * 
	 * @param plans
	 */
	public void setPrintplans(Vector<String> plans);

	/**
	 * 返回自动打印方案名
	 * 
	 * @return
	 */
	public String getAutoprintplan();

	/**
	 * 设置自动打印方案
	 * 
	 * @param planname
	 * @return
	 */
	public void setAutoprintplan(String planname);
	
	
	public Vector<DBColumnDisplayInfo> getDBColumnDisplayInfos();

	public Vector<DBColumnDisplayInfo> loadOrgDBmodeldefine();

}
