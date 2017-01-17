package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PeNetCashflowRepository;
import kz.nicnbk.repo.model.pe.PENetCashflow;
import kz.nicnbk.service.api.pe.PENetCashflowService;
import kz.nicnbk.service.converter.pe.PENetCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PENetCashflowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Service
public class PENetCashflowServiceImpl implements PENetCashflowService {

    @Autowired
    private PeNetCashflowRepository peNetCfRepository;

    @Autowired
    private PENetCashflowEntityConverter peNetCfEntityConverter;


    @Override
    public Long save(PENetCashflowDto dto) {
        try {
            PENetCashflow entity = this.peNetCfEntityConverter.assemble(dto);
            Long id = this.peNetCfRepository.save(entity).getId();
            return id;
        } catch(Exception ex){
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.peNetCfRepository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public PENetCashflowDto get(Long id) {
        return null;
    }

    @Override
    public List<PENetCashflowDto> findByFundId(Long id) {
        List<PENetCashflow> entities = this.peNetCfRepository.getEntitiesByFundId(id);
        return this.peNetCfEntityConverter.disassembleList(entities);
    }
}
