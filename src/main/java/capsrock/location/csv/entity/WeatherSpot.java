package capsrock.location.csv.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WeatherSpot {
    @Id
    private Integer stn;
    private Double latitude;
    private Double longitude;

    public WeatherSpot() { }

    public WeatherSpot(Integer stn, Double latitude, Double longitude) {
        this.stn = stn;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getStn() { return stn; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
