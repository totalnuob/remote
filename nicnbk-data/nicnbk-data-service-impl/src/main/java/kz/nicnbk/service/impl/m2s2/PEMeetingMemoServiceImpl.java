package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.m2s2.MeetingMemoRepository;
import kz.nicnbk.repo.api.m2s2.PEMeetingMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.api.m2s2.PEMeetingMemoService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.m2s2.MeetingMemoEntityConverter;
import kz.nicnbk.service.converter.m2s2.PEMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class PEMeetingMemoServiceImpl implements PEMeetingMemoService {

    private static final Logger logger = LoggerFactory.getLogger(PEMeetingMemoServiceImpl.class);

    @Autowired
    private PEMeetingMemoRepository repository;

    @Autowired
    private PEMeetingMemoEntityConverter converter;

    @Autowired
    private MeetingMemoEntityConverter memoConverter;

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private MeetingMemoRepository memoRepository;

    @Autowired
    private PEFundService fundService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(PrivateEquityMeetingMemoDto memoDto, String updater) {

        try {
            // assemble
            PrivateEquityMeetingMemo entity = converter.assemble(memoDto);
            if(memoDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(memoDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.repository.findOne(memoDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = repository.findOne(memoDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long memoId = repository.save(entity).getId();
            logger.info(memoDto.getId() == null ? "PE memo created: " + memoId + ", by " + entity.getCreator().getUsername() :
                    "PE memo updated: " + memoId + ", by " + updater);
            return memoId;
        }catch (Exception ex){
            logger.error("Error saving PE memo: " + (memoDto != null && memoDto.getId() != null ? memoDto.getId() : "new") ,ex);
            return null;
        }

        // save files
        /*
        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
            for(FilesDto filesDto: memoDto.getFiles()){
                if(filesDto.getId() == null || filesDto.getId().longValue() == 0) {
                    // new attachments
                    Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                    MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                    memoFilesRepository.save(memoFiles);
                }else{
                    // existing attachments

                }
            }
        }
        */
    }

    @Override
    public PrivateEquityMeetingMemoDto get(Long id) {
        try {
            PrivateEquityMeetingMemo entity = repository.getFullEagerById(id);
            PrivateEquityMeetingMemoDto memoDto = converter.disassemble(entity);

            // set firms funds
            if (memoDto.getFirm() != null) {
                memoDto.getFirm().setFunds(this.fundService.loadFirmFunds(memoDto.getFirm().getId(), false));
            }

            // get attachment files
            memoDto.setFiles(memoService.getAttachments(id));

            return memoDto;
        }catch(Exception ex){
            logger.error("Error loading PE memo: " + id, ex);
        }
        return null;
    }

    @Override
    public MemoPagedSearchResult search(MemoSearchParams searchParams) {
        try {
            Page<PrivateEquityMeetingMemo> memoPage = null;
            int page = 0;
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : memoService.DEFAULT_PAGE_SIZE;

            if ((searchParams.getFundName() == null || searchParams.getFundName().isEmpty()) && searchParams.getMeetingType() == null
                    && searchParams.getFromDate() == null && searchParams.getFromDate() == null && searchParams.getToDate() == null) {
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                memoPage = repository.findAllByFirmIdOrderByMeetingDateDesc(searchParams.getFirmId(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                if (searchParams.getFromDate() == null && searchParams.getToDate() == null) {
                    memoPage = repository.findWithoutDates(
                            searchParams.getFirmId(),
                            StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(),
                            searchParams.getFundName(),
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
                } else if (searchParams.getFromDate() != null && searchParams.getToDate() != null) {
                    memoPage = repository.findBothDates(searchParams.getFirmId(), StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(), searchParams.getToDate(),
                            new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
                } else if (searchParams.getFromDate() != null) {
                    memoPage = repository.findDateFrom(searchParams.getFirmId(), StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getFromDate(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));

                } else {
                    memoPage = repository.findDateTo(searchParams.getFirmId(), StringUtils.isValue(searchParams.getMeetingType()) ? searchParams.getMeetingType() : null,
                            //StringUtils.isValue(searchParams.getMemoType()) ? searchParams.getMemoType() : null,
                            searchParams.getMemoType(),
                            searchParams.getFirmName(), searchParams.getFundName(), searchParams.getToDate(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
                }
            }

            MemoPagedSearchResult result = new MemoPagedSearchResult();
            if (memoPage != null) {
                result.setTotalElements(memoPage.getTotalElements());
                if (memoPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(memoService.DEFAULT_PAGES_PER_VIEW, page + 1));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(memoService.DEFAULT_PAGES_PER_VIEW,
                            page + 1, result.getShowPageFrom(), memoPage.getTotalPages()));
                }
                result.setTotalPages(memoPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setMemos(memoConverter.disPE(memoPage.getContent()));
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching pe memos", ex);
        }
        return null;
    }
}
