package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PECompanyPerformanceIddRepository;
import kz.nicnbk.repo.model.pe.PECompanyPerformanceIdd;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.converter.pe.PECompanyPerformanceIddEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddResultDto;
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
 * Created by Pak on 17.10.2017.
 */
@Service
public class PECompanyPerformanceIddServiceImpl implements PECompanyPerformanceIddService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PECompanyPerformanceIddRepository repository;

    @Autowired
    private PECompanyPerformanceIddEntityConverter converter;

    @Autowired
    private PEFundService peFundService;

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
    public PECompanyPerformanceIddResultDto saveList(List<PECompanyPerformanceIddDto> performanceIddDtoList, Long fundId) {
        try {
            if (performanceIddDtoList == null || fundId == null) {
                return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                if (performanceIddDto.getCompanyName() != null) {
                    performanceIddDto.setCompanyName(performanceIddDto.getCompanyName().trim());
                }
                if (performanceIddDto.getCompanyName() == null || performanceIddDto.getCompanyName().equals("")) {
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty company name!", "");
                }
                if ((performanceIddDto.getInvested() != null && performanceIddDto.getInvested() < 0) ||
                        (performanceIddDto.getRealized() != null && performanceIddDto.getRealized() < 0) ||
                        (performanceIddDto.getUnrealized() != null && performanceIddDto.getUnrealized() < 0) ||
                        (performanceIddDto.getTotalValue() != null && performanceIddDto.getTotalValue() < 0) ||
                        (performanceIddDto.getMultiple() != null && performanceIddDto.getMultiple() < 0)) {
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send negative numbers!", "");
                }
            }

            for (PECompanyPerformanceIddDto performanceIddDto1 : performanceIddDtoList) {
                int i = 0;
                for (PECompanyPerformanceIddDto performanceIddDto2 : performanceIddDtoList) {
                    if (performanceIddDto1.getCompanyName().equals(performanceIddDto2.getCompanyName())) {
                        i++;
                    }
                }
                if (i > 1) {
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Names must be unique!", "");
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            for (PECompanyPerformanceIdd performanceIdd : this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")))) {
                int i = 0;
                for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                    if (performanceIdd.getId().equals(performanceIddDto.getId())) {
                        i++;
                        break;
                    }
                }
                if ( i == 0) {
                    this.repository.delete(performanceIdd);
                }
            }

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                Long id = save(performanceIddDto, fundId);
                if (id == null) {
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
                } else {
                    performanceIddDto.setId(id);
                }
            }

            return new PECompanyPerformanceIddResultDto(performanceIddDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's company performance", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId, ex);
            return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
        }
    }

    @Override
    public PECompanyPerformanceIddResultDto recalculatePerformanceIdd(Long fundId) {
        try {
            PEFundDto fundDto = this.peFundService.get(fundId);
            if (fundDto == null) {
                return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            deleteByFundId(fundId);

            List<PECompanyPerformanceIddDto> performanceIddDtoList = new ArrayList<>();

            for (PEGrossCashflowDto cashflowDto : this.cashflowService.findByFundId(fundId)) {
                boolean found = false;
                for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                    if (cashflowDto.getCompanyName().equalsIgnoreCase(performanceIddDto.getCompanyName())) {
                        found = true;
                        if (cashflowDto.getInvested() != null) {
                            performanceIddDto.setInvested(performanceIddDto.getInvested() != null ?
                                    (performanceIddDto.getInvested() - cashflowDto.getInvested()) : - cashflowDto.getInvested());
                        }
                        if (cashflowDto.getRealized() != null) {
                            performanceIddDto.setRealized(performanceIddDto.getRealized() != null ?
                                    (performanceIddDto.getRealized() + cashflowDto.getRealized()) : cashflowDto.getRealized());
                        }
                        if (cashflowDto.getUnrealized() != null) {
                            performanceIddDto.setUnrealized(performanceIddDto.getUnrealized() != null ?
                                    (performanceIddDto.getUnrealized() + cashflowDto.getUnrealized()) : cashflowDto.getUnrealized());
                        }
                        break;
                    }
                }
                if (!found) {
                    performanceIddDtoList.add(
                            new PECompanyPerformanceIddDto(
                                    cashflowDto.getCompanyName(),
                                    (cashflowDto.getInvested() == null) ? null : - cashflowDto.getInvested(),
                                    cashflowDto.getRealized(),
                                    cashflowDto.getUnrealized(),
                                    null, null, true, null, null));
                }
            }

            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                performanceIddDto.setTotalValue(
                        (performanceIddDto.getRealized() == null ? 0.0 : performanceIddDto.getRealized()) +
                                (performanceIddDto.getUnrealized() == null ? 0.0 : performanceIddDto.getUnrealized()));

                performanceIddDto.setMultiple(performanceIddDto.getInvested() == null ? null : performanceIddDto.getTotalValue() / performanceIddDto.getInvested());

                List<PEGrossCashflowDto> cashflowDtoList = this.cashflowService.findByFundIdAndCompanyName(fundId, performanceIddDto.getCompanyName());
                if (cashflowDtoList == null) {
                    return new PECompanyPerformanceIddResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error updating PE fund's company performance", "");
                }
                performanceIddDto.setGrossIrr(this.irrService.getIRR(cashflowDtoList));
            }

            return saveList(performanceIddDtoList, fundId);
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
    public boolean deleteByFundId(Long fundId) {
        this.repository.deleteByFundId(fundId);
        return true;
    }
}
