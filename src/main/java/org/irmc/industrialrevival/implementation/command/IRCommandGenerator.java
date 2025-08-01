package org.irmc.industrialrevival.implementation.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.irmc.industrialrevival.api.elements.compounds.ChemicalCompound;
import org.irmc.industrialrevival.api.items.IndustrialRevivalItem;
import org.irmc.industrialrevival.api.items.attributes.CompoundContainerHolder;
import org.irmc.industrialrevival.api.timings.TimingViewRequest;
import org.irmc.industrialrevival.core.guide.impl.CheatGuide;
import org.irmc.industrialrevival.core.guide.impl.SurvivalGuide;
import org.irmc.industrialrevival.dock.IRDock;
import org.irmc.industrialrevival.implementation.IndustrialRevival;
import org.irmc.industrialrevival.utils.Constants;
import org.irmc.industrialrevival.utils.DataUtil;
import org.irmc.industrialrevival.utils.ItemUtils;
import org.irmc.industrialrevival.api.language.MessageReplacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IRCommandGenerator {
    private static CommandAPICommand instance;

    public static void registerCommand(IndustrialRevival plugin) {
        instance = new CommandAPICommand("industrialrevival")
                .withAliases("ir")
                .withArguments(new TextArgument("subCommand"))
                .executes(info -> {
                    CommandSender sender = info.sender();
                    sendHelpMessage(sender);
                })
                .withSubcommand(new CommandAPICommand("help")
                        .executes(executionInfo -> {
                            CommandSender sender = executionInfo.sender();
                            sendHelpMessage(sender);
                        }))
                .withSubcommand(new CommandAPICommand("guide")
                        .withPermission(Constants.Permissions.COMMAND_GUIDE)
                        .executesPlayer(executionInfo -> {
                            Player player = executionInfo.sender();
                            player.getInventory().addItem(Constants.ItemStacks.GUIDE_BOOK_ITEM.clone());
                        }))
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission(Constants.Permissions.COMMAND_RELOAD)
                        .executes(executionInfo -> {
                            IRDock.getPlugin().reloadConfig();
                            IRDock
                                    .getLanguageManager()
                                    .sendMessage(executionInfo.sender(), "command.reload");
                        }))
                .withSubcommand(new CommandAPICommand("info")
                        .withPermission(Constants.Permissions.COMMAND_INFO)
                        .executes(executionInfo -> {
                            CommandSender sender = executionInfo.sender();
                            sendInfoMessage(sender);
                        }))
                .withSubcommand(new CommandAPICommand("cheat")
                        .withPermission(Constants.Permissions.COMMAND_CHEAT)
                        .executesPlayer(executionInfo -> {
                            Player player = executionInfo.sender();
                            CheatGuide.instance().open(player);
                        }))
                .withSubcommand(new CommandAPICommand("give")
                        .withPermission(Constants.Permissions.COMMAND_GIVE)
                        .withArguments(new PlayerArgument("target"))
                        .withArguments(new NamespacedKeyArgument("id")
                                .replaceSuggestions(ArgumentSuggestions.stringsAsync(
                                        _ -> CompletableFuture.supplyAsync(() -> IRDock
                                                .getRegistry()
                                                .getItems()
                                                .keySet()
                                                .stream().map(NamespacedKey::toString)
                                                .toArray(String[]::new)))))
                        .withOptionalArguments(new IntegerArgument("amount"))
                        .executes((sender, args) -> {
                            Player target = (Player) args.get("target");
                            NamespacedKey itemID = (NamespacedKey) args.get("id");
                            Integer amount = (Integer) args.get("amount");

                            int finalAmount = amount == null ? 1 : amount;
                            IndustrialRevivalItem item = IRDock
                                    .getRegistry()
                                    .getItems()
                                    .get(itemID);

                            if (target == null) {
                                sender.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(sender, "command.give.target_not_found"));
                                return;
                            }

                            if (item == null) {
                                sender.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(sender, "command.give.item_not_found"));
                                return;
                            }

                            ItemStack itemStack = item.getIcon().clone();
                            itemStack.setAmount(finalAmount);
                            IRDock.getItemDataService().setId(itemStack, itemID);
                            target.getInventory().addItem(itemStack);

                            MessageReplacement itemName = MessageReplacement.replace(
                                    "%item%",
                                    MiniMessage.miniMessage()
                                            .serialize(ItemUtils.getDisplayName(itemStack)));
                            MessageReplacement itemAmount =
                                    MessageReplacement.replace("%amount%", String.valueOf(finalAmount));

                            target.sendMessage(IRDock
                                    .getLanguageManager()
                                    .getMsgComponent(sender, "command.give.success", itemName, itemAmount));
                        }))
                .withSubcommand(new CommandAPICommand("giveSilently")
                        .withPermission(Constants.Permissions.COMMAND_GIVE)
                        .withArguments(new PlayerArgument("target"))
                        .withArguments(new NamespacedKeyArgument("id")
                                .replaceSuggestions(ArgumentSuggestions.stringsAsync(
                                        _ -> CompletableFuture.supplyAsync(() -> IRDock
                                                .getRegistry()
                                                .getItems()
                                                .keySet()
                                                .stream().map(NamespacedKey::toString)
                                                .toArray(String[]::new)))))
                        .withOptionalArguments(new IntegerArgument("amount"))
                        .executes((sender, args) -> {
                            Player target = (Player) args.get("target");
                            NamespacedKey itemID = (NamespacedKey) args.get("id");
                            Integer amount = (Integer) args.getOrDefault("amount", 1);

                            IndustrialRevivalItem item = IRDock
                                    .getRegistry()
                                    .getItems()
                                    .get(itemID);

                            if (target == null) {
                                sender.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(sender, "command.give.target_not_found"));
                                return;
                            }

                            if (item == null) {
                                sender.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(sender, "command.give.item_not_found"));
                                return;
                            }

                            ItemStack itemStack = item.getIcon().clone();
                            IRDock.getItemDataService().setId(itemStack, itemID);
                            itemStack.setAmount(amount);
                            target.getInventory().addItem(itemStack);
                        }))
                .withSubcommand(new CommandAPICommand("timings")
                        .withPermission(Constants.Permissions.COMMAND_TIMINGS)
                        .executes((sender, args) -> {
                            TimingViewRequest request = new TimingViewRequest(sender, true);
                            IRDock.getRunningProfilerService().requestTimingView(request);
                            sender.sendMessage(IRDock
                                    .getLanguageManager()
                                    .getMsgComponent(sender, "command.timings.waiting"));
                        }))
                .withSubcommand(new CommandAPICommand("chemistry")
                        .withPermission(Constants.Permissions.COMMAND_CHEMISTRY)
                        .withArguments(new StringArgument("object").replaceSuggestions(ArgumentSuggestions.stringsAsync(
                                _ -> CompletableFuture.supplyAsync(() -> {
                                        var lst = new ArrayList<>(IRDock
                                                .getRegistry()
                                                .getChemicalCompounds().values().stream()
                                                .distinct()
                                                .map(ChemicalCompound::getName)
                                                .sorted()
                                                .toList());

                                        lst.add("ALL");
                                        return lst.toArray(new String[0]);
                                }))))
                        .withArguments(new DoubleArgument("mass"))
                        .executesPlayer((player, args) -> {
                            double mass = (double) args.getOrDefault("mass", 1D);
                            var b = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
                            if (b == null) {
                                player.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(player, "command.chemistry.no_target_block"));
                                return;
                            }

                            var loc = b.getLocation();
                            var ir = DataUtil.getItem(loc);
                            if (!(ir instanceof CompoundContainerHolder holder)) {
                                player.sendMessage(IRDock
                                        .getLanguageManager()
                                        .getMsgComponent(player, "command.chemistry.no_item_block"));
                                return;
                            }

                            String object = (String) args.get("object");
                            if ("ALL".equals(object)) {
                                Map<ChemicalCompound, Double> masses = new HashMap<>();
                                for (var compound : IRDock.getRegistry().getChemicalCompounds().values()) {
                                    masses.put(compound, mass);
                                }

                                holder.mixCompounds(loc, masses);
                            } else {
                                var compound = ChemicalCompound.forName(object);
                                if (compound == null) {
                                    player.sendMessage(IRDock
                                            .getLanguageManager()
                                            .getMsgComponent(player, "command.chemistry.compound_not_found"));
                                    return;
                                }

                                holder.mixCompounds(loc, compound, mass);
                            }
                        }))
                .withSubcommand(new CommandAPICommand("test")
                        .withPermission(Constants.Permissions.COMMAND_INFO)
                        .executesPlayer((player, args) -> {
                            SurvivalGuide.instance().open(player);
                        }));

        instance.register(plugin);
    }

    private static void sendHelpMessage(CommandSender sender) {
        List<CommandAPICommand> subcommands = instance.getSubcommands();
        Component msg =
                IRDock.getLanguageManager().getMsgComponent(sender, "command.help.header");
        for (CommandAPICommand subcommand : subcommands) {
            String commandName = subcommand.getName();
            msg = msg.append(Component.newline());
            msg = msg.append(IRDock
                    .getLanguageManager()
                    .getMsgComponent(sender, "command.help." + commandName));
        }
        sender.sendMessage(msg);
    }

    private static void sendInfoMessage(CommandSender sender) {
        Component msg =
                IRDock.getLanguageManager().getMsgComponent(sender, "command.info.header");
        msg = msg.append(Component.newline());

        MessageReplacement ver = MessageReplacement.replace(
                "%version%", IRDock.getVersion());

        msg = msg.append(IRDock
                .getLanguageManager()
                .getMsgComponent(sender, "command.info.version", ver));

        msg = msg.append(Component.newline());

        MessageReplacement serverVer = MessageReplacement.replace("%server_version%", Bukkit.getVersion());

        msg = msg.append(IRDock
                .getLanguageManager()
                .getMsgComponent(sender, "command.info.server_version", serverVer));

        for (Plugin addon : IRDock.getAddons()) {
            MessageReplacement name = MessageReplacement.replace("%addon_name%", addon.getName());
            MessageReplacement version = MessageReplacement.replace(
                    "%addon_version%", addon.getPluginMeta().getVersion());
            msg = msg.append(Component.newline());
            msg = msg.append(IRDock
                    .getLanguageManager()
                    .getMsgComponent(sender, "command.info.addon_item", name, version));
        }

        sender.sendMessage(msg);
    }
}
