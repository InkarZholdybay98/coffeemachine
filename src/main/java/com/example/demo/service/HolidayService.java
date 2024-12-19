package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class HolidayService {

    private final RestTemplate restTemplate;
    private static final String HOLIDAYS_URL = "https://date.nager.at/Api/v3/PublicHolidays/{year}/{countryCode}";
    private final ObjectMapper objectMapper;

    public HolidayService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "holidays", key = "#countryCode + #year")
    public List<Map<String, Object>> getHolidays(String countryCode, int year) {
        String response = fetchHolidaysFromApi(countryCode, year);
        return parseHolidays(response);
    }

    public boolean isHoliday(String countryCode , LocalDate date){
        List<Map<String , Object>> holidays = getHolidays(countryCode , date.getYear());
        return  holidays
                .stream()
                .anyMatch(holiday -> holiday.get("date").equals(date.toString()));
    }

    private String fetchHolidaysFromApi(String countryCode, int year) {
        try {
            return restTemplate.getForObject(HOLIDAYS_URL, String.class, year, countryCode);
        } catch (RestClientException e) {
            System.err.println("Ошибка при вызове внешнего API: " + e.getMessage());
            return null;
        }
    }

    private List<Map<String, Object>> parseHolidays(String response) {
        if (response == null) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, Object>> holidays = objectMapper.readValue(response, List.class);
            return holidays == null || holidays.isEmpty() ? Collections.emptyList() : holidays;
        } catch (Exception e) {
            System.err.println("Ошибка при обработке ответа API: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}
