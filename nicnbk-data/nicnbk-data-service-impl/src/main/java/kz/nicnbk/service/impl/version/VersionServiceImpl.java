package kz.nicnbk.service.impl.version;

import kz.nicnbk.service.api.version.VersionService;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.version.VersionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Pak on 08.11.2019.
 */

@Service
public class VersionServiceImpl implements VersionService {

    private static final Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);

    @Override
    public VersionResultDto get() {
        try {
            return null;
        } catch (Exception ex) {
            logger.error("Failed to load versions, ", ex);
            return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions!", "", null);
        }
    }
}
