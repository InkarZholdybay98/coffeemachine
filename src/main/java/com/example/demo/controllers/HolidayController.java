package com.example.demo.controllers;

import com.example.demo.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/coffeemachine")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService){
        this.holidayService = holidayService;
    }

    @GetMapping("/holidays")
    @Operation(summary = "Праздничные дни страны",
            description = "Вернет список праздничных дней по коду страны")
    public ResponseEntity<?> getHolidays(
            @RequestParam String countryCode,
            @RequestParam int year
    ){

        List<Map<String , Object>> holidays = holidayService.getHolidays( countryCode, year);
        return buildResponseForHolidays(holidays , countryCode);

    }

    public ResponseEntity<?> buildResponseForHolidays(List<Map<String , Object>> holidays , String countryCode){
        if(holidays.isEmpty()){
            return ResponseEntity.status(404).body("Праздники для страны "+ countryCode+" отсутствуют");
        }

        return ResponseEntity.ok(holidays);
    }
}
