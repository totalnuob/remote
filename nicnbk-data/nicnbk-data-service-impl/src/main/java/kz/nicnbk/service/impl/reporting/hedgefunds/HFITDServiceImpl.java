package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFITDRepository;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITD;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.service.api.reporting.hedgefunds.HFITDService;
import kz.nicnbk.service.converter.reporting.ReportingHFITDRecordConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.SingularityITDRecordDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HFITDServiceImpl implements HFITDService {

    private static final Logger logger = LoggerFactory.getLogger(HFITDServiceImpl.class);

    @Autowired
    private ReportingHFITDRecordConverter converter;

    @Autowired
    private ReportingHFITDRepository repository;

    @Override
    public ReportingHFITD assemble(SingularityITDRecordDto dto) {
        return dto != null ? this.converter.assemble(dto): null;
    }

    @Override
    public List<ReportingHFITD> assembleList(List<SingularityITDRecordDto> dtoList, Long reportId) {
        if(dtoList != null && !dtoList.isEmpty()){
            for(SingularityITDRecordDto dto: dtoList){
                dto.setPeriodicReport(new PeriodicReportDto(reportId));
            }
            return this.converter.assembleList(dtoList);
        }
        return null;
    }

    @Override
    public boolean save(List<ReportingHFITD> entities) {
        try{
            if(entities != null && !entities.isEmpty()) {
                this.repository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Failed to save ReportingHFITD entities with exception", ex);
            return false;
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        ConsolidatedReportRecordHolderDto holderDto = new ConsolidatedReportRecordHolderDto();
        List<ReportingHFITD> entitiesA = this.repository.getEntitiesByReportIdAndTranche(reportId, 1);
        if(entitiesA != null && !entitiesA.isEmpty()){
            holderDto.setRecordsITDTrancheA(this.converter.disassembleList(entitiesA));
        }
        List<ReportingHFITD> entitiesB = this.repository.getEntitiesByReportIdAndTranche(reportId, 2);
        if(entitiesB != null && !entitiesB.isEmpty()){
            holderDto.setRecordsITDTrancheB(this.converter.disassembleList(entitiesB));
        }
        return holderDto;
    }

    @Override
    public boolean deleteByReportIdAndTranche(Long reportId, int tranche) {
        try {
            this.repository.deleteByReportIdAndTranche(reportId, tranche);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting ReportingHFITD records with report id=" + reportId);
            return false;
        }
    }
}
