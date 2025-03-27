package capsrock.location.csv.repository;

import capsrock.location.csv.entity.WeatherSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherSpotRepository extends JpaRepository<WeatherSpot, Integer> {

}
