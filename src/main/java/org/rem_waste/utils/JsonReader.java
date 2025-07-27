package org.rem_waste.utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class JsonReader {
    public static List<Map<String, String>> getTestData(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(fileName)) {
            return mapper.readValue(inputStream, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data from JSON: " + fileName, e);
        }
    }

}
