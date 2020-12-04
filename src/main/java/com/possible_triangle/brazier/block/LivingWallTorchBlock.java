package com.possible_triangle.brazier.block;

import com.possible_triangle.brazier.Content;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;

public class LivingWallTorchBlock extends TorchBlock {

    public LivingWallTorchBlock() {
        super(Settings.copy(Blocks.WALL_TORCH), Content.FLAME_PARTICLE);
    }

}
