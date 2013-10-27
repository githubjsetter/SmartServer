package com.inca.np.gui.control;


/**
 * Hov回调接口
 * @author Administrator
 *
 */
public interface Hovcallback {
	/**
	 * HOV不要进行任何处理
	 */
	static int CALLBACK_DONOTHING=0;
	
	/**
	 * HOV关闭
	 */
	static int CALLBACK_DOCLOSE=1;
	
	/**
	 * HOV清除现在输入的数据，重新开始选HOV
	 */
	static int CALLBACK_DORESET=2;
	
	/**
	 * HOV窗口中的表格中的CELL进行了编辑
	 * @param row 行号
	 * @param colname 列名
	 * @param value 输入的值
	 * @param hovdbmodel  hov的dbmodel
	 * @return
	 */
	int hovcallback_itemvaluechanged(int hovrow,String hovcolname,String hovvalue ,DBTableModel hovdbmodel);
	
	/**
	 * 当hov要确定返回时，检查值
	 * @param hovrow 当前行
	 * @param hovdbmodel 数据
	 * @param hovtable 表格
	 * @return true 检查成功，值可以选择
	 */
	boolean hovcallback_checkresult(int hovrow,DBTableModel hovdbmodel,CTable hovtable);
	
	/**
	 * 确定返回
	 * @param hovrow 当前行
	 * @param hovdbmodel 数据
	 * @param hovtable 表格
	 */
	void hovcallback_ok(int hovrow,DBTableModel hovdbmodel,CTable hovtable);
	
	/**
	 * 取消返回
	 */
	void hovcallback_cancel();

}
