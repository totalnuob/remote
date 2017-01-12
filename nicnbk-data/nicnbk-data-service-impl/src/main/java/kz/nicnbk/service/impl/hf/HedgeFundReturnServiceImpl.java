package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.hf.HedgeFundReturnRepository;
import kz.nicnbk.repo.api.hf.InvestorBaseRepository;
import kz.nicnbk.repo.model.hf.HedgeFundReturn;
import kz.nicnbk.repo.model.hf.InvestorBase;
import kz.nicnbk.service.api.hf.HedgeFundReturnService;
import kz.nicnbk.service.api.hf.InvestorBaseService;
import kz.nicnbk.service.converter.hf.HedgeFundReturnEntityConverter;
import kz.nicnbk.service.converter.hf.InvestorBaseEntityConverter;
import kz.nicnbk.service.dto.hf.InvestorBaseDto;
import kz.nicnbk.service.dto.hf.ReturnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by magzumov on 15.12.2016.
 */
@Service
public class HedgeFundReturnServiceImpl implements HedgeFundReturnService {

    @Autowired
    private HedgeFundReturnRepository hedgeFundReturnRepository;

    @Autowired
    private HedgeFundReturnEntityConverter hedgeFundReturnEntityConverter;

    @Override
    public Long save(ReturnDto dto) {
        try {
            HedgeFundReturn entity = this.hedgeFundReturnEntityConverter.assemble(dto);
            Long id = this.hedgeFundReturnRepository.save(entity).getId();
            return id;
        } catch(Exception ex){
            // TODO: log exception
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.hedgeFundReturnRepository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public ReturnDto get(Long id) {
        return null;
    }

    @Override
    public List<ReturnDto> findByFundId(Long id) {
        List<HedgeFundReturn> entities =  this.hedgeFundReturnRepository.getEntitiesByFundId(id);
        return this.hedgeFundReturnEntityConverter.disassembleList(entities);
    }
}
