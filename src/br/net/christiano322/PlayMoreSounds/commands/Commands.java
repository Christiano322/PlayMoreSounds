package br.net.christiano322.PlayMoreSounds.commands;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.RegionManager.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.regions.*;
import br.net.christiano322.PlayMoreSounds.regions.listeners.*;
import br.net.christiano322.PlayMoreSounds.sound.*;
import br.net.christiano322.PlayMoreSounds.updater.*;
import br.net.christiano322.PlayMoreSounds.updater.Updater.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import br.net.christiano322.PlayMoreSounds.utils.Utility;

public class Commands implements CommandExecutor {

	private PMS main;

	public Commands(PMS main) {
		this.main = main;
	}

	public HashMap<String, String> confirm = new HashMap<>();

	public void list(int page, List<String> ob, CommandSender sender, String type, String label, String label2) {
		String list = "";
		int amount = main.getConfig().getInt("ListCommand.Amount");
		int soundLength = ob.size();
		int from = (page - 1) * amount;
		int to = from + amount;
		for (int i = from; i < to; i++) {
			if (i < soundLength) {
				if (list.length() != 0) {
					list = list + ChatColor.translateAlternateColorCodes('&',
																		 main.getConfig().getString("ListCommand.Separator"));
				}
				if ((i & 1) == 0) {
					list = list + ChatColor.translateAlternateColorCodes('&',
																		 main.getConfig().getString("ListCommand.Color2")) + ob.get(i);
				} else {
					list = list + ChatColor.translateAlternateColorCodes('&',
																		 main.getConfig().getString("ListCommand.Color")) + ob.get(i);
				}
			}
		}
		int totalPages = soundLength / amount;
		if (soundLength % amount != 0) {
			totalPages++;
		}
		if (page > totalPages) {
			sender.sendMessage(MessageSender.string("General.Prefix") + " "
							   + MessageSender.string("List.Error.NotExists").replace("<totalpages>", totalPages + ""));
			return;
		}
		sender.sendMessage(MessageSender.string("General.Prefix") + " "
						   + MessageSender.string("List.Header").replace("<type>", MessageSender.string(type).toLowerCase())
						   .replace("<page>", page + "").replace("<totalpages>", totalPages + ""));
		sender.sendMessage(list);
		if (page != totalPages) {
			sender.sendMessage(MessageSender.string("List.Footer").replace("<label>", label).replace("<args>", label2)
							   .replace("<type>", MessageSender.string(type).toLowerCase()));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Description:
		if (args.length == 0) {
			if (sender.hasPermission("playmoresounds.help") | sender.hasPermission("playmoresounds.description")) {
				sender.sendMessage(MessageSender.string("Description.Header").replace("<version>", main.pVersion));
				if (sender.hasPermission("playmoresounds.description")) {
					sender.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.GRAY + "Christiano322");
				}
				if (sender.hasPermission("playmoresounds.help")) {
					sender.sendMessage(MessageSender.string("Description.Help").replace("<label>", label));
				}
			} else {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
			}
			return true;
		}
		// Command play:
		if (args[0].equalsIgnoreCase("play")) {
			try {
				if (!sender.hasPermission("playmoresounds.play")) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.NoPermission"));
					return true;
				}
				if (args.length > 1) {
					List<Player> players =  new ArrayList<>();
					String player = MessageSender.string("General.You");
					if (args.length > 2) {
						if (args[2].equalsIgnoreCase("me")) {
							if (sender instanceof Player) {
								players.add((Player) sender);
							} else {
								sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotAPlayer"));
								return true;
							}
						} else if (args[2].equalsIgnoreCase("*") | args[2].equalsIgnoreCase("everyone") | args[2].equalsIgnoreCase("all")) {
							if (Bukkit.getOnlinePlayers().size() > 0) {
								players.addAll(Bukkit.getOnlinePlayers());
								player = MessageSender.string("General.Everyone");
							} else {
								sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NobodyOnline"));
								return true;
							}
						} else if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[2]))) {
							players.add(Bukkit.getPlayer(args[2]));
							if (!args[2].equalsIgnoreCase(sender.getName())) {
								player = Bukkit.getPlayer(args[2]).getName();
							}
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.PlayerNotFound").replace("<player>", args[2]));
							return true;
						}
					} else {
						if (sender instanceof Player) {
							players.add((Player) sender);
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotAPlayer"));
							return true;
						}
					}
					if (args[1].startsWith("config:")) {
						if (main.sounds.getKeys(false).contains(args[1].replace("config:", ""))) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Play.Success")
											   .replace("<sound>", PlayMoreSounds.getSoundOf(args[1].replace("config:", ""), 0))
											   .replace("<player>", player).replace("<volume>", PlayMoreSounds.getVolumeOf(args[1].replace("config:", ""), 0).toString())
											   .replace("<pitch>", PlayMoreSounds.getPitchOf(args[1].replace("config:", ""), 0).toString()));
							new SoundPlayer(main).playSound(EventName.CUSTOM, main.soundsFile, main.sounds, null, args[1].replace("config:", ""), null, players.get(0).getLocation(), false, players);
							return true;
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Play.Error.NotExists"));
							return true;
						}
					} else {
						if (main.soundsNames.contains(args[1].toUpperCase().replace("-", "_"))) {
							double volume = main.getConfig().getDouble("PlayCommandDefaultVolume");
							double pitch = main.getConfig().getDouble("PlayCommandDefaultPitch");
							if (args.length > 3) {
								if (Utility.isDoubleOrInteger(args[3])) {
									volume = Double.valueOf(args[3]);
								} else {
									sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotANumber").replace("<number", args[3]));
									return true;
								}
							}
							if (args.length > 4) {
								if (Utility.isDoubleOrInteger(args[4])) {
									pitch = Double.valueOf(args[4]);
								} else {
									sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotANumber").replace("<number", args[4]));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Play.Success")
											   .replace("<sound>", args[1])
											   .replace("<player>", player).replace("<volume>", String.valueOf(volume))
											   .replace("<pitch>", String.valueOf(pitch)));
							for (Player play : players) {
								play.playSound(play.getLocation(), SoundEnum.valueOf(args[1]).bukkitSound(), (float) volume, (float) pitch);
							}
							return true;
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Play.Error.NotExists"));
							return true;
						}
					}
				}
				if (sender instanceof Player) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.InvalidArguments").replace("<label>", label)
									   .replace("<label2>", args[0])
									   .replace("<args>", "<sound>|config:<config> [player] [volume] [pitch]"));
				} else {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.InvalidArguments").replace("<label>", label)
									   .replace("<label2>", args[0])
									   .replace("<args>", "<sound>|config:<config> <player> [volume] [pitch]"));
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				ErrorReport.errorReport(e, "Command Exception:");
			}
		}
		// Command region:
		if ((args[0].equalsIgnoreCase("regions")) | (args[0].equalsIgnoreCase("region"))
			| (args[0].equalsIgnoreCase("rg"))) {
			if (!sender.hasPermission("playmoresounds.region")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			if (args.length == 1) {
				if ((!sender.hasPermission("playmoresounds.region.create"))
					& (!sender.hasPermission("playmoresounds.region.delete"))
					& (!sender.hasPermission("playmoresounds.region.rename"))
					& (!sender.hasPermission("playmoresounds.region.set"))
					& (!sender.hasPermission("playmoresounds.region.wand"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.NoPermission"));
					return true;
				}
				if (sender instanceof Player) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.InvalidArguments")
									   .replace("<args>", "<create|delete|rename|set|wand>").replace("<label>", label)
									   .replace("<label2>", args[0]));
				} else {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.InvalidArguments").replace("<args>", "<delete|rename>")
									   .replace("<label>", label).replace("<label2>", args[0]));
				}
				return true;
			}
			if (args.length > 1) {
				if (args.length == 2) {
					if ((args[1].equalsIgnoreCase("create")) | (args[1].equalsIgnoreCase("new"))) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.InvalidArguments")
										   .replace("<args>", args[1] + " <" + MessageSender.string("Region.Region") + ">")
										   .replace("<label>", label).replace("<label2>", args[0]));
						return true;
					}
					if ((args[1].equalsIgnoreCase("delete")) | (args[1].equalsIgnoreCase("del"))
						| (args[1].equalsIgnoreCase("remove"))) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.InvalidArguments")
										   .replace("<args>", args[1] + " <" + MessageSender.string("Region.Region") + ">")
										   .replace("<label>", label).replace("<label2>", args[0]));
						return true;
					}
					if (args[1].equalsIgnoreCase("set")) {
						if (sender instanceof Player) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.InvalidArguments")
											   .replace("<args>", args[1] + " <p1|p2> [<x> <y> <z>]").replace("<label>", label)
											   .replace("<label2>", args[0]));
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NotAPlayer"));
						}
						return true;
					}
				}
				if (args[1].equalsIgnoreCase("wand")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						ItemStack item = new ItemStack(
							Material.valueOf(main.getConfig().getString("SoundRegions.WandTool.Material")), 1);
						ItemMeta meta = item.getItemMeta();
						meta.setUnbreakable(true);
						if (main.getConfig().getBoolean("SoundRegions.WandTool.Glowing")) {
							meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
							meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						}
						meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
																				   main.getConfig().getString("SoundRegions.WandTool.Name")));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
						sender.sendMessage(
							MessageSender.string("General.Prefix") + " " + MessageSender.string("Region.Wand"));
					} else {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NotAPlayer"));
					}
					return true;
				}
			}
			if (args.length > 2) {
				if (args[1].equalsIgnoreCase("set")) {
					if (!sender.hasPermission("playmoresounds.region.set")) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NoPermission"));
						return true;
					}
					if (sender instanceof Player) {
						Player p = (Player) sender;
						Location loc = p.getLocation();
						if ((args[2].equalsIgnoreCase("p1")) | (args[2].equalsIgnoreCase("1"))) {
							AreaSelector.p1.put(sender.getName(), loc);
							p.sendMessage(MessageSender.string("General.Prefix") + " "
										  + MessageSender.string("Region.PositionSelected1").replace("<coordinates>",
																									 "[X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:"
																									 + loc.getBlockZ() + ", W:" + loc.getWorld().getName() + "]"));
						}
						if ((args[2].equalsIgnoreCase("p2")) | (args[2].equalsIgnoreCase("2"))) {
							AreaSelector.p2.put(sender.getName(), loc);
							p.sendMessage(MessageSender.string("General.Prefix") + " "
										  + MessageSender.string("Region.PositionSelected2").replace("<coordinates>",
																									 "[X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:"
																									 + loc.getBlockZ() + ", W:" + loc.getWorld().getName() + "]"));
						}
					} else {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NotAPlayer"));
					}
					return true;
				}
				if ((args[1].equalsIgnoreCase("delete")) | (args[1].equalsIgnoreCase("del"))
					| (args[1].equalsIgnoreCase("remove"))) {
					if (!sender.hasPermission("playmoresounds.region.delete")) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NoPermission"));
						return true;
					}
					File folder = new File(main.getDataFolder(), "regions");
					List<String> files = new ArrayList<>();
					List<String> regions = new ArrayList<>();
					if (folder.exists()) {
						if (folder.listFiles().length > 0) {
							for (File file : folder.listFiles()) {
								if (file.getName().endsWith(".yml")) {
									if (YamlConfiguration.loadConfiguration(file).contains("Name")) {
										regions.add(YamlConfiguration.loadConfiguration(file).getString("Name"));
										files.add(file.getName().replace(".yml", ""));
									}
								}
							}
						}
					}
					if (regions.contains(args[2])) {
						try {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Region.Delete.Process").replace("<region>", args[2]));
							for (File file : folder.listFiles()) {
								if (YamlConfiguration.loadConfiguration(file).getString("Name").contains(args[2])) {
									file.delete();
								}
							}
						} catch (Exception e) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Region.Delete.Error.Default").replace("<region>", args[2]));
							return true;
						}
						return true;
					}
					if (files.contains(args[2].toLowerCase())) {
						try {
							sender.sendMessage(
								MessageSender.string("General.Prefix") + " "
								+ MessageSender.string("Region.Delete.Process")
								.replace("<region>",
										 YamlConfiguration
										 .loadConfiguration(new File(main.getDataFolder(),
																	 "regions"
																	 + System.getProperty(
																		 "file.separator")
																	 + args[2].toLowerCase() + ".yml"))
										 .getString("Name")));
							new File(folder, args[2].toLowerCase() + ".yml").delete();
						} catch (Exception e) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Region.Delete.Error.Default").replace("<region>", args[2]));
						}
						return true;
					}
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Region.Delete.Error.NotExists").replace("<region>", args[2]));
					return true;
				}
				if ((args[1].equalsIgnoreCase("create")) | (args[1].equalsIgnoreCase("new"))) {
					if (!sender.hasPermission("playmoresounds.region.create")) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NoPermission"));
						return true;
					}
					if (sender instanceof Player) {
						if ((AreaSelector.p1.containsKey(sender.getName())
							& (AreaSelector.p2.containsKey(sender.getName())))) {
							RegionCreationResult r = PlayMoreSounds.getRegionManager().createRegion(
								AreaSelector.p1.get(sender.getName()), AreaSelector.p2.get(sender.getName()),
								args[2]);
							if (r.equals(RegionCreationResult.UNEXPECTED)) {
								sender.sendMessage(MessageSender.string("Prefix") + " " + MessageSender
												   .string("Region.Creation.Error.Default").replace("<region>", args[2]));
								return true;
							}
							if (r.equals(RegionCreationResult.ALREADY_EXISTS)) {
								sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
												   .string("Region.Creation.Error.AlreadyExists").replace("<region>", args[2]));
							}
							if (r.equals(RegionCreationResult.DIFFERENT_WORLDS)) {
								sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
												   .string("Region.Creation.Error.DifferentWorlds").replace("<region>", args[2]));
							}
							if (r.equals(RegionCreationResult.SUCCESS)) {
								sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
												   .string("Region.Creation.Success").replace("<region>", args[2]));
							}
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Region.Creation.Error.NotSelected")
											   .replace("<label>", label).replace("<label2>", args[0]));
						}
					} else {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.NotAPlayer"));
					}
					return true;
				}
				if (args.length > 3) {
					if (args[1].equalsIgnoreCase("rename")) {
						if (!sender.hasPermission("playmoresounds.region.rename")) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						}
						try {
							List<String> regions = new ArrayList<>();
							File folder = new File(main.getDataFolder(), "regions");
							for (File s : folder.listFiles()) {
								if (s.getName().endsWith(".yml")) {
									if (YamlConfiguration.loadConfiguration(s).contains("Name")) {
										regions.add(YamlConfiguration.loadConfiguration(s).getString("Name"));
									}
								}
							}
							if (sender instanceof Player) {
								if (args[2].equalsIgnoreCase("-here")) {
									if (PlayMoreSounds.getRegionManager()
										.isInsideARegion(((Player) sender).getLocation())) {
										SoundRegion region = PlayMoreSounds.getRegionManager()
											.getRegionByLocation(((Player) sender).getLocation());
										region.renameTo(args[3]);
										sender.sendMessage(MessageSender.string("General.Prefix") + " "
														   + MessageSender.string("Region.Rename.Success")
														   .replace("<region>", region.getId())
														   .replace("<newname>", args[3]));
									} else {
										sender.sendMessage(MessageSender.string("General.Prefix") + " "
														   + MessageSender.string("Region.Rename.Error.NotInside"));
									}
								} else {
									if (regions.contains(args[2])) {
										SoundRegion region = PlayMoreSounds.getRegionManager().getRegionByName(args[2]);
										region.renameTo(args[3]);
										sender.sendMessage(MessageSender.string("General.Prefix") + " "
														   + MessageSender.string("Region.Rename.Success")
														   .replace("<region>", args[2]).replace("<newname>", args[3]));
										return true;
									} else {
										sender.sendMessage(MessageSender.string("General.Prefix") + " "
														   + MessageSender.string("Region.Rename.Error.NotFound"));
									}
								}
							} else {
								if (regions.contains(args[2])) {
									SoundRegion region = PlayMoreSounds.getRegionManager().getRegionByName(args[2]);
									region.renameTo(args[3]);
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("Region.Rename.Success").replace("<region>", args[2])
													   .replace("<newname>", args[3]));
									return true;
								} else {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("Region.Rename.Error.NotFound"));
								}
							}
						} catch (Exception e) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Region.Rename.Error.Default"));
							ErrorReport.errorReport(e, "Command Exception:");
							return true;
						}
						return true;
					}
					if (args.length > 5) {
						if (args[1].equalsIgnoreCase("set")) {
							if (!sender.hasPermission("playmoresounds.region.set")) {
								sender.sendMessage(MessageSender.string("General.Prefix") + " "
												   + MessageSender.string("General.NoPermission"));
								return true;
							}
							if (sender instanceof Player) {
								if ((!args[2].equalsIgnoreCase("p1")) & (!args[2].equalsIgnoreCase("1"))
									& (!args[2].equalsIgnoreCase("p2")) & (!args[2].equalsIgnoreCase("2"))) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.InvalidArguments").replace("<label>", label)
													   .replace("<label2>", args[0])
													   .replace("<args>", "<p1|p2> <X> <Y> <Z>"));
									return true;
								}
								Player p = (Player) sender;
								if (!Utility.isInteger(args[3])) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.NotANumber").replace("<number>", args[3]));
									return true;
								}
								if (!Utility.isInteger(args[4])) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.NotANumber").replace("<number>", args[4]));
									return true;
								}
								if (!Utility.isInteger(args[5])) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.NotANumber").replace("<number>", args[5]));
									return true;
								}
								Location loc = new Location(p.getWorld(), Integer.parseInt(args[3]),
															Integer.parseInt(args[4]), Integer.parseInt(args[5]));
								if ((args[2].equalsIgnoreCase("p1")) | (args[2].equalsIgnoreCase("1"))) {
									if (AreaSelector.p1.containsKey(sender.getName())) {
										if (AreaSelector.p1.get(sender.getName()) == loc) {
											return true;
										}
									}
									AreaSelector.p1.put(sender.getName(), loc);
									p.sendMessage(MessageSender.string("General.Prefix") + " "
												  + MessageSender.string("Region.PositionSelected1").replace("<coordinates>",
																											 "[X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:"
																											 + loc.getBlockZ() + ", W:" + loc.getWorld().getName()
																											 + "]"));
								}
								if ((args[2].equalsIgnoreCase("p2")) | (args[2].equalsIgnoreCase("2"))) {
									if (AreaSelector.p2.containsKey(sender.getName())) {
										if (AreaSelector.p2.get(sender.getName()) == loc) {
											return true;
										}
									}
									AreaSelector.p2.put(sender.getName(), loc);
									p.sendMessage(MessageSender.string("General.Prefix") + " "
												  + MessageSender.string("Region.PositionSelected2").replace("<coordinates>",
																											 "[X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:"
																											 + loc.getBlockZ() + ", W:" + loc.getWorld().getName()
																											 + "]"));
								}
							} else {
								sender.sendMessage(MessageSender.string("General.Prefix") + " "
												   + MessageSender.string("General.NotAPlayer"));
							}
							return true;
						}
					}
				}
			}
			if (sender instanceof Player) {
				sender.sendMessage(MessageSender.string("General.Prefix") + " "
								   + MessageSender.string("General.InvalidArguments").replace("<label>", label)
								   .replace("<label2>", args[0]).replace("<args>", "<create|delete|rename|set|wand>"));
			} else {
				sender.sendMessage(MessageSender.string("General.Prefix") + " "
								   + MessageSender.string("General.InvalidArguments").replace("<label>", label)
								   .replace("<label2>", args[0]).replace("<args>", "<delete|rename>"));
			}
			return true;
		}
		// Command reload:
		if (args[0].equalsIgnoreCase("reload")) {
			try {
				if (!sender.hasPermission("playmoresounds.reload")) {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
					return true;
				}
				main.checkUpdates(true);
				main.reloadConfig();
				main.loadConfiguration();
				main.reloadConfig();
				main.sounds = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds.yml"));
				if (main.getConfig().getConfigurationSection("PerGamemodeSounds").getBoolean("Enabled")) {
					main.gamemodes = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/gamemodes.yml"));
				}
				if (main.getConfig().getConfigurationSection("PerChatWordSounds").getBoolean("Enabled")) {
					main.chatwords = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/chatwords.yml"));
				}
				if (main.getConfig().getConfigurationSection("PerCommandSounds").getBoolean("Enabled")) {
					main.commands = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/commands.yml"));
				}
				main.regions = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/regions.yml"));
				main.customsounds = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/addons.yml"));
				main.worldsounds = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "sounds/worldsounds.yml"));
				if (main.getConfig().getBoolean("ExtractLanguageFiles")) {
					main.language = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "lang/language_"
																				 + main.getConfig().getString("Localization").toLowerCase() + ".properties"));
				} else {
					main.language = YamlConfiguration.loadConfiguration(new InputStreamReader(main.getResource("lang/language_" + main.getConfig().getString("Localization").toLowerCase() + ".properties")));
				}
				main.languageEn = YamlConfiguration.loadConfiguration(new InputStreamReader(PMS.plugin.getResource("lang/language_en.properties")));
				main.languageJar = YamlConfiguration.loadConfiguration(new InputStreamReader(PMS.plugin.getResource("lang/language_" + main.getConfig().getString("Localization").toLowerCase() + ".properties")));
				main.loadEvents();
				sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Reload"));
				if (sender instanceof Player) {
					Player p = (Player) sender;
					new SoundPlayer(main).playSound(EventName.CUSTOM, main.configFile, main.getConfig(), p, "ReloadCommand", null, null, false, null);
				}
			} catch (Exception e) {
				ErrorReport.errorReport(e, "ConfigurationReload Exception:");
				sender.sendMessage(MessageSender.string("General.Prefix") + ChatColor.RED + " Something went wrong. Check ERROR.LOG for more info.");
			}
			return true;
		}
		// Command restore:
		if (args[0].equalsIgnoreCase("restore")) {
			if (!sender.hasPermission("playmoresounds.restore")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			if (confirm.containsKey(sender.getName())) {
				confirm.remove(sender.getName());
			}
			if (args.length == 1) {
				sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Restore.Files")
								   .replace("<lang>", main.getConfig().getString("Localization").toUpperCase()));
				return true;
			}
			if (args.length > 1) {
				String senderName = sender.getName();
				if ((args[1].equalsIgnoreCase("all"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Restore.Confirm")
									   .replace("<file>", MessageSender.string("General.AllFiles").toLowerCase())
									   .replace("<label>", label));
					confirm.put(senderName, "all");
					return true;
				}
				if ((args[1].equalsIgnoreCase("config"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "config.yml").replace("<label>", label));
					confirm.put(senderName, "config");
					return true;
				}
				if ((args[1].equalsIgnoreCase("lang" + main.getConfig().getString("Localization")))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Restore.Confirm")
									   .replace("<file>", "language_"
												+ main.getConfig().getString("Localization").toLowerCase() + ".properties")
									   .replace("<label>", label));
					confirm.put(senderName, "language");
					return true;
				}
				if ((args[1].equalsIgnoreCase("commands"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "commands.yml").replace("<label>", label));
					confirm.put(senderName, "commands");
					return true;
				}
				if ((args[1].equalsIgnoreCase("gamemodes"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "gamemodes.yml").replace("<label>", label));
					confirm.put(senderName, "gamemodes");
					return true;
				}
				if ((args[1].equalsIgnoreCase("sounds"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "sounds.yml").replace("<label>", label));
					confirm.put(senderName, "sounds");
					return true;
				}
				if ((args[1].equalsIgnoreCase("chatwords"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "chatwords.yml").replace("<label>", label));
					confirm.put(senderName, "chatwords");
					return true;
				}
				if ((args[1].equalsIgnoreCase("regions"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "regions.yml").replace("<label>", label));
					confirm.put(senderName, "regions");
					return true;
				}
				if ((args[1].equalsIgnoreCase("addonsconf"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "addons.yml").replace("<label>", label));
					confirm.put(senderName, "addonsconf");
					return true;
				}
				if ((args[1].equalsIgnoreCase("worldsounds"))) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
									   .string("Restore.Confirm").replace("<file>", "worldsounds.yml").replace("<label>", label));
					confirm.put(senderName, "worldsounds");
					return true;
				}
				sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender
								   .string("Restore.Error.NotAFile").replace("<file>", args[1]).replace("<label>", label));
			}
			return true;
		}
		// Command confirm:
		if (args[0].equalsIgnoreCase("confirm")) {
			if (!sender.hasPermission("playmoresounds.confirm")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			String senderName = sender.getName();
			if (confirm.containsKey(senderName)) {
				if (confirm.get(senderName).equals("all")) {
					try {
						main.Restore(PMSFile.ADDONS);
						main.Restore(PMSFile.CHATWORDS);
						main.Restore(PMSFile.COMMANDS);
						main.Restore(PMSFile.CONFIG);
						main.Restore(PMSFile.GAMEMODES);
						main.Restore(PMSFile.REGIONS);
						main.Restore(PMSFile.SOUNDS);
						main.Restore(PMSFile.WORLDSOUNDS);
						main.saveResource(
							"lang/language_" + main.getConfig().getString("Localization").toLowerCase() + ".properties",
							true);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Plural").replace("<file>",
																									MessageSender.string("General.AllFiles")));
					} catch (Exception e) {
						sender.sendMessage(
							MessageSender.string("General.Prefix") + " " + MessageSender.string("Restore.Error.Default")
							.replace("<file>", MessageSender.string("General.AllFiles").toLowerCase()));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("config")) {
					try {
						main.Restore(PMSFile.CONFIG);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Config.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "config.yml"));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("language")) {
					try {
						main.saveResource(
							"lang/language_" + main.getConfig().getString("Localization").toLowerCase() + ".properties",
							true);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Language_"
																									 + main.getConfig().getString("Localization").toLowerCase() + ".properties"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "language_"
																								   + main.getConfig().getString("Localization").toLowerCase() + ".properties"));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("commands")) {
					try {
						main.Restore(PMSFile.COMMANDS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Commands.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "commands.yml"));
					}
					confirm.remove(senderName);
					return true;
				} else 
				if (confirm.get(senderName).equals("sounds")) {
					try {
						main.Restore(PMSFile.SOUNDS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Sounds.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "sounds.yml"));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("gamemodes")) {
					try {
						main.Restore(PMSFile.GAMEMODES);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Gamemodes.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "gamemodes.yml"));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("regions")) {
					try {
						main.Restore(PMSFile.REGIONS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Regions.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "regions.yml"));
					}
					confirm.remove(senderName);
					return true;
				} else
				if (confirm.get(senderName).equals("chatwords")) {
					try {
						main.Restore(PMSFile.CHATWORDS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Chatwords.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "chatwords.yml"));
					}
					confirm.remove(senderName);
					return true;
				}
				if (confirm.get(senderName).equals("addonsconf")) {
					try {
						main.Restore(PMSFile.ADDONS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Addons.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "Addons.yml"));
					}
					confirm.remove(senderName);
					return true;
				}
				if (confirm.get(senderName).equals("worldsounds")) {
					try {
						main.Restore(PMSFile.WORLDSOUNDS);
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Success.Default").replace("<file>", "Worldsounds.yml"));
					} catch (Exception e) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Restore.Error.Default").replace("<file>", "Worldsounds.yml"));
					}
					confirm.remove(senderName);
					return true;
				}
			} else {
				sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Confirm.Error"));
			}
			return true;
		}
		// Command help:
		if (args[0].equalsIgnoreCase("help")) {
			if (sender.hasPermission("playmoresounds.help")) {
				int page = 1;
				if (args.length > 1) {
					if (Utility.isInteger(args[1])) {
						if (Integer.parseInt(args[1]) > 1) {
							page = Integer.parseInt(args[1]);
						}
					} else {
						if (args[1].equalsIgnoreCase("confirm")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.confirm")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "confirm:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>", "0"));
									sender.sendMessage(MessageSender.string("Help.Confirm").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("edit")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.edit")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "edit:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																									 "2 <config> <section>"));
									sender.sendMessage(MessageSender.string("Help.Edit").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("list")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.list")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "list:");
									sender.sendMessage(
										MessageSender.string("Help.Required").replace("<args>", "Optional [page]"));
									sender.sendMessage(MessageSender.string("Help.List").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("play")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.play")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "play:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																									 (sender instanceof Player ? "1 <sound>" : "2 <sound> <player>")));
									sender.sendMessage(MessageSender.string("Help.Play").replace("<label>", label)
													   .replace("<arg>", (sender instanceof Player ? "[player]" : "<player>"))
													   .replace("<arg2>", "[vol]").replace("<arg3>", "[pitch]"));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if ((args[1].equalsIgnoreCase("region")) | (args[1].equalsIgnoreCase("regions"))
								   | (args[1].equalsIgnoreCase("rg"))) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.region")) {
									if (args.length > 2) {
										if ((args[2].equalsIgnoreCase("create")) | (args[2].equalsIgnoreCase("new"))) {
											if (sender.hasPermission("playmoresounds.region.create")) {
												sender.sendMessage(MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("General.Command") + " " + "region "
																   + args[2] + ":");
												sender.sendMessage(MessageSender.string("Help.Required")
																   .replace("<args>", "1 <region name>"));
												sender.sendMessage(MessageSender.string("Help.Region.Create")
																   .replace("<label>", label));
												return true;
											}
										} else if ((args[2].equalsIgnoreCase("delete"))
												   | (args[2].equalsIgnoreCase("del"))
												   | (args[2].equalsIgnoreCase("remove"))) {
											if (sender.hasPermission("playmoresounds.region.delete")) {
												sender.sendMessage(MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("General.Command") + " " + "region "
																   + args[2] + ":");
												sender.sendMessage(MessageSender.string("Help.Required")
																   .replace("<args>", "1 <region name>"));
												sender.sendMessage(MessageSender.string("Help.Region.Delete")
																   .replace("<label>", label));
												return true;
											}
										} else if (args[2].equalsIgnoreCase("set")) {
											if (sender.hasPermission("playmoresounds.region.set")) {
												sender.sendMessage(MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("General.Command") + " "
																   + "region set:");
												sender.sendMessage(MessageSender.string("Help.Required")
																   .replace("<args>", "1 <p1|p2> [<x> <y> <z>]"));
												sender.sendMessage(MessageSender.string("Help.Region.Set")
																   .replace("<label>", label));
												return true;
											}
										} else if (args[2].equalsIgnoreCase("rename")) {
											if (sender.hasPermission("playmoresounds.region.rename")) {
												sender.sendMessage(MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("General.Command") + " "
																   + "region rename:");
												sender.sendMessage(MessageSender.string("Help.Required")
																   .replace("<args>", "2 <oldname> <newname>"));
												sender.sendMessage(MessageSender.string("Help.Region.Rename")
																   .replace("<label>", label));
												return true;
											}
										} else if (args[2].equalsIgnoreCase("wand")) {
											if (sender.hasPermission("playmoresounds.region.wand")) {
												sender.sendMessage(MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("General.Command") + " "
																   + "region wand:");
												sender.sendMessage(
													MessageSender.string("Help.Required").replace("<args>", "0"));
												sender.sendMessage(MessageSender.string("Help.Region.Wand")
																   .replace("<label>", label));
												return true;
											}
										} else {
											sender.sendMessage(MessageSender.string("General.Prefix") + " "
															   + MessageSender.string("General.Command") + " " + "region:");
											sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																											 "2 <create|delete|rename|set|wand> <argument>"));
											sender.sendMessage(MessageSender.string("Help.Region.Default")
															   .replace("<label>", label));
											return true;
										}
									}
									if (args.length == 2) {
										sender.sendMessage(MessageSender.string("General.Prefix") + " "
														   + MessageSender.string("General.Command") + " " + "region:");
										sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																										 "2 <create|delete|rename|set|wand> <argument>"));
										sender.sendMessage(
											MessageSender.string("Help.Region.Default").replace("<label>", label));
										return true;
									}
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("reload")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.reload")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "reload:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>", "0"));
									sender.sendMessage(MessageSender.string("Help.Reload").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("restore")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.restore")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "restore:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																									 "1 <allfiles|filename>"));
									sender.sendMessage(MessageSender.string("Help.Restore").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("sounds")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.toggle.check")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "sounds:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																									 "Optional [player]"));
									sender.sendMessage(MessageSender.string("Help.Sounds").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("toggle")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.toggle")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "toggle:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>",
																									 "Optional [player]"));
									sender.sendMessage(MessageSender.string("Help.Toggle").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else if (args[1].equalsIgnoreCase("update")) {
							if (sender.hasPermission("playmoresounds.commandhelp")) {
								if (sender.hasPermission("playmoresounds.update")) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("General.Command") + " " + "update:");
									sender.sendMessage(MessageSender.string("Help.Required").replace("<args>", "0"));
									sender.sendMessage(MessageSender.string("Help.Update").replace("<label>", label));
									return true;
								}
							}
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NoPermission"));
							return true;
						} else {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("General.NotANumber").replace("<number>", args[1]));
							return true;
						}
					}
				}
				if (sender instanceof Player) {
					List<String> commands = new ArrayList<>();
					if (main.getConfig().getBoolean("HelpBasedOnPermissions")) {
						if (sender.hasPermission("playmoresounds.confirm")) {
							commands.add(MessageSender.string("Help.Confirm").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.edit")) {
							commands.add(MessageSender.string("Help.Edit").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.commandhelp")) {
							commands.add(MessageSender.string("Help.Help").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.list")) {
							commands.add(MessageSender.string("Help.List").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.play")) {
							if (sender.hasPermission("playmoresounds.play.others")) {
								commands.add(MessageSender.string("Help.Play").replace("<label>", label)
											 .replace("<arg>", "[player|me|*]").replace("<arg2>", "[vol]")
											 .replace("<arg3>", "[pitch]"));
							} else {
								commands.add(MessageSender.string("Help.Play").replace("<label>", label)
											 .replace("<arg>", "[" + sender.getName() + "|me]").replace("<arg2>", "[vol]")
											 .replace("<arg3>", "[pitch]"));
							}
						}
						if (sender.hasPermission("playmoresounds.region")) {
							commands.add(MessageSender.string("Help.Region.Default").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.reload")) {
							commands.add(MessageSender.string("Help.Reload").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.restore")) {
							commands.add(MessageSender.string("Help.Restore").replace("<label>", label));
						}
						if (sender.hasPermission("playmoresounds.toggle.check")) {
							if (sender.hasPermission("playmoresounds.toggle.check.others")) {
								commands.add(MessageSender.string("Help.Sounds").replace("<label>", label)
											 .replace("<arg>", "[player]"));
							} else {
								commands.add(MessageSender.string("Help.Sounds").replace("<label>", label)
											 .replace("<arg>", ""));
							}
						}
						if (sender.hasPermission("playmoresounds.toggle")) {
							if (sender.hasPermission("playmoresounds.toggle.others")) {
								commands.add(MessageSender.string("Help.Toggle").replace("<label>", label)
											 .replace("<arg>", "[player]"));
							} else {
								commands.add(MessageSender.string("Help.Toggle").replace("<label>", label)
											 .replace("<arg>", ""));
							}
						}
						if (sender.hasPermission("playmoresounds.update")) {
							commands.add(MessageSender.string("Help.Update").replace("<label>", label));
						}
					} else {
						commands.add(MessageSender.string("Help.Confirm").replace("<label>", label));
						commands.add(MessageSender.string("Help.Edit").replace("<label>", label));
						commands.add(MessageSender.string("Help.Help").replace("<label>", label));
						commands.add(MessageSender.string("Help.List").replace("<label>", label));
						commands.add(MessageSender.string("Help.Play").replace("<label>", label)
									 .replace("<arg>", "[player|me|*]").replace("<arg2>", "[vol]")
									 .replace("<arg3>", "[pitch]"));
						commands.add(MessageSender.string("Help.Region.Default").replace("<label>", label));
						commands.add(MessageSender.string("Help.Reload").replace("<label>", label));
						commands.add(MessageSender.string("Help.Restore").replace("<label>", label));
						commands.add(MessageSender.string("Help.Sounds").replace("<label>", label).replace("<arg>",
																										   "[player]"));
						commands.add(MessageSender.string("Help.Toggle").replace("<label>", label).replace("<arg>",
																										   "[player]"));
						commands.add(MessageSender.string("Help.Update").replace("<label>", label));
					}
					String list = "";
					int soundLength = commands.size();
					int from = (page - 1) * 3;
					int to = from + 3;
					for (int i = from; i < to; i++) {
						if (i < soundLength) {
							if (list.length() != 0) {
								list = list + '\n';
							}
							list = list + commands.get(i).toString();
						}
					}
					int totalPages = soundLength / 3;
					if (soundLength % 3 != 0) {
						totalPages++;
					}
					if (page > totalPages) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Help.Page.NotExists"));
						return true;
					}
					sender.sendMessage(MessageSender.string("Help.Header").replace("<s>", " ")
									   .replace("<page>", "" + page).replace("<totalpages>", "" + totalPages));
					sender.sendMessage("");
					sender.sendMessage(list);
					sender.sendMessage("");
					int arg = page + 1;
					if (page < totalPages) {
						Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "minecraft:tellraw "
											   + sender.getName() + " [\"\",{\"text\":\""
											   + MessageSender.string("Help.Page.More").replace("<label>", label)
											   .replace("<page>", arg + "").replace("&", "�")
											   + "\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + "/pms help " + arg
											   + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
											   + MessageSender.string("Help.Page.Click").replace("<page>", arg + "").replace("&", "�")
											   + "\"}]}}}]");
					}
				} else {
					sender.sendMessage(MessageSender.string("Help.Header").replace(" ", "-").replace("<s>", " ")
									   .replace("<page>", "1").replace("<totalpages>", "1"));
					sender.sendMessage(MessageSender.string("Help.Confirm").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.Edit").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.Help").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.List").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.Play").replace("<label>", label)
									   .replace("<arg>", "<player|*>").replace("<arg2>", "[vol]").replace("<arg3>", "[pitch]"));
					sender.sendMessage(MessageSender.string("Help.Region.Default").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.Reload").replace("<label>", label));
					sender.sendMessage(MessageSender.string("Help.Restore").replace("<label>", label));
					sender.sendMessage(
						MessageSender.string("Help.Sounds").replace("<label>", label).replace("<arg>", "<player>"));
					sender.sendMessage(
						MessageSender.string("Help.Toggle").replace("<label>", label).replace("<arg>", "<player>"));
					sender.sendMessage(MessageSender.string("Help.Update").replace("<label>", label));
				}
			} else {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
			}
			return true;
		}
		// Command sounds:
		if (args[0].equalsIgnoreCase("sounds")) {
			if (!sender.hasPermission("playmoresounds.toggle.check")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			if (args.length == 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotAPlayer"));
					return true;
				}
				Player p = (Player) sender;
				if (main.ignoredPlayers.contains(p.getName())) {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("Sounds.Disabled.Default"));
				} else {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("Sounds.Enabled.Default"));
					if (main.getConfig().contains("World-BlackList")) {
						if (main.getConfig().getStringList("World-BlackList").contains(p.getWorld().getName())) {
							sender.sendMessage(MessageSender.string("Toggle.Error.BlackListedWorld"));
						}
					}
				}
			} else {
				if (!sender.hasPermission("playmoresounds.toggle.check.others")) {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
					return true;
				}
				if (containsP(Bukkit.getOnlinePlayers(), args[1])) {
					Player target = Bukkit.getPlayer(args[1]);
					if (main.ignoredPlayers.contains(target.getName())) {
						sender.sendMessage(
							MessageSender.string("General.Prefix") + " " + MessageSender.string("Sounds.Disabled.Player").replace("<player>", target.getName()));
					} else {
						sender.sendMessage(
							MessageSender.string("General.Prefix") + " " + MessageSender.string("Sounds.Enabled.Player").replace("<player>", target.getName()));
						if (main.getConfig().contains("World-BlackList")) {
							if (main.getConfig().getStringList("World-BlackList").contains(target.getWorld().getName())) {
								sender.sendMessage(MessageSender.string("Toggle.Error.StillCanNotHear"));
							}
						}
					}
				} else {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("General.PlayerNotFound").replace("<player>", args[1]));
					return true;
				}
			}
			return true;
		}
		// Command toggle:
		if (args[0].equalsIgnoreCase("toggle")) {
			if (!sender.hasPermission("playmoresounds.toggle")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			Player player = null;
			if (args.length > 1) {
				if (sender instanceof Player) {
					if (containsP(Bukkit.getOnlinePlayers(), args[1])) {
						if (Bukkit.getPlayer(args[1]) != ((Player) sender)) {
							if (!sender.hasPermission("playmoresounds.toggle.others")) {
								sender.sendMessage(MessageSender.string("General.Prefix") + " "
												   + MessageSender.string("General.NoPermission"));
								return true;
							}
						}
					}
				}
				if (containsP(Bukkit.getOnlinePlayers(), args[1])) {
					player = Bukkit.getPlayer(args[1]);
				} else {
					if (sender.hasPermission("playmoresounds.toggle.others")) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("General.PlayerNotFound").replace("<player>", args[1]));
						return true;
					} else {
						player = (Player) sender;
					}
				}
			}
			if ((playerIsEqualsTo(player, sender)) | (args.length == 1)) {
				if (sender instanceof Player) {
					player = (Player) sender;
				} else {
					sender.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NotAPlayer"));
					return true;
				}
			}
			if (main.ignoredPlayers.contains(player.getName())) {
				ToggleSoundsEvent event = new ToggleSoundsEvent(player, null, true);
				if (sender instanceof Player) {
					event = new ToggleSoundsEvent(player, (Player) sender, true);
				}
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return true;
				}
				main.ignoredPlayers.remove(player.getName());
				if (args.length > 1) {
					if (sender instanceof Player) {
						if (player == ((Player) sender)) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Toggle.Enabled.Default"));
							if (main.getConfig().contains("World-BlackList")) {
								if (main.getConfig().getStringList("World-BlackList")
									.contains(((Player) sender).getWorld().getName())) {
									sender.sendMessage(MessageSender.string("General.Prefix") + " "
													   + MessageSender.string("Toggle.Error.BlackListedWorld"));
								}
							}
							return true;
						}
					}
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Toggle.Enabled.Player").replace("<player>", player.getName()));
					if (main.getConfig().contains("World-BlackList")) {
						if (main.getConfig().getStringList("World-BlackList").contains(player.getWorld().getName())) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Toggle.Error.StillCanNotHear"));
						}
					}
					return true;
				}
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("Toggle.Enabled.Default"));
				if (main.getConfig().contains("World-BlackList")) {
					if (main.getConfig().getStringList("World-BlackList")
						.contains(((Player) sender).getWorld().getName())) {
						sender.sendMessage(MessageSender.string("General.Prefix") + " "
										   + MessageSender.string("Toggle.Error.BlackListedWorld"));
					}
				}
				return true;
			} else {
				ToggleSoundsEvent event = new ToggleSoundsEvent(player, null, false);
				if (sender instanceof Player) {
					event = new ToggleSoundsEvent(player, (Player) sender, false);
				}
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return true;
				}
				main.ignoredPlayers.add(player.getName());
				if (args.length > 1) {
					if (sender instanceof Player) {
						if (player == ((Player) sender)) {
							sender.sendMessage(MessageSender.string("General.Prefix") + " "
											   + MessageSender.string("Toggle.Disabled.Default"));
							return true;
						}
					}
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Toggle.Disabled.Player").replace("<player>", player.getName()));
					return true;
				}
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("Toggle.Disabled.Default"));
				return true;
			}
		}
		// Command list:
		if (args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("playmoresounds.list")) {
				int page = 1;
				if (args.length > 1) {
					if ((args[1].equalsIgnoreCase("-instruments")) | (args[1].equalsIgnoreCase("-instrument"))) {
						if (args.length > 2) {
							try {
								if (Integer.parseInt(args[2]) > 1) {
									page = Integer.parseInt(args[2]);
								}
							} catch (Exception e) {
								sender.sendMessage(MessageSender.string("Prefix") + " "
												   + MessageSender.string("General.NotANumber"));
								return true;
							}
						}
						int arg = page + 1;
						list(page, main.instrumentsNames, sender, "List.Type.Instruments", label, args[1] + " " + arg);
						return true;
					}
					if ((args[1].equalsIgnoreCase("-sounds")) | (args[1].equalsIgnoreCase("-sound"))) {
						if (args.length > 2) {
							try {
								if (Integer.parseInt(args[2]) > 1) {
									page = Integer.parseInt(args[2]);
								}
							} catch (Exception e) {
								sender.sendMessage(MessageSender.string("Prefix") + " "
												   + MessageSender.string("General.NotANumber"));
								return true;
							}
						}
						int arg = page + 1;
						list(page, main.soundsNames, sender, "List.Type.Sounds", label, args[1] + " " + arg);
						return true;
					}
					if (args.length > 2) {
						if ((args[2].equalsIgnoreCase("-instruments")) | (args[2].equalsIgnoreCase("-instrument"))) {
							try {
								if (Integer.parseInt(args[1]) > 1) {
									page = Integer.parseInt(args[1]);
								}
							} catch (Exception e) {
								sender.sendMessage(MessageSender.string("Prefix") + " "
												   + MessageSender.string("General.NotANumber"));
								return true;
							}
							int arg = page + 1;
							list(page, main.instrumentsNames, sender, "List.Type.Instruments", label,
								 arg + " " + args[2]);
							return true;
						}
						if ((args[2].equalsIgnoreCase("-sounds")) | (args[2].equalsIgnoreCase("-sound"))) {
							try {
								if (Integer.parseInt(args[1]) > 1) {
									page = Integer.parseInt(args[1]);
								}
							} catch (Exception e) {
								sender.sendMessage(MessageSender.string("Prefix") + " "
												   + MessageSender.string("General.NotANumber"));
								return true;
							}
							int arg = page + 1;
							list(page, main.soundsNames, sender, "List.Type.Sounds", label, arg + " " + args[2]);
							return true;
						}
					}
					try {
						if (Integer.parseInt(args[1]) > 1) {
							page = Integer.parseInt(args[1]);
						}
					} catch (Exception e) {
						sender.sendMessage(
							MessageSender.string("Prefix") + " " + MessageSender.string("General.NotANumber"));
						return true;
					}
					int arg = page + 1;
					list(page, main.soundsNames, sender, "List.Type.Sounds", label, arg + "");
					return true;
				}
				int arg = page + 1;

				list(page, main.soundsNames, sender, "List.Type.Sounds", label, arg + "");
				return true;
			} else {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
		}
		// Command update:
		if (args[0].equalsIgnoreCase("update")) {
			if (!sender.hasPermission("playmoresounds.update")) {
				sender.sendMessage(
					MessageSender.string("General.Prefix") + " " + MessageSender.string("General.NoPermission"));
				return true;
			}
			sender.sendMessage(MessageSender.string("General.Prefix") + " " + MessageSender.string("Update.Check"));
			Updater updater = new Updater("262494", false);
			if (updater.getCheckResult().equals(UpdateCheckResult.AVAILABLE)) {
				if (main.getConfig().getBoolean("CommandAutoUpdate")) {
					sender.sendMessage(MessageSender.string("General.Prefix") + " "
									   + MessageSender.string("Update.Found.Auto").replace("<update>", updater.getUpdateName()));
					updater.download();
					if (updater.getDownloadResult().equals(UpdateDownloadResult.SUCCESS)) {
						sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("Update.Downloaded")));
					} else {
						sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', MessageSender.string("General.Prefix") + " "
																   + MessageSender.string("Update.Error.Download")));
					}
				} else {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "minecraft:tellraw " + p.getName()
											   + " [\"\",{\"text\":\"" + MessageSender.string("General.Prefix").replace("&", "�")
											   + " " + "\"},{\"text\":\""
											   + MessageSender.string("Update.Found.Info").replace("<update>", updater.getUpdateName())
											   .replace("&", "�")
											   + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + updater.getUpdateLink()
											   + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
											   + MessageSender.string("Update.JsonChat").replace("&", "�") + "\"}]}}}]");
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
																				  MessageSender.string("General.Prefix") + " " + MessageSender.string("Update.Found.Info")
																				  .replace("<update>", updater.getUpdateName())));
					}
				}
				return true;
			} else if (updater.getCheckResult().equals(UpdateCheckResult.FAIL)) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
																		  MessageSender.string("General.Prefix") + " " + MessageSender.string("Update.Error.Check")));
				return true;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
																	  MessageSender.string("General.Prefix") + " " + MessageSender.string("Update.NotFound")));
			return true;
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageSender.string("General.Prefix") + " "
																  + MessageSender.string("General.UnknownCommand").replace("<label>", label)));

		return true;
	}

	boolean playerIsEqualsTo(Player player, CommandSender sender) {
		if (player != null) {
			if (sender instanceof Player) {
				if (player == (Player) sender) {
					return true;
				}
			}
		}
		return false;
	}

	boolean containsP(Collection<? extends Player> players, String string) {
		for (Player s : players) {
			if (s.getName().toLowerCase().contains(string.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
