package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEFundCompaniesPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;
import kz.nicnbk.service.api.pe.PEFundCompaniesPerformanceService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PEFundCompaniesPerformanceEntityConverter;
import kz.nicnbk.service.dto.pe.PEFundCompaniesPerformanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
@Service
public class PEFundCompaniesPerformanceServiceImpl implements PEFundCompaniesPerformanceService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEFundCompaniesPerformanceRepository repository;

    @Autowired
    private PEFundCompaniesPerformanceEntityConverter converter;

    @Autowired
    private PEFundService peFundService;

    @Override
    public Long save(PEFundCompaniesPerformanceDto performanceDto, Long fundId) {
        try {
            PEFundCompaniesPerformance entity = this.converter.assemble(performanceDto);
            entity.setFund(new PEFund(fundId));
            return repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

    @Override
    public String saveList(List<PEFundCompaniesPerformanceDto> performanceDtoList, Long fundId) {
        try {
            if (performanceDtoList == null || fundId == null) {
                return "Don't send NULL!";
            }

            for (PEFundCompaniesPerformanceDto performanceDto : performanceDtoList) {
                if (performanceDto.getCompanyName() == null || performanceDto.getCompanyName().equals("")) {
                    return "Don't send null or empty company name!";
                }
            }

            for (PEFundCompaniesPerformanceDto performanceDto1 : performanceDtoList) {
                int i = 0;
                for (PEFundCompaniesPerformanceDto performanceDto2 : performanceDtoList) {
                    if (performanceDto1.getCompanyName().equals(performanceDto2.getCompanyName())) {
                        i++;
                    }
                }
                if (i > 1) {
                    return "Names must be unique!";
                }
            }

            if (peFundService.get(fundId) == null) {
                return "Fund doesn't exist!";
            }

            this.deleteByFundId(fundId);

            for (PEFundCompaniesPerformanceDto performanceDto : performanceDtoList) {
                this.save(performanceDto, fundId);
            }

            return "Ok";
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
        }
        return "Error";
    }

    @Override
    public List<PEFundCompaniesPerformanceDto> getEntityDtosByFundId(Long fundId) {
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
