package com.inca.np.anyprint.impl;

public class Tocnmoney {
	static String[] cndig = new String[] { "��", "Ҽ", "��", "��", "��", "��", "½",
			"��", "��", "��" };
	static String[] rights = new String[] { "X", "ʰ", "��", "Ǫ", };
	
	public static String toChinese(String input) {
		// �ֽ���ţ�С��
		StringBuffer sb = new StringBuffer();
		if (input.startsWith("-")) {
			sb.append("��");
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
				sb.append("Ԫ");
		}

		int jiaofen = 0;
		try {
			jiaofen = Integer.parseInt(divide);
		} catch (Exception e) {
		}
		if (jiaofen == 0) {
			sb.append("��");
		} else if (jiaofen >= 10) {
			int jiao = divide.charAt(0) - '0';
			int fen = divide.charAt(1) - '0';
			sb.append(cndig[jiao] + "��");
			if (fen > 0)
				sb.append(cndig[fen] + "��");
		} else {
			if (intpart.length() > 0)
				sb.append("��");
			sb.append(cndig[jiaofen] + "��");
		}

		return sb.toString();
	}

	/**
	 * ���ⳤ������ת��
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
			//�ݹ����
			hi4=convert(yi);
			sb.append(hi4);
			sb.append("��");
		}
		int iinput=Integer.parseInt(input);
		input = String.valueOf(iinput);
		String low4=convert8(input);
		if(hi4.length()>0 && iinput > 0 && input.length()<8){
			sb.append("��");
		}
		
		sb.append(low4);
		return sb.toString();
	}

	/**
	 * 8λ�����ڵ���������
	 * 
	 * @param input
	 * @return
	 */
	private static String convert8(String input) {
		// ȡ������
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
			// ȥ��0
			inputwan = String.valueOf(Integer.parseInt(inputwan));
			sb.append(convertThousand(inputwan));
			sb.append("��");
		}

		int ii = Integer.parseInt(input);
		if (ii != 0 && input.charAt(0) == '0') {
			sb.append("��");
		}

		input = String.valueOf(Integer.parseInt(input));
		sb.append(convertThousand(input));
		return sb.toString();
	}

	/**
	 * 4λ������
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
				sb.append("��");
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
