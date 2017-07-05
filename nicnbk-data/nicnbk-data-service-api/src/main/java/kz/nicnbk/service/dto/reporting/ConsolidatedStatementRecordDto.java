package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 15.05.2017.
 */
@Deprecated
public class ConsolidatedStatementRecordDto implements BaseDto {

    private String name;
    private String category;
    private ConsolidatedStatementType type;
    private Double[] values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ConsolidatedStatementType getType() {
        return type;
    }

    public void setType(ConsolidatedStatementType type) {
        this.type = type;
    }

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

    public boolean isBalance(){
        return (this.type != null) && (this.type == ConsolidatedStatementType.ASSETS || this.type == ConsolidatedStatementType.LIABILITIES ||
                this.type == ConsolidatedStatementType.BALANCE_OTHER);
    }

    public boolean isOperations(){
        return (this.type != null) && (this.type == ConsolidatedStatementType.INCOME || this.type == ConsolidatedStatementType.EXPENSES ||
                this.type == ConsolidatedStatementType.OPERATIONS_OTHER);
    }
}
