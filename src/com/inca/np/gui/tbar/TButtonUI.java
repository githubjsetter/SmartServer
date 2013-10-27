package com.inca.np.gui.tbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import com.inca.np.util.SwingUtilities2;


//import com.sun.java.swing.SwingUtilities2;

public class TButtonUI extends ButtonUI {
	Color bordercolor = new Color(202,211,233);
	Color pressedbordercolor = new Color(168,176,200);
	Color highlightbg = new Color(254,254,254);

	
	// Shared UI object
	private final static TButtonUI buttonUI = new TButtonUI();

	// Visual constants
	// NOTE: This is not used or set any where. Were we allowed to remove
	// fields, this would be removed.
	protected int defaultTextIconGap;

	// Amount to offset text, the value of this comes from
	// defaultTextShiftOffset once setTextShiftOffset has been invoked.
	private int shiftOffset = 0;
	// Value that is set in shiftOffset once setTextShiftOffset has been
	// invoked. The value of this comes from the defaults table.
	protected int defaultTextShiftOffset;

	private final static String propertyPrefix = "Button" + ".";

	// ********************************
	// Create PLAF
	// ********************************
	public static ComponentUI createUI(JComponent c) {
		return buttonUI;
	}

	protected String getPropertyPrefix() {
		return propertyPrefix;
	}

	// ********************************
	// Install PLAF
	// ********************************
	public void installUI(JComponent c) {
		installDefaults((AbstractButton) c);
		installListeners((AbstractButton) c);
		installKeyboardActions((AbstractButton) c);
		BasicHTML.updateRenderer(c, ((AbstractButton) c).getText());
	}

	protected void installDefaults(AbstractButton b) {
		// load shared instance defaults
		String pp = getPropertyPrefix();

		defaultTextShiftOffset = UIManager.getInt(pp + "textShiftOffset");

		// set the following defaults on the button
		if (b.isContentAreaFilled()) {
			LookAndFeel.installProperty(b, "opaque", Boolean.TRUE);
		} else {
			LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
		}

		if (b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
			b.setMargin(UIManager.getInsets(pp + "margin"));
		}

		LookAndFeel.installColorsAndFont(b, pp + "background", pp
				+ "foreground", pp + "font");
		LookAndFeel.installBorder(b, pp + "border");

		Object rollover = UIManager.get(pp + "rollover");
		if (rollover != null) {
			LookAndFeel.installProperty(b, "rolloverEnabled", rollover);
		}

		LookAndFeel.installProperty(b, "iconTextGap", new Integer(4));
	}

	protected void installListeners(AbstractButton b) {
		TButtonUIListener listener = createButtonListener(b);
		if (listener != null) {
			b.addMouseListener(listener);
			b.addMouseMotionListener(listener);
			b.addFocusListener(listener);
			b.addPropertyChangeListener(listener);
			b.addChangeListener(listener);
		}
	}

	protected void installKeyboardActions(AbstractButton b) {
		TButtonUIListener listener = getButtonListener(b);

		if (listener != null) {
			listener.installKeyboardActions(b);
		}
	}

	// ********************************
	// Uninstall PLAF
	// ********************************
	public void uninstallUI(JComponent c) {
		uninstallKeyboardActions((AbstractButton) c);
		uninstallListeners((AbstractButton) c);
		uninstallDefaults((AbstractButton) c);
		BasicHTML.updateRenderer(c, "");
	}

	protected void uninstallKeyboardActions(AbstractButton b) {
		TButtonUIListener listener = getButtonListener(b);
		if (listener != null) {
			listener.uninstallKeyboardActions(b);
		}
	}

	protected void uninstallListeners(AbstractButton b) {
		TButtonUIListener listener = getButtonListener(b);
		if (listener != null) {
			b.removeMouseListener(listener);
			b.removeMouseMotionListener(listener);
			b.removeFocusListener(listener);
			b.removeChangeListener(listener);
			b.removePropertyChangeListener(listener);
		}
	}

	protected void uninstallDefaults(AbstractButton b) {
		LookAndFeel.uninstallBorder(b);
	}

