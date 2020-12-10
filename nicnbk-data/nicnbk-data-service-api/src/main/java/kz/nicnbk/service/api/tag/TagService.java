package kz.nicnbk.service.api.tag;

import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.tag.TagDto;

import java.util.List;

/**
 * Created by magzumov on 22.10.2018.
 */
public interface TagService {

    TagDto findByName(String name);

    List<TagDto> findByTypeCode(String code);

    TagDto findByNameAndTypeCode(String name, String code);

    EntitySaveResponseDto save(TagDto tagDto, String username);
}
