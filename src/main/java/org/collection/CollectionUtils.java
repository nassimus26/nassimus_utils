package org.collection;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by VF5416 on 03/09/2016.
 */
public class CollectionUtils {
    public static String join(Collection col, String delimiter){
        return (String) col.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(delimiter));
    }
    public static String joinInLines(Collection col){
        return join(col, "\r\n");
    }
}
