package com.smart.platform.gui.tbar;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class TButtonUIListener  implements MouseListener, MouseMotionListener,
		FocusListener, ChangeListener, PropertyChangeListener {
	private long lastPressedTimestamp = -1;
	private boolean shouldDiscardRelease = false;

	/**
	 * Populates Buttons actions.
	 */
	static void loadActionMap(LazyActionMap map) {
		map.put(new Actions(Actions.PRESS));
		map.put(new Actions(Actions.RELEASE));
	}

	public TButtonUIListener(AbstractButton b) {
	}

	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		if (prop == AbstractButton.MNEMONIC_CHANGED_PROPERTY) {
			updateMnemonicBinding((AbstractButton) e.getSource());
		} else if (prop == AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY) {
			checkOpacity((AbstractButton) e.getSource());
		} else if (prop == AbstractButton.TEXT_CHANGED_PROPERTY
				|| "font" == prop || "foreground" == prop) {
			AbstractButton b = (AbstractButton) e.getSource();
			BasicHTML.updateRenderer(b, b.getText());
		}
	}

	protected void checkOpacity(AbstractButton b) {
		b.setOpaque(b.isContentAreaFilled());
	}

	/**
	 * Register default key actions: pressing space to "click" a button and
	 * registring the keyboard mnemonic (if any).
	 */
	public void installKeyboardActions(JComponent c) {
		AbstractButton b = (AbstractButton) c;
		// Update the mnemonic binding.
		updateMnemonicBinding(b);

		LazyActionMap.installLazyActionMap(c, BasicButtonListener.class,
				"Button.actionMap");

		InputMap km = getInputMap(JComponent.WHEN_FOCUSED, c);

		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, km);
	}

	/**
	 * Unregister's default key actions
	 */
	public void uninstallKeyboardActions(JComponent c) {
		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_IN_FOCUSED_WINDOW,
				null);
		SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, null);
		SwingUtilities.replaceUIActionMap(c, null);
	}

	/**
	 * Returns the InputMap for condition <code>condition</code>. Called as
	 * part of <code>installKeyboardActions</code>.
	 */
	InputMap getInputMap(int condition, JComponent c) {
		if (condition == JComponent.WHEN_FOCUSED) {
			BasicButtonUI ui = (BasicButtonUI) getUIOfType(((AbstractButton) c)
					.getUI(), BasicButtonUI.class);
			if (ui != null) {
				return (InputMap) DefaultLookup.get(c, ui,
						"Button.focusInputMap");
			}
		}
		return null;
	}

	/**
	 * Resets the binding for the mnemonic in the WHEN_IN_FOCUSED_WINDOW UI
	 * InputMap.
	 */
	void updateMnemonicBinding(AbstractButton b) {
		int m = b.getMnemonic();
		if (m != 0) {
			InputMap map = SwingUtilities.getUIInputMap(b,
					JComponent.WHEN_IN_FOCUSED_WINDOW);

			if (map == null) {
				map = new ComponentInputMapUIResource(b);
				SwingUtilities.replaceUIInputMap(b,
						JComponent.WHEN_IN_FOCUSED_WINDOW, map);
			}
			map.clear();
			map.put(KeyStroke.getKeyStroke(m, InputEvent.ALT_MASK, false),
					"pressed");
			map.put(KeyStroke.getKeyStroke(m, InputEvent.ALT_MASK, true),
					"released");
			map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
		} else {
			InputMap map = SwingUtilities.getUIInputMap(b,
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			if (map != null) {
				map.clear();
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		b.repaint();
	}

	public void focusGained(FocusEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		if (b instanceof JButton && ((JButton) b).isDefaultCapable()) {
			JRootPane root = b.getRootPane();
			if (root != null) {
				BasicButtonUI ui = (BasicButtonUI) getUIOfType(
						((AbstractButton) b).getUI(), BasicButtonUI.class);
				if (ui != null
						&& DefaultLookup.getBoolean(b, ui, "Button."
								+ "defaultButtonFollowsFocus", true)) {
					root.putClientProperty("temporaryDefaultButton", b);
					root.setDefaultButton((JButton) b);
					root.putClientProperty("temporaryDefaultButton", null);
				}
			}
		}
		b.repaint();
	}

	public void focusLost(FocusEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		JRootPane root = b.getRootPane();
		if (root != null) {
			JButton initialDefault = (JButton) root
					.getClientProperty("initialDefaultButton");
			if (b != initialDefault) {
				BasicButtonUI ui = (BasicButtonUI) getUIOfType(
						((AbstractButton) b).getUI(), BasicButtonUI.class);
				if (ui != null
						&& DefaultLookup.getBoolean(b, ui, "Button."
								+ "defaultButtonFollowsFocus", true)) {
					root.setDefaultButton(initialDefault);
				}
			}
		}

		ButtonModel model = b.getModel();
		model.setArmed(false);
		model.setPressed(false);

		b.repaint();
	}

	public void mouseMoved(MouseEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		ButtonModel model = b.getModel();
		model.setArmed(true);

	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			AbstractButton b = (AbstractButton) e.getSource();

			if (b.contains(e.getX(), e.getY())) {
				long multiClickThreshhold = b.getMultiClickThreshhold();
				long lastTime = lastPressedTimestamp;
				long currentTime = lastPressedTimestamp = e.getWhen();
				if (lastTime != -1
						&& currentTime - lastTime < multiClickThreshhold) {
					shouldDiscardRelease = true;
					return;
				}

				ButtonModel model = b.getModel();
				if (!model.isEnabled()) {
					// Disabled buttons ignore all input...
					return;
				}
				if (!model.isArmed()) {
					// button not armed, should be
					model.setArmed(true);
				}
				model.setPressed(true);
				if (!b.hasFocus() && b.isRequestFocusEnabled()) {
					b.requestFocus();
				}
			}
		}
	};

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			// Support for multiClickThreshhold
			if (shouldDiscardRelease) {
				shouldDiscardRelease = false;
				return;
			}
			AbstractButton b = (AbstractButton) e.getSource();
			ButtonModel model = b.getModel();
			model.setPressed(false);
			model.setArmed(true);
		}
	};

	public void mouseEntered(MouseEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		ButtonModel model = b.getModel();
		if (b.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(e)) {
			model.setRollover(true);
		}
		//if (model.isPressed())
			model.setArmed(true);
	};

	public void mouseExited(MouseEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		ButtonModel model = b.getModel();
		if (b.isRolloverEnabled()) {
			model.setRollover(false);
		}
		model.setArmed(false);
	};

	/**
	 * Actions for Buttons. Two types of action are supported: pressed: Moves
	 * the button to a pressed state released: Disarms the button.
	 */
	private static class Actions extends UIAction {
		private static final String PRESS = "pressed";
		private static final String RELEASE = "released";

		Actions(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();
			String key = getName();
			if (key == PRESS) {
				ButtonModel model = b.getModel();
				model.setArmed(true);
				model.setPressed(true);
				if (!b.hasFocus()) {
					b.requestFocus();
				}
			} else if (key == RELEASE) {
				ButtonModel model = b.getModel();
				model.setPressed(false);
				model.setArmed(false);
			}
		}

		public boolean isEnabled(Object sender) {
			if (sender != null && (sender instanceof AbstractButton)
					&& !((AbstractButton) sender).getModel().isEnabled()) {
				return false;
			} else {
				return true;
			}
		}
	}

	static class LazyActionMap extends ActionMapUIResource {
		/**
		 * Object to invoke <code>loadActionMap</code> on. This may be a Class
		 * object.
		 */
		private transient Object _loader;

		/**
		 * Installs an ActionMap that will be populated by invoking the
		 * <code>loadActionMap</code> method on the specified Class when
		 * necessary.
		 * <p>
		 * This should be used if the ActionMap can be shared.
		 * 
		 * @param c
		 *            JComponent to install the ActionMap on.
		 * @param loaderClass
		 *            Class object that gets loadActionMap invoked on.
		 * @param defaultsKey
		 *            Key to use to defaults table to check for existing map and
		 *            what resulting Map will be registered on.
		 */

		/**
		 * Returns an ActionMap that will be populated by invoking the
		 * <code>loadActionMap</code> method on the specified Class when
		 * necessary.
		 * <p>
		 * This should be used if the ActionMap can be shared.
		 * 
		 * @param c
		 *            JComponent to install the ActionMap on.
		 * @param loaderClass
		 *            Class object that gets loadActionMap invoked on.
		 * @param defaultsKey
		 *            Key to use to defaults table to check for existing map and
		 *            what resulting Map will be registered on.
		 */
		static ActionMap getActionMap(Class loaderClass, String defaultsKey) {
			ActionMap map = (ActionMap) UIManager.get(defaultsKey);
			if (map == null) {
				map = new LazyActionMap(loaderClass);
				UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
			}
			return map;
		}

		private LazyActionMap(Class loader) {
			_loader = loader;
		}

		public void put(Action action) {
			put(action.getValue(Action.NAME), action);
		}

		public void put(Object key, Action action) {
			loadIfNecessary();
			super.put(key, action);
		}

		public Action get(Object key) {
			loadIfNecessary();
			return super.get(key);
		}

		public void remove(Object key) {
			loadIfNecessary();
			super.remove(key);
		}

		public void clear() {
			loadIfNecessary();
			super.clear();
		}

		public Object[] keys() {
			loadIfNecessary();
			return super.keys();
		}

		public int size() {
			loadIfNecessary();
			return super.size();
		}

		public Object[] allKeys() {
			loadIfNecessary();
			return super.allKeys();
		}

		public void setParent(ActionMap map) {
			loadIfNecessary();
			super.setParent(map);
		}

		private void loadIfNecessary() {
			if (_loader != null) {
				Object loader = _loader;

				_loader = null;
				Class klass = (Class) loader;
				try {
					Method method = klass.getDeclaredMethod("loadActionMap",
							new Class[] { LazyActionMap.class });
					method.invoke(klass, new Object[] { this });
				} catch (NoSuchMethodException nsme) {
					assert false : "LazyActionMap unable to load actions "
							+ klass;
				} catch (IllegalAccessException iae) {
					assert false : "LazyActionMap unable to load actions "
							+ iae;
				} catch (InvocationTargetException ite) {
					assert false : "LazyActionMap unable to load actions "
							+ ite;
				} catch (IllegalArgumentException iae) {
					assert false : "LazyActionMap unable to load actions "
							+ iae;
				}
			}
		}

		static void installLazyActionMap(JComponent c, Class loaderClass,
				String defaultsKey) {
			ActionMap map = (ActionMap) UIManager.get(defaultsKey);
			if (map == null) {
				map = new LazyActionMap(loaderClass);
				UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
			}
			SwingUtilities.replaceUIActionMap(c, map);
		}

	}

	static Object getUIOfType(ComponentUI ui, Class klass) {
		if (klass.isInstance(ui)) {
			return ui;
		}
		return null;
	}

}
