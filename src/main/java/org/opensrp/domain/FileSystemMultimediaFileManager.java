package org.opensrp.domain;

import org.opensrp.domain.contract.MultimediaFileManager;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

import static org.opensrp.service.MultimediaService.*;

/**
 * Created by Vincent Karuri on 24/10/2019
 */
public class FileSystemMultimediaFileManager implements MultimediaFileManager {

    private final MultimediaRepository multimediaRepository;

    private final ClientService clientService;

    private String multimediaDirPath;

    private static Logger logger = LoggerFactory.getLogger(FileSystemMultimediaFileManager.class.getName());

    private final String SUCCESS = "success";

    private final String FAIL = "fail";

    public FileSystemMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        this.multimediaRepository = multimediaRepository;
        this.clientService = clientService;
    }

    @Override
    public boolean saveFile(MultimediaDTO multimedia, MultipartFile file) {
        String status = saveMultimediaFile(multimedia, file);
        return SUCCESS.equals(status) ? true : false;
    }

    @Override
    public File retrieveFile(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file : null;
    }

    public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {

        if (uploadFile(multimediaDTO, file)) {
            try {
                logger.info("Image path : " + multimediaDirPath);

                Multimedia multimediaFile = new Multimedia().withCaseId(multimediaDTO.getCaseId())
                        .withProviderId(multimediaDTO.getProviderId()).withContentType(multimediaDTO.getContentType())
                        .withFilePath(multimediaDTO.getFilePath()).withFileCategory(multimediaDTO.getFileCategory());

                multimediaRepository.add(multimediaFile);
                Client client = clientService.getByBaseEntityId(multimediaDTO.getCaseId());
                if (client != null) {
                    if (client.getAttribute("Patient Image") != null) {
                        client.removeAttribute("Patient Image");
                    }
                    client.addAttribute("Patient Image", multimediaDTO.getCaseId() + ".jpg");
                    client.setServerVersion(null);
                    clientService.updateClient(client);
                }
                return SUCCESS;
            }
            catch (Exception e) {
                e.getMessage();
            }
        }

        return FAIL;
    }

    /**
     * Saves a multi-part file uploaded to the server
     *
     * @param multimediaDTO {@link MultimediaDTO} object populated with information about the file to be saved
     * @param multimediaFile {@link MultipartFile} file to save to disk
     *
     * @return true if the file was saved else false
     */
    public boolean uploadFile(MultimediaDTO multimediaDTO, MultipartFile multimediaFile) {

        // String baseMultimediaDirPath = System.getProperty("user.home");

        if (!multimediaFile.isEmpty()) {
            try {
                multimediaDirPath = baseMultimediaDirPath + File.separator;
                String fileExt = ".jpg";
                switch (multimediaDTO.getContentType()) {

                    case "application/octet-stream":
                        multimediaDirPath += VIDEOS_DIR;
                        fileExt = ".mp4";
                        break;
                    case "image/jpeg":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".jpg";
                        break;
                    case "image/gif":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".gif";
                        break;
                    case "image/png":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".png";
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown content type : " + multimediaDTO.getContentType());
                }

                String fileName;
                if (MULTI_VERSION.equals(multimediaDTO.getFileCategory())) {
                    // allow saving multiple multimedia associated with one client
                    String dirPath = multimediaDirPath + File.separator + multimediaDTO.getCaseId();
                    new File(dirPath).mkdirs();
                    fileName = dirPath + File.separator + UUID.randomUUID() + fileExt;
                } else {
                    // overwrite previously saved image
                    new File(multimediaDirPath).mkdirs();
                    fileName = multimediaDirPath + File.separator + multimediaDTO.getCaseId() + fileExt;
                }
                multimediaDTO.withFilePath(fileName);
                File multimediaFilePath = new File(fileName);
                multimediaFile.transferTo(multimediaFilePath);

                return true;
            }
            catch (Exception e) {
                logger.error("", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private void makeMultimediaDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists())
            file.mkdirs();

    }
}
