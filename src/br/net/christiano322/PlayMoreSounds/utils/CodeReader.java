package br.net.christiano322.PlayMoreSounds.utils;

public class CodeReader {
	public static String formatString(String string) {
		String data = "";
		for (int i = 0; i < string.length(); i++) {
			data = data + formatLetter(String.valueOf(string.charAt(i)));
		}
		return data;
	}

	static String formatLetter(String letter) {
		String[] split = "1.-,2abc,3def,4ghi,5jkl,6mno,7pqrs,8tuv,9wxyz".split(",");
		String string = "";
		for (String s : split) {
			if (s.replaceAll("[0-9]", "").contains(letter.toLowerCase())) {
				for (int i = 0; i < s.length(); i++) {
					if (letter.toCharArray()[0] == s.charAt(i)) {
 						for (int size = 0; size < i; size ++) {
							string = string + s.charAt(0);
						}
					}
				}
			}
		}
		if (string.equals("")) {
			string = " ";
		} else {
			string = string + "0";
		}
		return string;
	}
}
