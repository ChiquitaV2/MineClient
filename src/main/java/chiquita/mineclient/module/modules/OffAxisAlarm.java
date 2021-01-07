package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.utils.EntityUtils;
import chiquita.mineclient.utils.Logger;
import chiquita.mineclient.utils.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class OffAxisAlarm extends Module {
    public OffAxisAlarm() {
        super("OffAxisAlarm", Category.world, false, GLFW.GLFW_KEY_Y);
    }

    Timer chatTimer = new Timer();
    Timer timer = new Timer();

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        if (timer.passed(1000)) {
            switch (EntityUtils.determineHighway()) {
                case XP:
                    if (!(mc.player.getZ() > 0 && mc.player.getZ() < 1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            Logger.chatMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case XN:
                    if (!(mc.player.getZ() < 0 && mc.player.getZ() > -1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            Logger.chatMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case ZP:
                    if (!(mc.player.getX() < 0 && mc.player.getX() > -1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            Logger.chatMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case ZN:
                    if (!(mc.player.getX() > 0 && mc.player.getX() < 1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            Logger.chatMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
            }
        }
    });
}