package com.possible_triangle.brazier.block.tile;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;

public class BaseTile extends BlockEntity {

    public BaseTile(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public @Nullable BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 3, this.getUpdateTag());
    }

    public CompoundTag getUpdateTag() {
        return this.toTag(new CompoundTag());
    }

}
