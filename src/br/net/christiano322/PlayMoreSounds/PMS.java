package br.net.christiano322.PlayMoreSounds;

import br.net.christiano322.PlayMoreSounds.addons.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.listeners.sounds.*;
import br.net.christiano322.PlayMoreSounds.listeners.sounds.MC1_12.*;
import br.net.christiano322.PlayMoreSounds.listeners.sounds.MC1_7_9.*;
import br.net.christiano322.PlayMoreSounds.regions.listeners.*;
import br.net.christiano322.PlayMoreSounds.sound.*;
import br.net.christiano322.PlayMoreSounds.updater.*;
import br.net.christiano322.PlayMoreSounds.updater.Updater.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import com.google.common.io.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;
import sun.reflect.*;

import br.net.christiano322.PlayMoreSounds.utils.Utility;
import br.net.christiano322.PlayMoreSounds.api.*;

public class PMS extends JavaPlugin implements Listener {

	boolean firstLoad;
	ClassLoader classLoader;
	List<File> addons;
	List<String> registeredClasses;
	PluginManager pm;
	boolean schedulerStarted;
	int hardMethodsInt;
	public File chatwordsFile;
	public File commandsFile;
	public File configFile;
	public File customsoundsFile;
	public File gamemodesFile;
	public File regionsFile;
	public File soundsFile;
	public File thisFile;
	public File worldsoundsFile;
	public FileConfiguration chatwords;
	public FileConfiguration commands;
	public FileConfiguration customsounds;
	public FileConfiguration gamemodes;
	public FileConfiguration language;
	public FileConfiguration languageEn;
	public FileConfiguration languageJar;
	public FileConfiguration regions;
	public FileConfiguration sounds;
	public FileConfiguration worldsounds;
	public HashMap<SoundAddon, JarFile> soundAddon;
	public HashMap<String, BukkitRunnable> toggleCooldownTask;
	public HashMap<String, Integer> toggleCooldownTime;
	public List<EventName> blockedSounds;
	public List<String> compatibleLang;
	public List<String> compatibleVersions;
	public List<String> ignoredPlayers;
	public List<String> instrumentsNames;
	public List<String> soundsNames;
	public Logger logger;
	public String bukkitVersion;
	public String pVersion;
	public String previousVersion;
	public String updateLink;
	public boolean commandsEnabled;
	public boolean isIntervalErrorsEnabled;
	public boolean isPitchErrorsEnabled;
	public boolean isPositionsEnabled;
	public boolean isSoundErrorsEnabled;
	public boolean isVolumeErrorsEnabled;
	public boolean tabCompleter;
	public boolean updateFound;
	public boolean versionRestrictor;
	public static PMS plugin;

	void registerListener(Listener listener) {
		pm.registerEvents(listener, this);
	}

