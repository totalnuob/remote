package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.m2s2.REMeetingMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.RealEstateMeetingMemo;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.api.m2s2.REMeetingMemoService;
import kz.nicnbk.service.converter.m2s2.REMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.m2s2.RealEstateMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class REMeetingMemoServiceImpl implements REMeetingMemoService {

    @Autowired
    private REMeetingMemoRepository repository;

    @Autowired
    private REMeetingMemoEntityConverter converter;

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(RealEstateMeetingMemoDto memoDto) {
        // assemble
        RealEstateMeetingMemo entity = converter.assemble(memoDto);
        // set creator
        if(memoDto.getId() == null && memoDto.getOwner() != null){
            Employee employee = this.employeeRepository.findByUsername(memoDto.getOwner());
            entity.setCreator(employee);
        }else{
            Employee employee = this.repository.findOne(memoDto.getId()).getCreator();
            entity.setCreator(employee);
        }
        Long memoId = repository.save(entity).getId();

        // save files
        /*
        if(memoDto.getFiles() != null && !memoDto.getFiles().isEmpty()){
            for(FilesDto filesDto: memoDto.getFiles()){
                Long fileId = fileService.save(filesDto, FileTypeLookup.MEMO_ATTACHMENT.getCode());
                MemoFiles memoFiles = new MemoFiles(memoId, fileId);
                memoFilesRepository.save(memoFiles);
            }
        }
        */

        //TODO: delete files?

        return memoId;
    }

    @Override
    public RealEstateMeetingMemoDto get(Long id) {
        RealEstateMeetingMemo entity = repository.findOne(id);

        RealEstateMeetingMemoDto memoDto = converter.disassemble(entity);
        // get attachment files
        memoDto.setFiles(memoService.getAttachments(id));

        if(entity.getCreator() != null){
            memoDto.setOwner(entity.getCreator().getUsername());
            //memoDto.setOwner("galym");
        }

        return memoDto;
    }
}
