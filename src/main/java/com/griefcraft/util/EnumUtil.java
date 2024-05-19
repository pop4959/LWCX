package com.griefcraft.util;

import java.util.Optional;

public class EnumUtil {
    public static <T extends Enum<T>> Optional<T> valueOf(final Class<T> enumClass, final String name) {
        try {
            return Optional.of(Enum.valueOf(enumClass, name));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
