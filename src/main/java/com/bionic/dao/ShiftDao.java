package com.bionic.dao;

import com.bionic.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * author Dima Budko
 */
@Repository
public interface ShiftDao extends JpaRepository<Shift, Integer> {

    @Query("select u.shifts from User u where u.id =:userId")
    List<Shift> getByUserId(@Param("userId") int userId);

    @Query("select s from Shift s where s.user.id = :userId and s.endTime >= :startDate and s.startTime <= :endDate")
    List<Shift> getForPeriod(@Param("userId") int userId,
                             @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM Shift s where s.user.id=:userId")
    void deleteByUser(@Param("userId") int userId);

    @Query("select s from Shift s where s.user.id = :userId and " +
            "((s.startTime BETWEEN :startDate and :endDate) " +
            "or (s.endTime BETWEEN :startDate and :endDate)" +
            "or ((s.startTime <= :startDate) and (s.endTime >= :endDate)))")
    List<Shift> getOverlappedShifts(@Param("userId") int userId,
                                    @Param("startDate") Date startDate, @Param("endDate") Date endDate);


    @Query("select s from Shift s where s.user.id = :userId and " +
            "(s.id != :shiftId) and" +
            "((s.startTime BETWEEN :startDate and :endDate) " +
            "or (s.endTime BETWEEN :startDate and :endDate)" +
            "or ((s.startTime <= :startDate) and (s.endTime >= :endDate)))")
    List<Shift> getOverlappedShifts(@Param("userId") int userId,@Param("shiftId")  int shiftId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
