package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.hf.HedgeFundSubstrategyRepository;
import kz.nicnbk.repo.model.hf.HedgeFundSubstrategy;
import kz.nicnbk.service.api.hf.HedgeFundSubstrategyService;
import kz.nicnbk.service.converter.hf.HedgeFundSubstrategyEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundSubstrategyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by magzumov on 14.12.2016.
 */

@Service
public class HedgeFundSubstrategyServiceImpl implements HedgeFundSubstrategyService {

    @Autowired
    private HedgeFundSubstrategyRepository hedgeFundSubstrategyRepository;

    @Autowired
    private HedgeFundSubstrategyEntityConverter entityConverter;

    @Override
    public Long save(HedgeFundSubstrategyDto dto) {
        try {
            HedgeFundSubstrategy entity = this.entityConverter.assemble(dto);
            Long id = this.hedgeFundSubstrategyRepository.save(entity).getId();
            return id;
        }catch(Exception ex){
            // TODO: log exception
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean deleteByFundId(Long fundId) {

        // TODO: return value

        this.hedgeFundSubstrategyRepository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public HedgeFundSubstrategy get(Long id) {
        return null;
    }

    @Override
    public List<HedgeFundSubstrategyDto> findByFundId(Long id) {
        List<HedgeFundSubstrategy> substrategyList = this.hedgeFundSubstrategyRepository.getEntitiesByFundId(id);
        return this.entityConverter.disassembleList(substrategyList);
    }
}
