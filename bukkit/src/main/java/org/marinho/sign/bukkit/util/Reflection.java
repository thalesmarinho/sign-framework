package org.marinho.sign.bukkit.util;

import com.google.common.primitives.Primitives;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class to reflection for bukkit
 *
 * @since 1.0.0
 */
public class Reflection {

    private final static String nms;

    static {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version = pkg.substring(pkg.lastIndexOf(".") + 1);

        nms = "net.minecraft.server." + version + ".";
    }

    /**
     * Sends a packet to a player, with the arguments presented.
     *
     * @param player The player who will receive the packet
     * @param name   The name of the method to invoke
     * @param args   The arguments to be provided to the packet
     * @since 1.0.0
     */
    public static void sendPacket(Player player, String name, Object... args) {
        try {
            Object connection = getField(invoke(player, "getHandle"), "playerConnection");
            assert connection != null;

            connection.getClass().getMethod("sendPacket", getClass("Packet"))
                    .invoke(connection, newInstance(name, args));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the player's {@link io.netty.channel.Channel}.
     *
     * @param player The player that we are going to get the channel
     * @return The player's {@link io.netty.channel.Channel}
     * @since 1.0.0
     */
    public static Channel getChannel(Player player) {
        Object connection = getField(invoke(player, "getHandle"),
                "playerConnection");
        Object network = getField(connection, "networkManager");

        return (Channel) getField(network, "channel");
    }

    /**
     * Convert the given objects to type parameter and
     * cast their primitives.
     *
     * @param args The arguments to convert and cast
     * @return The converted arguments
     * @since 1.0.0
     */
    public static Class<?>[] toPrimitives(Object... args) {
        Class<?>[] classes = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++)
            classes[i] = Primitives.unwrap(args[i].getClass());

        return classes;
    }

    /**
     * Invokes a method with the arguments presented.
     *
     * @param clazz The class where the method is
     * @param name  The name of the method
     * @param args  The arguments to be provided to the method
     * @return <code>null</code> or the result of invoking the method
     * @since 1.0.0
     */
    public static Object invoke(Object clazz, String name, Object... args) {
        try {
            Method method = clazz.getClass().getDeclaredMethod(name, toPrimitives(args));
            method.setAccessible(true);

            return method.invoke(clazz, args);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }

    /**
     * Instantiates an object with the arguments presented.
     *
     * @param name The name of the class
     * @param args The arguments to be provided when instantiating
     * @return 1.0.0
     */
    public static Object newInstance(String name, Object... args) {
        try {
            Class<?> clazz = getClass(name);
            assert clazz != null;

            Constructor<?> constructor = clazz.getDeclaredConstructor(toPrimitives(args));
            constructor.setAccessible(true);

            return constructor.newInstance(args);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }

    /**
     * Gets the class by the given name.
     *
     * @param name The name of the class
     * @return The class with the given name
     * @since 1.0.0
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(nms + name);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Gets the contents of the field by the given name.
     *
     * @param clazz The class where the field is
     * @param name  The name of the field
     * @return The field's content
     * @since 1.0.0
     */
    public static Object getField(Object clazz, String name) {
        try {
            Field field = clazz.getClass().getDeclaredField(name);
            field.setAccessible(true);

            return field.get(clazz);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }
}