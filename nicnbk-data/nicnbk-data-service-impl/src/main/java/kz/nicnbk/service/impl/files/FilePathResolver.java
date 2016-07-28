package kz.nicnbk.service.impl.files;

import kz.nicnbk.common.service.util.HashUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * File path resolver.
 * Determines destination/source folder for files.
 *
 * Created by magzumov on 19.05.2016.
 */
@Service
public class FilePathResolver {

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    /**
     * Returns absolute path for the file directory.
     *
     * @param fileId - file id
     * @param catalog - catalog / dir
     * @return - directory
     */
     public String resolveDirectory(Long fileId, String catalog){
         if(fileId == null){
             throw new IllegalArgumentException("Cannot resolve directory for fileid: " + fileId);
         }
        String hash = HashUtils.hashMD5String(fileId.toString());
        //get 3-level folders from hash, with each folder name = two characters
        StringBuilder folders = new StringBuilder(hash);
        folders.delete(6, folders.length());
        folders.append(File.separator);
        folders.insert(4, File.separator);
        folders.insert(2, File.separator);
        return rootDirectory + File.separator + catalog + File.separator + folders.toString();
    }

    /**
     * Returns absolute path to the file.
     *
     * @param fileId
     * @param catalog
     * @return
     */
    public String resolveAbsoluteFilePath(Long fileId, String catalog){
        StringBuilder absolutePath = new StringBuilder(resolveDirectory(fileId, catalog));
        absolutePath.append(HashUtils.hashMD5String(fileId.toString()));
        return absolutePath.toString();
    }

    // TODO: close for update?!
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }
}