	// ********************************
	// Create Listeners
	// ********************************
	protected TButtonUIListener createButtonListener(AbstractButton b) {
		return new TButtonUIListener(b);
	}

	public int getDefaultTextIconGap(AbstractButton b) {
		return defaultTextIconGap;
	}

	/*
	 * These rectangles/insets are allocated once for all ButtonUI.paint()
	 * calls. Re-using rectangles rather than allocating them in each paint call
	 * substantially reduced the time it took paint to run. Obviously, this
	 * method can't be re-entered.
	 */
	private static Rectangle viewRect = new Rectangle();
	private static Rectangle textRect = new Rectangle();
	private static Rectangle iconRect = new Rectangle();


	// ********************************
	// Paint Methods
	// ********************************

	public void paint(Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		String text = layout(b, SwingUtilities2.getFontMetrics(b, g), b
				.getWidth(), b.getHeight());

		clearTextShiftOffset();

		if (model.isArmed()) {
			paintBorder(g, b);
		}

		// perform UI specific press action, e.g. Windows L&F shifts text
		if (model.isArmed() && model.isPressed()) {
			paintButtonPressed(g, b);
		}

		// Paint the Icon
		if (b.getIcon() != null) {
			paintIcon(g, c, iconRect);
		}

		if (text != null && !text.equals("")) {
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				v.paint(g, textRect);
			} else {
				paintText(g, b, textRect, text);
			}
		}

