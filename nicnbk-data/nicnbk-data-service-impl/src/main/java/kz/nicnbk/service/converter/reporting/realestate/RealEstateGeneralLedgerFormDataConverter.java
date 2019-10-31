package kz.nicnbk.service.converter.reporting.realestate;

import kz.nicnbk.repo.api.reporting.realestate.TerraNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.lookup.reporting.ChartAccountsTypeLookup;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;

import kz.nicnbk.repo.model.reporting.realestate.RETrancheType;
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
        if(dto.getTrancheType() != null){
            RETrancheType reTrancheType = this.lookupService.findByTypeAndCode(RETrancheType.class, dto.getTrancheType());
            entity.setTrancheType(reTrancheType);
        }
        if(entity.getTrancheType() == null){
            logger.error("Error assembling RealEstateGeneralLedgerFormData: tranche type is null: searching code=" + dto.getTrancheType());
            throw new IllegalArgumentException("Tranche type could not be determined: code=" + dto.getTrancheType());
        }

        if(dto.getFinancialStatementCategory() != null){
            FinancialStatementCategory financialStatementCategory =
                    this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory());
            entity.setFinancialStatementCategory(financialStatementCategory);
        }
        if(entity.getFinancialStatementCategory() == null){
            logger.error("Error assembling RealEstateGeneralLedgerFormData: financial statement category is null: searching code=" + dto.getFinancialStatementCategory());
            throw new IllegalArgumentException("Financial statement category could not be determined: code=" + dto.getFinancialStatementCategory());
        }

        if(dto.getTerraNICChartOfAccountsName() != null){
            List<TerraNICChartOfAccounts> terraNICChartOfAccounts =
                    this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsNameAndAddable(dto.getTerraNICChartOfAccountsName(), true);

            if (terraNICChartOfAccounts != null && !terraNICChartOfAccounts.isEmpty()) {
                if (terraNICChartOfAccounts.size() == 1) {
                    boolean isPositiveOnly = terraNICChartOfAccounts.get(0).getChartAccountsType() != null &&
                            terraNICChartOfAccounts.get(0).getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) ? true : false;
                    boolean isNegativeOnly = terraNICChartOfAccounts.get(0).getChartAccountsType() != null &&
                            terraNICChartOfAccounts.get(0).getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) ? true : false;

                    boolean match = (isPositiveOnly && dto.getGLAccountBalance() != null && dto.getGLAccountBalance() >= 0) ||
                            (isNegativeOnly && dto.getGLAccountBalance() != null && dto.getGLAccountBalance() < 0) ||
                            (!isPositiveOnly && !isNegativeOnly);
                    if(match) {
                        entity.setTerraNICChartOfAccounts(terraNICChartOfAccounts.get(0));
                    }
                } else if (terraNICChartOfAccounts.size() == 2) {
                    boolean found = false;
                    for (TerraNICChartOfAccounts anEntity : terraNICChartOfAccounts) {
                        if (dto.getGLAccountBalance() != null && dto.getGLAccountBalance() < 0) {
                            if (anEntity.getChartAccountsType() != null && anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode())) {
                                entity.setTerraNICChartOfAccounts(anEntity);
                                found = true;
                                break;
                            }
                        } else if (dto.getGLAccountBalance() != null && dto.getGLAccountBalance() >= 0) {
                            if (anEntity.getChartAccountsType() != null && anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode())) {
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
