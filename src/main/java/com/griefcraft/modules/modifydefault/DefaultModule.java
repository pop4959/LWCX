package com.griefcraft.modules.modifydefault;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.LWCPlayer;
import com.griefcraft.model.Protection;
import com.griefcraft.modules.modify.ModifyModule;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.scripting.event.LWCProtectionInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import static com.griefcraft.util.StringUtil.join;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 */
public class DefaultModule extends JavaModule {

	@Override
	public void onCommand(LWCCommandEvent event) {
		if (event.isCancelled()) {
            return;
        }

        if (!event.hasFlag("default")) {
            return;
        }

		LWC lwc = event.getLWC();
        CommandSender sender = event.getSender();
        String[] args = event.getArgs();
        String full = join(args, 0).trim();
        event.setCancelled(true);
        
        if("".equals(full)) {
            lwc.sendLocale(sender, "help.default");
            return;
        }

        if (!lwc.hasPlayerPermission(sender, "lwc.modify")) {
            lwc.sendLocale(sender, "protection.accessdenied");
            return;
        }

		if (args.length < 1) {
            lwc.sendLocale(sender, "help.default");
            return;
        }

		// full is the value to store in the player DB
		lwc.processDefaultModifications(sender, full);
	}

    @Override
    public void onPostRegistration(LWCProtectionRegistrationPostEvent event) {
        LWC lwc = event.getLWC();
        if(event.getProtection().getType() == Protection.Type.PRIVATE) {
            String data = lwc.getDefaultsCache().getOrLoad(event.getPlayer());
            if(data == null) {
                // They have not set a default yet.
                return;
            }
            // Fake a command + interact through the ModifyModule, to ensure we automatically
            // pick up future changes to the system.
            ModifyModule modify = new ModifyModule();

            LWCPlayer player = lwc.wrapPlayer(event.getPlayer());

            com.griefcraft.model.Action action = new com.griefcraft.model.Action();
            action.setName("modify");
            action.setPlayer(player);
            action.setData(data);
            player.removeAllActions();
            player.addAction(action);

            PlayerInteractEvent interactEvent = new PlayerInteractEvent(event.getPlayer(), Action.LEFT_CLICK_BLOCK,
                    null, event.getProtection().getBlock(), BlockFace.EAST);
            Set<String> actions = new HashSet<>();
            actions.add("modify");
            LWCProtectionInteractEvent interact = new LWCProtectionInteractEvent(interactEvent, event.getProtection(),
                    actions, true, true);
            modify.onProtectionInteract(interact);
        }
    }



}
