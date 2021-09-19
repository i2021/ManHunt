package de.shiirroo.manhunt.utilis.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class ConfigCreator {

    private final String configName;
    private Boolean configSettingBool;
    private Integer configSettingInt;
    private Integer min;
    private Integer max;
    private Integer defaultValue;
    private FileConfiguration config;
    private Plugin plugin;


    public ConfigCreator(String configName){
        this.configName = configName;
    };

    public ConfigCreator(String configName, Integer min, Integer max, Integer defaultValue){
        this.configName = configName;
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    };

    public ConfigCreator configCreator(FileConfiguration config) {
        this.config = config;
        Object configSetting = config.get(this.configName);
        if (configSetting instanceof Integer)
            if((Integer) configSetting >= this.min && (Integer) configSetting <= this.max) {
                this.configSettingInt = (Integer) configSetting;
            } else {
                this.configSettingInt = this.defaultValue;
            }
        if (configSetting instanceof Boolean)
            this.configSettingBool = (Boolean) configSetting;
        return this;
    }

    public ConfigCreator Plugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }


    public Object getConfigSetting(){
        if(this.configSettingBool != null){
            return this.configSettingBool;
        } else {
            return this.configSettingInt;
        }
    }

    public void setConfigSetting(Object configSetting){
        if (configSetting instanceof Integer configSettingInt){
            if(configSettingInt >= this.min && configSettingInt <= this.max && !Objects.equals(this.configSettingInt, configSettingInt)) {
                this.configSettingInt = configSettingInt;
                this.config.set(this.configName, configSettingInt);
                this.plugin.saveConfig();
            }
        } else if(configSetting instanceof Boolean configSettingBool) {
            if (this.configSettingBool != configSettingBool) {
                this.configSettingBool = configSettingBool;
                this.config.set(this.configName, configSettingBool);
                this.plugin.saveConfig();
            }
        }
    }



    public Integer getMin() {
        return this.min;
    }

    public Integer getMax() {
        return this.max;
    }

    public String getConfigName() {
        return this.configName;
    }
}