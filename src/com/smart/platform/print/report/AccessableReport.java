package com.smart.platform.print.report;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.print.drawable.PPage;


public abstract interface AccessableReport
{
  public abstract DBTableModel getDbmodel();

  public abstract DBTableModel getMasterDbmodel();

  public abstract PPage getPage();

  public abstract int getPrintingpageno();

  public abstract int getPagecount();

  public abstract int getMasterdbmodelrow();

  public abstract String getParam(String paramString);

  public abstract String getSortexpr();

  public abstract void setSortexpr(String paramString);
}
