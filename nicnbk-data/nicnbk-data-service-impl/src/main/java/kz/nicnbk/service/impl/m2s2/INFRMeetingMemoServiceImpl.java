package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.m2s2.INFRMeetingMemoRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.InfrastructureMeetingMemo;
import kz.nicnbk.repo.model.m2s2.RealEstateMeetingMemo;
import kz.nicnbk.service.api.m2s2.INFRMeetingMemoService;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;
import kz.nicnbk.service.api.m2s2.REMeetingMemoService;
import kz.nicnbk.service.converter.m2s2.INFRMeetingMemoEntityConverter;
import kz.nicnbk.service.dto.m2s2.InfrastructureMeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.RealEstateMeetingMemoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by magzumov on 19.07.2016.
 */
@Service
public class INFRMeetingMemoServiceImpl implements INFRMeetingMemoService {

    private static final Logger logger = LoggerFactory.getLogger(INFRMeetingMemoServiceImpl.class);

    @Autowired
    private INFRMeetingMemoRepository infrMeetingMemoRepository;

    @Autowired
    private INFRMeetingMemoEntityConverter infrMeetingMemoEntityConverter;

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(InfrastructureMeetingMemoDto  memoDto, String updater) {
        try {
            // assemble
            InfrastructureMeetingMemo entity = infrMeetingMemoEntityConverter.assemble(memoDto);
            if(memoDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(memoDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.infrMeetingMemoRepository.findOne(memoDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = infrMeetingMemoRepository.findOne(memoDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long memoId = infrMeetingMemoRepository.save(entity).getId();
            logger.info(memoDto.getId() == null ? "RE memo created: " + memoId + ", by " + entity.getCreator().getUsername() :
                    "RE memo updated: " + memoId + ", by " + updater);
            return memoId;
        }catch (Exception ex){
            logger.error("Error saving RE memo: " + (memoDto != null && memoDto.getId() != null ? memoDto.getId() : "new") ,ex);
            return null;
        }
    }

    @Override
    public InfrastructureMeetingMemoDto get(Long id) {
        try {
            InfrastructureMeetingMemo entity = infrMeetingMemoRepository.findOne(id);

            InfrastructureMeetingMemoDto memoDto = infrMeetingMemoEntityConverter.disassemble(entity);
            // get attachment files
            memoDto.setFiles(memoService.getAttachments(id));

            if (entity.getCreator() != null) {
                memoDto.setOwner(entity.getCreator().getUsername());
                //memoDto.setOwner("galym");
            }

            return memoDto;
        }catch (Exception ex){
            logger.error("Error loading RE memo: " + id, ex);
        }
        return null;
    }
}
