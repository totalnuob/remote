package kz.nicnbk.service.impl.pe;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.pe.PEFirmRepository;
import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.converter.pe.PEFirmEntityConverter;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PESearchParams;
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
public class PEFirmServiceImpl implements PEFirmService {

    @Autowired
    private PEFirmRepository repository;

    @Autowired
    private PEFirmEntityConverter converter;

    @Override
    public Long save(PEFirmDto firmDto) {
        PEFirm entity = converter.assemble(firmDto);
        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public PEFirmDto get(Long id) {
        PEFirm entity = this.repository.findOne(id);
        PEFirmDto firmDto = this.converter.disassemble(entity);

        return firmDto;
    }

    @Override
    public Set<PEFirmDto> findByName(PESearchParams searchParams) {
        if(StringUtils.isEmpty(searchParams.getName())){
            Page<PEFirm> page = this.repository.findAll(new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
            return this.converter.disassembleSet(page.getContent());
        }
        Page<PEFirm> page = this.repository.findByName(searchParams.getName(), new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));

        return this.converter.disassembleSet(page.getContent());
    }


}
