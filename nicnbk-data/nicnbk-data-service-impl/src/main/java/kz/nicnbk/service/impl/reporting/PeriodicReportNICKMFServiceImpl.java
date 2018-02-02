package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.NICKMFReportingDataRepository;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.api.reporting.PeriodicReportNICKMFService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.converter.reporting.NICKMFReportingDataConverter;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataDto;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by magzumov on 18.01.2018.
 */
@Service
public class PeriodicReportNICKMFServiceImpl implements PeriodicReportNICKMFService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportNICKMFServiceImpl.class);

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private NICKMFReportingDataRepository nickmfReportingDataRepository;

    @Autowired
    private NICKMFReportingDataConverter nickmfReportingDataConverter;

    @Transactional
    @Override
    public EntityListSaveResponseDto saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto) {
        EntityListSaveResponseDto entityListSaveResponseDto = new EntityListSaveResponseDto();
        try {
            if(dataHolderDto != null && dataHolderDto.getRecords() != null){
                checkNICKMFReportingData(dataHolderDto.getRecords());
                // check report status
                PeriodicReportDto periodicReport = this.periodicReportService.getPeriodicReport(dataHolderDto.getReport().getId());
                if(periodicReport != null && periodicReport.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    entityListSaveResponseDto.setErrorMessageEn("Cannot edit report with status 'SUBMITTED'");
                    return entityListSaveResponseDto;
                }
                this.nickmfReportingDataRepository.deleteAllByReportId(dataHolderDto.getReport().getId());
                for(NICKMFReportingDataDto dto: dataHolderDto.getRecords()){
                    NICKMFReportingData entity = this.nickmfReportingDataConverter.assemble(dto);
                    entity.setReport(new PeriodicReport(dataHolderDto.getReport().getId()));
                    this.nickmfReportingDataRepository.save(entity);
                }
                entityListSaveResponseDto.setSuccessMessageEn("NICK MF records successfully saved");
            }
        }catch (IllegalArgumentException ex){
            logger.error("Error saving NICK MF Reporting data: input validation failed", ex);
            entityListSaveResponseDto.setErrorMessageEn("Input validation failed. " + ex.getMessage());

        }catch (Exception ex){
            logger.error("Error saving NICK MF Reporting data", ex);
            entityListSaveResponseDto.setErrorMessageEn("Error saving NICK MF Reporting data");
        }
        return entityListSaveResponseDto;
    }

    private void checkNICKMFReportingData(List<NICKMFReportingDataDto> records){
        if(records != null){
            double totalSum = 0.0;
            Set<String> codes = new HashSet<>();
            for(NICKMFReportingDataDto record: records){

                if(codes.contains(record.getNicChartOfAccountsCode())){
                    throw new IllegalArgumentException("Duplicate chart of accounts code: " + record.getNbChartOfAccountsCode());
                }

                if(StringUtils.isEmpty(record.getNicChartOfAccountsCode())){
                    throw new IllegalArgumentException("Record missing NIC Chart of accounts code value: " + record.getNbChartOfAccountsCode());
                }else{
                    // check code exists ???
                }

                if(record.getAccountBalance() == null){
                    throw new IllegalArgumentException("Record missing 'Account Balance' value");
                }else{
                    totalSum += record.getAccountBalance().doubleValue();
                }

                codes.add(record.getNicChartOfAccountsCode());
            }

            //
//            if(totalSum > 2 || totalSum < -2){
//                throw new IllegalArgumentException("Total sum = " + totalSum +" ; expected value 0 (or between -1 and 1)");
//            }
        }
    }

    @Override
    public NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId){
        try {
            NICKMFReportingDataHolderDto holderDto = new NICKMFReportingDataHolderDto();
            List<NICKMFReportingData> entities = this.nickmfReportingDataRepository.getEntitiesByReportId(reportId);
            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
            if (report != null) {
                holderDto.setReport(report);
            }
            if (entities != null) {
                List<NICKMFReportingDataDto> records = new ArrayList<>();
                for (NICKMFReportingData entity : entities) {
                    NICKMFReportingDataDto dto = this.nickmfReportingDataConverter.disassemble(entity);
                    records.add(dto);
                }
                holderDto.setRecords(records);
            }
            return holderDto;
        }catch(Exception ex){
            logger.error("Error loading NICK MG reporting data: report id " + reportId, ex);
            return null;
        }
    }

    @Override
    public NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId){
        try {
            NICKMFReportingDataHolderDto holderDto = new NICKMFReportingDataHolderDto();
            PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
            if (currentReport != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
                PeriodicReportDto previousReport = this.periodicReportService.findReportByReportDate(previousDate);
                if (previousReport != null) {
                    List<NICKMFReportingData> entities = this.nickmfReportingDataRepository.getEntitiesByReportId(previousReport.getId());
                    if (currentReport != null) {
                        holderDto.setReport(currentReport);
                    }
                    if (entities != null) {
                        List<NICKMFReportingDataDto> records = new ArrayList<>();
                        for (NICKMFReportingData entity : entities) {
                            NICKMFReportingDataDto dto = this.nickmfReportingDataConverter.disassemble(entity);
                            records.add(dto);
                        }
                        holderDto.setRecords(records);
                    }
                }
            }

            return holderDto;
        }catch (Exception ex){
            logger.error("Error loading NICK MF reporting data for previous month: report id " + reportId, ex);
            return null;
        }
    }
}
