package com.yx.mall.manage;
import lombok.extern.log4j.Log4j;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j
public class MallManageWebApplicationTests {
	@Test
	public void contextLoads() throws Exception{
		String path = MallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
		log.debug("tracker.config_path=" + path);
		ClientGlobal.init(path);
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		StorageClient storageClient = new StorageClient(trackerServer,null);
		String[] uploadInfos = storageClient.upload_file("C:/yxtmp/timg.jpg","jpg",null);
		String url = "http://192.168.1.121";
		for (String uploadInfo : uploadInfos) {
			url += "/" + uploadInfo;
		}
		log.info("imgURL=" + url);
	}
}