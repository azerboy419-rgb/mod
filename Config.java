package edge.toggle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/** Все настройки в одном месте. Меняются прямо здесь и пересобираются. */
final class Config {
    // AimAssist (значения из исходного AimAssist.java, кроме приоритета)
    static final String PRIORITY = "Health";   // Distance / Health / Angle
    static final double SPEED = 3.0;            // 1..10
    static final double STRENGTH = 100.0;       // 0..100
    static final double DISTANCE = 4.5;         // 1..8 блоков
    static final double FOV = 90.0;             // 1..360 градусов
    static final boolean STICKY = false;        // true = целиться и сквозь стены

    // TriggerBot
    static final boolean WEAPON_ONLY = true;    // бить только с оружием в руке
    static final double REACH = 3.0;            // дистанция атаки

    // В качестве цели берутся ТОЛЬКО другие игроки.
    private Config() {}

    static boolean matches(LivingEntity e) {
        // Игнорируем своего же игрока
        if (e == MinecraftClient.getInstance().player) {
            return false;
        }
        // Возвращаем true только если сущность — другой игрок
        return e instanceof PlayerEntity;
    }
}

