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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.util.Colors;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AdminVersion extends JavaModule {

	private static JSONParser jsonParser = new JSONParser();
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

        if (!args[0].equals("version")) {
            return;
        }

        // we have the right command
        event.setCancelled(true);

        // force a reload of the latest versions
        String pluginColor = Colors.Green;
        
        lwc.sendLocale(sender,"protection.admin.version.finalize", "plugin_color", pluginColor, "plugin_version", lwc.getPlugin().getDescription().getVersion().toString(), "latest_plugin", getVersion());
    }
    
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }
    
    public static String getVersion() {
        try {
            String userData = readUrl("https://api.inventivetalent.org/spigot/resource-simple/2162"); // Read the URL and get the data as a String.
            Object parsedData = jsonParser.parse(userData); // Use the JSON parser to parse the information into an Object.
            if (parsedData instanceof JSONObject) { // If the parsed information is a Map (in the format of JSON), continue.
                JSONObject jsonData = (JSONObject) parsedData; // Convert the information into a JSON map (JSONObject is a Map).
                if (jsonData.containsKey("version")) { // If the Map contains 'streams'.
                    if (!jsonData.isEmpty()) { // If the streams array list is not empty.
                        JSONObject jsonMap = (JSONObject) jsonData.get(0); // Get the first Map in the array of streams. This is hardcoded unfortunately.
                        return jsonMap.toString(); // If the Map contains "viewers", parse it and return it, if not, return 0.
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "0";
    }
}
