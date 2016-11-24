package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.hf.HedgeFundRepository;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.converter.hf.HedgeFundEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundDto2;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
@Service
public class HedgeFundServiceImpl implements HedgeFundService {

    @Autowired
    private HedgeFundRepository repository;

    @Autowired
    private HedgeFundEntityConverter converter;

    @Override
    public Long save(HedgeFundDto2 hedgeFundDto) {
        HedgeFund entity = converter.assemble(hedgeFundDto);
        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public HedgeFundDto2 get(Long id) {
        HedgeFund entity = this.repository.findOne(id);
        return this.converter.disassemble(entity);
    }

    @Override
    public Set<HedgeFundDto2> loadManagerFunds(Long managerId) {
        Page<HedgeFund> page = repository.findByManager(managerId,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
        return this.converter.disassembleSet(page.getContent());
    }

    @Override
    public Set<HedgeFundDto2> findByName(HedgeFundSearchParams searchParams) {
//        if(StringUtils.isEmpty(name)){
//            Page<HedgeFund> page = this.repository.findAll( new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
//            return this.converter.disassembleSet(page.getContent());
//        }
        Page<HedgeFund> page = this.repository.findByName(searchParams.getName(),
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));

        return this.converter.disassembleSet(page.getContent());
    }
}
