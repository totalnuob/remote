package kz.nicnbk.ws.rest;

import kz.nicnbk.service.dto.common.*;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *  Common REST Service class.
 *  Child REST Service classes inherit common functions such as building response entity, etc.
 *
 * Created by magzumov on 24.03.2017.
 */
public abstract class CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceREST.class);

    public ResponseEntity buildOKResponse(){
        ResponseDto response = new ResponseDto();
        response.setStatus(ResponseStatusType.SUCCESS);
        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }

    public ResponseEntity buildOKWithErrorResponse(String message){
        ResponseDto response = new ResponseDto();
        response.setErrorMessageEn(message);
        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }


    /**
     * Returns ResponseEntity with object as response body.
     * Returns ResponseEntity with status status OK 200 if response not null, status 500 Error otherwise.
     *
     * @param response - response body
     * @return - ResponseEntity object
     */
    public ResponseEntity buildNonNullResponse(Object response){
        if(response != null){
            return new ResponseEntity<>(response, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns ResponseEntity with status from response dto parameter.
     * If status is null, then returns error response.
     * If status is not null, then return status based on response dto status field.
     *
     * @param responseDto - response dto
     * @return - ResponseEntity object
     */
    public ResponseEntity buildNonNullResponseWithStatus(ResponseDto responseDto){
        if(responseDto == null || responseDto.getStatus() == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            HttpStatus httpStatus = responseDto.getStatus() == ResponseStatusType.SUCCESS ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(responseDto, null, httpStatus);
        }
    }

    /**
     * Returns ResponseEntity with status 401 Unauthorized.
     * Sets corresponding response body.
     * @return - ResponseEntity object
     */
    public ResponseEntity<ResponseDto> buildUnauthorizedResponse(){
        ResponseDto response = new ResponseDto();
        response.setStatus(ResponseStatusType.FAIL);
        ResponseMessageDto message = new ResponseMessageDto();
        message.setNameEn("Access denied");
        response.setMessage(message);
        return new ResponseEntity<>(response, null, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Returns ResponseEntity for save request with status OK.
     * Sets corresponding response body.
     *
     * @param id - entity id
     * @param creationDate - entity creation date
     * @return - ResponseEntity object
     */
    @Deprecated
    public ResponseEntity<EntitySaveResponseDto> buildEntitySaveResponse(Long id, Date creationDate){
        EntitySaveResponseDto response = new EntitySaveResponseDto();
        response.setEntityId(id);
        if (creationDate == null) {
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(creationDate);
        }
        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }


    public ResponseEntity<EntitySaveResponseDto> buildEntitySaveResponse(EntitySaveResponseDto entitySaveResponseDto){
        if(entitySaveResponseDto == null || entitySaveResponseDto.getStatus() == null){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpStatus httpStatus = entitySaveResponseDto.getStatus() == ResponseStatusType.SUCCESS ? HttpStatus.OK :
                HttpStatus.INTERNAL_SERVER_ERROR;
//            // TODO: fix create date
//            if (entitySaveResponseDto.getCreationDate() == null) {
//                entitySaveResponseDto.setCreationDate(new Date());
//            }
        return new ResponseEntity<>(entitySaveResponseDto, null, httpStatus);
    }

    public ResponseEntity<EntityListSaveResponseDto> buildEntityListSaveResponse(EntityListSaveResponseDto entityListSaveResponseDto){
        if(entityListSaveResponseDto == null || entityListSaveResponseDto.getStatus() == null){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpStatus httpStatus = entityListSaveResponseDto.getStatus() == ResponseStatusType.SUCCESS ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(entityListSaveResponseDto, null, httpStatus);
    }



    /**
     * Returns FilesDto built from MultipartFile and file type.
     *
     * @param file - file
     * @param fileType - file type
     * @return - FilesDto object
     */
    public FilesDto buildFilesDtoFromMultipart(MultipartFile file, String fileType){
        MultipartFile[] files = new MultipartFile[1];
        files[0] = file;
        return (FilesDto) buildFilesDtoFromMultipart(files, fileType).toArray()[0];
    }

    /**
     * Returns set of FilesDto's built from MultipartFile array and file type.
     *
     * @param files - files
     * @param fileType - files type
     * @return - FilesDto set
     */
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

    /**
     * Returns ResponseEntity for delete request.
     * Returns status OK 200 if deleted is true, status 500 Error otherwise.
     *
     * @param deleted - true/false
     * @return - ResponseEntity object
     */
    public ResponseEntity<?> buildDeleteResponseEntity(boolean deleted){
        if(deleted) {
            ResponseDto response = new ResponseDto();
            response.setStatus(ResponseStatusType.SUCCESS);
            return new ResponseEntity<>(response, null, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
