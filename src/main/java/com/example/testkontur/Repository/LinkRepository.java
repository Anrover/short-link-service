package com.example.testkontur.Repository;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.RankedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, String> {
    //List<Link> findAllByName(String name);

    //@Query("SELECT new Thing(t, cos(t.someDouble)*sin(t.someDouble))FROM Thing t WHERE t.someValue = :someValue AND t.someDouble = :someDouble AND cos(t.someDouble)*sin(t.someDouble)> 30")
    //@Query(value = "select new Link(l.shortLink, l.originLink, l.countRequests) from Link l where l.shortLink = :shortLink")
    //@Query(value = "select new Link(l.shortLink, l.originLink, l.countRequests, rownum) from Link l where l.shortLink = :shortLink order by l.countRequests ASC")//"SELECT l, ROW_NUMBER() OVER(ORDER BY l)\n FROM Link")
//    @Query(value = "select top 1 * from (select l.Original_Link as originLink, l.Short_Link as shortLink," +
//            " l.Count_Requests as countRequests, rownum() as rank" +
//            " from Link l order by l.Count_Requests) as t where l.Short_Link = :shortLink", nativeQuery = true)
    @Query(value = "select top 1 t.Original_Link as original, t.Short_Link as link, t.Count_Requests as count, " +
            "t.rank as rank from (select *, rownum as rank from (select * from LINK order by COUNT_REQUESTS desc)) t" +
            " where t.Short_Link = :shortLink", nativeQuery = true)
    RankedLink findLinkStats(@Param("shortLink") String shortLink);

    @Query(value = "select t.Original_Link as original, t.Short_Link as link, t.Count_Requests as count, " +
            "t.rank as rank from (select *, rownum as rank from (select * from LINK order by COUNT_REQUESTS desc)) t" +
            " where t.rank >= :startIndex and t.rank <= :endIndex", nativeQuery = true)
    List<RankedLink> findSubRankedLinks(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    @Modifying(clearAutomatically=true)
    @Query("update Link l set l.countRequests = l.countRequests + 1 where l.shortLink = :shortLink")
    Link updateRequestsLink(@Param("shortLink") String shortLink);
}

