package org.marinho.sign;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Getter
public abstract class SignView<P, L, B> {

    protected final String[] text;

    @Setter protected BiPredicate<P, String[]> response;

    protected L location;

    @Accessors(fluent = true)
    protected boolean retryIfFail;

    protected SignHandler<P> handler;
    protected List<P> players;

    @Setter protected boolean updating;

    public SignView(String... text) {
        String[] array;

        if(text.length < 4) {
            List<String> list = new ArrayList<>(Arrays.asList(text));

            for(int i = 0; i < (4 - text.length); i++)
                list.add("");

            array = list.toArray(new String[0]);
        } else array = text;

        this.text = array;
        this.players = new CopyOnWriteArrayList<>();
    }

    public <T extends SignView<P, L, B>> T response(BiPredicate<P, String[]> response) {
        this.response = response;

        return (T) this;
    }

    public <T extends SignView<P, L, B>> T updateLine(int index, Function<String, String> function) {
        this.text[index] = function.apply(text[index]);

        update();

        return (T) this;
    }

    public <T extends SignView<P, L, B>> T retryIfFail(boolean state) {
        this.retryIfFail = state;

        return (T) this;
    }

    public abstract void open(P player);
    public abstract void update();

}