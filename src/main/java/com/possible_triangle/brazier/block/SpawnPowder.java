package com.possible_triangle.brazier.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SpawnPowder extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);

    public SpawnPowder() {
        super(Settings.of(Material.SUPPORTED)
                .noCollision()
                .breakInstantly()
                .luminance($ -> 1)
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        return below.isSideSolid(world, pos.down(), Direction.UP, SideShapeType.FULL);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return canPlaceAt(state, world, pos) ? super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom) : Blocks.AIR.getDefaultState();
    }

}
