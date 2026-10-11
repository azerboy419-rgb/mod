package edge.toggle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

final class AimAssist {
    void tick(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null || client.world == null || !p.isAlive()) return;

        Vec3d eye = p.getEyePos();
        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;

        for (Entity e : client.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == p || !le.isAlive()) continue;
            if (!Config.matches(le)) continue;

            Vec3d point = le.getBoundingBox().getCenter();
            double dist = eye.distanceTo(point);
            if (dist > Config.DISTANCE) continue;
            if (!Config.STICKY && !p.canSee(le)) continue;

            double angle = Math.abs(MathHelper.wrapDegrees(yawTo(eye, point) - p.getYaw()));
            if (angle > Config.FOV / 2.0) continue;

            double score;
            switch (Config.PRIORITY) {
                case "Health": score = le.getHealth(); break;
                case "Angle": score = angle; break;
                default: score = dist; break;
            }
            if (score < bestScore) {
                bestScore = score;
                best = le;
            }
        }
        if (best == null) return;

        Vec3d t = best.getBoundingBox().getCenter();
        double dx = t.x - eye.x, dy = t.y - eye.y, dz = t.z - eye.z;
        float targetYaw = yawTo(eye, t);
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        float k = (float) ((Config.STRENGTH / 100.0) / Config.SPEED);
        p.setYaw(p.getYaw() + MathHelper.wrapDegrees(targetYaw - p.getYaw()) * k);
        p.setPitch(MathHelper.clamp(p.getPitch() + (targetPitch - p.getPitch()) * k, -90.0f, 90.0f));
    }

    private static float yawTo(Vec3d from, Vec3d to) {
        return (float) (Math.toDegrees(Math.atan2(to.z - from.z, to.x - from.x)) - 90.0);
    }
}
