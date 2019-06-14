package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
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
            List<NicPortfolioDto> nicPortfolioDtoList = new ArrayList<>();
            for (NicPortfolio entity : this.repository.findAll()) {
                nicPortfolioDtoList.add(this.converter.disassemble(entity));
            }
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

            int rowNumber = 0;

            for (int i = 0; i < 15; i++) {
                previousRow = rowIterator.next();
                rowNumber++;
            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                try {
                    previousDate = previousRow.getCell(0).getDateCellValue();
                } catch (Exception ex) {
                    previousDate = null;
                }
                int monthPrevious = DateUtils.getMonth(previousDate);
                int monthCurrent = DateUtils.getMonth(currentRow.getCell(0).getDateCellValue());


//                Double a = ExcelUtils.getDoubleValueFromCell(row.getCell(0));
//                break;






                previousRow = currentRow;
            }





            List<NicPortfolioDto> nicPortfolioDtoList = new ArrayList<>();
            for (NicPortfolio entity : this.repository.findAll()) {
                nicPortfolioDtoList.add(this.converter.disassemble(entity));
            }
            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been updated successfully!", "");
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data, ", ex);
        }
        return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data!", "");
    }
}
