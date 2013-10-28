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
     *  取当前的页
     */
    //PageInfo getCurrentPage();

    /**
     * 取总页数
     */
    int getPageCount();

    /**
     * 取一个参数
     * @param paramname 参数名
     * @return 参数值
     * @throws Exception
     */
    String getParam(String paramname)throws Exception;

    /**
     * 取数据值
     * @param row 行
     * @param colname 列名
     * @return
     * @throws Exception
     */
    String getData(int row,String colname)throws Exception;

    /**
     * 取显示的数据列
     * @param colname
     * @return
     * @throws Exception
     */
    //TextReportColumn getBodyColumn(String colname)throws Exception;

    /**
     * 取数据行的高。
     * @return
     */
    int getRowHeight();

    /**
     * 算某列的合计
     * @param colname
     * @return
     */
    String calcSum(String colname);

    /**
     * 算某组的小计
     * @param row  行
     * @param groupno  　组号从１起
     * @param colname 列名
     * @return
     */
    String calcGroupSum(int row,int groupno,String colname);

    /**
     *  算某页小计
     * @param startrow
     * @param endrow
     * @param colname
     * @return
     */
    String calcPageSum(int startrow,int endrow,String colname);

    /**
     * 取当前组号
     * @return
     */
    int getCurrentGroupNo();
}
