package kz.nicnbk.service.impl.pe;

import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.repo.api.pe.PEGrossCashflowRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.converter.pe.PEGrossCashflowEntityConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Service
public class PEGrossCashflowServiceImpl implements PEGrossCashflowService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEGrossCashflowRepository repository;

    @Autowired
    private PEGrossCashflowEntityConverter converter;

    @Autowired
    private PEFundService peFundService;

    @Autowired
    private PEIrrService irrService;

    @Autowired
    private PECompanyPerformanceIddService performanceIddService;

    @Override
    public Long save(PEGrossCashflowDto cashflowDto, Long fundId) {
        try {
            PEGrossCashflow entity = this.converter.assemble(cashflowDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PEGrossCashflowResultDto saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId) {
        try {
            if (cashflowDtoList == null || fundId == null) {
                return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (cashflowDto.getCompanyName() != null) {
                    cashflowDto.setCompanyName(cashflowDto.getCompanyName().trim());
                }
                if (cashflowDto.getCompanyName() == null ||
                        cashflowDto.getCompanyName().equals("") ||
                        cashflowDto.getDate() == null) {
                    return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty company name or date!", "");
                }
//                if ((cashflowDto.getInvested() != null && cashflowDto.getInvested() > 0) ||
//                        (cashflowDto.getRealized() != null && cashflowDto.getRealized() < 0) ||
//                        (cashflowDto.getUnrealized() != null && cashflowDto.getUnrealized() < 0)) {
//                    return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Check the positiveness of the values!", "");
//                }
            }

            for (PEGrossCashflowDto cashflowDto1 : cashflowDtoList) {
                int i = 0;
                for (PEGrossCashflowDto cashflowDto2 : cashflowDtoList) {
                    if (cashflowDto1.getCompanyName().equals(cashflowDto2.getCompanyName()) &&
                            cashflowDto1.getDate().equals(cashflowDto2.getDate())) {
                        i++;
                    }
                }
                if (i > 1) {
                    return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "The pairs (\"Company name\", \"Date of transaction\") must be unique!", "");
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            for (PEGrossCashflow cashflow : this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName", "date")))) {
                int i = 0;
                for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                    if (cashflow.getId().equals(cashflowDto.getId())) {
                        i++;
                        break;
                    }
                }
                if (i == 0) {
                    this.repository.delete(cashflow);
                }
            }

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (cashflowDto.getAutoCalculation()) {
                    cashflowDto.setGrossCF(
                            (cashflowDto.getInvested() == null ? 0.0 : cashflowDto.getInvested()) +
                                    (cashflowDto.getRealized() == null ? 0.0 : cashflowDto.getRealized()) +
                                    (cashflowDto.getUnrealized() == null ? 0.0 : cashflowDto.getUnrealized()));
                } else if (cashflowDto.getGrossCF() == null) {
                    cashflowDto.setGrossCF(0.0);
                }
                Long id = save(cashflowDto, fundId);
                if (id == null) {
                    return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's gross cash flow", "");
                } else {
                    cashflowDto.setId(id);
                }
            }

            return new PEGrossCashflowResultDto(cashflowDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's gross cash flow", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's gross cash flow: " + fundId, ex);
            return new PEGrossCashflowResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's gross cash flow", "");
        }
    }

    @Override
    public PEGrossCashflowResultDto uploadGrossCF(Set<FilesDto> filesDtoSet) {
        try {
            List<PEGrossCashflowDto> cashflowDtoList = new ArrayList<>();

            FilesDto filesDto = filesDtoSet.iterator().next();
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);

            int rowNum = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowNum != 0) {
//                    cashflowDtoList.add(new PEGrossCashflowDto("AAA", new Date(), -1000000.0, 2000000.0, 3000000.0, 7000000.0, false));
                    cashflowDtoList.add(new PEGrossCashflowDto(
                            ExcelUtils.getTextValueFromAnyCell(row.getCell(0)),
                            row.getCell(1).getDateCellValue(),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(2)),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(3)),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(4)),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(5)),
                            false));
                }
                rowNum++;
            }

            return new PEGrossCashflowResultDto(cashflowDtoList, StatusResultType.SUCCESS, "", "A new portion of the Gross Cash Flow has been successfully uploaded, but NOT saved!", "");
        } catch (Exception ex) {
            logger.error("Failed to upload PE fund's gross cash flow, ", ex);
        }
        return new PEGrossCashflowResultDto(null, StatusResultType.FAIL, "", "Failed to upload PE fund's Gross Cash Flow!", "");
    }

    private Iterator<Row> getRowIterator(FilesDto filesDto, int sheetNumber) {
        InputStream inputFile = null;
        try {
            inputFile = new ByteArrayInputStream(filesDto.getBytes());
            String extension = filesDto.getFileName().substring(filesDto.getFileName().lastIndexOf(".") + 1,
                    filesDto.getFileName().length());
            if (extension.equalsIgnoreCase("xls")) {
                HSSFWorkbook workbook = new HSSFWorkbook(inputFile);
                HSSFSheet sheet = workbook.getSheetAt(sheetNumber);
                return sheet.iterator();
            }
        } catch (Exception ex) {
            logger.error("Failed to get file iterator, ", ex);
        } finally {
            try {
                inputFile.close();
            } catch (Exception e) {
                logger.error("Failed to close the file, ", e);
            }
        }
        return null;
    }

    @Override
    public List<PEGrossCashflowDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "companyName", "date"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public List<PEGrossCashflowDto> findByFundIdSortedByDate(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "date"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public List<PEGrossCashflowDto> findByFundIdAndCompanyName(Long fundId, String companyName) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundIdAndCompanyName(fundId, companyName, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.ASC, "date"))));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public List<PEGrossCashflowDto> findByFundIdAndPortfolioInfo(Long fundId, PEPortfolioInfoDto portfolioInfoDto) {
        try {
            List<PEGrossCashflowDto> cashflowDtoList = this.findByFundIdSortedByDate(fundId);

            String companyDescription = portfolioInfoDto.getCompanyDescription();
            String industry = portfolioInfoDto.getIndustry();
            String country = portfolioInfoDto.getCountry();
            String typeOfInvestment = portfolioInfoDto.getTypeOfInvestment();
            String control = portfolioInfoDto.getControl();
            String dealSource = portfolioInfoDto.getDealSource();
            String currency = portfolioInfoDto.getCurrency();

            for (PEGrossCashflowDto cashflowDto : cashflowDtoList) {
                if (companyDescription != null && companyDescription.equals("NONE") == false) {
                    PECompanyPerformanceIddDto a = this.performanceIddService.findByFundIdAndCompanyName(fundId, cashflowDto.getCompanyName());
                    cashflowDtoList.remove(cashflowDto);
                }
            }





            return cashflowDtoList;
        } catch (Exception ex) {
            logger.error("Error loading PE fund's gross cash flow: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PEIrrResultDto calculateIRR(PEPortfolioInfoDto portfolioInfoDto, Long fundId) {
        try {
            List<PEGrossCashflowDto> cashflowDtoList = this.findByFundIdAndPortfolioInfo(fundId, portfolioInfoDto);

            if (cashflowDtoList != null) {
                Double irr = this.irrService.getIRR(cashflowDtoList);

                if (irr != null) {
                    return new PEIrrResultDto(irr, StatusResultType.SUCCESS, "", "Successfully calculated PE fund's IRR", "");
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to calculate PE fund's IRR, ", ex);
        }
        return new PEIrrResultDto(null, StatusResultType.FAIL, "", "Error calculating PE fund's IRR", "");
    }

//    @Override
//    public boolean deleteByFundId(Long fundId) {
//        this.repository.deleteByFundId(fundId);
//        return true;
//    }
}