package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.repo.api.monitoring.NicPortfolioRepository;
import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.converter.monitoring.NicPortfolioEntityConverter;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioResultDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Pak on 13.06.2019.
 */

@Service
public class NicPortfolioServiceImpl implements NicPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(NicPortfolioServiceImpl.class);

    @Autowired
    private NicPortfolioRepository repository;

    @Autowired
    private NicPortfolioEntityConverter converter;

    @Override
    public NicPortfolioResultDto get() {
        try {
            List<NicPortfolioDto> nicPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been loaded successfully!", "");
        } catch (Exception ex) {
            logger.error("Error loading NIC Portfolio data, ", ex);
        }
        return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to load NIC Portfolio data!", "");
    }

    @Override
    public NicPortfolioResultDto upload(Set<FilesDto> filesDtoSet) {
        try {
            FilesDto filesDto = filesDtoSet.iterator().next();
            InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
            XSSFSheet sheet = workbook.getSheet("Database");
            Iterator<Row> rowIterator = sheet.iterator();

            Row previousRow = null;
            Row currentRow;
            Date previousDate;
            Date currentDate;
            int previousMonth;
            int currentMonth;

            int rowNumber = 0;

            for (int i = 0; i < 15; i++) {
                previousRow = rowIterator.next();
                rowNumber++;
            }

            this.repository.deleteAll();

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                try {
                    if(previousRow.getCell(0) == null || previousRow.getCell(0).getDateCellValue() == null) {
                        //end of data
                        break;
                    } else {
                        previousDate = previousRow.getCell(0).getDateCellValue();
                    }

                    if(currentRow.getCell(0) == null || currentRow.getCell(0).getDateCellValue() == null) {
                        //end of data
                        break;
                    } else {
                        currentDate = currentRow.getCell(0).getDateCellValue();
                    }

                    previousMonth = DateUtils.getMonth(previousDate);
                    currentMonth = DateUtils.getMonth(currentDate);
                } catch (Exception ex) {
                    logger.error("Error getting date from cell, row number: " + rowNumber, ex);
                    break;
                }

                if (previousMonth != currentMonth) {
                    this.repository.save(new NicPortfolio(
                            previousRow.getCell(0).getDateCellValue(),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(3)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(88)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(92)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(93)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(94)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(23)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(26)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(27)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(28)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(44)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(48)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(49)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(50)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(66)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(70)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(71)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(72)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(152)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(157)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(158)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(159)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(218)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(222)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(223)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(224)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(240)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(244)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(245)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(246)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(272)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(275)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(276)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(277)),

                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(110)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(114)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(115)),
                            ExcelUtils.getDoubleValueFromCell(previousRow.getCell(116))
                            ));
                }

                previousRow = currentRow;
            }

            List<NicPortfolioDto> nicPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());

            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been updated successfully!", "");
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data, ", ex);
        }
        return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data!", "");
    }
}
