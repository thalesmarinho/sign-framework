package org.marinho.sign;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

@Getter
public abstract class SignView<P, L, B> {

    protected final String[] text;

    @Setter protected BiPredicate<P, String[]> response;

    protected L location;
    protected B block;

    public SignView(String... text) {
        String[] array;

        if(text.length < 4) {
            List<String> list = Arrays.asList(text);

            for(int i = 0; i < (4 - text.length); i++)
                list.add("");

            array = list.toArray(new String[0]);
        } else array = text;

        this.text = array;
    }

    public <T extends SignView<P, L, B>> T response(BiPredicate<P, String[]> response) {
        this.response = response;

        return (T) this;
    }

    public abstract void open(P player);
    public abstract SignHandler<P> getHandler();

}