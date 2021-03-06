package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PECompanyPerformanceRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import kz.nicnbk.service.api.pe.PECompanyPerformanceService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.*;
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

    private static final Logger logger = LoggerFactory.getLogger(PECompanyPerformanceServiceImpl.class);

    @Autowired
    private PECompanyPerformanceRepository repository;

    @Autowired
    private PECompanyPerformanceEntityConverter converter;

//    @Autowired
//    private PEGrossCashflowService cashflowService;

//    @Autowired
//    private PEIrrService irrService;

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
    public PECompanyPerformanceResultDto saveList(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username) {
        try {
            if (performanceDtoList == null || fundId == null) {
                logger.error("Error saving PE fund's company performance list : " + fundId);
                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
                if (performanceDto.getCompanyName() != null) {
                    performanceDto.setCompanyName(performanceDto.getCompanyName().trim());
                }
                if (performanceDto.getCompanyName() == null || performanceDto.getCompanyName().equals("")) {
                    logger.error("Error saving PE fund's company performance list : " + fundId);
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty company name!", "");
                }
//                if ((performanceDto.getInvested() != null && performanceDto.getInvested() < 0) ||
//                        (performanceDto.getRealized() != null && performanceDto.getRealized() < 0) ||
//                        (performanceDto.getUnrealized() != null && performanceDto.getUnrealized() < 0) ||
//                        (performanceDto.getTotalValue() != null && performanceDto.getTotalValue() < 0) ||
//                        (performanceDto.getMultiple() != null && performanceDto.getMultiple() < 0)) {
//                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send negative numbers!", "");
//                }
            }

            for (PECompanyPerformanceDto performanceDto1 : performanceDtoList) {
                int i = 0;
                for (PECompanyPerformanceDto performanceDto2 : performanceDtoList) {
                    if (performanceDto1.getCompanyName().equals(performanceDto2.getCompanyName())) {
                        i++;
                    }
                }
                if (i > 1) {
                    logger.error("Error saving PE fund's company performance list : " + fundId);
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Names must be unique!", "");
                }
            }

            for (PECompanyPerformance performance : this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")))) {
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
                if (performanceDto.getAutoCalculation()) {
                    performanceDto.setTotalValue(
                            (performanceDto.getRealized() == null ? 0.0 : performanceDto.getRealized()) +
                                    (performanceDto.getUnrealized() == null ? 0.0 : performanceDto.getUnrealized()));
                    performanceDto.setMultiple((performanceDto.getInvested() == null || performanceDto.getInvested() == 0.0) ? null : performanceDto.getTotalValue() / performanceDto.getInvested());
                } else if (performanceDto.getTotalValue() == null) {
                    performanceDto.setTotalValue(0.0);
                }
                Long id = save(performanceDto, fundId);
                if (id == null) {
                    logger.error("Error saving PE fund's company performance list : " + fundId);
                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
                } else {
                    performanceDto.setId(id);
                }
            }

            logger.info("Saved PE fund's company performance list : " + fundId + ", updater : " + username);
            return new PECompanyPerformanceResultDto(performanceDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's company performance", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
            return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
        }
    }

//    @Override
//    public PECompanyPerformanceResultDto recalculatePerformance(Long fundId) {
//        try {
//            PEFundDto fundDto = this.peFundService.get(fundId);
//            if (fundDto == null) {
//                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
//            }
//
//            deleteByFundId(fundId);
//
//            List<PECompanyPerformanceDto> performanceDtoList = new ArrayList<>();
//
//            for (PEGrossCashflowDto cashflowDto : this.cashflowService.findByFundId(fundId)) {
//                boolean found = false;
//                for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
//                    if (cashflowDto.getCompanyName().equalsIgnoreCase(performanceDto.getCompanyName())) {
//                        found = true;
//                        if (cashflowDto.getInvested() != null) {
//                            performanceDto.setInvested(performanceDto.getInvested() - cashflowDto.getInvested());
//                        }
//                        if (cashflowDto.getRealized() != null) {
//                            performanceDto.setRealized(performanceDto.getRealized() + cashflowDto.getRealized());
//                        }
//                        if (cashflowDto.getUnrealized() != null) {
//                            performanceDto.setUnrealized(performanceDto.getUnrealized() + cashflowDto.getUnrealized());
//                        }
//                        break;
//                    }
//                }
//                if (!found) {
//                    performanceDtoList.add(
//                            new PECompanyPerformanceDto(
//                                    cashflowDto.getCompanyName(),
//                                    (cashflowDto.getInvested() == null) ? 0.0 : - cashflowDto.getInvested(),
//                                    (cashflowDto.getRealized() == null) ? 0.0 : cashflowDto.getRealized(),
//                                    (cashflowDto.getUnrealized() == null) ? 0.0 : cashflowDto.getUnrealized(),
//                                    null, null, true, null, null));
//                }
//            }
//
//            for (PECompanyPerformanceDto performanceDto : performanceDtoList) {
//                performanceDto.setTotalValue(performanceDto.getRealized() + performanceDto.getUnrealized());
//
//                performanceDto.setMultiple(performanceDto.getInvested() == 0.0 ? null : performanceDto.getTotalValue() / performanceDto.getInvested());
//
//                List<PEGrossCashflowDto> cashflowDtoList = this.cashflowService.findByFundIdAndCompanyName(fundId, performanceDto.getCompanyName());
//                if (cashflowDtoList == null) {
//                    return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
//                }
//                performanceDto.setGrossIrr(this.irrService.getIRR(cashflowDtoList));
//            }
//
//            return saveList(performanceDtoList, fundId);
//        } catch (Exception ex) {
//            logger.error("Error updating PE fund's company performance: " + fundId ,ex);
//            return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
//        }
//    }

    @Override
    public List<PECompanyPerformanceDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

//    @Override
//    public boolean deleteByFundId(Long fundId) {
//        this.repository.deleteByFundId(fundId);
//        return true;
//    }

    @Override
    public PEFundTrackRecordResultDto calculateTrackRecord(Long fundId) {

        try {
            Integer numberOfInvestments = 0;
            Double investedAmount = 0.0;
            Double realized = 0.0;
            Double unrealized = 0.0;
            Double dpi = null;
            Double grossTvpi = null;

            for (PECompanyPerformanceDto performanceDto : findByFundId(fundId)) {
                numberOfInvestments++;
                if (performanceDto.getInvested() != null) {
                    investedAmount += performanceDto.getInvested() / 1000000;
                }
                if (performanceDto.getRealized() != null) {
                    realized += performanceDto.getRealized() / 1000000;
                }
                if (performanceDto.getUnrealized() != null) {
                    unrealized += performanceDto.getUnrealized() / 1000000;
                }
            }

            if (investedAmount != 0.0) {
                dpi = realized / investedAmount;
                grossTvpi = (realized + unrealized) / investedAmount;
            }

            return new PEFundTrackRecordResultDto(
                    new PEFundTrackRecordDto(
                            1,
                            numberOfInvestments,
                            investedAmount,
                            realized,
                            unrealized,
                            dpi,
                            null, null, null,
                            grossTvpi,
                            null, null, null, null),
                    StatusResultType.SUCCESS, "", "Successfully calculated PE fund's key statistics", "");
        } catch (Exception ex) {
            logger.error("Error calculating PE fund's key statistics: " + fundId, ex);
            return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Error calculating PE fund's key statistics", "");
        }
    }
}
