package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.pe.PEFundRepository;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEIndustry;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.pe.*;
import kz.nicnbk.service.converter.pe.PEFundEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by zhambyl on 15-Nov-16.
 */
@Service
public class PEFundServiceImpl implements PEFundService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

//    private static final double[] EMPTY_DOUBLE_ARRAY = null;

    @Autowired
    private PEFundRepository peFundRepository;

    @Autowired
    private PEFundEntityConverter converter;

    @Autowired
    private PEGrossCashflowService grossCFService;

    @Autowired
    private PENetCashflowService netCFService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PECompanyPerformanceService performanceService;

    @Autowired
    private PECompanyPerformanceIddService performanceIddService;

    @Autowired
    private PEOnePagerDescriptionsService onePagerDescriptionsService;

    @Autowired
    private PEFundManagementTeamService managementTeamService;

    @Autowired
    private PEIrrService irrService;

    @Autowired
    private FileService fileService;

    @Override
    public PEFundDto get(Long fundId) {
        try {
            PEFundDto dto = this.converter.disassemble(this.peFundRepository.findOne(fundId));

            dto.setGrossCashflow(this.grossCFService.findByFundId(fundId));
            dto.setNetCashflow(this.netCFService.findByFundId(fundId));
            dto.setCompanyPerformance(this.performanceService.findByFundId(fundId));
            dto.setCompanyPerformanceIdd(this.performanceIddService.findByFundId(fundId));
            dto.setOnePagerDescriptions(this.onePagerDescriptionsService.findByFundId(fundId));
            dto.setManagementTeam(this.managementTeamService.findByFundId(fundId));
//            calculatePerformanceParameters(grossCFDto, netCFDto, dto);

            // load logo
//            if (dto.getFirm() != null && dto.getFirm().getLogo() != null) {
//                try {
//                    dto.getFirm().getLogo().setBytes(IOUtils.toByteArray(fileService.getFileInputStream(dto.getFirm().getLogo().getId(), FileTypeLookup.PE_FIRM_LOGO.getCode())));
//                } catch (Exception ex) {
//                    logger.error("Error loading PE firm logo: fund=" + fundId, ex);
//                }
//            }

            return dto;
        } catch (Exception ex) {
            logger.error("Error loading PE fund: " + fundId, ex);
            return null;
        }
    }

    @Override
    public Long save(PEFundDto fundDto, String updater) {

        // TODO: transactions

        try {
            if (fundDto.getId() == null){
                fundDto.setOwner(updater);
            }
            if (fundDto.getFundName() == null || fundDto.getFundName().equals("")) {
                logger.error("Error saving PE fund: " + (fundDto.getId() != null ? fundDto.getId() : "new") + ", null or empty FundName");
                return null;
            }
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
            return null;
        }
    }

    @Override
    public PECompanyPerformanceResultDto savePerformance(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String updater) {

        try {
            if (this.peFundRepository.findOne(fundId) == null) {
                logger.error("Error saving PE fund's company performance: " + fundId);
                return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            PECompanyPerformanceResultDto resultDto = this.performanceService.saveList(performanceDtoList, fundId, updater);

            if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
                logger.info("PE fund's company performance updated: " + fundId + ", by " + updater);
            } else {
                logger.error("Error saving PE fund's company performance: " + fundId);
            }

            return resultDto;
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance: " + fundId ,ex);
            return new PECompanyPerformanceResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's company performance", "");
        }
    }

    @Override
    public PEFundTrackRecordResultDto calculateTrackRecord(Long fundId, int calculationType) {

        try {
            PEFund fund = this.peFundRepository.findOne(fundId);
            if (fund == null) {
                logger.error("Error calculating PE fund's Track Record: " + fundId);
                return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            if (calculationType == 0) {
                logger.info("Successfully calculated PE fund's Track Record: " + fundId);
                return new PEFundTrackRecordResultDto(
                        new PEFundTrackRecordDto(
                                0,
                                fund.getNumberOfInvestments(),
                                fund.getInvestedAmount(),
                                fund.getRealized(),
                                fund.getUnrealized(),
                                fund.getDpi(),
                                fund.getNetIrr(),
                                fund.getNetTvpi(),
                                fund.getGrossIrr(),
                                fund.getGrossTvpi(),
                                fund.getAsOfDate(),
                                fund.getBenchmarkNetIrr(),
                                fund.getBenchmarkNetTvpi(),
                                fund.getBenchmarkName()),
                        StatusResultType.SUCCESS, "", "", "");
            } else if (calculationType == 1) {
                PEFundTrackRecordResultDto resultDto = this.performanceService.calculateTrackRecord(fundId);

                resultDto.getTrackRecordDTO().setNetIrr(fund.getNetIrr());
                resultDto.getTrackRecordDTO().setNetTvpi(fund.getNetTvpi());
                resultDto.getTrackRecordDTO().setGrossIrr(fund.getGrossIrr());
                resultDto.getTrackRecordDTO().setAsOfDate(fund.getAsOfDate());
                resultDto.getTrackRecordDTO().setBenchmarkNetIrr(fund.getBenchmarkNetIrr());
                resultDto.getTrackRecordDTO().setBenchmarkNetTvpi(fund.getBenchmarkNetTvpi());
                resultDto.getTrackRecordDTO().setBenchmarkName(fund.getBenchmarkName());

                logger.info("Successfully calculated PE fund's Track Record: " + fundId);
                return resultDto;
            } else if (calculationType == 2) {
                PEFundTrackRecordResultDto resultDto = this.performanceIddService.calculateTrackRecord(fundId);

                resultDto.getTrackRecordDTO().setNetIrr(fund.getNetIrr());
                resultDto.getTrackRecordDTO().setNetTvpi(fund.getNetTvpi());
                resultDto.getTrackRecordDTO().setAsOfDate(fund.getAsOfDate());
                resultDto.getTrackRecordDTO().setBenchmarkNetIrr(fund.getBenchmarkNetIrr());
                resultDto.getTrackRecordDTO().setBenchmarkNetTvpi(fund.getBenchmarkNetTvpi());
                resultDto.getTrackRecordDTO().setBenchmarkName(fund.getBenchmarkName());

                List<PEGrossCashflowDto> cashflowDtoList = this.grossCFService.findByFundIdSortedByDate(fundId);
                if (cashflowDtoList == null) {
                    logger.error("Error calculating PE fund's Track Record: " + fundId);
                    return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Error calculating PE fund's Track Record", "");
                }
                resultDto.getTrackRecordDTO().setGrossIrr(this.irrService.getIRR(cashflowDtoList));

                logger.info("Successfully calculated PE fund's Track Record: " + fundId);
                return resultDto;
            } else {
                logger.error("Error calculating PE fund's Track Record: " + fundId);
                return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Error calculating PE fund's Track Record", "");
            }
        } catch (Exception ex) {
            logger.error("Error calculating PE fund's Track Record: " + fundId ,ex);
            return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Error calculating PE fund's Track Record", "");
        }
    }

    @Override
    public PEFundTrackRecordResultDto updateStatistics(Long fundId, String username) {

        try {
            PEFund fund = this.peFundRepository.findOne(fundId);
            if (fund == null) {
                logger.error("Error updating PE fund's key statistics: " + fundId);
                return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            PEFundTrackRecordResultDto trackRecordResultDto = calculateTrackRecord(fundId, fund.getCalculationType());
            if (trackRecordResultDto.getStatus().equals(StatusResultType.FAIL)) {
                return trackRecordResultDto;
            }

            fund.setCalculationType(trackRecordResultDto.getTrackRecordDTO().getCalculationType());
            fund.setNumberOfInvestments(trackRecordResultDto.getTrackRecordDTO().getNumberOfInvestments());
            fund.setInvestedAmount(trackRecordResultDto.getTrackRecordDTO().getInvestedAmount());
            fund.setRealized(trackRecordResultDto.getTrackRecordDTO().getRealized());
            fund.setUnrealized(trackRecordResultDto.getTrackRecordDTO().getUnrealized());
            fund.setDpi(trackRecordResultDto.getTrackRecordDTO().getDpi());
            fund.setNetIrr(trackRecordResultDto.getTrackRecordDTO().getNetIrr());
            fund.setNetTvpi(trackRecordResultDto.getTrackRecordDTO().getNetTvpi());
            fund.setGrossIrr(trackRecordResultDto.getTrackRecordDTO().getGrossIrr());
            fund.setGrossTvpi(trackRecordResultDto.getTrackRecordDTO().getGrossTvpi());
            fund.setAsOfDate(trackRecordResultDto.getTrackRecordDTO().getAsOfDate());
            fund.setBenchmarkNetIrr(trackRecordResultDto.getTrackRecordDTO().getBenchmarkNetIrr());
            fund.setBenchmarkNetTvpi(trackRecordResultDto.getTrackRecordDTO().getBenchmarkNetTvpi());
            fund.setBenchmarkName(trackRecordResultDto.getTrackRecordDTO().getBenchmarkName());

            peFundRepository.save(fund);

            logger.error("Updated PE fund's key statistics: " + fundId + ", updater: " + username);
            return trackRecordResultDto;
        } catch (Exception ex) {
            logger.error("Error updating PE fund's key statistics: " + fundId, ex);
            return new PEFundTrackRecordResultDto(new PEFundTrackRecordDto(), StatusResultType.FAIL, "", "Error updating PE fund's key statistics", "");
        }
    }

    @Override
    public PECompanyPerformanceAndFundTrackRecordResultDto savePerformanceAndUpdateStatistics(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String updater) {

        try {
            if (this.peFundRepository.findOne(fundId) == null) {
                logger.error("Error saving PE fund's company performance and restoring/updating key statistics, fund: " + fundId);
                return new PECompanyPerformanceAndFundTrackRecordResultDto(new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }
        } catch (Exception ex) {
            logger.error("Error saving PE fund's company performance and restoring/updating key statistics, fund: " + fundId);
            return new PECompanyPerformanceAndFundTrackRecordResultDto(new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", "Error", "");
        }

        PECompanyPerformanceResultDto performanceResultDto = savePerformance(performanceDtoList, fundId, updater);

        if (performanceResultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's company performance and restoring/updating key statistics, fund: " + fundId);
            return new PECompanyPerformanceAndFundTrackRecordResultDto(new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", performanceResultDto.getMessageEn(), "");
        }

        PEFundTrackRecordResultDto trackRecordResultDto = updateStatistics(fundId, updater);

        if (trackRecordResultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's company performance and restoring/updating key statistics, fund: " + fundId);
            return new PECompanyPerformanceAndFundTrackRecordResultDto(new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", trackRecordResultDto.getMessageEn(), "");
        }

        logger.info("Successfully saved PE fund's company performance and restored/updated key statistics, fund: " + fundId + ", updater: " + updater);
        return new PECompanyPerformanceAndFundTrackRecordResultDto(
                trackRecordResultDto.getTrackRecordDTO(),
                performanceResultDto.getPerformanceDtoList(),
                StatusResultType.SUCCESS, "", "Successfully saved PE fund's company performance and restored/updated key statistics", "");
    }

    @Override
    public PEGrossCashflowResultDto saveGrossCF(List<PEGrossCashflowDto> cashflowDtoList, Long fundId, String updater) {

        try {
            if (this.peFundRepository.findOne(fundId) == null) {
                logger.error("Error saving PE fund's gross cash flow: " + fundId);
                return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId);
            return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error", "");
        }

        PEGrossCashflowResultDto resultDto = this.grossCFService.saveList(cashflowDtoList, fundId, updater);

        if (resultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId);
        } else {
            logger.info("PE fund's gross cash flow updated: " + fundId + ", by " + updater);
        }

        return resultDto;
    }

    @Override
    public PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto saveGrossCFAndRecalculatePerformanceIddAndUpdateStatistics(List<PEGrossCashflowDto> cashflowDtoList, Long fundId, String updater) {

        try {
            if (this.peFundRepository.findOne(fundId) == null) {
                logger.error("Error saving PE fund's gross cash flow and updating company performance and restoring/updating key statistics, fund: " + fundId);
                return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(new ArrayList<>(), new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow and updating company performance and restoring/updating key statistics, fund: " + fundId);
            return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(new ArrayList<>(), new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", "Error", "");
        }

        PEGrossCashflowResultDto grossCFResultDto = saveGrossCF(cashflowDtoList, fundId, updater);

        if (grossCFResultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's gross cash flow and updating company performance and restoring/updating key statistics, fund: " + fundId);
            return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(new ArrayList<>(), new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", grossCFResultDto.getMessageEn(), "");
        }

        PECompanyPerformanceIddResultDto performanceIddResultDto = this.performanceIddService.recalculatePerformanceIdd(fundId, updater);

        if (performanceIddResultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's gross cash flow and updating company performance and restoring/updating key statistics, fund: " + fundId);
            return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(new ArrayList<>(), new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", performanceIddResultDto.getMessageEn(), "");
        }

        PEFundTrackRecordResultDto trackRecordResultDto = updateStatistics(fundId, updater);

        if (trackRecordResultDto.getStatus().equals(StatusResultType.FAIL)) {
            logger.error("Error saving PE fund's gross cash flow and updating company performance and restoring/updating key statistics, fund: " + fundId);
            return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(new ArrayList<>(), new PEFundTrackRecordDto(), new ArrayList<>(), StatusResultType.FAIL, "", trackRecordResultDto.getMessageEn(), "");
        }

        logger.info("Successfully saved PE fund's gross cash flow and updated company performance and restored/updated key statistics, fund: " + fundId + ", updater: " + updater);
        return new PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(
                performanceIddResultDto.getPerformanceIddDtoList(),
                trackRecordResultDto.getTrackRecordDTO(),
                grossCFResultDto.getCashflowDtoList(),
                StatusResultType.SUCCESS, "", "Successfully saved PE fund's gross cash flow and updated company performance and restored/updated key statistics", "");
    }

    @Override
    public List<PEFundDto> loadFirmFunds(Long firmId, boolean report) {
        try {
            Page<PEFund> page = this.peFundRepository.findByFirmId(firmId, new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "vintage")));
            List<PEFundDto> fundDtoList = this.converter.disassembleList(page.getContent());
            if (report) {
                for (PEFundDto fundDto : fundDtoList) {
                    List<PEGrossCashflowDto> grossCFDto = this.grossCFService.findByFundId(fundDto.getId());
                    List<PENetCashflowDto> netCFDto = this.netCFService.findByFundId(fundDto.getId());

                    fundDto.setGrossCashflow(grossCFDto);
                    fundDto.setNetCashflow(netCFDto);

                    //Commented by Pak
//                    if(!grossCFDto.isEmpty()) {
//                        calculatePerformanceParameters(grossCFDto, netCFDto, fundDto);
//                    }
                }
            }
            return fundDtoList;
        } catch (Exception ex) {
            logger.error("Failed to load PE firm funds: firm=" + firmId, ex);
        }
        return null;
    }

    @Override
    public String getIndustriesAsString(Long fundId) {
        PEFund fund = this.peFundRepository.findOne(fundId);
        if (fund == null) {
            return null;
        }
        Set<PEIndustry> industrySet = fund.getIndustry();
        if (industrySet == null) {
            return null;
        }
        String st = "";
        for (PEIndustry industry : industrySet) {
            if (st.equals("")) {
                st = industry.getNameEn();
            } else {
                st += ", " + industry.getNameEn();
            }
        }

        return st;
    }

    @Override
    public String getStrategiesAsString(Long fundId) {
        PEFund fund = this.peFundRepository.findOne(fundId);
        if (fund == null) {
            return null;
        }
        Set<Strategy> strategySet = fund.getStrategy();
        if (strategySet == null) {
            return null;
        }
        String st = "";
        for (Strategy strategy : strategySet) {
            if (st.equals("")) {
                st = strategy.getNameEn();
            } else {
                st += ", " + strategy.getNameEn();
            }
        }

        return st;
    }

    @Override
    public String getGeographiesAsString(Long fundId) {
        PEFund fund = this.peFundRepository.findOne(fundId);
        if (fund == null) {
            return null;
        }
        Set<Geography> geographySet = fund.getGeography();
        if (geographySet == null) {
            return null;
        }
        String st = "";
        for (Geography geography : geographySet) {
            if (st.equals("")) {
                st = geography.getNameEn();
            } else {
                st += ", " + geography.getNameEn();
            }
        }

        return st;
    }

//    @Override
//    public Date updateAsOfDateOnePager(Date asOfDateOnePager, Long fundId) {
//        try {
//            PEFund fund = this.peFundRepository.findOne(fundId);
//            fund.setAsOfDateOnePager(asOfDateOnePager);
//            this.peFundRepository.save(fund);
//            return fund.getAsOfDateOnePager();
//        } catch (Exception ex) {
//            logger.error("Error updating PE fund As of Date for One Pager: " + fundId, ex);
//        }
//        return null;
//    }

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
//            List<PECompanyPerformanceDto> performanceDtoList = new ArrayList<>();
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
//                    performanceDtoList.add(new PECompanyPerformanceDto(grossCfDtoList.get(i - 1).getCompanyName(), Math.round(-cashInvested),
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
//                    dto.setCompanyPerformance(performanceDtoList);
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
//            performanceDtoList.add(new PECompanyPerformanceDto(grossCfDtoList.get(grossCfDtoList.size() - 1).getCompanyName(), Math.round(-cashInvested),
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
//
//
//    public static double[] toPrimitive(Double[] array) {
//        if (array == null) {
//            return null;
//        } else if (array.length == 0) {
//            return EMPTY_DOUBLE_ARRAY;
//        }
//        final double[] result = new double[array.length];
//        for (int i = 0; i < array.length; i++) {
//            result[i] = array[i].doubleValue();
//        }
//        return result;
//    }
}