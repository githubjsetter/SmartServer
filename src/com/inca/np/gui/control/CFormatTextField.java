package com.inca.np.gui.control;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import java.awt.Color;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-5
 * Time: 16:02:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class CFormatTextField extends JFormattedTextField {
    protected CFormatTextFieldListener changelistener = null;
    protected String oldvalue = "";
    protected boolean canedit = true;

    /**
     * ���ڵ����õ����ơ�
     */
    private String editorname="";
    /**
     * ���س���tab���ܲ��ܵ�����
     */
    protected boolean keyfocusable=true;

    /**
        ͨ�������������ǿ���Ƿ���Թ���뿪������hov
     protected boolean canlosefocus=true;
    */

	/**
	 * �����ɫ
	 */
	public static Color bordercolor=new Color(74, 140, 218);

    protected CFormatTextField() {
    }

    private CFormatterbase formatter=null;
    protected CFormatTextField(CFormatterbase formatter) {
        super(formatter);
        this.formatter=formatter;
        //this.setDisabledTextColor(Color.BLACK);
        this.setInputVerifier(new FormattedTextFieldVerifier());
        setBorder(BorderFactory.createLineBorder(bordercolor, 1));
    }

    public String getEditorname() {
        return editorname;
    }

    public void setEditorname(String editorname) {
        this.editorname = editorname;
    }



    public boolean isCanedit() {
        return canedit;
    }

    public void setCanedit(boolean canedit) {
        this.canedit = canedit;
        formatter.setCanedit(canedit);
    }

    public boolean isKeyfocusable() {
        //return keyfocusable;
    	//20070925 �о�û�б�Ҫ�����������
    	return true;
    }

    public void setKeyfocusable(boolean keyfocusable) {
        this.keyfocusable = keyfocusable;
    }

    /**
     * ����������focus�����뿪
     */
    public class FormattedTextFieldVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            if (input instanceof JFormattedTextField) {
                JFormattedTextField ftf = (JFormattedTextField) input;
                AbstractFormatter formatter = ftf.getFormatter();
                String text = ftf.getText();
                String newvalue="";
                if (formatter != null && text.length() > 0) {
                    try {
                        newvalue = (String) formatter.stringToValue(text);
                    } catch (ParseException pe) {
                        JOptionPane.showMessageDialog(null, pe.getMessage(),
                                "�������", JOptionPane.ERROR_MESSAGE);
                        //System.out.println(editorname+"verify return false");
                        return false;
                    }
                }
                //���ԭֵΪ�գ���ֵΪ�գ�������hov����һ��commitEdit  20070425
                if(oldvalue==null){
                    oldvalue="";
                }

                /* ��ֵҲ����hov
                if(newvalue.length()==0){
                    return true;
                }
                */

                //�����hov������ֵ�仯�ˣ���Ҫ�뿪
                if(changelistener!=null && !oldvalue.equals(newvalue) && changelistener.isHov(editorname)){
                    changelistener.invokeHov(editorname,newvalue,oldvalue);
                    oldvalue=newvalue; //hov������ֵ
                    //System.out.println(editorname+"invokehov,verify return false");
                    if(newvalue.length()==0){
                    	return true;
                    }
                    return false;
                }

                if(changelistener!=null && oldvalue.equals(newvalue) && changelistener.isHov(editorname)){
                	if(newvalue.length()==0){
                		return true;
                	}else{
	                    boolean ret=changelistener.confirmHov(editorname,newvalue,oldvalue);
	                    System.out.println(editorname+"confirmHov,verify return "+ret);
	                    return ret;
                	}
                }

                //System.out.println(editorname+"verify return false");
                return true;
            }
            //System.out.println(editorname+"verify return false");
            return true;
        }

        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }
    }

    public void replaceSelection(String content) {
        if (canedit) {
            super.replaceSelection(content);
        }
    }



    public CFormatTextFieldListener getChangelistener() {
        return changelistener;
    }

    public void setChangelistener(CFormatTextFieldListener changelistener) {
        this.changelistener = changelistener;
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setValue(Object value) {
        super.setValue(value);
        oldvalue=(String)value;
    }

    public void commitEdit() throws ParseException {
        String value = this.getText();
        if (changelistener != null) {
            if (value == null) value = "";
            if (oldvalue == null) oldvalue = "";

            if(changelistener!=null && !oldvalue.equals(value) && changelistener.isHov(editorname)){
/*                if(value.equals("")){
                    changelistener.cancelHov(editorname,value,oldvalue);
                    oldvalue=value;
                    return;
                }
*/
                oldvalue=value; //������oldvalue
            	changelistener.invokeHov(editorname,value,oldvalue);
                return ;
            }

            if(changelistener!=null && oldvalue.equals(value) && changelistener.isHov(editorname)){
                if(value.equals("")){
                    changelistener.cancelHov(editorname,value,oldvalue);
                    oldvalue=value;
                    return;
                }
                boolean ret=changelistener.confirmHov(editorname,value,oldvalue);
                if(ret){
                    super.commitEdit();
                }
                oldvalue=value;
                return ;
            }else{
                super.commitEdit();
            }

/*
            //��������20070425
            if(value.equals(oldvalue)){
                return;
            }
*/
            if(canedit){
            	changelistener.onchanged(this, (String) value, oldvalue);
            }

        }else{
            super.commitEdit();
            oldvalue=value;

        }
    }
    
    Color darkbackgroud=new Color(234,234,234);

	@Override
	public Color getBackground() {
		if(!canedit || !isEnabled()){
			return darkbackgroud;
		}else{
			return super.getBackground();
		}
	}

	protected DocumentListener doclistener;
	public void addDoclistener(DocumentListener doclistener){
		this.doclistener=doclistener;
		getDocument().addDocumentListener(doclistener);
	}
	/**
	 * �ͷ��ڴ�. ɾ��document listener
	 */
	public void freeMemory(){
		getDocument().removeDocumentListener(doclistener);
		doclistener=null;
	}
    
}
