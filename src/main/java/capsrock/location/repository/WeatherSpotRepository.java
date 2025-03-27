package capsrock.location.repository;

import capsrock.location.model.entity.WeatherSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherSpotRepository extends JpaRepository<WeatherSpot, Integer> {

}
