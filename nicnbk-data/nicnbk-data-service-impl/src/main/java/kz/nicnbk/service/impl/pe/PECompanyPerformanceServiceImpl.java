package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PECompanyPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import kz.nicnbk.service.api.pe.PECompanyPerformanceService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceEntityConverter;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
@Service
public class PECompanyPerformanceServiceImpl implements PECompanyPerformanceService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PECompanyPerformanceRepository repository;

    @Autowired
    private PECompanyPerformanceEntityConverter converter;

    @Autowired
    private PEFundService peFundService;

    @Override
    public Long save(PECompanyPerformanceDto performanceDto, Long fundId) {
        try {
            PECompanyPerformance entity = this.converter.assemble(performanceDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

    @Override
    public String saveList(List<PECompanyPerformanceDto> performanceDtoList, Long fundId) {
        try {
            if (performanceDtoList == null || fundId == null) {
                return "Don't send NULL!";
            }

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                if (performanceDto.getCompanyName() == null || performanceDto.getCompanyName().equals("")) {
                    return "Don't send null or empty company name!";
                }
                if (performanceDto.getInvested() < 0 ||
                        performanceDto.getRealized() < 0 ||
                        performanceDto.getUnrealized() < 0 ||
                        performanceDto.getTotalValue() < 0 ||
                        performanceDto.getMultiple() < 0) {
                    return "Don't send negative numbers!";
                }
            }

            for (PECompanyPerformanceDto performanceDto1 : performanceDtoList) {
                int i = 0;
                for (PECompanyPerformanceDto performanceDto2 : performanceDtoList) {
                    if (performanceDto1.getCompanyName().equals(performanceDto2.getCompanyName())) {
                        i++;
                    }
                }
                if (i > 1) {
                    return "Names must be unique!";
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return "Fund doesn't exist!";
            }

            this.deleteByFundId(fundId);

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                this.save(performanceDto, fundId);
            }

            return "Ok";
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
        }
        return "Error";
    }

    @Override
    public List<PECompanyPerformanceDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.repository.deleteByFundId(fundId);
        return true;
    }
}
