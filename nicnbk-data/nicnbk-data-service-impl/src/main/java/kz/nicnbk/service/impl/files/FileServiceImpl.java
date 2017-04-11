package kz.nicnbk.service.impl.files;

import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.files.FilesRepository;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by magzumov on 18.07.2016.
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FilesRepository filesRepository;

    @Autowired
    private FilePathResolver filePathResolver;

    @Autowired
    private FilesEntityConverter filesConverter;

    @Override
    public Long save(FilesDto fileDto, String catalog) {
        // save file meta data
        Long fileId = filesRepository.save(filesConverter.assemble(fileDto)).getId();

        // save file contents

        // TODO: get catalog from file type !

        String path = filePathResolver.resolveDirectory(fileId, catalog);
        File file = new File(path);
        file.mkdirs();
        try {
            String fileName = HashUtils.hashMD5String(fileId.toString());
            writeFile(fileDto.getBytes(), path, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return fileId;
    }

    @Override
    public FilesDto getFileInfo(Long fileId) {
        Files file = filesRepository.findOne(fileId);
        if(file != null){
            return filesConverter.disassemble(file);
        }
        return null;
    }

    private String getFileCatalog(Long fileId) {
        Files file = filesRepository.findOne(fileId);
        if(file != null && file.getType() != null){
            return file.getType().getCatalog();
        }
        return null;
    }

    @Override
    public InputStream getFileInputStream(Long fileId, String fileType) throws IllegalArgumentException{
        try {
            String absolutePath = filePathResolver.resolveAbsoluteFilePath(fileId, getCatalogByFileType(fileType));
            File file = new File(absolutePath);
            InputStream inputStream = null;
            inputStream = new FileInputStream(file.getAbsolutePath());
            return inputStream;
        } catch (FileNotFoundException e) {
            logger.error("Failed to get file input stream: file=" + fileId + ", file type=" + fileType, e);
        }
        return null;
    }

    private String getCatalogByFileType(String fileType)  throws IllegalArgumentException{
        // TODO: refactor
        if(fileType == null ){
            throw new IllegalArgumentException("Illegal file tyoe: null");
        }
        if(fileType.equals(FileTypeLookup.MEMO_ATTACHMENT.getCode())){
            return FileTypeLookup.MEMO_ATTACHMENT.getCatalog();
        }
        throw new IllegalArgumentException("Illegal file tyoe: " + fileType);
    }

    @Override
    public boolean delete(Long fileId) {

        // TODO: handle possible errors

        //String type = getFileInfo(fileId).getType();
        String catalog = getFileCatalog(fileId);

        if(StringUtils.isEmpty(catalog)){
            return false;
        }

        // delete file info
        filesRepository.delete(fileId);

        // delete file bytes
        String path = filePathResolver.resolveAbsoluteFilePath(fileId, catalog  );
        File file = new File(path);
        boolean deleted = file.delete();

        return deleted;
    }

    @Override
    public boolean safeDelete(Long id) {
        int rows = filesRepository.setDeleted(id);
            // logging done in upper service layer
        return rows == 1;
    }


    private void writeFile(byte[] bytes, String path, String fileName) throws IllegalArgumentException, IOException {
        checkFolder(path);
        OutputStream out = new FileOutputStream(new File(path + fileName));
        out.write(bytes);
        out.flush();
        out.close();
    }

    private void checkFolder(String filePath) throws IllegalArgumentException {
        File path = new File(filePath);
        if (!path.exists()) {
            throw new IllegalArgumentException("the path '"
                    + path.getPath() + "' does not exist in file system. Contact admin");
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException("the path '"
                    + path.getPath() + "' is not a directory in file system. Contact admin");
        } else if (!path.canWrite()) {
            throw new IllegalArgumentException("the path '"
                    + path.getPath() + "' has not write access in file system. Contact admin");
        }
    }
}
