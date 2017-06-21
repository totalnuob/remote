package kz.nicnbk.service.impl.macromonitor;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.macromonitor.MacroMonitorScoreRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.service.api.macromonitor.MacroMonitorScoreService;
import kz.nicnbk.service.converter.macromonitor.MacroMonitorScoreEntityConverter;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */
@Service
public class MacroMonitorScoreServiceImpl implements MacroMonitorScoreService {

    private static final Logger logger = LoggerFactory.getLogger(MacroMonitorScoreServiceImpl.class);

    @Autowired
    private MacroMonitorScoreRepository repository;

    @Autowired
    private MacroMonitorScoreEntityConverter converter;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(List<MacroMonitorScoreDto> macroMonitorScoreDtoList, String username) {
        try {

            if (macroMonitorScoreDtoList != null && macroMonitorScoreDtoList.size() != 0) {
                MacroMonitorScore entity = this.converter.assemble(macroMonitorScoreDtoList.get(0));
                Integer id = entity.getType().getId();
                Employee employee = this.employeeRepository.findByUsername(username);

                repository.deleteByTypeId(id);

                for (MacroMonitorScoreDto dto : macroMonitorScoreDtoList) {
                    MacroMonitorScore tempEntity = this.converter.assemble(dto);
                    tempEntity.setCreator(employee);
                    tempEntity.setUpdateDate(new Date());
                    tempEntity.setUpdater(employee);
                    repository.save(tempEntity);
                }
                logger.info("Macromonitor scores updated: " + id + ", by " + username);
                return 1L;
            }
        } catch (Exception ex) {
            logger.error("Error updating Macromonitor scores: " + (macroMonitorScoreDtoList != null && macroMonitorScoreDtoList.size() != 0 ? this.converter.assemble(macroMonitorScoreDtoList.get(0)).getType().getId() : "new"), ex);
        }
        return 1L;
    }

    @Override
    public List<MacroMonitorScoreDto> getList(Integer typeId) {
        try {
            List<MacroMonitorScore> entities = this.repository.getEntitiesByType(typeId);
            List<MacroMonitorScoreDto> dtoList = this.converter.disassembleList(entities);
            return dtoList;
        } catch (Exception ex) {
            logger.error("Error loading Macromonitor scores: " + typeId, ex);
        }
        return null;
    }

    @Override
    public Long deleteAll(Integer typeId, String username) {
        try {
            repository.deleteByTypeId(typeId);
            logger.info("Macromonitor scores deleted: " + typeId + ", by " + username);
            return 1L;
        } catch (Exception ex) {
            logger.error("Failed to delete Macromonitor scores: " + typeId + ", by " + username, ex);
        }
        return null;
    }
}
