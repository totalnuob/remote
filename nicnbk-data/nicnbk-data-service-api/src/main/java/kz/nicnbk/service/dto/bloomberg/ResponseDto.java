package kz.nicnbk.service.dto.bloomberg;

import java.util.ArrayList;
import java.util.List;

public class ResponseDto {
    private List<SecurityDataDto> securityDataDtoList = new ArrayList<>();

    public List<SecurityDataDto> getSecurityDataDtoList() {
        return securityDataDtoList;
    }

    public void setSecurityDataDtoList(List<SecurityDataDto> securityDataDtoList) {
        this.securityDataDtoList = securityDataDtoList;
    }

    public void add(SecurityDataDto securityDataDto) {
        this.securityDataDtoList.add(securityDataDto);
    }

}
