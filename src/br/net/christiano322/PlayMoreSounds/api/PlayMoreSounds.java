package br.net.christiano322.PlayMoreSounds.api;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.io.*;
import java.util.*;
import org.bukkit.configuration.file.*;

public class PlayMoreSounds {

	public enum EventName {
		ARROW_HIT("ArrowHit"),
		BED_ENTER("BedEnter"),
		BED_LEAVE("BedLeave"),
		CHANGE_HOTBAR("ChangeHotbar"),
		CHANGE_LEVEL("ChangeLevel"),
		CRAFTING_EXTRACT("CraftingExtract"),
		CUSTOM("Custom"),
		FIRST_JOIN("FirstJoin"),
		FURNACE_EXTRACT("FurnaceExtract"),
		GAMEMODE_CHANGE("GamemodeChange"),
		HAND_HIT("HandHit"),
		INVENTORY_CLICK("InventoryClick"),
		JOIN_SERVER("JoinServer"),
		LEAVE_SERVER("LeaveServer"),
		PLAYER_ACHIEVEMENT("PlayerAchievement"),
		PLAYER_ADVANCEMENT_DONE("PlayerAdvancementDone"),
		PLAYER_BAN("PlayerBan"),
		PLAYER_CHAT("PlayerChat"),
		PLAYER_COMMAND("PlayerCommand"),
		PLAYER_DEATH("PlayerDeath"),
		PLAYER_DROP("PlayerDrop"),
		PLAYER_FLIGHT("PlayerFlight"),
		PLAYER_FLIGHT_STOP("PlayerFlightStop"),
		PLAYER_JUMP("PlayerJump"),
		PLAYER_KICKED("PlayerKicked"),
		PLAYER_TELEPORT("PlayerTeleport"),
		REGION_ENTER("RegionEnter"),
		REGION_LEAVE("RegionLeave"),
		SWORD_HIT("SwordHit"),
		WORLD_TIME("WorldTime");

		String name;

		EventName(String name) {
			this.name = name;
		}

		public String toName() {
			return name;
		}
	}

	public static enum PMSFile {
		ADDONS, CHATWORDS, COMMANDS, CONFIG, GAMEMODES, REGIONS, SOUNDS, WORLDSOUNDS
		}

	public static void toggleErrorMessages(boolean sounds, boolean intervals, boolean volumes, boolean pitches) {
		PMS.plugin.isSoundErrorsEnabled = sounds;
		PMS.plugin.isIntervalErrorsEnabled = intervals;
		PMS.plugin.isVolumeErrorsEnabled = volumes;
		PMS.plugin.isPitchErrorsEnabled = pitches;
	}

	public static List<String> getSoundList() {
		return PMS.plugin.soundsNames;
	}

	public static void togglePostions(boolean positions) {
		PMS.plugin.isPositionsEnabled = positions;
	}

	public static FileConfiguration getPMSConfig() {
		return PMS.plugin.getConfig();
	}

	public static FileConfiguration getSoundsConfig() {
		return PMS.plugin.sounds;
	}

	public static FileConfiguration getCustomConfig() {
		return PMS.plugin.customsounds;
	}

	public static FileConfiguration getCommandsConfig() {
		return PMS.plugin.commands;
	}

	public static FileConfiguration getRegionsConfig() {
		return PMS.plugin.regions;
	}

	public static FileConfiguration getGamemodesConfig() {
		return PMS.plugin.gamemodes;
	}

	public static double getSoundPositionsOf(String event, int section, String position) {
		List<String> intervals = new ArrayList<>();
		intervals.addAll(PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getKeys(false));
		return PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getConfigurationSection(intervals.get(section)).getConfigurationSection("Options.SoundPosition").getDouble(position);
	}

	public static double getRadiusOf(String event, int section) {
		List<String> intervals = new ArrayList<>();
		intervals.addAll(PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getKeys(false));
		return PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getConfigurationSection(intervals.get(section)).getDouble("Options.Radius.Range");
	}

	public static boolean isCancellableEnabled(String event) {
		if (PMS.plugin.sounds.getConfigurationSection(event).getBoolean("Cancellable")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSoundOf(String event, int section) {
		List<String> intervals = new ArrayList<>();
		intervals.addAll(PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getKeys(false));
		return PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getConfigurationSection(intervals.get(section)).getString("Sound");
	}

	public static Float getVolumeOf(String event, int section) {
		List<String> intervals = new ArrayList<>();
		intervals.addAll(PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getKeys(false));
		return Float.valueOf((float) PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getConfigurationSection(intervals.get(section)).getDouble("Volume"));
	}

	public static Float getPitchOf(String event, int section) {
		List<String> intervals = new ArrayList<>();
		intervals.addAll(PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getKeys(false));
		return Float.valueOf((float) PMS.plugin.sounds.getConfigurationSection(event + ".Sounds").getConfigurationSection(intervals.get(section)).getDouble("Pitch"));
	}

	public static void restoreFile(PMSFile file) throws IOException {
		PMS.plugin.Restore(file);
	}

	public static RegionManager getRegionManager() {
		return new RegionManager();
	}

	public static MessageSender getMessageSender() {
		return new MessageSender();
	}
}
