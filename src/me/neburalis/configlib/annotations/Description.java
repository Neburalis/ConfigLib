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

package me.neburalis.configlib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для описания значений в конфигурации.
 * 
 * <p>Эта аннотация позволяет добавлять описания к полям и методам,
 * которые будут сохранены в XML файле как атрибуты description.
 * Это полезно для документирования конфигурации.</p>
 * 
 * <p><strong>Использование:</strong></p>
 * <pre>{@code
 * @Description("Порт сервера для HTTP соединений")
 * private int serverPort = 8080;
 * 
 * @Description("Максимальное количество одновременных соединений")
 * public void setMaxConnections(int max) {
 *     // реализация
 * }
 * }</pre>
 * 
 * <p><strong>Почему используется аннотация:</strong></p>
 * <ul>
 *   <li>Декларативность: описание привязано к коду</li>
 *   <li>Автоматизация: можно автоматически генерировать документацию</li>
 *   <li>Типобезопасность: проверяется на этапе компиляции</li>
 * </ul>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Description {
    /**
     * Описание значения.
     * 
     * @return строковое описание
     */
    String value();
} 