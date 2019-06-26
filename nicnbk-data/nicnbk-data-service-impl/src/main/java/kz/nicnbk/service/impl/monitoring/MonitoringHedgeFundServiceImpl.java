package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.service.api.monitoring.MonitoringHedgeFundService;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MonitoringHedgeFundServiceImpl implements MonitoringHedgeFundService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringHedgeFundServiceImpl.class);

    @Override
    public List<MonitoringHedgeFundDataDto> getAllData() {
        return getDummyData();
    }

    @Override
    public boolean save(MonitoringHedgeFundDataDto dataDto, String username) {
        return false;
    }

    private List<MonitoringHedgeFundDataDto> getDummyData(){
        List<MonitoringHedgeFundDataDto> data = new ArrayList<>();
        return data;
    }
}