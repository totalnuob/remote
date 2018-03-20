package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.api.reporting.NICReportingChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.PreviousYearInputDataRepository;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PreviousYearInputData;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.PeriodicReportPrevYearInputService;
import kz.nicnbk.service.converter.common.BaseTypeEntityConverter;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import kz.nicnbk.service.dto.reporting.PreviousYearInputDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;


/**
 * Created by magzumov on 17.01.2018.
 */
@Service
public class PeriodicReportPrevYearInputServiceImpl implements PeriodicReportPrevYearInputService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportPrevYearInputServiceImpl.class);

    @Autowired
    private PreviousYearInputDataRepository previousYearInputDataRepository;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private BaseTypeEntityConverter baseTypeEntityConverter;

    @Autowired
    private NICReportingChartOfAccountsRepository nicReportingChartOfAccountsRepository;

    @Override
    public List<PreviousYearInputDataDto> getPreviousYearInputData(Long reportId) {
        try {
            List<PreviousYearInputDataDto> records = new ArrayList<>();
            List<PreviousYearInputData> entities = previousYearInputDataRepository.getEntitiesByReportId(reportId);
            if (entities != null) {
                for (PreviousYearInputData entity : entities) {
                    PreviousYearInputDataDto record = new PreviousYearInputDataDto();
                    if (entity.getChartOfAccounts() != null) {
                        BaseDictionaryDto nicChartAccountsBaseDto = baseTypeEntityConverter.disassemble(entity.getChartOfAccounts());
                        NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                        if (entity.getChartOfAccounts().getNbChartOfAccounts() != null) {
                            nicChartAccountsDto.setNBChartOfAccounts(baseTypeEntityConverter.disassemble(entity.getChartOfAccounts().getNbChartOfAccounts()));
                        }
                        record.setChartOfAccounts(nicChartAccountsDto);
                    }
                    record.setAccountBalance(entity.getAccountBalance());
                    record.setAccountBalanceKZT(entity.getAccountBalanceKZT());
                    if (entity.getReport() != null) {
                        record.setReport(this.periodicReportService.getPeriodicReport(reportId));
                    }
                    records.add(record);
                }
            }
            return records;
        }catch (Exception ex){
            logger.error("Error loading previous year input data for report id " + reportId, ex);
            return null;
        }
    }

    @Override
    public List<PreviousYearInputDataDto> getPreviousYearInputDataFromPreviousMonth(Long reportId) {

        try {
            List<PreviousYearInputDataDto> records = new ArrayList<>();
            PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
            if (currentReport != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
                PeriodicReportDto previousReport = this.periodicReportService.findReportByReportDate(previousDate);
                List<PreviousYearInputData> entities = previousYearInputDataRepository.getEntitiesByReportId(previousReport.getId());
                if (entities != null) {
                    for (PreviousYearInputData entity : entities) {
                        PreviousYearInputDataDto record = new PreviousYearInputDataDto();
                        if (entity.getChartOfAccounts() != null) {
                            BaseDictionaryDto nicChartAccountsBaseDto = baseTypeEntityConverter.disassemble(entity.getChartOfAccounts());
                            NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                            if (entity.getChartOfAccounts().getNbChartOfAccounts() != null) {
                                nicChartAccountsDto.setNBChartOfAccounts(baseTypeEntityConverter.disassemble(entity.getChartOfAccounts().getNbChartOfAccounts()));
                            }
                            record.setChartOfAccounts(nicChartAccountsDto);
                        }
                        record.setAccountBalance(entity.getAccountBalance());
                        record.setAccountBalanceKZT(entity.getAccountBalanceKZT());
                        record.setReport(previousReport);
                        records.add(record);
                    }
                }
            }
            return records;
        }catch (Exception ex){
            logger.error("Error loading Previous Year Input Data for previous month, report id " + reportId, ex);
            return null;
        }
    }

    @Transactional // if DB operation fails, no record will be saved, i.e. no partial commits
    @Override
    public EntityListSaveResponseDto savePreviousYearInputData(List<PreviousYearInputDataDto> records, Long reportId) {
        EntityListSaveResponseDto entityListSaveResponseDto = new EntityListSaveResponseDto();
        try {
            if (records != null && reportId != null) {
                try {
                    // check report status
                    PeriodicReportDto periodicReport = this.periodicReportService.getPeriodicReport(reportId);
                    if (periodicReport != null && periodicReport.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                        entityListSaveResponseDto.setErrorMessageEn("Cannot edit report with status 'SUBMITTED': report id" + reportId);
                        return entityListSaveResponseDto;
                    }

                    //this.previousYearInputDataRepository.getEntitiesByReportId(reportId);
                    this.previousYearInputDataRepository.deleteAllByReportId(reportId);

                    for (PreviousYearInputDataDto record : records) {
                        PreviousYearInputData entity = new PreviousYearInputData();
                        entity.setAccountBalance(record.getAccountBalance());
                        entity.setAccountBalanceKZT(record.getAccountBalanceKZT());
                        entity.setReport(new PeriodicReport(reportId));
                        if (record.getChartOfAccounts() != null) {
                            NICReportingChartOfAccounts nicReportingChartOfAccounts = this.nicReportingChartOfAccountsRepository.findByCode(record.getChartOfAccounts().getCode());
                            if (nicReportingChartOfAccounts != null) {
                                entity.setChartOfAccounts(nicReportingChartOfAccounts);
                            } else {
                                // throw error
                                logger.error("Could not find NIC chart of account for '" + record.getChartOfAccounts().getCode() + "'");
                                entityListSaveResponseDto.setErrorMessageEn("Could not find NIC chart of account for '" +
                                        record.getChartOfAccounts().getCode() + "'. Please check lookup tables.");
                                return entityListSaveResponseDto;
                            }
                        } else {
                            // throw error
                            logger.error("Previous Year input data chart of accounts not specified");
                            entityListSaveResponseDto.setErrorMessageEn("Previous Year input data chart of accounts not specified");
                            return entityListSaveResponseDto;
                        }

                        this.previousYearInputDataRepository.save(entity);
                    }
                    entityListSaveResponseDto.setSuccessMessageEn("Successfully saved records");
                    logger.info("Successfully saved Previous Year input data for report id " + reportId);
                    return entityListSaveResponseDto;
                } catch (Exception ex) {
                    logger.error("Error saving previous year input data for report id " + reportId, ex);
                    entityListSaveResponseDto.setErrorMessageEn("Error saving previous year input data");
                    return entityListSaveResponseDto;
                }
            } else if (records == null) {
                logger.error("Error saving previous year input data: no records specified");
                entityListSaveResponseDto.setErrorMessageEn("Error saving previous year input data: no records specified");
                return entityListSaveResponseDto;
            } else {
                logger.error("Error saving previous year input data: no report id specified");
                entityListSaveResponseDto.setErrorMessageEn("Error saving previous year input data: no report id specified");
                return entityListSaveResponseDto;
            }
        }catch (Exception ex){
            logger.error("Error saving previous year input data: report id " + reportId, ex);
            entityListSaveResponseDto.setErrorMessageEn("Error saving previous year input data: report id " + reportId);
            return entityListSaveResponseDto;
        }

//        if(existingEntities != null && !existingEntities.isEmpty()) {
//            this.previousYearInputDataRepository.save(existingEntities);
//        }
//        return entityListSaveResponseDto;
    }
}
