package org.mziuri.service;

import org.mziuri.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService {

    private final Map<String, Item> items;

    public DatabaseService() {
        this.items = new HashMap<>();
    }

    public void addItem(Item item) {
        items.put(item.id(), item);
    }

    public Item getItem(String id) {
        return items.get(id);
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    public void deleteItem(String id) {
        items.remove(id);
    }

}
