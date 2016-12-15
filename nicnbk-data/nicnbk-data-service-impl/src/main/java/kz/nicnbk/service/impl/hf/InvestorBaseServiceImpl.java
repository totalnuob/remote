package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.hf.InvestorBaseRepository;
import kz.nicnbk.repo.model.hf.InvestorBase;
import kz.nicnbk.service.api.hf.InvestorBaseService;
import kz.nicnbk.service.converter.hf.InvestorBaseEntityConverter;
import kz.nicnbk.service.dto.hf.InvestorBaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by magzumov on 15.12.2016.
 */
@Service
public class InvestorBaseServiceImpl implements InvestorBaseService {

    @Autowired
    private InvestorBaseRepository investorBaseRepository;

    @Autowired
    private InvestorBaseEntityConverter investorBaseEntityConverter;

    @Override
    public Long save(InvestorBaseDto dto) {
        try {
            InvestorBase entity = this.investorBaseEntityConverter.assemble(dto);
            Long id = this.investorBaseRepository.save(entity).getId();
            return id;
        } catch(Exception ex){
            // TODO: log exception
            ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean deleteByFundId(Long fundId) {
        this.investorBaseRepository.deleteByFundId(fundId);
        return true;
    }

    @Override
    public InvestorBaseDto get(Long id) {
        return null;
    }

    @Override
    public List<InvestorBaseDto> findByFundId(Long id) {
        List<InvestorBase> entities =  this.investorBaseRepository.getEntitiesByFundId(id);
        return this.investorBaseEntityConverter.disassembleList(entities);
    }
}
