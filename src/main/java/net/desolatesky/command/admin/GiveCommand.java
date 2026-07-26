package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public final class GiveCommand extends Command {

    private final ItemFactory itemFactory;
    private final ArgumentString itemIdArg = ArgumentType.String("item");
    private final ArgumentNumber<Integer> itemAmountArg = ArgumentType.Integer("amount").max(64);
    private final ArgumentEntity playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true);

    public GiveCommand(ItemFactory itemFactory) {
        super("give");
        this.itemFactory = itemFactory;

        this.setCondition(this::hasPermission);

        this.itemIdArg.setSuggestionCallback((sender, context, suggestion) -> {
            final String arg = context.getOrDefault(this.itemIdArg, "");
            this.itemFactory.getALlItemIds().stream().map(Key::asString)
                    .filter(s -> s.contains(arg) || arg.isBlank())
                    .sorted()
                    .forEach(id -> suggestion.addEntry(new SuggestionEntry(id, Component.text("Test"))));
        });

        this.addSyntax(this::onGive, this.playerArg, this.itemIdArg);
        this.addSyntax(this::onGive, this.playerArg, this.itemIdArg, this.itemAmountArg);
        this.addSyntax(this::onGive, this.itemIdArg, this.itemAmountArg);
        this.addSyntax(this::onGive, this.itemIdArg);
    }

    private boolean hasPermission(@Nullable CommandSender sender, @Nullable String unused) {
        return sender instanceof ConsoleCommandHandler || (sender instanceof final DSPlayer player && player.hasPermission(Permission.CMD_GIVE));
    }

    private void onGive(CommandSender sender, CommandContext context) {
        final String itemId = context.getOrDefault(this.itemIdArg, "");
        final Key itemKey = Key.key(itemId);
        final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(itemKey);
        if (itemDefinition == null) {
            return;
        }
        Entity entity = null;
        if (context.has(this.playerArg)) {
            entity = context.get(this.playerArg).findFirstEntity(null, null);
        }
        if (entity == null) {
            if (!(sender instanceof final DSPlayer player)) {
                throw new IllegalArgumentException();
            }
            entity = player;
        }
        final DSPlayer player = (DSPlayer) entity;
        final int amount = context.getOrDefault(this.itemAmountArg, 1);
        player.getInventory().addItemStack(itemDefinition.defaultItemStack().withAmount(amount));
    }
}
