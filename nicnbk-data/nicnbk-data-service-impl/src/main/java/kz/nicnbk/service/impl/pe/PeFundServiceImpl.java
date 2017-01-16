package kz.nicnbk.service.impl.pe;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;
import kz.nicnbk.repo.api.pe.FundRepository;
import kz.nicnbk.repo.api.pe.PeGrossCashflowRepository;
import kz.nicnbk.repo.api.pe.PeNetCashflowRepository;
import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.repo.model.pe.PeGrossCashflow;
import kz.nicnbk.repo.model.pe.PeNetCashflow;
import kz.nicnbk.service.api.pe.PeGrossCashflowService;
import kz.nicnbk.service.api.pe.PeFundService;
import kz.nicnbk.service.api.pe.PeNetCashflowService;
import kz.nicnbk.service.converter.pe.PeGrossCashflowEntityConverter;
import kz.nicnbk.service.converter.pe.PeFundEntityConverter;
import kz.nicnbk.service.converter.pe.PeNetCashflowEntityConverter;
import kz.nicnbk.service.dto.pe.PeGrossCashflowDto;
import kz.nicnbk.service.dto.pe.PeFundCompaniesPerformanceDto;
import kz.nicnbk.service.dto.pe.PeFundDto;
import kz.nicnbk.service.dto.pe.PeNetCashflowDto;
import org.apache.poi.ss.usermodel.DateUtil;
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

    private static final double[] EMPTY_DOUBLE_ARRAY = null;

    @Autowired
    private FundRepository repository;

    @Autowired
    private PeFundEntityConverter converter;

    @Autowired
    private PeGrossCashflowRepository grossCFRepository;

    @Autowired
    private PeGrossCashflowService grossCFService;

    @Autowired
    private PeGrossCashflowEntityConverter grossCFConverter;

    @Autowired
    private PeNetCashflowRepository netCFRepository;

    @Autowired
    private PeNetCashflowService netCFService;

    @Autowired
    private PeNetCashflowEntityConverter netCFConverter;



    @Override
    public Long save(PeFundDto fundDto) {
        Fund entity = converter.assemble(fundDto);
        Long id =repository.save(entity).getId();
        fundDto.setId(id);

        boolean deleted = this.grossCFService.deleteByFundId(id);

        //Save gross cashflows
        if(fundDto.getGrossCashflow() != null){
            for(PeGrossCashflowDto dto: fundDto.getGrossCashflow()){
                dto.setFund(fundDto);
                this.grossCFService.save(dto);
            }
        }

        boolean deleted2 = this.netCFService.deleteByFundId(id);

        //Save net cashflows
        if(fundDto.getNetCashflow() != null){
            for(PeNetCashflowDto dto: fundDto.getNetCashflow()){
                dto.setFund(fundDto);
                this.netCFService.save(dto);
            }
        }
        return id;
    }

    @Override
    public PeFundDto get(Long id) {
        Fund entity = this.repository.findOne(id);

        List<PeGrossCashflow> grossCfEntity = this.grossCFRepository.getEntitiesByFundId(id, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")));
        List<PeNetCashflow> netCfEntity = this.netCFRepository.getEntitiesByFundId(id);

        PeFundDto dto = this.converter.disassemble(entity);

        List<PeGrossCashflowDto> grossCFDto = this.grossCFConverter.disassembleList(grossCfEntity);
        List<PeNetCashflowDto> netCFDto = this.netCFConverter.disassembleList(netCfEntity);

        dto.setGrossCashflow(grossCFDto);
        dto.setNetCashflow(netCFDto);




        double totalInvestedAmount = 0;
        double totalRealized = 0;
        double totalUnrealized = 0;
        double totalTotalValue = 0;
        double totalGrossTvpi = 0;

        if(!grossCFDto.isEmpty()) {

            // Calculating fund's companies performance
            List<PeFundCompaniesPerformanceDto> performanceDtoList = new ArrayList<>();

            int j = 0;
            double cashInvested = grossCFDto.get(0).getInvested();
            double realized = grossCFDto.get(0).getRealized();
            double unrealized = grossCFDto.get(0).getUnrealized();
            double totalValue;
            double multiple;
            double grossIrr;

            XIRR irrCalculator = new XIRR();

            List<Double> grossCf = new ArrayList<>();
            grossCf.add(grossCFDto.get(0).getGrossCF());

            List<Double> dates = new ArrayList<>();
            dates.add(DateUtil.getExcelDate(grossCFDto.get(0).getDate()));
            double[] cf;
            double[] pDates;
            for (int i = 1; i < grossCFDto.size(); i++) {
                if (!Objects.equals(grossCFDto.get(j).getCompanyName(), grossCFDto.get(i).getCompanyName())) {
                    totalValue = realized + unrealized;
                    if (cashInvested != 0) {
                        multiple = totalValue / cashInvested;
                    } else {
                        multiple = 0;
                    }

                    cf = toPrimitive(grossCf.toArray(new Double[0]));
                    pDates = toPrimitive(dates.toArray(new Double[0]));

//                    System.out.println(CFDto.get(i-1).getCompanyName());
//                    System.out.println(dates);
//                    System.out.println(grossCf);

                    grossIrr = irrCalculator.xirr(new XIRRData(cf.length, 0.1, cf, pDates));


                    performanceDtoList.add(new PeFundCompaniesPerformanceDto(grossCFDto.get(i - 1).getCompanyName(), cashInvested, realized, unrealized, totalValue, multiple, (grossIrr - 1)*100, 123));
                    totalInvestedAmount = totalInvestedAmount + cashInvested;
                    totalRealized = totalRealized + realized;
                    totalUnrealized = totalUnrealized + unrealized;
                    totalTotalValue = totalTotalValue + totalValue;

                    cashInvested = grossCFDto.get(i).getInvested();
                    realized = grossCFDto.get(i).getRealized();
                    unrealized = grossCFDto.get(i).getUnrealized();


                    dto.setFundCompanyPerformance(performanceDtoList);

                    grossCf = new ArrayList<>();
                    grossCf.add(grossCFDto.get(i).getGrossCF());

                    dates = new ArrayList<>();
                    dates.add(DateUtil.getExcelDate(grossCFDto.get(i).getDate()));
                    j = i;

                } else {
                    cashInvested = cashInvested + grossCFDto.get(i).getInvested();
                    realized = realized + grossCFDto.get(i).getRealized();
                    unrealized = unrealized + grossCFDto.get(i).getUnrealized();

                    grossCf.add(grossCFDto.get(i).getGrossCF());

                    dates.add(DateUtil.getExcelDate(grossCFDto.get(i).getDate()));

                }
            }

            totalValue = realized + unrealized;
            if (cashInvested != 0) {
                multiple = totalValue / cashInvested;
            } else {
                multiple = 0;
            }

            cf = toPrimitive(grossCf.toArray(new Double[0]));
            pDates = toPrimitive(dates.toArray(new Double[0]));
            grossIrr = irrCalculator.xirr(new XIRRData(cf.length, 0.1, cf, pDates));

            totalInvestedAmount = totalInvestedAmount + cashInvested;
            totalRealized = totalRealized + realized;
            totalUnrealized = totalUnrealized + unrealized;
            totalTotalValue = totalTotalValue + totalValue;

            totalGrossTvpi = totalTotalValue/-totalInvestedAmount;

            performanceDtoList.add(new PeFundCompaniesPerformanceDto(grossCFDto.get(grossCFDto.size() - 1).getCompanyName(), cashInvested, realized, unrealized, totalValue, multiple, (grossIrr - 1)*100, 123));


            dto.setNumberOfInvestments(performanceDtoList.size());
            dto.setInvestedAmount(-totalInvestedAmount);
            dto.setRealized(totalRealized);
            dto.setUnrealized(totalUnrealized);
            dto.setGrossTvpi(totalGrossTvpi);



            System.out.println("JOB IS DONE in service");
        }

        double totalNetDrawn = 0;
        double totalNetDistributed = 0;
        double totalNav = 0;

        if(!netCFDto.isEmpty()){

            for(int i = 0; i < netCFDto.size(); i++){
                totalNetDrawn = totalNetDrawn + netCFDto.get(i).getDrawn();
                totalNetDistributed = totalNetDistributed + netCFDto.get(i).getDistributed();
                totalNav = totalNav + netCFDto.get(i).getNav();
            }
        }
        System.out.println("totalNetDistributed = " + totalNetDistributed);
        System.out.println("totalNetDrawn = " + totalNetDrawn);

        dto.setDpi(totalNetDistributed / -totalNetDrawn);
        dto.setNetTvpi((totalNetDistributed + totalNav) / -totalNetDrawn);

        return dto;
    }

    @Override
    public Set<PeFundDto> loadFirmFunds(Long firmId) {
        Page<Fund> page = this.repository.findByFirmId(firmId, new PageRequest(0,10, new Sort(Sort.Direction.ASC, "vintage")));
        return this.converter.disassembleSet(page.getContent());
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }
}

