package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PeNetCashflowRepository;
import kz.nicnbk.repo.model.pe.PeNetCashflow;
import kz.nicnbk.service.api.pe.PeNetCashflowService;
import kz.nicnbk.service.converter.pe.PeNetCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PeNetCashflowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Service
public class PeNetCashflowServiceImpl implements PeNetCashflowService {

    @Autowired
    private PeNetCashflowRepository peNetCfRepository;

    @Autowired
    private PeNetCashflowEntityConverter peNetCfEntityConverter;


    @Override
    public Long save(PeNetCashflowDto dto) {
        try {
            PeNetCashflow entity = this.peNetCfEntityConverter.assemble(dto);
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
    public PeNetCashflowDto get(Long id) {
        return null;
    }

    @Override
    public List<PeNetCashflowDto> findByFundId(Long id) {
        List<PeNetCashflow> entities = this.peNetCfRepository.getEntitiesByFundId(id);
        return this.peNetCfEntityConverter.disassembleList(entities);
    }
}
