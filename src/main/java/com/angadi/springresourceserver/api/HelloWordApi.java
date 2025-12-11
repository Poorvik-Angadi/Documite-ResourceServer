package com.angadi.springresourceserver.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.Map;


@RestController
@SecurityRequirement(name = "bearerAuth")
public class HelloWordApi {

    @GetMapping
    public String welcome(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt != null) {

                String[] chunks = jwt.getTokenValue().split("\\.");
                Base64.Decoder decoder = Base64.getUrlDecoder();

                String header = new String(decoder.decode(chunks[0]));
                String payload = new String(decoder.decode(chunks[1]));

                Map<String, Object> payloadMap = mapPayloadToMap(payload);


                return "Welcome " + payloadMap.get("name");
            } else
                return "hello World, no JWT";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> mapPayloadToMap(String payloadJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payloadJson, Map.class);
    }
}
