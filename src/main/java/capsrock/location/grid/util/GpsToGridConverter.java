package capsrock.location.grid.util;

import capsrock.location.grid.dto.Grid;

//https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16 참고
public final class GpsToGridConverter {

    // 지구 반경(km)
    private static final double RE = 6371.00877;
    // 격자 간격(km)
    private static final double GRID = 5.0;
    // 투영 위도1(degree)
    private static final double SLAT1 = 30.0;
    // 투영 위도2(degree)
    private static final double SLAT2 = 60.0;
    // 기준점 경도(degree)
    private static final double OLON = 126.0;
    // 기준점 위도(degree)
    private static final double OLAT = 38.0;
    // 기준점 X좌표(GRID)
    private static final double XO = 43.0;
    // 기준점 Y좌표(GRID)
    private static final double YO = 136.0;

    public static Grid convertToGrid(double lat, double lng) {

        final double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double tanned = Math.tan(Math.PI * 0.25 + slat1 * 0.5);

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / tanned;
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = tanned;
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;


        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lng * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        return new Grid((int) Math.floor(ra * Math.sin(theta) + XO + 0.5),
                (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5));
    }


}
