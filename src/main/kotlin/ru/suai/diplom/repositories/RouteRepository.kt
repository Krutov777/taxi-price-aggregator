package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.Route

@Repository
interface RouteRepository : JpaRepository<Route, Long> {
    @Query(
        value = "select * from route where from_latitude = ?1" +
                " and from_longitude = ?2" +
                " and to_latitude = ?3" +
                " and to_longitude = ?4",
        nativeQuery = true
    )
    fun findAll(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double
    ) : List<Route>
}