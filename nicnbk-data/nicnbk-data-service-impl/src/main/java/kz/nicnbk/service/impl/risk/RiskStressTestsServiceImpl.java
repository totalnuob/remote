package kz.nicnbk.service.impl.risk;

import kz.nicnbk.repo.api.risk.RiskStressTestsRepository;
import kz.nicnbk.repo.model.risk.RiskStressTests;
import kz.nicnbk.service.api.risk.RiskStressTestsService;
import kz.nicnbk.service.dto.monitoring.RiskStressTestsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RiskStressTestsServiceImpl implements RiskStressTestsService {

    @Autowired
    private RiskStressTestsRepository stressTestsRepository;

    @Override
    public List<RiskStressTestsDto> getStressTestsByDate(Date date) {
        List<RiskStressTestsDto> stressTests = new ArrayList<>();
        List< RiskStressTests> entities = this.stressTestsRepository.findByDate(date);
        if(entities != null && !entities.isEmpty()){
            for(RiskStressTests entity: entities){
                RiskStressTestsDto dto = new RiskStressTestsDto(entity.getName(), entity.getValue());
                stressTests.add(dto);
            }
        }
        return stressTests;
    }
}
