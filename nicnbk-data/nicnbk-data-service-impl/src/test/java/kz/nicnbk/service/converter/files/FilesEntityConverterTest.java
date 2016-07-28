package kz.nicnbk.service.converter.files;

import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.config.ServiceBeanConfiguration;
import kz.nicnbk.service.dto.files.FilesDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Created by magzumov on 07.07.2016.
 */


@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class FilesEntityConverterTest {

    @Autowired
    private FilesEntityConverter converter;

    @Test
    public void testFilesEntityAssemble(){
        FilesDto dto = getTestDto();
        Files entity = converter.assemble(dto);
        assert (checkEquals(entity, dto));
    }

    @Test
    public void testFilesEntityDisassemble(){
        Files entity = getTestEntity();
        FilesDto dto = converter.disassemble(entity);
        assert (checkEquals(entity, dto));
    }

    private static FilesDto getTestDto(){
        FilesDto dto = new FilesDto();
        dto.setId(11L);
        dto.setFileName("Test file name");
        dto.setMimeType("Test mime");
        dto.setSize(123123L);
        dto.setType(FileTypeLookup.MEMO_ATTACHMENT.getCode());
        return dto;
    }

    private static Files getTestEntity(){
        Files entity = new Files();
        entity.setId(11L);
        entity.setFileName("Test file name");
        entity.setMimeType("Test mime");
        entity.setSize(123123L);
        FilesType filesType = new FilesType();
        filesType.setId(10);
        filesType.setCode(FileTypeLookup.MEMO_ATTACHMENT.getCode());
        entity.setType(filesType);
        return entity;
    }

    private static boolean checkEquals(Files entity, FilesDto dto){
        return entity.getId().longValue() == dto.getId().longValue() &&
                entity.getFileName().equals(dto.getFileName()) &&
                entity.getSize().longValue() == dto.getSize().longValue() &&
                entity.getMimeType().equals(dto.getMimeType()) &&
                entity.getType().getCode().equals(dto.getType());
    }

}
