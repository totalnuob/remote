package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.LiquidPortfolioResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Created by Pak on 13.06.2019.
 */

@RestController
@RequestMapping("/monitoring/liq")
public class MonitoringLiquidPortfolioServiceREST extends CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringLiquidPortfolioServiceREST.class);

    @Autowired
    private LiquidPortfolioService service;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get() {

        LiquidPortfolioResultDto resultDto = this.service.get();

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity upload(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        LiquidPortfolioResultDto resultDto = this.service.upload(filesDtoSet, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void FileDownload(HttpServletResponse response) {

        FilesDto filesDto;
        try {
            filesDto = this.service.getFileWithInputStream();
        } catch (Exception ex) {
            filesDto = null;
        }

        if (filesDto == null || filesDto.getInputStream() == null) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType(filesDto.getMimeType());

        try {
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(Monitoring, Liquid Portfolio) File export request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(Monitoring, Liquid Portfolio) File export request failed: io exception", e);
        } catch (Exception e){
            logger.error("(Monitoring, Liquid Portfolio) File export request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            if (filesDto.getMimeType().equals("application/zip")) {
                new File(filesDto.getFileName()).delete();
            }
        } catch (IOException e) {
            logger.error("(Monitoring, Liquid Portfolio) File export: failed to close input stream", e);
        }
    }
}
