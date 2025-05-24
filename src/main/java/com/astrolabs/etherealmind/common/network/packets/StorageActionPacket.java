package com.astrolabs.etherealmind.common.network.packets;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.astrolabs.etherealmind.common.menu.DimensionalStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class StorageActionPacket {
    public enum Action {
        CHANGE_PAGE,
        SET_CATEGORY,
        SEARCH,
        QUICK_ACCESS_HOTKEY,
        MOVE_TO_QUICK_ACCESS,
        SYNC_SLOT
    }
    
    private final Action action;
    private final int value1;
    private final int value2;
    private final int value3;
    private final String stringValue;
    
    // Constructor for numeric actions
    public StorageActionPacket(Action action, int value1, int value2, int value3) {
        this(action, value1, value2, value3, "");
    }
    
    // Constructor for string actions
    public StorageActionPacket(Action action, int value1, int value2, int value3, String stringValue) {
        this.action = action;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.stringValue = stringValue;
    }
    
    public static void encode(StorageActionPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.action);
        buf.writeVarInt(packet.value1);
        buf.writeVarInt(packet.value2);
        buf.writeVarInt(packet.value3);
        buf.writeUtf(packet.stringValue);
    }
    
    public static StorageActionPacket decode(FriendlyByteBuf buf) {
        Action action = buf.readEnum(Action.class);
        int value1 = buf.readVarInt();
        int value2 = buf.readVarInt();
        int value3 = buf.readVarInt();
        String stringValue = buf.readUtf();
        return new StorageActionPacket(action, value1, value2, value3, stringValue);
    }
    
    public static void handle(StorageActionPacket packet, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            
            // Check if player has storage menu open
            if (!(player.containerMenu instanceof DimensionalStorageMenu menu)) {
                return;
            }
            
            CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
            if (cosmo == null) return;
            
            switch (packet.action) {
                case CHANGE_PAGE:
                    handlePageChange(menu, packet.value1);
                    break;
                    
                case SET_CATEGORY:
                    handleCategoryChange(menu, cosmo, packet.stringValue);
                    break;
                    
                case SEARCH:
                    handleSearch(menu, cosmo, packet.stringValue);
                    break;
                    
                case QUICK_ACCESS_HOTKEY:
                    handleQuickAccessHotkey(player, menu, packet.value1);
                    break;
                    
                case MOVE_TO_QUICK_ACCESS:
                    handleMoveToQuickAccess(menu, packet.value1, packet.value2);
                    break;
                    
                case SYNC_SLOT:
                    // Used for syncing specific slot updates
                    break;
            }
        });
        ctx.setPacketHandled(true);
    }
    
    private static void handlePageChange(DimensionalStorageMenu menu, int newPage) {
        menu.setPage(newPage);
        // The container will automatically sync slots to client
    }
    
    private static void handleCategoryChange(DimensionalStorageMenu menu, CosmoEntity cosmo, String category) {
        // TODO: Implement category filtering
        // This would involve filtering the visible items based on category
        // For now, just store the category preference
    }
    
    private static void handleSearch(DimensionalStorageMenu menu, CosmoEntity cosmo, String searchQuery) {
        // TODO: Implement search functionality
        // This would filter visible items based on the search query
        // For now, the search is visual only on the client
    }
    
    private static void handleQuickAccessHotkey(ServerPlayer player, DimensionalStorageMenu menu, int slot) {
        // Handle quick access slot activation
        if (slot >= 0 && slot < 9) {
            // TODO: Implement quick item retrieval from quick access bar
            // This could swap the item with the player's current held item
        }
    }
    
    private static void handleMoveToQuickAccess(DimensionalStorageMenu menu, int fromSlot, int toQuickSlot) {
        // Move item from storage to quick access
        if (toQuickSlot >= 0 && toQuickSlot < 9) {
            // TODO: Implement moving items to quick access
        }
    }
}