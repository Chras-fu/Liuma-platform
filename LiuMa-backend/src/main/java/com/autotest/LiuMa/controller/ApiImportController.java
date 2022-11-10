package com.autotest.LiuMa.controller;


import com.autotest.LiuMa.service.ApiImportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/autotest/import")
public class ApiImportController {
    @Resource
    public ApiImportService apiImportService;

    @PostMapping("/api")
    public String uploadHandler( @RequestParam MultipartFile file, @RequestParam String platformType, @RequestParam String project_id, @RequestParam String module_id, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        Map<String, String> assistMap = new HashMap<>();
        assistMap.put("userId", userId);
        assistMap.put("projectId", project_id);
        assistMap.put("moduleId", module_id);

        StringBuilder stringBuilder ;
        try {
            if (file!=null) {
                InputStream bb = file.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(bb);
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                bb.close();
                System.out.println(stringBuilder);
                if (apiImportService.verifyApi(stringBuilder.toString(), platformType)){
                    return apiImportService.saveImportApi(stringBuilder.toString(), platformType, assistMap) + "";
                }
                else{
                    return "import api error";
                }
            } else {
                return "need upload file";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown error";
    }

}
