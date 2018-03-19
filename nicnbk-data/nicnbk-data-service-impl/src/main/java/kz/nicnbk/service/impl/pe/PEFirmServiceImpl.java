package kz.nicnbk.service.impl.pe;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.pe.PEFirmRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PEFirmEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEPagedSearchResult;
import kz.nicnbk.service.dto.pe.PESearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@Service
public class PEFirmServiceImpl implements PEFirmService {

    private static final Logger logger = LoggerFactory.getLogger(PEFirmServiceImpl.class);


    @Autowired
    private PEFirmRepository peFirmRepository;

    @Autowired
    private PEFirmEntityConverter converter;

    @Autowired
    private PEFundService fundService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService;

    @Override
    public Long save(PEFirmDto firmDto, String updater) {
        try {
            PEFirm entity = converter.assemble(firmDto);
            if(firmDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(firmDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.peFirmRepository.findOne(firmDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = peFirmRepository.findOne(firmDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long id = peFirmRepository.save(entity).getId();
            logger.info(firmDto.getId() == null ? "PE firm created: " + id + ", by " + entity.getCreator().getUsername() :
                    "PE firm updated: " + id + ", by " + updater);
            return id;
        }catch (Exception ex){
            logger.error("Error saving PE firm: " + (firmDto != null && firmDto.getId() != null ? firmDto.getId() : "new") ,ex);
        }
        return null;
    }

    @Override
    public PEFirmDto get(Long id) {
        try {
            PEFirm entity = this.peFirmRepository.findOne(id);
            PEFirmDto firmDto = this.converter.disassemble(entity);
            // load funds
            firmDto.setFunds(this.fundService.loadFirmFunds(id, false));
            return firmDto;
        }catch(Exception ex){
            logger.error("Error loading PE firm: " + id, ex);
        }
        return null;
    }

    @Override
    public List<PEFirmDto> findAll() {
        try {
            return converter.disassembleList(peFirmRepository.findAllByOrderByFirmNameAsc());
        }catch (Exception ex){
            logger.error("Failed to load all PE firms", ex);
        }
        return null;
    }

    @Override
    public Set<FilesDto> saveLogo(Long firmId, Set<FilesDto> filesDtoSet) {
        try {
            Set<FilesDto> dtoSet = new HashSet<>();
            if (filesDtoSet != null) {
                Iterator<FilesDto> iterator = filesDtoSet.iterator();
                if (iterator.hasNext()) {
                    FilesDto filesDto = iterator.next();
                    System.out.println("123");
                    Long fileId = fileService.save(filesDto, FileTypeLookup.PE_FIRM_LOGO.getCatalog());
//                    logger.info("Saved PE firm logo file: firm=" + firmId + ", file=" + fileId);
//                    MemoFiles memoFiles = new MemoFiles(memoId, fileId);
//                    memoFilesRepository.save(memoFiles);
//                    logger.info("Saved PE firm logo info: firm=" + firmId + ", file=" + fileId);
//
//                    FilesDto newFileDto = new FilesDto();
//                    newFileDto.setId(fileId);
//                    newFileDto.setFileName(filesDto.getFileName());
//                    dtoSet.add(newFileDto);
                }
            }
            return dtoSet;
        } catch (Exception ex) {
            logger.error("Error saving PE firm logo: firm=" + firmId, ex);
        }
        return null;
    }

    @Override
    public PEPagedSearchResult findByName(PESearchParams searchParams) {
        try {
            int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            Page<PEFirm> entityPage = this.peFirmRepository.findByName(searchParams.getName(),
                    //new PageRequest(searchParams.getPage(), searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
                    new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));

            PEPagedSearchResult result = new PEPagedSearchResult();

            if (entityPage != null) {
                result.setTotalElements(entityPage.getTotalElements());
                if (entityPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
                }
                result.setTotalPages(entityPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setFirms(this.converter.disassembleList(entityPage.getContent()));
            }
            return result;
        }catch (Exception ex){
            logger.error("Failed to search PE firms", ex);
        }
        return null;
    }
}