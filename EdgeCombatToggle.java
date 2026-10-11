package edge.toggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** AimAssist + TriggerBot, работают только в своём мире (одиночная/LAN), только по враждебным мобам. */
public final class EdgeCombatToggle implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean enabled = false;
    private final AimAssist aimAssist = new AimAssist();
    private final TriggerBot triggerBot = new TriggerBot();

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.edge_combat_toggle.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyBinding.Category.MISC
        ));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            if (!enabled && !client.isIntegratedServerRunning()) {
                say(client, "[Edge] Работает только в своём мире (одиночная игра или LAN)");
                continue;
            }
            enabled = !enabled;
            say(client, "[Edge] AimAssist + TriggerBot: " + (enabled ? "ВКЛ" : "ВЫКЛ"));
        }

        // На чужом сервере всё выключается.
        if (enabled && !client.isIntegratedServerRunning()) {
            enabled = false;
            say(client, "[Edge] Выключено: это чужой сервер");
        }

        if (!enabled || client.player == null || client.world == null || client.currentScreen != null) return;
        aimAssist.tick(client);
        triggerBot.tick(client);
    }

    private static void say(MinecraftClient client, String msg) {
        if (client.player != null) client.player.sendMessage(Text.literal(msg), true);
    }
}
