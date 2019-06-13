package kz.nicnbk.service.converter.monitoring;

import kz.nicnbk.repo.model.monitoring.PerformanceNAV;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.monitoring.PerformanceNAVDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 13.06.2019.
 */

@Component
public class PerformanceNAVEntityConverter extends BaseDozerEntityConverter<PerformanceNAV, PerformanceNAVDto> {
}
