package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEGrossCashflowRepository;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.converter.pe.PEGrossCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
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
    public PEGrossCashflowDto get(Long id) {
        return null;
    }

    @Override
    public List<PEGrossCashflowDto> findByFundId(Long id) {
        List<PEGrossCashflow> entities = this.peCFRepository.getEntitiesByFundId(id, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")));
        return this.peCFEntityConverter.disassembleList(entities);
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.peCFRepository.deleteByFundId(fundId);
        return true;
    }
}