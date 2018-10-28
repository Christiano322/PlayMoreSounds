package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

public class PlayerHit implements Listener {

	PMS main;

	public PlayerHit(PMS main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakePlayerHit(EntityDamageByEntityEvent e) {
		try {
			Entity damager = e.getDamager();
			if ((main.bukkitVersion.contains("1.7")) | (main.bukkitVersion.contains("1.8"))) {
				if ((!(e.getEntity() instanceof Monster)) & (!(e.getEntity() instanceof Animals))
					& (!(e.getEntity() instanceof NPC)) & (!(e.getEntity() instanceof Slime))
					& (!(e.getEntity() instanceof Squid)) & (!(e.getEntity() instanceof Bat))
					& (!(e.getEntity() instanceof Snowman)) & (!(e.getEntity() instanceof IronGolem))) {
					return;
				}
			}
			if ((!main.bukkitVersion.contains("1.7")) & (!main.bukkitVersion.contains("1.8"))) {
				if ((!(e.getEntity() instanceof Monster)) & (!(e.getEntity() instanceof Animals))
					& (!(e.getEntity() instanceof NPC)) & (!(e.getEntity() instanceof Slime))
					& (!(e.getEntity() instanceof Squid)) & (!(e.getEntity() instanceof Bat))
					& (!(e.getEntity() instanceof org.bukkit.entity.Shulker))
					& (!(e.getEntity() instanceof Snowman)) & (!(e.getEntity() instanceof IronGolem))) {
					return;
				}
			}
			Entity entity = e.getEntity();
			if (!(entity instanceof LivingEntity)) {
				return;
			}
			LivingEntity victim = (LivingEntity) entity;
			if (victim.getNoDamageTicks() > victim.getMaximumNoDamageTicks() / 2.0F) {
				return;
			}
			if (damager instanceof Player) {
				Player p = (Player) damager;
				Location loc = victim.getLocation();
				if (p.getItemInHand().getType().toString().contains("SWORD")) {
					EventName event = EventName.SWORD_HIT;
					String name = event.toName();
					if (e.isCancelled()) {
						if (main.sounds.getBoolean(name + ".Cancellable")) {
							return;
						}
					}
					new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, loc, false, null);
					return;
				}
				EventName event = EventName.HAND_HIT;
				String name = event.toName();
				if (e.isCancelled()) {
					if (main.sounds.getBoolean(name + ".Cancellable")) {
						return;
					}
				}
				new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, loc, false, null);
			}
			if (damager instanceof Arrow) {
				Arrow arrow = (Arrow) damager;
				if (arrow.getShooter() instanceof Player) {
					Player p = (Player) arrow.getShooter();
					EventName event = EventName.ARROW_HIT;
					String name = event.toName();
					if (e.isCancelled()) {
						if (main.sounds.getBoolean(name + ".Cancellable")) {
							return;
						}
					}
					new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, null, false, null);
				}
			}
		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
			ErrorReport.errorReport(l, "PlayerHit Exception:");
		}
	}
}
