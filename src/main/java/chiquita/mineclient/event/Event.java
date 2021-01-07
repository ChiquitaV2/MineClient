package chiquita.mineclient.event;

import me.zero.alpine.event.type.Cancellable;

public class Event extends Cancellable {

    private Era era = Era.PRE;

    public enum Era {
        PRE,
        POST
    }

    public Event(Era Era) {
        era = Era;
    }

    public Event() {

    }

    public Era getEra() {
        return era;
    }
}