package com.example.aimassist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistHandler {
    private static final double MAX_DISTANCE = 64.0;
    private static final float SMOOTHING = 0.1f;

    public void tick(MinecraftClient client) {
        Entity target = findClosestTarget(client);
        if (target == null) return;

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        Vec3d playerPos = client.player.getEyePos();
        
        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;
        
        double distance = Math.sqrt(dx * dx + dz * dz);
        float targetYaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float targetPitch = (float)-(Math.atan2(dy, distance) * 180.0 / Math.PI);
        
        float currentYaw = client.player.getYaw();
        float currentPitch = client.player.getPitch();
        
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float deltaPitch = MathHelper.wrapDegrees(targetPitch - currentPitch);
        
        client.player.setYaw(currentYaw + deltaYaw * SMOOTHING);
        client.player.setPitch(currentPitch + deltaPitch * SMOOTHING);
    }

    private Entity findClosestTarget(MinecraftClient client) {
        Entity closest = null;
        double closestDist = MAX_DISTANCE;

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            if (!(entity instanceof HostileEntity) && !(entity instanceof PlayerEntity)) continue;
            
            double dist = entity.distanceTo(client.player);
            if (dist < closestDist && client.player.canSee(entity)) {
                closest = entity;
                closestDist = dist;
            }
        }

        return closest;
    }
}
