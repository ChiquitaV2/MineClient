package chiquita.mineclient.event;

public class KeyPressEvent extends Event {
    public final int keyCode;
    public final int action;

    public KeyPressEvent(int keyCode, int action)
    {
        this.keyCode = keyCode;
        this.action = action;
    }
}

