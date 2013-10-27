package com.inca.np.gui.control;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:21:12
 * To change this template use File | Settings | File Templates.
 */
public class CMenuItem extends JMenuItem
{

	public CMenuItem ()
	{
		super ();
	}	//	CMenuItem

	public CMenuItem (Icon icon)
	{
		super (icon);
	}	//	CMenuItem

	public CMenuItem (String text)
	{
		super (text);
	}	//	CMenuItem

	public CMenuItem (Action a)
	{
		super (a);
	}	//	CMenuItem

	public CMenuItem (String text, Icon icon)
	{
		super (text, icon);
	}	//	CMenuItem

	public CMenuItem (String text, int mnemonic)
	{
		super (text, mnemonic);
	}	//	CMenuItem

	/**
	 * 	Set Text
	 *	@param text text
	 */
	public void setText (String text)
	{
		if (text == null)
		{
			super.setText(text);
			return;
		}
		int pos = text.indexOf("&");
		if (pos != -1 && text.length() > pos)	//	We have a nemonic - creates ALT-_
		{
			int mnemonic = text.toUpperCase().charAt(pos+1);
			if (mnemonic != ' ')
			{
				setMnemonic(mnemonic);
				text = text.substring(0, pos) + text.substring(pos+1);
			}
		}
		super.setText (text);
		if (getName() == null)
			setName (text);
	}	//	setText

}	//	CMenuItem
