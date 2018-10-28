package br.net.christiano322.PlayMoreSounds.sound;
import java.util.*;

public class SoundConfig {
	String soundName;
	Cancellable cancellable;
	boolean enabled;
	List<Interval> intervals;

	public static enum Cancellable {
		NO, TRUE, FALSE
		}

	public SoundConfig(String soundName, Cancellable cancellable, boolean enabled, List<Interval> intervals) {
		this.soundName = soundName;
		this.cancellable = cancellable;
		this.enabled = enabled;
		this.intervals = intervals;
	}

	public String getSoundName() {
		return soundName;
	}

	public Cancellable getCancellable() {
		return cancellable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<Interval> getIntervals() {
		return intervals;
	}
}
