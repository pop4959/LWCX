package com.griefcraft.modules.pluginsupport;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCAccessEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig.Factions.Protection;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Factions extends JavaModule {
    private boolean factionCheck;
    private Plugin factions = null;
    private boolean legacy = false;

    @Override
    public void load(LWC lwc) {
        this.factions = LWC.getInstance().getPlugin().getServer().getPluginManager().getPlugin("Factions");
        this.factionCheck = lwc.getConfiguration().getBoolean("core.factionCheck", false);
        String version = factions.getDescription().getVersion().replaceAll("[^\\d]", "");

        if (version.compareTo("1695049") < 0) {
            this.legacy = true;
        }
    }

    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        if (factions == null || !factionCheck) {
            return;
        }

        boolean canProtect = false;
        FLocation fLocation = new FLocation(event.getBlock().getLocation());
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        Faction faction = Board.getInstance().getFactionAt(fLocation);

        if (!legacy) {
            canProtect = canProtect(fPlayer, faction);
        } else {
            try {
                canProtect = canProtectLegacy(fPlayer, faction);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException |
                    SecurityException | ClassNotFoundException e) {
            }
        }

        if (!canProtect) {
            event.getLWC().sendLocale(event.getPlayer(), "lwc.factions.blocked");
            event.setCancelled(true);
        }
    }

    public boolean canProtect(FPlayer fPlayer, Faction faction) {
        Protection prot = FactionsPlugin.getInstance().conf().factions().protection();
        boolean fBypass = prot.getPlayersWhoBypassAllProtection().contains(fPlayer.getName())
                || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !prot.isWildernessDenyBuild();
        boolean fWarzone = faction.isWarZone() && !prot.isWarZoneDenyBuild();
        boolean fSafezone = faction.isSafeZone() && !prot.isSafeZoneDenyBuild();

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            return true;
        } else if (faction.hasAccess(fPlayer, PermissibleAction.BUILD)) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean canProtectLegacy(FPlayer fPlayer, Faction faction) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        Class<?> conf = Class.forName("com.massivecraft.factions.Conf");
        boolean wDenyBuild = conf.getDeclaredField("wildernessDenyBuild").getBoolean(null);
        boolean wzDenyBuild = conf.getDeclaredField("warZoneDenyBuild").getBoolean(null);
        boolean szDenyBuild = conf.getDeclaredField("safeZoneDenyBuild").getBoolean(null);
        Set<String> bypassPlayers = (Set<String>) conf.getDeclaredField("playersWhoBypassAllProtection").get(null);

        boolean fBypass = bypassPlayers.contains(fPlayer.getName())
                || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !wDenyBuild;
        boolean fWarzone = faction.isWarZone() && !wzDenyBuild;
        boolean fSafezone = faction.isSafeZone() && !szDenyBuild;

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            return true;
        } else if (faction.getFPlayers().contains(fPlayer)) {
            return true;
        }

        return false;
    }

    @Override
    public void onAccessRequest(LWCAccessEvent event) {
        Player player = event.getPlayer();
        com.griefcraft.model.Protection protection = event.getProtection();

        if (event.getAccess() != Permission.Access.NONE) {
            return;
        }

        if (protection.getType() != com.griefcraft.model.Protection.Type.PRIVATE) {
            return;
        }

        if (factions == null) {
            return;
        }

        for (Permission permission : protection.getPermissions()) {
            if (permission.getType() != Permission.Type.FACTION) {
                continue;
            }

            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
            Faction faction = com.massivecraft.factions.Factions.getInstance().getFactionById(permission.getName());

            // Check if the player is a member of the faction
            if (faction.getFPlayerAdmin().getName().equalsIgnoreCase(player.getName())) {
                // Faction admin
                event.setAccess(Permission.Access.ADMIN);
            } else if (faction.getFPlayers().contains(fPlayer)) {
                // Member of the faction
                event.setAccess(Permission.Access.PLAYER);
            } else {
                // Doesn't meet any of the requirements.
                event.setAccess(Permission.Access.NONE);
            }
        }
    }
}
