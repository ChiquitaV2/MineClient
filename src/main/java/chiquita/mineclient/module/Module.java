package chiquita.mineclient.module;

import chiquita.mineclient.Mineclient;
import chiquita.mineclient.settings.Value;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class Module implements Listenable {

    public final static int KEY_UNBOUND = 63;

    private boolean toggled = false;
    public String name;
    public Category category;
    public boolean hasSettings;
    public List<Value> settings;
    public int bind = -2;

    public MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String displayName, Category category, boolean doesItHaveSettings, int defaultBind) {
        this.name = displayName;
        this.category = category;
        this.hasSettings = doesItHaveSettings;
        this.bind = defaultBind;
    }

    public void toggle() {
        toggled = !toggled;
        if (toggled) onEnable();
        else onDisable();
    }

    public enum Category {
        movement, player, world
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) onEnable();
        else onDisable();
    }

    public boolean isToggled() {
        return toggled;
    }

    public void onEnable() {
        Mineclient.eventBus.subscribe(this);
    }

    public void onDisable() {
        try {
            Mineclient.eventBus.unsubscribe(this);
        }
        catch (Exception uhoh) {
            uhoh.printStackTrace();
        }
    }

    public Category getCategory() {
        return category;
    }
    public void signalValueChange(Value value) {

    }


    public void signalEnumChange() {

    }

    public String getName() {
        return name;
    }

    public List<Value> getSettings()
    {
        return settings;
    }
    public int getBind() {
        return bind;
    }

    public void toggleNoSave()
    {
        this.toggled = !toggled;
        if (toggled) onEnable();
        else onDisable();
    }

    public void setBind(String bindString) {
        int bindNum = Integer.parseInt(bindString);
        this.bind = bindNum;
    }

    public void setBind(int bindInt) {
        this.bind = bindInt;
    }

    public void onToggle() {}

}
