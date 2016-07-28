package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.m2s2.GeneralMeetingMemoRepository;
import kz.nicnbk.repo.api.m2s2.MemoFilesRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.GeneralMeetingMemoService;
import kz.nicnbk.service.converter.m2s2.GeneralMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.GeneralMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class GeneralMeetingMemoServiceImpl implements GeneralMeetingMemoService {

    @Autowired
    private GeneralMeetingMemoRepository repository;

    @Autowired
    private GeneralMeetingMemoEntityConverter converter;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Override
    public Long save(GeneralMeetingMemoDto memoDto) {
        // save memo
        GeneralMeetingMemo entity = converter.assemble(memoDto);
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
    public GeneralMeetingMemoDto get(Long id) {
        GeneralMeetingMemo entity = repository.findOne(id);
        return converter.disassemble(entity);
    }
}
