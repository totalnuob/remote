package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.hf.HedgeFundRepository;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.service.api.hf.HedgeFundReturnService;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.api.hf.HedgeFundSubstrategyService;
import kz.nicnbk.service.api.hf.InvestorBaseService;
import kz.nicnbk.service.converter.hf.HedgeFundEntityConverter;
import kz.nicnbk.service.dto.hf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
@Service
public class HedgeFundServiceImpl implements HedgeFundService {

    @Autowired
    private HedgeFundRepository repository;

    @Autowired
    private HedgeFundEntityConverter converter;

    @Autowired
    private HedgeFundSubstrategyService hedgeFundSubstrategyService;

    @Autowired
    private InvestorBaseService investorBaseService;

    @Autowired
    private HedgeFundReturnService hedgeFundReturnService;

    @Override
    public Long save(HedgeFundDto2 hedgeFundDto) {
        HedgeFund entity = converter.assemble(hedgeFundDto);
        if(hedgeFundDto.getId() != null){
            entity.setUpdateDate(new Date());
        }
        Long id = repository.save(entity).getId();

        // dekete substrategies
        boolean deleted = this.hedgeFundSubstrategyService.deleteByFundId(id);
        // save substrategy
        if(hedgeFundDto.getStrategyBreakdownList() != null) {
            for (SubstrategyBreakdownDto substrategyBreakdownDto : hedgeFundDto.getStrategyBreakdownList()) {
                HedgeFundSubstrategyDto dto = new HedgeFundSubstrategyDto();
                HedgeFundDto2 fundDto = new HedgeFundDto2();
                fundDto.setId(id);
                dto.setFund(fundDto);

                BaseDictionaryDto substrategyDto = new BaseDictionaryDto();
                substrategyDto.setCode(substrategyBreakdownDto.getCode());
                dto.setSubstrategy(substrategyDto);
                dto.setValue(substrategyBreakdownDto.getValue());
                this.hedgeFundSubstrategyService.save(dto);
            }
        }

        // delete investor base
        deleted = this.investorBaseService.deleteByFundId(id);

        // save investor base
        if(hedgeFundDto.getInvestorBaseList() != null){
            for(InvestorBaseDto dto: hedgeFundDto.getInvestorBaseList()){
                dto.setHedgeFund(hedgeFundDto);
                this.investorBaseService.save(dto);
            }
        }

        // delete returns
        deleted = this.hedgeFundReturnService.deleteByFundId(id);

        // save investor base
        if(hedgeFundDto.getReturns() != null){
            for(ReturnDto dto: hedgeFundDto.getReturns()){
                dto.setFund(hedgeFundDto);
                this.hedgeFundReturnService.save(dto);
            }
        }

        // TODO: handle errors

        return id;
    }

    @Override
    public HedgeFundDto2 get(Long fundId) {
        HedgeFund entity = this.repository.findOne(fundId);
        HedgeFundDto2 fundDto = this.converter.disassemble(entity);

        // substrategy breakdown
        List<HedgeFundSubstrategyDto> substrategyDtoList = this.hedgeFundSubstrategyService.findByFundId(fundId);
        if(substrategyDtoList != null) {
            List<SubstrategyBreakdownDto> substrategyBreakdownDtoList = new ArrayList<>();
            for (HedgeFundSubstrategyDto entityDto : substrategyDtoList) {
                SubstrategyBreakdownDto dto = new SubstrategyBreakdownDto();
                dto.setCode(entityDto.getSubstrategy().getCode());
                dto.setValue(entityDto.getValue());
                substrategyBreakdownDtoList.add(dto);
            }
            fundDto.setStrategyBreakdownList(substrategyBreakdownDtoList);
        }

        // investor base
        List<InvestorBaseDto> investorBaseList = this.investorBaseService.findByFundId(fundId);
        fundDto.setInvestorBaseList(investorBaseList);

        // returns
        List<ReturnDto> returnsList = this.hedgeFundReturnService.findByFundId(fundId);
        fundDto.setReturns(returnsList);

        return fundDto;
    }

    @Override
    public List<HedgeFundDto2> loadManagerFunds(Long managerId) {
        Page<HedgeFund> page = repository.findByManager(managerId,
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
        return this.converter.disassembleList(page.getContent());
    }

    @Override
    public Set<HedgeFundDto2> findByName(HedgeFundSearchParams searchParams) {
//        if(StringUtils.isEmpty(name)){
//            Page<HedgeFund> page = this.repository.findAll( new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
//            return this.converter.disassembleSet(page.getContent());
//        }
        Page<HedgeFund> page = this.repository.findByName(searchParams.getName(),
                new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));

        return this.converter.disassembleSet(page.getContent());
    }
}
