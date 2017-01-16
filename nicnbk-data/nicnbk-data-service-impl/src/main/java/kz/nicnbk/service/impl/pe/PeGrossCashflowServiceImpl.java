package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PeGrossCashflowRepository;
import kz.nicnbk.repo.model.pe.PeGrossCashflow;
import kz.nicnbk.service.api.pe.PeGrossCashflowService;
import kz.nicnbk.service.converter.pe.PeGrossCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PeGrossCashflowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Service
public class PeGrossCashflowServiceImpl implements PeGrossCashflowService {

    @Autowired
    private PeGrossCashflowRepository peCFRepository;

    @Autowired
    private PeGrossCashflowEntityConverter peCFEntityConverter;

    @Override
    public Long save(PeGrossCashflowDto dto) {
        try {
            PeGrossCashflow entity = this.peCFEntityConverter.assemble(dto);
            Long id = this.peCFRepository.save(entity).getId();
            return id;
        } catch(Exception ex){
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.peCFRepository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public PeGrossCashflowDto get(Long id) {
        return null;
    }

    @Override
    public List<PeGrossCashflowDto> findByFundId(Long id) {
        List<PeGrossCashflow> entities = this.peCFRepository.getEntitiesByFundId(id, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")));
        return this.peCFEntityConverter.disassembleList(entities);
    }
}
