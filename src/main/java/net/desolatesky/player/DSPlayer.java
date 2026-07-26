package net.desolatesky.player;

import net.desolatesky.cooldown.DurationCooldown;
import net.desolatesky.crafting.CraftingMenuHolder;
import net.desolatesky.data.DataHolder;
import net.desolatesky.lock.Lockable;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.permission.Permission;
import net.desolatesky.recipe.type.ShapedRecipe;
import net.desolatesky.server.DSServer;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.pos.WorldPosition;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@NotNullByDefault
public final class DSPlayer extends net.minestom.server.entity.Player implements Lockable, DataHolder<DSPlayer>, CraftingMenuHolder {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final DSServer server;
    private final boolean newPlayer;
    private @Nullable UUID islandId;
    private @UnknownNullability User user;
    private boolean creatingIsland = false;

    private boolean afk = false;

    private @Nullable WorldPosition logoutPos;

    private boolean teleporting = false;
    private @Nullable ShapedRecipe.Result currentOutputResult;
    private @Nullable Key currentRecipeId;

    // todo make an actual cooldown system, this is just to prevent people spam creating islands
    private @Nullable DurationCooldown islandCreateCooldown;

    public DSPlayer(
            PlayerConnection playerConnection,
            GameProfile gameProfile,
            DSServer server,
            @Nullable DSPlayerData playerData
    ) {
        super(playerConnection, gameProfile);
        this.server = server;
        this.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0);
        if (playerData != null) {
            this.newPlayer = false;
            this.islandId = playerData.islandId();
            final List<ItemStack> items = playerData.inventory();
            int slot = 0;
            for (final ItemStack itemStack : items) {
                this.inventory.setItemStack(slot, itemStack);
                slot++;
            }
            this.logoutPos = playerData.logoutPos();
        } else {
            this.newPlayer = true;
        }
    }

    public boolean hasPermission(String permission) {
        final User user = this.getUser();
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    private @UnknownNullability User getUser() {
        if (this.user == null) {
            this.user = LuckPermsProvider.get().getUserManager().getUser(this.getUuid());
        }
        return this.user;
    }

    @Override
    public Component getDisplayName() {
        final Component display = super.getDisplayName();
        if (display == null) {
            return this.updateDisplayName();
        }
        return display;
    }

    public Component updateDisplayName() {
        final String prefixString = this.getUser().getCachedData().getMetaData().getPrefix();
        Component display = Component.text(this.getUsername());
        if (prefixString != null) {
            display = ComponentUtil.parse(prefixString)
                    .appendSpace()
                    .append(display);
        }
        if (this.afk) {
            display = Component.empty()
                    .append(Component.text("[AFK] ")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD))
                    .append(display);
        }
        super.setDisplayName(display);
        return display;
    }

    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.path());
    }

    public @Nullable UUID getIslandId() {
        return this.lockRead(() -> this.islandId);
    }

    public boolean hasIsland() {
        return this.lockRead(() -> this.islandId != null);
    }

    public void setIslandId(@Nullable UUID islandId) {
        this.lockWrite(() -> {
            this.islandId = islandId;
            if (this.islandId == null) {
                return;
            }
            this.islandCreateCooldown = new DurationCooldown(Instant.now(), Duration.ofMinutes(10));
        });
    }

    public @Nullable DurationCooldown getIslandCreateCooldown() {
        return this.lockRead(() -> {
            if (this.islandCreateCooldown != null && this.islandCreateCooldown.isComplete()) {
                this.islandCreateCooldown = null;
            }
            return this.islandCreateCooldown;
        });
    }

    public void setCreatingIsland(boolean creatingIsland) {
        this.creatingIsland = creatingIsland;
    }

    public boolean isCreatingIsland() {
        return this.creatingIsland;
    }

    public DSPlayerData createSnapshot() {
        return this.lockRead(() -> new DSPlayerData(this.getUuid(),
                this.islandId,
                Arrays.asList(this.getInventory().getItemStacks()),
                this.logoutPos
        ));
    }

    @Override
    public void setCurrentOutputResult(@Nullable ShapedRecipe.Result currentOutputResult) {
        this.lockWrite(() -> {
            this.currentOutputResult = currentOutputResult;
        });
    }

    @Override
    public @Nullable ShapedRecipe.Result currentOutputResult() {
        return this.lockRead(() -> this.currentOutputResult);
    }

    @Override
    public void setCurrentRecipeId(@Nullable Key currentRecipeId) {
        this.lockWrite(() -> this.currentRecipeId = currentRecipeId);
    }

    @Override
    public @Nullable Key currentRecipeId() {
        return this.lockRead(() -> this.currentRecipeId);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        this.inventory.setItemStack(slot, itemStack);
    }

    @Override
    public ItemStack getItemStack(int slot) {
        return this.inventory.getItemStack(slot);
    }

    public boolean newPlayer() {
        return this.newPlayer;
    }

    public @Nullable WorldPosition getLogoutPos() {
        return this.lockRead(() -> this.logoutPos);
    }

    public void setLogoutPos(@Nullable WorldPosition logoutPos) {
        this.lockWrite(() -> this.logoutPos = logoutPos);
    }

    public @Nullable WorldPosition getWorldPosition() {
        if (!(this.getInstance() instanceof final DSWorld world)) {
            return null;
        }
        return new WorldPosition(this.getIslandId(), world.getUuid(), this.getPosition(), world.worldType());
    }

    public boolean isTeleporting() {
        return this.lockRead(() -> this.teleporting);
    }

    public void setTeleporting(boolean teleporting) {
        this.lockWrite(() -> this.teleporting = teleporting);
    }

    public DSServer server() {
        return this.server;
    }

    @Override
    public void kick(Component message) {
        super.kick(message);
        DSLogger.getLogger().info(this.getUsername() + " was kicked with reason: " + ComponentUtil.serialize(message));
    }

    @Override
    public void kick(String message) {
        super.kick(message);
        DSLogger.getLogger().info(this.getUsername() + " was kicked with reason: " + message);
    }

    public void toggleAfkStatus() {
        this.afk = !this.afk;
        this.updateDisplayName();
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}

