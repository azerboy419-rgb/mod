package edge.toggle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

final class TriggerBot {
    void tick(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (Config.WEAPON_ONLY && !holdingWeapon(p)) return;
        if (!(client.crosshairTarget instanceof EntityHitResult hit)) return;

        Entity target = hit.getEntity();
        if (!(target instanceof LivingEntity le) || !le.isAlive() || !Config.matches(le)) return;
        if (p.getEyePos().distanceTo(hit.getPos()) > Config.REACH) return;
        if (p.getAttackCooldownProgress(0.5f) < 1.0f) return;

        client.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
    }

    private static boolean holdingWeapon(ClientPlayerEntity p) {
        ItemStack s = p.getMainHandStack();
        return s.isIn(ItemTags.SWORDS) || s.isIn(ItemTags.AXES)
                || s.isOf(Items.MACE) || s.isOf(Items.TRIDENT);
    }
}
