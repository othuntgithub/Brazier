package com.possible_triangle.brazier.data;

import com.possible_triangle.brazier.Content;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.possible_triangle.brazier.Content.MODID;

public final class LootInjects {

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, identifier, builder, lootTableSetter) -> {
            if (injects.isEmpty()) addTables();
            long injected = injects.stream()
                    .filter(i -> i.predicate.get())
                    .filter(i -> i.into.equals(identifier))
                    .map(i -> LootPool.builder().with(LootTableEntry.builder(i.name)))
                    .peek(builder::pool)
                    .count();

            if (injected > 0) LOGGER.info(String.format("Injected %d pools into '%s'", injected, identifier));
        });
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Inject> injects = new ArrayList<>();

    private static void addTables() {

        addInject("warped_wart", Blocks.NETHER_WART.getLootTableId(), LootPool.builder()
                .with(ItemEntry.builder(Content.WARPED_NETHERWART)
                        .conditionally(RandomChanceLootCondition.builder(0.02F))
                        .conditionally(BlockStatePropertyLootCondition.builder(Blocks.NETHER_WART)
                                .properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))
                        )
                )
        );

        addInject("flame_jungle_temple", LootTables.JUNGLE_TEMPLE_CHEST, LootPool.builder()
                        .with(ItemEntry.builder(Content.LIVING_FLAME))
                        .with(EmptyEntry.Serializer().weight(1)),
                () -> true // TODO use config
        );

        addInject("wither_ash", EntityType.WITHER_SKELETON.getLootTableId(), LootPool.builder()
                .with(ItemEntry.builder(Content.ASH)
                        .apply(SetCountLootFunction.builder(UniformLootTableRange.between(-1, 2)))
                        .apply(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0, 1)))
                )
        );
    }

    private static void addInject(String injectName, Identifier into, LootPool.Builder pool) {
        addInject(injectName, into, pool, () -> true);
    }

    private static void addInject(String injectName, Identifier into, LootPool.Builder pool, Supplier<Boolean> predicate) {
        Identifier loc = new Identifier(MODID, "inject/" + injectName);
        if (injects.stream().anyMatch(i -> i.name.equals(loc))) {
            LOGGER.warn(String.format("Attempted to add duplicate inject table '%s'", injectName));
        } else {
            injects.add(new Inject(loc, into, predicate));
        }
    }

    public static class Inject {
        public final Identifier name;
        public final Identifier into;
        public final Supplier<Boolean> predicate;

        private Inject(Identifier inject, Identifier into, Supplier<Boolean> predicate) {
            this.name = inject;
            this.into = into;
            this.predicate = predicate;
        }
    }

}
