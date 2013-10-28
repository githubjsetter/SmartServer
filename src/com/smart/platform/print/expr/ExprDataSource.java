package com.smart.platform.print.expr;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-25
 * Time: 17:47:13
 * To change this template use File | Settings | File Templates.
 */
public interface ExprDataSource {
    /**
     *  ȡ��ǰ��ҳ
     */
    //PageInfo getCurrentPage();

    /**
     * ȡ��ҳ��
     */
    int getPageCount();

    /**
     * ȡһ������
     * @param paramname ������
     * @return ����ֵ
     * @throws Exception
     */
    String getParam(String paramname)throws Exception;

    /**
     * ȡ����ֵ
     * @param row ��
     * @param colname ����
     * @return
     * @throws Exception
     */
    String getData(int row,String colname)throws Exception;

    /**
     * ȡ��ʾ��������
     * @param colname
     * @return
     * @throws Exception
     */
    //TextReportColumn getBodyColumn(String colname)throws Exception;

    /**
     * ȡ�����еĸߡ�
     * @return
     */
    int getRowHeight();

    /**
     * ��ĳ�еĺϼ�
     * @param colname
     * @return
     */
    String calcSum(String colname);

    /**
     * ��ĳ���С��
     * @param row  ��
     * @param groupno  ����Ŵӣ���
     * @param colname ����
     * @return
     */
    String calcGroupSum(int row,int groupno,String colname);

    /**
     *  ��ĳҳС��
     * @param startrow
     * @param endrow
     * @param colname
     * @return
     */
    String calcPageSum(int startrow,int endrow,String colname);

    /**
     * ȡ��ǰ���
     * @return
     */
    int getCurrentGroupNo();
}
