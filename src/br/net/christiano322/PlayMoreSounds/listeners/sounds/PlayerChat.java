package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerChat implements Listener {

	PMS main;

	public PlayerChat(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeAsyncChat(AsyncPlayerChatEvent e) {
		try {
			EventName event = EventName.PLAYER_CHAT;
			if (main.getConfig().getBoolean("PerChatWordSounds.Enabled")) {
				String[] split = e.getMessage().split(" ");
				List<String> highlight = new ArrayList<>();
				boolean yes = false;
				for (String string : split) {
					if (main.chatwords.getKeys(false).contains(string)) {
						if (main.chatwords.getConfigurationSection(string).getBoolean("Cancellable") ? !e.isCancelled() : 0 == 0) {
							yes = true;
							highlight.add(string);
							new SoundPlayer(main).playSound(event, main.chatwordsFile, main.chatwords, e.getPlayer(), string, null, null, false, null);
						}
					}
				}
				if (!highlight.isEmpty()) {
					for (String string : highlight) {
						if (main.chatwords.getConfigurationSection(string).getBoolean("Highlight.Enabled")) {
							String message = ChatColor.translateAlternateColorCodes('&', main.chatwords.getConfigurationSection(string).getString("Highlight.Message").replace("<word>", string));
							if (e.getPlayer().hasPermission(main.chatwords.getConfigurationSection(string).getString("Highlight.Permission"))) {
								e.setMessage(e.getMessage().replace(string, message));
							}
						}
					}
				}
				if (yes) {
					return;
				}
			}
			String name = event.toName();
			if (e.isCancelled()) {
				if (main.sounds.getBoolean(name + ".Cancellable")) {
					return;
				}
			}
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
