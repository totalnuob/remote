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

        calculatePerformanceParameters(grossCFDto, netCFDto, dto);

        return dto;
    }

    @Override
    public List<PeFundDto> loadFirmFunds(Long firmId) {
        Page<Fund> page = this.repository.findByFirmId(firmId, new PageRequest(0,10, new Sort(Sort.Direction.ASC, "vintage")));
        List<PeFundDto> fundDtoList = this.converter.disassembleList(page.getContent());
        for(PeFundDto fundDto: fundDtoList){
            List<PeGrossCashflow> grossCfEntity = this.grossCFRepository.getEntitiesByFundId(fundDto.getId(), new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")));
            List<PeNetCashflow> netCfEntity = this.netCFRepository.getEntitiesByFundId(fundDto.getId());
            List<PeGrossCashflowDto> grossCFDto = this.grossCFConverter.disassembleList(grossCfEntity);
            List<PeNetCashflowDto> netCFDto = this.netCFConverter.disassembleList(netCfEntity);

            fundDto.setGrossCashflow(grossCFDto);
            fundDto.setNetCashflow(netCFDto);

            calculatePerformanceParameters(grossCFDto, netCFDto, fundDto);
        }
        return fundDtoList;
    }

    private void calculatePerformanceParameters(List<PeGrossCashflowDto> grossCfDtoList, List<PeNetCashflowDto> netCfDtoList, PeFundDto dto) {
        double totalInvestedAmount = 0;
        double totalRealized = 0;
        double totalUnrealized = 0;
        double totalTotalValue = 0;
        double totalGrossTvpi = 0;

        XIRR irrCalculator = new XIRR();

        if(!grossCfDtoList.isEmpty()) {

            // Calculating fund's companies performance
            List<PeFundCompaniesPerformanceDto> performanceDtoList = new ArrayList<>();

            int j = 0;

            double cashInvested = grossCfDtoList.get(0).getInvested();
            double realized = grossCfDtoList.get(0).getRealized();
            double unrealized = grossCfDtoList.get(0).getUnrealized();
            double totalValue;
            double multiple;
            double grossIrr;
            double fundGrossIrr;

            List<Double> grossCf = new ArrayList<>();
            grossCf.add(grossCfDtoList.get(0).getGrossCF());

            List<Double> fundGrossCf = new ArrayList<>();
            fundGrossCf.add(grossCfDtoList.get(0).getGrossCF());

            List<Double> dates = new ArrayList<>();
            dates.add(DateUtil.getExcelDate(grossCfDtoList.get(0).getDate()));

            List<Double> fundDates = new ArrayList<>();
            fundDates.add(DateUtil.getExcelDate(grossCfDtoList.get(0).getDate()));

            double[] cf;
            double[] pDates;

            double[] fundCf;
            double[] fpDates;

            for (int i = 1; i < grossCfDtoList.size(); i++) {
                if (!Objects.equals(grossCfDtoList.get(j).getCompanyName(), grossCfDtoList.get(i).getCompanyName())) {
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


                    performanceDtoList.add(new PeFundCompaniesPerformanceDto(grossCfDtoList.get(i - 1).getCompanyName(), Math.round(-cashInvested*100)/100.00, Math.round(realized*100)/100.00, Math.round(unrealized*100)/100.00, Math.round(totalValue*100)/100.00, Math.round(multiple*100)/100.00, Math.round(((grossIrr - 1) * 100)*100)/100.00, 123));

                    totalInvestedAmount = totalInvestedAmount + cashInvested;
                    totalRealized = totalRealized + realized;
                    totalUnrealized = totalUnrealized + unrealized;
                    totalTotalValue = totalTotalValue + totalValue;

                    cashInvested = grossCfDtoList.get(i).getInvested();
                    realized = grossCfDtoList.get(i).getRealized();
                    unrealized = grossCfDtoList.get(i).getUnrealized();


                    dto.setFundCompanyPerformance(performanceDtoList);

                    grossCf = new ArrayList<>();
                    grossCf.add(grossCfDtoList.get(i).getGrossCF());
                    fundGrossCf.add(grossCfDtoList.get(i).getGrossCF());

                    dates = new ArrayList<>();
                    dates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));
                    fundDates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));

                    j = i;

                } else {
                    cashInvested = cashInvested + grossCfDtoList.get(i).getInvested();
                    realized = realized + grossCfDtoList.get(i).getRealized();
                    unrealized = unrealized + grossCfDtoList.get(i).getUnrealized();

                    grossCf.add(grossCfDtoList.get(i).getGrossCF());
                    fundGrossCf.add(grossCfDtoList.get(i).getGrossCF());


                    dates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));
                    fundDates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));

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

            fundCf = toPrimitive(fundGrossCf.toArray(new Double[0]));
            fpDates = toPrimitive(fundDates.toArray(new Double[0]));
            fundGrossIrr = irrCalculator.xirr(new XIRRData(fundCf.length, 0.1, fundCf, fpDates));

            totalInvestedAmount = totalInvestedAmount + cashInvested;
            totalRealized = totalRealized + realized;
            totalUnrealized = totalUnrealized + unrealized;
            totalTotalValue = totalTotalValue + totalValue;

            totalGrossTvpi = totalTotalValue/-totalInvestedAmount;

            performanceDtoList.add(new PeFundCompaniesPerformanceDto(grossCfDtoList.get(grossCfDtoList.size() - 1).getCompanyName(), Math.round(-cashInvested*1000000), Math.round(realized*1000000), Math.round(unrealized*1000000), Math.round(totalValue*100)/100.00, Math.round(multiple*100)/100.00, Math.round(((grossIrr - 1) * 100)*100)/100.00, 123));


            dto.setNumberOfInvestments(performanceDtoList.size());
            dto.setInvestedAmount(Math.round(-totalInvestedAmount/1000000.00));
            dto.setRealized(Math.round(totalRealized/1000000.00));
            dto.setUnrealized(Math.round(totalUnrealized/1000000.00));
            dto.setGrossTvpi(Math.round(totalGrossTvpi*100)/100.00);
            dto.setGrossIrr(Math.round((fundGrossIrr - 1) * 100 * 100)/100.00);

