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

        Channel channel = Reflection.getChannel(player);

        if(channel == null)
            return;

        channel.pipeline().addAfter("decoder", "update_sign", new MessageToMessageDecoder() {

            @Override
            protected void decode(ChannelHandlerContext context, Object packet, List out) {
                if(!packet.toString().contains("PacketPlayInUpdateSign"))
                    return;

                BukkitView view = BukkitFramework.getInstance().unregister(player);

                if(view == null)
                    return;

                Object[] components = (Object[]) Reflection.invokeMethod(packet, "b");

                if (components == null || components.length <= 0) {
                    eject(player);

                    return;
                }

                String[] text = new String[components.length];

                for(int i = 0; i < components.length; i++)
                    text[i] = (String) Reflection.invokeMethod(components[i], "getText");

                view.getResponse().and((player, value) -> { eject(player);
                    Block block = view.getBlock();

                    Material material = block == null ? Material.AIR : block.getType();
                    byte data = block == null ? (byte) 0 : block.getData();

                    player.sendBlockChange(view.getLocation(), material, data);

                    return true;
                }).test(player, text);

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