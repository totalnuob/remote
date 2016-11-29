package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.FundRepository;
import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.service.api.pe.PeFundService;
import kz.nicnbk.service.converter.pe.PeFundEntityConverter;
import kz.nicnbk.service.dto.pe.PeFundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
@Service
public class PeFundServiceImpl implements PeFundService {

    @Autowired
    private FundRepository repository;

    @Autowired
    private PeFundEntityConverter converter;

    @Override
    public Long save(PeFundDto fundDto) {
        Fund entity = converter.assemble(fundDto);
        Long id =repository.save(entity).getId();
        return id;
    }

    @Override
    public PeFundDto get(Long id) {
        Fund entity = this.repository.findOne(id);
        return this.converter.disassemble(entity);
    }

    @Override
    public Set<PeFundDto> loadFirmFunds(Long firmId) {
        Page<Fund> page = this.repository.findByFirmId(firmId, new PageRequest(0,10, new Sort(Sort.Direction.DESC, "id")));
        return this.converter.disassembleSet(page.getContent());
    }
}

