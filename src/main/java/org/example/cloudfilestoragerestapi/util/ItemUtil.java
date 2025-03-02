package org.example.cloudfilestoragerestapi.util;


import io.minio.Result;
import io.minio.messages.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    public static List<Item> getItemsFromResult(Iterable<Result<Item>> results) {
        List<Item> items = new ArrayList<>();
        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                items.add(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return items;
    }
}
