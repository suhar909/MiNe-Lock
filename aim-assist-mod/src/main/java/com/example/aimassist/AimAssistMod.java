
```java
package com.example.aimassist;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AimAssistMod implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean enabled = false;
    private static AimAssistHandler handler;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.aimassist"
        ));

        handler = new AimAssistHandler();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("Aim Assist: " + (enabled ? "§aON" : "§cOFF")),
                        true
                    );
                }
            }

            if (enabled && client.player != null && client.world != null) {
                handler.tick(client);
            }
        });
    }
}
