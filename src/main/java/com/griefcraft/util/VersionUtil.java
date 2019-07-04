package com.griefcraft.util;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtil {

    public static int getMinorVersion() {
        Matcher versionCheck = Pattern.compile("\\d[.]\\d+").matcher(Bukkit.getVersion());
        return versionCheck.find() ? Integer.parseInt(versionCheck.group().substring(2)) : -1;
    }

}