		if (b.isFocusPainted() && b.hasFocus()) {
			// paint UI specific focus
			paintFocus(g, b, viewRect, textRect, iconRect);
		}
	}

	void paintBorder(Graphics g, AbstractButton b) {
		int arcwidth = 8;
		int pressedoffset = 1;
		if (b.getModel().isPressed()) {
			pressedoffset = 1;
		} else {
			pressedoffset = 0;
		}
		g.setColor(highlightbg);
		g.fillRoundRect(pressedoffset, pressedoffset, 
				b.getWidth() - 1 - 2 * pressedoffset, b.getHeight()
				- 1 - 2 *pressedoffset, arcwidth, arcwidth);
		if (b.getModel().isPressed()) {
			g.setColor(pressedbordercolor);
		} else {
			g.setColor(bordercolor);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.drawRoundRect(pressedoffset, pressedoffset, b.getWidth() - 1 - 2* pressedoffset, b
				.getHeight()
				- 1 - 2 * pressedoffset, arcwidth, arcwidth);

	}

	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		Icon icon = b.getIcon();
		Icon tmpIcon = null;

		if (icon == null) {
			return;
		}

		Icon selectedIcon = null;

		/* the fallback icon should be based on the selected state */
		if (model.isSelected()) {
			selectedIcon = (Icon) b.getSelectedIcon();
			if (selectedIcon != null) {
				icon = selectedIcon;
			}
		}

		if (!model.isEnabled()) {
			if (model.isSelected()) {
				tmpIcon = (Icon) b.getDisabledSelectedIcon();
				if (tmpIcon == null) {
					tmpIcon = selectedIcon;
				}
			}

			if (tmpIcon == null) {
				tmpIcon = (Icon) b.getDisabledIcon();
			}
		} else if (model.isPressed() && model.isArmed()) {
			tmpIcon = (Icon) b.getPressedIcon();
			if (tmpIcon != null) {
				// revert back to 0 offset
				clearTextShiftOffset();
			}
		} else if (b.isRolloverEnabled() && model.isRollover()) {
			if (model.isSelected()) {
				tmpIcon = (Icon) b.getRolloverSelectedIcon();
				if (tmpIcon == null) {
					tmpIcon = selectedIcon;
				}
			}

			if (tmpIcon == null) {
				tmpIcon = (Icon) b.getRolloverIcon();
			}
		}

		if (tmpIcon != null) {
			icon = tmpIcon;
		}

		if (model.isPressed() && model.isArmed()) {
			icon.paintIcon(c, g, iconRect.x + getTextShiftOffset(), iconRect.y
					+ getTextShiftOffset());
		} else {
			icon.paintIcon(c, g, iconRect.x, iconRect.y);
		}

	}

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;                       
        ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

	/* Draw the Text */
	if(model.isEnabled()) {
	    /*** paint the text normally */
	    g.setColor(b.getForeground());
	    SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex,
					  textRect.x + getTextShiftOffset(),
					  textRect.y + fm.getAscent() + getTextShiftOffset());
	}
	else {
	    /*** paint the text disabled ***/
	    g.setColor(b.getBackground().brighter());
	    SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex,
					  textRect.x, textRect.y + fm.getAscent());
	    g.setColor(b.getBackground().darker());
	    SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex,
					  textRect.x - 1, textRect.y + fm.getAscent() - 1);
	}
    }

	/**
	 * Method which renders the text of the current button.
	 * <p>
	 * 
	 * @param g
	 *            Graphics context
	 * @param b
	 *            Current button to render
	 * @param textRect
	 *            Bounding rectangle to render the text.
	 * @param text
	 *            String to render
	 * @since 1.4
	 */
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
			String text) {
		paintText(g, (JComponent) b, textRect, text);
	}

	// Method signature defined here overriden in subclasses.
	// Perhaps this class should be abstract?
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
			Rectangle textRect, Rectangle iconRect) {
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		paintBorder(g, b);
	}

	protected void clearTextShiftOffset() {
		this.shiftOffset = 0;
	}

	protected void setTextShiftOffset() {
		this.shiftOffset = defaultTextShiftOffset;
	}

	protected int getTextShiftOffset() {
		return shiftOffset;
	}

	// ********************************
	// Layout Methods
	// ********************************
	public Dimension getMinimumSize(JComponent c) {
		Dimension d = getPreferredSize(c);
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null) {
			d.width -= v.getPreferredSpan(View.X_AXIS)
					- v.getMinimumSpan(View.X_AXIS);
		}
		return d;
	}

	public Dimension getPreferredSize(JComponent c) {
		AbstractButton b = (AbstractButton) c;
		return BasicGraphicsUtils.getPreferredButtonSize(b, b.getIconTextGap());
	}

	public Dimension getMaximumSize(JComponent c) {
		Dimension d = getPreferredSize(c);
		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null) {
			d.width += v.getMaximumSpan(View.X_AXIS)
					- v.getPreferredSpan(View.X_AXIS);
		}
		return d;
	}



	/**
	 * Returns an enum indicating how the baseline of the component changes as
	 * the size changes.
	 * 
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @see javax.swing.JComponent#getBaseline(int, int)
	 * @since 1.6
	public Component.BaselineResizeBehavior getBaselineResizeBehavior(
			JComponent c) {
		super.getBaselineResizeBehavior(c);
		if (c.getClientProperty(BasicHTML.propertyKey) != null) {
			return Component.BaselineResizeBehavior.OTHER;
		}
		switch (((AbstractButton) c).getVerticalAlignment()) {
		case AbstractButton.TOP:
			return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
		case AbstractButton.BOTTOM:
			return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
		case AbstractButton.CENTER:
			return Component.BaselineResizeBehavior.CENTER_OFFSET;
		}
		return Component.BaselineResizeBehavior.OTHER;
	}
	 */

	private String layout(AbstractButton b, FontMetrics fm, int width,
			int height) {
		Insets i = b.getInsets();
		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = width - (i.right + viewRect.x);
		viewRect.height = height - (i.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		// layout the text and icon
		return SwingUtilities.layoutCompoundLabel(b, fm, b.getText(), b
				.getIcon(), b.getVerticalAlignment(), b
				.getHorizontalAlignment(), b.getVerticalTextPosition(), b
				.getHorizontalTextPosition(), viewRect, iconRect, textRect, b
				.getText() == null ? 0 : b.getIconTextGap());
	}

	/**
	 * Returns the ButtonListener for the passed in Button, or null if one could
	 * not be found.
	 */
	private TButtonUIListener getButtonListener(AbstractButton b) {
		MouseMotionListener[] listeners = b.getMouseMotionListeners();

		if (listeners != null) {
			for (int counter = 0; counter < listeners.length; counter++) {
				if (listeners[counter] instanceof TButtonUIListener) {
					return (TButtonUIListener) listeners[counter];
				}
			}
		}
		return null;
	}

}
