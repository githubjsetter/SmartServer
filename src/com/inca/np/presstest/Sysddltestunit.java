package com.inca.np.presstest;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.StringCommand;

/**
 * ��ѯϵͳѡ��Ĳ��Ե�Ԫ
 * @author Administrator
 *
 */
public class Sysddltestunit extends Presstestunit{

	public Sysddltestunit() {
		super();
		ClientRequest req=new ClientRequest("��ѯϵͳѡ���ֵ�");
		req.addCommand(new StringCommand("PUB_COMPANY_TYPE"));
		
		reqs.add(req);
		
	}

}
