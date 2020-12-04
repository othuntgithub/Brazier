package com.possible_triangle.brazier.entity;

import com.possible_triangle.brazier.Content;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CrazedFlame extends Entity {

    private static final int INITIAL_LIFE = 20 * 4;

    private LivingEntity caster;
    private UUID casterUuid;
    private int life = INITIAL_LIFE;
    private boolean sentSpikeEvent;

    public CrazedFlame(World world, double x, double y, double z, LivingEntity caster) {
        this(world);
        this.setCaster(caster);
        this.setPos(x, y, z);
    }

    @SuppressWarnings("unused")
    public CrazedFlame(World world) {
        this(Content.CRAZED_FLAME, world);
    }

    public CrazedFlame(EntityType<? extends CrazedFlame> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        --this.life;
        if (world instanceof ClientWorld) {
            if (this.life % 4 == 0) {
                for (int i = 0; i < 2; ++i) {
                    double x = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getWidth() * 0.5D;
                    double y = this.getY() - 0.4;
                    double z = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getWidth() * 0.5D;
                    double dx = (this.random.nextDouble() * 2.0D - 1.0D) * 0.05D;
                    double dy = 0.02D + this.random.nextDouble() * 0.05D;
                    double dz = (this.random.nextDouble() * 2.0D - 1.0D) * 0.05D;
                    this.world.addParticle(Content.FLAME_PARTICLE, x, y + 1.0D, z, dx, dy, dz);
                }
            }
        } else {
            if (this.life <= 0) remove();

            if (INITIAL_LIFE - 20 > life && life % 5 == 0) {
                world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(0.2D, 0.2D, 0.2D), $ -> true).forEach(this::damage);
                BlockPos pos = this.getBlockPos();
                BlockState down = world.getBlockState(pos.down());
                if(world.getBlockState(pos).isAir() && down.getMaterial().isBurnable())
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }
        }
    }

    private void damage(LivingEntity target) {
        LivingEntity caster = this.getCaster();
        if (target.isAlive() && !target.isInvulnerable() && target != caster) {
            if (caster == null) {
                target.damage(DamageSource.IN_FIRE, 6.0F);
            } else if (!caster.isTeammate(target)) {
                // TODO fire damage source
                target.damage(new ProjectileDamageSource("inFire", this, caster), 6.0F);
            }
        }
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUuid();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.world instanceof ServerWorld) {
            Entity entity = ((ServerWorld) this.world).getEntity(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }

        return this.caster;
    }

    @Override
    protected void initDataTracker() {
    }

    protected void readCustomDataFromTag(CompoundTag compound) {
        if (compound.containsUuid("owner")) {
            this.casterUuid = compound.getUuid("owner");
        }
        if (compound.contains("life")) this.life = compound.getInt("life");
    }

    protected void writeCustomDataToTag(CompoundTag compound) {
        if (this.casterUuid != null) {
            compound.putUuid("owner", this.casterUuid);
        }
        compound.putInt("life", life);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
