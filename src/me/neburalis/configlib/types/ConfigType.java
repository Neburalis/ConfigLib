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

/**
 * Перечисление всех поддерживаемых типов данных конфига
 */
public enum ConfigType {
    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean"),
    DURATION("duration"),
    URL("url"),
    EMAIL("email"),
    LARGE_NUMBER("large_number"),
    SCIENTIFIC_NUMBER("scientific_number"),
    ARRAY("array");
    
    private final String name;
    
    ConfigType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 