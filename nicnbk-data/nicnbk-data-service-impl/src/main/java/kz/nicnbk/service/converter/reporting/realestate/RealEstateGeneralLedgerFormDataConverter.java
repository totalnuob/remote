package kz.nicnbk.service.converter.reporting.realestate;

import kz.nicnbk.repo.api.reporting.realestate.TerraNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;

import kz.nicnbk.repo.model.reporting.realestate.RealEstateGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.realestate.TerraNICChartOfAccounts;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.realestate.RealEstateGeneralLedgerFormDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class RealEstateGeneralLedgerFormDataConverter extends BaseDozerEntityConverter<RealEstateGeneralLedgerFormData, RealEstateGeneralLedgerFormDataDto> {

    private static final Logger logger = LoggerFactory.getLogger(RealEstateGeneralLedgerFormDataConverter.class);

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
            List<TerraNICChartOfAccounts> terraNICChartOfAccounts =
                    this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsNameAndAddable(dto.getTerraNICChartOfAccountsName(), true);

            if (terraNICChartOfAccounts != null && !terraNICChartOfAccounts.isEmpty()) {
                if (terraNICChartOfAccounts.size() == 1) {
                    boolean match = (terraNICChartOfAccounts.get(0).getPositiveOnly() != null &&
                            terraNICChartOfAccounts.get(0).getPositiveOnly().booleanValue() &&
                            dto.getGLAccountBalance() != null && dto.getGLAccountBalance() >= 0) ||
                            (terraNICChartOfAccounts.get(0).getNegativeOnly() != null &&
                            terraNICChartOfAccounts.get(0).getNegativeOnly().booleanValue() &&
                            dto.getGLAccountBalance() != null && dto.getGLAccountBalance() < 0) ||
                            (terraNICChartOfAccounts.get(0).getPositiveOnly() == null && terraNICChartOfAccounts.get(0).getNegativeOnly() == null);
                    if(match) {
                        entity.setTerraNICChartOfAccounts(terraNICChartOfAccounts.get(0));
                    }
                } else if (terraNICChartOfAccounts.size() == 2) {
                    boolean found = false;
                    for (TerraNICChartOfAccounts anEntity : terraNICChartOfAccounts) {
                        if (dto.getGLAccountBalance() != null && dto.getGLAccountBalance() < 0) {
                            if (anEntity.getNegativeOnly() != null && anEntity.getNegativeOnly().booleanValue()) {
                                entity.setTerraNICChartOfAccounts(anEntity);
                                found = true;
                                break;
                            }
                        } else if (dto.getGLAccountBalance() != null && dto.getGLAccountBalance() >= 0) {
                            if (anEntity.getPositiveOnly() != null && anEntity.getPositiveOnly().booleanValue()) {
                                entity.setTerraNICChartOfAccounts(anEntity);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        logger.error("Error setting Terra NIC Chart of accounts for '" + dto.getTerraNICChartOfAccountsName() + "' " +
                                " : positiveOnly/negativeOnly flags not set properly");
                    }
                } else {
                    logger.error("Error setting Terra NIC Chart of accounts for '" + dto.getTerraNICChartOfAccountsName() + "' " +
                            " : more than 2 mappings found.");
                }
            }


            //entity.setTerraNICChartOfAccounts(terraNICChartOfAccounts);
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
