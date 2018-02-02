package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class PEGeneralLedgerFormDataConverter extends BaseDozerEntityConverter<PEGeneralLedgerFormData, PEGeneralLedgerFormDataDto> {

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
        entity.setId(null); // TODO: ?
        if(dto.getFinancialStatementCategory() != null){
            FinancialStatementCategory financialStatementCategory =
                    this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory());
            entity.setFinancialStatementCategory(financialStatementCategory);
        }
        if(dto.getTarragonNICChartOfAccountsName() != null){
            TarragonNICChartOfAccounts tarragonNICChartOfAccounts =
                    this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsNameAndAddable(dto.getTarragonNICChartOfAccountsName(), true);
            entity.setTarragonNICChartOfAccounts(tarragonNICChartOfAccounts);
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
