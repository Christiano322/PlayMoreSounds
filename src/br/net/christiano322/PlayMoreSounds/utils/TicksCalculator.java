package br.net.christiano322.PlayMoreSounds.utils;

public class TicksCalculator {
	public static String getTime(long ticks) {
		long second = 0;
		long minute = 0;
		long hour = 0;

		if ((ticks / 20) >= 1) {
			second = ticks / 20;
		}

		if ((second / 60) >= 1) {
			minute = second / 60;
			second = second - (minute * 60);
		}

		if ((minute / 60) >= 1) {
			hour = minute / 60;
			minute = minute - (hour * 60);
		}

		String string = "";

		string = string + plural(hour, " " + MessageSender.string("General.Time.Hour"), " " + MessageSender.string("General.Time.Hours"));
		if (hour > 0 & minute > 0) {
			string = string + " ";
		}
		string = string + plural(minute, " " +  MessageSender.string("General.Time.Minute"), " " + MessageSender.string("General.Time.Minutes"));
		if (minute > 0 & second > 0) {
			string = string + " ";
		} else if (hour > 0 & second > 0) {
			string = string + " ";
		}

		string = string + plural(second, " " + MessageSender.string("General.Time.Second"), " " + MessageSender.string("General.Time.Seconds"));

		return string;
	}

	private static String plural(long value, String singular, String plural) {
		String string = "";

		if (value > 1) {
			string = string + value + plural;
		} else if (value == 1) {
			string = string + value + singular;
		}

		return string;
	}
}
