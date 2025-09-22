package org.smarteye.backend.unit;

import org.junit.jupiter.api.Test;
import org.smarteye.backend.config.ConveyorProperties;
import org.smarteye.backend.service.ConveyorPipelineService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConveyorPipelineServiceTest {
    @Test
    void offsetIsConfigurable() {
        ConveyorProperties p = new ConveyorProperties();
        p.setOffsetBetweenScales(7);
        ConveyorPipelineService s = new ConveyorPipelineService(p);
        assertEquals(7, s.getOffsetBetweenScales());
    }
}
