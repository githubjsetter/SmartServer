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
 * 垂直表的生成
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
	 * 在画布上首页开始位置.只对第一页有用.缺省为0.如果使用了相对位置,该值需要设置.
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
	 * 放在表中的当前的组号
	 */
	int dmgrouplevel = -1;

	/**
	 * 最大级的分组号
	 */
	int enddmgrouplevel = -1;

	int printingpageno = 0;

	/**
	 * 分页等。
	 */
	public boolean prepareData() {
		if(orgdatadm==null || orgdatadm.getDisplaycolumninfos().size()==0){
			return true;
		}

		logger.debug("tablevrender prepareData(),rowcount="+orgdatadm.getRowCount());
		
		int papermaxheight = sizeoncanvas.height;
		/**
		 * 计算好分组.
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
		 * 对于一个数据行分多行显示的情况,curdataindex记录这个数据行已显示到第几行.
		 */
		int curdataindex = 0;
		int i = 0;
		// 在groupdbmodel中取得的group level

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
					logger.error("纸张高度过小");
					return false;
				}
			}

			// 放入数据行了.
			int ret = 0;
			// 因为groupdm算了合计行,所以少取一行
			for (; dmrow < datadm.getRowCount() - 1; dmrow++) {
				// 如果是分组列
				RecordTrunk rec = datadm.getRecordThunk(dmrow);
				if (RecordTrunk.SUMFLAG_SUMMARY == rec.getSumflag()
						&& rec.getGrouplevel() >= 0) {
					// 上一行也是组吗?
					// int priorgrouplevel = 999;
					if (dmrow > 0) {
						int priorrow = dmrow - 1;
						int tmp = datadm.getRecordThunk(priorrow)
								.getGrouplevel();
						// if(tmp>=0)priorgrouplevel=tmp;
					}

					// 说明进入了分组.
					// 其中level为从0起数字.数字越小,分组的级别越高,越优先
					// splitggroupcolumn为分组列.

					if (dmgrouplevel < 0) {
						dmgrouplevel = rec.getGrouplevel();
					}

					int indexintabledef = tablevdef
							.getIndexIntabledefByGroup(dmgrouplevel);
					ret = tryPutInPage(indexintabledef, papermaxheight);
					if (ret == -1) {
						// 说明分组时纸不够高了,break开始新页
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

			// 放入表尾.
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
	 * @return 0 成功 -1 放不下了
	 */
	int tryPutInPage(int i, int papermaxheight) {
		int rowtype = tablevdef.getRowtypes()[i];
		int rowheight = tablevdef.getRowheights()[i];
		if (rowheight == 0) {
			// 自动行高
			rowheight = calcDmrowheight(dmrow, tablevdef.getCells()[i]);
		}
		// 需要分页吗?
		int fixrowcountinpage = tablevdef.getFixrowcountperpage();
		if (fixrowcountinpage > 0) {
			if (curpage.rowcount >= fixrowcountinpage) {
				return -1;
			}
		} else {
			// 自动决定一页放多少行.
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

		// 可以放的下
		// 表头或表尾.
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
	 * 计算数据行的高度.
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
					.println("一共" + pages.size() + "页,pageindex=" + pageindex);
			return;
		}
		this.printingpageno = pageindex;
		BIPage page = pages.elementAt(pageindex);
		page.setDbtablemode(groupdatadm);
		page.setCalcer(calcer);
		page.setPrinting(isPrinting());

		if (pageindex == 0 && layoutstarty > 0) {
			// 如果第0页有开始位置的下移.
			Graphics2D g2tmp = (Graphics2D) g2.create(0, layoutstarty,
					sizeoncanvas.width, sizeoncanvas.height - layoutstarty);
			page.paint(g2tmp, tablevdef);
		} else {
			page.paint(g2, tablevdef);
		}
	}

	/**
	 * 取页数
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
		// 生成groupinfos
		Vector<SplitGroupInfo> groupinfos = tablevdef.getGroupinfos();
		// 排序

		
		// 计算分组
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
	 * 本页合计信息
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
		return id + ":垂直表";
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
	 * 是否遇到了链接?
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
