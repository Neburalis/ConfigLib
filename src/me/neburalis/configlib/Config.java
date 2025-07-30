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
 * Основной класс для работы с конфигурациями.
 * 
 * <p>Этот класс предоставляет единый интерфейс для работы с конфигурационными файлами
 * в формате XML. Он объединяет в себе функциональность парсинга, сохранения,
 * отслеживания изменений и типизированного доступа к данным.</p>
 * 
 * <p><strong>Архитектурные особенности:</strong></p>
 * <ul>
 *   <li><strong>Типизированный доступ:</strong> Каждое значение имеет строго определенный тип,
 *       что предотвращает ошибки типов во время выполнения</li>
 *   <li><strong>Потокобезопасность:</strong> Использует ConcurrentHashMap для безопасной работы
 *       в многопоточных приложениях</li>
 *   <li><strong>Автоматическое отслеживание:</strong> ConfigWatcher автоматически перезагружает
 *       конфигурацию при изменении файла</li>
 *   <li><strong>Разделение ответственности:</strong> Парсинг вынесен в отдельный класс ConfigParser,
 *       что упрощает тестирование и поддержку</li>
 * </ul>
 * 
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * Config config = new Config("config.xml");
 * config.set("app.name", "MyApp", "Название приложения");
 * config.set("app.port", 8080, "Порт приложения");
 * 
 * String name = config.getString("app.name");
 * int port = config.getNumber("app.port").intValue();
 * }</pre>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
 */
public class Config {
    private final Map<String, ConfigValue> values = new ConcurrentHashMap<>();
    private final Map<String, String> descriptions = new ConcurrentHashMap<>();
    private final Path configFile;
    private final ConfigWatcher watcher;
    private final ConfigParser parser;
    
    /**
     * Создает новый экземпляр конфигурации.
     * 
     * <p>Конструктор выполняет следующие действия:</p>
     * <ol>
     *   <li>Создает путь к файлу конфигурации</li>
     *   <li>Инициализирует парсер для работы с XML</li>
     *   <li>Создает наблюдатель за изменениями файла</li>
     *   <li>Загружает существующую конфигурацию</li>
     *   <li>Запускает автоматическое отслеживание изменений</li>
     * </ol>
     * 
     * <p><strong>Почему именно такая последовательность:</strong></p>
     * <ul>
     *   <li>Сначала создаются все зависимости, чтобы избежать null pointer exceptions</li>
     *   <li>Загрузка происходит до запуска отслеживания, чтобы избежать ложных срабатываний</li>
     *   <li>Отслеживание запускается в конце, чтобы не перехватывать собственные изменения</li>
     * </ul>
     * 
     * @param filePath путь к файлу конфигурации (может быть относительным или абсолютным)
     * @throws IllegalArgumentException если filePath равен null или пустой строке
     * @throws IOException если файл существует, но не может быть прочитан
     */
    public Config(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.configFile = Paths.get(filePath);
        this.parser = new ConfigParser();
        this.watcher = new ConfigWatcher(this);
        loadConfig();
        startWatching();
    }
    
    /**
     * Получить значение конфигурации по указанному пути.
     * 
     * <p>Этот метод возвращает типизированное значение ConfigValue, которое содержит
     * как само значение, так и информацию о его типе. Это позволяет безопасно
     * преобразовывать значения в нужный тип без риска ClassCastException.</p>
     * 
     * <p><strong>Почему возвращается ConfigValue, а не Object:</strong></p>
     * <ul>
     *   <li>Типобезопасность: ConfigValue знает свой тип и может безопасно преобразовываться</li>
     *   <li>Единообразие: Все значения имеют одинаковый интерфейс</li>
     *   <li>Метаданные: ConfigValue содержит информацию о типе и валидности</li>
     * </ul>
     * 
     * @param path путь к значению в формате "section.subsection.key"
     * @return ConfigValue с типизированным значением или null, если путь не найден
     * @throws IllegalArgumentException если path равен null или пустой строке
     */
    public ConfigValue get(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        return values.get(path);
    }
    
