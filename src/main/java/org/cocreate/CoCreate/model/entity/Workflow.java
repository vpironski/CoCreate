package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class Workflow {

    private LinkedHashMap<String, List<Task>> cards = new LinkedHashMap<>();

    public Workflow() {
        cards.put("To Do", new ArrayList<>());
        cards.put("In Progress", new ArrayList<>());
        cards.put("Done", new ArrayList<>());
    }

    public Task getTaskById(String id) {
        return cards.values().stream()
                .flatMap(List::stream)
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeTaskById(String id) {
        cards.values().forEach(tasks -> tasks.removeIf(task -> task.getId().equals(id)));
    }


    public void createCard(String name){
        cards.put(name, new ArrayList<>());
    }

    public void removeCard(String name){
        cards.remove(name);
    }

    public void addTaskToCard(String name, Task task) {
        cards.get(name).add(task);
    }

    public void removeTaskFromCard(String name, Task task){
        cards.get(name).remove(task);
    }


}
