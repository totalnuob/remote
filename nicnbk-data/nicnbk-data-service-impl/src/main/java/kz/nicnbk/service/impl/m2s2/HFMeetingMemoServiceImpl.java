package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.m2s2.HFMeetingMemoRepository;
import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.service.api.m2s2.HFMeetingMemoService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.converter.m2s2.HFMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MeetingMemoService memoService;

    @Override
    public Long save(HedgeFundsMeetingMemoDto memoDto) {
        // save memo
        HedgeFundsMeetingMemo entity = converter.assemble(memoDto);
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
        // get attachment files
        memoDto.setFiles(memoService.getAttachments(id));

        return memoDto;
    }
}
