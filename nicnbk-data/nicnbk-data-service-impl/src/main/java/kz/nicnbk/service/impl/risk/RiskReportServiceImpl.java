package kz.nicnbk.service.impl.risk;

import kz.nicnbk.service.api.risk.RiskReportService;
import kz.nicnbk.service.dto.riskmanagement.LiquidPortfolioReportDto;
import kz.nicnbk.service.dto.riskmanagement.TableColumnDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 01.11.2016.
 */
@Service
public class RiskReportServiceImpl implements RiskReportService {

    @Override
    public LiquidPortfolioReportDto getLiquidPortfolioReport() {
        LiquidPortfolioReportDto reportDto = getReportTemp();
        return reportDto;
    }

    private LiquidPortfolioReportDto getReportTemp(){
        LiquidPortfolioReportDto reportDto = new LiquidPortfolioReportDto();
        reportDto.setColumns(getColumns());
        reportDto.setRows(getRows());
        return reportDto;
    }

    private List<TableColumnDto> getColumns(){
        List<TableColumnDto> columns = new ArrayList<>();
        columns.add(new TableColumnDto("string", "Client id"));
        columns.add(new TableColumnDto("string", "Portfolio"));
        columns.add(new TableColumnDto("string", "Pv(%)"));
        columns.add(new TableColumnDto("string", "Present Value"));
        columns.add(new TableColumnDto("string", "Standard Deviation Historical"));
        return columns;
    }

    private List<List<String>> getRows(){
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<String>();
        row.add("QWERTYUIOP");
        row.add("Liquidity");
        row.add("10");
        row.add("20");
        row.add("30");
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        rows.add(row);
        return rows;
    }
}
