package com.possible_triangle.brazier;

import com.possible_triangle.brazier.block.BrazierBlock;
import com.possible_triangle.brazier.block.LivingTorchBlock;
import com.possible_triangle.brazier.block.LivingWallTorchBlock;
import com.possible_triangle.brazier.block.SpawnPowder;
import com.possible_triangle.brazier.block.tile.BrazierTile;
import com.possible_triangle.brazier.block.tile.render.BrazierRenderer;
import com.possible_triangle.brazier.entity.Crazed;
import com.possible_triangle.brazier.entity.CrazedFlame;
import com.possible_triangle.brazier.entity.render.CrazedFlameRenderer;
import com.possible_triangle.brazier.entity.render.CrazedRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraft.util.registry.Registry.*;

public class Content {

    public static final String MODID = "brazier";

    public static Identifier modLoc(String path) {
        return new Identifier(MODID, path);
    }

    public static final Tag<Block> BRAZIER_BASE_BLOCKS = TagRegistry.block(modLoc("brazier_base_blocks"));
    public static final Tag<EntityType<?>> BRAZIER_WHITELIST = TagRegistry.entityType(modLoc("brazier_whitelist"));
    public static final Tag<EntityType<?>> BRAZIER_BLACKLIST = TagRegistry.entityType(modLoc("brazier_blacklist"));

    public static final Tag<Item> TORCHES = TagRegistry.item(modLoc("torches"));
    public static final Tag<Item> INDICATORS = TagRegistry.item(modLoc("range_indicator"));
    public static final Tag<Item> ASH_TAG = TagRegistry.item(modLoc("ash"));
    public static final Tag<Item> WARPED_WART_TAG = TagRegistry.item(modLoc("warped_wart"));

    // TODO use own particle
    //public static final ParticleType FLAME_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, "flame", () -> new ParticleType<>(false));
    public static final ParticleEffect FLAME_PARTICLE = ParticleTypes.FLAME;

    public static final BrazierBlock BRAZIER = registerBlock("brazier", new BrazierBlock(), p -> p.group(ItemGroup.MISC));
    public static final BlockEntityType<BrazierTile> BRAZIER_TILE = Registry.register(BLOCK_ENTITY_TYPE, modLoc("brazier"),
            BlockEntityType.Builder.create(BrazierTile::new, BRAZIER).build(null)
    );

    public static final Block LIVING_TORCH_BLOCK = Registry.register(BLOCK, "living_torch", new LivingTorchBlock());
    public static final Block LIVING_TORCH_BLOCK_WALL = Registry.register(BLOCK, "living_wall_torch", new LivingWallTorchBlock());
    public static final Block LIVING_LANTERN = registerBlock("living_lantern", new LanternBlock(AbstractBlock.Settings.copy(Blocks.LANTERN)), p -> p.group(ItemGroup.DECORATIONS));

    public static final Item LIVING_FLAME = Registry.register(ITEM, modLoc("living_flame"), new Item(new Item.Settings().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)));
    public static final Item LIVING_TORCH = Registry.register(ITEM, modLoc("living_torch"), new WallStandingBlockItem(LIVING_TORCH_BLOCK, LIVING_TORCH_BLOCK_WALL, (new Item.Settings()).group(ItemGroup.DECORATIONS)));

    public static final Item ASH = Registry.register(ITEM, "ash", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
    public static final Item WARPED_NETHERWART = Registry.register(ITEM, "warped_nether_wart", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
    public static final Block SPAWN_POWDER = registerBlock("spawn_powder", new SpawnPowder(), p -> p.group(ItemGroup.MATERIALS));

    public static final EntityType<Crazed> CRAZED = Registry.register(ENTITY_TYPE, "crazed",
            FabricEntityTypeBuilder.<Crazed>create(SpawnGroup.MONSTER, Crazed::new)
                    .fireImmune()
                    .build()
    );

    public static final SpawnEggItem CRAZED_SPAWN_EGG = Registry.register(ITEM, "crazed_spawn_egg", new SpawnEggItem(CRAZED,
            new Color(9804699).getRGB(),
            new Color(0x89CB07).getRGB(),
            new Item.Settings().group(ItemGroup.MISC)
    ));

    public static final EntityType<CrazedFlame> CRAZED_FLAME = Registry.register(ENTITY_TYPE, "crazed_flame",
            FabricEntityTypeBuilder.<CrazedFlame>create(SpawnGroup.MISC, CrazedFlame::new)
                    .dimensions(new EntityDimensions(0.6F, 0.6F, true))
                    .fireImmune()
                    .build()
    );

    public static <B extends Block> B registerBlock(String name, B supplier, Function<Item.Settings, Item.Settings> props) {
        B block = Registry.register(BLOCK, modLoc(name), supplier);
        Registry.register(ITEM, name, new BlockItem(block, props.apply(new Item.Settings())));
        return block;
    }

    public static void setup() {
        Crazed.init(Content.CRAZED);
    }

    // TODO register particles
    //public static void registerParticles(ParticleFactoryRegistry event) {
    //    MinecraftClient.getInstance().particleManager.registerFactory(FLAME_PARTICLE, FlameParticle.Factory::new);
    //}


    // TODO Item colors
    //@Environment(EnvType.CLIENT)
    //public static void itemColors(ColorHandlerEvent.Item event) {
    //
    //    event.getItemColors().register((s, i) -> {
    //        if (s.getItem() instanceof LazySpawnEgg) {
    //            LazySpawnEgg egg = (LazySpawnEgg) s.getItem();
    //            return egg.getColor(i);
    //        } else return -1;
    //    }, CRAZED_SPAWN_EGG);
    //
    //}

    @Environment(EnvType.CLIENT)
    public static void clientSetup() {
        MinecraftClient mc = MinecraftClient.getInstance();
        EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();

        EntityRendererRegistry.INSTANCE.register(CRAZED, (d, $) -> new CrazedRender(d));
        EntityRendererRegistry.INSTANCE.register(CRAZED_FLAME, (d, $) -> new CrazedFlameRenderer(d));

        Stream.of(BRAZIER, LIVING_TORCH_BLOCK, LIVING_TORCH_BLOCK_WALL)
                .forEach(b -> BlockRenderLayerMapImpl.INSTANCE.putBlock(b, RenderLayer.getCutout()));

        BlockEntityRendererRegistry.INSTANCE.register(BRAZIER_TILE, BrazierRenderer::new);

        BlockRenderLayerMapImpl.INSTANCE.putBlock(SPAWN_POWDER, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(LIVING_LANTERN, RenderLayer.getCutout());
    }
}