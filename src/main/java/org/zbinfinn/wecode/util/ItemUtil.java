package org.zbinfinn.wecode.util;

import dev.dfonline.flint.Flint;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class ItemUtil {

    public static NbtCompound getItemTags(ItemStack item) {
        NbtComponent data = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = data.copyNbt();

        return nbt.getCompound("PublicBukkitValues");
    }

    public static String getNBT(ItemStack item) {
        return item.toNbt(Flint.getClient().world.getRegistryManager()).asString();
    }

    public static ItemStack setNBT(String nbt) {
        try {
            return ItemStack.fromNbt(Flint.getClient().world.getRegistryManager(), StringNbtReader.parse(nbt)).get();
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }
}
