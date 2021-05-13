package org.marinho.sign.bukkit.util;

import io.netty.channel.Channel;
import org.apache.commons.lang.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    public static String getPackage() {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version = pkg.substring(pkg.lastIndexOf(".") + 1);

        return "net.minecraft.server." + version;
    }

    public static Class<?>[] toParamTypes(Object... params) {
        Class<?>[] classes = new Class<?>[params.length];

        for (int i = 0; i < params.length; i++)
            classes[i] = ClassUtils.wrapperToPrimitive(params[i].getClass());

        return classes;
    }

    public static void sendPacket(Player player, String name, Object... params) {
        Object connection = getConnection(player);

        try {
            connection.getClass().getMethod("sendPacket", getClass(getPackage() +
                    ".Packet")).invoke(connection, callConstructor(getClass(getPackage() +
                    "." + name), params));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object invokeMethod(Object object, String name, Object... params) {
        try {
            Method method = object.getClass().getDeclaredMethod(name, toParamTypes(params));
            method.setAccessible(true);

            return method.invoke(object, params);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }

    public static Object callConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> connection = clazz.getDeclaredConstructor(toParamTypes(params));
            connection.setAccessible(true);

            return connection.newInstance(params);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }

    public static Object getConnection(Player player) {
        return getField(invokeMethod(player, "getHandle"), "playerConnection");
    }

    public static Channel getChannel(Player player) {
        Object connection = getConnection(player);
        Object network = getField(connection, "networkManager");

        return (Channel) getField(network, "channel");
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception exception) {
            return null;
        }
    }

    public static Object getField(Object object, String field) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);

            return f.get(object);
        } catch (Exception exception) {
            exception.printStackTrace();

            return null;
        }
    }
}