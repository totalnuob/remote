package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEGrossCashflowRepository;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.converter.pe.PEGrossCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PEFundCompaniesPerformanceDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
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
    private PEGrossCashflowRepository peCFRepository;

    @Autowired
    private PEGrossCashflowEntityConverter peCFEntityConverter;

    @Override
    public Long save(PEGrossCashflowDto dto, Long fundId) {
        try {
            PEGrossCashflow entity = this.peCFEntityConverter.assemble(dto);
            Long id = this.peCFRepository.save(entity).getId();
            return id;
        } catch(Exception ex){
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public String saveList(List<PEFundCompaniesPerformanceDto> performanceDtoList, Long fundId) {
        fdsfdsfs
    }

    @Override
    public List<PEGrossCashflowDto> findByFundId(Long fundId) {
        try {
            return this.peCFEntityConverter.disassembleList(this.peCFRepository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName", "date"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.peCFRepository.deleteByFundId(fundId);
        return true;
    }
}