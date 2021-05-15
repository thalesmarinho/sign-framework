package org.marinho.sign.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.marinho.sign.SignView;
import org.marinho.sign.bukkit.util.Reflection;

import static com.google.common.base.Preconditions.checkNotNull;

public class BukkitView extends SignView<Player, Location, Block> {

    public BukkitView(String... text) {
        super(text);

        handler = new BukkitHandler();
    }

    @Override
    public void open(Player player) {
        checkNotNull(player);

        if(!players.contains(player))
            players.add(player);

        location = player.getLocation().clone()
                .subtract(0, 5, 0);

        player.closeInventory();
        player.sendBlockChange(location, Material.SIGN_POST, (byte) 0);
        player.sendSignChange(location, text);

        Reflection.sendPacket(player, "PacketPlayOutOpenSignEditor",
                Reflection.newInstance("BlockPosition", location.getX(),
                        location.getY(), location.getZ()));

        handler.inject(player);

        BukkitFramework.getInstance().register(player, this);
    }

    @Override
    public void update() {
        for(Player player : players) {
            setUpdating(true);
            open(player);
        }
    }
}