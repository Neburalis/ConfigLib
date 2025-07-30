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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Реализация больших числовых значений конфига (1k, 1m, 1b, 1t, 1p)
 */
public class LargeNumberValue implements ConfigValue {
    private final String originalValue;
    private final Number value;
    
    public LargeNumberValue(String value) {
        this.originalValue = value;
        this.value = parseLargeNumber(value);
    }
    
    private Number parseLargeNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        
        str = str.trim().toLowerCase();
        
        try {
            if (str.endsWith("p")) {
                return new BigDecimal(str.substring(0, str.length() - 1))
                    .multiply(new BigDecimal("1000000000000000")); // 10^15
            } else if (str.endsWith("t")) {
                return new BigDecimal(str.substring(0, str.length() - 1))
                    .multiply(new BigDecimal("1000000000000")); // 10^12
            } else if (str.endsWith("b")) {
                return new BigDecimal(str.substring(0, str.length() - 1))
                    .multiply(new BigDecimal("1000000000")); // 10^9
            } else if (str.endsWith("m")) {
                return new BigDecimal(str.substring(0, str.length() - 1))
                    .multiply(new BigDecimal("1000000")); // 10^6
            } else if (str.endsWith("k")) {
                return new BigDecimal(str.substring(0, str.length() - 1))
                    .multiply(new BigDecimal("1000")); // 10^3
            } else {
                return new BigDecimal(str);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.LARGE_NUMBER;
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