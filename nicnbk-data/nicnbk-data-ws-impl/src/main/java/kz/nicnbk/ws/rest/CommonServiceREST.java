package kz.nicnbk.ws.rest;

import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import kz.nicnbk.ws.model.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 24.03.2017.
 */
public abstract class CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceREST.class);

    public ResponseEntity buildResponse(Object response){
        if(response != null){
            return new ResponseEntity<>(response, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Response> buildUnauthorizedResponse(){
        Response response = new Response();
        response.setSuccess(false);
        ResponseMessage message = new ResponseMessage();
        message.setNameEn("Accees denied");
        response.setMessage(message);
        return new ResponseEntity<>(response, null, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<EntitySaveResponse> buildEntitySaveResponse(Long id, Date creationDate){
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if (creationDate == null) {
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(creationDate);
        }
        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }

    public FilesDto buildFilesDtoFromMultipart(MultipartFile file, String fileType){
        MultipartFile[] files = new MultipartFile[1];
        files[0] = file;
        return (FilesDto) buildFilesDtoFromMultipart(files, fileType).toArray()[0];
    }

    public Set<FilesDto> buildFilesDtoFromMultipart(MultipartFile[] files, String fileType){
        Set<FilesDto> filesDtoSet = new HashSet<>();
        if(files != null && files.length > 0) {
            for (MultipartFile file : files) {
                FilesDto filesDto = new FilesDto();
                filesDto.setType(fileType);
                filesDto.setFileName(file.getOriginalFilename());
                filesDto.setMimeType(file.getContentType());
                filesDto.setSize(file.getSize());
                try {
                    filesDto.setBytes(file.getBytes());
                } catch (IOException ex) {
                    logger.error("Files upload failed: io exception", ex);
                }
                filesDtoSet.add(filesDto);
            }
            return filesDtoSet;
        }else{
            return null;
        }
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

    public ResponseEntity<?> buildDeleteResponseEntity(boolean deleted){
        if(deleted) {
            Response response = new Response();
            response.setSuccess(deleted);
            return new ResponseEntity<>(response, null, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
