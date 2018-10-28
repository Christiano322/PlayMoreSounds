package br.net.christiano322.PlayMoreSounds.utils;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.*;
import java.io.*;
import java.util.*;
import org.bukkit.configuration.file.*;
import org.bukkit.*;

public class MessageSender {

    public static String string(String string) {
		return ChatColor.translateAlternateColorCodes('&', get(string));
	}
	
	static String get(String string) {
		try {
			FileConfiguration conf = PlayMoreSounds.getPMSConfig();
			FileConfiguration jar = PMS.plugin.languageJar;
			FileConfiguration lang = PMS.plugin.language;
			FileConfiguration langEn = PMS.plugin.languageEn;
			if (conf.getBoolean("ExtractLanguageFiles")) {
				if (lang.contains(string)) {
					return lang.getString(string);
				} else if (jar.contains(string)) {
					return jar.getString(string);
				} else if (langEn.contains(string)) {
					return langEn.getString(string);
				} else {
					return internal(string);
				}
			} else {
				if (lang.contains(string)) {
					return lang.getString(string);
				} else if (langEn.contains(string)) {
					return langEn.getString(string);
				} else {
					return internal(string);
				}
			}
		} catch (Exception e) {
			PMS.plugin.logger.warn("Language file has generated an exception.");
			ErrorReport.errorReport(e, "MessageSend Exception:");
			return "&fString not found.";
		}
	}

