package kz.nicnbk.service.impl.tag;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.common.TagTypeRepository;
import kz.nicnbk.repo.api.tag.TagRepository;
import kz.nicnbk.repo.model.common.TagType;
import kz.nicnbk.repo.model.tag.Tag;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.tag.TagService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.tag.TagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 22.10.2018.
 */
@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagTypeRepository tagTypeRepository;

    @Autowired
    private EmployeeService employeeService;

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

    @Override
    public List<TagDto> findByTypeCode(String code){
        List<TagDto> tags = new ArrayList<>();
        List<Tag> entities = this.tagRepository.findByTypeCode(code);
        if(entities != null){
            for(Tag entity: entities) {
                TagDto tagDto = new TagDto();
                tagDto.setId(entity.getId());
                tagDto.setName(entity.getName());
                if (entity.getType() != null) {
                    BaseDictionaryDto type = new BaseDictionaryDto(entity.getType().getCode(), entity.getType().getNameEn(), null, null);
                    tagDto.setType(type);
                }
                tags.add(tagDto);
            }
        }
        return tags;
    }

    @Override
    public TagDto findByNameAndTypeCode(String name, String code) {
        Tag entity = this.tagRepository.findByNameAndTypeCode(name, code);
        if(entity != null){
            TagDto tagDto = new TagDto();
            tagDto.setId(entity.getId());
            tagDto.setName(entity.getName());
            if(entity.getType() != null) {
                BaseDictionaryDto type = new BaseDictionaryDto(entity.getType().getCode(), entity.getType().getNameEn(), null, null);
                tagDto.setType(type);
            }
            return tagDto;
        }
        return null;
    }

    @Override
    public EntitySaveResponseDto save(TagDto tagDto, String username) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(!checkEditableTag(tagDto, username)){
            String errorMessage = "Error saving tag: entity is not editable";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        Tag entity = new Tag();
        entity.setName(tagDto.getName());
        if(tagDto.getType() != null && tagDto.getType().getCode() != null) {
            TagType type = this.tagTypeRepository.findByCode(tagDto.getType().getCode());
            entity.setType(type);
        }
        if(entity.getType() == null){
            String errorMessage = "Error saving tag: type is required";
            logger.error(errorMessage);
            saveResponseDto.setErrorMessageEn(errorMessage);
            return saveResponseDto;
        }
        this.tagRepository.save(entity);
        saveResponseDto.setSuccessMessageEn("Successfully saved new tag");
        return saveResponseDto;
    }

    private boolean checkEditableTag(TagDto tagDto, String username){
        // check role
        if(!checkEditableTagByUserAndType(tagDto, username)){
            return false;
        }

        // TODO: check usage
        return true;
    }
    private boolean checkEditableTagByUserAndType(TagDto tagDto, String username){
        if(this.employeeService.checkRoleByUsername(UserRoles.ADMIN, username)){
            return true;
        }
        if(tagDto.getType() != null){
            // TODO: refactor
            if(tagDto.getType().getCode().equalsIgnoreCase("IC")){
                if(this.employeeService.checkRoleByUsername(UserRoles.IC_ADMIN, username)){
                    return true;
                }
            }
        }
        return false;
    }

}
