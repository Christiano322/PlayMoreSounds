package br.net.christiano322.PlayMoreSounds.utils;
import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.sound.*;
import br.net.christiano322.PlayMoreSounds.sound.SoundConfig.*;
import java.util.*;

public class Storage {
	public static String getSoundsData(boolean convert) {
		String data;
		data = "#                                    - Sounds Configuration -\n" +
			"#\n" +
			"#                                      - Multiple Sounds -\n" +
			"#\n" +
			"#ArrowHit: = The event.\n" +
			"#  Cancellable: true = If another plugin has cancelled the event, the sound won't play.\n" +
			"#  Enabled: true = Disable the event if you don't want to play.\n" +
			"#  Sounds:\n" +
			"#    '0': = The interval. You can play multiple sounds at the same time or set different intervals between sounds. Just copy this template, paste below and change the interval.\n" +
			"#      Options:\n" +
			"#        IgnoreToggle: false = If true, even if the player has disabled his sounds with '/pms toggle', the sound will be played.\n" +
			"#        Location: EYE = When a player trigger the event, what location should the plugin take? EYE or DEFAULT? - EYE change when the player is crouching.\n" +
			"#        PermissionListen: 'playmoresounds.sound.arrowhit.listen' = Only the players that have the permission 'playmoresounds.sound.arrowhit.listen' will be able to listen to the sound.\n" +
			"#        PermissionRequired: 'playmoresounds.sound.arrowhit' = The sound will only play if the event trigger has this permission.\n" +
			"#        # 3D sounds based on minecraft mechanics.\n" +
			"#        # Add blocks based on the player location to change the sound location.\n" +
			"#        # Warning: This option consumes lots of memory, long math calculations may decrease performance or crash your server.\n" +
			"#        # Remember: SoundPosition will work properly according to the volume you set below." +
			"#        SoundPosition:\n" +
			"#          Up: 8.0\n" +
			"#          Down: 3.0\n" +
			"#          Front: 7.0\n" +
			"#          Back: 12.0\n" +
			"#          Right: 4.0\n" +
			"#          Left: 1.0\n" +
			"#        Radius:\n" +
			"#          CrossWorlds: true = If the radius is -1 the sound should be played to everyone in the server or to everyone in the world that the event was triggered?\n" +
			"#          Range: -1 = The sound should be played to everyone in the server. Check out the radius tutorial below.\n" +
			"#      Pitch: 1 = The pitch of the sound.\n" +
			"#      Sound: ENTITY_EXPERIENCE_ORB_PICKUP = The sound that you want to play.\n" +
			"#      Volume: 1 = The volume of the sound.\n" +
			"#\n" +
			"# - To see the compatible sounds use the command \"/pms list\".\n" +
			"#\n" +
			"#                                        - =Noteblock Notes= -\n" +
			"#\n" +
			"#  If you want noteblocks, replace Sound to Note.INTRUMENT;NOTE\n" +
			"#INSTRUMENT is the noteblock instrument that you want, NOTE is the note of the noteblock.\n" +
			"#  For some reason, in versions above 1.8.9, noteblocks notes can only be listen if music is on.\n" +
			"#\n" +
			"# ArrowHit:\n" +
			"#   Cancellable: true\n" +
			"#   Enabled: true\n" +
			"#   Sounds:\n" +
			"#     '0':\n" +
			"#       Options:\n" +
			"#         IgnoreToggle: false\n" +
			"#         Location: EYE #You don't need to add SoundPosition, PermissionListen, PermissionRequired if you don't want to.\n" +
			"#         Radius:\n" +
			"#           CrossWorlds: true" +
			"#           Range: -1\n" +
			"#       Pitch: 0\n" +
			"#       Sound: Note.PIANO;24\n" +
			"#       Volume: 0\n" +
			"#\n" +
			"# - To see the compatible noteblock instruments use the command \"/pms list -instruments\".\n" +
			"#\n" +
			"#                                         - =Radius Sounds= -\n" +
			"#\n" +
			"#  If you set the 'Radius' configuration, the plugin will play the sounds to the players that are in\n" +
			"#this distance. For example:\n" +
			"#  PlayerDeath:\n" +
			"#    Radius: 0 = Only the event maker will listen to the sound.\n" +
			"#  PlayerChat:\n" +
			"#    Radius: -1 = All players allowed to hear PlayerChat in the server/world will listen to this sound.\n" +
			"#  PlayerTeleport:\n" +
			"#    Radius: 10 = All players allowed to hear PlayerTeleport in this distance from event maker will\n" + 
			"#listen to this sound.\n" +
			"#\n" +
			"# Turn 'Enabled' to false to disable the event.";
//		if (convert) {
//			FileConfiguration sounds = Main.plugin.sounds;
//			List<String> listName = new ArrayList<>();
//			for (EventName name : EventName.values()) {
//				if (!name.equals(EventName.Custom)) {
//					listName.add(name.toName());
//				}
//			}
//			if (sounds.getString("SoundsConfigVersion").equals("2.4.0")) {
//				for (String s : Main.plugin.sounds.getKeys(false)) {
//					if (listName.contains(s)) {
//						data = data + "\n\n" + s;
//						if (sounds.getDouble(s + ".Radius") < -1) {
//							data = data + "\n  Enabled: false";
//						} else {
//						if (sounds.getConfigurationSection(s).contains("Cancellable")) {
//							data = data + "\n  Cancellable: " + sounds.getBoolean(s + ".Cancellable");
//						}
//						data = data + 
//						}
//					}
//				}
//			} else if (sounds.getString("SoundsConfigVersion").startsWith("2.3")) {
//
//			}
//		} else {
		for (SoundConfig s : getDefaultSounds()) {
			data = data + "\n\n" + s.getSoundName() + ":";
			if (!s.getCancellable().equals(Cancellable.NO)) {
				data = data + "\n  Cancellable: " + s.getCancellable().toString().toLowerCase();
			}
			data = data + "\n  Enabled: " + s.isEnabled();
			if (s.getSoundName().equals("RegionEnter")) {
				data = data + "\n  StopOnExit: false";
			}
			data = data + "\n  Sounds:";
			for (Interval i : s.getIntervals()) {
				data = data + "\n    '" + i.getInterval() + "':";
				data = data + "\n      Options:" +
					"\n        IgnoreToggle: " + i.getIgnoreToggle() +
					"\n        Location: " + i.getLocationType().toString();
				if (!i.getPermissionListen().equals("")) {
					data = data + "\n        PermissionListen: '" + i.getPermissionListen() + "'";
				}
				if (!i.getPermission().equals("")) {
					data = data + "\n        PermissionRequired: '" + i.getPermission() + "'";
				}
				if (!i.getPositions().equals("0;0;0;0;0;0")) {
					data = data + "\n        SoundPosition:";
					String[] split = i.getPositions().split(";");
					if (!split[0].equals("0")) {
						data = data + "\n          Up: " + split[0];
					}
					if (!split[1].equals("0")) {
						data = data + "\n          Down: " + split[1];
					}
					if (!split[2].equals("0")) {
						data = data + "\n          Front: " + split[2];
					}
					if (!split[3].equals("0")) {
						data = data + "\n          Back: " + split[3];
					}
					if (!split[4].equals("0")) {
						data = data + "\n          Right: " + split[4];
					}
					if (!split[5].equals("0")) {
						data = data + "\n          Left: " + split[5];
					}
				}
				data = data + "\n        Radius:" +
					"\n          CrossWorlds: " + i.crossWorlds() +
					"\n          Range: " + i.getRadius() +
					"\n      Pitch: " + i.getPitch() +
					"\n      Sound: '" + i.getSound().toString() + "'" +
					"\n      Volume: " + i.getVolume();
			}
		}
		//}
		data = data + "\n\n#Don't touch this if you don't want your configuration to be restored." +
			"\nSoundsConfigVersion: " + PMS.plugin.pVersion;
		return data;
	}

