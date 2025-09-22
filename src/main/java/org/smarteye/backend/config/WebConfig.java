package org.smarteye.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Общие настройки Spring MVC.
 * CORS вынесен в отдельный CorsConfig.
 * Swagger-ресурсы конфигурирует springdoc автоматически.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Пример: строгий парсер OffsetDateTime из ISO-строки
        registry.addConverter(String.class, OffsetDateTime.class,
                s -> OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Место для кастомных резолверов (пагинация, сортировка и т.п.), если понадобятся
    }
}
