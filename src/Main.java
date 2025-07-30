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

import me.neburalis.configlib.Config;
import me.neburalis.configlib.types.ConfigValue;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        // Создаем конфиг с XML файлом
        Config config = new Config("test_config.xml");

        // Устанавливаем значения с иерархической структурой
        config.set("server.host", "localhost", "Хост сервера");
        config.set("server.port", 8080, "Порт сервера");
        config.set("server.enabled", true, "Включен ли сервер");
        config.set("server.timeout", "30s", "Таймаут соединения");
        config.set("server.max_connections", "1k", "Максимальное количество соединений");
        config.set("server.memory_limit", "1e9", "Лимит памяти в байтах");

        // Массивы разных типов
        config.set("notify.email", new me.neburalis.configlib.types.ArrayValue(
                new String[]{"admin@example.com", "admin2@example.com", "admin3@example.com"}, "email"), "Email для уведомлений");
        config.set("notify.webhook_urls", new me.neburalis.configlib.types.ArrayValue(
                new String[]{"https://webhook1.example.com", "https://webhook2.example.com"}, "url"), "Webhook URLs");
        config.set("notify.ports", new me.neburalis.configlib.types.ArrayValue(
                new String[]{"8080", "9090", "3000"}, "number"), "Порты для мониторинга");
        config.set("notify.enabled_features", new me.neburalis.configlib.types.ArrayValue(
                new String[]{"true", "false", "true"}, "boolean"), "Включенные функции");
        config.set("notify.timeouts", new me.neburalis.configlib.types.ArrayValue(
                new String[]{"30s", "60s", "120s"}, "duration"), "Таймауты уведомлений");

        // Вложенная структура
        config.set("database.name", "mydb", "Имя базы данных");
        config.set("database.url", "127.0.0.1", "URL базы данных");
        config.set("database.port", 5342, "Порт базы данных");
        config.set("database.connection_pool_size", 10, "Размер пула соединений");
        config.set("database.connection.timeout", "5s", "Таймаут соединения с БД");
        config.set("database.connection.retry_count", 3, "Количество попыток переподключения");

        // Дополнительные секции
        config.set("logging.level", "INFO", "Уровень логирования");
        config.set("logging.file", "app.log", "Файл логов");
        config.set("logging.max_size", "100m", "Максимальный размер файла логов");

        // Читаем значения
        System.out.println("=== Чтение конфигурации ===");
        System.out.println("Server Host: " + config.getString("server.host"));
        System.out.println("Server Port: " + config.getNumber("server.port"));
        System.out.println("Server Enabled: " + config.getBoolean("server.enabled"));
        System.out.println("Server Timeout: " + config.getDuration("server.timeout"));
        System.out.println("Server Max Connections: " + config.getString("server.max_connections"));
        System.out.println("Server Memory Limit: " + config.getString("server.memory_limit"));

        System.out.println("\n=== Массивы (типизированные) ===");
        me.neburalis.configlib.types.ConfigValue[] emails = config.getArray("notify.email");
        if (emails != null) {
            System.out.println("Notify Emails (массив EmailValue):");
            for (int i = 0; i < emails.length; i++) {
                System.out.println("  [" + i + "] " + emails[i].asEmail() + " (тип: " + emails[i].getClass().getSimpleName() + ")");
            }
        }

        me.neburalis.configlib.types.ConfigValue[] webhooks = config.getArray("notify.webhook_urls");
        if (webhooks != null) {
            System.out.println("Webhook URLs (массив UrlValue):");
            for (int i = 0; i < webhooks.length; i++) {
                System.out.println("  [" + i + "] " + webhooks[i].asUrl() + " (тип: " + webhooks[i].getClass().getSimpleName() + ")");
            }
        }

        me.neburalis.configlib.types.ConfigValue[] ports = config.getArray("notify.ports");
        if (ports != null) {
            System.out.println("Notify Ports (массив NumberValue):");
            for (int i = 0; i < ports.length; i++) {
                System.out.println("  [" + i + "] " + ports[i].asNumber() + " (тип: " + ports[i].getClass().getSimpleName() + ")");
            }
        }

        // Получение отдельных элементов массива как типизированные объекты
        me.neburalis.configlib.types.ConfigValue firstEmail = config.getArrayElement("notify.email", 0);
        me.neburalis.configlib.types.ConfigValue secondWebhook = config.getArrayElement("notify.webhook_urls", 1);

        System.out.println("Первый email (EmailValue): " + (firstEmail != null ? firstEmail.asEmail() : "null"));
        System.out.println("Второй webhook (UrlValue): " + (secondWebhook != null ? secondWebhook.asUrl() : "null"));
        System.out.println("Размер массива портов: " + config.getArraySize("notify.ports"));

        // Для обратной совместимости - получение как строки
        System.out.println("\n=== Обратная совместимость (строки) ===");
        String[] emailStrings = config.getStringArray("notify.email");
        if (emailStrings != null) {
            System.out.println("Emails как строки: " + String.join(", ", emailStrings));
        }
        System.out.println("Первый email как строка: " + config.getArrayElementString("notify.email", 0));

        System.out.println("\n=== База данных ===");
        System.out.println("DB Name: " + config.getString("database.name"));
        System.out.println("DB URL: " + config.getString("database.url"));
        System.out.println("DB Port: " + config.getNumber("database.port"));
        System.out.println("DB Pool Size: " + config.getNumber("database.connection_pool_size"));
        System.out.println("DB Connection Timeout: " + config.getDuration("database.connection.timeout"));
        System.out.println("DB Retry Count: " + config.getNumber("database.connection.retry_count"));

        System.out.println("\n=== Логирование ===");
        System.out.println("Log Level: " + config.getString("logging.level"));
        System.out.println("Log File: " + config.getString("logging.file"));
        System.out.println("Log Max Size: " + config.getString("logging.max_size"));

        // Перезагружаем конфиг
        System.out.println("\n=== Перезагрузка конфигурации ===");
        config.reload();

        // Проверяем, что значения сохранились
        System.out.println("Server Host после перезагрузки: " + config.getString("server.host"));
        System.out.println("DB Name после перезагрузки: " + config.getString("database.name"));

        // Проверяем существование путей
        System.out.println("\n=== Проверка путей ===");
        System.out.println("Has server.host: " + config.hasPath("server.host"));
        System.out.println("Has database.connection.timeout: " + config.hasPath("database.connection.timeout"));
        System.out.println("Has nonexistent: " + config.hasPath("nonexistent"));

        // Получаем все пути
        System.out.println("\n=== Все пути в конфигурации ===");
        for (String path : config.getPaths()) {
            System.out.println("  " + path);
        }

        // Останавливаем отслеживание
        config.stopWatching();

        System.out.println("\n=== Конфигурация сохранена в XML формате ===");
        System.out.println("Файл: " + config.getConfigFile());
    }
}