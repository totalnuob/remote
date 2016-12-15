package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.hf.HFManagerRepository;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.converter.hf.HFManagerEntityConverter;
import kz.nicnbk.service.dto.hf.HFFirmDto;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by timur on 19.10.2016.
 */
@Service
public class HFManagerServiceImpl implements HFManagerService {

    @Autowired
    private HFManagerRepository repository;

    @Autowired
    private HFManagerEntityConverter converter;

    @Autowired
    private HedgeFundService fundService;

    @Override
    public Long save(HFFirmDto firmDto) {
        HFManager entity = converter.assemble(firmDto);
        Long id = repository.save(entity).getId();
        return id;
    }

    @Override
    public HFFirmDto get(Long id) {
        HFManager entity = this.repository.findOne(id);
        HFFirmDto firmDto = this.converter.disassemble(entity);

        // load funds
        //firmDto.setFunds(this.fundService.loadManagerFunds(id));

        return firmDto;
    }
}
