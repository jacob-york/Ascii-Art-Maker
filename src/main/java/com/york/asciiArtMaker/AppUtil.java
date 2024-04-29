package com.york.asciiArtMaker;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class AppUtil {
    private AppUtil() {}

    // why tf do I have to write these myself, java?
    public static <T> Set<T> union(Set<T> coll1, Set<T> coll2) {
        Set<T> returnVal = new HashSet<>(coll1);
        returnVal.addAll(coll2);
        return returnVal;
    }

    public static <T> Set<T> intersection(Set<T> coll1, Set<T> coll2) {
        return coll1.stream()
                .filter(item -> !coll2.contains(item))
                .collect(Collectors.toSet());
    }

    public static java.awt.Color JFXColorToJavaColor(javafx.scene.paint.Color fx) {
        return new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }
}
