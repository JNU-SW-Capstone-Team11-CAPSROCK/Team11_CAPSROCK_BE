package capsrock.location.service;

import capsrock.location.model.entity.WeatherSpot;
import capsrock.location.repository.WeatherSpotRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CoordinateToStnConverterService {

    private static final double EARTH_RADIUS = 6371.0;

    private final WeatherSpotRepository weatherSpotRepository;

    public CoordinateToStnConverterService(WeatherSpotRepository weatherSpotRepository) {
        this.weatherSpotRepository = weatherSpotRepository;
    }

    @Transactional
    public Integer convertToStn(Double latitude, Double longitude) {
        List<WeatherSpot> weatherSpots = weatherSpotRepository.findAll(); //todo: 성능 최적화

        return weatherSpots.stream()
                .min(Comparator.comparingDouble(
                        spot -> haversine(latitude, longitude,
                                spot.getLatitude(), spot.getLongitude())))
                .map(WeatherSpot::getStn)
                .orElse(null); // todo: orElseThrow 로 리팩터링
    }


    // 🌍 하버사인 공식 (Haversine Formula) 사용
    private double haversine(Double lat1, Double lon1, Double lat2, Double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // 최종 거리 (km 단위)
    }

}
