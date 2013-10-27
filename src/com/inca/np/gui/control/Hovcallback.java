package com.inca.np.gui.control;


/**
 * Hov�ص��ӿ�
 * @author Administrator
 *
 */
public interface Hovcallback {
	/**
	 * HOV��Ҫ�����κδ���
	 */
	static int CALLBACK_DONOTHING=0;
	
	/**
	 * HOV�ر�
	 */
	static int CALLBACK_DOCLOSE=1;
	
	/**
	 * HOV���������������ݣ����¿�ʼѡHOV
	 */
	static int CALLBACK_DORESET=2;
	
	/**
	 * HOV�����еı���е�CELL�����˱༭
	 * @param row �к�
	 * @param colname ����
	 * @param value �����ֵ
	 * @param hovdbmodel  hov��dbmodel
	 * @return
	 */
	int hovcallback_itemvaluechanged(int hovrow,String hovcolname,String hovvalue ,DBTableModel hovdbmodel);
	
	/**
	 * ��hovҪȷ������ʱ�����ֵ
	 * @param hovrow ��ǰ��
	 * @param hovdbmodel ����
	 * @param hovtable ���
	 * @return true ���ɹ���ֵ����ѡ��
	 */
	boolean hovcallback_checkresult(int hovrow,DBTableModel hovdbmodel,CTable hovtable);
	
	/**
	 * ȷ������
	 * @param hovrow ��ǰ��
	 * @param hovdbmodel ����
	 * @param hovtable ���
	 */
	void hovcallback_ok(int hovrow,DBTableModel hovdbmodel,CTable hovtable);
	
	/**
	 * ȡ������
	 */
	void hovcallback_cancel();

}
