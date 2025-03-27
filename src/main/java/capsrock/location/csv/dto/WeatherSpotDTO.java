package capsrock.location.csv.dto;

import capsrock.location.csv.entity.WeatherSpot;
import com.opencsv.bean.CsvBindByName;

public final class WeatherSpotDTO {

    @CsvBindByName
    private Integer stn;
    @CsvBindByName
    private Double longitude;
    @CsvBindByName
    private Double latitude;

    public WeatherSpotDTO() { }

    public WeatherSpotDTO(
            Integer stn,
            Double longitude,
            Double latitude
    ) {
        this.stn = stn;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public WeatherSpot toEntiy() {
        return new WeatherSpot(stn, longitude, latitude);
    }

    public Integer getStn() {
        return stn;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
