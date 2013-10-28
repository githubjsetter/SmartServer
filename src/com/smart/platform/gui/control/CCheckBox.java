package com.smart.platform.gui.control;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 10:06:42
 * To change this template use File | Settings | File Templates.
 */
public class CCheckBox extends JCheckBox 
{
	/**
	 * Creates an initially unselected check box button with no text, no icon.
	 */
	public CCheckBox ()
	{
		super ();
		init();
/*		ButtonModel bm=this.getModel();
		int d;
		d=3;
		this.setModel(new MybuttonModel());
*/	}

	/**
	 * Creates an initially unselected check box with an icon.
	 * 
	 * @param icon
	 *            the Icon image to display
	 */
	public CCheckBox(Icon icon)
	{
		super (icon);
		init();
	}

	/**
	 * Creates a check box with an icon and specifies whether or not it is
	 * initially selected.
	 * 
	 * @param icon
	 *            the Icon image to display
	 * @param selected
	 *            a boolean value indicating the initial selection state. If
	 *            <code>true</code> the check box is selected
	 */
	public CCheckBox(Icon icon, boolean selected)
	{
		super (icon, selected);
		init();
	}

	/**
	 * Creates an initially unselected check box with text.
	 * 
	 * @param text
	 *            the text of the check box.
	 */
	public CCheckBox (String text)
	{
		super (text);
		init();
	}

	/**
	 * Creates a check box where properties are taken from the Action supplied.
	 * 
	 * @param a
	 */
	public CCheckBox(Action a)
	{
		super (a);
		init();
	}

	/**
	 * Creates a check box with text and specifies whether or not it is
	 * initially selected.
	 * 
	 * @param text
	 *            the text of the check box.
	 * @param selected
	 *            a boolean value indicating the initial selection state. If
	 *            <code>true</code> the check box is selected
	 */
	public CCheckBox (String text, boolean selected)
	{
		super (text, selected);
		init();
	}

	/**
	 * Creates an initially unselected check box with the specified text and
	 * icon.
	 * 
	 * @param text
	 *            the text of the check box.
	 * @param icon
	 *            the Icon image to display
	 */
	public CCheckBox(String text, Icon icon)
	{
		super (text, icon, false);
		init();
	}

	/**
	 * Creates a check box with text and icon, and specifies whether or not it
	 * is initially selected.
	 * 
	 * @param text
	 *            the text of the check box.
	 * @param icon
	 *            the Icon image to display
	 * @param selected
	 *            a boolean value indicating the initial selection state. If
	 *            <code>true</code> the check box is selected
	 */
	public CCheckBox (String text, Icon icon, boolean selected)
	{
		super (text, icon, selected);
		init();
	}

	/**
	 * Common Init
	 */
	private void init()
	{
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}

	/** ********************************************************************** */

	/** Mandatory (default false) */
	private boolean m_mandatory = false;
	/** Read-Write */
	private boolean m_readWrite = true;


	/**
	 * Set Editor Mandatory
	 * 
	 * @param mandatory
	 *            true, if you have to enter data
	 */
	public void setMandatory (boolean mandatory)
	{
		m_mandatory = mandatory;
		setBackground(false);
	}   // setMandatory

	/**
	 * Is Field mandatory
	 * 
	 * @return true, if mandatory
	 */
	public boolean isMandatory()
	{
		return m_mandatory;
	}   // isMandatory

	/**
	 * Enable Editor
	 * 
	 * @param rw
	 *            true, if you can enter/select data
	 */
	public void setReadWrite (boolean rw)
	{
		if (super.isEnabled() != rw)
			super.setEnabled (rw);
		setBackground(false);
		m_readWrite = rw;
	}   // setEditable

	/**
	 * Is it possible to edit
	 * 
	 * @return true, if editable
	 */
	public boolean isReadWrite()
	{
		return m_readWrite;
	}   // isEditable

	/**
	 * Set Background based on editable/mandatory/error - ignored -
	 * 
	 * @param error
	 *            if true, set background to error color, otherwise
	 *            mandatory/editable
	 */
	public void setBackground (boolean error)
	{
	}   // setBackground

	/**
	 * Set Background
	 * 
	 * @param bg
	 */
	public void setBackground (Color bg)
	{
		if (bg.equals(getBackground()))
			return;
		super.setBackground(bg);
	}   // setBackground



	/**
	 * Set Editor to value. Interpret Y/N and Boolean
	 * 
	 * @param value
	 *            value of the editor
	 */
	public void setValue (Object value)
	{
		boolean sel = false;
		if (value == null)
			sel = false;
		else if (value.toString().equals("1"))
			sel = true;
		else if (value.toString().equals("0"))
			sel = false;
		else if (value instanceof Boolean)
			sel = ((Boolean)value).booleanValue();
		else
		{
			try
			{
				sel = Boolean.getBoolean(value.toString());
			}
			catch (Exception e)
			{
			}
		}
		this.setSelected(sel);
	}   // setValue

	/**
	 * Return Editor value
	 * 
	 * @return current value as String or Boolean
	 */
	public String getValue()
	{
			return super.isSelected() ? "1" : "0";
	}

	/**
	 * Set Text
	 * 
	 * @param mnemonicLabel
	 *            text
	 */
	public void setText (String mnemonicLabel)
	{
		super.setText (createMnemonic(mnemonicLabel));
	}	// setText

	/**
	 * Create Mnemonics of text containing "&". Based on MS notation of &Help =>
	 * H is Mnemonics Creates ALT_
	 * 
	 * @param text
	 *            test with Mnemonics
	 * @return text w/o &
	 */
	private String createMnemonic(String text)
	{
		if (text == null)
			return text;
		int pos = text.indexOf("&");
		if (pos != -1)					// We have a nemonic
		{
			char ch = text.charAt(pos+1);
			if (ch != ' ')              // &_ - is the & character
			{
				setMnemonic(ch);
				return text.substring(0, pos) + text.substring(pos+1);
			}
		}
		return text;
	}   // createMnemonic

	public void setArmed(boolean armed){
		ButtonModel bm=this.getModel();
		bm.setArmed(armed);
	}
	
	int mousereleasect=-1;
	public void setMousereleasecount(int ct){
		mousereleasect=ct;
	}

	/**
	 * 		BasicTableUI会多发一次mouse事件,所以需要这样处理
class MybuttonModel extends JToggleButton.ToggleButtonModel{
		@Override
		public void setPressed(boolean b) {
			if(!b){
				if(mousereleasect!=0){
					//第一次按mouse不要触发
					super.setPressed(b);
				}else{
		            if (b) {
		                stateMask |= PRESSED;
		            } else {
		                stateMask &= ~PRESSED;
		            }
				}
				mousereleasect++;
			}else{
				super.setPressed(b);
			}
		}
		
		
		
	}
	 */
	
/*	
 * 20090219 删除下面处理. 否则要点击两次
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if(e.getID()==MouseEvent.MOUSE_PRESSED){
			if(mousereleasect==0){
				return;
			}
		}else if(e.getID()==MouseEvent.MOUSE_RELEASED){
			if(mousereleasect==0){
				mousereleasect++;
				return;
			}
		}
		super.processMouseEvent(e);
	}
*/	
	

}   // CCheckBox
