package org.zbinfinn.wecode;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zbinfinn.wecode.util.ItemUtil;

import java.util.ArrayList;
import java.util.Arrays;

public enum InstancedItem {
    CODEBLOCKS(Items.DIAMOND),
    VALUES(Items.IRON_INGOT),
    GLITCH_STICK(Items.STICK),
    REFERENCE_BOOK(Items.WRITTEN_BOOK),
    BRACKET_FINDER(Items.BLAZE_POWDER);

    public final Item item;

    InstancedItem(Item item) {
        this.item = item;
    }

    public static ArrayList<String> getTranslations() {
        ArrayList<String> items = new ArrayList<>();
        Arrays.stream(InstancedItem.values()).toList().forEach(item -> items.add(item.item.getTranslationKey()));
        return items;
    }

    public static InstancedItem getKey(Item item) {
        for (InstancedItem instancedItem : InstancedItem.values()) {
            if (instancedItem.item == item) {
                return instancedItem;
            }
        }
        return null;
    }

    public static boolean isInstancedItem(ItemStack item) {
        if(getTranslations().contains(item.getItem().getTranslationKey())) {
            return ItemUtil.getItemTags(item).contains("hypercube:item_instance");
        }

        return false;
    }
}
