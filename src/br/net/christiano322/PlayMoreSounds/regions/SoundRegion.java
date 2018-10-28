package br.net.christiano322.PlayMoreSounds.regions;

import java.io.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

//import java.io.File;

//import org.bukkit.configuration.file.FileConfiguration;

public class SoundRegion {

    private String id;
    private Location P1;
    private Location P2;
    private File file;

    public SoundRegion(String id, Location P1, Location P2, File file) {
        this.id = id;
        this.P1 = P1;
        this.P2 = P2;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return getId();
    }

    public Location getPosition1() {
        return P1;
    }

    public Location getPosition2() {
        return P2;
    }

    public World getWorld() {
        return Bukkit.getWorld(YamlConfiguration.loadConfiguration(file).getString("World"));
    }

    public void setPosition1(Location loc) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("World", loc.getWorld().getName());
        config.set("Locations.P1.X", loc.getBlockX());
        config.set("Locations.P1.Y", loc.getBlockY());
        config.set("Locations.P1.Z", loc.getBlockZ());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPosition2(Location loc) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("World", loc.getWorld().getName());
        config.set("Locations.P2.X", loc.getBlockX());
        config.set("Locations.P2.Y", loc.getBlockY());
        config.set("Locations.P2.Z", loc.getBlockZ());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renameTo(String string) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("Name", string);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        file.delete();
    }
}
