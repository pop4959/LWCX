/*
 * Copyright 2020 cs8425. All rights reserved.
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

package com.griefcraft.sql;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

//import java.util.ArrayList;
//import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//import java.util.List;
//import java.util.UUID;

public class PersistentDB {
	/**
	 * NamespacedKeys for PersistentDataContainer
	 */
	private final Map<String, NamespacedKey> nKey = new HashMap<>();

	/**
	 * ID for ContainerID
	 */
	private File counterFile;
	private YamlConfiguration config;
	private int counter;

	public PersistentDB(JavaPlugin plugin) {
		nKey.put("id", new NamespacedKey(plugin, "id"));
		nKey.put("type", new NamespacedKey(plugin, "type"));
		nKey.put("owner", new NamespacedKey(plugin, "owner"));
		nKey.put("password", new NamespacedKey(plugin, "pwd"));
		nKey.put("date", new NamespacedKey(plugin, "date"));
		nKey.put("last_accessed", new NamespacedKey(plugin, "last_accessed"));
		nKey.put("JSON_data", new NamespacedKey(plugin, "data"));

		counterFile = new File(plugin.getDataFolder() + File.separator + "pdc.yml");
		if(!counterFile.exists()){
			try{
				counterFile.createNewFile();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(counterFile);
		counter = config.getInt("sn", 0);
	}

	public synchronized int getID() {
		counter -= 1;
		return counter;
	}
	public synchronized void dispose() {
		config.set("sn", counter);
		try{
			config.save(counterFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public boolean readFrom(PersistentDataContainer dc, ProtectionInfo out) {
		out.protectionId = dc.getOrDefault(nKey.get("id"), PersistentDataType.INTEGER, null);
		if (out.protectionId == null) return false;

		out.type = dc.getOrDefault(nKey.get("type"), PersistentDataType.INTEGER, null);
		if (out.type == null) return false;

		out.owner = dc.getOrDefault(nKey.get("owner"), PersistentDataType.STRING, null);
		if (out.owner == null) return false;

		out.password = dc.getOrDefault(nKey.get("password"), PersistentDataType.STRING, null);
		if (out.password == null) return false;

		out.date = dc.getOrDefault(nKey.get("date"), PersistentDataType.STRING, null);
		if (out.date == null) return false;

		out.lastAccessed = dc.getOrDefault(nKey.get("last_accessed"), PersistentDataType.LONG, null);
		if (out.lastAccessed == null) return false;

		out.data = dc.getOrDefault(nKey.get("JSON_data"), PersistentDataType.STRING, null);
		if (out.data == null) return false;

		return true;
	}

	public boolean saveTo(PersistentDataContainer dc, ProtectionInfo info) {
		dc.set(nKey.get("id"), PersistentDataType.INTEGER, info.protectionId);
		dc.set(nKey.get("type"), PersistentDataType.INTEGER, info.type);
		dc.set(nKey.get("owner"), PersistentDataType.STRING, info.owner);
		dc.set(nKey.get("password"), PersistentDataType.STRING, info.password);
		dc.set(nKey.get("date"), PersistentDataType.STRING, info.date);
		dc.set(nKey.get("last_accessed"), PersistentDataType.LONG, info.lastAccessed);
		dc.set(nKey.get("JSON_data"), PersistentDataType.STRING, info.data);
		return true;
	}

	public boolean clear(PersistentDataContainer dc) {
		dc.remove(nKey.get("id"));
		dc.remove(nKey.get("type"));
		dc.remove(nKey.get("owner"));
		dc.remove(nKey.get("password"));
		dc.remove(nKey.get("date"));
		dc.remove(nKey.get("last_accessed"));
		dc.remove(nKey.get("JSON_data"));
		return true;
	}

	public static class ProtectionInfo {
		public Integer protectionId; // id
		public Integer type; // type
		public String owner; // owner
		public String password; // password
		public String date; // date
		public Long lastAccessed; // last_accessed
		public String data; // JSON_data
	}
}

