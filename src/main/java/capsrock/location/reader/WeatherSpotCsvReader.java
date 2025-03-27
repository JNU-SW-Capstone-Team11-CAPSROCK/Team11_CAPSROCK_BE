package capsrock.location.reader;

import capsrock.location.dto.WeatherSpotDTO;
import com.opencsv.bean.CsvToBeanBuilder;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import org.springframework.stereotype.Component;


@Component
public class WeatherSpotCsvReader {

    public List<WeatherSpotDTO> readCsv() {
        try {
            return new CsvToBeanBuilder<WeatherSpotDTO>(
                    new FileReader("src/main/resources/WeatherSpot.csv"))
                    .withType(WeatherSpotDTO.class)
                    .build()
                    .parse();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}