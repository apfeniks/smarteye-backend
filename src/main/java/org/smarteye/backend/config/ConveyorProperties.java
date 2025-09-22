package org.smarteye.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки конвейера. Загружаются из:
 * - application*.yml (секция conveyor)
 * - либо из отдельного conveyor.yml, импортированного в application.yml
 *
 * Важно: offsetBetweenScales по умолчанию = 5, но может быть переопределён ENV-переменной
 * CONVEYOR_OFFSET_BETWEEN_SCALES или в *.yml.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "conveyor")
public class ConveyorProperties {

    /**
     * Смещение между весами BEFORE и AFTER (кол-во техподдонов между постами).
     * По умолчанию 5.
     */
    private int offsetBetweenScales = 5;
}
