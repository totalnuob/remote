package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PECompanyPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import kz.nicnbk.service.api.pe.PECompanyPerformanceService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public PECompanyPerformanceResultDto saveList(List<PECompanyPerformanceDto> performanceDtoList, Long fundId) {
        try {
            if (performanceDtoList == null || fundId == null) {
                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                if (performanceDto.getCompanyName() == null || performanceDto.getCompanyName().equals("")) {
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty company name!", "");
                }
                if ((performanceDto.getInvested() != null && performanceDto.getInvested() < 0) ||
                        (performanceDto.getRealized() != null && performanceDto.getRealized() < 0) ||
                        (performanceDto.getUnrealized() != null && performanceDto.getUnrealized() < 0) ||
                        (performanceDto.getTotalValue() != null && performanceDto.getTotalValue() < 0) ||
                        (performanceDto.getMultiple() != null && performanceDto.getMultiple() < 0)) {
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send negative numbers!", "");
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
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Names must be unique!", "");
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            for (PECompanyPerformance performance: this.repository.getEntitiesByFundId(fundId)) {
                int i = 0;
                for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                    if (performance.getId().equals(performanceDto.getId())) {
                        i++;
                        break;
                    }
                }
                if ( i == 0) {
                    this.repository.delete(performance);
                }
            }

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                Long id = this.save(performanceDto, fundId);
                if (id == null) {
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
                } else {
                    performanceDto.setId(id);
                }
            }

            return new PECompanyPerformanceResultDto(performanceDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's company performance", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
            return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
        }
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
