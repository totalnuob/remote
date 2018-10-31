package kz.nicnbk.service.api.tag;

import kz.nicnbk.service.dto.tag.TagDto;

/**
 * Created by magzumov on 22.10.2018.
 */
public interface TagService {

    TagDto findByName(String name);
}
