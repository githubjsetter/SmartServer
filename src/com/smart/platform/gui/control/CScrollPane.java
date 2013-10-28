package com.smart.platform.gui.control;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:35:05
 * To change this template use File | Settings | File Templates.
 */
public class CScrollPane extends JScrollPane
{

	/**
	 * 	Compiere ScollPane
	 */
	public CScrollPane ()
	{
		this (null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}	//	CScollPane

	/**
	 * 	Compiere ScollPane
	 *	@param vsbPolicy vertical policy
	 *	@param hsbPolicy horizontal policy
	 */
	public CScrollPane (int vsbPolicy, int hsbPolicy)
	{
		this (null, vsbPolicy, hsbPolicy);
	}	//	CScollPane

	/**
	 * 	Compiere ScollPane
	 *	@param view view
	 */
	public CScrollPane (Component view)
	{
		this (view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}	//	CScollPane

	/**
	 * 	Compiere ScollPane
	 *	@param view view
	 *	@param vsbPolicy vertical policy
	 *	@param hsbPolicy horizontal policy
	 */
	public CScrollPane (Component view, int vsbPolicy, int hsbPolicy)
	{
		super (view, vsbPolicy, hsbPolicy);
		//setBackgroundColor(null);
		setOpaque(false);
		getViewport().setOpaque(false);
	}	//	CScollPane


	/**
	 *  Set Background
	 *  @param bg CompiereColor for Background, if null set standard background
	 */
/*
	public void setBackgroundColor (CompiereColor bg)
	{
		if (bg == null)
			bg = CompierePanelUI.getDefaultBackground();
		putClientProperty(CompierePLAF.BACKGROUND, bg);
	//	super.setBackground(bg.getFlatColor());
	//	getViewport().putClientProperty(CompierePLAF.BACKGROUND, bg);
	//	getViewport().setBackground(bg.getFlatColor());
	//	getViewport().setOpaque(true);
	}   //  setBackground
*/

}	//	CScollPane
