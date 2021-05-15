package org.marinho.sign.bukkit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.marinho.sign.SignHandler;
import org.marinho.sign.bukkit.util.Reflection;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class BukkitHandler implements SignHandler<Player> {

    @Override
    public void inject(Player player) {
        checkNotNull(player);

        if(!player.isOnline()) {
            BukkitView view = BukkitFramework.getInstance().unregister(player);

            if(view != null)
                view.getPlayers().remove(player);

            return;
        }

        Channel channel = Reflection.getChannel(player);

        if(channel.pipeline().get("update_sign") != null)
            return;

        channel.pipeline().addAfter("decoder", "update_sign", new MessageToMessageDecoder() {

            @Override
            protected void decode(ChannelHandlerContext context, Object packet, List out) {
                if(!packet.toString().contains("PacketPlayInUpdateSign"))
                    return;

                BukkitView view = BukkitFramework.getInstance().get(player);

                if(view == null)
                    return;
                else if(view.isUpdating()) {
                    view.setUpdating(false);

                    return;
                }

                BukkitFramework.getInstance().unregister(player);
                view.getPlayers().remove(player);

                eject(player);

                Object[] components = (Object[]) Reflection.invoke(packet, "b");

                if (components == null || components.length <= 0)
                    return;

                String[] text = new String[components.length];

                for(int i = 0; i < components.length; i++)
                    text[i] = (String) Reflection.invoke(components[i], "getText");

                if(view.getResponse() != null) {
                    boolean success = view.getResponse().test(player, text);

                    if (view.retryIfFail() && !success)
                        view.open(player);
                }

                Block block = view.getLocation().getBlock();

                Material material = block == null ? Material.AIR : block.getType();
                byte data = block == null ? (byte) 0 : block.getData();

                player.sendBlockChange(view.getLocation(), material, data);

                out.add(packet);
            }
        });
    }

    @Override
    public void eject(Player player) {
        checkNotNull(player);

        Channel channel = Reflection.getChannel(player);

        if(channel == null)
            return;

        if (channel.pipeline().get("update_sign") != null)
            channel.pipeline().remove("update_sign");
    }
}