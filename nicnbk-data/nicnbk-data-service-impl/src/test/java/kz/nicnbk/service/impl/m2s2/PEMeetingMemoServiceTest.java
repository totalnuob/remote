package kz.nicnbk.service.impl.m2s2;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.api.m2s2.MemoFilesRepository;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.MeetingTypeLookup;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.service.CommonTest;
import kz.nicnbk.service.api.m2s2.PEMeetingMemoService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import kz.nicnbk.service.impl.files.FilePathResolver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by magzumov on 20.07.2016.
 */

public class PEMeetingMemoServiceTest extends CommonTest {

    @Autowired
    private PEMeetingMemoService PEMemoService;

    @Autowired
    private MemoFilesRepository memoFilesRepository;

    @Autowired
    private FilePathResolver filePathResolver;

    @Autowired
    private FilesRepository filesRepository;

    @Test
    @DatabaseSetup({"classpath:datasets/m2s2/pe_memo_save.xml"})
    @DatabaseTearDown({"classpath:datasets/m2s2/memo_files_clear.xml"})
    public void testSave() throws IOException {
        Long memoId = PEMemoService.save(getTestDto());
        assert (memoId > 0);

        //check files
        List<MemoFiles> memoFilesList = memoFilesRepository.getFilesByMemoId(memoId);
        assert (memoFilesList.size() == 1);

        String filePath = filePathResolver.resolveAbsoluteFilePath(memoFilesList.get(0).getFile().getId(), FileTypeLookup.MEMO_ATTACHMENT.getCatalog());
        Scanner in = new Scanner(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        while(in.hasNext()){
            stringBuilder.append(in.next());
        }
        in.close();
        assert (stringBuilder.toString().equals("This_is_a_test_file."));

        // delete memo folder
        FileUtils.deleteDirectory(new File(filePathResolver.getRootDirectory() + "/memo"));

    }

    @Test
    @DatabaseSetup({"classpath:datasets/m2s2/pe_memo_get.xml"})
    @DatabaseTearDown({"classpath:datasets/m2s2/memo_files_clear.xml"})
    public void testGet(){
        PrivateEquityMeetingMemoDto memoDto = PEMemoService.get(220L);
        assert (memoDto != null);
        assert (memoDto.getFiles().size() == 1);
    }

    private PrivateEquityMeetingMemoDto getTestDto() throws IOException {
        PrivateEquityMeetingMemoDto dto = new PrivateEquityMeetingMemoDto();
        dto.setMeetingType(MeetingTypeLookup.MEETING.getCode());
        dto.setMeetingDate(new Date());
        dto.setSuitable(true);
        dto.setConviction((short)3);

        // files
        FilesDto file = new FilesDto();
        file.setType(FileTypeLookup.MEMO_ATTACHMENT.getCode());
        file.setFileName("testfile");
        file.setMimeType("txt");
        file.setSize(100L);
        file.setBytes(getTestFileBytes());
        Set<FilesDto> files = new HashSet<>();
        files.add(file);

        dto.setFiles(files);
        return dto;
    }

    private byte[] getTestFileBytes() throws IOException {
        InputStream input = this.getClass().getResourceAsStream("testfile.txt");
        assert (input != null);
        byte[] data = IOUtils.toByteArray(input);
        assert (data != null);
        return data;
    }
}
