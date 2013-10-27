package com.inca.np.gui.control;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:07:23
 * To change this template use File | Settings | File Templates.
 */
public class CCheckBoxMenuItem extends JCheckBoxMenuItem
{

	public CCheckBoxMenuItem ()
	{
		super ();
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (Icon icon)
	{
		super (icon);
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (String text)
	{
		super (text);
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (Action a)
	{
		super (a);
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (String text, Icon icon)
	{
		super (text, icon);
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (String text, boolean b)
	{
		super (text, b);
	}	//	CCheckBoxMenuItem

	public CCheckBoxMenuItem (String text, Icon icon, boolean b)
	{
		super (text, icon, b);
	}	//	CCheckBoxMenuItem

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

}	//	CCheckBoxMenuItem
