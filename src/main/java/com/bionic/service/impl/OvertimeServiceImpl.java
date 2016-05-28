package com.bionic.service.impl;

import com.bionic.dao.DayTypeDao;
import com.bionic.dao.ShiftDao;
import com.bionic.dto.OvertimeDTO;
import com.bionic.exception.shift.impl.ShiftsNotFoundException;
import com.bionic.model.DayType;
import com.bionic.model.Ride;
import com.bionic.model.Shift;
import com.bionic.service.OvertimeService;
import com.bionic.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.bionic.service.util.MonthCalculator.getMonthEndTime;
import static com.bionic.service.util.MonthCalculator.getMonthStartTime;
import static com.bionic.service.util.PeriodCalculator.getPeriodEndTime;
import static com.bionic.service.util.PeriodCalculator.getPeriodStartTime;
import static com.bionic.service.util.WeekCalculator.*;

/**
 * Created by Forsent on 28.05.2016.
 */
@Service
public class OvertimeServiceImpl implements OvertimeService {

    @Autowired
    private ShiftDao shiftDao;

    @Autowired
    private DayTypeDao dayTypeDao;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Override
    public List<OvertimeDTO> getOvertimeForPeriod(int userId, int year, int period) throws ShiftsNotFoundException {

        Date periodStartTime = getPeriodStartTime(year, period);
        Date periodEndTime = getPeriodEndTime(year, period);

        List<Shift> shifts = shiftDao.getForPeriod(userId, periodStartTime, periodEndTime);
        if (ObjectUtils.isEmpty(shifts)) throw new ShiftsNotFoundException();
        List<DayType> dayTypes = dayTypeDao.getDayTypesForPeriod(userId, periodStartTime, periodEndTime);


        List<OvertimeDTO> overtime = new ArrayList<>();


        return null;
    }

    @Override
    public List<OvertimeDTO> getOvertimeForMonth(int userId, int year, int month) throws ShiftsNotFoundException {
        Date monthStartTime = getMonthStartTime(year, month);
        Date monthEndTime = getMonthEndTime(year, month);


        List<Shift> shifts = shiftDao.getForPeriod(userId, monthStartTime, monthEndTime);
        if (ObjectUtils.isEmpty(shifts)) throw new ShiftsNotFoundException();

        List<OvertimeDTO> overtimeDTOs = new ArrayList<>();
        int numberOfWeeks = getWeeksBetween(monthStartTime, monthEndTime);

        for (int week = 1; week <= numberOfWeeks; week++) {
            Date weekStartTime = getMonthWeekStartTime(year, month, week);
            Date weekEndTime = getMonthWeekEndTime(year, month, week);

            int contractHours = workScheduleService.getContractHoursForWeek(userId, weekStartTime);
            if (contractHours == 0) contractHours = 40;
            long contractTime = contractHours * 60 * 60 * 1000;
            int weekNumber = getMonthWeekOfYear(year, month, week);

            List<DayType> dayTypes = dayTypeDao.getDayTypesForPeriod(userId, monthStartTime, monthEndTime);

            OvertimeDTO workingWeek = getOvertimeForWeek(dayTypes, shifts, weekStartTime, weekEndTime, contractTime);
            workingWeek.setWeekOfYear(weekNumber);

            overtimeDTOs.add(workingWeek);
        }

        return overtimeDTOs;
    }

    @Override
    public OvertimeDTO getOvertimeForWeek(List<DayType> dayTypes, List<Shift> shifts, Date weekStartTime, Date weekEndTime, long contractTime) {
        OvertimeDTO overtimeDTO = new OvertimeDTO();
        overtimeDTO.setStartTime(weekStartTime);
        overtimeDTO.setEndTime(weekEndTime);

        shift:
        for (Shift s : shifts) {

            List<Ride> rides = s.getRides();
            Collections.sort(rides, (l, r) -> (int) (l.getStartTime().getTime() - r.getStartTime().getTime()));

            for (Ride r : rides) {
                if (r.getEndTime().getTime() > weekStartTime.getTime()) {
                    if (r.getStartTime().getTime() > weekEndTime.getTime()) break shift;
                }
            }
        }
        return null;
    }
}
