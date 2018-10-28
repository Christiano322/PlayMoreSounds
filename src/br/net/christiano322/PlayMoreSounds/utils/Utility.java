package br.net.christiano322.PlayMoreSounds.utils;
import java.text.*;
import java.util.*;

public class Utility {

	public static int integerConversor(String integer) throws NumberFormatException {
		return Integer.parseInt(integer);
	}
	
	public static long longConversor(String lon) throws NumberFormatException {
		return Long.parseLong(lon);
	}

	public static double doubleConversor(String integer) throws NumberFormatException {
		return Double.parseDouble(integer);
	}

	public static boolean isInteger(String arg0) {
		try {
			integerConversor(arg0);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isLong(String arg0) {
		try {
			longConversor(arg0);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean contains(List<String> list, String string) {
		for (String s : list) {
			if (string.contains(s)) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean containsSet(Set<String> list, String string) {
		for (String s : list) {
			if (s.contains(string)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isDouble(String args0) {
		try {
			doubleConversor(args0);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isDoubleOrInteger(String string) {
		if (isDouble(string) | isInteger(string)) {return true;}
		return false;
	}

	public static boolean isDate(String contains) {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		if (format.format(now).contains(contains)) {
			return true;
		}
		return false;
	}

	public static long formatToTicks(String interval) {
		if ((interval.endsWith("s")) | (interval.endsWith("m")) | (interval.endsWith("h"))) {
			if (interval.endsWith("s")) {
				return Long.parseLong(interval.replaceAll("s", "")) * 20;
			} else if (interval.endsWith("m")) {
				return Long.parseLong(interval.replaceAll("m", "")) * 1200;
			} else if (interval.endsWith("h")) {
				return Long.parseLong(interval.replaceAll("h", "")) * 72000;
			} else {
				return 0;
			}
		}
		return Long.parseLong(interval) * 20;
	}
	public static int getRandomInteger(int max, int minimum) {
		int maximum = max + 1;
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }
}
