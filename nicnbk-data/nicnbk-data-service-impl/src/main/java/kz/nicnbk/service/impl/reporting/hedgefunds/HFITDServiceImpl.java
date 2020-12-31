package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFITDHistoricalRepository;
import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFITDRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITD;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITDHistorical;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFITDService;
import kz.nicnbk.service.converter.reporting.ReportingHFITDHistoricalRecordConverter;
import kz.nicnbk.service.converter.reporting.ReportingHFITDRecordConverter;
import kz.nicnbk.service.dto.reporting.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.*;

@Service
public class HFITDServiceImpl implements HFITDService {

    private static final Logger logger = LoggerFactory.getLogger(HFITDServiceImpl.class);

    @Autowired
    private ReportingHFITDRecordConverter converter;

    @Autowired
    private ReportingHFITDHistoricalRecordConverter historicalITDConverter;

    @Autowired
    private ReportingHFITDRepository repository;

    @Autowired
    private ReportingHFITDHistoricalRepository historicalITDRepository;

    @Autowired
    private PeriodicReportService periodicReportService;

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
    public List<ReportingHFITDHistorical> assembleListHRS(List<SingularityITDHistoricalRecordDto> dtoList, Long reportId) {
        if(dtoList != null && !dtoList.isEmpty()){
            for(SingularityITDHistoricalRecordDto dto: dtoList){
                dto.setPeriodicReport(new PeriodicReportDto(reportId));
            }
            return this.historicalITDConverter.assembleList(dtoList);
        }
        return null;
    }

    @Override
    public boolean updateHRS(List<SingularityITDHistoricalRecordDto> dtoList, Long reportId) {
        List<ReportingHFITDHistorical> entitiesToSave = new ArrayList<>();
        List<SingularityITDHistoricalRecordDto> dtoToSave = new ArrayList<>();
        if(dtoList != null && !dtoList.isEmpty()){
            for(SingularityITDHistoricalRecordDto dto: dtoList){
                // find matching entities
                List<ReportingHFITDHistorical> entities =
                        this.historicalITDRepository.findByDateAndFundNameAndPortfolio(dto.getDate(), dto.getFundName(), dto.getPortfolio());
                if(entities != null && !entities.isEmpty()){
                    // check if changed
                    for(ReportingHFITDHistorical entity: entities){
                        boolean same = dto.getNetContribution() != null && entity.getNetContribution() != null ?
                                (dto.getNetContribution().doubleValue() == entity.getNetContribution().doubleValue()) :
                                dto.getNetContribution() == null && entity.getNetContribution() == null ? true : false;
                        same = same && (dto.getEndingBalance() != null && entity.getEndingBalance() != null ?
                                (dto.getEndingBalance().doubleValue() == entity.getEndingBalance().doubleValue()) :
                                dto.getEndingBalance() == null && entity.getEndingBalance() == null ? true : false);
                        if(!same){
                            entity.setNetContribution(dto.getNetContribution());
                            entity.setEndingBalance(dto.getEndingBalance());
                            entity.setReport(new PeriodicReport(reportId));
                            entity.setUpdateDate(new Date());
                            entitiesToSave.add(entity);
                        }
                    }
                }else{
                    dtoToSave.add(dto);
                }
            }
            if(!dtoToSave.isEmpty()){
                List<ReportingHFITDHistorical> entities = this.historicalITDConverter.assembleList(dtoToSave, reportId);
                entitiesToSave.addAll(entities);
            }
            if(!entitiesToSave.isEmpty()){
                return saveHRS(entitiesToSave);
            }
        }
        return true;
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
    public boolean saveHRS(List<ReportingHFITDHistorical> entities) {
        try{
            if(entities != null && !entities.isEmpty()) {
                this.historicalITDRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Failed to save ReportingHFITDHistorical entities with exception", ex);
            return false;
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto getParsedData(Long reportId) {
        ConsolidatedReportRecordHolderDto holderDto = new ConsolidatedReportRecordHolderDto();
        List<ReportingHFITD> entitiesA = this.repository.getEntitiesByReportIdAndTranche(reportId, 1);
        if(entitiesA != null && !entitiesA.isEmpty()){
            holderDto.setRecordsITDTrancheA(this.converter.disassembleList(entitiesA));
        }
        List<ReportingHFITD> entitiesB = this.repository.getEntitiesByReportIdAndTranche(reportId, 2);
        if(entitiesB != null && !entitiesB.isEmpty()){
            holderDto.setRecordsITDTrancheB(this.converter.disassembleList(entitiesB));
        }

        // HRS
        List<ReportingHFITDHistorical> entitiesAHRS = this.historicalITDRepository.getEntitiesByReportIdAndTranche(reportId, 1);
        if(entitiesAHRS != null && !entitiesAHRS.isEmpty()){
            holderDto.setRecordsITDTrancheAHRS(this.historicalITDConverter.disassembleList(entitiesAHRS));
        }
        List<ReportingHFITDHistorical> entitiesBHRS = this.historicalITDRepository.getEntitiesByReportIdAndTranche(reportId, 2);
        if(entitiesBHRS != null && !entitiesBHRS.isEmpty()){
            holderDto.setRecordsITDTrancheBHRS(this.historicalITDConverter.disassembleList(entitiesBHRS));
        }

        return holderDto;
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.repository.deleteByReportId(reportId);
            this.historicalITDRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting ReportingHFITD records with report id=" + reportId);
            return false;
        }
    }

    @Override
    public SingularityITDHistoricalRecordHolderDto getHistoricalData(Date date) {
        Date reportDate = date;
        SingularityITDHistoricalRecordHolderDto holder = new SingularityITDHistoricalRecordHolderDto();
        List<SingularityITDHistoricalRecordDto> dtoList = new ArrayList<>();
        if(dtoList.isEmpty()){
            PeriodicReportDto currentPeriodicReport = this.periodicReportService.findReportByReportDate(date);
            if(currentPeriodicReport != null){
                List<ReportingHFITDHistorical> entities = this.historicalITDRepository.findByReportId(currentPeriodicReport.getId());
                if(entities != null && !entities.isEmpty()){
                    for(ReportingHFITDHistorical entity: entities){
                        dtoList.add(this.historicalITDConverter.disassemble(entity));
                    }
                }
            }
        }
        if(dtoList.isEmpty()){
            reportDate = null;
            PeriodicReportDto mostRecentReport = this.periodicReportService.findMostRecentReport();
            if(mostRecentReport != null){
                reportDate = mostRecentReport.getReportDate();
                while(reportDate.compareTo(date) >= 0){
                    PeriodicReportDto reportDto = this.periodicReportService.findReportByReportDate(reportDate);
                    List<ReportingHFITDHistorical> entities = this.historicalITDRepository.findByReportId(reportDto.getId());
                    if(entities != null && !entities.isEmpty()){
                        for(ReportingHFITDHistorical entity: entities){
                            dtoList.add(this.historicalITDConverter.disassemble(entity));
                        }
                        break;
                    }
                    reportDate = DateUtils.getLastDayOfPreviousMonth(reportDate);
                }
            }
        }

        holder.setRecords(dtoList);
        holder.setReportDate(dtoList.isEmpty() ? null : reportDate);
        return holder;
    }
}
