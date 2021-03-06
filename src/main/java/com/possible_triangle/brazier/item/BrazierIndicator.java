package com.possible_triangle.brazier.item;

import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.block.tile.BrazierTile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;

import java.util.stream.Stream;

public class BrazierIndicator {

    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote || event.player.ticksExisted % 2 != 0) return;
        ServerWorld world = (ServerWorld) event.player.world;

        Stream<ItemStack> items = Stream.of(event.player.getHeldItemOffhand(), event.player.getHeldItemMainhand());
        if (items.map(ItemStack::getItem).anyMatch(Content.INDICATORS::contains)) {

            int step = 3;
            float radius = 5F;
            for (int i = -step; i <= step; i++) {
                float ry = i / (float) step;
                double y = event.player.prevPosY + ry * (radius / 2) + 1;
                for (float r = 0; r < radius; r += 0.2F) {
                    int degSteps = (int) (1 - r / radius) * 33 + 12;
                    for (int deg = 0; deg < 360; deg += degSteps) {
                        float rad = (float) ((deg + i + event.player.ticksExisted) / 180F * Math.PI);
                        double x = event.player.prevPosX + MathHelper.sin(rad) * r;
                        double z = event.player.prevPosZ + MathHelper.cos(rad) * r;
                        if (BrazierTile.isBorder(new Vector3d(x, y, z))) {
                            world.spawnParticle(Content.FLAME_PARTICLE.get(), x, y, z, 1, 0, 0.2, 0, 0.01);
                        }
                    }
                }
            }

        }
    }

}
