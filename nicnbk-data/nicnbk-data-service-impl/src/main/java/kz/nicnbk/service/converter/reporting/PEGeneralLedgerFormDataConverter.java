package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.lookup.reporting.ChartAccountsTypeLookup;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class PEGeneralLedgerFormDataConverter extends BaseDozerEntityConverter<PEGeneralLedgerFormData, PEGeneralLedgerFormDataDto> {

    private static final Logger logger = LoggerFactory.getLogger(PEGeneralLedgerFormDataConverter.class);

    @Autowired
    private LookupService lookupService;



    @Autowired
    private TarragonNICChartOfAccountsRepository tarragonNICChartOfAccountsRepository;

    @Override
    public PEGeneralLedgerFormData assemble(PEGeneralLedgerFormDataDto dto) {
        if (dto == null) {
            return null;
        }
        PEGeneralLedgerFormData entity = super.assemble(dto);

        if(dto.getTrancheType() != null){
            PETrancheType peTrancheType = this.lookupService.findByTypeAndCode(PETrancheType.class, dto.getTrancheType());
            entity.setTrancheType(peTrancheType);
        }
        if(entity.getTrancheType() == null){
            logger.error("Error assembling PEGeneralLedgerFormData: tranche type is null: searching code=" + dto.getTrancheType());
            throw new IllegalArgumentException("Tranche type could not be determined: code=" + dto.getTrancheType());
        }
        //entity.setId(null);
        if(dto.getFinancialStatementCategory() != null){
            FinancialStatementCategory financialStatementCategory =
                    this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory());
            entity.setFinancialStatementCategory(financialStatementCategory);
        }
        if(entity.getFinancialStatementCategory() == null){
            logger.error("Error assembling PEGeneralLedgerFormData: financial statement category is null: searching code=" + dto.getFinancialStatementCategory());
            throw new IllegalArgumentException("Financial statement category could not be determined: code=" + dto.getFinancialStatementCategory());
        }
        if(dto.getTarragonNICChartOfAccountsName() != null){
            List<TarragonNICChartOfAccounts> tarragonNICChartOfAccounts =
                    this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsNameAndAddable(dto.getTarragonNICChartOfAccountsName(), true);

            if (tarragonNICChartOfAccounts != null && !tarragonNICChartOfAccounts.isEmpty()) {
                for(TarragonNICChartOfAccounts anEntity: tarragonNICChartOfAccounts){
                    if(anEntity.getChartAccountsType() != null && anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) &&
                            entity.getGLAccountBalance() != null && entity.getGLAccountBalance() >= 0){
                        entity.setTarragonNICChartOfAccounts(anEntity);
                        break;
                    }else if(anEntity.getChartAccountsType() != null && anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) &&
                            entity.getGLAccountBalance() != null && entity.getGLAccountBalance() < 0){
                        entity.setTarragonNICChartOfAccounts(anEntity);
                        break;
                    }else if(tarragonNICChartOfAccounts.size() == 1){
                        entity.setTarragonNICChartOfAccounts(anEntity);
                    }
                }

                //entity.setTarragonNICChartOfAccounts(tarragonNICChartOfAccounts.get(0));

//                for(TarragonNICChartOfAccounts chartOfAccounts: tarragonNICChartOfAccounts){
//                    if(dto.getTarragonNICChartOfAccountsName().equalsIgnoreCase("Current tax (expense) benefit")){
//                        if(dto.getGLAccountBalance() != null && dto.getGLAccountBalance() < 0){
//                            if(chartOfAccounts.getNegativeOnly() != null && chartOfAccounts.getNegativeOnly().booleanValue()){
//                                entity.setTarragonNICChartOfAccounts(chartOfAccounts);
//                                break;
//                            }
//                        }else if(dto.getGLAccountBalance() != null && dto.getGLAccountBalance() >= 0){
//                            if(chartOfAccounts.getPositiveOnly() != null && chartOfAccounts.getPositiveOnly().booleanValue()){
//                                entity.setTarragonNICChartOfAccounts(chartOfAccounts);
//                                break;
//                            }
//                        }
//
//                    }else {
//                        entity.setTarragonNICChartOfAccounts(chartOfAccounts);
//                        break;
//                    }
//                }
            }

            //entity.setTarragonNICChartOfAccounts(tarragonNICChartOfAccounts);
        }

        return entity;
    }

    @Override
    public PEGeneralLedgerFormDataDto disassemble(PEGeneralLedgerFormData entity) {
        if (entity == null) {
            return null;
        }
        PEGeneralLedgerFormDataDto dto = super.disassemble(entity);
        dto.setId(null); // TODO: ?

        if(entity.getTrancheType() != null){
            dto.setTrancheType(entity.getTrancheType().getCode());
        }

        if(entity.getFinancialStatementCategory() != null) {
            dto.setFinancialStatementCategory(entity.getFinancialStatementCategory().getCode());
        }
        if(entity.getTarragonNICChartOfAccounts() != null) {
            dto.setTarragonNICChartOfAccountsName(entity.getTarragonNICChartOfAccounts().getTarragonChartOfAccountsName());
            if(entity.getTarragonNICChartOfAccounts().getNicReportingChartOfAccounts() != null){
                dto.setNicAccountName(entity.getTarragonNICChartOfAccounts().getNicReportingChartOfAccounts().getNameRu());
                if(entity.getTarragonNICChartOfAccounts().getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                    dto.setNbAccountNumber(entity.getTarragonNICChartOfAccounts().getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                }
            }
        }

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        return dto;
    }
}
