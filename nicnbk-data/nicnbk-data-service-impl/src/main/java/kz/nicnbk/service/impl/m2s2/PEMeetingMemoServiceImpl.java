package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.m2s2.MemoFilesRepository;
import kz.nicnbk.repo.api.m2s2.PEMeetingMemoRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.api.m2s2.PEMeetingMemoService;
import kz.nicnbk.service.converter.m2s2.PEMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class PEMeetingMemoServiceImpl implements PEMeetingMemoService {

    @Autowired
    private PEMeetingMemoRepository repository;

    @Autowired
    private PEMeetingMemoEntityConverter converter;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Autowired
    private MeetingMemoService memoService;

    @Override
    public Long save(PrivateEquityMeetingMemoDto memoDto) {
        // save memo
        PrivateEquityMeetingMemo entity = converter.assemble(memoDto);
        Long memoId = repository.save(entity).getId();

        // save files
        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
            for(FilesDto filesDto: memoDto.getFiles()){
                Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
                MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                memoFilesRepository.save(memoFiles);
            }
        }

        //TODO: delete existing files not in dto.files ?

        return memoId;
    }

    @Override
    public PrivateEquityMeetingMemoDto get(Long id) {
        PrivateEquityMeetingMemo entity = repository.getFullEagerById(id);
        PrivateEquityMeetingMemoDto memoDto = converter.disassemble(entity);

        // get attachment files
        memoDto.setFiles(memoService.getAttachments(id));

        return memoDto;
    }
}
