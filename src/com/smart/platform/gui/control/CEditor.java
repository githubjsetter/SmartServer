package com.smart.platform.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 9:58:07
 * To change this template use File | Settings | File Templates.
 */
public interface CEditor
{
	/**
	 *	Enable Editor
	 *  @param rw true, if you can enter/select data
	 */
	public void setReadWrite (boolean rw);

	/**
	 *	Is it possible to edit
	 *  @return true, if editable
	 */
	public boolean isReadWrite();

	/**
	 *	Set Editor Mandatory
	 *  @param mandatory true, if you have to enter data
	 */
	public void setMandatory (boolean mandatory);

	/**
	 *	Is Field mandatory
	 *  @return true, if mandatory
	 */
	public boolean isMandatory();

	/**
	 *  Set Background based on editable / mandatory / error
	 *  @param error if true, set background to error color, otherwise mandatory/editable
	 */
	public void setBackground (boolean error);

	/**
	 *  Set Visible
	 *  @param visible true if field is to be shown
	 */
	public void setVisible (boolean visible);

	/**
	 *	Set Editor to value
	 *  @param value value of the editor
	 */
	public void setValue (Object value);

	/**
	 *	Return Editor value
	 *  @return current value
	 */
	public Object getValue();

	/**
	 *  Return Display Value
	 *  @return displayed String value
	 */
	public String getDisplay();

}   //  CEditor
