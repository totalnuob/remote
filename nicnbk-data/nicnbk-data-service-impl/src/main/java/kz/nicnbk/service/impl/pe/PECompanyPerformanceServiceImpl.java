package kz.nicnbk.service.impl.pe;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;
import kz.nicnbk.repo.api.pe.PECompanyPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import kz.nicnbk.service.api.pe.PECompanyPerformanceService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceResultDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private PEGrossCashflowService cashflowService;

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
                if (performanceDto.getCompanyName() != null) {
                    performanceDto.setCompanyName(performanceDto.getCompanyName().trim());
                }
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

            for (PECompanyPerformance performance: this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")))) {
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
                Long id = save(performanceDto, fundId);
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
    public PECompanyPerformanceResultDto recalculatePerformance(Long fundId) {
        try {
            PEFundDto fundDto = this.peFundService.get(fundId);
            if (fundDto == null) {
                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            deleteByFundId(fundId);

            List<PECompanyPerformanceDto> performanceDtoList = new ArrayList<>();

            for (PEGrossCashflowDto cashflowDto : this.cashflowService.findByFundId(fundId)) {
                boolean found = false;
                for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                    if (cashflowDto.getCompanyName().equalsIgnoreCase(performanceDto.getCompanyName())) {
                        found = true;
                        if (cashflowDto.getInvested() != null) {
                            performanceDto.setInvested(performanceDto.getInvested() != null ?
                                    (performanceDto.getInvested() - cashflowDto.getInvested()) : - cashflowDto.getInvested());
                        }
                        if (cashflowDto.getRealized() != null) {
                            performanceDto.setRealized(performanceDto.getRealized() != null ?
                                    (performanceDto.getRealized() + cashflowDto.getRealized()) : cashflowDto.getRealized());
                        }
                        if (cashflowDto.getUnrealized() != null) {
                            performanceDto.setUnrealized(performanceDto.getUnrealized() != null ?
                                    (performanceDto.getUnrealized() + cashflowDto.getUnrealized()) : cashflowDto.getUnrealized());
                        }
                        break;
                    }
                }
                if (found == false) {
                    performanceDtoList.add(
                            new PECompanyPerformanceDto(
                                    cashflowDto.getCompanyName(),
                                    (cashflowDto.getInvested() == null) ? null : - cashflowDto.getInvested(),
                                    cashflowDto.getRealized(),
                                    cashflowDto.getUnrealized(),
                                    null, null, true, null, null));
                }
            }

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                performanceDto.setTotalValue(
                        (performanceDto.getRealized() == null ? 0.0 : performanceDto.getRealized()) +
                        (performanceDto.getUnrealized() == null ? 0.0 : performanceDto.getUnrealized())
                );
                performanceDto.setMultiple(performanceDto.getInvested() == null ? null : performanceDto.getTotalValue() / performanceDto.getInvested());
            }

            XIRR irrCalculator = new XIRR();
            irrCalculator.xirr(new XIRRData());

            return saveList(performanceDtoList, fundId);
        } catch (Exception ex) {
            logger.error("Error updating PE fund's company performance: " + fundId ,ex);
            return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
        }
    }

    @Override
    public List<PECompanyPerformanceDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName"))));
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
