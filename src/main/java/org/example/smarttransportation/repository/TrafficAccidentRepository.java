package org.example.smarttransportation.repository;

import org.example.smarttransportation.entity.TrafficAccident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 交通事故数据访问层
 *
 * @author pojin
 * @date 2025/11/23
 */
@Repository
public interface TrafficAccidentRepository extends JpaRepository<TrafficAccident, Long> {

    /**
     * 根据时间范围查询事故
     */
    @Query("SELECT t FROM TrafficAccident t WHERE t.crashDate BETWEEN :startDate AND :endDate")
    List<TrafficAccident> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 查询指定时间范围内的高峰时段事故
     */
    @Query("SELECT t FROM TrafficAccident t WHERE t.crashDate BETWEEN :startDate AND :endDate")
    List<TrafficAccident> findRushHourAccidents(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查询天气相关事故
     */
    @Query("SELECT t FROM TrafficAccident t WHERE t.crashDate BETWEEN :startDate AND :endDate " +
           "AND (LOWER(t.contributingFactorVehicle1) LIKE %:weatherFactor% " +
           "OR LOWER(t.contributingFactorVehicle2) LIKE %:weatherFactor%)")
    List<TrafficAccident> findWeatherRelatedAccidents(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("weatherFactor") String weatherFactor);

    /**
     * 按街道统计事故数量
     */
    @Query("SELECT t.onStreetName, COUNT(t) FROM TrafficAccident t " +
           "WHERE t.crashDate BETWEEN :startDate AND :endDate " +
           "AND t.onStreetName IS NOT NULL " +
           "GROUP BY t.onStreetName ORDER BY COUNT(t) DESC")
    List<Object[]> countAccidentsByStreet(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 查询严重事故（有伤亡）
     */
    @Query("SELECT t FROM TrafficAccident t WHERE t.crashDate BETWEEN :startDate AND :endDate " +
           "AND (t.numberOfPersonsInjured > 0 OR t.numberOfPersonsKilled > 0)")
    List<TrafficAccident> findSevereAccidents(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    /**
     * 按时段统计事故数量
     */
    @Query("SELECT t.crashDate, COUNT(t) FROM TrafficAccident t " +
           "WHERE t.crashDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.crashDate ORDER BY t.crashDate")
    List<Object[]> countAccidentsByHour(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}