package kz.nicnbk.service.impl.pe;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;
import kz.nicnbk.repo.api.pe.FundRepository;
import kz.nicnbk.repo.api.pe.PeCashflowRepository;
import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.repo.model.pe.PeCashflow;
import kz.nicnbk.service.api.pe.PeCashflowService;
import kz.nicnbk.service.api.pe.PeFundService;
import kz.nicnbk.service.converter.pe.PeCashflowEntityConverter;
import kz.nicnbk.service.converter.pe.PeFundEntityConverter;
import kz.nicnbk.service.dto.pe.PeCashflowDto;
import kz.nicnbk.service.dto.pe.PeFirmDto;
import kz.nicnbk.service.dto.pe.PeFundCompaniesPerformanceDto;
import kz.nicnbk.service.dto.pe.PeFundDto;
import org.hibernate.sql.Template;
import org.hibernate.type.descriptor.java.CalendarTypeDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhambyl on 15-Nov-16.
 */
@Service
public class PeFundServiceImpl implements PeFundService {

    @Autowired
    private FundRepository repository;

    @Autowired
    private PeFundEntityConverter converter;

    @Autowired
    private PeCashflowRepository CFRepository;

    @Autowired
    private PeCashflowService CFService;

    @Autowired
    private PeCashflowEntityConverter CFConverter;

    @Override
    public Long save(PeFundDto fundDto) {
        Fund entity = converter.assemble(fundDto);
        Long id =repository.save(entity).getId();

        boolean deleted = this.CFService.deleteByFundId(id);

        //Save cashflows
        if(fundDto.getCashflow() != null){
            for(PeCashflowDto dto: fundDto.getCashflow()){
                dto.setFund(fundDto);
                this.CFService.save(dto);
            }
        }
        return id;
    }

    @Override
    public PeFundDto get(Long id) {
        Fund entity = this.repository.findOne(id);
        List<PeCashflow> cfEntity = this.CFRepository.getEntitiesByFundId(id);
        PeFundDto dto = this.converter.disassemble(entity);
        List<PeCashflowDto> CFDto = this.CFConverter.disassembleList(cfEntity);
        dto.setCashflow(CFDto);

        // Calculating fund's companies performance
        List<PeFundCompaniesPerformanceDto> performanceDtoList = new ArrayList<>();

        List<String> names = new ArrayList<>();


        int j = 0;
        double cashInvested = CFDto.get(0).getInvested();
        double realized = CFDto.get(0).getRealized();
        double unrealized = CFDto.get(0).getUnrealized();
        double totalValue;
        double multiple;
        double grossIrr;



        for(int i = 1; i < CFDto.size(); i++){
            if(!Objects.equals(CFDto.get(j).getCompanyName(), CFDto.get(i).getCompanyName())){
                totalValue = realized + unrealized;
                if(cashInvested != 0) {
                    multiple = totalValue / cashInvested;
                } else {
                    multiple = 0;
                }
                performanceDtoList.add(new PeFundCompaniesPerformanceDto(CFDto.get(i-1).getCompanyName(), cashInvested, realized, unrealized, totalValue, multiple, 123, 123));
                cashInvested = CFDto.get(i).getInvested();
                realized = CFDto.get(i).getRealized();
                unrealized = CFDto.get(i).getUnrealized();

                dto.setFundCompanyPerformance(performanceDtoList);

                j=i;

            } else {
                cashInvested = cashInvested + CFDto.get(i).getInvested();
                realized = realized + CFDto.get(i).getRealized();
                unrealized = unrealized + CFDto.get(i).getUnrealized();

            }
        }
        totalValue = realized + unrealized;
        if(cashInvested != 0) {
            multiple = totalValue / cashInvested;
        } else {
            multiple = 0;
        }
        performanceDtoList.add(new PeFundCompaniesPerformanceDto(CFDto.get(CFDto.size() - 1).getCompanyName(), cashInvested, realized, unrealized, totalValue, multiple, 123, 123));

        return dto;
    }

    @Override
    public Set<PeFundDto> loadFirmFunds(Long firmId) {
        Page<Fund> page = this.repository.findByFirmId(firmId, new PageRequest(0,10, new Sort(Sort.Direction.ASC, "vintage")));
        return this.converter.disassembleSet(page.getContent());
    }
}

