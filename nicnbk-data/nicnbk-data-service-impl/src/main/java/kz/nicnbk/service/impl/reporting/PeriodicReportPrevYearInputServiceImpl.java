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
        List<PreviousYearInputDataDto> records = new ArrayList<>();
        List<PreviousYearInputData> entities = previousYearInputDataRepository.getEntitiesByReportId(reportId);
        if(entities != null){
            for(PreviousYearInputData entity: entities){
                PreviousYearInputDataDto record = new PreviousYearInputDataDto();
                if(entity.getChartOfAccounts() != null) {
                    BaseDictionaryDto nicChartAccountsBaseDto = baseTypeEntityConverter.disassemble(entity.getChartOfAccounts());
                    NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                    if (entity.getChartOfAccounts().getNbChartOfAccounts() != null) {
                        nicChartAccountsDto.setNBChartOfAccounts(baseTypeEntityConverter.disassemble(entity.getChartOfAccounts().getNbChartOfAccounts()));
                    }
                    record.setChartOfAccounts(nicChartAccountsDto);
                }
                record.setAccountBalance(entity.getAccountBalance());
                record.setAccountBalanceKZT(entity.getAccountBalanceKZT());
                if(entity.getReport() != null) {
                    record.setReport(this.periodicReportService.getPeriodicReport(reportId));
                }
                records.add(record);
            }
        }
        return records;
    }

    @Override
    public List<PreviousYearInputDataDto> getPreviousYearInputDataFromPreviousMonth(Long reportId) {

        List<PreviousYearInputDataDto> records = new ArrayList<>();

        PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
        if(currentReport != null) {
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
    }

    @Override
    public boolean savePreviousYearInputData(List<PreviousYearInputDataDto> records, Long reportId) {
        if(records != null && reportId != null){
            try {

                // TODO: check report status
                PeriodicReportDto periodicReport = this.periodicReportService.getPeriodicReport(reportId);
                if(periodicReport != null && periodicReport.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    return false;
                }

                // TODO: rollback on error? Transactional?
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
                            // TODO: throw error
                        }
                    } else {
                        // TODO: throw error
                    }

                    this.previousYearInputDataRepository.save(entity);
                }
            }catch (Exception ex){

                // TODO: rollback on error? Transactional?

                logger.error("Error saving previous year input data for reportId=" + reportId, ex);
                return false;
            }
            return true;
        }else {
            return false;
        }
    }
}
