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

package me.neburalis.configlib.types;

import java.util.*;

/**
 * Значение массива с типизированными элементами
 */
public class ArrayValue implements ConfigValue {
    private final List<ConfigValue> values;
    private final String elementType;
    
    public ArrayValue(String[] stringValues, String elementType) {
        this.elementType = elementType;
        this.values = new ArrayList<>();
        
        // Преобразуем строки в соответствующие ConfigValue
        for (String value : stringValues) {
            ConfigValue configValue = createTypedValue(value, elementType);
            this.values.add(configValue);
        }
    }
    
    public ArrayValue(List<String> stringValues, String elementType) {
        this(stringValues.toArray(new String[0]), elementType);
    }
    
    /**
     * Создать типизированное значение на основе строки и типа
     */
    private ConfigValue createTypedValue(String value, String type) {
        switch (type.toLowerCase()) {
            case "string":
                return new StringValue(value);
            case "boolean":
                return new BooleanValue(Boolean.parseBoolean(value));
            case "number":
                return new NumberValue(parseNumber(value));
            case "duration":
                return new DurationValue(value);
            case "url":
                return new UrlValue(value);
            case "email":
                return new EmailValue(value);
            case "large_number":
                return new LargeNumberValue(value);
            case "scientific_number":
                return new ScientificNumberValue(value);
            default:
                return new StringValue(value);
        }
    }
    
    /**
     * Парсить число
     */
    private Number parseNumber(String str) {
        try {
            if (str.contains(".")) {
                return Double.parseDouble(str);
            } else {
                return Long.parseLong(str);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Получить элементы массива как список ConfigValue
     */
    public List<ConfigValue> getValues() {
        return new ArrayList<>(values);
    }
    
    /**
     * Получить элементы массива как массив ConfigValue
     */
    public ConfigValue[] getArray() {
        return values.toArray(new ConfigValue[0]);
    }
    
    /**
     * Получить элементы массива как массив строк (для обратной совместимости)
     */
    public String[] getStringArray() {
        return values.stream()
                .map(ConfigValue::toString)
                .toArray(String[]::new);
    }
    
    /**
     * Получить элемент по индексу как ConfigValue
     */
    public ConfigValue get(int index) {
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        }
        return null;
    }
    
    /**
     * Получить элемент по индексу как строку (для обратной совместимости)
     */
    public String getString(int index) {
        ConfigValue value = get(index);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Получить размер массива
     */
    public int size() {
        return values.size();
    }
    
    /**
     * Получить тип элементов массива
     */
    public String getElementType() {
        return elementType;
    }
    
    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(values.get(i).toString());
        }
        return sb.toString();
    }
    
    @Override
    public Number asNumber() {
        // Для массива возвращаем размер
        return values.size();
    }
    
    @Override
    public Boolean asBoolean() {
        // Для массива возвращаем true если не пустой
        return !values.isEmpty();
    }
    
    @Override
    public java.time.Duration asDuration() {
        // Для массива возвращаем null
        return null;
    }
    
    @Override
    public java.net.URL asUrl() {
        // Для массива возвращаем null
        return null;
    }
    
    @Override
    public String asEmail() {
        // Для массива возвращаем null
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(values.get(i).toString());
        }
        return sb.toString();
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.ARRAY;
    }
    
    @Override
    public boolean isNull() {
        return values.isEmpty();
    }
    
    @Override
    public Object getValue() {
        return values;
    }
} 