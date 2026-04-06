package com.gp.asset.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.asset.entity.GpAsset;
import com.gp.asset.repository.GpAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GpAssetService {

    @Autowired
    private GpAssetRepository repository;

    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.token}")
    private String token;

    @Value("${external.api.geofenceIds}")
    private String geofenceIds;
    @Value("${external.api.asset.details.url}")
    private String assetDetailsUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<GpAsset> fetchAllAssets() {

        List<GpAsset> savedList = new ArrayList<>();

        try {

            // ✅ Convert geofenceIds
            List<Long> geoIds = new ArrayList<>();
            for (String id : geofenceIds.split(",")) {
                geoIds.add(Long.parseLong(id.trim()));
            }

            Long lastAssetId = null;
            Set<Long> uniqueIds = new HashSet<>();

            while (true) {

                // ✅ Request Body
                Map<String, Object> body = new HashMap<>();
                body.put("lastAssetId", lastAssetId);
                body.put("assetTypes", List.of("GP", "ONT"));
                body.put("geofences", geoIds);

                // ✅ Headers
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request =
                        new HttpEntity<>(body, headers);

                // 🔥 MAIN API CALL
                ResponseEntity<String> response = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        request,
                        String.class
                );

                JsonNode rootNode = mapper.readTree(response.getBody());
                JsonNode assets = rootNode.path("assets");

                // ✅ Stop condition
                if (assets == null || assets.size() == 0) {
                    break;
                }

                for (JsonNode asset : assets) {

                    Long assetId = asset.path("id").asLong();
                    String name = asset.path("name").asText(null);
                    String type = asset.path("assetTypeName").asText(null);
                    String amcStatus = asset.path("amcStatus").asText(null);
                    String availability = asset.path("availability").asText(null);
                    String district = asset.path("district").asText(null);
                    String phase = asset.path("phase").asText("");
                    String block = asset.path("block").asText(null);

                    // ✅ Skip SURVEY
                    if ("SURVEY".equalsIgnoreCase(phase)) continue;

                    // ✅ Avoid duplicates
                    if (uniqueIds.contains(assetId)) continue;
                    uniqueIds.add(assetId);

                    // 🔥 NEW: Fetch LGD Code
                    String lgdCode = fetchLgdCode(assetId);

                    // ✅ Save
                    GpAsset assetObj = new GpAsset(
                            name,
                            type,
                            amcStatus,
                            assetId,
                            availability,
                            district,
                            block,
                            lgdCode
                    );

                    savedList.add(repository.save(assetObj));

                    // ✅ Update lastAssetId
                    lastAssetId = assetId;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedList;
    }

    // 🔥 HELPER METHOD (LGD FETCH)
    private String fetchLgdCode(Long assetId) {

        try {
            // ✅ CORRECT URL
            String url = assetDetailsUrl + "/" + assetId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            JsonNode attributeValues = root.path("assetTypeAttributeValues");

            if (attributeValues.isArray()) {
                for (JsonNode attr : attributeValues) {

                    String attrName = attr
                            .path("assetTypeAttribute")
                            .path("name")
                            .asText();

                    if ("lgd_code".equalsIgnoreCase(attrName)) {
                        String lgd = attr.path("attributeValue").asText(null);

                        System.out.println("✅ LGD FOUND → " + assetId + " = " + lgd);

                        return lgd;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ LGD fetch failed for assetId: " + assetId);
            e.printStackTrace(); // 🔥 IMPORTANT for debugging
        }

        return null;
    }
}