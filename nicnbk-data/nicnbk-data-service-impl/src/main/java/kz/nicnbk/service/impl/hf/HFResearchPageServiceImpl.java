package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.hf.HFResearchPageFilesReposiotry;
import kz.nicnbk.repo.api.hf.HFResearchPageRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.HFResearchPage;
import kz.nicnbk.repo.model.hf.HFResearchPageFiles;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HFResearchPageService;
import kz.nicnbk.service.api.hf.HFResearchService;
import kz.nicnbk.service.converter.hf.HFResearchPageConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.HFResearchDto;
import kz.nicnbk.service.dto.hf.HFResearchPageDto;
import kz.nicnbk.service.dto.m2s2.MemoDeleteResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhambyl on 12/11/2018.
 */
@Service
public class HFResearchPageServiceImpl implements HFResearchPageService {

    private static final Logger logger = LoggerFactory.getLogger(HFResearchServiceImpl.class);

    @Autowired
    private HFResearchPageRepository hfResearchPageRepository;

    @Autowired
    private HFResearchPageConverter hfResearchPageConverter;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private HFResearchPageFilesReposiotry hfResearchPageFilesReposiotry;

    @Autowired
    private FileService fileService;


    @Override
    public Long save(HFResearchPageDto researchPageDto, String updater) {
        try {
            HFResearchPage entity = hfResearchPageConverter.assemble(researchPageDto);

            if(researchPageDto.getId() == null || researchPageDto.getId() == 0 ){ //CREATE
                Employee employee = this.employeeRepository.findByUsername(researchPageDto.getOwner());
                entity.setCreator(employee);
            } else {
                // set creator
                Employee employee = this.hfResearchPageRepository.findOne(researchPageDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = hfResearchPageRepository.findOne(researchPageDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }

            Long researchPageId = hfResearchPageRepository.save(entity).getId();
            logger.info(researchPageDto.getId() == null ? "HF Research page created: " + researchPageId + ", by " + entity.getCreator().getUsername() :
                    "HF Research page updated: " + researchPageId + ", by " + updater);
            return researchPageId;
        } catch (Exception ex) {
            logger.error("Error saving HF Research page: " + (researchPageDto != null && researchPageDto.getId() != null ? researchPageDto.getId() : "new") ,ex);
            return null;
        }
    }

    @Override
    public HFResearchPageDto get(Long researchPageId) {
        try{
            HFResearchPage entity = this.hfResearchPageRepository.findOne(researchPageId);
            HFResearchPageDto researchPageDto = this.hfResearchPageConverter.disassemble(entity);

            // get attachment files
            researchPageDto.setFiles(this.getAttachments(researchPageId));

            if (entity.getCreator() != null) {
                researchPageDto.setOwner(entity.getCreator().getUsername());
            }
            return researchPageDto;
        } catch(Exception ex) {
            logger.error("Error loading HF Research page: " + researchPageId, ex);
        }
        return null;
    }

    @Override
    public List<HFResearchPageDto> loadResearch(Long managerId) {
        try{
            List<HFResearchPage> hfResearchPages = hfResearchPageRepository.findByManager(managerId);
            List<HFResearchPageDto> hfResearchPageDtos = this.hfResearchPageConverter.disassembleList(hfResearchPages);

            for(HFResearchPageDto dto: hfResearchPageDtos) {
                List<HFResearchPageFiles> entities = hfResearchPageFilesReposiotry.getFilesByMemoId(dto.getId());
                Set<FilesDto> files = new HashSet<>();
                if (entities != null) {
                    for (HFResearchPageFiles researchFile : entities) {
                        FilesDto fileDto = new FilesDto();
                        fileDto.setId(researchFile.getFile().getId());
                        fileDto.setFileName(researchFile.getFile().getFileName());
                        files.add(fileDto);
                    }
                    dto.setFiles(files);
                }
            }

            return hfResearchPageDtos;
        } catch (Exception ex) {
            logger.error("Failed to load HF Research pages: manager=" + managerId, ex);
        }
        return null;
    }

    @Override
    public boolean safeDeleteAttachment(Long researchPageId, Long fileId, String username) {
        try{
            HFResearchPageFiles entity = hfResearchPageFilesReposiotry.getFilesByFileId(fileId);
            if (entity != null && entity.getResearchPage().getId().longValue() == researchPageId) {
                hfResearchPageFilesReposiotry.delete(entity.getId());
                boolean deleted = fileService.delete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) HF Research page attachment: Research page=" + researchPageId + ", file=" + fileId + ", by " + username);
                }else{
                    logger.info("Deleted(safe) HF Research page attachment: Research page=" + researchPageId + ", file=" + fileId + ", by " + username);
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) HF Research page attachment: Research page=" + researchPageId + ", file=" + fileId + ", by " + username +
                        ". Memo-File entity not found");
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) attachment with exception: Research page=" + researchPageId + ", file=" + fileId + ", by " + username, ex);
        }
        return false;
    }

    @Override
    public Set<FilesDto> getAttachments(Long researchPageId) {
        try {
            List<HFResearchPageFiles> entities = hfResearchPageFilesReposiotry.getFilesByMemoId(researchPageId);
            Set<FilesDto> files = new HashSet<>();
            if (entities != null) {
                for (HFResearchPageFiles researchFile : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setId(researchFile.getFile().getId());
                    fileDto.setFileName(researchFile.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting HF Research page attachments: Research page=" + researchPageId, ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveAttachments(Long researchPageId, Set<FilesDto> attachments) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (attachments != null) {
                Iterator<FilesDto> iterator = attachments.iterator();
                while (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    if (filesDto.getId() == null) {
                        Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                        logger.info("Saved HF Research page attachment file: Research page=" + researchPageId + ", file=" + fileId);
                        HFResearchPageFiles hfResearchPageFiles = new HFResearchPageFiles(researchPageId, fileId);
                        hfResearchPageFilesReposiotry.save(hfResearchPageFiles);
                        logger.info("Saved HF Research page attachment info: Research page=" + researchPageId + ", file=" + fileId);

                        FilesDto newFileDto = new FilesDto();
                        newFileDto.setId(fileId);
                        newFileDto.setFileName(filesDto.getFileName());
                        dtoSet.add(newFileDto);
                    }
                }
            }
            return dtoSet;
        }catch (Exception ex){
            logger.error("Error saving HF Research page attachments: Research page=" + researchPageId, ex);
        }
        return null;
    }

    @Override
    public MemoDeleteResultDto delete(Long pageId, String username) {
        try {
            HFResearchPage page = this.hfResearchPageRepository.findOne(pageId);

            if (page == null) {
                logger.error("Error deleting HF Research page: Research page=" + pageId);
                return new MemoDeleteResultDto("Not done!", StatusResultType.FAIL, "", "HF Research page does not exist", "");
            }

            List<HFResearchPageFiles> entities = hfResearchPageFilesReposiotry.getFilesByMemoId(pageId);

            for(HFResearchPageFiles entity: entities){
                hfResearchPageFilesReposiotry.delete(entity.getId());
                fileService.delete(entity.getFile().getId());
            }

            this.hfResearchPageRepository.delete(page);

            logger.info("Deleted HF Research page: Research page=" + pageId + ", updater=" + username);
            return new MemoDeleteResultDto("Done!", StatusResultType.SUCCESS, "", "Successfully deleted HF Research page", "");

        } catch (Exception ex){
            logger.error("Error deletingHF Research page: Research page=" + pageId, ex);
            return new MemoDeleteResultDto("Not done!", StatusResultType.FAIL, "", "Error deleting HF Research page", "");
        }
    }
}
