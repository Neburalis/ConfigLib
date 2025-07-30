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
 * Реализация временных значений конфига (1ms, 1s, 1m, 1h, 1d, 1w, 1mo, 1q, 1y)
 */
public class DurationValue implements ConfigValue {
    private final String originalValue;
    private final Duration value;
    
    public DurationValue(String value) {
        this.originalValue = value;
        this.value = parseDuration(value);
    }
    
    private Duration parseDuration(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        
        str = str.trim().toLowerCase();
        
        try {
            if (str.endsWith("ms")) {
                long milliseconds = Long.parseLong(str.substring(0, str.length() - 2));
                return Duration.ofMillis(milliseconds);
            } else if (str.endsWith("s")) {
                long seconds = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofSeconds(seconds);
            } else if (str.endsWith("m")) {
                long minutes = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofMinutes(minutes);
            } else if (str.endsWith("h")) {
                long hours = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofHours(hours);
            } else if (str.endsWith("d")) {
                long days = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofDays(days);
            } else if (str.endsWith("w")) {
                long weeks = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofDays(weeks * 7);
            } else if (str.endsWith("mo")) {
                long months = Long.parseLong(str.substring(0, str.length() - 2));
                return Duration.ofDays(months * 30); // Приблизительно
            } else if (str.endsWith("q")) {
                long quarters = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofDays(quarters * 90); // 3 месяца
            } else if (str.endsWith("y")) {
                long years = Long.parseLong(str.substring(0, str.length() - 1));
                return Duration.ofDays(years * 365); // Приблизительно
            } else {
                // Попробуем как обычное число секунд
                long seconds = Long.parseLong(str);
                return Duration.ofSeconds(seconds);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.DURATION;
    }
    
    @Override
    public String asString() {
        return originalValue;
    }
    
    @Override
    public Number asNumber() {
        return value != null ? value.toMillis() : null;
    }
    
    @Override
    public Boolean asBoolean() {
        if (value == null) return null;
        return !value.isZero();
    }
    
    @Override
    public Duration asDuration() {
        return value;
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