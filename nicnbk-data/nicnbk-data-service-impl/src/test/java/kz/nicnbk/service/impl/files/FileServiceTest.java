package kz.nicnbk.service.impl.files;

import kz.nicnbk.service.CommonTest;
import kz.nicnbk.service.api.files.FileService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by magzumov on 19.07.2016.
 */
public class FileServiceTest extends CommonTest {

    @Autowired
    private FileService fileService;

    @Test
    public void testContext(){
        assert (fileService != null);
    }
}
