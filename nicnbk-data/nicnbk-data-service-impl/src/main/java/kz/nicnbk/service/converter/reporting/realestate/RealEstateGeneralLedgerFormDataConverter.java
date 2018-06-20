package kz.nicnbk.service.converter.reporting.realestate;

import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.realestate.TerraNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import kz.nicnbk.repo.model.reporting.realestate.RealEstateGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.realestate.TerraNICChartOfAccounts;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import kz.nicnbk.service.dto.reporting.realestate.RealEstateGeneralLedgerFormDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class RealEstateGeneralLedgerFormDataConverter extends BaseDozerEntityConverter<RealEstateGeneralLedgerFormData, RealEstateGeneralLedgerFormDataDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private TerraNICChartOfAccountsRepository terraNICChartOfAccountsRepository;

    @Override
    public RealEstateGeneralLedgerFormData assemble(RealEstateGeneralLedgerFormDataDto dto) {
        if (dto == null) {
            return null;
        }
        RealEstateGeneralLedgerFormData entity = super.assemble(dto);
        //entity.setId(null);
        if(dto.getFinancialStatementCategory() != null){
            FinancialStatementCategory financialStatementCategory =
                    this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory());
            entity.setFinancialStatementCategory(financialStatementCategory);
        }
        if(dto.getTerraNICChartOfAccountsName() != null){
            TerraNICChartOfAccounts terraNICChartOfAccounts =
                    this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsNameAndAddable(dto.getTerraNICChartOfAccountsName(), true);
            entity.setTerraNICChartOfAccounts(terraNICChartOfAccounts);
        }

        return entity;
    }

    @Override
    public RealEstateGeneralLedgerFormDataDto disassemble(RealEstateGeneralLedgerFormData entity) {
        if (entity == null) {
            return null;
        }
        RealEstateGeneralLedgerFormDataDto dto = super.disassemble(entity);
        dto.setId(null); // TODO: ?

        if(entity.getFinancialStatementCategory() != null) {
            dto.setFinancialStatementCategory(entity.getFinancialStatementCategory().getCode());
        }
        if(entity.getTerraNICChartOfAccounts() != null) {
            dto.setTerraNICChartOfAccountsName(entity.getTerraNICChartOfAccounts().getTerraChartOfAccountsName());
            if(entity.getTerraNICChartOfAccounts().getNicReportingChartOfAccounts() != null){
                dto.setNicAccountName(entity.getTerraNICChartOfAccounts().getNicReportingChartOfAccounts().getNameRu());
                if(entity.getTerraNICChartOfAccounts().getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                    dto.setNbAccountNumber(entity.getTerraNICChartOfAccounts().getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
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
