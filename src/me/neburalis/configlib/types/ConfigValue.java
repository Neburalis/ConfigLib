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
 * Базовый интерфейс для всех типов значений конфигурации.
 * 
 * <p>Этот интерфейс определяет единый контракт для всех типов значений
 * в конфигурации. Он обеспечивает типобезопасность и единообразный
 * доступ к данным независимо от их исходного типа.</p>
 * 
 * <p><strong>Архитектурные особенности:</strong></p>
 * <ul>
 *   <li><strong>Полиморфизм:</strong> каждый тип значения реализует этот интерфейс</li>
 *   <li><strong>Типобезопасность:</strong> методы преобразования предотвращают ClassCastException</li>
 *   <li><strong>Единообразие:</strong> все типы имеют одинаковый API</li>
 *   <li><strong>Метаданные:</strong> каждый тип знает о себе и может валидироваться</li>
 * </ul>
 * 
 * <p><strong>Почему используется интерфейс, а не абстрактный класс:</strong></p>
 * <ul>
 *   <li>Гибкость: можно реализовать несколько интерфейсов</li>
 *   <li>Простота: нет необходимости наследовать общую реализацию</li>
 *   <li>Тестирование: легко создать mock объекты</li>
 * </ul>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ConfigValue {
    /**
     * Получить тип значения.
     * 
     * @return ConfigType, представляющий тип этого значения
     */
    ConfigType getType();
    
    /**
     * Получить значение как строку.
     * Все типы значений могут быть преобразованы в строку.
     * 
     * @return строковое представление значения
     */
    String asString();
    
    /**
     * Получить значение как число.
     * Если значение не является числом, может вернуть null или 0.
     * 
     * @return числовое представление значения или null
     */
    Number asNumber();
    
    /**
     * Получить значение как булево.
     * Строки "true"/"false" и числа (0 = false, другое = true) преобразуются корректно.
     * 
     * @return булево представление значения или null
     */
    Boolean asBoolean();
    
    /**
     * Получить значение как время.
     * Поддерживает строки с единицами времени (ms, s, m, h, d, w, mo, q, y).
     * 
     * @return Duration представление значения или null
     */
    Duration asDuration();
    
    /**
     * Получить значение как URL.
     * Проверяет корректность URL формата.
     * 
     * @return URL представление значения или null
     * @throws IllegalArgumentException если значение не является корректным URL
     */
    URL asUrl();
    
    /**
     * Получить значение как email.
     * Проверяет корректность email формата.
     * 
     * @return email адрес или null
     * @throws IllegalArgumentException если значение не является корректным email
     */
    String asEmail();
    
    /**
     * Получить исходное значение.
     * Возвращает значение в его оригинальном типе.
     * 
     * @return исходное значение
     */
    Object getValue();
    
    /**
     * Проверить, является ли значение null.
     * 
     * @return true, если значение равно null
     */
    boolean isNull();
} 