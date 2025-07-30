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

import java.nio.file.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Отслеживание изменений конфигурационного файла.
 * 
 * <p>Этот класс обеспечивает автоматическое перезагружение конфигурации
 * при изменении файла. Использует NIO.2 WatchService для эффективного
 * отслеживания изменений файловой системы.</p>
 * 
 * <p><strong>Архитектурные особенности:</strong></p>
 * <ul>
 *   <li><strong>Асинхронность:</strong> работает в отдельном потоке</li>
 *   <li><strong>Эффективность:</strong> использует системные события файловой системы</li>
 *   <li><strong>Безопасность:</strong> предотвращает ложные срабатывания</li>
 *   <li><strong>Управляемость:</strong> можно остановить и запустить отслеживание</li>
 * </ul>
 * 
 * <p><strong>Почему используется WatchService:</strong></p>
 * <ul>
 *   <li>Производительность: не нужно постоянно опрашивать файл</li>
 *   <li>Надежность: использует системные события ОС</li>
 *   <li>Эффективность: минимальное потребление ресурсов</li>
 * </ul>
 * 
 * @author Neburalis
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConfigWatcher {
    private final Config config;
    private final Path configFile;
    private final WatchService watchService;
    private final ExecutorService executor;
    private final AtomicBoolean running;
    
    public ConfigWatcher(Config config) {
        this.config = config;
        this.configFile = config.getConfigFile();
        this.running = new AtomicBoolean(false);
        
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.executor = Executors.newSingleThreadExecutor();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create watch service", e);
        }
    }
    
    /**
     * Начать отслеживание
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            executor.submit(this::watch);
        }
    }
    
    /**
     * Остановить отслеживание
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                watchService.close();
                executor.shutdown();
            } catch (IOException e) {
                System.err.println("Error closing watch service: " + e.getMessage());
            }
        }
    }
    
    /**
     * Основной цикл отслеживания
     */
    private void watch() {
        try {
            Path parentDir = configFile.getParent();
            if (parentDir != null) {
                parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            }
            
            while (running.get()) {
                WatchKey key = watchService.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    
                    if (fileName.equals(configFile.getFileName())) {
                        // Небольшая задержка для завершения записи файла
                        Thread.sleep(100);
                        config.reload();
                    }
                }
                
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Error watching config file: " + e.getMessage());
        }
    }
} 