    /**
     * Установить значение конфигурации по указанному пути.
     * 
     * <p>Этот метод автоматически определяет тип значения и создает соответствующий
     * ConfigValue. Поддерживаются следующие типы:</p>
     * <ul>
     *   <li>String, Number, Boolean - базовые типы</li>
     *   <li>ConfigValue - уже типизированные значения</li>
     *   <li>Специальные строки для больших чисел, времени, URL, email</li>
     * </ul>
     * 
     * <p><strong>Почему используется Object вместо дженериков:</strong></p>
     * <ul>
     *   <li>Гибкость: позволяет передавать любые типы данных</li>
     *   <li>Автоматическое определение типа: парсер сам определяет подходящий тип</li>
     *   <li>Простота использования: не нужно указывать тип явно</li>
     * </ul>
     * 
     * @param path путь к значению в формате "section.subsection.key"
     * @param value значение для установки (может быть любого поддерживаемого типа)
     * @throws IllegalArgumentException если path равен null или пустой строке
     */
    public void set(String path, Object value) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
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
     * Установить значение конфигурации с описанием.
     * 
     * <p>Этот метод аналогичен {@link #set(String, Object)}, но дополнительно
     * сохраняет описание значения, которое будет записано в XML файл как атрибут
     * description. Это полезно для документирования конфигурации.</p>
     * 
     * <p><strong>Почему описания сохраняются отдельно:</strong></p>
     * <ul>
     *   <li>Производительность: не нужно парсить описания при каждом обращении к значению</li>
     *   <li>Разделение данных и метаданных: описания не влияют на логику работы</li>
     *   <li>Гибкость: можно легко добавить/удалить описания без изменения значений</li>
     * </ul>
     * 
     * @param path путь к значению в формате "section.subsection.key"
     * @param value значение для установки (может быть любого поддерживаемого типа)
     * @param description описание значения (будет сохранено в XML)
     * @throws IllegalArgumentException если path равен null или пустой строке
     */
    public void set(String path, Object value, String description) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
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
     * Получить строковое значение конфигурации.
     * Все типы значений могут быть преобразованы в строку.
     * 
     * @param path путь к значению
     * @return строковое представление или null, если путь не найден
     */
    public String getString(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asString() : null;
    }
    
    /**
     * Получить числовое значение конфигурации.
     * Поддерживает обычные числа, большие числа (1k, 1m, 1b) и научную нотацию (1e-8).
     * 
     * @param path путь к значению
     * @return числовое значение или null, если путь не найден
     */
    public Number getNumber(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asNumber() : null;
    }
    
    /**
     * Получить булево значение конфигурации.
     * Поддерживает строки "true"/"false" и числовые значения (0 = false, другое = true).
     * 
     * @param path путь к значению
     * @return булево значение или null, если путь не найден
     */
    public Boolean getBoolean(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asBoolean() : null;
    }
    
    /**
     * Получить временное значение конфигурации.
     * Поддерживает единицы: ms, s, m, h, d, w, mo, q, y.
     * 
     * @param path путь к значению
     * @return Duration или null, если путь не найден
     */
    public Duration getDuration(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asDuration() : null;
    }
    
    /**
     * Получить URL значение конфигурации.
     * Проверяет корректность URL формата.
     * 
     * @param path путь к значению
     * @return URL или null, если путь не найден
     * @throws IllegalArgumentException если значение не является корректным URL
     */
    public java.net.URL getUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asUrl() : null;
    }
    
    /**
     * Получить email значение конфигурации.
     * Проверяет корректность email формата.
     * 
     * @param path путь к значению
     * @return email адрес или null, если путь не найден
     * @throws IllegalArgumentException если значение не является корректным email
     */
    public String getEmail(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        return value != null ? value.asEmail() : null;
    }
    
    /**
     * Получить массив как типизированные ConfigValue[].
     * Каждый элемент сохраняет свой тип и может быть преобразован в нужный формат.
     * 
     * @param path путь к массиву
     * @return массив ConfigValue или null, если путь не найден
     */
    public ConfigValue[] getArray(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).getArray();
        }
        return null;
    }
    
    /**
     * Получить массив как String[] для обратной совместимости.
     * Все элементы преобразуются в строки.
     * 
     * @param path путь к массиву
     * @return массив строк или null, если путь не найден
     */
    public String[] getStringArray(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ConfigValue value = get(path);
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).getStringArray();
        }
        return null;
    }
    
    /**
     * Получить элемент массива по индексу как ConfigValue
     */
    public ConfigValue getArrayElement(String path, int index) {
        ConfigValue value = get(path);
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).get(index);
        }
        return null;
    }
    
    /**
     * Получить элемент массива по индексу как String (для обратной совместимости)
     */
    public String getArrayElementString(String path, int index) {
        ConfigValue value = get(path);
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).getString(index);
        }
        return null;
    }
    
    /**
     * Получить размер массива
     */
    public int getArraySize(String path) {
        ConfigValue value = get(path);
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).size();
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