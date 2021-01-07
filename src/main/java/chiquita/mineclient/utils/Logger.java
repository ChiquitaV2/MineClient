package chiquita.mineclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Logger {
    public static void chatMessage(String s) {
        try {
            MinecraftClient.getInstance().inGameHud.getChatHud()
                    .addMessage(new LiteralText(Text(Formatting.DARK_RED) + "" + s));
        } catch (Exception e) {
            System.out.println("[MineClient] INFO: " + s);
        }
    }

    private static String Text(Formatting color) {
        return color + "[MineClient] \u00A7f";
    }

}
