package com.inca.np.communicate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-26
 * Time: 17:28:46
 * 命令产生和解析
 */
public class CommandFactory {
	/**
	 * 读入字符串。 第0个字节是长度。如果长度大于127,下一个字节也是长度.
	 * 表达最大长度2^15.
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String readString(InputStream in) throws Exception{
		int stringlen=readShort(in);
		if(stringlen>=0X7FFFFFFF){
			throw new Exception("字符串太长或输入流结束");
		}
		byte data[]=new byte[stringlen];
		int totalrd=0;
		while(totalrd<stringlen){
			int rd = in.read(data,totalrd,stringlen - totalrd);
			if(rd<=0)break;
			totalrd+=rd;
		}
		if(totalrd!=stringlen){
			throw new Exception("读出字符串失败,期待长度"+stringlen+",实际读"+totalrd);
		}
		String s=new String(data,"gbk");

		return s;
	}

	public static void writeString(String s,OutputStream out) throws Exception{
		if(s==null){
			writeShort(0,out);
			return;
		}
		byte[] data = s.getBytes("gbk");
		writeShort(data.length,out);
		out.write(data);
	}



	public static int readShort(InputStream in) throws Exception{
		int b0=in.read();
		if(b0<0)return -1;
		if(b0<0)b0+=256;
		if( (b0 & 0x80)==0){
			return b0;
		}
		b0 &=0x7f;

		int b1=in.read();if(b1<0)b1+=256;
		int b2=in.read();if(b2<0)b2+=256;
		int b3=in.read();if(b3<0)b3+=256;

		int m=(b0<<24) | (b1 << 16) | (b2 << 8) | b3;

		return m;

	}

	public static void main(String[] argv){
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		try {
			CommandFactory.writeShort(124,bout);
			byte[] data = bout.toByteArray();


			ByteArrayInputStream bin = new ByteArrayInputStream(data);
			int result = CommandFactory.readShort(bin);

			int m;
			m=3;

			String msg="sadfdsafsdaf";
			bout=new ByteArrayOutputStream();
			CommandFactory.writeString(msg,bout);
			bin = new ByteArrayInputStream(bout.toByteArray());
			String ss = CommandFactory.readString(bin);

			System.out.println(ss);


		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}


	}

	/**
	 * len必须为正或0
	 * 如果len小于128, 一个字节.
	 * 如果大于128,为从高到底4个字节,其中第0个字节的第7位必须是1
	 * @param len
	 * @param out
	 * @throws Exception
	 */
	public static void writeShort(int len,OutputStream out) throws Exception{
		if((len & 0x80000000)!=0){
			throw new Exception("字符串长度太长,错误长度:"+len);
		}

		if(len<128){
			out.write(len);
		}else{
			int tmpb = (len>>24)&0xff;
			tmpb|=0x80;
			out.write(tmpb);
			tmpb = (len>>16)&0xff;
			out.write(tmpb);
			tmpb = (len>>8)&0xff;
			out.write(tmpb);
			tmpb = (len)&0xff;
			out.write(tmpb);
		}
	}

	public static void writeCommand(CommandBase command,OutputStream out)throws Exception{
		command.write(out);
	}

	public static CommandBase readCommand(InputStream in) throws Exception{
		CommandHead head=new CommandHead();
		head.read(in);
		if(head.commandtype.equals(CommandHead.COMMANDTYPE_PARAM)){
			ParamCommand pcmd=new ParamCommand(head);
			pcmd.readData(in);
			return pcmd;
		}else if(head.commandtype.equals(CommandHead.COMMANDTYPE_STRING)){
			StringCommand scmd=new StringCommand(head);
			scmd.readData(in);
			return scmd;
		}else if(head.commandtype.equals(CommandHead.COMMANDTYPE_SQL)){
			SqlCommand scmd=new SqlCommand(head);
			scmd.readData(in);
			return scmd;
		}else if(head.commandtype.equals(CommandHead.COMMANDTYPE_DATA)){
			DataCommand dcmd=new DataCommand(head);
			dcmd.readData(in);
			return dcmd;
		}else if(head.commandtype.equals(CommandHead.COMMANDTYPE_RESULT)){
			ResultCommand rcmd=new ResultCommand(head);
			rcmd.readData(in);
			return rcmd;
		}else if(head.commandtype.equals(CommandHead.COMMANDTYPE_FILE)){
			BinfileCommand rcmd=new BinfileCommand(head);
			rcmd.readData(in);
			return rcmd;
		}else{
			throw new Exception("无法解析命令类型commandhead.commandtype:"+head.commandtype);
		}
	}
}
