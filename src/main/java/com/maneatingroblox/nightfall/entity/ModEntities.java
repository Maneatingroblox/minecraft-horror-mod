package com.maneatingroblox.nightfall.entity;

import com.maneatingroblox.nightfall.Nightfall;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Nightfall.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Nightfall.MOD_ID);

    public static final RegistryObject<EntityType<TheHollowEntity>> THE_HOLLOW = ENTITY_TYPES.register("the_hollow",
            () -> EntityType.Builder.<TheHollowEntity>of(TheHollowEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.05F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build(Nightfall.MOD_ID + ":the_hollow"));

    private ModEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(THE_HOLLOW.get(), TheHollowEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacement(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> SpawnPlacements.register(
                THE_HOLLOW.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TheHollowEntity::checkSpawnRules));
    }
}
