package com.example.demo.aspects;

import com.example.demo.exceptions.NotWorkingDaysException;
import com.example.demo.exceptions.NotWorkingHoursException;
import com.example.demo.service.HolidayService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Aspect
@Component
public class WorkingHoursAspect {

    private final HolidayService holidayService;

    String countryCode = "KZ";

    public WorkingHoursAspect(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @Around("execution(* com.example.demo.controllers.*.*(..))")
    public Object checkWorkingCondition(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();

        checkWorkingHours(currentTime);
        checkWeekends(currentDate);
        checkHolidays(currentDate);

        return joinPoint.proceed();

    }

    public void checkWorkingHours(LocalTime currentTime){
        if(currentTime.isBefore(LocalTime.of(8,0)) || currentTime.isAfter(LocalTime.of(17,0))){
            throw  new NotWorkingHoursException("В нерабочее время кофемашина недоступна.Рабочие часы:08:00 - 17:00");
        }
    }

    public void checkWeekends(LocalDate currentDate){
        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new NotWorkingDaysException("Кофемашина не работает в выходные дни");
        }
    }

    public void checkHolidays(LocalDate currentDate){
        if (holidayService.isHoliday(countryCode, currentDate)) {
            throw new NotWorkingDaysException("Кофемашина не работает в праздничные дни");
        }
    }

}
