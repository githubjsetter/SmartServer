package com.inca.npbi.server;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npbi.client.view.View_ste;
/*����"��ͼ����"Ӧ�÷���������*/
public class View_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new View_ste(null);
	}
	protected String getTablename() {
		return "npbi_view";
	}
}
