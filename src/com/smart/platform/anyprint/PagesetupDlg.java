package com.smart.platform.anyprint;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.hssf.model.TextboxShape;

import com.smart.platform.anyprint.impl.Parts;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CDialogOkcancel;

/**
 * 纸张 各part高设置
 * 
 * @author Administrator
 * 
 */
public class PagesetupDlg extends CDialogOkcancel {
	Frame frm;
	Printplan plan;

	String mediasizenames[] = { "自定义", "A0", "A1", "A2", "A3", "A4", "A5",
			"A6", "A7", "A8", "A9", "A10", "B0", "B1", "B2", "B3", "B4", "B5",
			"B6", "B7", "B8", "B9", "B10", };

	private CComboBox cbMediasize;

	private JTextField textPaperw;

	private JTextField textPaperh;

	private JTextField textheadh;

	private JTextField textTablebodyh;

	private JTextField textTableheadh;

	private JTextField textTablefooth;

	private JTextField textFootheight;

	DecimalFormat dfmt = new DecimalFormat("0.0");
	private JCheckBox cbTableheadonce;
	private JCheckBox cbForbidtabletaileverypage;
	private JTextField texthmargin;
	private JTextField textvmargin;
	private JCheckBox cbLandscape;

	public PagesetupDlg(Frame frm, Printplan plan) {
		super(frm, "页面设置", true);
		this.frm = frm;
		this.plan = plan;
		init();
		bind();
		localCenter();
	}

	void bind() {
		if (plan == null)
			return;
		Parts parts = plan.getParts();
		cbMediasize.setSelectedItem(parts.getPapername());
		textPaperw.setText(dfmt.format(parts.getPaperwidth()));
		textPaperh.setText(dfmt.format(parts.getPaperheight()));
		cbLandscape.setSelected(parts.isLandscape());
		texthmargin.setText(dfmt.format(parts.getHmargin()));
		textvmargin.setText(dfmt.format(parts.getVmargin()));
		textheadh.setText(String.valueOf(parts.getHead().getHeight()));
		textTableheadh
				.setText(String.valueOf(parts.getTablehead().getHeight()));
		textTablebodyh.setText(String.valueOf(parts.getBody().getHeight()));
		textTablefooth
				.setText(String.valueOf(parts.getTablefoot().getHeight()));
		textFootheight.setText(String.valueOf(parts.getFoot().getHeight()));
		cbTableheadonce.setSelected(plan.getParts().isTableheadonce());
		cbForbidtabletaileverypage.setSelected(plan.getParts().isForbidtabletaileverypage());
	}

	void init() {
		GridBagLayout g = new GridBagLayout();
		Container cp = getContentPane();
		cp.setLayout(g);

		JLabel lb;
		lb = new JLabel("纸张类型");
		int y = 0;
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		cbMediasize = new CComboBox(mediasizenames);
		cbMediasize.addItemListener(new Cblistener());
		addEnterkeyTraver(cbMediasize);
		cp.add(cbMediasize, new GridBagConstraints(1, y, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("纸张宽");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textPaperw = new JTextField(6);
		addEnterkeyTraver(textPaperw);
		cp.add(textPaperw, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		lb = new JLabel("MM");
		cp.add(lb, new GridBagConstraints(2, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("纸张高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textPaperh = new JTextField(6);
		addEnterkeyTraver(textPaperh);
		cp.add(textPaperh, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		lb = new JLabel("MM");
		cp.add(lb, new GridBagConstraints(2, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		//横向打印
		y++;
		cbLandscape = new JCheckBox("横向打印");
		cp.add(cbLandscape, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		
		
		y++;
		lb = new JLabel("左右留白");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		texthmargin = new JTextField(6);
		addEnterkeyTraver(texthmargin);
		cp.add(texthmargin, new GridBagConstraints(1, y, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		lb = new JLabel("MM");
		cp.add(lb, new GridBagConstraints(2, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("上下留白");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textvmargin = new JTextField(6);
		addEnterkeyTraver(textvmargin);
		cp.add(textvmargin, new GridBagConstraints(1, y, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		lb = new JLabel("MM");
		cp.add(lb, new GridBagConstraints(2, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		
		y++;
		lb = new JLabel("页头高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textheadh = new JTextField(6);
		addEnterkeyTraver(textheadh);
		cp.add(textheadh, new GridBagConstraints(1, y, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("表头高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textTableheadh = new JTextField(6);
		addEnterkeyTraver(textTableheadh);
		cp.add(textTableheadh, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		y++;
		lb = new JLabel("表行高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textTablebodyh = new JTextField(6);
		addEnterkeyTraver(textTablebodyh);
		cp.add(textTablebodyh, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("表尾高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textTablefooth = new JTextField(6);
		addEnterkeyTraver(textTablefooth);
		cp.add(textTablefooth, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		lb = new JLabel("页脚高");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		textFootheight = new JTextField(6);
		addEnterkeyTraver(textFootheight);
		cp.add(textFootheight, new GridBagConstraints(1, y, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));

		y++;
		cbTableheadonce = new JCheckBox("表头只打一次（零售小票)");
		cp.add(cbTableheadonce, new GridBagConstraints(0, y, 3, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		
		y++;
		cbForbidtabletaileverypage= new JCheckBox("不要每页都打印表尾");
		cp.add(cbForbidtabletaileverypage, new GridBagConstraints(0, y, 3, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						2, 2, 2), 0, 0));
		
		
		y++;
		JPanel jp = createOkcancelPane();
		cp.add(jp, new GridBagConstraints(0, y, 3, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						2, 2, 2, 2), 0, 0));

	}

	class Cblistener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			MediaSize mediasize = null;
			String medianame = (String) cbMediasize.getSelectedItem();
			if (medianame.equals("自定义")) {
				textPaperw.setEditable(true);
				textPaperh.setEditable(true);
				return;
			}

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
			float wh[] = mediasize.getSize(MediaSize.MM);
			textPaperw.setText(dfmt.format(wh[0]));

			textPaperh.setText(dfmt.format(wh[1]));
			textPaperw.setEditable(false);
			textPaperh.setEditable(false);

		}

	}

	@Override
	protected void onOk() {
		try {
			Parts parts = plan.getParts();
			parts.setPapername((String) cbMediasize.getSelectedItem());
			parts.setPaperwidth(Float.parseFloat(textPaperw.getText()));
			parts.setPaperheight(Float.parseFloat(textPaperh.getText()));
			parts.setLandscape(cbLandscape.isSelected());
			parts.setHmargin(Float.parseFloat(texthmargin.getText()));
			parts.setVmargin(Float.parseFloat(textvmargin.getText()));
			parts.getHead().setHeight(Integer.parseInt(textheadh.getText()));
			parts.getTablehead().setHeight(
					Integer.parseInt(textTableheadh.getText()));
			parts.getBody().setHeight(
					Integer.parseInt(textTablebodyh.getText()));
			parts.getTablefoot().setHeight(
					Integer.parseInt(textTablefooth.getText()));
			parts.getFoot().setHeight(
					Integer.parseInt(textFootheight.getText()));
			parts.setTableheadonce(cbTableheadonce.isSelected());
			parts.setForbidtabletaileverypage(cbForbidtabletaileverypage.isSelected());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "请输入数字");
			return;
		}

		super.onOk();
	}

	public static void main(String[] args) {
		PagesetupDlg dlg = new PagesetupDlg(null, null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
