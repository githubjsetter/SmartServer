package com.smart.platform.gui.control;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 10:22:55
 * To change this template use File | Settings | File Templates.
 */
public class CPanel extends JPanel
{
	/**
	 * Creates a new CompierePanel with the specified layout manager
	 * and buffering strategy.
	 * @param layout  the LayoutManager to use
	 * @param isDoubleBuffered  a boolean, true for double-buffering, which
	 *        uses additional memory space to achieve fast, flicker-free updates
	 */
	public CPanel (LayoutManager layout, boolean isDoubleBuffered)
	{
		super (layout, isDoubleBuffered);
		init();
	}   //  CPanel

	/**
	 * Create a new buffered CPanel with the specified layout manager
	 * @param layout  the LayoutManager to use
	 */
	public CPanel (LayoutManager layout)
	{
		super (layout);
		init();
	}   //  CPanel

	/**
	 * Creates a new <code>CPanel</code> with <code>FlowLayout</code>
	 * and the specified buffering strategy.
	 * If <code>isDoubleBuffered</code> is true, the <code>CPanel</code>
	 * will use a double buffer.
	 * @param isDoubleBuffered  a boolean, true for double-buffering, which
	 *        uses additional memory space to achieve fast, flicker-free updates
	 */
	public CPanel (boolean isDoubleBuffered)
	{
		super (isDoubleBuffered);
		init();
	}   //  CPanel

	/**
	 * Creates a new <code>CPanel</code> with a double buffer and a flow layout.
	 */
	public CPanel()
	{
		super ();
		init();
	}   //  CPanel

	/**
	 * Creates a new <code>CPanel</code> with a double buffer and a flow layout.
	 * @param bc Initial Background Color
	 */
/*
	public CPanel(CompiereColor bc)
	{
		this ();
		init();
		setBackgroundColor (bc);
	}   //  CPanel
*/

	/**
	 *  Common init.
	 *  Compiere backround requires that for the base, background is set explictily.
	 *  The additional panels should be transparent.
	 */
	private void init()
	{
		setOpaque(false);	//	transparent
	}   //  init


	/**************************************************************************
	 *  Set Background - ignored by UI -
	 *  @param bg ignored
	 */
	public void setBackground (Color bg)
	{
		if (bg.equals(getBackground()))
			return;
		super.setBackground (bg);
		//  ignore calls from javax.swing.LookAndFeel.installColors(LookAndFeel.java:61)
/*
		if (!Trace.getCallerClass(1).startsWith("javax"))
			setBackgroundColor (new CompiereColor(bg));
*/
	}   //  setBackground

	/**
	 *  Set Background
	 *  @param bg CompiereColor for Background, if null set standard background
	 */
/*
	public void setBackgroundColor (CompiereColor bg)
	{
		if (bg == null)
			bg = CompierePanelUI.getDefaultBackground();
		setOpaque(true);	//	not transparent
		putClientProperty(CompierePLAF.BACKGROUND, bg);
		super.setBackground (bg.getFlatColor());
	}   //  setBackground
*/

	/**
	 *  Get Background
	 *  @return Color for Background
	 */
/*
	public CompiereColor getBackgroundColor ()
	{
		try
		{
			return (CompiereColor)getClientProperty(CompierePLAF.BACKGROUND);
		}
		catch (Exception e)
		{
			System.err.println("CPanel - ClientProperty: " + e.getMessage());
		}
		return null;
	}   //  getBackgroundColor
*/

	/*************************************************************************/

	/**
	 *  Set Tab Hierarchy Level.
	 *  Has only effect, if tabs are on left or right side
	 *
	 *  @param level
	 */
/*
	public void setTabLevel (int level)
	{
		if (level == 0)
			putClientProperty(CompierePLAF.TABLEVEL, null);
		else
			putClientProperty(CompierePLAF.TABLEVEL, new Integer(level));
	}   //  setTabLevel
*/

	/**
	 *  Get Tab Hierarchy Level
	 *  @return Tab Level
	 */
/*
	public int getTabLevel()
	{
		try
		{
			Integer ll = (Integer)getClientProperty(CompierePLAF.TABLEVEL);
			if (ll != null)
				return ll.intValue();
		}
		catch (Exception e)
		{
			System.err.println("ClientProperty: " + e.getMessage());
		}
		return 0;
	}   //  getTabLevel
*/


	/**************************************************************************
	 *  String representation
	 *  @return String representation
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer ("CPanel [");
		sb.append(super.toString());
/*
		CompiereColor bg = getBackgroundColor();
		if (bg != null)
			sb.append(bg.toString());
*/
		sb.append("]");
		return sb.toString();
	}   //  toString

}   //  CPanel
