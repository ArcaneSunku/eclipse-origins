package dev.atomixsoft.solar_eclipse.core.config;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.INIConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;


public class INIConfigurationFile implements ConfigurationFile {
    private INIConfiguration m_Config;

    @Override
    public void load(String path) throws Exception {
        Configurations configs = new Configurations();
        this.m_Config = configs.ini(new File(path));
    }

    @Override
    public void save(String path) throws Exception {
        FileWriter writer = new FileWriter(path);
        this.m_Config.write(writer);
    }

    @Override
    public String getValue(String key) {
        try {
            return this.m_Config.getString(key);
        } catch (Exception e) {
            throw new IllegalArgumentException("Key not found");
        }
    }

    @Override
    public void setValue(String key, String value) {
        try {
            this.m_Config.setProperty(key, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Key could not be set");
        }
    }

    @Override
    public Map<String, String> getConfig() {
        Map<String, String> values = new HashMap<>();

        try{
            this.m_Config.getKeys().forEachRemaining(key -> values.put(key, this.m_Config.getString(key)));

            return values;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not get the configuration");
        }
    }

    @Override
    public void setConfig(Map<String, String> values) {
        try{
            this.m_Config.clear();
            values.forEach((key, value) -> this.m_Config.setProperty(key, value));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not set the configuration");
        }
    }
}
