package net.desolatesky.player;

import net.desolatesky.crafting.CraftingMenuHolder;
import net.desolatesky.data.DataHolder;
import net.desolatesky.lock.Lockable;
import net.desolatesky.permission.Permission;
import net.desolatesky.recipe.type.ShapedRecipe;
import net.desolatesky.server.DSServer;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.pos.WorldPosition;
import net.kyori.adventure.key.Key;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class DSPlayer extends net.minestom.server.entity.Player implements Lockable, DataHolder<DSPlayer>, CraftingMenuHolder {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final DSServer server;
    private final boolean newPlayer;
    private @Nullable UUID islandId;
    private @UnknownNullability User user;
    private boolean creatingIsland = false;

    private @Nullable WorldPosition logoutPos;

    private boolean teleporting = false;
    private @Nullable ShapedRecipe.Result currentOutputResult;
    private @Nullable Key currentRecipeId;

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
        if (this.user == null) {
            this.user = LuckPermsProvider.get().getUserManager().getUser(this.getUuid());
        }
        if (this.user == null) {
            return false;
        }
        return this.user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
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

    public void setIslandId(UUID islandId) {
        this.lockWrite(() -> this.islandId = islandId);
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
        this.lockWrite(() -> this.currentOutputResult = currentOutputResult);
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
    public ReadWriteLock lock() {
        return this.lock;
    }
}

