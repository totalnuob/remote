package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.m2s2.HFMeetingMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.api.m2s2.HFMeetingMemoService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.converter.m2s2.HFMeetingMemoEntityConverter;
import kz.nicnbk.service.converter.m2s2.MeetingMemoEntityConverter;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class HFMeetingMemoServiceImpl implements HFMeetingMemoService {

    @Autowired
    private HFMeetingMemoRepository repository;

    @Autowired
    private HFMeetingMemoEntityConverter converter;

    @Autowired
    private MeetingMemoEntityConverter memoConverter;

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private HedgeFundService fundService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(HedgeFundsMeetingMemoDto memoDto) {
        // assemble memo
        HedgeFundsMeetingMemo entity = converter.assemble(memoDto);

        // set creator
        if(memoDto.getId() == null){
            Employee employee = this.employeeRepository.findByUsername(memoDto.getOwner());
            entity.setCreator(employee);
        }else{
            Employee employee = this.repository.findOne(memoDto.getId()).getCreator();
            entity.setCreator(employee);
        }
        // save
        Long memoId = repository.save(entity).getId();

        // save files
        /*
        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
            for(FilesDto filesDto: memoDto.getFiles()){
                if(filesDto.getId() == null || filesDto.getId().longValue() == 0) {
                    // new file
                    Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCode());
                    MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                    memoFilesRepository.save(memoFiles);
                }else{
                    // old file
                }
            }
        }
        */

        //TODO: delete files?

        return memoId;
    }

    @Override
    public HedgeFundsMeetingMemoDto get(Long id) {
        HedgeFundsMeetingMemo entity = repository.findOne(id);

        HedgeFundsMeetingMemoDto memoDto = converter.disassemble(entity);

        // set strategy name
        if(entity.getFund() != null && entity.getFund().getStrategy() != null) {
            memoDto.getFund().setStrategyName(entity.getFund().getStrategy().getNameEn());
        }

        // set managers funds
        if(memoDto.getManager() != null) {
            memoDto.getManager().setFunds(this.fundService.loadManagerFunds(memoDto.getManager().getId()));
        }

        // get attachment files
        memoDto.setFiles(memoService.getAttachments(id));

        return memoDto;
    }

    @Override
    public MemoPagedSearchResult search(MemoSearchParams searchParams) {
        Page<MeetingMemo> memoPage = null;
        int page = 0;
        int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : memoService.DEFAULT_PAGE_SIZE;

        if((searchParams.getFundName() == null || searchParams.getFundName().isEmpty()) && searchParams.getMeetingType() == null
                && searchParams.getFromDate() == null && searchParams.getFromDate() == null && searchParams.getToDate() == null){
            page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            memoPage = repository.findAllByManagerIdOrderByMeetingDateDesc(searchParams.getFirmId(), new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "meetingDate")));
        }else {
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
            }  else if (searchParams.getFromDate() != null) {
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
        if(memoPage != null) {
            result.setTotalElements(memoPage.getTotalElements());
            if(memoPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(memoService.DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(memoService.DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), memoPage.getTotalPages()));
            }
            result.setTotalPages(memoPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if(searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }
            result.setMemos(memoConverter.disassembleList(memoPage.getContent()));

            // TODO: temp, need a new DB structure
            // firm and fund names
            for(MeetingMemoDto memoDto: result.getMemos()){
                if(memoDto.getMemoType() == 3){
                    // HF memo
                    String firmName = this.repository.getManagerNameByMemoId(memoDto.getId());
                    String fundName = this.repository.getFundNameByMemoId(memoDto.getId());
                    memoDto.setFirmName(firmName);
                    memoDto.setFundName(fundName);
                }
            }

        }
        return result;
    }

}
