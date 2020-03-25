package com.yx.mall.manage.util;

import lombok.extern.log4j.Log4j;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;

@Component
@Log4j
public class UploadUtil {

    private static String fdfsTrackerServerIp;

    @Value("${fdfs.tracker.server.ip}")
    public void setFdfsTrackerServerIp(String fdfsTrackerServerIp) {
        this.fdfsTrackerServerIp = fdfsTrackerServerIp;
    }

    public static String uploadFile(MultipartFile multipartFile)throws Exception{

        log.debug("【fdfsTrackerServerIp=】"+ fdfsTrackerServerIp);
        String imgUrl = "http://" + fdfsTrackerServerIp;
        String trackerConfigPath =  UploadUtil.class.getResource("/tracker.conf").getPath();
        log.debug("【trackerConfigPath=】" + trackerConfigPath);
        ClientGlobal.init(trackerConfigPath);
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageClient storageClient = new StorageClient(trackerServer,null);
        String oriFilename = multipartFile.getOriginalFilename();
        String file_ext_name = oriFilename.substring(oriFilename.lastIndexOf(".")+1);
        String[] uploadFiles = storageClient.upload_file(multipartFile.getBytes(),file_ext_name,null);
        for (String uploadFile : uploadFiles) {
            imgUrl +=  "/" + uploadFile;
        }
        log.debug("【imgUrl=】" + imgUrl);
        return imgUrl;
    }
}
