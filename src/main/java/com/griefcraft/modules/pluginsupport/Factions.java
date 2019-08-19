package com.griefcraft.modules.pluginsupport;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig.Factions.Protection;
import com.massivecraft.factions.perms.PermissibleAction;
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

        FLocation fLocation = new FLocation(event.getBlock().getLocation());
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        Protection prot = FactionsPlugin.getInstance().conf().factions().protection();

        boolean fBypass = prot.getPlayersWhoBypassAllProtection().contains(fPlayer.getName())
                || fPlayer.isAdminBypassing();
        boolean fWilderness = faction.isWilderness() && !prot.isWildernessDenyBuild();
        boolean fWarzone = faction.isWarZone() && !prot.isWarZoneDenyBuild();
        boolean fSafezone = faction.isSafeZone() && !prot.isSafeZoneDenyBuild();

        if (!(fBypass || fWilderness || fWarzone || fSafezone) && !faction.hasAccess(fPlayer, PermissibleAction.BUILD)) {
            event.getLWC().sendLocale(event.getPlayer(), "lwc.factions.blocked");
            event.setCancelled(true);
        }
    }
}
