package kz.nicnbk.service.impl.hf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kz.nicnbk.repo.api.hf.HFResearchRepository;
import kz.nicnbk.repo.model.hf.HFResearch;
import kz.nicnbk.service.api.hf.HFResearchService;
import kz.nicnbk.service.converter.hf.HFResearchEntityConverter;
import kz.nicnbk.service.dto.hf.HFResearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhambyl on 06/11/2018.
 */
@Service
public class HFResearchServiceImpl implements HFResearchService {

    private static final Logger logger = LoggerFactory.getLogger(HFResearchServiceImpl.class);

    @Autowired
    private HFResearchRepository repository;

    @Autowired
    private HFResearchEntityConverter converter;

    @Override
    public Long save(HFResearchDto researchDto) {
        return null;
    }

    @Override
    public HFResearchDto get(Long id) {
        try {
            HFResearch entity = this.repository.findOne(id);
            HFResearchDto researchDto = this.converter.disassemble(entity);
            return researchDto;
        }catch(Exception ex){
            logger.error("Failed tp load HF research page:" + id, ex);
        }
        return null;
    }
}
