package com.example.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class OembedServiceImp implements OembedService {

    private final static String FACEBOOKURL = "https://www.facebook.com/plugins/post/oembed.json/?url=";
    private final static String INSTAGRAMURL = "https://api.instagram.com/oembed?url=";
    private final static String TWITTERURL = "https://publish.twitter.com/oembed?url=";
    private final static String TIKTOKURL = "https://www.tiktok.com/oembed?url=";
    private final static String YOUTUBEURL = "https://www.youtube.com/embed?url=";


    public static String getName(String url) {
        if (url.startsWith("www.")) {
            url = url.substring(4);
        }
        if (url.endsWith(".com")) {
            url = url.substring(0, url.length()-4);
        }
        return url;

    }
    public HttpEntity<Map<String, Object>> callEmbedProcess(String paramUrl) {
        Map<String, Object> result = new HashMap<>();
        String domain = "";
        domain = getName(paramUrl);

        if (domain.equals("instagram")) {
            return getInstagramHTML(paramUrl);
        } else if (domain.equals("facebook")) {
            return getFacebookHTML(paramUrl);
        } else if (domain.equals("twitter")) {
            return getTwitterHTML(paramUrl);
        } else if (domain.equals("tiktok")) {
            return getTiktokHTML(paramUrl);
        } else {
            result.put("result", "Fail");
            result.put("response", "현재 지원하지 않는 Social 이거나, 잘못된 URL 입니다. 다시 확인해주시기 바랍니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }


    private HttpEntity<Map<String, Object>> getFacebookHTML(String paramUrl) {
        Map<String, String> embedResult = new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        // 정규표현식을 사용해 Facebook이 Embed를 지원하는 URL인지 확인
        boolean isFacebookPost = Pattern.compile("https://www.facebook.com/.*?/(posts|photos|videos)/.*?").matcher(paramUrl).find();

        // 지원하지 않는 URL은 실패 처리
        if (isFacebookPost == false) {
            result.put("result", "Fail");
            result.put("response", "지원하지 않는 형식의 Facebook URL 입니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // 지원하는 URL이 판단되면 API를 통해 HTML을 받아온다.
        RestTemplate template = new RestTemplate();
        String embedResponse = template.getForObject(FACEBOOKURL + paramUrl, String.class);

        // TODO: Facebook return Type Check
        // 페이스북의 경우 text 형태로 값을 리턴하기에 jackson을 사용하여 다시 Map으로 만들어준다.
        ObjectMapper mapper = new ObjectMapper();
        try {
            embedResult = mapper.readValue(embedResponse, new TypeReference<Map<String, String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            result.put("result", "Fail");
            result.put("response", "지원하지 않는 형식의 Facebook URL 입니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        result.put("result", "success");
        result.put("response", embedResult.get("html"));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private HttpEntity<Map<String, Object>> getInstagramHTML(String paramUrl) {
        Map<String, Object> embedResult = new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        // 인스타그램 포스트 확인
        boolean isInstagramPost = Pattern.compile("(https://www.instagram.com/p/.*?)").matcher(paramUrl).find();
        if (isInstagramPost == false) {
            result.put("result", "Fail");
            result.put("response", "지원하지 않는 형식의 Instagram URL 입니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // API 요청
        RestTemplate template = new RestTemplate();
        embedResult = template.getForObject(INSTAGRAMURL + paramUrl, Map.class);

        result.put("result", "success");
        result.put("response", embedResult.get("html"));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Twitter Embed
     * @param paramUrl
     * @return
     */
    private HttpEntity<Map<String, Object>> getTwitterHTML(String paramUrl) {
        Map<String, Object> embedResult = new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        // 트위터 포스트 확인
        boolean isTwitterPost = Pattern.compile("(https://twitter.com/.*/status/.*?)").matcher(paramUrl).find();
        if (isTwitterPost == false) {
            result.put("result", "Fail");
            result.put("response", "지원하지 않는 형식의 Twitter URL 입니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // API 요청
        RestTemplate template = new RestTemplate();
        embedResult = template.getForObject(TWITTERURL + paramUrl, Map.class);

        result.put("result", "success");
        result.put("response", embedResult.get("html"));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private HttpEntity<Map<String, Object>> getTiktokHTML(String paramUrl) {
        Map<String, Object> embedResult = new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        // 틱톡 포스트 확인
        boolean isTiktokPost = Pattern.compile("(https://www.tiktok.com/.*/video/.*?)").matcher(paramUrl).find();
        if (isTiktokPost == false) {
            result.put("result", "Fail");
            result.put("response", "지원하지 않는 형식의 Instagram URL 입니다.");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // API 요청
        RestTemplate template = new RestTemplate();
        embedResult = template.getForObject(TIKTOKURL + paramUrl, Map.class);

        result.put("result", "success");
        result.put("response", embedResult.get("html"));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private HttpEntity<Map<String, Object>> getYouTubeHTML(String paramUrl) {
        Map<String, Object> embedResult = new HashMap<>();
        Map<String, Object> result = new HashMap<>();



        boolean isYouTubePost = Pattern.compile("(https://www.youtube.com/.*/video/.*?)").matcher(paramUrl).find();

        return null;
    }


}
