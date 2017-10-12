package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEGrossCashflowRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.converter.pe.PEGrossCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Service
public class PEGrossCashflowServiceImpl implements PEGrossCashflowService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEGrossCashflowRepository repository;

    @Autowired
    private PEGrossCashflowEntityConverter converter;

    @Autowired
    private PEFundService peFundService;

    @Override
    public Long save(PEGrossCashflowDto cashflowDto, Long fundId) {
        try {
            PEGrossCashflow entity = this.converter.assemble(cashflowDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PEGrossCashflowResultDto saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId) {
        try {
            if (cashflowDtoList == null || fundId == null) {
                return "Don't send NULL!";
            }

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (cashflowDto.getCompanyName() == null ||
                        cashflowDto.getCompanyName().equals("") ||
                        cashflowDto.getDate() == null) {
                    return "Don't send null or empty company name or date!";
                }
                if ((cashflowDto.getInvested() != null && cashflowDto.getInvested() > 0) ||
                        (cashflowDto.getRealized() != null && cashflowDto.getRealized() < 0) ||
                        (cashflowDto.getUnrealized() != null && cashflowDto.getUnrealized() < 0)) {
                    return "Check the positiveness of the values!";
                }
            }

            for (PEGrossCashflowDto cashflowDto1 : cashflowDtoList) {
                int i = 0;
                for (PEGrossCashflowDto cashflowDto2 : cashflowDtoList) {
                    if (cashflowDto1.getCompanyName().equals(cashflowDto2.getCompanyName()) &&
                            cashflowDto1.getDate().equals(cashflowDto2.getDate())) {
                        i++;
                    }
                }
                if (i > 1) {
                    return "The pairs (\"Company name\", \"Date of transaction\") must be unique!";
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return "Fund doesn't exist!";
            }

            this.deleteByFundId(fundId);

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                this.save(cashflowDto, fundId);
            }

            return "Ok";
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId, ex);
        }
        return "Error";



        resultDto.setMessageEn("Successfully saved PE fund's gross cash flow");
        resultDto.setMessageEn("Error saving fund's gross cash flow");
    }

    @Override
    public List<PEGrossCashflowDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName", "date"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.repository.deleteByFundId(fundId);
        return true;
    }
}