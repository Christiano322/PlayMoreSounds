package br.net.christiano322.PlayMoreSounds.addons;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

import br.net.christiano322.PlayMoreSounds.utils.Utility;

public abstract class SoundAddon {

	String addonName;

	public enum SoundType {
		SOUNDSYML, PER_REGION, DEFAULT
		}

	public JarFile getJarFile() {
		for (SoundAddon sA : PMS.plugin.soundAddon.keySet()) {
			if (sA == this) {
				return PMS.plugin.soundAddon.get(sA);
			}
		}
		return null;
	}

	public File getFile() {
		return new File(getJarFile().getName());
	}

	public String getAddonName() {	
		if (addonName != null) {
			if (!addonName.equals("")) {
				return addonName;
			}
		}
		return getFile().getName().substring(0, getFile().getName().lastIndexOf("."));
	}

	public void setAddonName(String name) {
		addonName = name;
	}

	public boolean hasLoadedBefore() {
		File file = new File(getPluginFolder(), "data/addons.dat");
		if (file.exists()) { 
			if (Utility.containsSet(YamlConfiguration.loadConfiguration(file).getKeys(false), CodeReader.formatString(getAddonName().toLowerCase()))) {
				return true;
			}}
		return false;
	}

	/**
	 * This should register events from the specified Listener class.
	 *
	 * @param listener
	 *            The class that implements Listener.
	 */
	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, PMS.plugin);
	}

	/**
	 * Returns the data folder.
	 */
	public File getPluginFolder() {
		return PMS.plugin.getDataFolder();
	}

	/**
	 * This will interrupt the specified event from playing a sound.
	 *
	 * @param event
	 *            The EventName that you want to configure.
	 * @param enabled
	 *            Set the event enabled or disabled
	 */
	public void toggleEvent(EventName event, boolean enabled) {
		if (enabled) {
			if (PMS.plugin.blockedSounds.contains(event)) {
				PMS.plugin.blockedSounds.remove(event);
			}
		} else {
			PMS.plugin.blockedSounds.add(event);
		}
	}

	/**
	 * Should generate configurations and play the sounds that you specify.
	 *
	 * @param type
	 *            This is the FILE that your sound will be generated and searched.
	 *            (If you choose SOUNDSYML, no configuration section will be
	 *            generated)
	 * @param player
	 *            The player that will listen/reproduce the sound.
	 * @param name
	 *            The path of all configurations that PMS will search or generate in
	 *            the specified file.
	 * @param sound
	 *            The default sound settings that will be set to the specified
	 *            configuration section. (Only if was not set before.) Use this
	 *            format:
	 *            Sound=SOUND_HERE;Interval=INTERVAL_HERE,Volume=VOLUME_HERE;Pitch=PITCH_HERE
	 * @param radius
	 *            The default radius that will be set to the specified configuration
	 *            section. (Only if it was not set before.)
	 * @param permission
	 *            The permission that the player requires to play the sound. (Set to
	 *            null if you don't want permissions to this sound.)
	 * @param allInLocation
	 *            Instead of playing a sound to the event maker location, this will
	 *            play a sound to the location of all players inside the radius.
	 * @see <a href=
	 *      "https://playmoresounds.wordpress.com/addons/">https://playmoresounds.wordpress.com/addons/</a>
	 */
	public void play(SoundType type, Player player, String path, Double customRadius, Location customLocation,
					 boolean allInLocation, List<Player> nullPlayerList) throws NullPointerException {
		File file = new File(PMS.plugin.getDataFolder(), "sounds/addons.yml");
		FileConfiguration config = PMS.plugin.customsounds;
		if (type.equals(SoundType.SOUNDSYML)) {
			file = new File(PMS.plugin.getDataFolder(), "sounds.yml");
			config = PMS.plugin.sounds;
		}
		if (type.equals(SoundType.PER_REGION)) {
			file = new File(PMS.plugin.getDataFolder(), "sounds/regions.yml");
			config = PMS.plugin.regions;
		}
		new SoundPlayer(PMS.plugin).playSound(EventName.CUSTOM, file, config, player, path, customRadius, customLocation, allInLocation, nullPlayerList);
	}

	public void play(SoundType type, Player player, String path) {
		play(type, player, path, null, null, false, null);
	}

	/**
	 * You can use this only when Radius is not 0.
	 */
	public void play(SoundType type, String path, Location location) {
		play(type, null, path, null, location, false, null);
	}

	public void play(SoundType type, String path, Location location, List<Player> listeners) {
		play(type, null, path, null, location, false, listeners);
	}

	/**
	 * Creates a new file based on the data.
	 *
	 * @param file
	 *             The file that should be generated.
	 * @param data
	 *             The data of the file.
	 */
	public void stringToFile(File file, String data) throws IOException {
		FileWriter outStream = new FileWriter(file.getAbsolutePath(), true);
        BufferedWriter out = new BufferedWriter(outStream);
        data.replaceAll("\n", System.getProperty("line.separator"));
		out.append(data);
        out.close();
	}

	/**
	 * Sends a message to console
	 * 
	 * @param message
	 *            The message that you want to send to console.
	 */
	public void notifyConsole(String message) {
		PMS.plugin.logger.log("&e[" + getAddonName() + "]&r " + message);
	}

	/**
	 * Returns the addons.yml configuration.
	 * 
	 * @return
	 */
	public FileConfiguration getConfig() {
		return PMS.plugin.customsounds;
	}

	/**
	 * Reload the addons.yml configuration.
	 */
	public void reloadConfig() {
		File f = new File(getPluginFolder(), "sounds/addons.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {}
		}
		PMS.plugin.customsounds = YamlConfiguration.loadConfiguration(f);
	}

	/**
	 * Returns the sound.yml configuration.
	 */
	public FileConfiguration getSoundsConfig() {
		return PMS.plugin.sounds;
	}

	public void toggleVersionRestrictor(boolean value) {
		PMS.plugin.versionRestrictor = value;
	}

	public boolean getVersionRestrictorStatus() {
		return PMS.plugin.versionRestrictor;
	}

	public List<String> getVersionRestrictorVersions() {
		return PMS.plugin.compatibleVersions;
	}

	public List<String> getLanguageRestrictorLanguages() {
		return PMS.plugin.compatibleLang;
	}

	public void toggleCommands(boolean value) {
		PMS.plugin.commandsEnabled = value;
	}

	public void toggleTabCompleter(boolean value) {
		PMS.plugin.tabCompleter = value;
	}

	/**
	 * Reload the sounds.yml configuration.
	 */
	public void reloadSoundsConfig() {
		PMS.plugin.sounds = YamlConfiguration.loadConfiguration(new File(getPluginFolder(), "sounds.yml"));
	}

	/**
	 * Returns the regions.yml configuration.
	 */
	public FileConfiguration getRegionsConfig() {
		return PMS.plugin.regions;
	}

	/**
	 * Reload the regions.yml configuration.
	 */
	public void reloadRegionsConfig() {
		PMS.plugin.regions = YamlConfiguration.loadConfiguration(new File(getPluginFolder(), "sounds/regions.yml"));
	}

	/**
	 * Called when your addon is loaded.
	 */
	public void onStart() throws Exception {
	}

	/**
	 * Called when your addon is unloaded.
	 */
	public void onEnd() throws Exception {
	}
}
