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
 * Перечисление всех поддерживаемых типов данных конфигурации.
 * 
 * <p>Этот enum определяет все типы данных, которые поддерживает библиотека.
 * Каждый тип имеет строковое представление, используемое в XML файлах.</p>
 * 
 * <p><strong>Поддерживаемые типы:</strong></p>
 * <ul>
 *   <li><strong>STRING:</strong> обычные строки</li>
 *   <li><strong>NUMBER:</strong> целые и дробные числа</li>
 *   <li><strong>BOOLEAN:</strong> логические значения</li>
 *   <li><strong>DURATION:</strong> временные интервалы</li>
 *   <li><strong>URL:</strong> веб-адреса</li>
 *   <li><strong>EMAIL:</strong> email адреса</li>
 *   <li><strong>LARGE_NUMBER:</strong> большие числа с суффиксами (1k, 1m, 1b)</li>
 *   <li><strong>SCIENTIFIC_NUMBER:</strong> научная нотация (1e-8)</li>
 *   <li><strong>ARRAY:</strong> типизированные массивы</li>
 * </ul>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
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
    
    /**
     * Получить строковое представление типа.
     * Используется в XML файлах для указания типа значения.
     * 
     * @return строковое имя типа
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 