	static List<SoundConfig> getDefaultSounds() {
		List<SoundConfig> sounds = new ArrayList<SoundConfig>();

		List<Interval> aH = new ArrayList<>();
		aH.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.arrowhit", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP, 10F, true));
		sounds.add(new SoundConfig("ArrowHit", Cancellable.TRUE, true, aH));

		List<Interval> bE = new ArrayList<>();
		bE.add(new Interval(0L, false, Interval.LocationType.EYE, "", "playmoresounds.sound.bedenter", "0;0;0;0;0;0", 8D, 0.6F, SoundEnum.ENTITY_VILLAGER_AMBIENT, 0.5F, true));
		sounds.add(new SoundConfig("BedEnter", Cancellable.TRUE, true, bE));

		List<Interval> bL = new ArrayList<>();
		bL.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.bedleave", "0;0;0;0;0;0", 0D, 0F, SoundEnum.AMBIENT_CAVE, 0F, true));
		sounds.add(new SoundConfig("BedLeave", Cancellable.NO, false, bL));

		List<Interval> cH = new ArrayList<>();
		cH.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.changehotbar", "0;0;0;0;0;0", 0D, 2F, SoundEnum.BLOCK_NOTE_BLOCK_HAT, 10F, true));
		sounds.add(new SoundConfig("ChangeHotbar", Cancellable.FALSE, true, cH));

		List<Interval> cL = new ArrayList<>();
		cL.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.changelevel", "0;1;0;0;0;0", 0D, 2F, SoundEnum.ENTITY_PLAYER_LEVELUP, 0.55F, true));
		sounds.add(new SoundConfig("ChangeLevel", Cancellable.NO, true, cL));

		List<Interval> cE = new ArrayList<>();
		cE.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.craftingextract", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_PLAYER_LEVELUP, 0.4F, true));
		sounds.add(new SoundConfig("CraftingExtract", Cancellable.NO, true, cE));

		List<Interval> fJ = new ArrayList<>();
		fJ.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		fJ.add(new Interval(2L, false, Interval.LocationType.DEFAULT, "", "", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		fJ.add(new Interval(13L, false, Interval.LocationType.DEFAULT, "", "", "0;0;0;0;0;0", -1D, 2F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		sounds.add(new SoundConfig("FirstJoin", Cancellable.NO, true, fJ));

		List<Interval> fE = new ArrayList<>();
		fE.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.furnaceextract", "0;0;0;0;0;0", 15D, 1F, SoundEnum.BLOCK_FIRE_EXTINGUISH, 0.9F, true));
		fE.add(new Interval(1L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.furnaceextract", "0;0;0;0;0;0", 15D, 2F, SoundEnum.BLOCK_FIRE_EXTINGUISH, 0.9F, true));
		sounds.add(new SoundConfig("FurnaceExtract", Cancellable.NO, true, fE));

		List<Interval> gC = new ArrayList<>();
		gC.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.gamemodechange", "0;0;0;0;0;0", 0D, 1F, SoundEnum.BLOCK_ANVIL_LAND, 10F, true));
		sounds.add(new SoundConfig("GamemodeChange", Cancellable.TRUE, true, gC));

		List<Interval> hH = new ArrayList<>();
		hH.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.handhit", "0;0;0;0;0;0", 0D, 2F, SoundEnum.ENTITY_PLAYER_HURT, 1F, true));
		sounds.add(new SoundConfig("HandHit", Cancellable.TRUE, true, hH));

		List<Interval> iC = new ArrayList<>();
		iC.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.inventoryclick", "0;0;0;0;0;0", 0D, 1F, SoundEnum.UI_BUTTON_CLICK, 0.4F, true));
		sounds.add(new SoundConfig("InventoryClick", Cancellable.TRUE, true, iC));

		List<Interval> jS = new ArrayList<>();
		jS.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "playmoresounds.sound.joinserver.listen", "playmoresounds.sound.joinserver", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		sounds.add(new SoundConfig("JoinServer", Cancellable.NO, true, jS));

		List<Interval> lS = new ArrayList<>();
		lS.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "playmoresounds.sound.leaveserver.listen", "playmoresounds.sound.leaveserver", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		lS.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "playmoresounds.sound.leaveserver.listen", "playmoresounds.sound.leaveserver", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		lS.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "playmoresounds.sound.leaveserver.listen", "playmoresounds.sound.leaveserver", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		sounds.add(new SoundConfig("LeaveServer", Cancellable.NO, true, lS));

		if (!PMS.plugin.bukkitVersion.contains("1.12") & !PMS.plugin.bukkitVersion.contains("1.13")) {
			List<Interval> pA = new ArrayList<>();
			pA.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerachievement", "0;0;0;0;0;0", 0D, 0F, SoundEnum.BLOCK_ANVIL_BREAK, 0F, true));
			sounds.add(new SoundConfig("PlayerAchievement", Cancellable.TRUE, true, pA));
		}

		if (PMS.plugin.bukkitVersion.contains("1.12") | PMS.plugin.bukkitVersion.contains("1.13")) {
			List<Interval> pAD  = new ArrayList<>();
			pAD.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playeradvancementdone", "0;0;0;0;0;0", 0D, 0F, SoundEnum.BLOCK_ANVIL_BREAK, 0F, true));
			sounds.add(new SoundConfig("PlayerAdvancementDone", Cancellable.NO, true, pAD));
		}

		List<Interval> pB = new ArrayList<>();
		pB.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerban", "0;0;0;0;0;0", -1D, 0F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		pB.add(new Interval(10L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerban", "0;0;0;0;0;0", -1D, 0F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		pB.add(new Interval(20L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerban", "0;0;0;0;0;0", -1D, 0F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		sounds.add(new SoundConfig("PlayerBan", Cancellable.TRUE, true, pB));

		List<Interval> pC  = new ArrayList<>();
		pC.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerchat", "0;0;0;0;0;0", -1D, 1F, SoundEnum.ENTITY_ITEM_PICKUP, 0.5F, true));
		sounds.add(new SoundConfig("PlayerChat", Cancellable.TRUE, true, pC));

		List<Interval> pCo  = new ArrayList<>();
		pCo.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playercommand", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_ITEM_PICKUP, 10F, true));
		sounds.add(new SoundConfig("PlayerCommand", Cancellable.TRUE, true, pCo));

		List<Interval> pD  = new ArrayList<>();
		pD.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerdeath", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_WITHER_SPAWN, 10F, true));
		sounds.add(new SoundConfig("PlayerDeath", Cancellable.NO, true, pD));

		List<Interval> pDr  = new ArrayList<>();
		pDr.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerdrop", "0;0;0;0;0;0", 12D, 1F, SoundEnum.ENTITY_EGG_THROW, 0.5F, true));
		sounds.add(new SoundConfig("PlayerDrop", Cancellable.TRUE, true, pDr));

		List<Interval> pF  = new ArrayList<>();
		pF.add(new Interval(0L, false, Interval.LocationType.EYE, "", "playmoresounds.sound.playerflight", "0;0;0;0;0;0", 8D, 1F, SoundEnum.BLOCK_PISTON_EXTEND, 0.35F, true));
		sounds.add(new SoundConfig("PlayerFlight", Cancellable.TRUE, true, pF));

		List<Interval> pFS  = new ArrayList<>();
		pFS.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerflightstop", "0;0;0;0;0;0", 8D, 1F, SoundEnum.BLOCK_PISTON_CONTRACT, 0.4F, true));
		sounds.add(new SoundConfig("PlayerFlightStop", Cancellable.TRUE, true, pFS));

		List<Interval> pJ = new ArrayList<>();
		pJ.add(new Interval(0L, false, Interval.LocationType.EYE, "", "playmoresounds.sound.playerjump", "0;1;0;0;0;0", 0D, 0F, SoundEnum.AMBIENT_CAVE, 0F, true));
		sounds.add(new SoundConfig("PlayerJump", Cancellable.TRUE, false, pJ));

		List<Interval> pK = new ArrayList<>();
		pK.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerkicked", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		pK.add(new Interval(10L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerkicked", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		pK.add(new Interval(20L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerkicked", "0;0;0;0;0;0", -1D, 1F, SoundEnum.BLOCK_NOTE_BLOCK_BASS, 10F, true));
		sounds.add(new SoundConfig("PlayerKicked", Cancellable.TRUE, true, pK));

		List<Interval> pT  = new ArrayList<>();
		pT.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerteleport", "0;0;0;0;0;0", 16D, 1F, SoundEnum.ENTITY_ENDERMAN_TELEPORT, 1F, true));
		sounds.add(new SoundConfig("PlayerTeleport", Cancellable.TRUE, true, pT));

		List<Interval> pUB = new ArrayList<>();
		pUB.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerunban", "0;0;0;0;0;0", -1D, 2F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		pUB.add(new Interval(10L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerunban", "0;0;0;0;0;0", -1D, 2F, SoundEnum.BLOCK_NOTE_BLOCK_PLING, 10F, true));
		pUB.add(new Interval(20L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.playerunban", "0;0;0;0;0;0", -1D, 1.5F, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP, 10F, true));
		sounds.add(new SoundConfig("PlayerUnBan", Cancellable.TRUE, true, pUB));

		List<Interval> rE  = new ArrayList<>();
		rE.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.regionenter", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, true));
		sounds.add(new SoundConfig("RegionEnter", Cancellable.TRUE, true, rE));

		List<Interval> rL  = new ArrayList<>();
		rL.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.regionleave", "0;0;0;0;0;0", 0D, 1F, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, true));
		sounds.add(new SoundConfig("RegionLeave", Cancellable.TRUE, true, rL));

		List<Interval> sH  = new ArrayList<>();
		sH.add(new Interval(0L, false, Interval.LocationType.DEFAULT, "", "playmoresounds.sound.swordhit", "0;0;0;0;0;0", 12D, 1F, SoundEnum.ENTITY_PLAYER_HURT, 1F, true));
		sounds.add(new SoundConfig("SwordHit", Cancellable.TRUE, true, sH));

		return sounds;
	}
}
	

