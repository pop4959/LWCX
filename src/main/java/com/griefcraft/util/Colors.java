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

package com.griefcraft.util;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {

    // Color codes
    public static final String Black           = "\u00A70";
    public static final String Dark_Blue       = "\u00A71";
    public static final String Dark_Green      = "\u00A72";
    public static final String Dark_Aqua       = "\u00A73";
    public static final String Dark_Red        = "\u00A74";
    public static final String Dark_Purple     = "\u00A75";
    public static final String Gold            = "\u00A76";
    public static final String Gray            = "\u00A77";
    public static final String Dark_Gray       = "\u00A78";
    public static final String Blue            = "\u00A79";
    public static final String Green           = "\u00A7a";
    public static final String Aqua            = "\u00A7b";
    public static final String Red             = "\u00A7c";
    public static final String Light_Purple    = "\u00A7d";
    public static final String Yellow          = "\u00A7e";
    public static final String White           = "\u00A7f";

    // Special codes
    public static final String Obfuscated       = "\u00A7k";
    public static final String Bold             = "\u00A7l";
    public static final String Strikethrough    = "\u00A7m";
    public static final String Underline        = "\u00A7n";
    public static final String Italic           = "\u00A7o";
    public static final String Reset            = "\u00A7r";

    // RGB pattern
    public static final Pattern RGB_PATTERN     = Pattern.compile("%#[0-9a-fA-F]{6}%");

    // contains colors for locales
    public static final Map<String, String> localeColors = new HashMap<>();

    static {
        // Color code in percentage block
        localeColors.put("%black%", Black);
        localeColors.put("%dark_blue%", Dark_Blue);
        localeColors.put("%dark_green%", Dark_Green);
        localeColors.put("%dark_aqua%", Dark_Aqua);
        localeColors.put("%dark_red%", Dark_Red);
        localeColors.put("%dark_purple%", Dark_Purple);
        localeColors.put("%gold%", Gold);
        localeColors.put("%gray%", Gray);
        localeColors.put("%dark_gray%", Dark_Gray);
        localeColors.put("%blue%", Blue);
        localeColors.put("%green%", Green);
        localeColors.put("%aqua%", Aqua);
        localeColors.put("%red%", Red);
        localeColors.put("%light_purple%",Light_Purple);
        localeColors.put("%yellow%", Yellow);
        localeColors.put("%white%", White);

        localeColors.put("%obfuscated%", Obfuscated);
        localeColors.put("%bold%", Bold);
        localeColors.put("%strikethrough%", Strikethrough);
        localeColors.put("%underline%", Underline);
        localeColors.put("%italic%", Italic);
        localeColors.put("%reset%", Reset);
    }

    public static String apply(String message) {
        for (String colorKey : localeColors.keySet()) {
            if (message.contains(colorKey)) {
                message = StringUtil.fastReplace(message, colorKey, localeColors.get(colorKey));
            }
        }
        Matcher rgbMatcher = RGB_PATTERN.matcher(message);
        while (rgbMatcher.find()) {
            String rgbMatch = rgbMatcher.group();
            String rgbColor = String.valueOf(ChatColor.COLOR_CHAR) + 'x' +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(2) +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(3) +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(4) +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(5) +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(6) +
                    ChatColor.COLOR_CHAR + rgbMatch.charAt(7);
            message = StringUtil.fastReplace(message, rgbMatch, rgbColor);
        }
        return message;
    }
}
