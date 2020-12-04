package com.possible_triangle.brazier.block;

import com.possible_triangle.brazier.Content;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;

public class LivingTorchBlock extends TorchBlock {

    public LivingTorchBlock() {
        super(AbstractBlock.Settings.copy(Blocks.TORCH), Content.FLAME_PARTICLE);
    }

}
