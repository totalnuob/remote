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
            Iterator<Row> rowIterator;
            Row previousRow = null;
            Row currentRow;
            List<NicPortfolio> portfolioList = new ArrayList<>();

            try {
                FilesDto filesDto = filesDtoSet.iterator().next();
                InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheet = workbook.getSheet("Database");
                rowIterator = sheet.iterator();
            } catch (Exception ex) {
                logger.error("Failed to update NIC Portfolio data: the file or the sheet 'Database' cannot be opened, ", ex);
                return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: the file or the sheet 'Database' cannot be opened!", "");
            }

            for (int i = 0; i < 15; i++) {
                if(rowIterator.hasNext()) {
                    previousRow = rowIterator.next();
                } else {
                    logger.error("Failed to update NIC Portfolio data: the sheet 'Database' contains less than 15 rows!");
                    return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: the sheet 'Database' contains less than 15 rows!", "");
                }
            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();

                if (previousRow.getCell(0) == null || previousRow.getCell(0).getDateCellValue() == null ||
                        currentRow.getCell(0) == null || currentRow.getCell(0).getDateCellValue() == null) {
                    //end of data
                    break;
                }

                if (previousRow.getCell(0).getDateCellValue().after(new Date())) {
                    break;
                }

                int previousMonth = DateUtils.getMonth(previousRow.getCell(0).getDateCellValue());
                int currentMonth = DateUtils.getMonth(currentRow.getCell(0).getDateCellValue());

                if (previousMonth != currentMonth) {
                    NicPortfolio a = this.create(previousRow);
                    portfolioList.add(a);
                }

                previousRow = currentRow;
            }

            try {
                this.repository.deleteAll();
                this.repository.save(portfolioList);
            } catch (Exception ex) {
                logger.error("Failed to update NIC Portfolio data: repository problem, ", ex);
                return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data: repository problem!", "");
            }

            List<NicPortfolioDto> nicPortfolioDtoList = this.converter.disassembleList(this.repository.findAllByOrderByDateAsc());
            return new NicPortfolioResultDto(nicPortfolioDtoList, ResponseStatusType.SUCCESS, "", "NIC Portfolio data has been updated successfully!", "");
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data, ", ex);
            return new NicPortfolioResultDto(null, ResponseStatusType.FAIL, "", "Failed to update NIC Portfolio data!", "");
        }
    }

    @Override
    public NicPortfolio create(Row row) {
        try {
            return new NicPortfolio(
                    row.getCell(0).getDateCellValue(),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(3)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(88)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(92)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(93)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(94)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(23)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(26)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(27)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(28)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(44)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(48)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(49)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(50)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(66)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(70)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(71)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(72)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(152)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(157)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(158)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(159)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(218)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(222)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(223)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(224)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(240)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(244)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(245)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(246)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(272)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(275)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(276)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(277)),

                    ExcelUtils.getDoubleValueFromCell(row.getCell(110)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(114)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(115)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(116))
            );
        } catch (Exception ex) {
            logger.error("Failed to update NIC Portfolio data: row read error, ", ex);
            throw ex;
        }
    }
}
