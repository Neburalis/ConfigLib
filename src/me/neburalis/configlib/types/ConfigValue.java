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
 * Базовый интерфейс для всех типов значений конфига
 */
public interface ConfigValue {
    /**
     * Получить тип значения
     */
    ConfigType getType();
    
    /**
     * Получить значение как строку
     */
    String asString();
    
    /**
     * Получить значение как число
     */
    Number asNumber();
    
    /**
     * Получить значение как булево
     */
    Boolean asBoolean();
    
    /**
     * Получить значение как время
     */
    Duration asDuration();
    
    /**
     * Получить значение как URL
     */
    URL asUrl();
    
    /**
     * Получить значение как email
     */
    String asEmail();
    
    /**
     * Получить исходное значение
     */
    Object getValue();
    
    /**
     * Проверить, является ли значение null
     */
    boolean isNull();
} 