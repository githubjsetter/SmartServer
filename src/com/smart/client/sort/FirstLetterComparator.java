package com.smart.client.sort;

import java.util.Comparator;

/**
 * 取汉字拼音首字母比较大小。
 * @author Administrator
 *
 */
public class FirstLetterComparator implements Comparator<String> {

	public int compare(String o1, String o2) {

		boolean flag1 = isChinese(o1.charAt(0));
		boolean flag2 = isChinese(o2.charAt(0));
		String o = new Chinesetoen().getAllFirstLetter(o1);
		String o22 = new Chinesetoen().getAllFirstLetter(o2);
		if (flag1 && flag2) {
			return o.compareTo(o22);
		} else if (!flag1 && flag2) {
			return -1;
		} else if (flag1 && !flag2) {
			return 1;
		} else {
			return o.compareTo(o22);
		}
	}

	private boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

			return true;
		}

		return false;

	}

}
