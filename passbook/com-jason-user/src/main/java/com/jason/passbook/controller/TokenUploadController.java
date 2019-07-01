package com.jason.passbook.controller;

import com.jason.passbook.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * write Token to Redis
 * PassTemplate Token Upload
 */

/**
 * RestController他会返回一个json字符串的结果
 * 而controller可以返回一个html模板文件
 */
@Controller
@Slf4j
public class TokenUploadController {

    /**
     * Redis 客户端
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/upload")
    public String upload(){
        return "upload";
    }

    @PostMapping("/token")
    public String tokenFileUpload(@RequestParam("merchantsId") String merchantsId,
                                  @RequestParam("passTemplateId") String passTemplateId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes){
        if(null == passTemplateId || file.isEmpty()){
            redirectAttributes.addFlashAttribute(
                    "message", "passTemplateId is Null or file is empty!");
            return "redirect:/uploadStatus";
        }

        try{
            File cur = new File(Constants.TOKEN_DIR + merchantsId);
             if(!cur.exists()){
                 log.info("Create File: {}", cur.mkdir());
             }

             Path path = Paths.get(Constants.TOKEN_DIR, merchantsId, passTemplateId);
             Files.write(path, file.getBytes());
             if(!writeTokenToRedis(path, passTemplateId)){
                 //写入失败了:
                 redirectAttributes.addFlashAttribute("message", "write token error");
             }else{
                 //写入成功:
                 redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
             }
        } catch (IOException ex){
            log.error("tokenFileUpload error: {}", ex.getMessage());
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus(){
        return "uploadStatus";
        //会返回一个"uploadStatus"的字符串
        // 然后会自动跳转到uploadStatus.hmtl这个页面上
    }



    /**
     * 将token写入redis
     * @param path {@link Path}
     * @param key Redis的key
     * @return true/false
     */
    private boolean writeTokenToRedis(Path path, String key){

        //防止重复, 使用set
        Set<String> tokens;
        try(Stream<String> stream = Files.lines(path)){
            tokens = stream.collect(Collectors.toSet());
        } catch (IOException ex){
            log.error("writeTokenToRedis: {}", ex.getMessage());
            return false;
        }

        if(!CollectionUtils.isEmpty(tokens)){
            //connection 强转成 (RedisCallback<Object>) 类型
            //批量一次请求、一次批量返回，从而节省开销、提高效率。
            redisTemplate.executePipelined((RedisCallback<Object>) connection ->{
                for(String token : tokens){
                    connection.sAdd(key.getBytes(), token.getBytes());
                }
                return null;
            });
            return true;
        }
        return false;
    }
}
