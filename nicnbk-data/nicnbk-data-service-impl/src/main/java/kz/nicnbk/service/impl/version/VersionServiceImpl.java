package kz.nicnbk.service.impl.version;

import kz.nicnbk.service.api.version.VersionService;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.version.VersionResultDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            for(String filePath : filePathList) {
                Resource resource = new ClassPathResource(filePath);

                InputStream wordFileToRead;
                try {
                    wordFileToRead = resource.getInputStream();
                } catch (IOException e) {
                    logger.error("Failed to load versions: error opening files, ", e);
                    return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions: error opening files!", "", null);
                }

                try {
                    XWPFDocument document = new XWPFDocument(wordFileToRead);
                    List<XWPFParagraph> paragraphs = document.getParagraphs();
                    if(paragraphs != null) {
                        for(XWPFParagraph paragraph: paragraphs) {
                            List<XWPFRun> runs = paragraph.getRuns();
                            if(runs != null) {
                                for (XWPFRun r : runs) {
                                    continue;
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Failed to load versions: error parsing files, ", ex);
                    return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions: error parsing files!", "", null);
                }
            }

            return new VersionResultDto(ResponseStatusType.SUCCESS, "", "Successfully loaded version files!", "", null);

        } catch (Exception ex) {
            logger.error("Failed to load versions, ", ex);
            return new VersionResultDto(ResponseStatusType.FAIL, "", "Failed to load versions!", "", null);
        }
    }
}
