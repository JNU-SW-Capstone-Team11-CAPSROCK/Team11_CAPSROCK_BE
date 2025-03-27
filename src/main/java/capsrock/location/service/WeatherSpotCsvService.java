package capsrock.location.service;

import capsrock.location.dto.WeatherSpotDTO;
import capsrock.location.model.entity.WeatherSpot;
import capsrock.location.reader.WeatherSpotCsvReader;
import capsrock.location.repository.WeatherSpotRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class WeatherSpotCsvService implements ApplicationRunner {

    private final WeatherSpotCsvReader csvReader;
    private final WeatherSpotRepository weatherSpotRepository;

    @Autowired
    WeatherSpotCsvService(WeatherSpotCsvReader csvReader,
            WeatherSpotRepository weatherSpotRepository) {
        this.csvReader = csvReader;
        this.weatherSpotRepository = weatherSpotRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            List<WeatherSpotDTO> WeatherSpotDTOList = csvReader.readCsv();

            List<WeatherSpot> weatherSpotList = WeatherSpotDTOList
                    .stream()
                    .map(WeatherSpotDTO::toEntiy)
                    .toList();

            weatherSpotRepository.saveAll(weatherSpotList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
