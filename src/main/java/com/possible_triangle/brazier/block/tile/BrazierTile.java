package com.possible_triangle.brazier.block.tile;

import com.google.common.collect.Maps;
import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.block.BrazierBlock;
import com.possible_triangle.brazier.config.DistanceHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class BrazierTile extends BaseTile implements Tickable {

    private static final HashMap<BlockPos, Integer> BRAZIERS = Maps.newHashMap();
    private static final float TOLERANCE = 0.03F;

    private int ticksExisted = 0;
    private int height = 0;

    public static boolean isBorder(Vec3d pos) {
        synchronized (BRAZIERS) {
            return BRAZIERS.entrySet().stream().anyMatch(e -> {
                double dist = DistanceHandler.getDistance(pos, e.getKey());
                double minDist = Math.pow(e.getValue() - TOLERANCE, 2);
                double maxDist = Math.pow(e.getValue() + TOLERANCE, 2);
                return dist <= maxDist && dist >= minDist;
            });
        }
    }

    public static boolean inRange(BlockPos pos) {
        synchronized (BRAZIERS) {
            return BRAZIERS.entrySet().stream().anyMatch(e -> {
                // TODO use config
                //if (!BrazierConfig.SERVER.PROTECT_ABOVE.get() && e.getKey().getY() < pos.getY()) return false;
                double dist = DistanceHandler.getDistance(pos, e.getKey());
                int maxDist = e.getValue() * e.getValue();
                return dist <= maxDist;
            });
        }
    }

    private void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
            markDirty();
            if (this.pos != null) synchronized (BRAZIERS) {
                BRAZIERS.put(pos, getRange());
            }
        }
    }

    @Override
    public void tick() {
        ++this.ticksExisted;
        if (this.ticksExisted % 40 == 0) checkStructure();

        if (this.height > 0 && world instanceof ServerWorld && ticksExisted % 10 == 0) {
            ((ServerWorld) world).spawnParticles(Content.FLAME_PARTICLE, pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, 1, 0.4, 0.8, 0.4, 0);
        }
    }

    public void playSound(SoundEvent sound) {
        if (world != null) world.playSound(null, this.pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private void checkStructure() {
        if (world != null) {
            int height = findHeight();
            if (height != this.height) {

                setHeight(height);
                BlockState s = world.getBlockState(pos);
                world.setBlockState(pos, s.with(BrazierBlock.LIT, height > 0));
                if (height > 0) playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                else playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            }
        }
    }

    private int findHeight() {
        assert world != null;
        // TODO use config
        //int max = BrazierConfig.SERVER.MAX_HEIGHT.get();
        int max = 10;
        if (!world.getBlockState(pos.up()).isAir()) return 0;
        for (int height = 1; height <= max; height++) {
            boolean b = true;
            for (int x = -2; x <= 2; x++)
                for (int z = -2; z <= 2; z++)
                    if (Math.abs(x * z) < 4) {
                        BlockState state = world.getBlockState(pos.add(x, -height, z));
                        b = b && Content.BRAZIER_BASE_BLOCKS.contains(state.getBlock());
                    }
            if (!b) return height - 1;
        }
        return max;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        super.fromTag(state, nbt);
        if (nbt.contains("height")) setHeight(nbt.getInt("height"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag nbt = super.toTag(tag);
        nbt.putInt("height", height);
        return nbt;
    }


    public BrazierTile() {
        super(Content.BRAZIER_TILE);
    }

    public int getRange() {
        if (height <= 0) return 0;
        // TODO use config
        //return BrazierConfig.SERVER.BASE_RANGE.get() + BrazierConfig.SERVER.RANGE_PER_LEVEL.get() * (height - 1);
        return 20;
    }

    public int getHeight() {
        return height;
    }

    // TODO find alternative
    /*
    @Override
    public void onLoad() {
        if (this.height > 0) synchronized (BRAZIERS) {
            BRAZIERS.put(pos, getRange());
        }
    }
    */

    @Override
    public void markRemoved() {
        synchronized (BRAZIERS) {
            BRAZIERS.remove(pos);
        }
    }

    @Override
    public double getSquaredRenderDistance() {
        return height;
    }

}
