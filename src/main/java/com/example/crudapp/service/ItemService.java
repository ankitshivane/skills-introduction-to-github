package com.example.crudapp.service;

import com.example.crudapp.model.Item;
import com.example.crudapp.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> getAllItems() {
        return repository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return repository.findById(id);
    }

    public Item createItem(Item item) {
        item.setId(null); // ensure a new ID is assigned
        return repository.save(item);
    }

    public Optional<Item> updateItem(Long id, Item updatedItem) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        updatedItem.setId(id);
        return Optional.of(repository.save(updatedItem));
    }

    public boolean deleteItem(Long id) {
        return repository.deleteById(id);
    }
}
