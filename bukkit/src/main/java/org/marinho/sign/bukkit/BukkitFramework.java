package org.marinho.sign.bukkit;

import org.bukkit.entity.Player;
import org.marinho.sign.SignFramework;

public class BukkitFramework extends SignFramework<Player, BukkitView> {

    private static final BukkitFramework instance = new BukkitFramework();

    public static BukkitFramework getInstance() {
        return instance;
    }
}