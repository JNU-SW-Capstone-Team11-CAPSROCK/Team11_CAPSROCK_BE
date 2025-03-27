package capsrock.location.csv.service;


import capsrock.location.csv.dto.WeatherSpotDTO;
import capsrock.location.csv.entity.WeatherSpot;
import capsrock.location.csv.reader.WeatherSpotCsvReader;
import capsrock.location.csv.repository.WeatherSpotRepository;
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
            capsrock.location.csv.repository.WeatherSpotRepository weatherSpotRepository) {
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
