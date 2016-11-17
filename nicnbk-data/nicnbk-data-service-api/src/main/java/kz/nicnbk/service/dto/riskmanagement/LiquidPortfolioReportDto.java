package kz.nicnbk.service.dto.riskmanagement;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 01.11.2016.
 */
public class LiquidPortfolioReportDto implements BaseDto {

    private List<TableColumnDto> columns;
    private List<List<String>>rows;

    public List<TableColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumnDto> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
