package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.pe.PEFundRepository;
import kz.nicnbk.repo.api.pe.PEGrossCashflowRepository;
import kz.nicnbk.repo.api.pe.PENetCashflowRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.repo.model.pe.PENetCashflow;
import kz.nicnbk.service.api.pe.PEFundCompaniesPerformanceService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PENetCashflowService;
import kz.nicnbk.service.converter.pe.PEGrossCashflowEntityConverter;
import kz.nicnbk.service.converter.pe.PEFundEntityConverter;
import kz.nicnbk.service.converter.pe.PENetCashflowEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PEFundServiceImpl implements PEFundService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    private static final double[] EMPTY_DOUBLE_ARRAY = null;

    @Autowired
    private PEFundRepository peFundRepository;

    @Autowired
    private PEFundEntityConverter converter;

    @Autowired
    private PEGrossCashflowRepository grossCFRepository;

    @Autowired
    private PEGrossCashflowService grossCFService;

    @Autowired
    private PEGrossCashflowEntityConverter grossCFConverter;

    @Autowired
    private PENetCashflowRepository netCFRepository;

    @Autowired
    private PENetCashflowService netCFService;

    @Autowired
    private PENetCashflowEntityConverter netCFConverter;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PEFundCompaniesPerformanceService performanceService;

    @Override
    public PEFundDto get(Long id) {
        try {
            PEFund entity = this.peFundRepository.findOne(id);
            PEFundDto dto = this.converter.disassemble(entity);

            List<PEGrossCashflowDto> grossCFDto = this.grossCFService.findByFundId(id);
            List<PENetCashflowDto> netCFDto = this.netCFService.findByFundId(id);
            List<PEFundCompaniesPerformanceDto> performanceDto = this.performanceService.getEntityDtosByFundId(id);

            dto.setGrossCashflow(grossCFDto);
            dto.setNetCashflow(netCFDto);
            dto.setFundCompanyPerformance(performanceDto);
//            calculatePerformanceParameters(grossCFDto, netCFDto, dto);
            return dto;
        } catch (Exception ex) {
            logger.error("Error loading PE fund: " + id, ex);
        }
        return null;
    }

    @Override
    public Long save(PEFundDto fundDto, String updater) {

        // TODO: transactions

        try {
            PEFund entity = converter.assemble(fundDto);
            if(fundDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(fundDto.getOwner());
                // set creator
                entity.setCreator(employee);
            } else { // UPDATE
                // set creator
                Employee employee = this.peFundRepository.findOne(fundDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = peFundRepository.findOne(fundDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long id = peFundRepository.save(entity).getId();
            fundDto.setId(id);

            // TODO: log cash flow saving

            logger.info(fundDto.getId() == null ? "PE fund created: " + id + ", by " + entity.getCreator().getUsername() :
                    "PE fund updated: " + id + ", by " + updater);
            return id;
        } catch (Exception ex) {
            logger.error("Error saving PE fund: " + (fundDto != null && fundDto.getId() != null ? fundDto.getId() : "new") ,ex);
        }
        return null;
    }

    @Override
    public StatusResultDto savePerformance(List<PEFundCompaniesPerformanceDto> performanceDtoList, Long fundId, String updater) {

        StatusResultDto statusResultDto = new StatusResultDto(StatusResultType.FAIL, "", "", "");

        try {
            String saveResponse = this.performanceService.saveList(performanceDtoList, fundId);
            if (!saveResponse.equals("Ok")) {
                statusResultDto.setStatus(StatusResultType.FAIL);
                statusResultDto.setMessageEn(saveResponse);
                return statusResultDto;
            }

            PEFund fund = peFundRepository.findOne(fundId);
            fund.setAutoCalculation(false);
            peFundRepository.save(fund);

            logger.info("PE fund's company performance updated: " + fundId + ", by " + updater);

            statusResultDto.setStatus(StatusResultType.SUCCESS);
            statusResultDto.setMessageEn("Successfully saved fund's company performance");
            return statusResultDto;
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId ,ex);

            statusResultDto.setStatus(StatusResultType.FAIL);
            statusResultDto.setMessageEn("Error saving fund's company performance");
            return statusResultDto;
        }
    }

    @Override
    public PEFundTrackRecordResultDto savePerformanceAndRecalculateStatistics(List<PEFundCompaniesPerformanceDto> performanceDtoList, Long fundId, String updater) {

        StatusResultDto statusResultDto = this.savePerformance(performanceDtoList, fundId, updater);

        if (statusResultDto.getStatus().getCode().equals("FAIL")) {
            return new PEFundTrackRecordResultDto(
                    new PEFundTrackRecordDto(),
                    statusResultDto.getStatus(), statusResultDto.getMessageRu(), statusResultDto.getMessageEn(), statusResultDto.getMessageKz()
            );
        }

        try {
            PEFund fund = this.peFundRepository.findOne(fundId);
            if (fund == null) {
                statusResultDto.setStatus(StatusResultType.FAIL);
                statusResultDto.setMessageEn("Fund doesn't exist!");
                return new PEFundTrackRecordResultDto(
                        new PEFundTrackRecordDto(),
                        statusResultDto.getStatus(), statusResultDto.getMessageRu(), statusResultDto.getMessageEn(), statusResultDto.getMessageKz()
                );
            }

            fund.setNumberOfInvestments(0);
            fund.setInvestedAmount(0.0);
            fund.setRealized(0.0);
            fund.setUnrealized(0.0);

            for (PEFundCompaniesPerformanceDto performanceDto : performanceDtoList) {
                fund.setNumberOfInvestments(fund.getNumberOfInvestments() + 1);
                if (performanceDto.getInvested() != null) {
                    fund.setInvestedAmount(fund.getInvestedAmount() + performanceDto.getInvested());
                }
                if (performanceDto.getRealized() != null) {
                    fund.setRealized(fund.getRealized() + performanceDto.getRealized());
                }
                if (performanceDto.getUnrealized() != null) {
                    fund.setUnrealized(fund.getUnrealized() + performanceDto.getUnrealized());
                }
            }

            if (fund.getInvestedAmount() != 0) {
                fund.setDpi(fund.getRealized()/fund.getInvestedAmount());
                fund.setGrossTvpi((fund.getRealized() + fund.getUnrealized())/fund.getInvestedAmount());
            } else {
                fund.setDpi(null);
                fund.setGrossTvpi(null);
            }

            fund.setAutoCalculation(true);
            peFundRepository.save(fund);

            statusResultDto.setStatus(StatusResultType.SUCCESS);
            statusResultDto.setMessageEn("Successfully saved fund's company performance and updated the key fund statistics");
            return new PEFundTrackRecordResultDto(
                    new PEFundTrackRecordDto(
                            fund.getNumberOfInvestments(),
                            fund.getInvestedAmount(),
                            fund.getRealized(),
                            fund.getUnrealized(),
                            fund.getDpi(),
                            null, null, null,
                            fund.getGrossTvpi(),
                            null, null, null, null, null),
                    statusResultDto.getStatus(), statusResultDto.getMessageRu(), statusResultDto.getMessageEn(), statusResultDto.getMessageKz());
        } catch (Exception ex) {
            logger.error("Error updating PE fund's key statistics: " + fundId ,ex);

            statusResultDto.setStatus(StatusResultType.FAIL);
            statusResultDto.setMessageEn("Error updating fund's key statistics");
            return new PEFundTrackRecordResultDto(
                    new PEFundTrackRecordDto(),
                    statusResultDto.getStatus(), statusResultDto.getMessageRu(), statusResultDto.getMessageEn(), statusResultDto.getMessageKz()
            );
        }
    }

    @Override
    public StatusResultDto saveGrossCF(List<PEGrossCashflowDto> cashflowDtoList, Long fundId, String updater) {

        StatusResultDto statusResultDto = new StatusResultDto(StatusResultType.FAIL, "", "", "");

        try {
            String saveResponse = this.grossCFService.saveList(cashflowDtoList, fundId);
            if (!saveResponse.equals("Ok")) {
                statusResultDto.setStatus(StatusResultType.FAIL);
                statusResultDto.setMessageEn(saveResponse);
                return statusResultDto;
            }

            logger.info("PE fund's gross cash flow updated: " + fundId + ", by " + updater);
            statusResultDto.setStatus(StatusResultType.SUCCESS);
            statusResultDto.setMessageEn("Successfully saved fund's gross cash flow");
            return statusResultDto;
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId ,ex);
            statusResultDto.setStatus(StatusResultType.FAIL);
            statusResultDto.setMessageEn("Error saving fund's gross cash flow");
            return statusResultDto;
        }
    }

    @Override
    public List<PEFundDto> loadFirmFunds(Long firmId, boolean report) {
        try {
            Page<PEFund> page = this.peFundRepository.findByFirmId(firmId, new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "vintage")));
            List<PEFundDto> fundDtoList = this.converter.disassembleList(page.getContent());
            if (report) {
                for (PEFundDto fundDto : fundDtoList) {
                    List<PEGrossCashflow> grossCfEntity = this.grossCFRepository.getEntitiesByFundId(fundDto.getId(), new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName")));
                    List<PENetCashflow> netCfEntity = this.netCFRepository.getEntitiesByFundId(fundDto.getId());
                    List<PEGrossCashflowDto> grossCFDto = this.grossCFConverter.disassembleList(grossCfEntity);
                    List<PENetCashflowDto> netCFDto = this.netCFConverter.disassembleList(netCfEntity);

                    fundDto.setGrossCashflow(grossCFDto);
                    fundDto.setNetCashflow(netCFDto);

                    if(!grossCFDto.isEmpty()) {
                        //Commented by Pak
//                        calculatePerformanceParameters(grossCFDto, netCFDto, fundDto);
                    }
                }
            }
            return fundDtoList;
        } catch (Exception ex) {
            logger.error("Failed to load PE firm funds: firm=" + firmId, ex);
        }
        return null;
    }

    //Commented by Pak
