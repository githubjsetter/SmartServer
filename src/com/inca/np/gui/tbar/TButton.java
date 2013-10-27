package com.inca.np.gui.tbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


public class TButton  extends JButton{
	static Color  textColor = new Color(45,70,111);
	static Color  bdcolor = new Color(232,239,249);

	public TButton() {
		super();
		initButton();
	}

	public TButton(Action a) {
		super(a);
		initButton();
	}

	public TButton(Icon icon) {
		super(icon);
		initButton();
	}

	public TButton(String text, Icon icon) {
		super(text);
		initButton();
	}

	public TButton(String text) {
		super(text);
		initButton();
	}

	
	protected void initButton(){
		Font font=new Font("ËÎÌו",Font.PLAIN,12);
		setFont(font);
		setUI(new TButtonUI());
		setForeground(textColor);
		
		setContentAreaFilled(true);
		setBackground(bdcolor);
		setMargin(new Insets(0,0,0,0));
		//setBorder(BorderFactory.createEmptyBorder());
		Border bd =getBorder();
		int m;
		m=3;
		setBorder( BorderFactory.createEmptyBorder());
/*		
		Border newbd=new BorderUIResource.CompoundBorderUIResource(
				   new MetalBorders.ButtonBorder(),
				   new MetalBorders.InternalFrameBorder());
		setBorder(newbd);
*/		
	}

	@Override
	public Dimension getPreferredSize() {
		FontMetrics fm=java.awt.Toolkit.getDefaultToolkit().getFontMetrics(getFont());
		int textlen=fm.charsWidth(getText().toCharArray(), 0, getText().length());
		textlen+=15;
		
		return new Dimension(textlen,20);
	}
	
	
}
