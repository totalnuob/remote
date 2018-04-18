package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PECompanyPerformanceIddRepository;
import kz.nicnbk.repo.model.pe.PECompanyPerformanceIdd;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceIddEntityConverter;
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
 * Created by Pak on 17.10.2017.
 */
@Service
public class PECompanyPerformanceIddServiceImpl implements PECompanyPerformanceIddService {

    private static final Logger logger = LoggerFactory.getLogger(PECompanyPerformanceIddServiceImpl.class);

    @Autowired
    private PECompanyPerformanceIddRepository repository;

    @Autowired
    private PECompanyPerformanceIddEntityConverter converter;

    @Autowired
    private PEGrossCashflowService cashflowService;

    @Autowired
    private PEIrrService irrService;

    @Override
    public Long save(PECompanyPerformanceIddDto performanceIddDto, Long fundId) {
        try {
            PECompanyPerformanceIdd entity = this.converter.assemble(performanceIddDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PECompanyPerformanceIddResultDto saveList(List<PECompanyPerformanceIddDto> performanceIddDtoList, Long fundId, String username) {
        try {
            if (performanceIddDtoList == null || fundId == null) {
                logger.error("Error saving PE fund's company performance list / portfolio info : " + fundId);
                return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                if (performanceIddDto.getCompanyName() != null) {
                    performanceIddDto.setCompanyName(performanceIddDto.getCompanyName().trim());
                }
                if (performanceIddDto.getCompanyName() == null || performanceIddDto.getCompanyName().equals("")) {
                    logger.error("Error saving PE fund's company performance list / portfolio info : " + fundId);
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty company name!", "");
                }
//                if ((performanceIddDto.getInvested() != null && performanceIddDto.getInvested() < 0) ||
//                        (performanceIddDto.getRealized() != null && performanceIddDto.getRealized() < 0) ||
//                        (performanceIddDto.getUnrealized() != null && performanceIddDto.getUnrealized() < 0) ||
//                        (performanceIddDto.getTotalValue() != null && performanceIddDto.getTotalValue() < 0) ||
//                        (performanceIddDto.getMultiple() != null && performanceIddDto.getMultiple() < 0)) {
//                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send negative numbers!", "");
//                }
            }

            for (PECompanyPerformanceIddDto performanceIddDto1 : performanceIddDtoList) {
                int i = 0;
                for (PECompanyPerformanceIddDto performanceIddDto2 : performanceIddDtoList) {
                    if (performanceIddDto1.getCompanyName().equals(performanceIddDto2.getCompanyName())) {
                        i++;
                    }
                }
                if (i > 1) {
                    logger.error("Error saving PE fund's company performance list / portfolio info : " + fundId);
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Names must be unique!", "");
                }
            }

            for (PECompanyPerformanceIdd performanceIdd : this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")))) {
                int i = 0;
                for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                    if (performanceIddDto.getCompanyName().equals(performanceIdd.getCompanyName())) {
                        performanceIddDto.setId(performanceIdd.getId());
//                        performanceIddDto.setCompanyDescription(performanceIdd.getCompanyDescription());
//                        performanceIddDto.setIndustry(performanceIdd.getIndustry());
//                        performanceIddDto.setCountry(performanceIdd.getCountry());
//                        performanceIddDto.setTypeOfInvestment(performanceIdd.getTypeOfInvestment());
//                        performanceIddDto.setControl(performanceIdd.getControl());
//                        performanceIddDto.setDealSource(performanceIdd.getDealSource());
//                        performanceIddDto.setCurrency(performanceIdd.getCurrency());
                        i++;
                        break;
                    }
                }
                if ( i == 0) {
                    this.repository.delete(performanceIdd);
                }
            }

//            List<PECompanyPerformanceIdd> performanceIddList = this.converter.assembleListWithFundId(performanceIddDtoList, fundId);
//            this.repository.save(performanceIddList);

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                Long id = save(performanceIddDto, fundId);
                if (id == null) {
                    logger.error("Error saving PE fund's company performance list / portfolio info : " + fundId);
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance / portfolio info", "");
                } else {
                    performanceIddDto.setId(id);
                }
            }

            logger.info("Saved PE fund's company performance list / portfolio info : " + fundId + ", updater : " + username);
            return new PECompanyPerformanceIddResultDto(performanceIddDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's company performance / portfolio info", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance list / portfolio info : " + fundId, ex);
            return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance / portfolio info", "");
        }
    }

    @Override
    public PECompanyPerformanceIddResultDto recalculatePerformanceIdd(Long fundId, String username) {
        try {
//            deleteByFundId(fundId);

            List<PECompanyPerformanceIddDto> performanceIddDtoList = new ArrayList<>();

            for (PEGrossCashflowDto cashflowDto : this.cashflowService.findByFundId(fundId)) {
                boolean found = false;
                for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                    if (cashflowDto.getCompanyName().equalsIgnoreCase(performanceIddDto.getCompanyName())) {
                        found = true;
                        if (cashflowDto.getInvested() != null) {
                            performanceIddDto.setInvested(performanceIddDto.getInvested() - cashflowDto.getInvested());
                        }
                        if (cashflowDto.getRealized() != null) {
                            performanceIddDto.setRealized(performanceIddDto.getRealized() + cashflowDto.getRealized());
                        }
                        if (cashflowDto.getUnrealized() != null) {
                            performanceIddDto.setUnrealized(performanceIddDto.getUnrealized() + cashflowDto.getUnrealized());
                        }
                        break;
                    }
                }
                if (!found) {
                    performanceIddDtoList.add(
                            new PECompanyPerformanceIddDto(
                                    cashflowDto.getCompanyName(),
                                    (cashflowDto.getInvested() == null) ? 0.0 : - cashflowDto.getInvested(),
                                    (cashflowDto.getRealized() == null) ? 0.0 : cashflowDto.getRealized(),
                                    (cashflowDto.getUnrealized() == null) ? 0.0 : cashflowDto.getUnrealized(),
                                    null, null, true, null, null, null, null, null, null, null, null, null));
                }
            }

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                performanceIddDto.setTotalValue(performanceIddDto.getRealized() + performanceIddDto.getUnrealized());

                performanceIddDto.setMultiple(performanceIddDto.getInvested() == 0.0 ? null : performanceIddDto.getTotalValue() / performanceIddDto.getInvested());

                List<PEGrossCashflowDto> cashflowDtoList = this.cashflowService.findByFundIdAndCompanyName(fundId, performanceIddDto.getCompanyName());
                if (cashflowDtoList == null) {
                    logger.error("Error updating PE fund's company performance: " + fundId);
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
                }
                performanceIddDto.setGrossIrr(this.irrService.getIRR(cashflowDtoList));

                for (PECompanyPerformanceIdd performanceIdd : this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")))) {
                    if (performanceIddDto.getCompanyName().equals(performanceIdd.getCompanyName())) {
                        performanceIddDto.setCompanyDescription(performanceIdd.getCompanyDescription());
                        performanceIddDto.setIndustry(performanceIdd.getIndustry());
                        performanceIddDto.setCountry(performanceIdd.getCountry());
                        performanceIddDto.setTypeOfInvestment(performanceIdd.getTypeOfInvestment());
                        performanceIddDto.setControl(performanceIdd.getControl());
                        performanceIddDto.setDealSource(performanceIdd.getDealSource());
                        performanceIddDto.setCurrency(performanceIdd.getCurrency());
                        break;
                    }
                }
            }

            return saveList(performanceIddDtoList, fundId, username);
        } catch (Exception ex) {
            logger.error("Error updating PE fund's company performance: " + fundId ,ex);
            return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
        }
    }

    @Override
    public List<PECompanyPerformanceIddDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's company performance: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PECompanyPerformanceIddDto findByFundIdAndCompanyName(Long fundId, String companyName) {
        try {
            return this.converter.disassemble(this.repository.getEntitiesByFundIdAndCompanyName(fundId, companyName));
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

            for (PECompanyPerformanceIddDto performanceIddDto : findByFundId(fundId)) {
                numberOfInvestments++;
                if (performanceIddDto.getInvested() != null) {
                    investedAmount += performanceIddDto.getInvested() / 1000000;
                }
                if (performanceIddDto.getRealized() != null) {
                    realized += performanceIddDto.getRealized() / 1000000;
                }
                if (performanceIddDto.getUnrealized() != null) {
                    unrealized += performanceIddDto.getUnrealized() / 1000000;
                }
            }

            if (investedAmount != 0.0) {
                dpi = realized / investedAmount;
                grossTvpi = (realized + unrealized) / investedAmount;
            }

            return new PEFundTrackRecordResultDto(
                    new PEFundTrackRecordDto(
                            2,
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
