package com.maneatingroblox.nightfall;

import com.maneatingroblox.nightfall.entity.ModEntities;
import com.maneatingroblox.nightfall.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(Nightfall.MOD_ID)
public final class Nightfall {
    public static final String MOD_ID = "nightfall";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> NIGHTFALL_TAB = CREATIVE_TABS.register("nightfall",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup.nightfall"))
                    .icon(() -> ModItems.ECHO_BELL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ECHO_BELL.get());
                        output.accept(ModItems.HOLLOW_SPAWN_EGG.get());
                        output.accept(ModItems.FIELD_NOTES.get());
                    })
                    .build());

    public Nightfall(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModEntities.register(modBus);
        ModItems.register(modBus);
        CREATIVE_TABS.register(modBus);

        context.registerConfig(ModConfig.Type.COMMON, NightfallConfig.SPEC);

        LOGGER.info("Nightfall is listening.");
    }
}