	public static HashMap<String, String> getAvailableStrings() {
		HashMap<String, String> list = new HashMap<>();
        list.put("Confirm.Error", "&cThere's nothing to confirm!");
		list.put("Description.Header", "&6&m------------&6[&9PlayMoreSounds v<version>&6]&m------------");
		list.put("Description.Help", "&6Type \"&7&n/<label> help&6\" to see the list of commands.");
		list.put("General.AllFiles", "All files");
		list.put("General.Command", "Command");
		list.put("General.InvalidArguments", "&cIncorrect command syntax! Try \"&7&n/<label> <label2> <args>&c\".");
      	list.put("General.NoPermission", "&4You don't have permission to do that!");
		list.put("General.NobodyOnline", "&cThere are no online players on the server.");
		list.put("General NotANumber", "&cThis isn't a number! (\"<number>\")");
		list.put("General.NotAPlayer", "&cYou must be a player to do this.");
		list.put("General PlayerNotFound", "&cThe player \"<player>\" was not found.");
		list.put("General.Prefix", "&6[&9PlayMoreSounds&6]");
		list.put("General.Time.Hour", "hour");
		list.put("General.Time.Hours", "hours");
		list.put("General Time.Minute", "minute");
		list.put("General Time.Minutes", "minutes");
		list.put("General.Time.Second", "second");
		list.put("General.Time.Seconds", "seconds");
		list.put("General.UnknownCommand", "&cUnknown command. Use \"&7&n/<label> help&c\" to see the list of commands available to you.");
		list.put("General.You", "you");
		list.put("Help.Confirm", "&6&n/<label> confirm\n&7 > Confirms something");
		list.put("Help.Edit", "&6&n/<label> edit <config> <type> <value>\n&7 > Edits something in config");
		list.put("Help.Header", "&6&m+             &6&l<s>[&9&lPlayMoreSounds<s>Help<s><page>/<totalpages>&6&l]&6<s>&m             +\n&6<s><><s>Required<s>Argument;<s>[]<s>Optional<s>Argument.");
		list.put("Help.List", "&6&n/<label> list <page> [-instruments]\n&7 > Lists compatible sounds/instruments");
		list.put("Help.Page.Click", "&7Click to go to page <page>.");
		list.put("Help.Page.More", "&6&l-&9&l Type &7&l&n/<label> help <page>&9&l to see more commands.");
		list.put("Help.Page.NotExists", "&cThis page doesn't exists!");
		list.put("Help.Play", "&6&n/<label> play <sound> <arg> <arg2> <arg3>\n&7 > Plays a sound");
		list.put("Help.Region.Default", "&6&n/<label> region <create|delete|rename|set|wand>\n&7 > Regions command");
		list.put("Help Region.Create", "&6&n/<label> region create <region>\n&7 > Creates a region based on your selection");
		list.put("Help.Region.Delete", "&6&n/<label> region delete <region>\n&7 > Deletes an existing region");
		list.put("Help.Region.Rename", "&6&n/<label> region rename <region> <newname>\n&7 > Renames a region");
		list.put("Help.Region.Set", "&6&n/<label> region set <p1|p2> [<x> <y> <z>] [world]\n&7 > Sets the selection positions by command");
		list.put("Help.Region.Wand", "&6&n/<label> region wand\n&7 > Gives you the selection item");
		list.put("Help.Reload", "&6&n/<label> reload\n&7 > Reloads the plugin");
		list.put("Help.Required", "&7 > Arguments required: <args>");
		list.put("Help.Restore", "&6&n/<label> restore [filename]\n&7 > Restores a file");
		list.put("Help.Sounds", "&6&n/<label> sounds <arg>\n&7 > Checks if the sounds are on or off");
		list.put("Help.Toggle", "&6&n/<label> toggle <arg>\n&7 > Enable/Disable sounds");
		list.put("Help.Update", "&6&n/<label> update\n&7 > Checks for updates");
		list.put("List.Error.NotExists", "&cThis page doesn't exists! Maximum: <totalpages>.");
		list.put("List.Footer", "&7View other <type> with \"&n/<label> list <args>&7\"");
		list.put("List.Header", "&7List of available <type> [Page <page> of <totalpages>]:");
		list.put("List.Type.Instruments", "Instruments");
		list.put("List.Type.Sounds", "Sounds");
		list.put("Play.Error.ListError", "&cThis sound doesn't exists. Type &7/pms list&c to see the list of available sounds.");
		list.put("Play.Success", "&7Playing the sound &f<sound> &7to &f<player>&7 with volume &f<volume>&7 and pitch &f<pitch>");
		list.put("Region.Creation.Error.AlreadyExists", "&cThis region already exists! Type \"&7&n/<label> <label2> delete <region>&c\" to delete this region.");
		list.put("Region.Creation.Error.Default", "&cAn error has occurred while creating region &7<region>&c!");
		list.put("Region.Creation.Error.DifferentWorlds", "&cYou need to select two locations in the same world!");
		list.put("Region.Creation.Error.NotSelected", "&cType \"&7&n/<label> <label2> wand&c\" to get the tool to select the region that you want to create.");
		list.put("Region.Creation.Success", "&aRegion &7<region>&a created successfully!");
		list.put("Region.Delete.Error.Default", "&cAn error has occurred while deleting <region> region!");
		list.put("Region.Delete.Error.NotExists", "&cThe region \"<region>\" was not found in the system!");
		list.put("Region.Delete.Process", "&aDeleting &7<region>&a region...");
		list.put("Region.PositionSelected1", "&6Position &a#1&6 selected! <coordinates>");
		list.put("Region.PositionSelected2", "&6Position &d#2&6 selected! <coordinates>");
		list.put("Region.Region", "region");
		list.put("Region.Rename.Error.Default", "&cAn error has occurred while renaming \"&7<region>&a\" to \"&7<newname>&a\".");
		list.put("Region.Rename.Error.NotExists", "&cThe region \"<region>\" was not found in the system!");
		list.put("Region.Rename.Error.NotInside", "&cYou are not inside a sound region!");
		list.put("Region.Rename.Success", "&aThe region \"&7<region>&a\" was successfully renamed to &7<newname>&a.");
		list.put("Region.Wand", "&6Wand tool: Left-Click selects position &a#1&6 and Right-Click selects position &d#2&6.");
		list.put("Reload", "&7Configuration reloaded.");
		list.put("Restore.Confirm", "&6To confirm the restoration of &7<file>&6, type: &7/<label> confirm&6");
		list.put("Restore.Error.Default", "&cAn error has occurred while restoring <file>!");
		list.put("Restore.Error.NotAFile", "&c\"<file>\" is not a valid file! Type &7/<label> restore&c to see the available files.");
		list.put("Restore.Files", "&6Available files: [&7AddonsConf&6, &7All&6, &7ChatWords&6, &7Commands&6, &7Config&6, &7Gamemodes&6, &7Lang<lang>&6, &7Regions&6, &7Sounds&6, &7WorldSounds&6]");
		list.put("Restore.Success.Default", "&f<file>&a has been restored successfully!");
		list.put("Restore.Success.Plural", "&f<file>&a have been restored successfully!");
		list.put("Sounds.Disabled.Default", "&cThe sounds are disabled!");
		list.put("Sounds.Disabled.Player", "&cThe sounds of &f<player>&c are disabled!");
		list.put("Sounds.Enabled.Default", "&aThe sounds are enabled!");
		list.put("Sounds.Enabled.Player", "&aThe sounds of &f<player>&a are enabled!");
		list.put("Toggle.Disabled.Default", "&cSounds toggled to off!");
		list.put("Toggle.Disabled.Player", "&cSounds of &f<player>&c toggled to off!");
		list.put("Toggle.Enabled.Default", "&aSounds toggled to on!");
		list.put("Toggle.Enabled.Player", "&aSounds of &f<player>&a toggled to on!");
		list.put("Toggle.Error.BlackListedWorld", "&cYou still can not hear the sounds because an operator has put this world into the blacklisted worlds.");
		list.put("Toggle.Error.Cooldown", "&cYou need to wait <time> before toggling sounds again.");
		list.put("Toggle.Error.StillCanNotHear", "&cThis player still can not hear the sounds because he is in a blacklisted world.");
		list.put("Update.Check", "&6Checking for updates...");
		list.put("Update.Downloaded", "&6The update has been downloaded. Restart your server to use it.");
		list.put("Update.Error.Check", "&4Failed to check for updates.");
		list.put("Update.Error.Download", "&4Failed to download the update.");
		list.put("Update.Found.Auto", "&aUpdate found! Downloading <update>...");
		list.put("Update.Found.Info", "&aUpdate found! <update>.");
		list.put("Update.JsonChat", "&6Click to download the update.");
		list.put("Update.NotFound", "&6No updates found.");
		return list;
	}

    public static String internal(String string) {
		HashMap<String, String> strings = getAvailableStrings();
		if (strings.containsKey(string)) {
			return strings.get(string);
		}
        return "&fString not found.";
    }
}
