package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class NICKMFReportingDataConverter extends BaseDozerEntityConverter<NICKMFReportingData, NICKMFReportingDataDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public NICKMFReportingData assemble(NICKMFReportingDataDto dto) {
        if (dto == null) {
            return null;
        }
        NICKMFReportingData entity = super.assemble(dto);
        entity.setId(null); // TODO: ?
        if(dto.getNicChartOfAccountsCode() != null){
            NICReportingChartOfAccounts chartOfAccounts = lookupService.findByTypeAndCode(NICReportingChartOfAccounts.class,
                    dto.getNicChartOfAccountsCode());
            entity.setNicReportingChartOfAccounts(chartOfAccounts);
        }

        return entity;
    }

    @Override
    public NICKMFReportingDataDto disassemble(NICKMFReportingData entity) {
        if (entity == null) {
            return null;
        }
        NICKMFReportingDataDto dto = super.disassemble(entity);
        dto.setId(null);

        if(entity.getNicReportingChartOfAccounts() != null){
            dto.setNicChartOfAccountsCode(entity.getNicReportingChartOfAccounts().getCode());
            dto.setNicChartOfAccountsName(entity.getNicReportingChartOfAccounts().getNameRu());
            if(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null){
                dto.setNbChartOfAccountsCode(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                dto.setNbChartOfAccountsName(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getNameRu());
            }
        }

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        return dto;
    }
}
