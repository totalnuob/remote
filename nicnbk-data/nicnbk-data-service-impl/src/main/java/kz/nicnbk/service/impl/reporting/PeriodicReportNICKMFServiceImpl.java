package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.NICKMFReportingDataRepository;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.api.reporting.PeriodicReportNICKMFService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.converter.reporting.NICKMFReportingDataConverter;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.impl.reporting.lookup.ReserveCalculationsEntityTypeLookup;
import kz.nicnbk.service.impl.reporting.lookup.ReserveCalculationsExpenseTypeLookup;
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

    @Autowired
    private ReserveCalculationService reserveCalculationService;

    @Transactional // if DB operation fails, no record will be saved, i.e. no partial commits
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

//                    Double calculatedValue =
//                            getNICKMFReportingDataCalculatedValue(report, dto.getNbChartOfAccountsCode(), dto.getNicChartOfAccountsName());
//                    dto.setCalculatedAccountBalance(calculatedValue);

                    setNICKMFReportingDataCalculatedValue(dto, report);

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


    private void setNICKMFReportingDataCalculatedValue(NICKMFReportingDataDto dto, PeriodicReportDto reportDto) {
        if(reportDto != null && dto != null && dto.getNbChartOfAccountsCode() != null && dto.getNicChartOfAccountsName() != null){
            int month = DateUtils.getMonth(reportDto.getReportDate()) + 1;
            if(dto.getNbChartOfAccountsCode().equalsIgnoreCase("2923.010") && dto.getNicChartOfAccountsName().equalsIgnoreCase("Начисленная амортизация - Организационные расходы NICK MF")){
                int monthDiff = DateUtils.getMonthsDifference(DateUtils.getDate("31.07.2015"), reportDto.getReportDate());
                Double value = MathUtils.multiply(MathUtils.divide(14963.0, 60.0), new Double(monthDiff));
                dto.setCalculatedAccountBalance(MathUtils.subtract(0.0, value));
                dto.setCalculatedAccountBalanceFormula(" - (14,963 / 60) * " + monthDiff + ", where '" + monthDiff + "' = months difference between 31.07.2017 and " + DateUtils.getDateFormatted(reportDto.getReportDate()));
            }else if(dto.getNbChartOfAccountsCode().equalsIgnoreCase("3393.020") && dto.getNicChartOfAccountsName().equalsIgnoreCase("Комиссия за администрирование к оплате NICK MF")){
                Double value = null;
                NICKMFReportingDataHolderDto previousDataHolder = getNICKMFReportingDataFromPreviousMonth(reportDto.getId());
                if(previousDataHolder != null && previousDataHolder.getRecords() != null){
                    for(NICKMFReportingDataDto record :previousDataHolder.getRecords()){
                        if(record.getNbChartOfAccountsCode().equalsIgnoreCase(dto.getNbChartOfAccountsCode()) &&
                                record.getNicChartOfAccountsName().equalsIgnoreCase(dto.getNicChartOfAccountsName())){
                            value = record.getAccountBalance();
                            break;
                        }
                    }
                }
                List<ReserveCalculationDto> reserveCalculationDtos =
                        this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADMINISTRATION_FEES.getCode(), reportDto.getReportDate(), true);
                if(reserveCalculationDtos != null){
                    for(ReserveCalculationDto record: reserveCalculationDtos){
                        value = MathUtils.subtract(value, record.getAmount());
                    }
                }

                value = MathUtils.subtract(value, (40000.0/12.0));

                dto.setCalculatedAccountBalance(value);
                dto.setCalculatedAccountBalanceFormula(" {previous month value} - 40,000/12 - sum of values from Capital Calls with type 'Комиссия' for current month" );

                /*
                Double remainder = 0.0;
                if(DateUtils.isJanuary(reportDto.getReportDate())) {
                    NICKMFReportingDataHolderDto previousMonthData = getNICKMFReportingDataFromPreviousMonth(reportDto.getId());
                    if(previousMonthData != null && previousMonthData.getRecords() != null){
                        for(NICKMFReportingDataDto record: previousMonthData.getRecords()){
                            if(record.getNbChartOfAccountsCode() != null && record.getNbChartOfAccountsCode().equalsIgnoreCase(dto.getNbChartOfAccountsCode()) &&
                                    record.getNicChartOfAccountsName().equalsIgnoreCase(dto.getNicChartOfAccountsName()) &&
                                    record.getAccountBalance() > 0.04){
                                remainder = record.getAccountBalance();
                                break;
                            }
                        }
                    }
                }
                Double administrationFees = 0.0;
                List<ReserveCalculationDto> administrationFeesList = this.reserveCalculationService.getReserveCalculationsByExpenseTypeAfterDate(ReserveCalculationsExpenseTypeLookup.ADMINISTRATION_FEES.getCode(), DateUtils.getFirstDayOfDateYear(reportDto.getReportDate()));
                if(administrationFeesList != null){
                    for(ReserveCalculationDto administrationFeeDto: administrationFeesList){
                        administrationFees = MathUtils.add(administrationFees, administrationFeeDto.getAmount());
                    }
                }
                Double value = MathUtils.multiply(MathUtils.divide(40000.0, 12.0), new Double(month));
                dto.setCalculatedAccountBalance(MathUtils.add(MathUtils.subtract(remainder, value), administrationFees));
                dto.setCalculatedAccountBalanceFormula(remainder + " - (40,000/12 * " + month + ") + " + administrationFees +
                        ", i.e. {if January, then remainder from December, else 0} - (40,000/12 * current month number ) + sum of values from Capital Calls with type 'Комиссия'" );
                */
            }else if(dto.getNbChartOfAccountsCode().equalsIgnoreCase("7473.080") && dto.getNicChartOfAccountsName().equalsIgnoreCase("Расходы за администрирование NICK MF")){
                dto.setCalculatedAccountBalance(MathUtils.multiply(MathUtils.divide(40000.0, 12.0), (month*1.0)));
                dto.setCalculatedAccountBalanceFormula("40,000 / 12 * " + month + ", where '" + month + "' is current month number");
            }else if(dto.getNbChartOfAccountsCode().equalsIgnoreCase("7473.080") && dto.getNicChartOfAccountsName().equalsIgnoreCase("Амортизация организационных расходов NICK MF")){

                Double value = MathUtils.multiply(MathUtils.divide(14963.0, 60.0), (month*1.0));
                if(value > 14963.0){
                    value = 14963.0;
                }
                dto.setCalculatedAccountBalance(value);
                dto.setCalculatedAccountBalanceFormula("14,963 / 60 * " + month + ", where '" + month + "' is current month number; value no more than 14,963");
            }else if(dto.getNbChartOfAccountsCode().equalsIgnoreCase("1033.010") &&
                    dto.getNicChartOfAccountsName().equalsIgnoreCase("Деньги на текущих счетах")){
                Double value = null;
                NICKMFReportingDataHolderDto previousDataHolder = getNICKMFReportingDataFromPreviousMonth(reportDto.getId());
                if(previousDataHolder != null && previousDataHolder.getRecords() != null){
                    for(NICKMFReportingDataDto record :previousDataHolder.getRecords()){
                        if(record.getNbChartOfAccountsCode().equalsIgnoreCase(dto.getNbChartOfAccountsCode()) &&
                                record.getNicChartOfAccountsName().equalsIgnoreCase(dto.getNicChartOfAccountsName())){
                            value = record.getAccountBalance();
                            break;
                        }
                    }
                }
                List<ReserveCalculationDto> reserveCalculationDtos =
                        this.reserveCalculationService.getReserveCalculationsForMonth(null,
                                reportDto.getReportDate(), false);
                if(reserveCalculationDtos != null){
                    for(ReserveCalculationDto record: reserveCalculationDtos){
                        if(record.getExpenseType() != null && record.getExpenseType().getCode().equalsIgnoreCase(ReserveCalculationsExpenseTypeLookup.ADD.getCode())) {
                            value = MathUtils.add(value, record.getAmount());
                        }else if(record.getExpenseType() != null && record.getExpenseType().getCode().equalsIgnoreCase(ReserveCalculationsExpenseTypeLookup.ADMINISTRATION_FEES.getCode())){
                            value = MathUtils.add(value, record.getAmount());
                        }else if(record.getExpenseType() != null && !record.getExpenseType().getCode().equalsIgnoreCase(ReserveCalculationsExpenseTypeLookup.ADMINISTRATION_FEES.getCode()) &&
                                record.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.NICKMF.getCode())){
                            value = MathUtils.add(value, record.getAmount());
                        }
                    }
                }

                List<ReserveCalculationDto> reserveCalculationDtosByValueDate =
                        this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(),
                                reportDto.getReportDate(), true);
                if(reserveCalculationDtosByValueDate != null){
                    for(ReserveCalculationDto record: reserveCalculationDtosByValueDate){
                        if(record.getValueDate() != null) {
                            value = MathUtils.subtract(value, record.getAmountToSPV() != null ? record.getAmountToSPV() : record.getAmount());
                        }
                    }
                }


                dto.setCalculatedAccountBalance(value);
                dto.setCalculatedAccountBalanceFormula("{previous month value} + sum of values from Capital Calls with type 'Комиссия' for current month + " +
                        "sum of value from Capital Calls with type not 'Комиссия' and recipient being NICK MF for current month");

            }
        }
    }

//    @Override
//    public Double getNICKMFReportingDataCalculatedValue(NICKMFReportingDataCalculatedValueRequestDto requestDto) {
//        if(requestDto != null){
//            if(requestDto.getReportId() != null){
//                PeriodicReportDto reportDto = this.periodicReportService.getPeriodicReport(requestDto.getReportId());
//                return getNICKMFReportingDataCalculatedValue(reportDto, requestDto.getCode(), requestDto.getNameRu());
//            }else{
//                logger.error("NICK MF Calculated value request is invalid, report id is null");
//                return null;
//            }
//        }
//        return null;
//    }
}
