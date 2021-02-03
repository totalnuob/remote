package kz.nicnbk.service.dto.files;

public class NamedFilesDto implements Comparable {
    private FilesDto file;
    private String name;
    private Integer topicOrder;

    public NamedFilesDto(){}

    public NamedFilesDto(FilesDto file, String name){
        this.file = file;
        this.name = name;
    }

    public FilesDto getFile() {
        return file;
    }

    public void setFile(FilesDto file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTopicOrder() {
        return topicOrder;
    }

    public void setTopicOrder(Integer topicOrder) {
        this.topicOrder = topicOrder;
    }

    @Override
    public int compareTo(Object o) {
        if(this.topicOrder != null){
            if(((NamedFilesDto)o).topicOrder != null){
                return this.topicOrder - ((NamedFilesDto)o).topicOrder;
            }else{
                return 1;
            }
        }else if(this.getFile().getId() != null){
            if(((NamedFilesDto)o).getFile().getId() != null){
                return this.getFile().getId().intValue() - ((NamedFilesDto)o).getFile().getId().intValue();
            }else{
                return 1;
            }
        }else{
            return 0;
        }
    }
}