	boolean registerAddonsUrls() throws Exception{
		try {
			File folder = new File(getDataFolder(), "addons");
			if (folder.listFiles().length > 0) {
				for (File file : folder.listFiles()) {
					if (file.getName().endsWith(".jar")) {
						addons.add(file);
					}
				}
			} else {
				return false;
			}
			List<URL> urls = new ArrayList<URL>();
			for (File file : addons) {
				urls.add(file.toURI().toURL());
			}
			classLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]), plugin.getClass().getClassLoader());
		} catch (Exception e) {
			new Exception("Something went wrong...");
			return false;
		}
		return true;
	}

	void loadAddons() throws Exception {
		if (registerAddonsUrls()) {
			JarFile jarFile = null;
			try {
				for (File pathToJar : addons) {
					jarFile = new JarFile(pathToJar);
					Enumeration<JarEntry> e = jarFile.entries();
					while (e.hasMoreElements()) {
						JarEntry je = e.nextElement();
						if (je.isDirectory() || !je.getName().endsWith(".class")) {
							continue;
						}
						String className = je.getName().substring(0, je.getName().length() - 6);
						className = className.replace('/', '.');
						Class<?> c = Class.forName(className, true, classLoader);
						if (!SoundAddon.class.isAssignableFrom(c) || c.isInterface()
							|| Modifier.isAbstract(c.getModifiers())) {
							continue;
						}
						ReflectionFactory rF = ReflectionFactory.getReflectionFactory();
						Constructor<?> declared = SoundAddon.class.getDeclaredConstructor();
						Constructor<?> rFConstructor = rF.newConstructorForSerialization(c, declared);
						SoundAddon addonClass = (SoundAddon) c.cast(rFConstructor.newInstance());
						soundAddon.put(addonClass, jarFile);
					}
				}
			} catch (Exception e) {
				new Exception("Something went wrong...");
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void missingConfig(String message) {
		logger.log("&e============================================");
		logger.error("      FATAL ERROR - PMS is missing the      ");
		logger.error(message);
		logger.log("&e============================================");
		logger.warn("Plugin disabled.");
		pm.disablePlugin(this);
		return;
	}

	@Override
	public void onEnable() {
		bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replace("_", ".");
		logger = new Logger();
		pm = Bukkit.getPluginManager();
		firstLoad = false;
		versionRestrictor = false;
		PMS.plugin = this;
		hardMethodsInt = 0;
		pVersion = "2.4.2.1";
		previousVersion = "2.4.2";
		toggleCooldownTime = new HashMap<String, Integer>();
		toggleCooldownTask = new HashMap<String, BukkitRunnable>();
		soundsFile = new File(getDataFolder(), "sounds.yml");
		commandsFile = new File(getDataFolder(), "sounds/commands.yml");
		gamemodesFile = new File(getDataFolder(), "sounds/gamemodes.yml");
		regionsFile = new File(getDataFolder(), "sounds/regions.yml");
		configFile = new File(getDataFolder(), "config.yml");
		customsoundsFile = new File(getDataFolder(), "sounds/addons.yml");
		chatwordsFile = new File(getDataFolder(), "sounds/chatwords.yml");
		soundsNames = new ArrayList<>();
		instrumentsNames = new ArrayList<>();
		ignoredPlayers = new ArrayList<>();
		blockedSounds = new ArrayList<>();
		registeredClasses = new ArrayList<>();
		addons = new ArrayList<>();
		schedulerStarted = false;
		thisFile = getFile();
		updateFound = false;
		isSoundErrorsEnabled = true;
		isIntervalErrorsEnabled = true;
		isVolumeErrorsEnabled = true;
		isPitchErrorsEnabled = true;
		isPositionsEnabled = true;
		tabCompleter = true;
		commandsEnabled = true;
		soundAddon = new HashMap<>();
		compatibleVersions = new ArrayList<>();
		compatibleVersions.add("1.14");
		compatibleVersions.add("1.13");
		compatibleVersions.add("1.12");
		compatibleVersions.add("1.11");
		compatibleVersions.add("1.10");
		compatibleVersions.add("1.9");
		compatibleVersions.add("1.8");
		compatibleVersions.add("1.7.10");
		compatibleVersions.add("1.7.9");
		compatibleLang = new ArrayList<>();
		compatibleLang.add("br");
		compatibleLang.add("en");
		compatibleLang.add("es");

		if (getDataFolder().exists()) {
			if (getDataFolder().listFiles().length == 0) {
				firstLoad = true;
			}
		} else {
			firstLoad = true;
		}

		try {
			loadConfiguration();
			languageEn = YamlConfiguration.loadConfiguration(new InputStreamReader(PMS.plugin.getResource("lang/language_en.properties")));
			languageJar = YamlConfiguration.loadConfiguration(new InputStreamReader(PMS.plugin.getResource("lang/language_" + getConfig().getString("Localization").toLowerCase() + ".properties")));
		} catch (Exception e) {
			ErrorReport.errorReport(e, "LoadConfiguration Exception:");
		}

		String soundList = "";
		for (SoundEnum s : SoundEnum.values()) {
			if (s.bukkitSound() != null) {
				soundList = soundList + s.toString() + ",\n";
				soundsNames.add(s.toString());
			}
		}

		try {
			extractAddons();
			loadAddons();
			for (SoundAddon sa : soundAddon.keySet()) {
				try {
					sa.onStart();
					String code = CodeReader.formatString(sa.getAddonName().toLowerCase());
					File file = new File(getDataFolder(), "data/addons.dat");
					if (!file.exists()) {
						try {
							new File(getDataFolder(), "data").mkdir();
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (!Utility.containsSet(YamlConfiguration.loadConfiguration(file).getKeys(false), code)) {
						String number = String.valueOf("\n" + Utility.getRandomInteger(99, 10)) + String.valueOf(Utility.getRandomInteger(99, 10)) + code + ": " + String.valueOf(Utility.getRandomInteger(99, 10));
						try {
							ErrorReport.stringToFile(number, file , false);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					logger.log("&2+&e The addon " + sa.getAddonName() + "&e has been enabled!");
				} catch (Exception e) {
					logger.log("&4X&c An error has occurred while enabling " + sa.getAddonName());
					ErrorReport.errorReport(e, "AddonLoad Exception: (" + sa.getAddonName() + ")");
				}
			}
		} catch (Exception e) {
			logger.error("Something went wrong while loading addons.");
			ErrorReport.errorReport(e, "AddonLoadExtract Exception:");
		}
		if (versionRestrictor) {
			if (!Utility.contains(compatibleVersions, bukkitVersion)) {
				checkUpdates(true);
				logger.log("&e============================================");
				logger.log("&b         Sorry, PMS does not support");
				logger.log("&b           your current MC version");
				logger.log("&e============================================");
				logger.warn("Plugin disabled.");
				pm.disablePlugin(this);
				return;
			}
		}
		if (new File(getDataFolder(), "list-of-sounds.yml").exists()) { 
			new File(getDataFolder(), "list-of-sounds.yml").delete();
		}
		try {
			ErrorReport.stringToFile(soundList, new File(getDataFolder(), "list-of-sounds.yml"), false);
		} catch (IOException e) {}
		for (Instrument i : Instrument.values()) {
			instrumentsNames.add(i.toString());
		}

		if (soundsFile.exists()) {
			if (configFile.exists()) {
				if (bukkitVersion.contains("1.11.2")) {
					logger.log("&e============================================");
					logger.log("&b   PlayerAchievementAwardedEvent will &nNOT");
					logger.log("&b       affect the server performance.");
					logger.log("&b For more information check the plugin page.");
					logger.log("&e============================================");
				} else {
					logger.log("&e============================================");
					logger.log("&bPlayMoreSounds has been enabled successfully");
					logger.log("&bVersion " + Bukkit.getBukkitVersion() + " detected");
					logger.log("&e============================================");
				}
			} else {
				missingConfig("              config.yml file.");
				return;
			}
		} else {
			if (configFile.exists()) {
				missingConfig("              sounds.yml file.");
				return;
			} else {
				missingConfig("      files sounds.yml and config.yml");
				return;
			}
		}

		pm.registerEvents(this, this);
		pm.registerEvents(new AreaSelector(this), this);
		pm.registerEvents(new PlayerMove(), this);
		pm.registerEvents(new PlayerTeleport(this), this);
		pm.registerEvents(new PlayerRegion(this), this);
		loadEvents();
		new WorldTime(this).time();

		if (tabCompleter) {
			getCommand("playmoresounds").setTabCompleter(new br.net.christiano322.PlayMoreSounds.commands.TabCompleter(this));
		}
		if (commandsEnabled) {
			getCommand("playmoresounds").setExecutor(new br.net.christiano322.PlayMoreSounds.commands.Commands(this));
		}

		if (Utility.isDate("31-10-")) {
			List<String> messages = new ArrayList<>();
			messages.add("Spooky!");
			messages.add("Happy Halloween!");
			messages.add("Trick or Treat?");
			logger.log("&aComment: " + messages.get(new Random().nextInt(messages.size())));
		}
		if (Utility.isDate("25-12-")) {
			List<String> messages = new ArrayList<>();
			messages.add("Happy Christmas!");
			messages.add("Merry Christmas!");
			logger.log("&aComment: " + messages.get(new Random().nextInt(messages.size())));
		}

		checkUpdates(true);
		if (getConfig().getConfigurationSection("UpdateScheduler").getBoolean("Enabled")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (schedulerStarted) {
						cancel();
						return;
					}
					checkUpdates(getConfig().getBoolean("UpdateScheduler.LogToConsole"));
				}
			}.runTaskTimer(this, Utility.formatToTicks(getConfig().getString("UpdateScheduler.Interval")),
						   Utility.formatToTicks(getConfig().getString("UpdateScheduler.Interval")));
		}
	}

	@Override
	public void onDisable() {
		logger.log("&eDisabling addons...");
		if (soundAddon != null) {
			if (soundAddon.size() != 0) {
				for (SoundAddon sA : soundAddon.keySet()) {
					try {
						sA.onEnd();
						logger.log("&4-&e The addon " + sA.getAddonName() + " has been disabled.");
					} catch (Exception e) {
						logger.log("&4X&c An error has occurred while disabling addon " + sA.getAddonName());
						ErrorReport.errorReport(e, "AddonUnload Exception: (" + sA.getAddonName() + ")");
					}
				}
				return;
			}
		}
		logger.log("&eNo addons found.");
	}

	void folder() {
		File folder = new File(getDataFolder(), "sounds");
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public void checkUpdates(boolean b) {
		if (getConfig().getBoolean("CheckForUpdates")) {
			if (!updateFound) {
				if (b) {
					logger.log("&eChecking for updates...");
				}
				final Updater updater = new Updater("262494", false);
				if (updater.getCheckResult().equals(UpdateCheckResult.AVAILABLE)) {
					updateFound = true;
					updateLink = updater.getUpdateLink();
					if (getConfig().getBoolean("CommandAutoUpdate")) {
						logger.info(updater.getUpdateName() + " is now available. Use /pms update to download it");
					} else {
						logger.info(
							updater.getUpdateName() + " is now available. Use /pms update for more information");
					}
					if (getConfig().getConfigurationSection("UpdateScheduler").getBoolean("Enabled")) {
						if (!schedulerStarted) {
							String interval = getConfig().getConfigurationSection("UpdateScheduler")
								.getString("Interval");
							new BukkitRunnable() {
								@Override
								public void run() {
									if (getConfig().getBoolean("CommandAutoUpdate")) {
										logger.info(updater.getUpdateName()
													+ " is now available. Use /pms update to download it");
									} else {
										logger.info(updater.getUpdateName()
													+ " is now available. Use /pms update for more information");
									}
								}
							}.runTaskTimer(this, Utility.formatToTicks(interval), Utility.formatToTicks(interval));
							schedulerStarted = true;
						}
					}
					return;
				} else if (updater.getCheckResult().equals(UpdateCheckResult.FAIL)) {
					logger.warn("Failed to check for updates on bukkit");
					return;
				}
				if (b) {
					logger.log("&eNo updates found");
				}
			}
		}
	}

	public void extractAddons() {
		File folder = new File(getDataFolder(), "addons");
		if (!folder.exists()) {
			folder.mkdir();
		}
		if (firstLoad) {
			check("Essentials", "EssentialsAddon.jar");
			check("Jobs", "JobsRebornAddon.jar");
			check("VanishNoPacket", "VanishNoPacketAddon.jar");
			check("RedProtect", "RedProtectAddon.jar");
			//check("WorldGuard", "WorldGuardAddon");
			//check("Legendchat", "LegendchatAddon");
			//check("SimpleClans", "SimpleClansAddon");
			//check("NoteBlockAPI", "NoteBlockAPIAddon");
			//check("UltimateChat", "UChatAddon");
			//check("Vault", "MoneyEventAddon");
			//check("ChatReaction", "ReactionAddon");
		}
	}

	void check(String plugin, String addon) {
		if (pm.isPluginEnabled(plugin)) {
			File f =new File(getDataFolder(), "addons/" + addon);
			if (!f.exists()) {
				saveResource("libraries/addons/" + addon, true);
				try {
					Files.move(new File(getDataFolder(), "libraries/addons/" + addon), f);
				} catch (IOException e) {}
				new File(getDataFolder(), "libraries/addons").delete();
				new File(getDataFolder(), "libraries").delete();
			}
		}
	}

	public void loadEvents() {
		eventLoader(new ChangeHotbar(this), "ChangeHotbar");
		eventLoader(new ChangeLevel(this), "ChangeLevel");
		eventLoader(new CraftingExtract(this), "CraftingExtract");
		eventLoader(new FurnaceExtract(this), "FurnaceExtract");
		perConfigLoader(new GamemodeChange(this), "GamemodeChange", "GamemodeChange", "PerGamemodeSounds.Enabled", gamemodes);
		eventLoader(new InventoryClick(this), "InventoryClick");
		multipleLoader(new JoinServer(this), "JoinEvents", "JoinServer", "FirstJoin");
		eventLoader(new LeaveServer(this), "LeaveServer");
		if (bukkitVersion.contains("1.14") | bukkitVersion.contains("1.13") | bukkitVersion.contains("1.12")) {
			eventLoader(new PlayerAdvancementDone(this), "PlayerAdvancementDone");
		} else {
			eventLoader(new PlayerAchievement(this), "PlayerAchievement");
		}
		multipleLoader(new PlayerBed(this), "BedEvents", "BedEnter", "BedLeave");
		eventLoader(new PlayerKicked(this), "PlayerKicked");
		perConfigLoader(new PlayerChat(this), "PlayerChat", "PlayerChat", "PerChatWordSounds.Enabled", chatwords);
		perConfigLoader(new PlayerCommand(this), "PlayerCommand", "PlayerCommand", "PerCommandSounds.Enabled", commands);
		eventLoader(new PlayerDeath(this), "PlayerDeath");
		eventLoader(new PlayerDrop(this), "PlayerDrop");
		multipleLoader(new PlayerFlight(this), "FlightEvents", "PlayerFlight", "PlayerFlightStop");
		multipleLoader(new PlayerHit(this), "HitEvents", "ArrowHit", "HandHit", "SwordHit");
		//multipleLoader(new PlayerKick(this), "KickEvents", "PlayerKicked", "PlayerBanned");
	}

	void eventLoader(Listener listener, String name) {
		if (registeredClasses.contains(name)) {
			if (!sounds.getBoolean(name + ".Enabled")) {
				HandlerList.unregisterAll(listener);
				registeredClasses.remove(name);
			}
		} else {
			if (sounds.getBoolean(name + ".Enabled")) {
				registerListener(listener);
				registeredClasses.add(name);
			}
		}
	}

	void perConfigLoader(Listener listener, String name, String event, String bool, FileConfiguration config) {
		if (registeredClasses.contains(name)) {
			if (!sounds.getBoolean(event + ".Enabled") & (getConfig().getBoolean(bool) ? config.getKeys(false).isEmpty() : 0 == 0)) {
				HandlerList.unregisterAll(listener);
				registeredClasses.remove(name);
			}
		} else {
			if (sounds.getBoolean(event + ".Enabled") | (getConfig().getBoolean(bool) ? !config.getKeys(false).isEmpty() : 0 == 0)) {
				registerListener(listener);
				registeredClasses.add(name);
			}
		}
	}

	void multipleLoader(Listener listener, String name, String event, String event2) {
		if (registeredClasses.contains(name)) {
			if (!sounds.getBoolean(event + ".Enabled") & !sounds.getBoolean(event2 + ".Enabled")) {
				HandlerList.unregisterAll(listener);
				registeredClasses.remove(name);
			}
		} else {
			if (sounds.getBoolean(event + ".Enabled") | sounds.getBoolean(event2 + ".Enabled")) {
				registerListener(listener);
				registeredClasses.add(name);
			}
		}
	}

	void multipleLoader(Listener listener, String name, String event, String event2, String event3) {
		if (registeredClasses.contains(name)) {
			if (!sounds.getBoolean(event + ".Enabled") & !sounds.getBoolean(event2 + ".Enabled") & !sounds.getBoolean(event3 + ".Enabled")) {
				HandlerList.unregisterAll(listener);
				registeredClasses.remove(name);
			}
		} else {
			if (sounds.getBoolean(event + ".Enabled") | sounds.getBoolean(event2 + ".Enabled") | sounds.getBoolean(event3 + ".Enabled")) {
				registerListener(listener);
				registeredClasses.add(name);
			}
		}
	}

	public void loadConfiguration() throws Exception {
		try {
			File config = new File(getDataFolder(), "config.yml");
			if (config.exists()) {
				FileConfiguration f = YamlConfiguration.loadConfiguration(config);
				if (f.contains("ConfigVersion")) {
					if (!isVersion(getConfig(), "ConfigVersion", Arrays.asList("2.4.2.1"))) {
						if (f.getBoolean("ConfirmOutdatedFileRestoration")) {
							logger.warn("Configuration outdated! To restore it, type '/pms restore config'");
						} else {
							logger.warn("Outdated configuration! Restoring config...");
							Restore(PMSFile.CONFIG);
							if (getConfig().getBoolean("Debug,Output")) {
								logger.log("&eConfiguration file restored successfully");
							}
						}
					}
				} else {
					if (f.getBoolean("ConfirmOutdatedFileRestoration")) {
						logger.warn("Configuration outdated! To restore it, type '/pms restore config'");
					} else {
						logger.warn("Outdated configuration! Restoring config...");
						Restore(PMSFile.CONFIG);
						if (getConfig().getBoolean("DebugOutput")) {
							logger.log("&eConfiguration file restored successfully");
						}
					}
				}
			} else {
				Restore(PMSFile.CONFIG);
				if (getConfig().getBoolean("DebugOutput")) {
					logger.info("Config.yml created successfully");
				}
			}
		} catch (Exception e) {
			logger.log("An error has occurred while creating config.yml");
			ErrorReport.errorReport(e, "ConfigurationExtract Exception: (config.yml)");
		}
		try {
			File sounds = new File(getDataFolder(), "sounds.yml");
			if (sounds.exists()) {
				FileConfiguration f = YamlConfiguration.loadConfiguration(sounds);
				if (f.contains("SoundsConfigVersion")) {
					if (!isVersion(f, "SoundsConfigVersion", Arrays.asList("2.4.2.1"))) {
						if (getConfig().getBoolean("ConfirmOutdatedFileRestoration")) {
							logger.warn("Sounds file outdated! To restore it, type '/pms restore sounds'");
						} else {
							logger.warn("Outdated sounds file! Restoring sounds...");
							Restore(PMSFile.SOUNDS);
							if (getConfig().getBoolean("DebugOutput")) {
								logger.log("&eSounds configuration restored successfully");
							}
						}
					}
				} else {
					if (getConfig().getBoolean("ConfirmOutdatedFileRestoration")) {
						logger.warn("Sounds file outdated! To restore it, type '/pms restore sounds'");
					} else {
						logger.warn("Outdated sounds file! Restoring sounds...");
						Restore(PMSFile.SOUNDS);
						if (getConfig().getBoolean("DebugOutput")) {
							logger.log("&eSounds configuration restored successfully");
						}
					}
				}
			} else {
				Restore(PMSFile.SOUNDS);
				if (getConfig().getBoolean("DebugOutput")) {
					logger.info("Sounds.yml created successfully");
				}
			}
			this.sounds = YamlConfiguration.loadConfiguration(sounds);
		} catch (Exception e) {
			logger.log("An error has occurred while creating sounds.yml");
			ErrorReport.errorReport(e, "ConfigurationExtract Exception: (sounds.yml)");
		}
		try {
			if (!customsoundsFile.exists()) {
				folder();
				File custom = new File(getDataFolder(), "sounds/addons.yml");
				custom.createNewFile();
				customsoundsFile = custom;
				if (getConfig().getBoolean("DebugOutput")) {
					logger.info("Addons.yml created successfully");
				}
			}
			customsounds = YamlConfiguration.loadConfiguration(customsoundsFile);
		} catch (Exception e) {
			logger.log("An error has occurred while creating addons.yml");
			ErrorReport.errorReport(e, "ConfigurationExtract Exception: (addons.yml)");
		}
		if (configFile.exists()) {
			if (!compatibleLang.contains(getConfig().getString("Localization").toLowerCase())) {
				logger.warn("Wrong Localization! \"" + getConfig().getString("Localization")
							+ "\" is not a supported language! LANGUAGE_EN set as default language.");
				getConfig().set("Localization", "en");
			}
		}
		if (getConfig().getBoolean("ExtractLanguageFiles")) {
			try {
				File folder = new File(getDataFolder(), "lang");
				if (!folder.exists()) {
					folder.mkdir();
				}
				File lang = new File(getDataFolder(), "lang/language_"
									 + getConfig().getString("Localization").toLowerCase() + ".properties");
				if (lang.exists()) {
					FileConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
					if (langConfig.contains("Version")) {
						if (!isVersion(langConfig, "Version", Arrays.asList("2.4.2.1", "2.4.2", "2.4.1"))) {
							if (getConfig().getBoolean("ConfirmOutdatedFileRestoration")) {
								logger.warn(getConfig().getString("Localization").toUpperCase().replace(" ", "")
											+ " language outdated! To restore it, type '/pms restore lang"
											+ getConfig().getString("Localization").toUpperCase().replace(" ", "") + "'");
							} else {
								logger.warn("Outdated "
											+ getConfig().getString("Localization").toUpperCase().replace(" ", "")
											+ " language! Restoring language...");
								saveResource("lang/language_" + getConfig().getString("Localization").toLowerCase()
											 + ".properties", true);
								if (getConfig().getBoolean("DebugOutput")) {
									logger.log(
										"&e" + getConfig().getString("Localization").toUpperCase().replace(" ", "")
										+ " language restored successfully");
								}
							}
						}
					} else {
						if (getConfig().getBoolean("ConfirmOutdatedFileRestoration")) {
							logger.warn(getConfig().getString("Localization").toUpperCase().replace(" ", "")
										+ " language outdated! To restore it, type '/pms restore lang"
										+ getConfig().getString("Localization").toUpperCase().replace(" ", "") + "'");
						} else {
							logger.warn(
								"Outdated " + getConfig().getString("Localization").toUpperCase().replace(" ", "")
								+ " language! Restoring language...");
							saveResource("lang/language_" + getConfig().getString("Localization").toLowerCase()
										 + ".properties", true);
							if (getConfig().getBoolean("DebugOutput")) {
								logger.log("&e" + getConfig().getString("Localization").toUpperCase().replace(" ", "")
										   + " language restored successfully");
							}
						}
					}
				} else {
					saveResource("lang/language_" + getConfig().getString("Localization").toLowerCase() + ".properties",
								 true);
				}
				language = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang/language_"
																		+ getConfig().getString("Localization").toLowerCase() + ".properties"));
			} catch (Exception e) {
				logger.log("An error has occurred while creating language_"
						   + getConfig().getString("Localization").toLowerCase() + ".properties");
				ErrorReport.errorReport(e, "ConfigurationExtract Exception: (language_" + getConfig().getString("Localization").toLowerCase() + ".properties)");
			}
		} else {
			language = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("lang/language_" + getConfig().getString("Localization").toLowerCase() + ".properties")));
		}
		if (getConfig().getConfigurationSection("PerCommandSounds").getBoolean("Enabled")) {
			try {
				File commands = new File(getDataFolder(), "sounds/commands.yml");
				if (!commands.exists()) {
					Restore(PMSFile.COMMANDS);
					if (getConfig().getBoolean("DebugOutput")) {
						logger.info("Commands.yml created successfully");
					}
				}
				this.commands = YamlConfiguration.loadConfiguration(commands);
			} catch (Exception e) {
				logger.log("An error has occurred while creating commands.yml");
				ErrorReport.errorReport(e, "ConfigurationExtract Exception: (commands.yml)");
			}
		}
		if (getConfig().getConfigurationSection("PerGamemodeSounds").getBoolean("Enabled")) {
			try {
				File gamemodes = new File(getDataFolder(), "sounds/gamemodes.yml");
				if (!gamemodes.exists()) {
					Restore(PMSFile.GAMEMODES);
					if (getConfig().getBoolean("DebugOutput")) {
						logger.info("Gamemodes.yml created successfully");
					}
				}
				this.gamemodes = YamlConfiguration.loadConfiguration(gamemodes);
			} catch (Exception e) {
				logger.log("An error has occurred while creating gamemodes.yml");
				ErrorReport.errorReport(e, "ConfigurationExtract Exception: (gamemodes.yml)");
			}
		}
		if (getConfig().getBoolean("PerChatWordSounds.Enabled")) {
			try {
				File chatwords = new File(getDataFolder(), "sounds/chatwords.yml");
				if (!chatwords.exists()) {
					Restore(PMSFile.CHATWORDS);
					if (getConfig().getBoolean("DebugOutput")) {
						logger.info("ChatWords.yml created successfully");
					}
				}
				this.chatwords = YamlConfiguration.loadConfiguration(chatwords);
			} catch (Exception e) {
				logger.log("An error has occurred while creating chatwords.yml");
				ErrorReport.errorReport(e, "ConfigurationExtract Exception: (chatwords.yml)");
			}
		}
		try {
			File regions = new File(getDataFolder(), "sounds/regions.yml");
			if (!regions.exists()) {
				Restore(PMSFile.REGIONS);
				if (getConfig().getBoolean("DebugOutput")) {
					logger.info("Regions.yml created successfully");
				}
			}
			this.regions = YamlConfiguration.loadConfiguration(regions);
		} catch (Exception e) {
			logger.log("An error has occurred while creating regions.yml");
			ErrorReport.errorReport(e, "ConfigurationExtract Exception: (regions.yml)");
		}
		try {
			File worldsounds = new File(getDataFolder(), "sounds/worldsounds.yml");
			if (!worldsounds.exists()) {
				Restore(PMSFile.WORLDSOUNDS);
				if (getConfig().getBoolean("DebugOutput")) {
					logger.info("Worldsounds.yml created successfully");
				}
			}
			this.worldsounds = YamlConfiguration.loadConfiguration(worldsounds);
		} catch (Exception e) {
			logger.log("An error has occurred while creating worldsounds.yml");
			ErrorReport.errorReport(e, "ConfigurationExtract Exception: (worldsounds.yml)");
		}
	}

	boolean isVersion(FileConfiguration config, String path, List<String> s) {
		for (String string : s) {
			if (config.getString(path).equals(string)) {
				return true;
			}
		}
		return false;
	}

	public void Restore(PMSFile file) throws IOException {
		if (file.equals(PMSFile.CONFIG)) {
			saveResource("libraries/stuff/config.yml", true);
			Files.move(new File(getDataFolder(), "libraries/stuff/config.yml"), new File(getDataFolder(), "config.yml"));
		}
		if (file.equals(PMSFile.SOUNDS)) {
			ErrorReport.stringToFile(Storage.getSoundsData(false), new File(getDataFolder(), "sounds.yml"), false);
		}
		if (file.equals(PMSFile.COMMANDS)) {
			saveResource("libraries/stuff/commands.yml", true);
			folder();
			Files.move(new File(getDataFolder(), "libraries/stuff/commands.yml"), new File(getDataFolder(), "sounds/commands.yml"));
		}
		if (file.equals(PMSFile.GAMEMODES)) {
			saveResource("libraries/stuff/gamemodes.yml", true);
			folder();
			Files.move(new File(getDataFolder(), "libraries/stuff/gamemodes.yml"), new File(getDataFolder(), "sounds/gamemodes.yml"));	
		}
		if (file.equals(PMSFile.CHATWORDS)) {
			saveResource("libraries/stuff/chatwords.yml", true);
			folder();
			Files.move(new File(getDataFolder(), "libraries/stuff/chatwords.yml"), new File(getDataFolder(), "sounds/chatwords.yml"));
		}
		if (file.equals(PMSFile.REGIONS)) {
			saveResource("libraries/stuff/regions.yml", true);
			folder();
			Files.move(new File(getDataFolder(), "libraries/stuff/regions.yml"), new File(getDataFolder(), "sounds/regions.yml"));
		}
		if (file.equals(PMSFile.ADDONS)) {
			saveResource("libraries/stuff/addons.yml", true);
			folder();
			Files.move(new File(getDataFolder(), "libraries/stuff/addons.yml"), new File(getDataFolder(), "sounds/addons.yml"));
		}
		if (file.equals(PMSFile.WORLDSOUNDS)) {
			File exists = new File(getDataFolder(), "sounds/worldsounds.yml");
			if (exists.exists()) {
				exists.delete();
			}
			folder();
			loadWorlds();
		}
		new File(getDataFolder(), "libraries/stuff").delete();
		new File(getDataFolder(), "libraries").delete();
	}

	void loadWorlds() {
		String data = "";
		for (World w : Bukkit.getWorlds()) {
			if (w.getEnvironment().equals(World.Environment.NORMAL)) {
				if (data.equals("")) {
					data = data + w.getName() + ": #The name of the world." +
						"\n  WorldTimeSounds: #Plays a sound to a specific world time." +
						"\n    0: #The time in ticks that you want to play a sound." +
						"\n      Sounds: #The intervals that you want to play a sound." +
						"\n        '0': #Inteval in ticks." +
						"\n          #Remember: PermissionRequired and Radius is not available to this event." +
						"\n          Options: #To see more information and more options, check sounds.yml." +
						"\n            IgnoreToggle: true" +
						"\n            Location: DEFAULT" +
						"\n            SoundPosition:" +
						"\n              Up: 3" +
						"\n              Back: 3" +
						"\n          Pitch: 1.5" +
						"\n          Sound: 'ENTITY_CHICKEN_HURT'" +
						"\n          Volume: 0.5" +
						"\n        '12': #Play a sound 12 ticks after the world has reached time 0." +
						"\n          Options:" +
						"\n            IgnoreToggle: true" +
						"\n            Location: DEFAULT" +
						"\n          Pitch: 1.0" +
						"\n          Sound: 'ENTITY_CHICKEN_EGG'" +
						"\n          Volume: 10.0" +
						"\n    13000: #Plays a sound when the world time reaches 13000." +
						"\n      Sounds:" +
						"\n        '0':" +
						"\n          Options:" +
						"\n            IgnoreToggle: false" +
						"\n            Location: DEFAULT" +
						"\n          Pitch: 1.0" +
						"\n          Sound: 'AMBIENT_CAVE'" +
						"\n          Volume: 10.0\n";
				} else {
					data = data + "\n" + w.getName() + ":" +
						"\n  WorldTimeSounds:" +
						"\n    0:" +
						"\n      Sounds:" +
						"\n        '0':" +
						"\n          Options:" +
						"\n            IgnoreToggle: true" +
						"\n            Location: DEFAULT" +
						"\n            Radius:" +
						"\n              CrossWorlds: false" +
						"\n            SoundPosition:" +
						"\n              Up: 3" +
						"\n              Back: 3" +
						"\n          Pitch: 1.5" +
						"\n          Sound: 'ENTITY_CHICKEN_HURT'" +
						"\n          Volume: 0.5" +
						"\n        '12':" +
						"\n          Options:" +
						"\n            IgnoreToggle: true" +
						"\n            Location: DEFAULT" +
						"\n            Radius:" +
						"\n              CrossWorlds: false" +
						"\n          Pitch: 1.0" +
						"\n          Sound: 'ENTITY_CHICKEN_EGG'" +
						"\n          Volume: 10.0" +
						"\n    13000:" +
						"\n      Sounds:" +
						"\n        '0':" +
						"\n          Options:" +
						"\n            IgnoreToggle: false" +
						"\n            Location: DEFAULT" +
						"\n            Radius:" +
						"\n              CrossWorlds: false" +
						"\n          Pitch: 1.0" +
						"\n          Sound: 'AMBIENT_CAVE'" +
						"\n          Volume: 10.0\n";
				}
			}
		}
		try {
			ErrorReport.stringToFile(data, new File(getDataFolder(), "sounds/worldsounds.yml"), false);
		} catch (IOException e) {}
	}

	HashMap<String, Long> playerTime = new HashMap<>();
	HashMap<String, BukkitRunnable> playerRunnable = new HashMap<>();

	void runToggleCooldown(final String p) {
		playerRunnable.put(p, new BukkitRunnable() {
				@Override
				public void run() {
					if (!playerTime.containsKey(p)) {
						playerRunnable.remove(p);
						cancel();
						return;
					}
					if (playerTime.get(p) == 0) {
						playerTime.remove(p);
						playerRunnable.remove(p);
						cancel();
						return;
					}
					playerTime.put(p, playerTime.get(p) - 20);
				}
			});
		playerRunnable.get(p).runTaskTimer(this, 0, 20);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onToggle(ToggleSoundsEvent e) {
		if (e.getAffected() == e.getController()) {
			Player player = e.getController();
			if (!player.hasPermission("playmoresounds.toggle.bypasscooldown")) {
				if (playerTime.containsKey(player.getName())) {
					long remainingCooldown = 0;
					try {
						remainingCooldown = playerTime.get(player.getName());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					player.sendMessage(
						MessageSender.string("General.Prefix") + " " + MessageSender.string("Toggle.Error.Cooldown")
						.replace("<time>", TicksCalculator.getTime(remainingCooldown)));
					e.setCancelled(true);
					return;
				}
			}
			playerTime.put(player.getName(), Utility.formatToTicks(getConfig().getString("ToggleCommand.Cooldown")));
			if (!playerRunnable.containsKey(player.getName())) {
				runToggleCooldown(player.getName());
			}
		}
		if (!e.isCancelled()) {
			if (e.getController() instanceof Player) {
				if (e.getToggleResult()) {
					new SoundPlayer(this).playSound(EventName.CUSTOM, configFile, getConfig(), e.getController(), "ToggleCommand.ToggleSounds.ToggleEnabled", null, null, false, null);
				} else {
					new SoundPlayer(this).playSound(EventName.CUSTOM, configFile, getConfig(), e.getController(), "ToggleCommand.ToggleSounds.ToggleDisabled", null, null, false, null);
				}
			}
		}
	}
}
