package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.PEInvestmentTypeRepository;
import kz.nicnbk.repo.api.lookup.PETrancheTypeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
import kz.nicnbk.service.impl.reporting.privateequity.PEScheduleInvestmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingPEScheduleInvestmentConverter extends BaseDozerEntityConverter<ReportingPEScheduleInvestment, ScheduleInvestmentsDto> {

    private static final Logger logger = LoggerFactory.getLogger(ReportingPEScheduleInvestmentConverter.class);

    @Autowired
    private PETrancheTypeRepository trancheTypeRepository;

    @Autowired
    private PEInvestmentTypeRepository peInvestmentTypeRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public ScheduleInvestmentsDto disassemble(ReportingPEScheduleInvestment entity){
        ScheduleInvestmentsDto dto = super.disassemble(entity);
        return dto;
    }

    public ReportingPEScheduleInvestment assemble(ScheduleInvestmentsDto dto, Long reportId) {

        ReportingPEScheduleInvestment entity = super.assemble(dto);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        if(dto.getTrancheType() != null){
            PETrancheType trancheType = this.trancheTypeRepository.findByNameEnIgnoreCase(dto.getTrancheType().getNameEn().trim());
            entity.setTrancheType(trancheType);

        }
        if(entity.getTrancheType() == null){
            String errorMessage = " SOI Report record tranche type could not be determined for '" + entity.getInvestment() +
                    "'. Expected values are 'Tranche A', 'Tranche B', etc.  Check for possible spaces in names.";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }

        // investment type
        if(dto.getType() != null) {
            PEInvestmentType investmentType = this.peInvestmentTypeRepository.findByNameEnIgnoreCase(dto.getType().getNameEn().trim());
            entity.setType(investmentType);

        }

        if(entity.getType() == null){
            String errorMessage = " SOI Report record type could not be determined for '" + entity.getInvestment() +
                    "'. Expected values are 'Fund Investments', 'Co-Investments', etc.  Check for possible spaces in names.";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }

        // strategy
        if(dto.getStrategy() != null /*&& entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS)*/) {
            Strategy strategy = this.strategyRepository.findByNameEnAndGroupType(dto.getStrategy().getNameEn().trim(), Strategy.TYPE_PRIVATE_EQUITY);
            entity.setStrategy(strategy);
        }else{
            entity.setStrategy(null);
        }
        if(/*entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS) &&*/
                entity.getStrategy() == null ){
            String errorMessage = "Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'" +
                    ". Please check strategy exists.";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }

        // currency
        if(dto.getCurrency() != null && dto.getCurrency().getCode() != null) {
            Currency currency = this.currencyRepository.findByCode(dto.getCurrency().getCode());
            entity.setCurrency(currency);
        }

        return entity;
    }
}
