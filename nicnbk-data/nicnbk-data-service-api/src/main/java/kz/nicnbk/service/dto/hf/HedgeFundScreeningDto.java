package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFundScreening;
import kz.nicnbk.service.dto.files.FilesDto;

import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningDto extends CreateUpdateBaseEntityDto<HedgeFundScreening> {

    private String name;
    private String description;
    private Date date;

    private Long fileId;
    private String fileName;

    private List<HedgeFundScreeningParsedDataDto> parsedData;

    public HedgeFundScreeningDto(){}

    public HedgeFundScreeningDto(Long id){
        setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<HedgeFundScreeningParsedDataDto> getParsedData() {
        return parsedData;
    }

    public void setParsedData(List<HedgeFundScreeningParsedDataDto> parsedData) {
        this.parsedData = parsedData;
    }
}


