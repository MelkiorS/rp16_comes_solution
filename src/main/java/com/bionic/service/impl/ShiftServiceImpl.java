package com.bionic.service.impl;

import com.bionic.dao.ShiftDao;

import com.bionic.exception.shift.impl.ShiftOverlapsException;
import com.bionic.model.Ride;
import com.bionic.model.Shift;
import com.bionic.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author taras.yaroshchuk
 */
@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftDao shiftDao;

    @Override
    public Shift addShift(Shift shift) throws ShiftOverlapsException {
        if (shift.getRides() != null) {
            for (Ride ride : shift.getRides()) {
                ride.setShift(shift);
            }
        }

        int userId = shift.getUser().getId();
        List<Shift> overlappedShifts = shiftDao.getOverlappedShifts(userId, shift.getStartTime(), shift.getEndTime());
        if (overlappedShifts != null)
            throw new ShiftOverlapsException(overlappedShifts);
        return shiftDao.saveAndFlush(shift);
    }


    @Override
    public void delete(int id)  { shiftDao.delete(id); }

    @Override
    public Shift getById(int id) {
        return shiftDao.findOne(id);
    }

    @Override
    public Shift editShift(Shift shift) {
        if (shift.getRides() != null) {
            for (Ride ride : shift.getRides()) {
                ride.setShift(shift);
            }
        }
        return shiftDao.saveAndFlush(shift);
    }

    @Override
    public List<Shift> getByUserId(int user_id) {
        return shiftDao.getByUserId(user_id);
    }

    @Override
    @Transactional
    public void deleteByUser(int user_id) {
        shiftDao.deleteByUser(user_id);
    }
}
