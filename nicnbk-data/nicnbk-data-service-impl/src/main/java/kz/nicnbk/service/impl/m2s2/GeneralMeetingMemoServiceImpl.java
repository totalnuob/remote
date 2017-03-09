package kz.nicnbk.service.impl.m2s2;

import com.auth0.jwt.JWT;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.m2s2.GeneralMeetingMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.m2s2.GeneralMeetingMemoService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.converter.m2s2.GeneralMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.employee.EmployeeDto;
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
    private MeetingMemoService memoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(GeneralMeetingMemoDto memoDto) {
        // save memo
        GeneralMeetingMemo entity = converter.assemble(memoDto);
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
    public GeneralMeetingMemoDto get(Long id) {
        GeneralMeetingMemo entity = repository.findOne(id);
        GeneralMeetingMemoDto memoDto = converter.disassemble(entity);

        // get attachment files
        memoDto.setFiles(memoService.getAttachments(id));

        if(entity.getCreator() != null){
            memoDto.setOwner(entity.getCreator().getUsername());
            //memoDto.setOwner("galym");
        }
        return memoDto;
    }

    @Override
    public boolean checkAccess(String token, Long memoId) {
        TokenUserInfo tokenUserInfo = this.tokenService.decode(token);
        if(tokenUserInfo != null){
            EmployeeDto employeeDto = this.employeeService.findByUsername(tokenUserInfo.getUsername());
            if(employeeDto != null){
                MeetingMemo meetingMemo = this.repository.findOne(memoId);
                if(meetingMemo != null){
                    return meetingMemo.getCreator() == null || (employeeDto.getId() == meetingMemo.getCreator().getId());
                }
            }
        }
        return false;
    }


}
