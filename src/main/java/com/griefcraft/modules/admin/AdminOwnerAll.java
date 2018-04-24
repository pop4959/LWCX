/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.griefcraft.modules.admin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.LWCPlayer;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.util.UUIDRegistry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AdminOwnerAll extends JavaModule {

	@Override
	public void onCommand(LWCCommandEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!event.hasFlag("a", "admin")) {
			return;
		}

		LWC lwc = event.getLWC();
		CommandSender sender = event.getSender();
		String[] args = event.getArgs();

		if (!args[0].equals("forceownerall")) {
			return;
		}

		if (args.length < 2) {
			lwc.sendSimpleUsage(sender,
					"/lwc admin forceownerall <OldPlayer> <NewPlayer>");
			return;
		}

		UUID oldOwner = UUIDRegistry.getUUID(args[2]);

		if (!(sender instanceof Player)) {
			lwc.sendLocale(sender, "protection.admin.noconsole");
			return;
		}

		String owner;

		if (oldOwner != null) {
			owner = oldOwner.toString();
		} else {
			owner = UUIDRegistry.getName(oldOwner);
		}

		UUID uuid = UUIDRegistry.getUUID(args[1]);
		List<Protection> protection;
		if (uuid != null) {
			protection = lwc.getPhysicalDatabase().loadProtectionsByPlayer(
					uuid.toString());
		} else {
			protection = lwc.getPhysicalDatabase().loadProtectionsByPlayer(
					args[1]);
		}

		LWCPlayer player = lwc.wrapPlayer(sender);
		for (Protection prot : protection) {
			prot.setOwner(owner);
			lwc.getPhysicalDatabase().saveProtection(prot);
			lwc.removeModes(player);
			System.out.println(prot.getOwner() + " Changed");
			lwc.sendLocale(player, "protection.interact.forceowner.finalize",
					"player", prot.getFormattedOwnerPlayerName());
		}
	}

}