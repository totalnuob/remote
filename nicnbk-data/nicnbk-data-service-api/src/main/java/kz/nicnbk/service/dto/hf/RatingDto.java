package kz.nicnbk.service.dto.hf;

/**
 * Created by timur on 27.10.2016.
 */
public class RatingDto {

    private String name;
    private String value;
    private String valueDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }
}
