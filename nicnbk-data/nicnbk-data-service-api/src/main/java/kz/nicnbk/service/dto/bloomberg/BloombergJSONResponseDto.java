package kz.nicnbk.service.dto.bloomberg;

import java.util.ArrayList;
import java.util.List;

public class BloombergJSONResponseDto {
    private List<BloombergJSONSecurityDataDto> bloombergJSONSecurityDataDtoList = new ArrayList<>();

    public List<BloombergJSONSecurityDataDto> getBloombergJSONSecurityDataDtoList() {
        return bloombergJSONSecurityDataDtoList;
    }

    public void setBloombergJSONSecurityDataDtoList(List<BloombergJSONSecurityDataDto> bloombergJSONSecurityDataDtoList) {
        this.bloombergJSONSecurityDataDtoList = bloombergJSONSecurityDataDtoList;
    }

    public void add(BloombergJSONSecurityDataDto bloombergJSONSecurityDataDto) {
        this.bloombergJSONSecurityDataDtoList.add(bloombergJSONSecurityDataDto);
    }

}
