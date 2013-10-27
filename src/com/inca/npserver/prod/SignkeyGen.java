package com.inca.npserver.prod;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import com.inca.npclient.system.NpclientParam;

/**
 * 生成密钥对.
 * 
 * @author Administrator
 * 
 */
public class SignkeyGen {
/*
	public static void genKeypair() throws Exception {
		File f = new File("signjar/passwd");
		BufferedReader rd = new BufferedReader(new FileReader(f));
		String seedstr = rd.readLine();
		rd.close();

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

		random.setSeed(seedstr.getBytes());
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		PublicKey publickey = pair.getPublic();
		System.out.println(publickey.getAlgorithm());// DSA
		System.out.println(publickey.getFormat());// X.509
		System.out.println(publickey.getEncoded());

		//将public key写到文件
		File publicf=new File("signjar/publickey");
		FileOutputStream fout=new FileOutputStream(publicf);
		fout.write(publickey.getEncoded());
		fout.close();
		

		PrivateKey privatekey = pair.getPrivate();
		System.out.println(privatekey.getAlgorithm());// DSA
		System.out.println(privatekey.getFormat());// X.509
		System.out.println(privatekey.getEncoded());

		File privatef=new File("signjar/privatekey");
		fout=new FileOutputStream(privatef);
		fout.write(privatekey.getEncoded());
		fout.close();
	}
*/
	
	public static void signLicenseinfo(Licenseinfo linfo,PrivateKey privatekey)throws Exception{
		String data=Licenseinfo2string(linfo);
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		dsa.initSign(privatekey);
		dsa.update(data.getBytes());
		
		byte[] sign=dsa.sign();
		//转为HEX
		String strsign=toHex(sign);
		linfo.setDigitsign(strsign);
		return ;
		
	}
	
	public static boolean verifySign(Licenseinfo linfo) throws Exception{
		//验证签名
		String data=Licenseinfo2string(linfo);

		//File publickeyf=new File("signjar/publickey");
		InputStream fin=SignkeyGen.class.getResourceAsStream("publickey");
		if(fin==null){
			throw new Exception("找不到publickey");
		}
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		int c;
		while((c=fin.read())>=0){
			bout.write(c);
		}
		fin.close();
		byte[] publicdata=bout.toByteArray();
		X509EncodedKeySpec keyspec=new X509EncodedKeySpec(publicdata);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		PublicKey publickey = keyFactory.generatePublic(keyspec);

	    Signature sig = Signature.getInstance("SHA1withDSA");
	    sig.initVerify(publickey);
	    sig.update(data.getBytes());
	    
	    byte[] signature=string2bytes(linfo.getDigitsign());
	    return sig.verify(signature);
	}
	
	
	 static byte[] string2bytes(String strsign) {
		 ByteArrayOutputStream bout=new ByteArrayOutputStream();
		 for(int i=0;i<strsign.length();i+=2){
			 String s=strsign.substring(i,i+2);
			 int intvalue=Integer.parseInt(s,16);
			 bout.write(intvalue);
		 }
		return bout.toByteArray();
	}


	public static String toHex(byte[] data){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<data.length;i++){
			int b=data[i];
			if(b<0)b+=256;
			String s=Integer.toHexString(b);
			if(s.length()==1){
				s="0"+s;
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	static String Licenseinfo2string(Licenseinfo linfo){
		SimpleDateFormat dfmt=new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb=new StringBuffer();
		sb.append(":"+linfo.getAuthunit());
		sb.append(":"+linfo.getCopyright());
		sb.append(":"+dfmt.format(linfo.getEnddate().getTime()));
		sb.append(":"+String.valueOf(linfo.getMaxclientuser()));
		
		Enumeration<String> en=linfo.getModules().elements();
		while(en.hasMoreElements()){
			sb.append(":"+en.nextElement());
		}
		sb.append(":"+linfo.getProdname());
		sb.append(":"+linfo.getServerip());
		sb.append(":"+dfmt.format(linfo.getStartdate().getTime()));
		return sb.toString();
	}
	

}
