# ConfigLib - Библиотека для работы с конфигурациями

[![](https://jitpack.io/v/Neburalis/ConfigLib.svg)](https://jitpack.io/#Neburalis/ConfigLib)
![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)
[![JavaDoc](https://img.shields.io/badge/JavaDoc-API-blue.svg)](https://javadoc.jitpack.io/com/github/Neburalis/ConfigLib/latest/javadoc/)

Библиотека для работы с конфигурациями на Java с поддержкой статической типизации, горячего обновления и сложных типов данных в XML формате.

## Возможности

- ✅ **Изменяемые конфиги** - можно изменять из кода и сохранять на диск
- ✅ **Горячее обновление** - автоматическое отслеживание изменений файла
- ✅ **Сложные пути** - поддержка вложенных путей (server.connection.timeout)
- ✅ **Статическая типизация** - каждый тип значения явно указан и не может быть изменен
- ✅ **XML формат** - структурированный формат с поддержкой иерархии и атрибутов
- ✅ **Типизированные массивы** - массивы с элементами определенного типа
- ✅ **Дополнительные типы данных**:
  - Большие числа (1k, 1m, 1b, 1t, 1p)
  - Научная нотация (1e-8, 1e15)
  - Время (1ms, 1s, 1m, 1h, 1d, 1w, 1mo, 1q, 1y)
  - URL
  - Email
- ✅ **Описания** - поддержка комментариев и описаний значений

## 🚀 Установка

### JitPack (Рекомендуемый способ)

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Neburalis</groupId>
        <artifactId>ConfigLib</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
```

#### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Neburalis:ConfigLib:v0.1.0'
}
```

## Быстрый старт

```java
import me.neburalis.configlib.Config;

// Создание конфига (XML файл)
Config config = new Config("config.xml");

// Установка значений
config.set("app.name", "MyApp", "Название приложения");
config.set("app.port", 8080, "Порт приложения");
config.set("memory.max", "1g", "Максимальный размер памяти");
config.set("timeout.connection", "30s", "Таймаут соединения");

// Чтение значений
String appName = config.getString("app.name");
int port = config.getNumber("app.port").intValue();
long memoryMax = config.getNumber("memory.max").longValue();
Duration timeout = config.getDuration("timeout.connection");
```

## Поддерживаемые типы данных

### 1. Строки
```java
config.set("app.name", "MyApp");
String name = config.getString("app.name");
```

### 2. Числа
```java
config.set("app.port", 8080);
Number port = config.getNumber("app.port");
```

### 3. Булевы значения
```java
config.set("app.debug", true);
Boolean debug = config.getBoolean("app.debug");
```

### 4. Большие числа
```java
config.set("memory.max", "1k");  // 1 000
config.set("memory.max", "1m");  // 1 000 000
config.set("memory.max", "1b");  // 1 000 000 000
config.set("memory.max", "1t");  // 1 000 000 000 000
config.set("memory.max", "1p");  // 1 000 000 000 000 000
```

### 5. Научная нотация
```java
config.set("precision", "1e-8");
config.set("max_value", "1e15");
```

### 6. Время
```java
config.set("timeout.connection", "30s");
config.set("timeout.read", "5m");
config.set("backup.interval", "1d");
config.set("session.duration", "1w");
config.set("maintenance.period", "1mo");
config.set("quarterly.report", "1q");  // 3 месяца
config.set("yearly.backup", "1y");
```

### 7. URL
```java
config.set("api.endpoint", "https://api.example.com");
URL endpoint = config.getUrl("api.endpoint");
```

### 8. Email
```java
config.set("admin.email", "admin@example.com");
String email = config.getEmail("admin.email");
```

### 9. Типизированные массивы
```java
import me.neburalis.configlib.types.ArrayValue;
import me.neburalis.configlib.types.ConfigValue;

// Массив email адресов
config.set("notify.emails", new ArrayValue(
    new String[]{"admin@example.com", "user@example.com"}, "email"), 
    "Email для уведомлений");

// Массив URL
config.set("webhooks", new ArrayValue(
    new String[]{"https://hook1.com", "https://hook2.com"}, "url"), 
    "Webhook URLs");

// Массив чисел
config.set("ports", new ArrayValue(
    new String[]{"8080", "9090", "3000"}, "number"), 
    "Порты сервисов");

// Чтение массивов
ConfigValue[] emails = config.getArray("notify.emails");
for (int i = 0; i < emails.length; i++) {
    System.out.println(emails[i].asEmail());
}

// Получение отдельных элементов
ConfigValue firstEmail = config.getArrayElement("notify.emails", 0);
String emailStr = firstEmail.asEmail();

// Размер массива
int size = config.getArraySize("notify.emails");

// Для обратной совместимости - как строки
String[] emailStrings = config.getStringArray("notify.emails");
```

## Сложные пути

Библиотека поддерживает вложенные пути любой глубины:

```java
config.set("database.connection.pool.max_size", 100);
config.set("database.connection.pool.min_size", 10);
config.set("database.connection.timeout", "5s");
config.set("server.ssl.certificate.path", "/path/to/cert");
```

## Описания значений

Можно добавлять описания к значениям, которые сохраняются в XML файл:

```java
config.set("app.name", "MyApp", "Название приложения");
config.set("app.port", 8080, "Порт приложения");
```

## Горячее обновление

Библиотека автоматически отслеживает изменения конфигурационного файла и перезагружает значения:

```java
Config config = new Config("config.xml");
// Конфиг автоматически отслеживает изменения файла

// Остановка отслеживания
config.stopWatching();
```

## API

### Основные методы

- `Config(String filePath)` - создание конфига
- `set(String path, Object value)` - установка значения
- `set(String path, Object value, String description)` - установка значения с описанием
- `get(String path)` - получение значения как ConfigValue
- `getString(String path)` - получение строкового значения
- `getNumber(String path)` - получение числового значения
- `getBoolean(String path)` - получение булевого значения
- `getDuration(String path)` - получение временного значения
- `getUrl(String path)` - получение URL
- `getEmail(String path)` - получение email

### Методы для работы с массивами

- `getArray(String path)` - получение массива как ConfigValue[]
- `getStringArray(String path)` - получение массива как String[] (обратная совместимость)
- `getArrayElement(String path, int index)` - получение элемента массива как ConfigValue
- `getArrayElementString(String path, int index)` - получение элемента как String
- `getArraySize(String path)` - получение размера массива

### Служебные методы

- `hasPath(String path)` - проверка существования пути
- `getPaths()` - получение всех путей
- `reload()` - перезагрузка конфига
- `stopWatching()` - остановка отслеживания

## Формат конфигурационного файла (XML)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <server>
    <host type="string" description="Хост сервера">localhost</host>
    <port type="number" description="Порт сервера">8080</port>
    <enabled type="boolean">true</enabled>
    <timeout type="duration" description="Таймаут соединения">30s</timeout>
    <max_connections type="large_number">1k</max_connections>
    <memory_limit type="scientific_number">1e9</memory_limit>
  </server>
  
  <notify>
    <emails type="array" description="Email для уведомлений">
      <li type="email">admin@example.com</li>
      <li type="email">user@example.com</li>
    </emails>
    <webhooks type="array" description="Webhook URLs">
      <li type="url">https://hook1.example.com</li>
      <li type="url">https://hook2.example.com</li>
    </webhooks>
  </notify>
  
  <database>
    <name type="string">mydb</name>
    <connection>
      <timeout type="duration">5s</timeout>
      <pool_size type="number">10</pool_size>
    </connection>
  </database>
</configuration>
```

## Особенности XML формата

- **Иерархическая структура**: поддержка вложенных элементов любой глубины
- **Атрибут type**: каждый элемент имеет явно указанный тип
- **Атрибут description**: описания сохраняются как атрибуты
- **Массивы**: реализованы через элементы с `type="array"` и дочерними `<li>` элементами
- **Типизация элементов массива**: каждый `<li>` элемент имеет свой тип

## Пример использования

Смотрите файл `src/Main.java` для полного примера использования библиотеки с различными типами данных и массивами.

## Требования

- Java 11 или выше
- Поддержка NIO.2 для отслеживания файлов
- XML парсер (включен в стандартную библиотеку Java)

## Лицензия

Этот проект лицензирован под [GNU General Public License v3.0](LICENSE).