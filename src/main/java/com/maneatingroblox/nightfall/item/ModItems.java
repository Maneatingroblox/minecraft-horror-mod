package com.maneatingroblox.nightfall.item;

import com.maneatingroblox.nightfall.Nightfall;
import com.maneatingroblox.nightfall.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nightfall.MOD_ID);

    public static final RegistryObject<Item> ECHO_BELL = ITEMS.register("echo_bell",
            () -> new EchoBellItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HOLLOW_SPAWN_EGG = ITEMS.register("the_hollow_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.THE_HOLLOW, 0x17151B, 0xA52B3B, new Item.Properties()));

    public static final RegistryObject<Item> FIELD_NOTES = ITEMS.register("field_notes",
            () -> new FieldNotesItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
