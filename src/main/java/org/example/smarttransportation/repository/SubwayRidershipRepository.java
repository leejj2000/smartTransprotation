package org.example.smarttransportation.repository;

import org.example.smarttransportation.entity.SubwayRidership;
import org.example.smarttransportation.entity.SubwayRidershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 地铁客流数据访问层
 *
 * @author pojin
 * @date 2025/11/23
 */
@Repository
public interface SubwayRidershipRepository extends JpaRepository<SubwayRidership, SubwayRidershipId> {

    /**
     * 根据时间范围查询客流数据
     */
    @Query("SELECT s FROM SubwayRidership s WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate ORDER BY s.transitTimestamp")
    List<SubwayRidership> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 查询指定区域的客流数据
     */
    @Query("SELECT s FROM SubwayRidership s WHERE s.borough = :borough " +
           "AND DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate")
    List<SubwayRidership> findByBoroughAndDateRange(@Param("borough") String borough,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 查询高峰时段客流
     */
    @Query("SELECT s FROM SubwayRidership s WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate")
    List<SubwayRidership> findRushHourRidership(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查询高密度客流站点
     */
    @Query("SELECT s FROM SubwayRidership s WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate " +
           "AND s.ridership >= :minRidership ORDER BY s.ridership DESC")
    List<SubwayRidership> findHighDensityStations(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("minRidership") Integer minRidership);

    /**
     * 按站点统计平均客流
     */
    @Query("SELECT s.stationComplex, AVG(s.ridership) FROM SubwayRidership s " +
           "WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate " +
           "GROUP BY s.stationComplex ORDER BY AVG(s.ridership) DESC")
    List<Object[]> getAverageRidershipByStation(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 按时段统计客流
     */
    @Query("SELECT HOUR(s.transitTimestamp), AVG(s.ridership) FROM SubwayRidership s " +
           "WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate " +
           "GROUP BY HOUR(s.transitTimestamp) ORDER BY HOUR(s.transitTimestamp)")
    List<Object[]> getAverageRidershipByHour(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * 查询指定站点的客流趋势
     */
    @Query("SELECT s FROM SubwayRidership s WHERE s.stationComplex = :stationName " +
           "AND DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate ORDER BY s.transitTimestamp")
    List<SubwayRidership> findStationRidershipTrend(@Param("stationName") String stationName,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * 查询客流异常站点（客流量超过平均值2倍）
     */
    @Query("SELECT s FROM SubwayRidership s WHERE DATE(s.transitTimestamp) BETWEEN :startDate AND :endDate " +
           "AND s.ridership > (SELECT AVG(sr.ridership) * 2 FROM SubwayRidership sr " +
           "WHERE DATE(sr.transitTimestamp) BETWEEN :startDate AND :endDate)")
    List<SubwayRidership> findAbnormalRidership(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}