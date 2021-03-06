package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PENetCashflowRepository;
import kz.nicnbk.repo.model.pe.PENetCashflow;
import kz.nicnbk.service.api.pe.PENetCashflowService;
import kz.nicnbk.service.converter.pe.PENetCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PENetCashflowDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Service
public class PENetCashflowServiceImpl implements PENetCashflowService {

    private static final Logger logger = LoggerFactory.getLogger(PENetCashflowServiceImpl.class);

    @Autowired
    private PENetCashflowRepository repository;

    @Autowired
    private PENetCashflowEntityConverter converter;

    @Override
    public Long save(PENetCashflowDto dto) {
        try {
            PENetCashflow entity = this.converter.assemble(dto);
            Long id = this.repository.save(entity).getId();
            return id;
        } catch(Exception ex){
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public List<PENetCashflowDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's net cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.repository.deleteByFundId(fundId);
        return true;
    }
}