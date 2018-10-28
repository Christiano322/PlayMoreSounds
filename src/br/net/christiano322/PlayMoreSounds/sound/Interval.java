package br.net.christiano322.PlayMoreSounds.sound;

public class Interval {
	long interval;
	boolean ignoreToggle;
	boolean crossWorlds;
	LocationType location;
	String permissionListen;
	String permission;
	String positions;
	double radius;
	float pitch;
	SoundEnum sound;
	float volume;

	public enum LocationType {
		DEFAULT, EYE
		}

	public Interval(long interval, boolean ignoreToggle, LocationType location, String permissionListen, String permission, String positions, double radius, float pitch, SoundEnum sound, float volume, boolean crossWorlds) {
		this.interval = interval;
		this.ignoreToggle = ignoreToggle;
		this.location = location;
		this.permissionListen = permissionListen;
		this.permission = permission;
		this.positions = positions;
		this.radius = radius;
		this.pitch = pitch;
		this.sound = sound;
		this.volume = volume;
		this.crossWorlds = crossWorlds;
	}

	public long getInterval() {
		return interval;
	}

	public boolean getIgnoreToggle() {
		return ignoreToggle;
	}

	public LocationType getLocationType() {
		return location;
	}

	public String getPermissionListen() {
		return permissionListen;
	}

	public String getPermission() {
		return permission;
	}

	public String getPositions() {
		return positions;
	}
	
	public boolean crossWorlds() {
		return crossWorlds;
	}

	public double getRadius() {
		return radius;
	}

	public float getPitch() {
		return pitch;
	}

	public SoundEnum getSound() {
		return sound;
	}

	public float getVolume() {
		return volume;
	}
}
