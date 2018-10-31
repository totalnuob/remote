package kz.nicnbk.service.impl.tag;

import kz.nicnbk.repo.api.tag.TagRepository;
import kz.nicnbk.repo.model.tag.Tag;
import kz.nicnbk.service.api.tag.TagService;
import kz.nicnbk.service.dto.tag.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 22.10.2018.
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public TagDto findByName(String name) {
        Tag entity = this.tagRepository.findByName(name);
        if(entity != null){
            TagDto tagDto = new TagDto();
            tagDto.setId(entity.getId());
            tagDto.setName(entity.getName());
            return tagDto;
        }
        return null;
    }
}
