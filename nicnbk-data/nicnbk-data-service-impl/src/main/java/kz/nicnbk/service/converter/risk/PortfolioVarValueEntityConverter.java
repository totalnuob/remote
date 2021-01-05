package kz.nicnbk.service.converter.risk;

import kz.nicnbk.repo.model.risk.PortfolioVar;
import kz.nicnbk.repo.model.risk.PortfolioVarValue;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortfolioVarValueEntityConverter extends BaseDozerEntityConverter<PortfolioVarValue, PortfolioVarValueDto> {

    @Autowired
    private LookupService lookupService;

    public PortfolioVarValue assemble(PortfolioVarValueDto dto) {
        PortfolioVarValue entity = super.assemble(dto);
        if (dto.getPortfolioVar() != null && dto.getPortfolioVar().getCode() != null) {
            PortfolioVar var = this.lookupService.findByTypeAndCode(PortfolioVar.class, dto.getPortfolioVar().getCode());
            entity.setPortfolioVar(var);
        }
        return entity;
    }
}
