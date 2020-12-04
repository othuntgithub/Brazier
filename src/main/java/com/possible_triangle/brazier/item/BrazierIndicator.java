package com.possible_triangle.brazier.item;

import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.block.tile.BrazierTile;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.stream.Stream;

public class BrazierIndicator {

    public static void playerTick(ServerPlayerEntity player) {
        if (player.age % 2 != 0) return;
        ServerWorld world = player.getServerWorld();

        Stream<ItemStack> items = Stream.of(player.getOffHandStack(), player.getMainHandStack());
        if (items.map(ItemStack::getItem).anyMatch(Content.INDICATORS::contains)) {

            int step = 3;
            float radius = 5F;
            for (int i = -step; i <= step; i++) {
                float ry = i / (float) step;
                double y = player.getPos().y + ry * (radius / 2) + 1;
                for (float r = 0; r < radius; r += 0.2F) {
                    int degSteps = (int) (1 - r / radius) * 33 + 12;
                    for (int deg = 0; deg < 360; deg += degSteps) {
                        float rad = (float) ((deg + i + player.age) / 180F * Math.PI);
                        double x = player.getPos().x + MathHelper.sin(rad) * r;
                        double z = player.getPos().z + MathHelper.cos(rad) * r;
                        if (BrazierTile.isBorder(new Vec3d(x, y, z))) {
                            world.spawnParticles(Content.FLAME_PARTICLE, x, y, z, 1, 0, 0.2, 0, 0.01);
                        }
                    }
                }
            }

        }
    }

}
