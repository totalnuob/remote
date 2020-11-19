package kz.nicnbk.service.dto.files;

import kz.nicnbk.repo.model.files.Files;

public class NamedFilesDto {
    private FilesDto file;
    private String name;

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
}
