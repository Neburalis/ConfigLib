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
 * Реализация чисел в научной нотации конфига (1e-8, 1e15)
 */
public class ScientificNumberValue implements ConfigValue {
    private final String originalValue;
    private final Number value;
    
    public ScientificNumberValue(String value) {
        this.originalValue = value;
        this.value = parseScientificNumber(value);
    }
    
    private Number parseScientificNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.SCIENTIFIC_NUMBER;
    }
    
    @Override
    public String asString() {
        return originalValue;
    }
    
    @Override
    public Number asNumber() {
        return value;
    }
    
    @Override
    public Boolean asBoolean() {
        if (value == null) return null;
        return value.doubleValue() != 0;
    }
    
    @Override
    public Duration asDuration() {
        return null;
    }
    
    @Override
    public URL asUrl() {
        return null;
    }
    
    @Override
    public String asEmail() {
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
        return originalValue;
    }
} 