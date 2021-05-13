package org.marinho.sign;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SignFramework<P, S> {

    @Getter private final Map<P, S> signs = new ConcurrentHashMap<>();

    public void register(P player, S sign) {
        signs.put(player, sign);
    }

    public S unregister(P player) {
        return signs.remove(player);
    }
}