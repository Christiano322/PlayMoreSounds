package br.net.christiano322.PlayMoreSounds.api.events;

import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.events.options.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import java.util.*;

public class ReproduceSoundEvent extends Event implements Cancellable,PeriodCancelled {

	private Player eventMaker;
	private Location location;
	private EventName eventName;
	private long interval;
	private Double radius;
	private String sound;
	private Float volume;
	private Float pitch;
	private boolean isCancelled;
	private boolean dontPlayThisTime;
	private List<Player> listeners;
	private static final HandlerList handlers = new HandlerList();

	/**
	 * @param eventMaker
	 *            The player that triggered the event.
	 * @param location
	 *            The location that the sound was reproduced.
	 * @param eventName
	 *            The name of the event that the player triggered.
	 * @param radius
	 *            The range of blocks that the sound will be heardable.
	 * @param permission
	 *            The permission that is required to play the sound.
	 * @param sound
	 *            The sound itself.
	 * @param volume
	 *            The volume of the sound.
	 * @param pitch
	 *            The pitch of the sound.
	 */
	public ReproduceSoundEvent(Player eventMaker, Location location, EventName eventName,
							   Double radius, String sound, Float volume, Float pitch, List<Player> listeners, long interval) {
		this.eventMaker = eventMaker;
		this.location = location;
		this.eventName = eventName;
		this.radius = radius;
		isCancelled = false;
		dontPlayThisTime = false;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.listeners = listeners;
		this.interval = interval;
	}

	/**
	 * Check if the event is already cancelled.
	 */
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	/**
	 * @param arg0
	 *            If true, the sound will not be reproduced. If false, it will
	 *            revalidate any 'cancel' that this event has.
	 */
	@Override
	public void setCancelled(boolean arg0) {
		this.isCancelled = arg0;
	}

	/**
	 * Useful to unregister the event in a RegisteredListener class.
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return HandlerList in a static way.
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public void setCancelledThisRound(boolean value) {
		dontPlayThisTime = value;
	}

	@Override
	public boolean isCancelledThisRound() {
		return dontPlayThisTime;
	}

	/**
	 * @return The player that triggered the event.
	 */
	public Player getEventMaker() {
		return this.eventMaker;
	}

	/**
	 * Returns null if UseHardMethods boolean on config is false.
	 */
	public List<Player> getListeners() {
		return listeners;
	}

	/**
	 * @return The location that the sound is played.
	 */
	public Location getLocation() {
		return this.location;
	}

	/**
	 * @return The name of the event that the player called to play a sound.
	 */
	public EventName getEventType() {
		return this.eventName;
	}

	/**
	 * @return The range of blocks that the sound will be heardable.
	 */
	public Double getRadius() {
		return this.radius;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	public void setLocation(Location loc) {
		this.location = loc;
	}

	/**
	 * @return The bukkit sound that will be reproduced.
	 */
	public String getSound() {
		return this.sound;
	}

	/**
	 * @return The volume of the sound.
	 */
	public Float getVolume() {
		return this.volume;
	}

	/**
	 * @return The pitch of the sound.
	 */
	public Float getPitch() {
		return this.pitch;
	}

	public long getInterval() {
		return this.interval;
	}
}
