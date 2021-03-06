package org.serverct.parrot.parrotx.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.serverct.parrot.parrotx.PPlugin;
import org.serverct.parrot.parrotx.data.PConfiguration;
import org.serverct.parrot.parrotx.utils.I18n;

import java.io.File;
import java.io.IOException;

public class PConfig implements PConfiguration {

    protected PPlugin plugin;
    @Getter
    protected File file;
    @Getter
    protected FileConfiguration config;
    private String id;
    private String name;

    public PConfig(@NonNull PPlugin plugin, String fileName, String typeName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName + ".yml");
        this.id = fileName;
        this.name = typeName;
    }

    @Override
    public String getTypeName() {
        return name + "/" + id;
    }

    @Override
    public String getFileName() {
        return id;
    }

    @Override
    public void init() {
        if (!file.exists()) {
            saveDefault();
            plugin.lang.log("未找到 &c" + getTypeName() + "&7, 已自动生成.", I18n.Type.WARN, false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        plugin.lang.log("已加载 &c" + getTypeName() + "&7.", I18n.Type.INFO, false);

        try {
            //parseAnnotations();
            load(file);
        } catch (Throwable e) {
            plugin.lang.logError(I18n.LOAD, getTypeName(), e, null);
        }
    }

    /*private void parseAnnotations() {
        try {
            Class<? extends PConfig> configClass = this.getClass();
            for (Field field : configClass.getFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    Class<? extends Annotation> type = annotation.annotationType();
                    if (type.equals(PConfigString.class)) {
                        PConfigString string = (PConfigString) annotation;
                        field.set(this, config.getString(string.path(), string.def()));
                    } else if (type.equals(PConfigBoolean.class)) {
                        PConfigBoolean bool = (PConfigBoolean) annotation;
                        field.set(this, config.getBoolean(bool.path(), bool.def()));
                    } else if (type.equals(PConfigInt.class)) {
                        PConfigInt integer = (PConfigInt) annotation;
                        field.set(this, config.getInt(integer.path(), integer.def()));
                    } else if (type.equals(PConfigDouble.class)) {
                        PConfigDouble doubleNumber = (PConfigDouble) annotation;
                        field.set(this, config.getDouble(doubleNumber.path(), doubleNumber.def()));
                    } else if (type.equals(PConfigData.class)) {
                        PConfigData data = (PConfigData) annotation;
                        field.set(this, config.get(data.path(), null));
                    }
                }
            }
        } catch (Throwable e) {
            plugin.lang.logError(I18n.LOAD, getTypeName(), e, null);
        }
    }*/

    @Override
    public void setFile(@NonNull File file) {
        this.file = file;
    }

    @Override
    public void load(@NonNull File file) {
    }

    @Override
    public void reload() {
        plugin.lang.logAction(I18n.RELOAD, getTypeName());
        init();
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.lang.logError(I18n.SAVE, getTypeName(), e, null);
        }
    }

    @Override
    public void delete() {
        if (file.delete()) {
            plugin.lang.logAction(I18n.DELETE, getTypeName());
        } else {
            plugin.lang.logError(I18n.DELETE, getTypeName(), "无法删除该文件");
        }
    }

    @Override
    public void saveDefault() {
    }
}
