package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by magzumov on 15.09.2017.
 */
@RestController
@RequestMapping("/files")
public class GenereicFileServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(GenereicFileServiceREST.class);

    @Autowired
    private FileService fileService;

    @RequestMapping(value="/download/{fileType}/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@PathVariable(value="id") Long fileId,
                             @PathVariable(value = "fileType") String fileType,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        InputStream inputStream = fileService.getFileInputStream(fileId, fileType);
        if(inputStream == null){
            // TODO: handle error
        }
        sendFileDownloadResponse(response, fileService.getFileInfo(fileId), inputStream);
    }

    public void sendFileDownloadResponse(HttpServletResponse response, FilesDto fileDto, InputStream inputStream){
        response.setContentType(fileDto.getMimeType());
        String fileName = null;
        try {
            fileName = URLEncoder.encode(fileDto.getFileName(), "UTF-8");
            fileName = URLDecoder.decode(fileName, "ISO8859_1");
            response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("File download request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("File download request failed: io exception", e);
        } catch (Exception e){
            logger.error("File download request failed", e);
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            logger.error("File download: failed to close input stream");
        }
    }
}
