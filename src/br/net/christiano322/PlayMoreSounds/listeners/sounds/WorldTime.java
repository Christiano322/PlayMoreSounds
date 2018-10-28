package br.net.christiano322.PlayMoreSounds.listeners.sounds;
import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;

public class WorldTime {

	static HashMap<World, BukkitRunnable> map = new HashMap<>();

	PMS main;

	public WorldTime(PMS main) {
		this.main = main;
	}

	public void time() {
		for (final World w : Bukkit.getWorlds()) {
			if (containsW(main, w)) {
				final SoundPlayer player = new SoundPlayer(main);
				map.put(w, new BukkitRunnable(){
						@Override
						public void run() {
							if (main.worldsounds.getConfigurationSection(w.getName()).getConfigurationSection("WorldTimeSounds").contains(String.valueOf(w.getTime()))) {
								if (w.getPlayers().size() != 0) {
									for (Player p : w.getPlayers()) {
										player.playSound(EventName.WORLD_TIME, main.worldsoundsFile, main.worldsounds, null, w.getName() + ".WorldTimeSounds." + w.getTime(), -1.0, p.getLocation(), false, null);
									}
								}
							}
						}
					});
				map.get(w).runTaskTimer(main, 0, 1);
			}
		}
	}
	boolean containsW(PMS main, World w) {
		if (main.worldsounds.contains(w.getName())) {
			if (main.worldsounds.getConfigurationSection(w.getName()).contains("WorldTimeSounds")) {
				return true;
			}
		}
		return false;
	}
}
