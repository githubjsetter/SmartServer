package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smart.bi.client.design.ReportcanvasFrame.CbLandscapehandler;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CNumberTextField;

public class PapersetupDlg extends CDialogOkcancel{
	ReportcanvasFrame frm=null;
	protected CNumberTextField textPaperwidth;
	protected CNumberTextField textPaperheight;
	protected JComboBox cbPapername;
	protected CNumberTextField textPapertopmargin;
	protected CNumberTextField textPaperbottommargin;
	protected CNumberTextField textPaperleftmargin;
	protected CNumberTextField textPaperrightmargin;
	private JCheckBox cbLandscape;

	public PapersetupDlg(ReportcanvasFrame frm){
		super(frm,"设置纸张大小",true);
		this.frm=frm;
		init();
		cbPapername.setSelectedIndex(5);
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.HIDE_ON_CLOSE);
	}
	
	void init(){
		Container cp=getContentPane();
		JPanel paperpane=createPaperPane();
		cp.add(paperpane,BorderLayout.CENTER);
		cp.add(createOkcancelPane(),BorderLayout.SOUTH);
	}
	
	JPanel createPaperPane() {
		GridBagLayout g = new GridBagLayout();

		JPanel jp = new JPanel(g);
		int line = 0;
		JLabel lb = new JLabel("纸张");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		String mediasizenames[] = { "自定义", "A0", "A1", "A2", "A3", "A4", "A5",
				"A6", "A7", "A8", "A9", "A10", "B0", "B1", "B2", "B3", "B4",
				"B5", "B6", "B7", "B8", "B9", "B10", };
		cbPapername = new JComboBox(mediasizenames);
		jp.add(cbPapername, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbPapername.addItemListener(new PapernameHandler(cbPapername));

		line++;
		lb = new JLabel("纸张宽(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		//PapersizeDocumentListener posdoclistener = new PapersizeDocumentListener();
		Dimension textfieldsize = new Dimension(60, 27);
		textPaperwidth = new CNumberTextField(1);
		//textPaperwidth.getDocument().addDocumentListener(posdoclistener);
		textPaperwidth.setMinimumSize(textfieldsize);
		textPaperwidth.setPreferredSize(textfieldsize);
		jp.add(textPaperwidth, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("纸张高(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperheight = new CNumberTextField(1);
		//textPaperheight.getDocument().addDocumentListener(posdoclistener);
		textPaperheight.setPreferredSize(textfieldsize);
		textPaperheight.setMinimumSize(textfieldsize);
		jp.add(textPaperheight, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("左边界(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperleftmargin = new CNumberTextField(1);
		textPaperleftmargin.setText("10");
		//textPaperleftmargin.getDocument().addDocumentListener(posdoclistener);
		textPaperleftmargin.setPreferredSize(textfieldsize);
		textPaperleftmargin.setMinimumSize(textfieldsize);
		jp.add(textPaperleftmargin, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("右边界(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperrightmargin = new CNumberTextField(1);
		textPaperrightmargin.setText("10");
		//textPaperrightmargin.getDocument().addDocumentListener(posdoclistener);
		textPaperrightmargin.setPreferredSize(textfieldsize);
		textPaperrightmargin.setMinimumSize(textfieldsize);
		jp.add(textPaperrightmargin, new GridBagConstraints(1, line, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("上边界(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPapertopmargin = new CNumberTextField(1);
		textPapertopmargin.setText("10");
		//textPapertopmargin.getDocument().addDocumentListener(posdoclistener);
		textPapertopmargin.setPreferredSize(textfieldsize);
		textPapertopmargin.setMinimumSize(textfieldsize);
		jp.add(textPapertopmargin, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("下边界(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperbottommargin = new CNumberTextField(0);
		textPaperbottommargin.setText("10");
		//textPaperbottommargin.getDocument().addDocumentListener(posdoclistener);
		textPaperbottommargin.setPreferredSize(textfieldsize);
		textPaperbottommargin.setMinimumSize(textfieldsize);
		jp.add(textPaperbottommargin, new GridBagConstraints(1, line, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		jp.setPreferredSize(new Dimension(180, 200));
		jp.setMaximumSize(new Dimension(180, 200));
		
		line++;
		cbLandscape = new JCheckBox("横向打印");
		jp.add(cbLandscape, new GridBagConstraints(0, line, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));


		return jp;
	}

	class PapernameHandler implements ItemListener {
		JComboBox cbPapername;

		PapernameHandler(JComboBox cbPapername) {
			this.cbPapername = cbPapername;
		}

		public void itemStateChanged(ItemEvent e) {
			String medianame = (String) cbPapername.getSelectedItem();
			MediaSize mediasize = MediaSize.ISO.A4;
			if (medianame.equals("A0")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A0);
			} else if (medianame.equals("A1")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A1);
			} else if (medianame.equals("A2")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A2);
			} else if (medianame.equals("A3")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A3);
			} else if (medianame.equals("A4")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
			} else if (medianame.equals("A5")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A5);
			} else if (medianame.equals("A6")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A6);
			} else if (medianame.equals("A7")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A7);
			} else if (medianame.equals("A8")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A8);
			} else if (medianame.equals("A9")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A9);
			} else if (medianame.equals("A10")) {
				mediasize = MediaSize
						.getMediaSizeForName(MediaSizeName.ISO_A10);
			} else if (medianame.equals("B0")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B0);
			} else if (medianame.equals("B1")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B1);
			} else if (medianame.equals("B2")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B2);
			} else if (medianame.equals("B3")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B3);
			} else if (medianame.equals("B4")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B4);
			} else if (medianame.equals("B5")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B5);
			} else if (medianame.equals("B6")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B6);
			} else if (medianame.equals("B7")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B7);
			} else if (medianame.equals("B8")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B8);
			} else if (medianame.equals("B9")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B9);
			} else if (medianame.equals("B10")) {
				mediasize = MediaSize
						.getMediaSizeForName(MediaSizeName.ISO_B10);
			}
			DecimalFormat dfmt = new DecimalFormat("0.0");
			float wh[] = mediasize.getSize(MediaSize.MM);
			//settingvalue = true;
			textPaperwidth.setText(dfmt.format(wh[0]));
			textPaperheight.setText(dfmt.format(wh[1]));
			//settingvalue = false;
			

		}
	}

	public String getPapername(){
		return (String)cbPapername.getSelectedItem();
	}
	
	public String getPaperwidth(){
		return textPaperwidth.getText();
	}
	public String getPaperheight(){
		return textPaperheight.getText();
	}
	
	public String getTopmargin(){
		return textPapertopmargin.getText();
	}

	public String getBottommargin(){
		return textPaperbottommargin.getText();
	}

	public String getLeftmargin(){
		return textPaperleftmargin.getText();
	}

	public String getRightmargin(){
		return textPaperrightmargin.getText();
	}
	
	public boolean isLandscape(){
		return cbLandscape.isSelected();
	}
	
	/////////////set

	public void setPapername(String s){
		cbPapername.setSelectedItem(s);
	}
	
	public void setPaperwidth(String s){
		textPaperwidth.setText(s);
	}
	public void setPaperheight(String s){
		textPaperheight.setText(s);
	}
	
	public void setTopmargin(String s){
		textPapertopmargin.setText(s);
	}

	public void setBottommargin(String s){
		textPaperbottommargin.setText(s);
	}

	public void setLeftmargin(String s){
		textPaperleftmargin.setText(s);
	}

	public void setRightmargin(String s){
		textPaperrightmargin.setText(s);
	}
	
	public void setLandscape(boolean b){
		cbLandscape.setSelected(b);
	}

}
