package br.net.christiano322.PlayMoreSounds.utils;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.sound.*;
import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.*;

public class SoundPlayer {

	PMS main;

	public SoundPlayer(PMS main) {
		this.main = main;
	}

	public void playSound(EventName event, File yaml, FileConfiguration config, Player eventMaker, String name, Double customRadius, Location customLocation, boolean allInLocation, List<Player> list) {
		try {
			boolean absentPlayer = false;
			if (eventMaker == null) {
				absentPlayer = true;
			}
			if ((config.getConfigurationSection(name).contains("Enabled") ? config.getBoolean(name + ".Enabled") : 0 == 0) & (!main.blockedSounds.contains(name))) {
				List<String> intervals = new ArrayList<>();
				intervals.addAll(config.getConfigurationSection(name + ".Sounds").getKeys(false));
				for (int i = 0; i < intervals.size(); i++) {
					String interval = intervals.get(i);
					List<Player> listeners = new ArrayList<>();
					ConfigurationSection section = config.getConfigurationSection(name + ".Sounds").getConfigurationSection(interval);
					if (parsePermission(section.getConfigurationSection("Options"), "PermissionRequired", eventMaker, absentPlayer)) {
						if (section.getString("Sound").startsWith("Note.")) {
							PlayMoreSounds.toggleErrorMessages(false, true, false, false);
						}
						if (main.isIntervalErrorsEnabled ? Utility.isLong(interval) : true) {
							if (main.isSoundErrorsEnabled ? main.soundsNames.contains(section.getString("Sound").toUpperCase().replace("-", "_")) : true) {
								if (main.isVolumeErrorsEnabled ? Utility.isDoubleOrInteger(section.getString("Volume")) : true) {
									if (main.isPitchErrorsEnabled ? Utility.isDoubleOrInteger(section.getString("Pitch")) : true) {
										Double radius;
										if (customRadius == null) {
											radius = section.getDouble("Options.Radius");
										} else {
											radius = customRadius;
										}
										Location soundLocation;
										boolean useHMethods = main.getConfig().getBoolean("UseHardMethods");
										if (customLocation == null) {
											if (absentPlayer) {
												main.logger.warn("Error: CustomLocation is null. The sound " + name + " section [" + i + "], file (" + yaml.getName() + ") could not be played because this sound isn't triggered by a player and the location of the sound could not be found.");
												return;
											} else {
												soundLocation = checkLocation(eventMaker, eventMaker.getLocation(), section);
											}
										} else {
											soundLocation = customLocation;
										}
										if (radius < 0) {
											if (crossWorlds(section)) {
												for (Player online : Bukkit.getOnlinePlayers()) {
													if (parseListenPermission(section.getConfigurationSection("Options"), "PermissionListen", online) & ignoreToggle(online, section.getBoolean("Options.IgnoreToggle"))) {
														if (worldList(online.getWorld())) {
															if (useHMethods) {
																listeners.add(online);
															} else {
																executor(interval, section, name, online, eventMaker, checkLocation(online, online.getLocation(), section), event, radius, listeners);
															}
														}
													}
												}
											} else {
												List<Player> worldPlayers = soundLocation.getWorld().getPlayers();
												if (worldPlayers != null) {
													if (!worldPlayers.isEmpty()) {
														if (worldPlayers.size() != 0) {
															for (Player online : worldPlayers) {
																if (parseListenPermission(section.getConfigurationSection("Options"), "PermissionListen", online) & ignoreToggle(online, section.getBoolean("Options.IgnoreToggle"))) {
																	if (worldList(online.getWorld())) {
																		if (useHMethods) {
																			listeners.add(online);
																		} else {
																			executor(interval, section, name, online, eventMaker, checkLocation(online, online.getLocation(), section), event, radius, listeners);
																		}
																	}
																}
															}
														}
													}
												}
											}
											if (useHMethods) {
												for (Player online : listeners) {
													executor(interval, section, name, online, eventMaker, checkLocation(online, online.getLocation(), section), event, radius, listeners);
												}
											}
										} else if (radius == 0) {
											if (absentPlayer) {
												if (list == null) {
													main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																											"Error (" + yaml.getName() + "): The &nradius&c provided in the event \"" + name + "\", section [" + i
																											+ "] is &nnot compatible&c because this event isn't triggered by a player. Please choose another radius or contact the author of the addon."));
													return;				   
												} else {
													for (Player p : list) {
														if (ignoreToggle(p, section.getBoolean("Options.IgnoreToggle"))) {
															if (parseListenPermission(section.getConfigurationSection("Options"), "PermissionListen", p)) {
																if (worldList(p.getWorld())) {
																	if (useHMethods) {
																		listeners.add(p);
																	} else {
																		executor(interval, section, name, p, eventMaker, checkLocation(p, soundLocation, section), event, radius, listeners);
																	}
																}
															}
														}
													}
													if (useHMethods) {
														for (Player p : listeners) {
															executor(interval, section, name, p, eventMaker, checkLocation(p, soundLocation, section), event, radius, listeners);
														}
													}
												}
											} else {
												if (ignoreToggle(eventMaker, section.getBoolean("Options.IgnoreToggle"))) {
													if (parseListenPermission(section.getConfigurationSection("Options"), "PermissionListen", eventMaker)) {
														if (worldList(eventMaker.getWorld())) {
															executor(interval, section, name, eventMaker, eventMaker, checkLocation(eventMaker, soundLocation, section), event, radius, Arrays.asList(eventMaker));
														}
													}
												}
											}
										} else if (radius > 0) {
											if (worldList(soundLocation.getWorld())) {
												for (Player online : Bukkit.getOnlinePlayers()) {
													if (online.getWorld() == soundLocation.getWorld()) {
														if (online.getLocation().distance(soundLocation) <= radius) {
															if (playerParser(online, eventMaker, absentPlayer)) {
																if (ignoreToggle(online, section.getBoolean("Options.IgnoreToggle"))) {
																	Location loc;
																	if (allInLocation) {
																		loc = checkLocation(online, online.getLocation(), section);
																	} else {
																		loc = soundLocation;
																	}
																	if (useHMethods) {
																		listeners.add(online);
																	} else {
																		executor(interval, section, name, online, eventMaker, loc, event, radius, listeners);
																	}
																}
															}
														}
													}
												}
												if (useHMethods) {
													for (Player online : listeners) {
														Location loc;
														if (allInLocation) {
															loc = checkLocation(online, online.getLocation(), section);
														} else {
															loc = soundLocation;
														}
														executor(interval, section, name, online, eventMaker, loc, event, radius, listeners);
													}
												}
											}
										}
									} else {
										main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																								"Error (" + yaml.getName() + "): The &npitch&c provided in the event \"" + name + "\", section [" + i
																								+ "] is &nnull or not a number&c."));
									}
								} else {
									main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																							"Error (" + yaml.getName() + "): The &nvolume&c provided in the event \"" + name + "\", section [" + i
																							+ "] is &nnull or not a number&c."));
								}
							} else {
								main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																						"Error (" + yaml.getName() + "): The &nsound&c provided in the event \"" + name + "\", section [" + i
																						+ "] is &nnull or invalid&c. Use /pms list to see the list of sounds."));
							}
						} else {
							main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																					"Error (" + yaml.getName() + "): The &ninterval&c provided in the event \"" + name + "\", section [" + i
																					+ "] is &nnull or not an long&c."));
						}
					}
				}
			}
		} catch (Exception e) {
			ErrorReport.errorReport(e, "SoundReproduce Exception: (Unknown)");
			main.logger.warn(ChatColor.translateAlternateColorCodes('&',
																	"Error (" + yaml.getName() + "): An unexpected error happened while playing the sound \"" + name + "\". Check ERROR.LOG for more info."));
		}
	}

	void executor(String interval, final ConfigurationSection section, final String name, final Player playTo, final Player eventMaker, Location soundLocation, final EventName event, final Double radius, final List<Player> listeners) {
		final String sound = section.getString("Sound");
		final float volume = (float) section.getDouble("Volume");
		final float pitch = (float) section.getDouble("Pitch");
		final Location loc;
		if (main.isPositionsEnabled) {
			loc = getPositions(soundLocation, section);
		} else {
			loc = soundLocation;
		}
		final long delay = Utility.longConversor(interval);
		if (delay == 0) {
			callEvent(delay, name, playTo, eventMaker, loc, event, radius, sound, volume, pitch, listeners);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					callEvent(delay, name, playTo, eventMaker, loc, event, radius, sound, volume, pitch, listeners);
				}
			}.runTaskLater(main, delay);
		}
	}

	void play(String name, Player playTo, Location soundLoc, String sound, Float volume, Float pitch) {
		try {
			if (!sound.equalsIgnoreCase("None")) {
				if (sound.startsWith("Note.")) {
					try {
						String[] split = sound.split(";");
						String instrument = split[0].replaceAll("Note.", "");
						playTo.playNote(soundLoc, Instrument.valueOf(instrument.toUpperCase()), new Note(Integer.parseInt(split[1])));
					} catch (Exception l) {
						noteException(l, sound, name);
					}
				} else {
					playTo.playSound(soundLoc, SoundEnum.valueOf(sound.toUpperCase().replace("-", "_").replaceAll("\\.", "_")).bukkitSound(), volume, pitch);
				}
			}
		} catch (Exception e) {
			main.logger.log("Error: Something went wrong while playing the sound " + name + " please check ERROR.LOG");
			ErrorReport.errorReport(e, "SoundReproduce Exception: (Unknown)");
		}
	}

	boolean crossWorlds(ConfigurationSection section) {
		if (section.getConfigurationSection("Options").contains("Radius")) {
			if (section.getConfigurationSection("Options.Radius").contains("CrossWorlds")) {
				return section.getBoolean("Options.Radius.CrossWorlds");
			}
		}
		return false;
	}

	void noteException(Exception e, String sound, String name) {
		String[] split = sound.split(";");
		if (!main.instrumentsNames.contains(split[0].replaceAll("Note.", ""))) {
			main.logger.log("Error: The " + name + " does not have a valid noteblock &ninstrument&r!");
			return;
		}
		if (split.length != 2) {
			main.logger.log("Error: The " + name + " does &nnot have&r a noteblock &nnote&r!");
			return;
		}
		if (!sound.contains(";")) {
			main.logger.log("Error: The " + name + " has a &nmisconfigured&r noteblock &nnote&r!");
			return;
		}
		if (Integer.parseInt(split[1]) < 0) {
			main.logger.log("Error: The " + name + " has a very &nlow&r noteblock &nnote&r!");
			return;
		}
		if (Integer.parseInt(split[1]) > 24) {
			main.logger.log("Error: The " + name + " has a very &nhigh&r noteblock &nnote&r!");
			return;
		}
		main.logger.log("Error: The " + name + " has generated an unexpected exception!");
		ErrorReport.errorReport(e, "NoteblockReproduce Exception: (Unknown)");
	}

	boolean isInvisible(Player p) {
		if (main.getConfig().getBoolean("HideRadiusSoundsTo.InvisibilityEffect.Enabled")) {
			if (main.getConfig().getBoolean("HideRadiusSoundsTo.InvisibilityEffect.OnlyWithPermission")) {
				if (p.hasPermission("playmoresounds.sounds.radius.hide.invisible")) {
					if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						return true;
					}
				}
			} else {
				if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					return true;
				}
			}
		}
		return false;
	}

	boolean ignoreToggle(Player p, boolean bool) {
		if (!bool) {
			if (main.ignoredPlayers.contains(p.getName())) {
				return false;
			}
		}
		return true;
	}

	boolean worldList(World w) {
		if (main.getConfig().contains("World-BlackList")) {
			if (!main.getConfig().getStringList("World-BlackList").contains(w.getName())) {
				return false;
			}
		}
		return true;
	}

	boolean isGamemode(Player p) {
		if (main.getConfig().getBoolean("HideRadiusSoundsTo.Gamemode.Adventure")) {
			if (!p.getGameMode().equals(GameMode.ADVENTURE)) {
				return false;
			}
		}
		if (main.getConfig().getBoolean("HideRadiusSoundsTo.Gamemode.Creative")) {
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				return false;
			}
		}
		if (main.getConfig().getBoolean("HideRadiusSoundsTo.Gamemode.Survival")) {
			if (!p.getGameMode().equals(GameMode.SURVIVAL)) {
				return false;
			}
		}
		if (!Bukkit.getBukkitVersion().contains("1.7")) {
			if (main.getConfig().getBoolean("HideRadiusSoundsTo.Gamemode.Spectator")) {
				if (!p.getGameMode().equals(GameMode.valueOf("SPECTATOR"))) {
					return false;
				}
			}
		}
		return true;
	}

	boolean canSee(Player p, Player soundMaker) {
		if (main.getConfig().getBoolean("HideRadiusSoundsTo.IfCanNotSee")) {
			if (!p.canSee(soundMaker)) {
				return false;
			}
		}
		return true;
	}

	boolean parsePermission(ConfigurationSection section, String permissionPath, Player p, boolean absentPlayer) {
		if (!absentPlayer) {
			if (section.contains(permissionPath)) {
				if (!section.getString(permissionPath).equals("")) {
					if (!p.hasPermission(section.getString(permissionPath))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	boolean parseListenPermission(ConfigurationSection section, String permissionPath, Player p) {
		if (section.contains(permissionPath)) {
			if (!section.getString(permissionPath).equals("")) {
				if (!p.hasPermission(section.getString(permissionPath))) {
					return false;
				}
			}
		}
		return true;
	}

	boolean playerParser(Player other, Player eventMaker, boolean absentPlayer) {
		if (!absentPlayer) {
			if (!canSee(other, eventMaker) | !isGamemode(eventMaker) | isInvisible(eventMaker)) {
				return false;
			}
		}
		return true;
	}

	void callEvent(long interval, String name, Player playTo, Player eventMaker, Location soundLoc, EventName event, Double radius, String sound, Float volume, Float pitch, List<Player> listeners) {
		ReproduceSoundEvent event2 = new ReproduceSoundEvent(eventMaker, soundLoc, event, radius,
															 sound, volume, pitch, listeners, interval);
		Bukkit.getPluginManager().callEvent(event2);
		if (event2.isCancelled()) {
			return;
		}
		if (event2.isCancelledThisRound()) {
			event2.setCancelledThisRound(false);
			return;
		}

		play(name, playTo, event2.getLocation(), event2.getSound(), event2.getVolume(), event2.getPitch());
	}

	Location checkLocation(Player p, Location loc, ConfigurationSection section) {
		if (p != null) {
			if (section.getString("Options.Location").equalsIgnoreCase("Eye")) {
				return p.getEyeLocation();
			}
		}
		return loc;
	}

	public Location getPositions(Location soundLoc, ConfigurationSection section) {
		if (section.getConfigurationSection("Options").contains("SoundPosition")) {
			ConfigurationSection positions = section.getConfigurationSection("Options.SoundPosition");

			boolean bx = true;
			boolean by = true;
			boolean bz = true;

			String front = getString(positions, "Front");
			String back = getString(positions, "Back");
			String up = getString(positions, "Up");
			String down = getString(positions, "Down");
			String right = getString(positions, "Right");
			String left = getString(positions, "Left");

			if (Utility.doubleConversor(front) == 0.0 & Utility.doubleConversor(back) == 0.0) {
				bx = false;
			}
			if (Utility.doubleConversor(up) == 0.0 & Utility.doubleConversor(down) == 0.0) {
				by = false;
			}
			if (Utility.doubleConversor(right) == 0.0 & Utility.doubleConversor(left) == 0.0) {
				bz = false;
			}

			Double FB = 0.0;
			if (bx)
				FB = (Utility.doubleConversor(front)) + (back.startsWith("-") ? Utility.doubleConversor(back.replace("-", "")) : Utility.doubleConversor("-" + back));
			Double UD = 0.0;
			if (by) 
				UD = (Utility.doubleConversor(up)) + (down.startsWith("-") ? Utility.doubleConversor(down.replace("-", "")) : Utility.doubleConversor("-" + down));
			Double LR = 0.0;
			if (bz)
				LR = (Utility.doubleConversor(left)) + (right.startsWith("-") ? Utility.doubleConversor(right.replace("-", "")) : Utility.doubleConversor("-" + right));

			Double newX = soundLoc.getX();
			Double newZ = soundLoc.getZ();
			if (FB != 0.0) {
				newX = (newX + (FB * Math.cos(Math.toRadians(soundLoc.getYaw() + 90 * 1))));
				newZ = (newZ + (FB * Math.sin(Math.toRadians(soundLoc.getYaw() + 90 * 1))));
			}
			if (LR != 0.0) {
				newX = (newX + (LR * Math.cos(Math.toRadians(soundLoc.getYaw() + 90 * 0))));
				newZ = (newZ + (LR * Math.sin(Math.toRadians(soundLoc.getYaw() + 90 * 0))));
			}
			Location loc = new Location(soundLoc.getWorld(), newX, soundLoc.getY() + UD, newZ, soundLoc.getYaw(), soundLoc.getPitch());
			return loc;
		}
		return soundLoc;
	}

	String getString(ConfigurationSection section, String path) {
		if (section.contains(path)) {
			return section.getString(path);
		}
		return "0";
	}
}
