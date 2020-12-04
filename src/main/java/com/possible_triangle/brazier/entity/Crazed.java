package com.possible_triangle.brazier.entity;

import com.possible_triangle.brazier.Content;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Crazed extends SpellcastingIllagerEntity {

    public static final double BUFF_RADIUS = 7;

    @SuppressWarnings("unused")
    public Crazed(World world) {
        this(Content.CRAZED, world);
    }

    public static void init(EntityType<? extends LivingEntity> type) {
        FabricDefaultAttributeRegistry.register(type, HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D)
        );
    }

    public Crazed(EntityType<? extends Crazed> type, World world) {
        super(type, world);
    }

    // TODO spawn egg
    //@Override
    //public ItemStack getPickedResult(RayTraceResult target) {
    //    return Content.CRAZED_SPAWN_EGG.map(ItemStack::new).orElse(ItemStack.EMPTY);
    //}

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.add(5, new BuffSpellGoal());
        this.goalSelector.add(6, new FlameSpellGoal());
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, RaiderEntity.class)).setGroupRevenge());
        this.targetSelector.add(2, (new FollowTargetGoal<>(this, PlayerEntity.class, true)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (new FollowTargetGoal<>(this, VillagerEntity.class, false)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, false));
        this.targetSelector.add(4, new FollowTargetGoal<>(this, GuardianEntity.class, false));
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ITEM_FIRECHARGE_USE;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
    }

    class FlameSpellGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private FlameSpellGoal() {
        }


        protected int getSpellTicks() {
            return 40;
        }

        protected int startTimeDelay() {
            return 100;
        }

        protected void castSpell() {
            LivingEntity target = Crazed.this.getTarget();
            if (target != null) spawnFlame(target.getPos().x, target.getPos().y, target.getPos().z);
        }

        private void spawnFlame(double x, double y, double z) {
            BlockPos blockpos = new BlockPos(x, y, z);
            if (!Crazed.this.world.isWater(blockpos))
                Crazed.this.world.spawnEntity(new CrazedFlame(Crazed.this.world, x, y + 0.4, z, Crazed.this));
        }

        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
        }

        protected Spell getSpell() {
            return Spell.FANGS;
        }
    }

    class BuffSpellGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private BuffSpellGoal() {
        }

        protected int getSpellTicks() {
            return 10;
        }

        protected int startTimeDelay() {
            return 200;
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && getTargets().stream().anyMatch(e -> !e.hasStatusEffect(StatusEffects.FIRE_RESISTANCE));
        }

        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(BUFF_RADIUS), e ->
                    e.isAlive() && (EntityTypeTags.RAIDERS.contains(e.getType()) || e.isTeammate(Crazed.this))
            );
        }

        protected void castSpell() {
            getTargets().forEach(this::buff);
        }

        private void buff(LivingEntity entity) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 10, 0));
        }

        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
        }

        protected Spell getSpell() {
            return Spell.FANGS;
        }
    }

    public static boolean canSpawnHere(EntityType<? extends HostileEntity> type, ServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
        return canMobSpawn(type, world, reason, pos, random);
    }

}
