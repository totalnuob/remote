package kz.nicnbk.service.impl.hr;

import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.lookup.FilesTypeRepository;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hr.DocsService;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.DocsResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DocsServiceImpl implements DocsService {

    private static final Logger logger = LoggerFactory.getLogger(DocsServiceImpl.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private FilesRepository filesRepository;

    @Autowired
    private FilesEntityConverter filesEntityConverter;

    @Autowired
    private FilesTypeRepository filesTypeRepository;

    @Override
    public DocsResultDto get() {
        try {
            FilesType dummyFilesType = new FilesType();
            dummyFilesType.setId(this.filesTypeRepository.findByCode(FileTypeLookup.HR_DOCS.getCode()).getId());

            List<Files> filesList = this.filesRepository.findAllByTypeOrderByFileNameAsc(dummyFilesType);

            List<FilesDto> filesDtoList = new ArrayList<>();

            for(Files file : filesList) {
                if(file.getDeleted() == null || !file.getDeleted()) {
                    filesDtoList.add(this.filesEntityConverter.disassemble(file));
                }
            }

            return new DocsResultDto(ResponseStatusType.SUCCESS, "", "The list of documents has been loaded successfully","", filesDtoList);
        } catch (Exception ex) {
            logger.error("Failed to load the list: repository problem, ", ex);
            return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to load the list: repository problem!", "", null);
        }
    }

    @Override
    public DocsResultDto upload(Set<FilesDto> filesDtoSet, String username) {
        try {
            FilesDto filesDto;
            Long fileId;

            try {
                filesDto = filesDtoSet.iterator().next();
            } catch (Exception ex) {
                logger.error("Failed to upload the document: the file cannot be opened, ", ex);
                return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to upload the document: the file cannot be opened!", "", null);
            }

            // Save the file
            try {
                filesDto.setType(FileTypeLookup.HR_DOCS.getCode());
                filesDto.setInsertedBy(username);
                fileId = this.fileService.save(filesDto, FileTypeLookup.HR_DOCS.getCatalog());

                if(fileId != null) {

                    DocsResultDto resultDto = this.get();

                    if(resultDto.getStatus().getCode().equals("SUCCESS")) {
                        return new DocsResultDto(ResponseStatusType.SUCCESS, "", "The document has been uploaded successfully!","", resultDto.getFilesDtoList());
                    }
                }

                logger.error("Failed to update Liquid Portfolio data: repository problem!");
                return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "", null);
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: repository problem, ", ex);
                return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "", null);
            }
        } catch (Exception ex) {
            logger.error("Failed to upload the document, ", ex);
            return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to upload the document!", "", null);
        }
    }

    @Override
    public DocsResultDto deleteDocument(Long fileId) {
        FilesDto filesDto = this.fileService.getFileInfo(fileId);
        if(filesDto != null &&
                filesDto.getType() != null &&
                filesDto.getType().equalsIgnoreCase(FileTypeLookup.HR_DOCS.getCode()) &&
                this.fileService.safeDelete(fileId)) {
            DocsResultDto resultDto = this.get();
            if(resultDto.getStatus().getCode().equals("SUCCESS")) {
                return new DocsResultDto(ResponseStatusType.SUCCESS, "", "The document has been deleted successfully!","", resultDto.getFilesDtoList());
            }
        }

        return new DocsResultDto(ResponseStatusType.FAIL, "", "Failed to delete the document!", "", null);
    }
}
