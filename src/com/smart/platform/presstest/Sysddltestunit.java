package com.smart.platform.presstest;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.StringCommand;

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
