package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.m2s2.HFMeetingMemoRepository;
import kz.nicnbk.repo.api.m2s2.MemoFilesRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.HFMeetingMemoService;
import kz.nicnbk.service.converter.m2s2.HFMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
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
    private FileService fileService;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Override
    public Long save(HedgeFundsMeetingMemoDto memoDto) {
        // save memo
        HedgeFundsMeetingMemo entity = converter.assemble(memoDto);
        Long memoId = repository.save(entity).getId();

        // save files
        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
            for(FilesDto filesDto: memoDto.getFiles()){
                Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCode());
                MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                memoFilesRepository.save(memoFiles);
            }
        }

        //TODO: delete files?

        return memoId;
    }

    @Override
    public HedgeFundsMeetingMemoDto get(Long id) {
        HedgeFundsMeetingMemo entity = repository.findOne(id);
        return converter.disassemble(entity);
    }
}
