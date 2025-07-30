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
 * Реализация булевого значения конфига
 */
public class BooleanValue implements ConfigValue {
    private final Boolean value;
    
    public BooleanValue(Boolean value) {
        this.value = value;
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.BOOLEAN;
    }
    
    @Override
    public String asString() {
        return value != null ? value.toString() : null;
    }
    
    @Override
    public Number asNumber() {
        if (value == null) return null;
        return value ? 1 : 0;
    }
    
    @Override
    public Boolean asBoolean() {
        return value;
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
        return value != null ? value.toString() : "null";
    }
} 