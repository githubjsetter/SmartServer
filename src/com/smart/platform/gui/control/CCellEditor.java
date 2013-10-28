package com.smart.platform.gui.control;

import javax.swing.table.TableCellEditor;
import javax.swing.*;
import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-4
 * Time: 15:21:33
 * To change this template use File | Settings | File Templates.
 */
public class CCellEditor extends DefaultCellEditor{
    public CCellEditor(final JTextField textField) {
        super(textField);
    }

    public CCellEditor(final JCheckBox checkBox) {
        super(checkBox);
    }

    public CCellEditor(final JComboBox comboBox) {
        super(comboBox);
    }

    
}
