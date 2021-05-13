package org.marinho.sign;

public interface SignHandler<P> {

    void inject(P player);
    void eject(P player);

}