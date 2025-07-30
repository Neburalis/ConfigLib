/*
 * This file is part of ConfigLib.
 *
 * ConfigLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConfigLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ConfigLib. If not, see <https://www.gnu.org/licenses/>.
 */

package me.neburalis.configlib;

import me.neburalis.configlib.types.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Duration;

/**
 * Основной класс для работы с конфигурациями
 */
public class Config {
    private final Map<String, ConfigValue> values = new ConcurrentHashMap<>();
    private final Map<String, String> descriptions = new ConcurrentHashMap<>();
    private final Path configFile;
    private final ConfigWatcher watcher;
    private final ConfigParser parser;
    
    public Config(String filePath) {
        this.configFile = Paths.get(filePath);
        this.parser = new ConfigParser();
        this.watcher = new ConfigWatcher(this);
        loadConfig();
        startWatching();
    }
    
    /**
     * Получить значение по пути
     */
    public ConfigValue get(String path) {
        return values.get(path);
    }
    
    /**
     * Установить значение по пути
     */
    public void set(String path, Object value) {
        ConfigValue configValue;
        if (value instanceof ConfigValue) {
            configValue = (ConfigValue) value;
        } else {
            configValue = parser.parseValue(value);
        }
        values.put(path, configValue);
        saveConfig();
    }
    
    /**
     * Установить значение с описанием
     */
    public void set(String path, Object value, String description) {
        ConfigValue configValue;
        if (value instanceof ConfigValue) {
            configValue = (ConfigValue) value;
        } else {
            configValue = parser.parseValue(value);
        }
        values.put(path, configValue);
        descriptions.put(path, description);
        saveConfig();
    }
    
    /**
     * Получить строковое значение
     */
    public String getString(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asString() : null;
    }
    
    /**
     * Получить числовое значение
     */
    public Number getNumber(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asNumber() : null;
    }
    
    /**
     * Получить булево значение
     */
    public Boolean getBoolean(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asBoolean() : null;
    }
    
    /**
     * Получить значение времени
     */
    public Duration getDuration(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asDuration() : null;
    }
    
    /**
     * Получить URL
     */
    public java.net.URL getUrl(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asUrl() : null;
    }
    
    /**
     * Получить email
     */
    public String getEmail(String path) {
        ConfigValue value = get(path);
        return value != null ? value.asEmail() : null;
    }
    
    /**
     * Получить массив как ConfigValue[]
     */
    public me.neburalis.configlib.types.ConfigValue[] getArray(String path) {
        ConfigValue value = get(path);
        if (value instanceof me.neburalis.configlib.types.ArrayValue) {
            return ((me.neburalis.configlib.types.ArrayValue) value).getArray();
        }
        return null;
    }
    
    /**
     * Получить массив как String[] (для обратной совместимости)
     */
    public String[] getStringArray(String path) {
        ConfigValue value = get(path);
        if (value instanceof me.neburalis.configlib.types.ArrayValue) {
            return ((me.neburalis.configlib.types.ArrayValue) value).getStringArray();
        }
        return null;
    }
    
    /**
     * Получить элемент массива по индексу как ConfigValue
     */
    public me.neburalis.configlib.types.ConfigValue getArrayElement(String path, int index) {
        ConfigValue value = get(path);
        if (value instanceof me.neburalis.configlib.types.ArrayValue) {
            return ((me.neburalis.configlib.types.ArrayValue) value).get(index);
        }
        return null;
    }
    
    /**
     * Получить элемент массива по индексу как String (для обратной совместимости)
     */
    public String getArrayElementString(String path, int index) {
        ConfigValue value = get(path);
        if (value instanceof me.neburalis.configlib.types.ArrayValue) {
            return ((me.neburalis.configlib.types.ArrayValue) value).getString(index);
        }
        return null;
    }
    
    /**
     * Получить размер массива
     */
    public int getArraySize(String path) {
        ConfigValue value = get(path);
        if (value instanceof me.neburalis.configlib.types.ArrayValue) {
            return ((me.neburalis.configlib.types.ArrayValue) value).size();
        }
        return 0;
    }
    
    /**
     * Загрузить конфиг из файла
     */
    private void loadConfig() {
        if (Files.exists(configFile)) {
            try {
                String content = Files.readString(configFile);
                Map<String, ConfigValue> loadedValues = parser.parse(content);
                values.clear();
                values.putAll(loadedValues);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }
        }
    }
    
    /**
     * Перезагрузить конфиг из файла
     */
    public void reload() {
        loadConfig();
    }
    
    /**
     * Получить путь к файлу конфига
     */
    public Path getConfigFile() {
        return configFile;
    }
    
    /**
     * Сохранить конфиг в файл
     */
    private void saveConfig() {
        try {
            String xmlContent = parser.toXml(values, descriptions);
            Files.writeString(configFile, xmlContent);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
    
    /**
     * Начать отслеживание изменений файла
     */
    private void startWatching() {
        watcher.start();
    }
    
    /**
     * Остановить отслеживание изменений файла
     */
    public void stopWatching() {
        watcher.stop();
    }
    
    /**
     * Получить все пути в конфиге
     */
    public Set<String> getPaths() {
        return new HashSet<>(values.keySet());
    }
    
    /**
     * Проверить существование пути
     */
    public boolean hasPath(String path) {
        return values.containsKey(path);
    }
} 