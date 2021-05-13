package org.marinho.sign.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.marinho.sign.SignHandler;
import org.marinho.sign.SignView;
import org.marinho.sign.bukkit.util.Reflection;

import static com.google.common.base.Preconditions.checkNotNull;

public class BukkitView extends SignView<Player, Location, Block> {

    private BukkitHandler handler;

    public BukkitView(String... text) {
        super(text);
    }

    @Override
    public void open(Player player) {
        checkNotNull(player);

        this.location = player.getLocation().clone()
                .subtract(0, 5, 0);

        Block block = location.getBlock();

        if(block != null && !block.getType().equals(Material.AIR))
            this.block = block;

        player.sendBlockChange(location, Material.SIGN_POST, (byte) 0);
        player.sendSignChange(location, text);

        assert block != null;

        Reflection.sendPacket(player, "PacketPlayOutOpenSignEditor",
                Reflection.callConstructor(Reflection.getClass(Reflection.getPackage()
                        + ".BlockPosition"), block.getX(), block.getY(), block.getZ()));

        handler = new BukkitHandler();
        handler.inject(player);

        BukkitFramework.getInstance().register(player, this);
    }

    @Override
    public SignHandler<Player> getHandler() {
        return handler;
    }
}