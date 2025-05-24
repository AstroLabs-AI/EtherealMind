package com.astrolabs.etherealmind.common.command;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CosmoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("cosmo")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("level")
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
                        
                        if (cosmo != null) {
                            int level = cosmo.getLevel();
                            float progress = cosmo.getLevelSystem().getXpProgress();
                            int xp = cosmo.getLevelSystem().getExperience();
                            int needed = cosmo.getLevelSystem().getXpForNextLevel();
                            
                            player.sendSystemMessage(Component.literal(
                                String.format("§d§lCOSMO Level %d §7[%d/%d XP] §e%.0f%%", 
                                    level, xp, needed, progress * 100)
                            ));
                        } else {
                            player.sendSystemMessage(Component.literal("§cYou don't have a COSMO companion!"));
                        }
                        return 1;
                    })
                    .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
                                int targetLevel = IntegerArgumentType.getInteger(context, "level");
                                
                                if (cosmo != null) {
                                    // Calculate XP needed for target level
                                    int totalXp = 0;
                                    for (int i = 1; i < targetLevel; i++) {
                                        totalXp += cosmo.getLevelSystem().getXpForNextLevel();
                                    }
                                    cosmo.getLevelSystem().addExperience(totalXp, player);
                                    
                                    player.sendSystemMessage(Component.literal(
                                        "§aCOSMO level set to " + targetLevel
                                    ));
                                }
                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.literal("ability")
                    .then(Commands.argument("ability", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("magnet");
                            builder.suggest("autodeposit");
                            builder.suggest("healing");
                            builder.suggest("home");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
                            String ability = StringArgumentType.getString(context, "ability");
                            
                            if (cosmo != null) {
                                switch (ability.toLowerCase()) {
                                    case "magnet":
                                        boolean magnetEnabled = !cosmo.getAbilities().isItemMagnetEnabled();
                                        cosmo.getAbilities().setItemMagnetEnabled(magnetEnabled);
                                        player.sendSystemMessage(Component.literal(
                                            "§eItem Magnet " + (magnetEnabled ? "§aENABLED" : "§cDISABLED")
                                        ));
                                        break;
                                    case "autodeposit":
                                        if (cosmo.getLevel() >= 3) {
                                            boolean autoEnabled = !cosmo.getAbilities().isAutoDepositEnabled();
                                            cosmo.getAbilities().setAutoDepositEnabled(autoEnabled);
                                            player.sendSystemMessage(Component.literal(
                                                "§eAuto-Deposit " + (autoEnabled ? "§aENABLED" : "§cDISABLED")
                                            ));
                                        } else {
                                            player.sendSystemMessage(Component.literal(
                                                "§cCOSMO needs to be level 3 to unlock Auto-Deposit!"
                                            ));
                                        }
                                        break;
                                    case "healing":
                                        if (cosmo.getLevel() >= 5) {
                                            boolean healEnabled = !cosmo.getAbilities().isHealingAuraEnabled();
                                            cosmo.getAbilities().setHealingAuraEnabled(healEnabled);
                                            player.sendSystemMessage(Component.literal(
                                                "§eHealing Aura " + (healEnabled ? "§aENABLED" : "§cDISABLED")
                                            ));
                                        } else {
                                            player.sendSystemMessage(Component.literal(
                                                "§cCOSMO needs to be level 5 to unlock Healing Aura!"
                                            ));
                                        }
                                        break;
                                    case "home":
                                        if (cosmo.getLevel() >= 7) {
                                            if (cosmo.getAbilities().getHomePos() != null) {
                                                cosmo.getAbilities().teleportHome(player);
                                                player.sendSystemMessage(Component.literal(
                                                    "§aTeleporting home..."
                                                ));
                                            } else {
                                                player.sendSystemMessage(Component.literal(
                                                    "§cNo home position set! Sneak + right-click COSMO to set."
                                                ));
                                            }
                                        } else {
                                            player.sendSystemMessage(Component.literal(
                                                "§cCOSMO needs to be level 7 to unlock Teleport Home!"
                                            ));
                                        }
                                        break;
                                    default:
                                        player.sendSystemMessage(Component.literal(
                                            "§cUnknown ability: " + ability
                                        ));
                                }
                            }
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("stats")
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        CosmoEntity cosmo = CosmoEntity.getCosmoForPlayer(player);
                        
                        if (cosmo != null) {
                            player.sendSystemMessage(Component.literal("§d§l=== COSMO Stats ==="));
                            player.sendSystemMessage(Component.literal(
                                "§7Level: §e" + cosmo.getLevel() + "/10"
                            ));
                            player.sendSystemMessage(Component.literal(
                                "§7Total XP: §e" + cosmo.getLevelSystem().getTotalExperience()
                            ));
                            player.sendSystemMessage(Component.literal(
                                "§7Playtime: §e" + (cosmo.getPlaytime() / 20 / 60) + " minutes"
                            ));
                            player.sendSystemMessage(Component.literal(
                                "§7Storage: §e" + cosmo.getStorage().getTotalItemCount() + "/" + 
                                cosmo.getStorage().getTotalSlots() + " items"
                            ));
                            player.sendSystemMessage(Component.literal("§7Abilities:"));
                            player.sendSystemMessage(Component.literal(
                                "  §7- Item Magnet: " + 
                                (cosmo.getAbilities().isItemMagnetEnabled() ? "§aON" : "§cOFF")
                            ));
                            if (cosmo.getLevel() >= 3) {
                                player.sendSystemMessage(Component.literal(
                                    "  §7- Auto-Deposit: " + 
                                    (cosmo.getAbilities().isAutoDepositEnabled() ? "§aON" : "§cOFF")
                                ));
                            }
                            if (cosmo.getLevel() >= 5) {
                                player.sendSystemMessage(Component.literal(
                                    "  §7- Healing Aura: " + 
                                    (cosmo.getAbilities().isHealingAuraEnabled() ? "§aON" : "§cOFF")
                                ));
                            }
                        }
                        return 1;
                    })
                )
        );
    }
}