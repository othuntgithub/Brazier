package com.possible_triangle.brazier.block;

import com.possible_triangle.brazier.Content;
import com.possible_triangle.brazier.block.tile.BrazierTile;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BrazierBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public BrazierBlock() {
        super(Settings.of(Material.METAL)
                .strength(3.0F)
                .luminance(s -> s.get(LIT) ? 15 : 0));
        setDefaultState(super.getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0, 0, 0, 16, 4, 16);

    public static boolean prevents(Entity entity) {
        EntityType<?> type = entity.getType();
        return (entity instanceof Monster && !Content.BRAZIER_WHITELIST.contains(type))
                || Content.BRAZIER_BLACKLIST.contains(type);
    }

    public static boolean prevents(SpawnReason reason) {
        switch (reason) {
            case CHUNK_GENERATION:
            case NATURAL:
            case PATROL:
                return true;
            default:
                return false;
        }
    }

    // TODO Call this
    public static boolean mobSpawn(ServerWorld world, Entity entity, BlockPos pos, SpawnReason reason) {

        // Check for spawn powder
        // TODO use config
        //if (BrazierConfig.SERVER.SPAWN_POWDER.get()) {
        Block block = world.getBlockState(pos).getBlock();
        if (Content.SPAWN_POWDER.equals(block)) {
            return false;
        }
        //}

        return prevents(reason) && prevents(entity) && BrazierTile.inRange(pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new BrazierTile();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty() && Content.TORCHES.contains(stack.getItem())) {
            if (!player.isCreative()) stack.decrement(1);
            player.giveItemStack(new ItemStack(Content.LIVING_TORCH, 1));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune() && state.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.damage(DamageSource.IN_FIRE, 2F);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

}
