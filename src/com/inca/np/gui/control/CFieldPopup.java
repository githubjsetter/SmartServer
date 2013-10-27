package com.inca.np.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:16:51
 * To change this template use File | Settings | File Templates.
 */
public interface CFieldPopup
{
	/**
	 *  Show Popup
	 */
	public void show();

	/**
	 *  Set Value
	 *  @param value
	 */
	public void setValue (Object value);

	/**
	 *  Get Value
	 *  @return value
	 */
	public Object getValue();

	/**
	 *  Set Format
	 *  @param format
	 */
	public void setFormat (Object format);

	/**
	 *  Get Format
	 *  @return format
	 */
	public Object getFormat();

}   //  CFieldPopup
