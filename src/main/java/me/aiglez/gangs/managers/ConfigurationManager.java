package me.aiglez.gangs.managers;

import me.aiglez.gangs.helpers.Message;
import me.lucko.helper.Helper;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationException;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.ValueType;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class ConfigurationManager {

    private final YAMLConfigurationLoader configLoader;
    private final YAMLConfigurationLoader langLoader;
    private ConfigurationNode config, language;

    public ConfigurationManager() {
        final File configFile = Helper.hostPlugin().getBundledFile("config.yml");
        if (!configFile.exists()) {
            throw new ConfigurationException("Couldn't find the config file in the jar");
        }

        final File langFile = Helper.hostPlugin().getBundledFile("lang.yml");
        if (!langFile.exists()) {
            throw new ConfigurationException("Couldn't find the language file in the jar");
        }

        this.configLoader = ConfigFactory.yaml().loader(configFile);
        this.langLoader = ConfigFactory.yaml().loader(langFile);
    }

    public boolean loadConfiguration() {
        try {
            this.config = this.configLoader.load();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadLanguage() {
        try {
            boolean save = false;
            this.language = this.langLoader.load();
            for (final Message message : Message.values()) {
                final ConfigurationNode node = this.language.getNode(message.toPath());
                if (node.getValueType() == ValueType.NULL) {
                    node.setValue(message.getDefaultValue());
                    save = true;
                }
            }

            if (save) {
                this.langLoader.save(this.language);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ConfigurationNode getLanguageNode() {
        return this.language;
    }

    public ConfigurationNode getConfigNode() {
        return this.config;
    }
}
