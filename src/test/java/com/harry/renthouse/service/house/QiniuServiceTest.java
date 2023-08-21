package com.harry.renthouse.service.house;

import com.google.gson.Gson;
import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.entity.HousePicture;
import com.harry.renthouse.repository.HousePictureRepository;
import com.harry.renthouse.web.dto.QiniuUploadResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/9 17:55
 */
class QiniuServiceTest extends RentHouseApplicationTests {

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Resource
    private Gson gson;

    @Value("${qiniu.cdnPrefix}")
    private String cndPrefix;

    @Test
    void uploadFile() {
        File file = new File("G:\\github\\rent-house\\src\\main\\resources\\static\\verify-image-1.jpg");
        Assert.isTrue(file.exists(), "文件不存在");
        String key = "FuEhA_YXF38R77bhtM00jlGt72V7";

        try {
            Response response = qiniuService.uploadFile(file,key);
            QiniuUploadResult qiniuUploadResult = gson.fromJson(response.bodyString(), QiniuUploadResult.class);
            System.out.println(qiniuUploadResult.getKey());
            Assert.isTrue(response.isOK(), "文件上传失败");
        } catch (QiniuException e) {
            e.printStackTrace();
            Assert.isTrue(false, "文件上传失败");
        }
    }

    @Test
    void uploadFileWithMysql() {
        String directoryPath = "G:\\github\\rent-house\\src\\main\\resources\\yourpicture\\";
        File directory = new File(directoryPath);
        ClassLoader classLoader = getClass().getClassLoader();

        if (directory.exists() && directory.isDirectory()) {
            System.out.println("指定的路径是一个有效的目录。");
        } else {
            System.out.println("指定的路径不是有效的目录。");
            return;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // 处理文件
                    System.out.println("文件：" + file.getAbsolutePath());
               }

            }
        }


        List<HousePicture> alllist = housePictureRepository.findAll();
        List<HousePicture> distinctLists = alllist.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new ArrayList<>(alllist.size())),
                        list -> list.stream().collect(Collectors.toMap(HousePicture::getPath, housePicture -> housePicture, (existing, replacement) -> existing))
                                .values()
                                .stream()
                                .collect(Collectors.toList())
                ));
        List<String> distinctList = alllist.stream().map(HousePicture::getPath).distinct().collect(Collectors.toList());
        int i=0;
        for (HousePicture housePicture : distinctLists) {

            assert files != null;
            Assert.isTrue(files[i].exists(), "文件不存在");
            File file = files[i];
            i++;
            String key = housePicture.getPath();

            try {
                Response response = qiniuService.uploadFile(file,key);
                QiniuUploadResult qiniuUploadResult = gson.fromJson(response.bodyString(), QiniuUploadResult.class);
                System.out.println(qiniuUploadResult.getKey());
                Assert.isTrue(response.isOK(), "文件上传失败");
            } catch (QiniuException e) {
                e.printStackTrace();
                Assert.isTrue(false, "文件上传失败");
            }
        }
    }

    @Test
    void testUploadFile() {
        try {
            Response resp = qiniuService.deleteFile("Fox8sEemX6ibXvjNGoX4grYSiHJ4");
            Assert.isTrue(resp.isOK(), "文件删除失败");
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteFile() {
    }
}