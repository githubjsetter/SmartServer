package com.inca.np.anyprint.impl;

public class Tocnmoney {
	static String[] cndig = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆",
			"柒", "捌", "玖" };
	static String[] rights = new String[] { "X", "拾", "佰", "仟", };
	
	public static String toChinese(String input) {
		// 分解符号，小数
		StringBuffer sb = new StringBuffer();
		if (input.startsWith("-")) {
			sb.append("负");
			input = input.substring(1);
		}
		int p = input.indexOf(".");
		String divide = "";
		if (p < 0) {
			divide = "00";
		} else {
			divide = input.substring(p + 1);
			input = input.substring(0, p);
		}
		if (divide.length() == 0) {
			divide = "00";
		} else if (divide.length() == 1) {
			divide += "0";
		}

		String intpart = convert(input);
		if (Double.parseDouble(input) != 0) {
			sb.append(intpart);
				sb.append("元");
		}

		int jiaofen = 0;
		try {
			jiaofen = Integer.parseInt(divide);
		} catch (Exception e) {
		}
		if (jiaofen == 0) {
			sb.append("整");
		} else if (jiaofen >= 10) {
			int jiao = divide.charAt(0) - '0';
			int fen = divide.charAt(1) - '0';
			sb.append(cndig[jiao] + "角");
			if (fen > 0)
				sb.append(cndig[fen] + "分");
		} else {
			if (intpart.length() > 0)
				sb.append("零");
			sb.append(cndig[jiaofen] + "分");
		}

		return sb.toString();
	}

	/**
	 * 任意长正整数转换
	 * 
	 * @param input
	 * @return
	 */
	private static String convert(String input) {
		StringBuffer sb = new StringBuffer();
		String hi4="";
		if (input.length() > 8) {
			int len = input.length() - 8;
			String yi = input.substring(0, len);
			input = input.substring(len);
			//递归调用
			hi4=convert(yi);
			sb.append(hi4);
			sb.append("亿");
		}
		int iinput=Integer.parseInt(input);
		input = String.valueOf(iinput);
		String low4=convert8(input);
		if(hi4.length()>0 && iinput > 0 && input.length()<8){
			sb.append("零");
		}
		
		sb.append(low4);
		return sb.toString();
	}

	/**
	 * 8位长度内的正整数。
	 * 
	 * @param input
	 * @return
	 */
	private static String convert8(String input) {
		// 取万以上
		String inputwan = "";

		if (input.length() >= 5) {
			int len = input.length() - 4;
			inputwan = input.substring(0, len);
			input = input.substring(len);
		}

		// System.out.println(inputwan);
		// System.out.println(input);

		StringBuffer sb = new StringBuffer();
		if (inputwan.length() > 0) {
			// 去掉0
			inputwan = String.valueOf(Integer.parseInt(inputwan));
			sb.append(convertThousand(inputwan));
			sb.append("万");
		}

		int ii = Integer.parseInt(input);
		if (ii != 0 && input.charAt(0) == '0') {
			sb.append("零");
		}

		input = String.valueOf(Integer.parseInt(input));
		sb.append(convertThousand(input));
		return sb.toString();
	}

	/**
	 * 4位长度内
	 * 
	 * @param input
	 * @return
	 */
	private static String convertThousand(String input) {
		boolean lastiszero = false;
		lastiszero = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			int right = input.length() - i;
			int dig = input.charAt(i) - '0';

			if (dig == 0) {
				if (lastiszero)
					continue;
				lastiszero = true;
				continue;
			}

			if (lastiszero) {
				sb.append("零");
			}
			sb.append(cndig[dig]);
			if (right > 1)
				sb.append(rights[right - 1]);
			lastiszero = false;

		}

		return sb.toString();
	}

	public static void main(String args[]) {
		
		String tests[] = { "0.01", "0.10", "0.12", "1.00", "1.01", "1.12",
				"1.1", "100010001.03","30120000078.00","-1" };
		
		Tocnmoney tm = new Tocnmoney();

		//String tests[] = { "12102000001.3" };
		//String tests[] = { "112002001001.3" };
		for (int i = 0; i < tests.length; i++) {
			System.out.println(tests[i] + "==>" + tm.toChinese(tests[i]));
		}
		// System.out.println(tm.convert("30120000078"));

	}
}
