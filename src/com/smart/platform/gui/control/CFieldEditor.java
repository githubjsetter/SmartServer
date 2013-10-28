package com.smart.platform.gui.control;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:15:17
 * To change this template use File | Settings | File Templates.
 */
public class CFieldEditor extends JTextField implements ComboBoxEditor
{
	/**
	 *
	 */
	public CFieldEditor()
	{
	}

	/**
	 *  Return the component that should be added to the tree hierarchy
	 *  for this editor
	 */
	public Component getEditorComponent()
	{
		return this;
	}   //  getEditorCimponent

	/**
	 *  Set Editor
	 *  @param anObject
	 */
	public void setItem (Object anObject)
	{
		if (anObject == null)
			setText("");
		else
			setText(anObject.toString());
	}   //  setItem

	/**
	 *  Get edited item
	 *  @return edited text
	 */
	public Object getItem()
	{
		return getText();
	}   //  getItem

	/**
	 *  Returns format Info (for Popup)
	 *  @return format
	 */
	public Object getFormat()
	{
		return null;
	}   //  getEditmask

}   //  CFieldEditor
