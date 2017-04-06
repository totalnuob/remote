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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class GeneralMeetingMemoServiceImpl implements GeneralMeetingMemoService {

    private static final Logger logger = LoggerFactory.getLogger(GeneralMeetingMemoServiceImpl.class);

    @Autowired
    private GeneralMeetingMemoRepository generalMeetingMemoRepository;

    @Autowired
    private GeneralMeetingMemoEntityConverter generalMeetingMemoEntityConverter;

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(GeneralMeetingMemoDto memoDto, String updater) {
        try {
            // save memo
            GeneralMeetingMemo entity = generalMeetingMemoEntityConverter.assemble(memoDto);
            if(memoDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(memoDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.generalMeetingMemoRepository.findOne(memoDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = generalMeetingMemoRepository.findOne(memoDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long memoId = generalMeetingMemoRepository.save(entity).getId();
            logger.info(memoDto.getId() == null ? "GENERAL memo created: " + memoId + ", by " + entity.getCreator().getUsername() :
                    "GENERAL memo updated: " + memoId + ", by " + updater);
            return memoId;
        }catch (Exception ex){
            logger.error("Error saving GENERAL memo: " + (memoDto != null && memoDto.getId() != null ? memoDto.getId() : "new") ,ex);
            return null;
        }

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
    }

    @Override
    public GeneralMeetingMemoDto get(Long id) {
        try {
            GeneralMeetingMemo entity = generalMeetingMemoRepository.findOne(id);
            GeneralMeetingMemoDto memoDto = generalMeetingMemoEntityConverter.disassemble(entity);

            // get attachment files
            memoDto.setFiles(memoService.getAttachments(id));

            if (entity.getCreator() != null) {
                memoDto.setOwner(entity.getCreator().getUsername());
            }
            return memoDto;
        }catch(Exception ex){
            logger.error("Error loading general memo: " + id, ex);
        }
        return null;
    }

    @Override
    public boolean checkOwner(String token, Long memoId) {
        TokenUserInfo tokenUserInfo = this.tokenService.decode(token);
        // check admin role
        for(String role: tokenUserInfo.getRoles()){
            if(role.equalsIgnoreCase("ROLE_ADMIN")){
                return true;
            }
        }
        if(tokenUserInfo != null){
            EmployeeDto employeeDto = this.employeeService.findByUsername(tokenUserInfo.getUsername());
            if(employeeDto != null){
                MeetingMemo meetingMemo = this.generalMeetingMemoRepository.findOne(memoId);
                if(meetingMemo != null){
                    boolean access = meetingMemo.getCreator() == null || (employeeDto.getId() == meetingMemo.getCreator().getId());
                    if(!access){
                        logger.error("General memo owner check failed: employee=" + employeeDto.getUsername()+ ", memo=" + memoId);
                    }
                }
            }
        }
        logger.error("General memo owner check failed: user authentication failed");
        return false;
    }


}
