package kz.nicnbk.service.impl.pe;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.pe.PEFirmRepository;
import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.service.api.pe.PeFirmService;
import kz.nicnbk.service.converter.pe.PeFirmEntityConverter;
import kz.nicnbk.service.dto.pe.PeFirmDto;
import kz.nicnbk.service.dto.pe.PeSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@Service
public class PeFirmServiceImpl implements PeFirmService{

    @Autowired
    private PEFirmRepository repository;

    @Autowired
    private PeFirmEntityConverter converter;

    @Override
    public Long save(PeFirmDto firmDto) {
        PEFirm entity = converter.assemble(firmDto);
        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public PeFirmDto get(Long id) {
        PEFirm entity = this.repository.findOne(id);
        PeFirmDto firmDto = this.converter.disassemble(entity);

        return firmDto;
    }

    @Override
    public Set<PeFirmDto> findByName(PeSearchParams searchParams) {
        if(StringUtils.isEmpty(searchParams.getName())){
            Page<PEFirm> page = this.repository.findAll(new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
            return this.converter.disassembleSet(page.getContent());
        }
        Page<PEFirm> page = this.repository.findByName(searchParams.getName(), new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));

        return this.converter.disassembleSet(page.getContent());
    }


}
