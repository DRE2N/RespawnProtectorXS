/*
 * Copyright (C) 2017 Daniel Saukel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.respawnprotectorxs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class RespawnProtectorXS extends JavaPlugin implements Listener {

    private Set<Entity> cache = new HashSet<>();
    private long time;

    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException exception) {
            }
        }
        if (!getConfig().contains("time")) {
            getConfig().set("time", 5);
            try {
                getConfig().save(configFile);
            } catch (IOException exception) {
            }
        }
        time = (long) (getConfig().getDouble("time") * 20);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        cache.add(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                cache.remove(player);
            }
        }.runTaskLater(this, time);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (cache.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

}
