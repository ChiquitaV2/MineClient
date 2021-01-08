package chiquita.mineclient.module;

import chiquita.mineclient.Mineclient;
import chiquita.mineclient.event.KeyPressEvent;
import chiquita.mineclient.module.modules.*;
import chiquita.mineclient.settings.Value;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager extends Object implements Listenable {
    public static ArrayList<Module> modules = new ArrayList<Module>();

    public static void init() {
        Mineclient.eventBus.subscribe(Mineclient.getModuleManager());
        add(new Yaw());
        add(new OffAxisAlarm());
        add(new Nuker());
        add(new HotbarCache());
        add(new AutoSwitcher());
        add(new AutoWalk());
        add(new AutoTunnel());
        add(new AutoEat());
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModule(Class module) {
        for (Module mod : modules) {
            if (mod.getClass() == module)
                return mod;
        }
        return null;
    }

    public static void add(Module mod) {
        try
        {
            for (Field field : mod.getClass().getDeclaredFields())
            {
                if (Value.class.isAssignableFrom(field.getType()))
                {
                    if (!field.isAccessible())
                    {
                        field.setAccessible(true);
                    }
                    final Value val = (Value) field.get(mod);
                    val.initializeMod(mod);
                }
            }
            modules.add(mod);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static final List<Module> getModuleList(Module.Category category) {
        List<Module> list = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory().equals(category)) {
                list.add(module);
            }
        }
        // Organize alphabetically or ppl will get mad :D
        list.sort(Comparator.comparing(Module::getName));
        return list;
    }

    public static ModuleManager get() {
        return Mineclient.getModuleManager();
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (name.equalsIgnoreCase(m.getName()))
                return m;
        }
        return null;
    }

    @EventHandler
    private Listener<KeyPressEvent> keyPressEventListener = new Listener<>(event -> {
        if (event.action != GLFW.GLFW_PRESS) return;
        if (MinecraftClient.getInstance().currentScreen != null) return;
        modules.stream().filter(m -> m.getBind() == event.keyCode).forEach(Module::toggle);
    });
}