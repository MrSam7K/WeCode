package org.zbinfinn.wecode.features.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.FlintAPI;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.util.FileUtil;
import org.zbinfinn.wecode.util.ItemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SaveLoadInvCommand /*implements CommandFeature*/ {
//    //private ArrayList<ItemStack> items;
//
//    @Override
//    public String commandName() {
//        return "inventory";
//    }
//
//    @Override
//    public Set<String> aliases() {
//        return Set.of(
//                "inv"
//        );
//    }
//
//    @Override
//    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
//        return builder
//                .then(literal("save").executes(this::save))
//                .then(literal("load").executes(this::load));
//    }
//
//    private int load(CommandContext<FabricClientCommandSource> context) {
//        if (!WeCode.MC.player.isInCreativeMode()) {
//            NotificationHelper.sendFailNotification("You have to be in creative mode to load inventories", 3);
//            return 0;
//        }
//
//        JsonObject data = FileUtil.loadJSON("saved_inventories.json");
//        JsonArray array = data.get("inv1").getAsJsonArray();
//
//        ArrayList<ItemStack> items = new ArrayList<>();
//
//        array.forEach(el -> {
//            items.add(el.getAsString().isEmpty() ? ItemStack.EMPTY : ItemUtil.setNBT(el.getAsString()));
//        });
//
//        for (int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
//            WeCode.MC.player.getInventory().setStack(i, items.get(i).copy());
//        }
//        NotificationHelper.sendAppliedNotification("Inventory Loaded", 3);
//        return 0;
//    }
//
//    private int save(CommandContext<FabricClientCommandSource> context) {
//        try {
//
//
//            JsonArray array = new JsonArray();
//            items.forEach(item -> array.add(item.isEmpty() ? "" : ItemUtil.getNBT(item)));
//
//            JsonObject data = new JsonObject();
//            data.add("inv1", array);
//
//            FileUtil.saveJSON("saved_inventories.json", data);
//            NotificationHelper.sendAppliedNotification("Inventory Saved", 3);
//        } catch(IOException e) {
//            e.printStackTrace();
//            NotificationHelper.sendFailNotification("Failed to save inventory, more info in console", 5);
//        }
//        return 0;
//    }
}
