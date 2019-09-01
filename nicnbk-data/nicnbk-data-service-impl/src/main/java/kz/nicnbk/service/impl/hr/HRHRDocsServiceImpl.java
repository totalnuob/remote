package kz.nicnbk.service.impl.hr;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hr.HRDocsService;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.HRDocsResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 28.08.2019.
 */

@Service
public class HRHRDocsServiceImpl implements HRDocsService {

    private static final Logger logger = LoggerFactory.getLogger(HRHRDocsServiceImpl.class);

    @Autowired
    private FileService fileService;

    @Override
    public HRDocsResultDto get() {
        try {

            List<FilesDto> filesDtoList = this.fileService.findAllByTypeCodeOrderByFileNameAscNotDeleted(FileTypeLookup.HR_DOCS.getCode());

            if (filesDtoList != null) {
                return new HRDocsResultDto(ResponseStatusType.SUCCESS, "", "The list of documents has been loaded successfully","", filesDtoList);
            }

            return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to load the list: repository problem!", "", null);
        } catch (Exception ex) {
            logger.error("Failed to load the list: repository problem, ", ex);
            return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to load the list: repository problem!", "", null);
        }
    }

    @Override
    public HRDocsResultDto upload(Set<FilesDto> filesDtoSet, String username) {
        try {
            FilesDto filesDto;
            Long fileId;

            try {
                filesDto = filesDtoSet.iterator().next();
            } catch (Exception ex) {
                logger.error("Failed to upload the document: the file cannot be opened, ", ex);
                return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to upload the document: the file cannot be opened!", "", null);
            }

            // Save the file
            try {
                filesDto.setType(FileTypeLookup.HR_DOCS.getCode());
                filesDto.setInsertedBy(username);
                fileId = this.fileService.save(filesDto, FileTypeLookup.HR_DOCS.getCatalog());

                if(fileId != null) {
                    return new HRDocsResultDto(ResponseStatusType.SUCCESS, "", "The document has been uploaded successfully!","", null);
                }

                logger.error("Failed to update Liquid Portfolio data: repository problem!");
                return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "", null);
            } catch (Exception ex) {
                logger.error("Failed to update Liquid Portfolio data: repository problem, ", ex);
                return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to update Liquid Portfolio data: repository problem!", "", null);
            }
        } catch (Exception ex) {
            logger.error("Failed to upload the document, ", ex);
            return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to upload the document!", "", null);
        }
    }

    @Override
    public HRDocsResultDto deleteDocument(Long fileId) {
        FilesDto filesDto = this.fileService.getFileInfo(fileId);
        if(filesDto != null &&
                filesDto.getType() != null &&
                filesDto.getType().equalsIgnoreCase(FileTypeLookup.HR_DOCS.getCode()) &&
                this.fileService.safeDelete(fileId)) {
            return new HRDocsResultDto(ResponseStatusType.SUCCESS, "", "The document has been deleted successfully!","", null);
        }

        return new HRDocsResultDto(ResponseStatusType.FAIL, "", "Failed to delete the document!", "", null);
    }
}
