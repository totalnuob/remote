package kz.nicnbk.service.dto.tag;


import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.tag.Tag;

/**
 * Created by magzumov on 07.07.2016.
 */
public class TagDto extends BaseEntityDto<Tag> {

    private String name;
    private BaseDictionaryDto type;

    public TagDto(){}

    public TagDto(Long id, String name){
        this.setId(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseDictionaryDto getType() {
        return type;
    }

    public void setType(BaseDictionaryDto type) {
        this.type = type;
    }
}
