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
 * Реализация числового значения конфига
 */
public class NumberValue implements ConfigValue {
    private final Number value;
    
    public NumberValue(Number value) {
        this.value = value;
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.NUMBER;
    }
    
    @Override
    public String asString() {
        return value != null ? value.toString() : null;
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
        return null; // Обычные числа не могут быть преобразованы в время
    }
    
    @Override
    public URL asUrl() {
        return null; // Числа не могут быть URL
    }
    
    @Override
    public String asEmail() {
        return null; // Числа не могут быть email
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