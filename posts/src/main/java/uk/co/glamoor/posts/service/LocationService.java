package uk.co.glamoor.posts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public LocationService() {
        this.restTemplate = new RestTemplate();
    }

    public double[] getLocation(String ip) {
        String url = "http://ip-api.com/json/" + ip;
        try {
            String response = restTemplate.getForObject(url, String.class);

            logger.info(response);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);

            if ("success".equalsIgnoreCase(jsonNode.get("status").toString())) {
                return new double[]{jsonNode.get("lng").asDouble(), jsonNode.get("lat").asDouble()};
            }
        } catch (Exception e) {
            logger.error("Could not retrieve location for: {}", ip, e);
        }
        return null;
    }
}

