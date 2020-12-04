package com.possible_triangle.brazier.mixin;

import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.entity.Crazed;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WoodlandMansionGenerator.Piece.class)
public class WoodlandMansionPiecesMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lnet/minecraft/structure/WoodlandMansionGenerator$Piece;handleMetadata(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/ServerWorldAccess;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;)V")
    public void handleDataMarker(String function, BlockPos pos, ServerWorldAccess world, Random random, BlockBox box, CallbackInfo callback) {
        // TODO use config
        //if (BrazierConfig.SERVER.SPAWN_CRAZED.get()) {
        //double chance = BrazierConfig.SERVER.CRAZED_CHANCE.get();
        double chance = 1D;
        if (function.equals("Mage") && chance > 0 && random.nextDouble() <= chance) {
            Crazed crazed = Content.CRAZED.create(world.toServerWorld());
            assert crazed != null;
            crazed.setPersistent();
            crazed.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            crazed.initialize(world, world.getLocalDifficulty(crazed.getBlockPos()), SpawnReason.STRUCTURE, null, null);
            world.spawnEntity(crazed);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            callback.cancel();
        }
        //}
    }

}
