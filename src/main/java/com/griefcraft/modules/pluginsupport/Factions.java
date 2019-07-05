package com.griefcraft.modules.pluginsupport;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.plugin.Plugin;

public class Factions extends JavaModule {

    private boolean factionCheck;

    private Plugin factions = null;

    @Override
    public void load(LWC lwc) {
        this.factionCheck = lwc.getConfiguration().getBoolean("core.factionCheck", false);
        factions = lwc.getPlugin().getServer().getPluginManager().getPlugin("Factions");
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
        Faction myFaction = fPlayer.getFaction();
        boolean fBypass = Conf.playersWhoBypassAllProtection.contains(fPlayer.getName()) || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !Conf.wildernessDenyBuild;
        boolean fWarzone = faction.isWarZone() && !Conf.warZoneDenyBuild;
        boolean fSafezone = faction.isSafeZone() && !Conf.safeZoneDenyBuild;

        if (fBypass || fWilderness || fWarzone || fSafezone) {
            canProtect = true;
        } else if (!myFaction.getRelationTo(faction).confDenyBuild(faction.hasPlayersOnline())) {
            canProtect = true;
        } else {
            Access fAccess = faction.getAccess(fPlayer, PermissableAction.BUILD);
            if (fAccess != null) { canProtect = (fAccess == Access.ALLOW); }
        }

        if (!canProtect) {
            event.getLWC().sendLocale(event.getPlayer(), "lwc.factions.blocked");
            event.setCancelled(true);
        }
    }
}
