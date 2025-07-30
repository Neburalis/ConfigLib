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

import java.time.Duration;
import java.net.URL;

/**
 * Реализация строкового значения конфига
 */
public class StringValue implements ConfigValue {
    private final String value;
    
    public StringValue(String value) {
        this.value = value;
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.STRING;
    }
    
    @Override
    public String asString() {
        return value;
    }
    
    @Override
    public Number asNumber() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public Boolean asBoolean() {
        if (value == null) return null;
        return Boolean.parseBoolean(value);
    }
    
    @Override
    public Duration asDuration() {
        return null; // Строки не могут быть преобразованы в время
    }
    
    @Override
    public URL asUrl() {
        try {
            return new URL(value);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String asEmail() {
        if (value != null && value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return value;
        }
        return null;
    }
    
    @Override
    public Object getValue() {
        return value;
    }
    
    @Override
    public boolean isNull() {
        return value == null;
    }
    
    @Override
    public String toString() {
        return value != null ? "\"" + value + "\"" : "null";
    }
} 