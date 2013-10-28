package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:27:15
 * To change this template use File | Settings | File Templates.
 */
public class CPassword extends JPasswordField implements CEditor
{
	/**
	 * Constructs a new <code>JPasswordField</code>,
	 * with a default document, <code>null</code> starting
	 * text string, and 0 column width.
	 */
	public CPassword()
	{
		super();
		init();
	}

	/**
	 * Constructs a new <code>JPasswordField</code> initialized
	 * with the specified text.  The document model is set to the
	 * default, and the number of columns to 0.
	 *
	 * @param text the text to be displayed, <code>null</code> if none
	 */
	public CPassword (String text)
	{
		super (text);
		init();
	}

	/**
	 * Constructs a new empty <code>JPasswordField</code> with the specified
	 * number of columns.  A default model is created, and the initial string
	 * is set to <code>null</code>.
	 *
	 * @param columns the number of columns >= 0
	 */
	public CPassword (int columns)
	{
		super (columns);
		init();
	}

	/**
	 * Constructs a new <code>JPasswordField</code> initialized with
	 * the specified text and columns.  The document model is set to
	 * the default.
	 *
	 * @param text the text to be displayed, <code>null</code> if none
	 * @param columns the number of columns >= 0
	 */
	public CPassword (String text, int columns)
	{
		super (text,columns);
		init();
	}

	/**
	 * Constructs a new <code>JPasswordField</code> that uses the
	 * given text storage model and the given number of columns.
	 * This is the constructor through which the other constructors feed.
	 * The echo character is set to '*'.  If the document model is
	 * <code>null</code>, a default one will be created.
	 *
	 * @param doc  the text storage to use
	 * @param txt the text to be displayed, <code>null</code> if none
	 * @param columns  the number of columns to use to calculate
	 *   the preferred width >= 0; if columns is set to zero, the
	 *   preferred width will be whatever naturally results from
	 *   the component implementation
	 */
	public CPassword (Document doc, String txt, int columns)
	{
		super (doc, txt, columns);
		init();
	}

	/**
	 *  Common Init
	 */
	private void init()
	{
		//setFont(CompierePLAF.getFont_Field());
		//setForeground(CompierePLAF.getTextColor_Normal());
	}   //  init

	/*************************************************************************/

	/** Mandatory (default false)   */
	private boolean m_mandatory = false;

	/**
	 *	Set Editor Mandatory
	 *  @param mandatory true, if you have to enter data
	 */
	public void setMandatory (boolean mandatory)
	{
		m_mandatory = mandatory;
		setBackground(false);
	}   //  setMandatory

	/**
	 *	Is Field mandatory
	 *  @return true, if mandatory
	 */
	public boolean isMandatory()
	{
		return m_mandatory;
	}   //  isMandatory

	/**
	 *	Enable Editor
	 *  @param rw true, if you can enter/select data
	 */
	public void setReadWrite (boolean rw)
	{
		if (super.isEditable() != rw)
			super.setEditable (rw);
		setBackground(false);
	}   //  setEditable

	/**
	 *	Is it possible to edit
	 *  @return true, if editable
	 */
	public boolean isReadWrite()
	{
		return super.isEditable();
	}   //  isReadWrite

	/**
	 *  Set Background based on editable / mandatory / error
	 *  @param error if true, set background to error color, otherwise mandatory/editable
	 */
	public void setBackground (boolean error)
	{
/*
		if (error)
			setBackground(CompierePLAF.getFieldBackground_Error());
		else if (!isReadWrite())
			setBackground(CompierePLAF.getFieldBackground_Inactive());
		else if (m_mandatory)
			setBackground(CompierePLAF.getFieldBackground_Mandatory());
		else
			setBackground(CompierePLAF.getFieldBackground_Normal());
*/
	}   //  setBackground

	/**
	 *  Set Background
	 *  @param bg
	 */
	public void setBackground (Color bg)
	{
		if (bg.equals(getBackground()))
			return;
		super.setBackground(bg);
	}   //  setBackground

	/**
	 *	Set Editor to value
	 *  @param value value of the editor
	 */
	public void setValue (Object value)
	{
		if (value == null)
			setText("");
		else
			setText(value.toString());
	}   //  setValue

	/**
	 *	Return Editor value
	 *  @return current value
	 */
	public Object getValue()
	{
		return new String(super.getPassword());
	}   //  getItemValue

	/**
	 *  Return Display Value
	 *  @return displayed String value
	 */
	public String getDisplay()
	{
		return new String(super.getPassword());
	}   //  getDisplay


}