//            System.out.println(fundGrossCf.size());
//            System.out.println(fundDates.size());

            System.out.println("JOB IS DONE in service");
        }

        double totalNetDrawn = 0;
        double totalNetDistributed = 0;
        double totalNav = 0;
        double fundNetIrr = 0;
        List<Double> fundNetCf = new ArrayList<>();
        List<Double> fundNetDates = new ArrayList<>();

        if(!netCfDtoList.isEmpty()){
            for(int i = 0; i < netCfDtoList.size(); i++){
                fundNetCf.add(netCfDtoList.get(i).getNetCF());
                fundNetDates.add(DateUtil.getExcelDate(netCfDtoList.get(i).getTransactionDate()));
                totalNetDrawn = totalNetDrawn + netCfDtoList.get(i).getDrawn();
                totalNetDistributed = totalNetDistributed + netCfDtoList.get(i).getDistributed();
                totalNav = totalNav + netCfDtoList.get(i).getNav();
            }
        }
//
//        System.out.println("totalNetDistributed = " + totalNetDistributed);
//        System.out.println("totalNetDrawn = " + totalNetDrawn);

        System.out.println(fundNetCf.size());
        System.out.println(fundNetDates.size());

        fundNetIrr = irrCalculator.xirr(new XIRRData(fundNetCf.size(), 0.1, toPrimitive(fundNetCf.toArray(new Double[0])), toPrimitive(fundNetDates.toArray(new Double[0])) ));

        dto.setDpi(Math.round((totalNetDistributed / -totalNetDrawn) * 100)/100.00);
        dto.setNetTvpi(Math.round((totalNetDistributed + totalNav) / -totalNetDrawn * 100) / 100.00);
        dto.setNetIrr(Math.round((fundNetIrr - 1) * 100 * 100) / 100.00);

        return;
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

