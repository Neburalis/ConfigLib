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
import java.util.regex.Pattern;

/**
 * Реализация email значения конфига
 */
public class EmailValue implements ConfigValue {
    private final String originalValue;
    private final String value;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public EmailValue(String value) {
        this.originalValue = value;
        this.value = parseEmail(value);
    }
    
    private String parseEmail(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        
        str = str.trim();
        if (EMAIL_PATTERN.matcher(str).matches()) {
            return str;
        }
        return null;
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.EMAIL;
    }
    
    @Override
    public String asString() {
        return originalValue;
    }
    
    @Override
    public Number asNumber() {
        return null;
    }
    
    @Override
    public Boolean asBoolean() {
        return null;
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
        return value;
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