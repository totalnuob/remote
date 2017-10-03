package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.NICReportingChartOfAccountsRepository;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataDto;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class NICKMFReportingDataConverter extends BaseDozerEntityConverter<NICKMFReportingData, NICKMFReportingDataDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private PeriodicReportConverter reportConverter;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private NICReportingChartOfAccountsRepository nicReportingChartOfAccountsRepository;

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
            if(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null){
                dto.setNbChartOfAccountsCode(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
            }
        }

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        return dto;
    }
}
