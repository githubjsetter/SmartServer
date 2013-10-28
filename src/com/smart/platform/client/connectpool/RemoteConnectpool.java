package com.smart.platform.client.connectpool;

import org.apache.log4j.Category;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;

/**
 * ��ೢ������,��ʱ5��,������Ϣ.
 * 
 * @author user
 * 
 */
public class RemoteConnectpool {
	String authstring;
	ClientRequest req;

	// ��10��
	int timeout = 10000;
	String strurl;
	int MAXWORKER = 3;
	Sendworker workers[] = null;
	Object signobject = new Object();
	int retrievedsize = 0;
	int inflatedsize = 0;

	public static int msgseqid = 0;
	Category logger = Category.getInstance(RemoteConnectpool.class);

	public RemoteConnectpool(String strurl, ClientRequest req) {
		super();
		this.strurl = strurl;
		this.req = req;
		this.authstring = req.getAuthstring();
		// ����msgseqid
		int thisseqid = msgseqid++;
		req.putContextvalue("msgid", req.getAuthstring() + "."
				+ String.valueOf(System.currentTimeMillis()) + "."
				+ String.valueOf(thisseqid));

		workers=new Sendworker[MAXWORKER];
		for (int i = 0; i < MAXWORKER; i++) {
			workers[i] = new Sendworker(i, strurl, req, signobject);
		}
	}

	public ServerResponse doSend() throws Exception {

		// һ��������.
		for (int i = 0; i < MAXWORKER; i++) {
			synchronized (signobject) {
				logger.debug("workerid=" + i + " start");
				workers[i].start();
				signobject.wait(timeout);
				logger.debug("��ʼ���״̬");

				// ����ɵ���?
				for (int j = 0; j <= i; j++) {
					logger.debug("workerid=" + j + " ״̬="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 1) {
						logger.debug("���� workerid="+j+"�ķ�������Ӧ");
						retrievedsize=workers[j].getRetrievedSize();
						inflatedsize=workers[j].getInflatSize();
						return workers[j].getSvrresp();
					}
				}
			}
		}

		logger.debug("�̶߳�����,����û��״̬����,ѭ��5���ٵ�");

		// �����û�����,ֻ�ܵ���
		for (int t = 0;t<100 ; t++) {
			synchronized (signobject) {
				signobject.wait(timeout);

				// ����ɵ���?
				for (int j = 0; j < MAXWORKER; j++) {
					logger.debug("workerid=" + j + " ״̬="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 1) {
						logger.debug("���� workerid="+j+"�ķ�������Ӧ");
						retrievedsize=workers[j].getRetrievedSize();
						inflatedsize=workers[j].getInflatSize();
						return workers[j].getSvrresp();
					}
				}

				//���쳣����
				for (int j = 0; j < MAXWORKER; j++) {
					logger.debug("workerid=" + j + " ״̬="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 2) {
						logger.debug("�ӳ�workerid="+j+"���쳣");
						throw workers[j].getSvrexception();
					}
				}

			}
		}

		logger.debug("��������ʱ.�����Ƿ��������ж�");
		throw new Exception("��������ʱ.�����Ƿ��������ж�");
	}

	public int getRetrievedsize() {
		return retrievedsize;
	}

	public int getInflatedsize() {
		return inflatedsize;
	}
	
}