//    private void calculatePerformanceParameters(List<PEGrossCashflowDto> grossCfDtoList, List<PENetCashflowDto> netCfDtoList, PEFundDto dto) {
//        double totalInvestedAmount = 0;
//        double totalRealized = 0;
//        double totalUnrealized = 0;
//        double totalTotalValue = 0;
//        double totalGrossTvpi = 0;
//
//        XIRR irrCalculator = new XIRR();
//
//        if(!grossCfDtoList.isEmpty()) {
//
//            // Calculating fund's companies performance
//            List<PEFundCompaniesPerformanceDto> performanceDtoList = new ArrayList<>();
//
//            int j = 0;
//
//            double cashInvested = grossCfDtoList.get(0).getInvested();
//            double realized = grossCfDtoList.get(0).getRealized();
//            double unrealized = grossCfDtoList.get(0).getUnrealized();
//            double totalValue = 0;
//            double multiple;
//            double grossIrr;
//            double fundGrossIrr;
//
//            List<Double> grossCf = new ArrayList<>();
//            grossCf.add(grossCfDtoList.get(0).getGrossCF());
//
//            List<Double> dates = new ArrayList<>();
//            dates.add(DateUtil.getExcelDate(grossCfDtoList.get(0).getDate()));
//
//            double[] cf;
//            double[] pDates;
//
//            for (int i = 1; i < grossCfDtoList.size(); i++) {
//                if (!Objects.equals(grossCfDtoList.get(j).getCompanyName(), grossCfDtoList.get(i).getCompanyName())) {
//                    totalValue = realized + unrealized;
//                    if (cashInvested != 0) {
//                        multiple = totalValue / cashInvested;
//                    } else {
//                        multiple = 0;
//                    }
//
//                    cf = toPrimitive(grossCf.toArray(new Double[0]));
//                    pDates = toPrimitive(dates.toArray(new Double[0]));
//
//                    grossIrr = irrCalculator.xirr(new XIRRData(cf.length, 0.2, cf, pDates));
//                    if(Double.isNaN(grossIrr) || grossIrr == 0 || cf.length == 1){
//                        grossIrr = Double.NaN;
//                    } else {
//                        grossIrr = Math.round(((grossIrr - 1) * 100)*100)/100.00;
//                    }
//
//                    performanceDtoList.add(new PEFundCompaniesPerformanceDto(grossCfDtoList.get(i - 1).getCompanyName(), Math.round(-cashInvested),
//                            Math.round(realized), Math.round(unrealized), Math.round(totalValue),
//                            Math.round(-multiple*100)/100.00, grossIrr));
//
//                    totalInvestedAmount = totalInvestedAmount + cashInvested;
//                    totalRealized = totalRealized + realized;
//                    totalUnrealized = totalUnrealized + unrealized;
//                    totalTotalValue = totalTotalValue + totalValue;
//
//                    cashInvested = grossCfDtoList.get(i).getInvested();
//                    realized = grossCfDtoList.get(i).getRealized();
//                    unrealized = grossCfDtoList.get(i).getUnrealized();
//
//
//                    dto.setFundCompanyPerformance(performanceDtoList);
//
//                    grossCf = new ArrayList<>();
//                    grossCf.add(grossCfDtoList.get(i).getGrossCF());
//
//                    dates = new ArrayList<>();
//                    dates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));
//
//                    j = i;
//                } else {
//
//                    cashInvested = cashInvested + grossCfDtoList.get(i).getInvested();
//                    realized = realized + grossCfDtoList.get(i).getRealized();
//                    unrealized = unrealized + grossCfDtoList.get(i).getUnrealized();
//
//                    grossCf.add(grossCfDtoList.get(i).getGrossCF());
//
//                    dates.add(DateUtil.getExcelDate(grossCfDtoList.get(i).getDate()));
//                }
//            }
//
//
//
//            totalValue = realized + unrealized;
//            if (cashInvested != 0) {
//                multiple = totalValue / cashInvested;
//            } else {
//                multiple = 0;
//            }
//
//            cf = toPrimitive(grossCf.toArray(new Double[0]));
//            pDates = toPrimitive(dates.toArray(new Double[0]));
//
//            grossIrr = irrCalculator.xirr(new XIRRData(cf.length, 0.2, cf, pDates));
//
//            //Copy of main cashflow dto in order to calculate Gross IRR for overall fund
//            List<PEGrossCashflowDto> grossCfDtoList2 = new ArrayList<>(grossCfDtoList);
//            Collections.copy(grossCfDtoList2, grossCfDtoList);
//            Collections.sort(grossCfDtoList2);
//            double[] fundCf = new double[grossCfDtoList2.size()];
//            double[] fundDate = new double[grossCfDtoList2.size()];
//
//            //setting double array for CF_values and Dates for XIRR calculation
//            for(int k = 0; k < grossCfDtoList2.size(); k++){
//                fundCf[k] = grossCfDtoList2.get(k).getGrossCF();
//                fundDate[k] = DateUtil.getExcelDate(grossCfDtoList2.get(k).getDate());
//            }
//
//            fundGrossIrr = irrCalculator.xirr(new XIRRData(fundCf.length, 0.2, fundCf, fundDate));
//
//            totalInvestedAmount = totalInvestedAmount + cashInvested;
//            totalRealized = totalRealized + realized;
//            totalUnrealized = totalUnrealized + unrealized;
//            totalTotalValue = totalTotalValue + totalValue;
//
//            totalGrossTvpi = totalTotalValue/-totalInvestedAmount;
//
//            performanceDtoList.add(new PEFundCompaniesPerformanceDto(grossCfDtoList.get(grossCfDtoList.size() - 1).getCompanyName(), Math.round(-cashInvested),
//                    Math.round(realized), Math.round(unrealized), Math.round(totalValue*100)/100.00, Math.round(-multiple*100)/100.00,
//                    Math.round(((grossIrr - 1) * 100)*100)/100.00));
//
//            dto.setNumberOfInvestments(performanceDtoList.size());
//            dto.setInvestedAmount(Math.round(-totalInvestedAmount/1000000));
//            dto.setRealized(Math.round(totalRealized/1000000));
//            dto.setUnrealized(Math.round(totalUnrealized/1000000));
//            dto.setGrossTvpi(Math.round(totalGrossTvpi*100)/100.00);
//            dto.setGrossIrr(Math.round((fundGrossIrr - 1) * 100 * 100)/100.00);
//        }
//
//        double totalNetDrawn = 0;
//        double totalNetDistributed = 0;
//        double totalNav = 0;
//        double fundNetIrr = 0;
//        double[] netCf = new double[netCfDtoList.size()];
//        double[] netCfDate = new double[netCfDtoList.size()];
//
//        Collections.sort(netCfDtoList);
//
//        if(!netCfDtoList.isEmpty()){
//            for(int i = 0; i < netCfDtoList.size(); i++){
//                netCf[i] = netCfDtoList.get(i).getNetCF();
//                netCfDate[i] = DateUtil.getExcelDate(netCfDtoList.get(i).getTransactionDate());
//                totalNetDrawn = totalNetDrawn + netCfDtoList.get(i).getDrawn();
//                totalNetDistributed = totalNetDistributed + netCfDtoList.get(i).getDistributed();
//                totalNav = totalNav + netCfDtoList.get(i).getNav();
//            }
//
//            fundNetIrr = irrCalculator.xirr(new XIRRData(netCf.length, 0.2, netCf, netCfDate));
//
//            dto.setDpi(Math.round((totalNetDistributed / -totalNetDrawn) * 100)/100.00);
//            dto.setNetTvpi(Math.round((totalNetDistributed + totalNav) / -totalNetDrawn * 100) / 100.00);
//            dto.setNetIrr(Math.round((fundNetIrr - 1) * 100 * 100) / 100.00);
//        }
//
//        return;
//    }


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