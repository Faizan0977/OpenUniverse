/*
 * The MIT License
 * Copyright © 2025 OpenUniverse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ou.common.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author k2Xzny
 */
public class CurrentDateTimeUtils {

    /**
     *
     * @return
     */
    public static Map<String, Object> getCurrentDateTimeInfo() {
        Map<String, Object> currentInfo = new HashMap<>();

        // Current date and time objects
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now();

        // Extract information from the current date and time
        currentInfo.put("currentYear", currentDate.getYear());
        currentInfo.put("currentMonth", currentDate.getMonth());
        currentInfo.put("currentMonthValue", currentDate.getMonthValue());
        currentInfo.put("currentDay", currentDate.getDayOfMonth());
        currentInfo.put("currentDayOfWeek", currentDate.getDayOfWeek());
        currentInfo.put("currentDayOfWeekValue", currentDate.getDayOfWeek().getValue());  // Added currentDayOfWeekValue
        currentInfo.put("currentDayOfYear", currentDate.getDayOfYear());
        currentInfo.put("isLeapYear", currentDate.isLeapYear());
        currentInfo.put("currentHour", currentTime.getHour());
        currentInfo.put("currentMinute", currentTime.getMinute());
        currentInfo.put("currentSecond", currentTime.getSecond());
        currentInfo.put("currentNano", currentTime.getNano());
        currentInfo.put("currentDateTime", currentDateTime);
        currentInfo.put("currentZonedDateTime", currentZonedDateTime);

        // Time zone info
        currentInfo.put("currentTimeZone", currentZonedDateTime.getZone());

        // Length of the month and year
        currentInfo.put("lengthOfMonth", currentDate.lengthOfMonth());
        currentInfo.put("lengthOfYear", currentDate.lengthOfYear());

        // First and last days of the month
        currentInfo.put("firstDayOfMonth", currentDate.withDayOfMonth(1));
        currentInfo.put("lastDayOfMonth", currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        // Calculate the quarter of the year (1, 2, 3, 4 instead of Q1, Q2, Q3, Q4)
        int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1; // Quarters: 1-4
        currentInfo.put("currentQuarter", currentQuarter);

        return currentInfo;
    }
}
