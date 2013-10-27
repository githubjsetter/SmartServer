package com.inca.np.print.report;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.print.drawable.PPage;


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
