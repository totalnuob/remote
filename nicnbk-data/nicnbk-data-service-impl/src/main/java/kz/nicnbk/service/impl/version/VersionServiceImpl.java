package kz.nicnbk.service.impl.version;

import kz.nicnbk.service.api.version.VersionService;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.version.VersionDto;
import kz.nicnbk.service.dto.version.VersionResultDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pak on 08.11.2019.
 */

@Service
public class VersionServiceImpl implements VersionService {

    private static final Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);

    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Override
    public VersionResultDto get() {
        try {
            List<String> filePathList;

            Path path = Paths.get(rootDirectory + "/versions");
            try (Stream<Path> stream = Files.walk(path, Integer.MAX_VALUE)) {
                filePathList = stream
                        .map(String::valueOf)
                        .filter(f -> f.endsWith(".docx"))
                        .collect(Collectors.toList());
            }

            if(filePathList == null || filePathList.isEmpty()) {
                logger.error("Failed to load versions: error finding version files!");
                return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions: error finding version files!", "", null);
            }

            List<VersionDto> versionDtoList = new ArrayList<>();

            for(String filePath : filePathList) {
                try {
                    File file = new File(filePath);
                    FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

                    XWPFDocument document = new XWPFDocument(fileInputStream);
                    List<XWPFParagraph> paragraphs = document.getParagraphs();
                    if(paragraphs != null && paragraphs.size() >= 2) {

                        System.out.println(paragraphs.get(0).getText());
                        System.out.println(paragraphs.get(1).getText());

                        versionDtoList.add(new VersionDto(paragraphs.get(0).getText(), paragraphs.get(1).getText()));
                    }
                } catch (Exception ex) {
                    logger.error("Failed to load versions: error parsing files, ", ex);
                    return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions: error parsing files!", "", null);
                }
            }

            return new VersionResultDto(ResponseStatusType.SUCCESS, "", "Successfully loaded version files!", "", versionDtoList);

        } catch (Exception ex) {
            logger.error("Failed to load versions, ", ex);
            return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions!", "", null);
        }
    }
}
