package chiquita.mineclient;

import chiquita.mineclient.module.ModuleManager;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import net.fabricmc.api.ModInitializer;

public class Mineclient implements ModInitializer {
    public static final String VERSION = "0.0.1";
    public static final String NAME = "MineClient ";
    public static EventBus eventBus = new EventManager();
    public static final ModuleManager moduleManager = new ModuleManager();


    @Override
    public void onInitialize() {
        moduleManager.init();
    }
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

}
