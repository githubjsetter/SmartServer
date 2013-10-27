package com.inca.npclient.sort;

import java.util.Comparator;

import com.inca.npclient.pinyin.PinyinHelper;

/**
 * 
 * @author Administrator
 * 
 */
public class PinyinComparator implements Comparator<String> {

	public int compare(String s1, String s2) {

		for (int i = 0; i < s1.length() && i < s2.length(); i++) {

			int codePoint1 = s1.charAt(i);
			int codePoint2 = s2.charAt(i);

			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);
			String pinyin1 = pinyin((char) codePoint1);
			String pinyin2 = pinyin((char) codePoint2);
			
			if(pinyin1==null && pinyin2!=null){
				return -1;
			}
			if(pinyin1!=null && pinyin2==null){
				return 1;
			}
			

			if (pinyin1 != null && pinyin1.length() > 0) {
				c1 = pinyin1.charAt(0);
			}

			if (pinyin2 != null && pinyin2.length() > 0) {
				c2 = pinyin2.charAt(0);
			}

			if (c1 != c2) {
				return c1 - c2 > 0 ? 1 : -1;
			} else {
				continue;
			}

		}
		return s1.length() - s2.length();
	}

	/**
	 * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
	 */
	private String pinyin(char c) {
		String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
		if (pinyins == null) {
			return null;
		}
		return pinyins[0];
	}

	public static void main(String[] args) {
		String s1 = "帐务";
		String s2 = "零售";

		PinyinComparator c = new PinyinComparator();
		int ret = c.compare(s1, s2);
		System.out.println(ret);
	}
}
