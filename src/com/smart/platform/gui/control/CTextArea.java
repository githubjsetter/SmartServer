package com.smart.platform.gui.control;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class CTextArea extends JScrollPane {
	JTextAreaExtend textarea=new JTextAreaExtend();
	boolean canedit=true;

	public CTextArea() {
		super();
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		Document doc=textarea.getDocument();
		textarea.setDocument(new TextareaDocument());
		super.setViewportView(textarea);
        setBorder(BorderFactory.createLineBorder(CFormatTextField.bordercolor, 1));
	}
	
	public InputMap getTextareaInputMap(){
		return textarea.getInputMap();
	}
	
	public ActionMap getTextareaActionmap(){
		return textarea.getActionMap();
	}
	
	public boolean isCanedit() {
		return canedit;
	}



	public void setCanedit(boolean canedit) {
		textarea.setCanedit(canedit);
		this.canedit = canedit;
	}



	public void setText(String s){
		boolean oldv=canedit;
		canedit=true;
		textarea.setText(s);
		setCanedit(oldv);
	}
	
	public String getText(){
		return textarea.getText();
	}

	public JTextArea getTextarea() {
		return textarea;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		textarea.setEnabled(enabled);
	}
	

	
	@Override
	public void requestFocus() {
		textarea.requestFocus();
	}
	
	@Override
	public boolean isFocusOwner(){
		return textarea.isFocusOwner();
	}

	@Override
	public boolean isEnabled() {
		return textarea.isEnabled();
	}



	class TextareaDocument extends PlainDocument{

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if(!canedit)return;
			super.insertString(offs, str, a);
		}

		@Override
		protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
			if(!canedit)return;
			super.insertUpdate(chng, attr);
		}

		@Override
		protected void removeUpdate(DefaultDocumentEvent chng) {
			if(!canedit)return;
			super.removeUpdate(chng);
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			if(!canedit)return;
			super.remove(offs, len);
		}
		
		
		
	}

	DocumentListener doclistener;
	public void addDoclistener(DocumentListener doclistener){
		this.doclistener=doclistener;
		getTextarea().getDocument().addDocumentListener(doclistener);
	}
	/**
	 * ÊÍ·ÅÄÚ´æ. É¾µôdocument listener
	 */
	public void freeMemory(){
		getTextarea().getDocument().removeDocumentListener(doclistener);
		doclistener=null;
	}


}
