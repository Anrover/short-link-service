package com.example.testkontur.Repository;

import com.example.testkontur.Entity.MLink;
import com.example.testkontur.Entity.RankedLinkProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<MLink, String> {
    @Query(value = "select top 1 t.Original_Link as original, t.Short_Link as link, t.Count_Requests as count, " +
            "t.rank as rank from (select *, rownum as rank from (select * from LINK order by COUNT_REQUESTS desc)) t" +
            " where t.Short_Link = :shortLink", nativeQuery = true)
    RankedLinkProjection findLinkStats(@Param("shortLink") String shortLink);

    @Query(value = "select t.Original_Link as original, t.Short_Link as link, t.Count_Requests as count, " +
            "t.rank as rank from (select *, rownum as rank from (select * from LINK order by COUNT_REQUESTS desc)) t" +
            " where t.rank >= :startIndex and t.rank <= :endIndex", nativeQuery = true)
    List<RankedLinkProjection> findSubRankedLinks(@Param("startIndex") long startIndex, @Param("endIndex") long endIndex);
}

