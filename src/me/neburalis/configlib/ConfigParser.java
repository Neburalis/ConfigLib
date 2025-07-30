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

package me.neburalis.configlib;

import me.neburalis.configlib.types.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Парсер конфигурационных файлов в XML формате.
 * 
 * <p>Этот класс отвечает за преобразование между XML форматом и внутренним
 * представлением конфигурации. Он поддерживает:</p>
 * <ul>
 *   <li><strong>Иерархическую структуру:</strong> вложенные элементы любой глубины</li>
 *   <li><strong>Типизированные значения:</strong> автоматическое определение типа по содержимому</li>
 *   <li><strong>Массивы:</strong> специальный формат для типизированных массивов</li>
 *   <li><strong>Метаданные:</strong> сохранение описаний как XML атрибутов</li>
 * </ul>
 * 
 * <p><strong>Архитектурные особенности:</strong></p>
 * <ul>
 *   <li><strong>Регулярные выражения:</strong> используются для быстрого определения типа значения</li>
 *   <li><strong>Рекурсивный парсинг:</strong> позволяет обрабатывать структуры любой сложности</li>
 *   <li><strong>Обработка ошибок:</strong> продолжает работу при ошибках в отдельных элементах</li>
 *   <li><strong>Валидация:</strong> проверяет корректность URL, email и других специальных типов</li>
 * </ul>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConfigParser {
    private static final Pattern LARGE_NUMBER_PATTERN = Pattern.compile("^\\d+[kmbtp]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCIENTIFIC_NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?[eE][+-]?\\d+$");
    private static final Pattern DURATION_PATTERN = Pattern.compile("^\\d+(ms|s|m|h|d|w|mo|q|y)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://.*");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Парсить конфигурационный XML файл.
     * 
     * <p>Этот метод преобразует XML строку в Map с типизированными значениями.
     * Использует стандартный DOM парсер для обработки XML структуры.</p>
     * 
     * <p><strong>Почему используется DOM, а не SAX:</strong></p>
     * <ul>
     *   <li>Простота: DOM проще в использовании для небольших файлов</li>
     *   <li>Гибкость: позволяет легко навигировать по структуре</li>
     *   <li>Обработка ошибок: можно продолжить работу при ошибках в отдельных элементах</li>
     * </ul>
     * 
     * @param content XML строка для парсинга
     * @return Map с путями в качестве ключей и типизированными значениями
     */
    public Map<String, ConfigValue> parse(String content) {
        Map<String, ConfigValue> result = new HashMap<>();

        if (content == null || content.trim().isEmpty()) {
            return result;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(content.getBytes()));

            Element root = document.getDocumentElement();
            parseElement(root, "", result);

        } catch (Exception e) {
            System.err.println("Error parsing XML config: " + e.getMessage());
        }

        return result;
    }

    /**
     * Рекурсивно парсить XML элементы
     */
    private void parseElement(Element element, String currentPath, Map<String, ConfigValue> result) {
        String elementName = element.getTagName();
        String path = currentPath.isEmpty() ? elementName : currentPath + "." + elementName;

        // Проверяем, является ли элемент массивом
        if (isArrayElement(element)) {
            parseArrayElement(element, path, result);
            return;
        }

        // Проверяем, есть ли дочерние элементы
        NodeList children = element.getChildNodes();
        boolean hasChildElements = false;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                hasChildElements = true;
                parseElement((Element) child, path, result);
            }
        }

        // Если нет дочерних элементов, это листовой узел
        if (!hasChildElements) {
            String value = element.getTextContent().trim();
            String type = element.getAttribute("type");
            String description = element.getAttribute("description");

            ConfigValue configValue = parseValueWithType(value, type);
            if (configValue != null) {
                result.put(path, configValue);
            }
        }
    }

    /**
     * Проверить, является ли элемент массивом
     */
    private boolean isArrayElement(Element element) {
        // Проверяем атрибут type="array" или наличие множественных дочерних элементов с одинаковыми именами
        if ("array".equals(element.getAttribute("type"))) {
            return true;
        }

        // Проверяем, есть ли множественные дочерние элементы с одинаковыми именами
        Map<String, Integer> childCounts = new HashMap<>();
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getNodeName();
                childCounts.put(childName, childCounts.getOrDefault(childName, 0) + 1);
            }
        }

        // Если есть элемент, который встречается больше одного раза, считаем это массивом
        for (int count : childCounts.values()) {
            if (count > 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Парсить элемент массива
     */
    private void parseArrayElement(Element element, String path, Map<String, ConfigValue> result) {
        NodeList children = element.getChildNodes();
        List<String> arrayValues = new ArrayList<>();
        String elementType = element.getAttribute("type");

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String value = childElement.getTextContent().trim();
                if (!value.isEmpty()) {
                    arrayValues.add(value);
                }
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent().trim();
                if (!text.isEmpty()) {
                    // Разбиваем текст на строки для поддержки многострочных массивов
                    String[] lines = text.split("\n");
                    for (String line : lines) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            arrayValues.add(line);
                        }
                    }
                }
            }
        }

        // Сохраняем массив как ArrayValue
        if (!arrayValues.isEmpty()) {
            // Определяем тип элементов массива, если не указан
            if (elementType.isEmpty() || "array".equals(elementType)) {
                elementType = determineArrayElementType(arrayValues);
            }

            ConfigValue configValue = new ArrayValue(arrayValues, elementType);
            result.put(path, configValue);
        }
    }

    /**
     * Определить тип элементов массива на основе содержимого
     */
    private String determineArrayElementType(List<String> values) {
        if (values.isEmpty()) {
            return "string";
        }

        // Проверяем первый элемент для определения типа
        String firstValue = values.get(0).trim();

        // Проверяем на email
        if (EMAIL_PATTERN.matcher(firstValue).matches()) {
            return "email";
        }

        // Проверяем на URL
        if (URL_PATTERN.matcher(firstValue).matches()) {
            return "url";
        }

        // Проверяем на boolean
        if ("true".equalsIgnoreCase(firstValue) || "false".equalsIgnoreCase(firstValue)) {
            return "boolean";
        }

        // Проверяем на большие числа
        if (LARGE_NUMBER_PATTERN.matcher(firstValue).matches()) {
            return "large_number";
        }

        // Проверяем на числа в научной нотации
        if (SCIENTIFIC_NUMBER_PATTERN.matcher(firstValue).matches()) {
            return "scientific_number";
        }

        // Проверяем на время
        if (DURATION_PATTERN.matcher(firstValue).matches()) {
            return "duration";
        }

        // Проверяем на обычное число
        try {
            if (firstValue.contains(".")) {
                Double.parseDouble(firstValue);
            } else {
                Long.parseLong(firstValue);
            }
            return "number";
        } catch (NumberFormatException e) {
            // Не число, значит строка
        }

        return "string";
    }

    /**
     * Парсить отдельное значение с указанным типом
     */
    public ConfigValue parseValueWithType(String value, String type) {
        if (value == null || value.trim().isEmpty()) {
            return new StringValue(null);
        }

        String strValue = value.trim();

        // Убираем кавычки если есть
        if (strValue.startsWith("\"") && strValue.endsWith("\"")) {
            strValue = strValue.substring(1, strValue.length() - 1);
        }

        // Проверяем на null
        if ("null".equalsIgnoreCase(strValue)) {
            return new StringValue(null);
        }

        // Если тип указан, используем его
        if (type != null && !type.isEmpty()) {
            switch (type.toLowerCase()) {
                case "string":
                    return new StringValue(strValue);
                case "boolean":
                    return new BooleanValue(Boolean.parseBoolean(strValue));
                case "number":
                    return new NumberValue(parseNumber(strValue));
                case "duration":
                    return new DurationValue(strValue);
                case "url":
                    return new UrlValue(strValue);
                case "email":
                    return new EmailValue(strValue);
                case "large_number":
                    return new LargeNumberValue(strValue);
                case "scientific_number":
                    return new ScientificNumberValue(strValue);
                case "array":
                    // Разбиваем строку на элементы массива
                    String[] arrayElements = strValue.split(",");
                    List<String> arrayValues = Arrays.asList(arrayElements);
                    String elementType = determineArrayElementType(arrayValues);
                    return new ArrayValue(arrayValues, elementType);
            }
        }

        // Если тип не указан, определяем автоматически
        return parseValue(strValue);
    }

    /**
     * Парсить отдельное значение (автоопределение типа)
     */
    public ConfigValue parseValue(Object value) {
        if (value == null) {
            return new StringValue(null);
        }

        String strValue = value.toString().trim();

        // Убираем кавычки если есть
        if (strValue.startsWith("\"") && strValue.endsWith("\"")) {
            strValue = strValue.substring(1, strValue.length() - 1);
        }

        // Проверяем на null
        if ("null".equalsIgnoreCase(strValue)) {
            return new StringValue(null);
        }

        // Проверяем на boolean
        if ("true".equalsIgnoreCase(strValue) || "false".equalsIgnoreCase(strValue)) {
            return new BooleanValue(Boolean.parseBoolean(strValue));
        }

        // Проверяем на большие числа (1k, 1m, 1b, 1t, 1p)
        if (LARGE_NUMBER_PATTERN.matcher(strValue).matches()) {
            return new LargeNumberValue(strValue);
        }

        // Проверяем на числа в научной нотации (1e-8, 1e15)
        if (SCIENTIFIC_NUMBER_PATTERN.matcher(strValue).matches()) {
            return new ScientificNumberValue(strValue);
        }

        // Проверяем на время (1ms, 1s, 1m, 1h, 1d, 1w, 1mo, 1q, 1y)
        if (DURATION_PATTERN.matcher(strValue).matches()) {
            return new DurationValue(strValue);
        }

        // Проверяем на URL
        if (URL_PATTERN.matcher(strValue).matches()) {
            return new UrlValue(strValue);
        }

        // Проверяем на email
        if (EMAIL_PATTERN.matcher(strValue).matches()) {
            return new EmailValue(strValue);
        }

        // Проверяем на обычное число
        try {
            if (strValue.contains(".")) {
                Double.parseDouble(strValue);
            } else {
                Long.parseLong(strValue);
            }
            return new NumberValue(parseNumber(strValue));
        } catch (NumberFormatException e) {
            // Не число, значит строка
        }

        // По умолчанию считаем строкой
        return new StringValue(strValue);
    }

    /**
     * Создать XML строку из конфигурации
     */
    public String toXml(Map<String, ConfigValue> values, Map<String, String> descriptions) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("configuration");
            document.appendChild(root);

            // Группируем значения по иерархии
            Map<String, Map<String, Object>> hierarchy = buildHierarchy(values, descriptions);

            // Создаем XML структуру
            for (Map.Entry<String, Map<String, Object>> entry : hierarchy.entrySet()) {
                String section = entry.getKey();
                Map<String, Object> sectionValues = entry.getValue();

                Element sectionElement = document.createElement(section);
                root.appendChild(sectionElement);

                for (Map.Entry<String, Object> valueEntry : sectionValues.entrySet()) {
                    String key = valueEntry.getKey();
                    Object value = valueEntry.getValue();

                    if (value instanceof Map) {
                        // Это вложенная секция
                        createNestedElement(document, sectionElement, key, (Map<String, Object>) value);
                    } else {
                        // Это простое значение
                        ConfigValue configValue = (ConfigValue) value;
                        String description = descriptions.get(section + "." + key);

                        if (configValue instanceof ArrayValue) {
                            // Создаем элемент массива
                            createArrayElement(document, sectionElement, key, (ArrayValue) configValue, description);
                        } else {
                            // Создаем обычный элемент
                            Element valueElement = document.createElement(key);
                            String type = getTypeName(configValue);
                            valueElement.setAttribute("type", type);

                            if (description != null && !description.isEmpty()) {
                                valueElement.setAttribute("description", description);
                            }

                            valueElement.setTextContent(configValue.toString());
                            sectionElement.appendChild(valueElement);
                        }
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            System.err.println("Error creating XML: " + e.getMessage());
            return "";
        }
    }

    /**
     * Создать вложенный элемент
     */
    private void createNestedElement(Document document, Element parent, String name, Map<String, Object> values) {
        Element element = document.createElement(name);
        parent.appendChild(element);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                createNestedElement(document, element, key, (Map<String, Object>) value);
            } else {
                Element valueElement = document.createElement(key);
                ConfigValue configValue = (ConfigValue) value;

                valueElement.setAttribute("type", getTypeName(configValue));
                valueElement.setTextContent(configValue.toString());
                element.appendChild(valueElement);
            }
        }
    }

    /**
     * Создать элемент массива в XML
     */
    private void createArrayElement(Document document, Element parent, String name, ArrayValue arrayValue, String description) {
        Element arrayElement = document.createElement(name);
        arrayElement.setAttribute("type", "array");

        if (description != null && !description.isEmpty()) {
            arrayElement.setAttribute("description", description);
        }

        ConfigValue[] values = arrayValue.getArray();
        String elementType = arrayValue.getElementType();

        for (ConfigValue value : values) {
            Element liElement = document.createElement("li");
            liElement.setAttribute("type", elementType);
            liElement.setTextContent(value.toString());
            arrayElement.appendChild(liElement);
        }

        parent.appendChild(arrayElement);
    }

    /**
     * Построить иерархию из плоского списка ключей
     */
    private Map<String, Map<String, Object>> buildHierarchy(Map<String, ConfigValue> values, Map<String, String> descriptions) {
        Map<String, Map<String, Object>> hierarchy = new HashMap<>();

        for (Map.Entry<String, ConfigValue> entry : values.entrySet()) {
            String key = entry.getKey();
            ConfigValue value = entry.getValue();

            String[] parts = key.split("\\.", 2);
            if (parts.length == 1) {
                // Корневой элемент
                if (!hierarchy.containsKey("root")) {
                    hierarchy.put("root", new HashMap<>());
                }
                hierarchy.get("root").put(parts[0], value);
            } else {
                // Вложенный элемент
                String section = parts[0];
                String subKey = parts[1];

                if (!hierarchy.containsKey(section)) {
                    hierarchy.put(section, new HashMap<>());
                }

                // Проверяем, есть ли еще вложенность
                String[] subParts = subKey.split("\\.", 2);
                if (subParts.length == 1) {
                    hierarchy.get(section).put(subKey, value);
                } else {
                    // Создаем вложенную структуру
                    createNestedStructure(hierarchy.get(section), subKey, value);
                }
            }
        }

        return hierarchy;
    }

    /**
     * Создать вложенную структуру
     */
    private void createNestedStructure(Map<String, Object> parent, String key, ConfigValue value) {
        String[] parts = key.split("\\.", 2);
        String currentKey = parts[0];

        if (parts.length == 1) {
            parent.put(currentKey, value);
        } else {
            if (!parent.containsKey(currentKey)) {
                parent.put(currentKey, new HashMap<String, Object>());
            }

            @SuppressWarnings("unchecked") Map<String, Object> nested = (Map<String, Object>) parent.get(currentKey);
            createNestedStructure(nested, parts[1], value);
        }
    }

    /**
     * Получить имя типа для ConfigValue
     */
    private String getTypeName(ConfigValue value) {
        if (value instanceof StringValue) return "string";
        if (value instanceof BooleanValue) return "boolean";
        if (value instanceof NumberValue) return "number";
        if (value instanceof DurationValue) return "duration";
        if (value instanceof UrlValue) return "url";
        if (value instanceof EmailValue) return "email";
        if (value instanceof LargeNumberValue) return "large_number";
        if (value instanceof ScientificNumberValue) return "scientific_number";
        if (value instanceof ArrayValue) return "array";
        return "string";
    }

    /**
     * Получить имя типа для XML с учетом массивов
     */
    private String getTypeNameForXml(ConfigValue value, String key) {
        String baseType = getTypeName(value);

        // Если это ArrayValue, возвращаем тип элементов массива
        if (value instanceof ArrayValue) {
            return ((ArrayValue) value).getElementType();
        }

        // Если это строка и содержит запятые, это может быть массив
        if (baseType.equals("string") && value.toString().contains(",")) {
            String[] parts = value.toString().split(",");
            if (parts.length > 1) {
                // Определяем тип элементов массива
                List<String> arrayValues = Arrays.asList(parts);
                return determineArrayElementType(arrayValues);
            }
        }

        return baseType;
    }

    private Number parseNumber(String str) {
        if (str.contains(".")) {
            return Double.parseDouble(str);
        } else {
            return Long.parseLong(str);
        }
    }
} 