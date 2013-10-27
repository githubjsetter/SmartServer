package com.inca.npbi.client.design;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.GroupDBTableModel;
import com.inca.np.gui.control.SplitGroupInfo;
import com.inca.npbi.client.design.param.BIReportparamdefine;

/**
 * ��ֱ�������
 * 
 * @author user
 * 
 */
public class BITableV_Render implements ReportcanvasPlaceableIF,
		ReportcalcerDatasourceIF, TabledefineChangedIF {

	BITableV_def tablevdef = null;
	static Dimension defaultsize = new Dimension(500, 700);
	Dimension sizeoncanvas = defaultsize;

	DBTableModel orgdatadm = null;
	GroupDBTableModel groupdatadm = null;

	Vector<BIPage> pages = null;
	BICellCalcer calcer = null;

	BIReportdsDefine dsdefine=null;
	
	boolean printing=false;

	/**
	 * �ڻ�������ҳ��ʼλ��.ֻ�Ե�һҳ����.ȱʡΪ0.���ʹ�������λ��,��ֵ��Ҫ����.
	 */
	int layoutstarty = 5;
	
	Category logger=Category.getInstance(BITableV_Render.class);

	public BITableV_Render(BITableV_def tablevdef) {
		super();
		this.tablevdef = tablevdef;
		tablevdef.addChanglistener(this);
		calcer = new BICellCalcer(this);
	}

	int dmrow = 0;
	BIPage curpage = null;
	int curtotalheight = 0;

	int maxrowsinpage = 1000;
	int rowtypes[] = new int[maxrowsinpage];
	int rowindexes[] = new int[maxrowsinpage];
	int definerowindexes[] = new int[maxrowsinpage];
	int rowheights[] = new int[maxrowsinpage];

	/**
	 * ���ڱ��еĵ�ǰ�����
	 */
	int dmgrouplevel = -1;

	/**
	 * ��󼶵ķ����
	 */
	int enddmgrouplevel = -1;

	int printingpageno = 0;

	/**
	 * ��ҳ�ȡ�
	 */
	public boolean prepareData() {
		if(orgdatadm==null || orgdatadm.getDisplaycolumninfos().size()==0){
			return true;
		}

		logger.debug("tablevrender prepareData(),rowcount="+orgdatadm.getRowCount());
		
		int papermaxheight = sizeoncanvas.height;
		/**
		 * ����÷���.
		 */
		this.groupdatadm = calcGroup();
		DBTableModel datadm = groupdatadm;

		pages = new Vector<BIPage>();

		curpage = new BIPage();
		dmrow = 0;
		curtotalheight = 0;
		if (tablevdef.isDrawgrid()) {
			curtotalheight = tablevdef.getGridwidth();
		}

		/**
		 * ����һ�������зֶ�����ʾ�����,curdataindex��¼�������������ʾ���ڼ���.
		 */
		int curdataindex = 0;
		int i = 0;
		// ��groupdbmodel��ȡ�õ�group level

		for (;;) {
			if (pages.size() == 0) {
				papermaxheight = sizeoncanvas.height - layoutstarty;
			} else {
				papermaxheight = sizeoncanvas.height;
			}
			for (i = 0; i < tablevdef.getRowcount(); i++) {
				if (tablevdef.getRowtypes()[i] != BITableV_def.ROWTYPE_HEAD)
					continue;
				int ret = tryPutInPage(i, papermaxheight);
				if (ret == -1) {
					logger.error("ֽ�Ÿ߶ȹ�С");
					return false;
				}
			}

			// ������������.
			int ret = 0;
			// ��Ϊgroupdm���˺ϼ���,������ȡһ��
			for (; dmrow < datadm.getRowCount() - 1; dmrow++) {
				// ����Ƿ�����
				RecordTrunk rec = datadm.getRecordThunk(dmrow);
				if (RecordTrunk.SUMFLAG_SUMMARY == rec.getSumflag()
						&& rec.getGrouplevel() >= 0) {
					// ��һ��Ҳ������?
					// int priorgrouplevel = 999;
					if (dmrow > 0) {
						int priorrow = dmrow - 1;
						int tmp = datadm.getRecordThunk(priorrow)
								.getGrouplevel();
						// if(tmp>=0)priorgrouplevel=tmp;
					}

					// ˵�������˷���.
					// ����levelΪ��0������.����ԽС,����ļ���Խ��,Խ����
					// splitggroupcolumnΪ������.

					if (dmgrouplevel < 0) {
						dmgrouplevel = rec.getGrouplevel();
					}

					int indexintabledef = tablevdef
							.getIndexIntabledefByGroup(dmgrouplevel);
					ret = tryPutInPage(indexintabledef, papermaxheight);
					if (ret == -1) {
						// ˵������ʱֽ��������,break��ʼ��ҳ
						break;
					} else {
						dmgrouplevel = -1;
					}
					rowindexes[curpage.rowcount] = dmrow;
					continue;
				}

				for (; curdataindex < tablevdef.getRowcount(); curdataindex++) {
					if (tablevdef.getRowtypes()[curdataindex] != BITableV_def.ROWTYPE_DATA)
						continue;
					ret = tryPutInPage(curdataindex, papermaxheight);
					if (ret == -1) {
						break;
					}
				}
				if (ret == -1) {
					break;
				}
				curdataindex = 0;

			}
			if (ret == -1) {
				newPage();
				curtotalheight = 0;
				if (tablevdef.isDrawgrid()) {
					curtotalheight = tablevdef.getGridwidth();
				}
				continue;
			}

			// �����β.
			for (i = 0; i < tablevdef.getRowcount(); i++) {
				if (tablevdef.getRowtypes()[i] != BITableV_def.ROWTYPE_FOOT)
					continue;
				ret = tryPutInPage(i, papermaxheight);
				if (ret == -1) {
					i--;
					newPage();
					curtotalheight = 0;
					if (tablevdef.isDrawgrid()) {
						curtotalheight = tablevdef.getGridwidth();
					}
				}
			}

			newPage();
			curtotalheight = 0;
			break;
		}
		logger.debug("tablevrender prepareData(),ok");
		return true;
	}

	void newPage() {
		setPageinfo();
		pages.add(curpage);
		curpage = new BIPage();
	}

	/**
	 * 
	 * @param i
	 * @return 0 �ɹ� -1 �Ų�����
	 */
	int tryPutInPage(int i, int papermaxheight) {
		int rowtype = tablevdef.getRowtypes()[i];
		int rowheight = tablevdef.getRowheights()[i];
		if (rowheight == 0) {
			// �Զ��и�
			rowheight = calcDmrowheight(dmrow, tablevdef.getCells()[i]);
		}
		// ��Ҫ��ҳ��?
		int fixrowcountinpage = tablevdef.getFixrowcountperpage();
		if (fixrowcountinpage > 0) {
			if (curpage.rowcount >= fixrowcountinpage) {
				return -1;
			}
		} else {
			// �Զ�����һҳ�Ŷ�����.
			if (tablevdef.isDrawgrid()) {
				if (curtotalheight + rowheight + tablevdef.getGridwidth() > papermaxheight) {
					return -1;
				}
			} else {
				if (curtotalheight + rowheight > papermaxheight) {
					return -1;
				}
			}

		}

		// ���Էŵ���
		// ��ͷ���β.
		rowtypes[curpage.rowcount] = rowtype;
		rowindexes[curpage.rowcount] = -1;
		if (rowtype == BITableV_def.ROWTYPE_DATA) {
			rowindexes[curpage.rowcount] = dmrow;
		} else if (rowtype == BITableV_def.ROWTYPE_GROUP) {
			rowindexes[curpage.rowcount] = dmrow;
		}

		rowheights[curpage.rowcount] = rowheight;
		definerowindexes[curpage.rowcount] = i;
		curpage.rowcount++;
		curtotalheight += rowheight;

		if (tablevdef.isDrawgrid()) {
			curtotalheight += tablevdef.getGridwidth();
		}

		return 0;
	}

	void setPageinfo() {
		curpage.rowtypes = new int[curpage.rowcount];
		curpage.datarowindexes = new int[curpage.rowcount];
		curpage.definerowindexes = new int[curpage.rowcount];
		curpage.rowheights = new int[curpage.rowcount];
		System.arraycopy(rowtypes, 0, curpage.rowtypes, 0, curpage.rowcount);
		System.arraycopy(rowindexes, 0, curpage.datarowindexes, 0,
				curpage.rowcount);
		System.arraycopy(definerowindexes, 0, curpage.definerowindexes, 0,
				curpage.rowcount);
		System
				.arraycopy(rowheights, 0, curpage.rowheights, 0,
						curpage.rowcount);
	}

	BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

	/**
	 * ���������еĸ߶�.
	 * 
	 * @param row
	 * @return
	 */
	int calcDmrowheight(int dmrow, BICell cells[]) {
		int maxheight = 0;
		for (int i = 0; i < cells.length; i++) {
			BICell cell = cells[i];
			Dimension oldsize = cell.getSize();
			oldsize.width = tablevdef.getColwidths()[i];
			cell.setCalcer(calcer);
			cell.drawCell((Graphics2D) bi.getGraphics(), dmrow);
			Dimension maxsize = cell.getMaxsize();
			if (maxsize.height > maxheight) {
				maxheight = maxsize.height;
			}
		}
		if (maxheight <= 1) {
			maxheight = 27;
		}
		return maxheight;
	}

	public void dumpPage() {
		Enumeration<BIPage> en = pages.elements();
		while (en.hasMoreElements()) {
			BIPage page = en.nextElement();
			page.dump();
		}
	}

	public void draw(Graphics2D g2, int pageindex) {
		if(pages==null){
			return;
		}
		if (pageindex > pages.size() - 1) {
			System.err
					.println("һ��" + pages.size() + "ҳ,pageindex=" + pageindex);
			return;
		}
		this.printingpageno = pageindex;
		BIPage page = pages.elementAt(pageindex);
		page.setDbtablemode(groupdatadm);
		page.setCalcer(calcer);
		page.setPrinting(isPrinting());

		if (pageindex == 0 && layoutstarty > 0) {
			// �����0ҳ�п�ʼλ�õ�����.
			Graphics2D g2tmp = (Graphics2D) g2.create(0, layoutstarty,
					sizeoncanvas.width, sizeoncanvas.height - layoutstarty);
			page.paint(g2tmp, tablevdef);
		} else {
			page.paint(g2, tablevdef);
		}
	}

	/**
	 * ȡҳ��
	 * 
	 * @return
	 */
	public int getPagecount() {
		if(pages==null){
			return 0;
		}
		return pages.size();
	}

	GroupDBTableModel calcGroup() {
		// ����groupinfos
		Vector<SplitGroupInfo> groupinfos = tablevdef.getGroupinfos();
		// ����

		
		// �������
		GroupDBTableModel gdm = new GroupDBTableModel(orgdatadm, groupinfos);
		// System.out.println(gdm.getRowCount());
		return gdm;
	}

	/**
	 * @return
	 */
	public DBTableModel getDbmodel() {
		return groupdatadm;
	}

	public int getCurrow() {
		return dmrow;
	}

	public int getPrintingpageno() {
		return printingpageno;
	}

	/**
	 * ��ҳ�ϼ���Ϣ
	 * 
	 * @param pageno
	 * @return
	 */
	public Splitpageinfo getPageinfo(int pageno) {
		return null;
	}

	public Dimension getSize() {
		return sizeoncanvas;
	}

	public String getType() {
		return id + ":��ֱ��";
	}

	public void setSize(Dimension size) {
		sizeoncanvas = size;
		if (orgdatadm != null) {
			tablevdef.fireDefinechanged();
		}
	}

	public void setCalcer(BICellCalcer calcer) {
		this.calcer = calcer;
	}

	public void setDbtablemode(DBTableModel dm) {
		this.orgdatadm = dm;
	}

	public BITableV_def getTablevdef() {
		return tablevdef;
	}

	public void onTabledefineChanged() {
		try {
			if (orgdatadm == null)
				return;
			prepareData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public GroupDBTableModel getGroupdatadm() {
		return groupdatadm;
	}

	public int getLayoutstarty() {
		return layoutstarty;
	}

	public void setLayoutstarty(int layoutstarty) {
		this.layoutstarty = layoutstarty;
	}

	public void setTablevdef(BITableV_def tablevdef) {
		this.tablevdef = tablevdef;
	}

	int id;

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
		this.id = id;
	}

	
	public BIReportdsDefine getDsdefine() {
		return dsdefine;
	}

	public void setDsdefine(BIReportdsDefine dsdefine) {
		this.dsdefine = dsdefine;
	}

	public String getParameter(String p) {
		Enumeration<BIReportparamdefine>en=dsdefine.params.elements();
		while(en.hasMoreElements()){
			BIReportparamdefine pdef=en.nextElement();
			if(pdef.paramname.equals(p)){
				return pdef.getInputvalue();
			}
		}
		return null;
	}

	public void reset(){
		sizeoncanvas = defaultsize;
		orgdatadm = null;
		groupdatadm = null;

		pages = null;
		dsdefine=null;
		layoutstarty = 5;
		
	}

	public void exportExcel(File outf,String reportname) throws Exception{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(reportname);

		Enumeration<BIPage>en=pages.elements();
		int excelrow=0;
		for(int i=0;i<pages.size();i++){
			BIPage page=pages.elementAt(i);
			page.setDbtablemode(groupdatadm);
			page.setCalcer(calcer);
			page.setTablevdef(tablevdef);

			boolean withhead=i==0;
			boolean withfoot=i==pages.size()-1;
			int rowcount=page.exportExcel(workbook,sheet, excelrow,withhead,withfoot);
			excelrow+=rowcount;
		}
		
		FileOutputStream fout=null;
		try {
			fout = new FileOutputStream(outf);
			workbook.write(fout);
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
	}
	
	public int exportExcel(int excelrow ,HSSFWorkbook workbook,HSSFSheet sheet,FileOutputStream fout) throws Exception{

		Enumeration<BIPage>en=pages.elements();
		for(int i=0;i<pages.size();i++){
			BIPage page=pages.elementAt(i);
			page.setDbtablemode(groupdatadm);
			page.setCalcer(calcer);
			page.setTablevdef(tablevdef);

			boolean withhead=i==0;
			boolean withfoot=i==pages.size()-1;
			int rowcount=page.exportExcel(workbook,sheet, excelrow,withhead,withfoot);
			excelrow+=rowcount;
		}

		return excelrow;
	}

	/**
	 * �Ƿ�����������?
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isMouseoverLink(int mx, int my) { 
		BIPage bipage=pages.elementAt(printingpageno);
		
		if(printingpageno==0){
			my-=layoutstarty;
		}
		if(bipage.isMouseoverlink(mx,my)){
			return true;
		}
		return false;
	}

	public boolean clickLink(int mx, int my,Component parentcomp,int compx,int compy) {
		BIPage bipage=pages.elementAt(printingpageno);
		
		if(printingpageno==0){
			my-=layoutstarty;
		}
		return bipage.clickLink(mx,my,parentcomp,compx,compy,dsdefine);
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}
	
	
}
