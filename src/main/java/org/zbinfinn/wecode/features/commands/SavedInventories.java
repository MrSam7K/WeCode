package org.zbinfinn.wecode.features.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.core.FeatureTrait;
import dev.dfonline.flint.feature.trait.ModeSwitchListeningFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.hypercube.Mode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.InstancedItem;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.util.FileUtil;
import org.zbinfinn.wecode.util.ItemUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SavedInventories implements FeatureTrait, ClientCommandRegistrationCallback, TickedFeature, ModeSwitchListeningFeature {

    private HashMap<String, ArrayList<ItemStack>> inventories = new HashMap<>();
    private boolean loaded = false;
    private static ArrayList<String> instancedItemsTranslations = new ArrayList<>();
    private static HashMap<InstancedItem, ItemStack> instancedItems = new HashMap<>();
    private static ArrayList<InstancedItem> instancedItemQueue = new ArrayList<>();

    public SavedInventories() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }

    private void loadAll() {
        loaded = true;
        instancedItemsTranslations = InstancedItem.getTranslations();

        JsonObject data = FileUtil.loadJSON("saved_inventories.json");
        if(data.isEmpty() || !data.has("inventories")) {
            inventories = new HashMap<>();
            save();
        } else {
            JsonObject inventoriesJson = data.getAsJsonObject("inventories");
            for(String key : inventoriesJson.keySet()) {
                ArrayList<ItemStack> items = new ArrayList<>();

                inventoriesJson.get(key).getAsJsonArray().forEach(el -> {
                    items.add(el.getAsString().isEmpty() ? ItemStack.EMPTY : ItemUtil.setNBT(el.getAsString()));
                });

                inventories.put(key, items);
            }
        }
    }

    public void save() {
        try {
            JsonObject data = new JsonObject();

            JsonObject inventoriesJson = new JsonObject();

            for (String key : inventories.keySet()) {
                JsonArray array = new JsonArray();
                inventories.get(key).forEach(item -> {
                    if(item == null || item.isEmpty()) {
                        array.add("");
                    } else if(InstancedItem.isInstancedItem(item)) {
                        array.add("instance:" + InstancedItem.getKey(item.getItem()).name());
                    } else
                        array.add(ItemUtil.getNBT(item));
                });

                inventoriesJson.add(key, array);
            }

            data.add("inventories", inventoriesJson);
            FileUtil.saveJSON("saved_inventories.json", data);
        } catch(IOException e) {
            WeCode.LOGGER.error("Failed to sanity-save saved inventories");
        }
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("inv")
                        .then(literal("load")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests(((commandContext, suggestionsBuilder) -> getSuggestions(suggestionsBuilder)))
                                        .executes(this::load)))
                        .then(literal("save")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests(((commandContext, suggestionsBuilder) -> getSuggestions(suggestionsBuilder)))
                                        .executes(this::save)))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests(((commandContext, suggestionsBuilder) -> getSuggestions(suggestionsBuilder)))
                                        .executes(this::remove)))
        );
    }

    private CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
        for (String key : inventories.keySet()) {
            if (key.startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(key);
            }
        }
        return builder.buildFuture();
    }

    private int remove(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");

        if(!inventories.containsKey(name)) {
            NotificationHelper.sendFailNotification(name + " does not exist!!", 5);
            return 0;
        }

        inventories.remove(name);
        NotificationHelper.sendAppliedNotification("Removed " + name + " inventory layout", 5);

        return 0;
    }

    private int load(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");

        if(!inventories.containsKey(name)) {
            NotificationHelper.sendFailNotification(name + " does not exist!!", 5);
            return 0;
        }

        if(!WeCode.MC.player.isInCreativeMode()) {
            NotificationHelper.sendFailNotification("You have to be in creative mode to load inventories", 3);
            return 0;
        }

        ArrayList<ItemStack> items = inventories.get(name);
        for(int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
            WeCode.MC.player.getInventory().setStack(i, items.get(i));
        }

        NotificationHelper.sendAppliedNotification("Inventory Loaded", 3);
        return 0;
    }

    private int save(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");

        ArrayList<ItemStack> items = new ArrayList<>(WeCode.MC.player.getInventory().size());

        for(int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
            items.add(WeCode.MC.player.getInventory().getStack(i).copy());
        }

        if(!inventories.containsKey(name))
            NotificationHelper.sendAppliedNotification("Saved new inventory layout as " + name, 5);
        else
            NotificationHelper.sendAppliedNotification("Replaced " + name + " inventory layout", 5);

        inventories.put(name, items);

        save();

        return 0;
    }

    @Override
    public void tick() {
        if(Flint.getClient().world == null) return;

        if(!loaded) loadAll();
    }

    public static boolean setStack(int slot, ItemStack stack) {
        if(InstancedItem.isInstancedItem(stack)) {
                InstancedItem instancedItem = InstancedItem.getKey(stack.getItem());
                instancedItems.put(instancedItem, stack);
                System.out.println("Put " + stack.getItem().getTranslationKey() + " as " + InstancedItem.getKey(stack.getItem()));

                if(instancedItemQueue.contains(instancedItem)) {
                    System.out.println("removed");
                    instancedItemQueue.remove(instancedItem);
                    WeCode.MC.player.getInventory().setStack(slot, ItemStack.EMPTY);
                }

                return true;
        }
        return false;
    }

    @Override
    public void onSwitchMode(Mode mode, Mode mode1) {
        instancedItemQueue.addAll(Arrays.stream(InstancedItem.values()).toList());

        if(mode.isEditor()) {
            if(InstancedItem.getKey(WeCode.MC.player.getInventory().main.get(8).getItem()) == null) {
                CommandSender.queue("rc");
            } else {
                instancedItemQueue.removeAll(List.of(InstancedItem.CODEBLOCKS, InstancedItem.VALUES, InstancedItem.REFERENCE_BOOK));
            }

            CommandSender.queue("p g");
            CommandSender.queue("bracket");
        }
    }
}
