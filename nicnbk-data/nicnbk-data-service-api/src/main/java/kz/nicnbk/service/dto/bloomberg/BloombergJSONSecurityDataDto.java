package kz.nicnbk.service.dto.bloomberg;

import java.util.ArrayList;
import java.util.List;

public class BloombergJSONSecurityDataDto {
    private String security;
    private List<CurrencyFieldJSONDataDto> currencyFieldJSONDataDtoList = new ArrayList<>();

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public List<CurrencyFieldJSONDataDto> getCurrencyFieldJSONDataDtoList() {
        return currencyFieldJSONDataDtoList;
    }

    public void setCurrencyFieldJSONDataDtoList(List<CurrencyFieldJSONDataDto> currencyFieldJSONDataDtoList) {
        this.currencyFieldJSONDataDtoList = currencyFieldJSONDataDtoList;
    }

    public void add(CurrencyFieldJSONDataDto currencyFieldJSONDataDto) {
        this.currencyFieldJSONDataDtoList.add(currencyFieldJSONDataDto);
    }
}
