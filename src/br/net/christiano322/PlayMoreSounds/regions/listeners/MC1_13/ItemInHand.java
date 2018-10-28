package br.net.christiano322.PlayMoreSounds.regions.listeners.MC1_13;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;

public class ItemInHand
{
	public static ItemStack getItemInMainHand(Player p) {
		return p.getInventory().getItemInMainHand();
	}
}
