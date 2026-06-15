package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.server.game.classes.ClassDefinition;

import java.util.HashMap;
import java.util.Map;

public class ClassService {

    private final Map<Integer, ClassDefinition> m_Classes;

    public ClassService() {
        m_Classes = new HashMap<>();
    }

    public void load() {

    }

    public ClassDefinition get(int id) {
        return m_Classes.get(id);
    }

    public Map<Integer, ClassDefinition> all() {
        return m_Classes;
    }

}
