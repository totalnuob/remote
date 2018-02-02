package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFNOALRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityNOALRecordDto;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 29.08.2017.
 */

@Service
public class HFNOALServiceImpl implements HFNOALService {

    private static final Logger logger = LoggerFactory.getLogger(HFNOALServiceImpl.class);

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ReportingHFNOALRepository hfNOALRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingHFNOAL assemble(SingularityNOALRecordDto dto, Long reportId) {
        ReportingHFNOAL entity = new ReportingHFNOAL();
        entity.setDate(dto.getDate());
        entity.setTransaction(dto.getTransaction());
        entity.setName(dto.getName());
        entity.setEffectiveDate(dto.getEffectiveDate());
        entity.setTransactionAmount(dto.getTransactionAmount());
        if(dto.getTransactionAmountCCY() != null) {
            entity.setTransactionAmountCCY(this.lookupService.findByTypeAndCode(Currency.class, dto.getTransactionAmountCCY()));
            if(entity.getTransactionAmountCCY() == null && entity.getTransactionAmount() != null){
                logger.error("Error parsing 'Singularity NOAL' file: transaction amount ccy could not be determined - " + dto.getTransactionAmountCCY());
                throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: transaction amount ccy could not be determined - " + dto.getTransactionAmountCCY());
            }
        }
        entity.setFunctionalAmount(dto.getFunctionalAmount());
        if(dto.getFunctionalAmountCCY() != null) {
            entity.setFunctionalAmountCCY(this.lookupService.findByTypeAndCode(Currency.class, dto.getFunctionalAmountCCY()));
            if(entity.getFunctionalAmountCCY() == null && entity.getFunctionalAmount() != null){
                logger.error("Error parsing 'Singularity NOAL' file: functional amount ccy could not be determined - " + dto.getFunctionalAmountCCY());
                throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: functional amount ccy could not be determined - " + dto.getFunctionalAmountCCY());
            }
        }
        entity.setTranche(dto.getTranche());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingHFNOAL> assembleList(List<SingularityNOALRecordDto> dtoList, Long reportId, int tranche) {
        List<ReportingHFNOAL> entities = new ArrayList<>();
        if(dtoList != null){
            SingularityNOALRecordDto previous = null;
            for(SingularityNOALRecordDto dto: dtoList){
                ReportingHFNOAL entity = assemble(dto, reportId);
                if(entity.getTransaction() != null && (entity.getTransaction().equalsIgnoreCase("Ending Balance") ||
                        entity.getTransaction().equalsIgnoreCase("Ending"))){
                    if(StringUtils.isEmpty(entity.getName())){
                        entity.setName(previous != null ? previous.getName() : null);
                    }
                    if(entity.getEffectiveDate() == null){
                        entity.setEffectiveDate(previous != null ? previous.getEffectiveDate() : null);
                    }
                }
                entity.setTranche(tranche);
                entities.add(entity);
                previous = dto;
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingHFNOAL> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            this.hfNOALRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId, int tranche) {
        List<ReportingHFNOAL> entities = this.hfNOALRepository.getEntitiesByReportIdAndTranche(reportId, tranche,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<SingularityNOALRecordDto> records = disassembleList(entities);

        if(tranche == 1) {
            result.setNoalTrancheAList(records);
        }else if(tranche ==2){
            result.setNoalTrancheBList(records);
        }

        if(entities != null && !entities.isEmpty()) {
            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }

    @Override
    public boolean deleteByReportId(Long reportId, int tranche) {
        try {
            this.hfNOALRepository.deleteByReportIdAndTranche(reportId, tranche);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId);
            return false;
        }
    }

    public List<SingularityNOALRecordDto> disassembleList(List<ReportingHFNOAL> entities){
        List<SingularityNOALRecordDto> records = new ArrayList<>();
        if(entities != null){
            for(ReportingHFNOAL entity: entities){
                SingularityNOALRecordDto dto = disassemble(entity);
                records.add(dto);
//                if(dto.getTransaction().equalsIgnoreCase("Ending Balance")){
//                    records.add(new SingularityNOALRecordDto());
//                }
            }
        }
        return records;
    }

    private SingularityNOALRecordDto disassemble(ReportingHFNOAL entity){
        if(entity != null){
            SingularityNOALRecordDto dto = new SingularityNOALRecordDto();
            dto.setDate(entity.getDate());
            dto.setTransaction(entity.getTransaction());
            if(dto.getTransaction() == null ||
                    !(dto.getTransaction().equalsIgnoreCase("Ending Balance") && dto.getTransaction().equalsIgnoreCase("Ending"))){
                dto.setName(entity.getName());
                dto.setEffectiveDate(entity.getEffectiveDate());
            }
            dto.setTransactionAmount(entity.getTransactionAmount());
            if(entity.getTransactionAmountCCY() != null) {
                dto.setTransactionAmountCCY(entity.getTransactionAmountCCY().getCode());
            }
            dto.setFunctionalAmount(entity.getFunctionalAmount());
            if(entity.getFunctionalAmountCCY() != null) {
                dto.setFunctionalAmountCCY(entity.getFunctionalAmountCCY().getCode());
            }
            dto.setTranche(entity.getTranche());
            dto.setAccountNumber(entity.getAccountNumber());
            return dto;
        }
        return null;
    }
}
