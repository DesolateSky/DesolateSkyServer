package net.desolatesky.command.admin;

import net.desolatesky.command.DSCommand;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public final class GiveCommand extends DSCommand {

    public static final String PERMISSION = "desolatesky.command.give";

    public GiveCommand(DSItemRegistry itemRegistry) {
        super(PERMISSION, "give");

        final ArgumentEntity playerArgument = new ArgumentEntity("player")
                .onlyPlayers(true)
                .singleEntity(true);
        final Argument<String> argument = ArgumentType.ResourceLocation("item").setSuggestionCallback((_, context, callback) -> {
            final String itemName = context.get("item");
            itemRegistry.getItems().values()
                    .stream()
                    .map(DSItem::key)
                    .filter(name -> name.value().toLowerCase().startsWith(itemName.toLowerCase()) || name.asString().toLowerCase().contains(itemName.toLowerCase()))
                    .map(Key::asString)
                    .map(SuggestionEntry::new)
                    .forEach(callback::addEntry);
        });
        final Argument<Integer> amountArgument = ArgumentType.Integer("amount").setDefaultValue(1);

        this.addSyntax((sender, context) -> {
            final Player targetPlayer = context.get(playerArgument).findFirstPlayer(sender);
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
        return (sender, _) -> sender instanceof DSPlayer;
    }

}