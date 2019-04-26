package com.nowcoder.toutiao.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @program: new_toutiao
 * @description: 七牛云服务，上传图片的功能
 * @author: Cheng Qun
 * @create: 2019-04-25 10:43
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    //构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.zone0());
    //...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);

    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "ju5Oq1uAcEi9OdfLVufv0-_XtxOED9xXL9aVldA5";
    String SECRET_KEY = "P3j5qFY35Gs1SVKx4cfYH1zc2jndoKv6Efz1m-3S";
    //要上传的空间
    String bucket = "nowcoder";

    //如果是Windows情况下，格式是 D:\\qiniu\\test.png
    String localFilePath = "D:\\qiniu";
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String key = null;

   // Auth auth = Auth.create(accessKey, secretKey);
   //

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
   // UploadManager uploadManager = new UploadManager();

    private static String QINIU_IMAGE_DOMAIN = "http://pqi5dh79o.bkt.clouddn.com/";
    String upToken = auth.uploadToken(bucket);
    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
//    public String getUpToken() {
//        return auth.uploadToken(bucketname);
//    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            //上传图片的格式判断
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            key= fileName;
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), key, upToken);
            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
            //打印返回的信息
//            System.out.println(res.bodyString());
//            return null;
            if (res.isOK() && res.isJson()) {
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX + putRet.key;
            } else {
                logger.error("七牛异常 try:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            logger.error("七牛异常 catch:" + e.getMessage());
            return null;
//            Response r = e.response;
//            System.err.println(r.toString());
//            try {
//                System.err.println(r.bodyString());
//            } catch (QiniuException ex2) {
//                //ignore
//            }
        }
       // return null;
    }

}
