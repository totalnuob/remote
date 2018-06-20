package kz.nicnbk.service.converter.reporting.realestate;

import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.ReportingREBalanceSheet;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.StatementBalanceOperationsDto;
import kz.nicnbk.service.dto.reporting.realestate.TerraBalanceSheetRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingREBalanceSheetConverter extends BaseDozerEntityConverter<ReportingREBalanceSheet, TerraBalanceSheetRecordDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingREBalanceSheet assemble(TerraBalanceSheetRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ReportingREBalanceSheet entity = super.assemble(dto);
        if(dto.getReport() != null && dto.getReport().getId() != null){
            entity.setReport(new PeriodicReport(dto.getReport().getId()));
        }
        // type
        if(dto.getType() != null && StringUtils.isNotEmpty(dto.getType().getCode())){
            REBalanceType type = lookupService.findByTypeAndCode(REBalanceType.class, dto.getType().getCode());
            entity.setType(type);
        }

        return entity;
    }

    @Override
    public TerraBalanceSheetRecordDto disassemble(ReportingREBalanceSheet entity) {
        if (entity == null) {
            return null;
        }
        TerraBalanceSheetRecordDto dto = super.disassemble(entity);
        // type
        if(entity.getType() != null) {
            HierarchicalBaseDictionaryDto typeDto = new HierarchicalBaseDictionaryDto();
            typeDto.setCode(entity.getType().getCode());
            typeDto.setNameEn(entity.getType().getNameEn());
            typeDto.setNameRu(entity.getType().getNameRu());
            typeDto.setNameKz(entity.getType().getNameKz());

            REBalanceType parentType = entity.getType().getParent();
            HierarchicalBaseDictionaryDto childDto = typeDto;
            HierarchicalBaseDictionaryDto parentDto = null;
            while(parentType != null){
                parentDto = new HierarchicalBaseDictionaryDto();
                parentDto.setCode(parentType.getCode());
                parentDto.setNameEn(parentType.getNameEn());
                parentDto.setNameRu(parentType.getNameRu());
                parentDto.setNameKz(parentType.getNameKz());
                childDto.setParent(parentDto);

                parentType = parentType.getParent();
                childDto = parentDto;
            }
            dto.setType(typeDto);
        }
        //report
        if(entity.getReport() != null) {
            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
        }

        return dto;
    }
}
