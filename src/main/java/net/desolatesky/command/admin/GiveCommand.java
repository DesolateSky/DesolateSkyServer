package net.desolatesky.command.admin;

import net.desolatesky.command.DSCommand;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentResourceLocation;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public final class GiveCommand extends DSCommand {

    public static final String PERMISSION = "desolatesky.command.give";

    public GiveCommand(DSItemRegistry itemRegistry) {
        super(PERMISSION, "give");

        final Argument<String> playerArgument = ArgumentType.String("player").setSuggestionCallback((sender, context, callback) -> {
            final String playerName = context.get("player");
            MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                    .map(Player::getUsername)
                    .filter(name -> name.toLowerCase().startsWith(playerName.toLowerCase()))
                    .map(SuggestionEntry::new)
                    .forEach(callback::addEntry);
        });
        final Argument<String> argument = ArgumentType.ResourceLocation("item").setSuggestionCallback((sender, context, callback) -> {
            final String itemName = context.get("item");
            itemRegistry.getItems().values()
                    .stream()
                    .map(item -> item.key().asString())
                    .filter(name -> name.toLowerCase().startsWith(itemName.toLowerCase()))
                    .map(item -> new SuggestionEntry(item))
                    .forEach(callback::addEntry);
        });
        final Argument<Integer> amountArgument = ArgumentType.Integer("amount").setDefaultValue(1);

        this.addSyntax((sender, context) -> {
            final Player targetPlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(context.get(playerArgument));
            if (targetPlayer == null) {
                sender.sendMessage(Component.text("Player not found: " + context.get(playerArgument)).color(NamedTextColor.RED));
                return;
            }
            final String itemName = context.get(argument);
            final Key key = Key.key(itemName);
            final ItemStack itemStack = itemRegistry.create(key);
            if (itemStack == null) {
                sender.sendMessage(Component.text("Item not found: " + itemName).color(NamedTextColor.RED));
                return;
            }
            final int amount = context.get(amountArgument);
            targetPlayer.getInventory().addItemStack(itemStack.withAmount(amount));
        }, playerArgument, argument, amountArgument);
    }

    @Override
    public CommandCondition getCondition() {
        return (sender, context) -> sender instanceof DSPlayer;
    }

}