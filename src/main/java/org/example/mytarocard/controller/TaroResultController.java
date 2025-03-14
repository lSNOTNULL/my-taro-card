package org.example.mytarocard.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mytarocard.model.dto.GeminiContentResponseDto;
import org.example.mytarocard.service.SupabaseService;
import org.example.mytarocard.service.SupabaseServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/result/*")
public class TaroResultController extends Controller {
    SupabaseService supabaseService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    //JSON  직렬화를 위한 Mapper 추가.
    @Override
    public void init() throws ServletException {
        log("TaroResultController Init");
        supabaseService = SupabaseServiceImpl.getInstance();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("TaroResultController doGet");
        String uuid = req.getPathInfo().substring(1);
        // /{uuid} -> / 없애주기 위해서 앞에 1개 없앰
//        req.setAttribute("uuid", uuid);
        String jsonData;
        try {
            jsonData = supabaseService.findById(uuid);
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }


        String contentValue = null;

        try {
            log("JSON Data: " + jsonData);
            List<GeminiContentResponseDto> responseList = objectMapper.readValue(jsonData, new TypeReference<List<GeminiContentResponseDto>>() {});
            // jsonData = supabaseService.findById(uuid); 의 값을 String 역직렬화 해줌.
            // 또한 [Key-Value] (배열)형태이므로, List
            contentValue = responseList.get(0).text();
            // GeminiContentResponse record의 content값 추출.

//            ObjectMapper mapper = new ObjectMapper();
//            List<Map<String, String>> resultList = objectMapper.readValue(jsonData, List.class);
//            String content = null;
//            if(!resultList.isEmpty()) {
//                Map<String, String> resultMap = resultList.get(0);
//                content = resultMap.get("text");
//            }
//            if(content != null) {
//                Map<String, String> map = new HashMap<>();
//                map.put("content", content);
//                contentValue = mapper.writeValueAsString(map);
//            } else {
//                contentValue = "{\"content\": \"결과가 없습니다.\"}";
//            }
        }catch (Exception e){
            log("TaroResultController doGet exception");
            log(e.getMessage());
            e.printStackTrace();
        }
        req.setAttribute("data", contentValue);
//        try {
//            req.setAttribute("data", supabaseService.findById(uuid));
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        view(req, resp, "result");
    }
}
