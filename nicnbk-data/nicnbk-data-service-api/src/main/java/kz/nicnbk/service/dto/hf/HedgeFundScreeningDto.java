package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFundScreening;

import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningDto extends CreateUpdateBaseEntityDto<HedgeFundScreening> {

    private String name;
    private String shortName;
    private String description;
    private Date date;
    private String startDate;

    private Long fileId;
    private String fileName;

    private Long ucitsFileId;
    private String ucitsFileName;

    private Long paramsFileId;
    private String paramsFileName;

    private List<HedgeFundScreeningParsedDataDto> parsedData;
    private List<HedgeFundScreeningParsedDataDto> parsedUcitsData;
    private List<HedgeFundScreeningFundParamDataDto> parsedFundParamData;

    private boolean editable;

    private int existingFilteredResults;

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

    public Long getUcitsFileId() {
        return ucitsFileId;
    }

    public void setUcitsFileId(Long ucitsFileId) {
        this.ucitsFileId = ucitsFileId;
    }

    public String getUcitsFileName() {
        return ucitsFileName;
    }

    public void setUcitsFileName(String ucitsFileName) {
        this.ucitsFileName = ucitsFileName;
    }

    public List<HedgeFundScreeningParsedDataDto> getParsedUcitsData() {
        return parsedUcitsData;
    }

    public void setParsedUcitsData(List<HedgeFundScreeningParsedDataDto> parsedUcitsData) {
        this.parsedUcitsData = parsedUcitsData;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getExistingFilteredResults() {
        return existingFilteredResults;
    }

    public void setExistingFilteredResults(int existingFilteredResults) {
        this.existingFilteredResults = existingFilteredResults;
    }

    public List<HedgeFundScreeningFundParamDataDto> getParsedFundParamData() {
        return parsedFundParamData;
    }

    public void setParsedFundParamData(List<HedgeFundScreeningFundParamDataDto> parsedFundParamData) {
        this.parsedFundParamData = parsedFundParamData;
    }

    public Long getParamsFileId() {
        return paramsFileId;
    }

    public void setParamsFileId(Long paramsFileId) {
        this.paramsFileId = paramsFileId;
    }

    public String getParamsFileName() {
        return paramsFileName;
    }

    public void setParamsFileName(String paramsFileName) {
        this.paramsFileName = paramsFileName;
    }
}


