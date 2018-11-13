package cn.lds.amap.util;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.MapProjection;

/**
 * 包名： com.amap.navi.demo.util
 * <p>
 * 创建时间：2018/4/19
 * 项目名称：NaviDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：
 */
public class NaviUtil {

    public static float calculateDistance( NaviLatLng start, NaviLatLng end) {
        double x1 = start.getLongitude();
        double y1 = start.getLatitude();
        double x2 = end.getLongitude();
        double y2 = end.getLatitude();
        return AMapUtils.calculateLineDistance(new LatLng(y1, x1), new LatLng(y2, x2));
    }


    public static NaviLatLng getPointForDis( NaviLatLng sPt, NaviLatLng ePt, double dis) {
        double lSegLength = calculateDistance(sPt, ePt);
        NaviLatLng pt = new NaviLatLng();
        double preResult = dis / lSegLength;
        pt.setLatitude(((ePt.getLatitude() - sPt.getLatitude()) * preResult + sPt.getLatitude()));
        pt.setLongitude(((ePt.getLongitude() - sPt.getLongitude()) * preResult + sPt.getLongitude()));
        return pt;
    }

    /**
     * 根据经纬度计算需要偏转的角度
     *
     * @param startPoi
     * @param secondPoi
     * @return
     */
    public static float getRotate( NaviLatLng startPoi, NaviLatLng secondPoi) {
        float rotate = 0;
        try {
            IPoint point1 = new IPoint();
            IPoint point2 = new IPoint();
            MapProjection.lonlat2Geo(startPoi.getLongitude(), startPoi.getLatitude(), point1);
            MapProjection.lonlat2Geo(secondPoi.getLongitude(), secondPoi.getLatitude(), point2);
            double x1 = point1.x;
            double x2 = point2.x;
            double y1 = point1.y;
            double y2 = point2.y;
            rotate = (float) (Math.atan2(y2 - y1, x2 - x1) / Math.PI * 180);
            rotate = rotate + 90;
            return rotate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    public static final int MAXZOOMLEVEL = 20;
    public static final int PixelsPerTile = 256;
    public static final double MinLatitude = -85.0511287798;
    public static final double MaxLatitude = 85.0511287798;
    public static final double MinLongitude = -180;
    public static final double MaxLongitude = 180;
    public static final int EarthRadiusInMeters = 6378137;
    public static final int TileSplitLevel = 0;


    public static final double EarthCircumferenceInMeters = 2 * Math.PI
            * EarthRadiusInMeters;


    public static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    public static IPoint lonlat2Geo( double latitude, double longitude,
                                     int levelOfDetail) {
        IPoint rPnt = new IPoint();
        latitude = clip(latitude, MinLatitude, MaxLatitude) * Math.PI / 180;
        longitude = clip(longitude, MinLongitude, MaxLongitude) * Math.PI / 180;
        double sinLatitude = Math.sin(latitude);
        double xMeters = EarthRadiusInMeters * longitude;
        double lLog = Math.log((1 + sinLatitude) / (1 - sinLatitude));
        double yMeters = EarthRadiusInMeters / 2 * lLog;
        long numPixels = (long) PixelsPerTile << levelOfDetail;
        double metersPerPixel = EarthCircumferenceInMeters / numPixels;
        rPnt.x = (int) clip((EarthCircumferenceInMeters / 2 + xMeters)
                / metersPerPixel + 0.5, 0, numPixels - 1);
        long tmp = (long) (EarthCircumferenceInMeters / 2 - yMeters);
        rPnt.y = (int) clip((double) tmp / metersPerPixel + 0.5, 0,
                numPixels - 1);
        return rPnt;
    }



    public static String formatKM( int d) {
        if (d == 0) {
            return "0米";
        } else if (d < 100) {
            return d + "米";
        } else if ((100 <= d) && (d < 1000)) {
            return d + "米";
        } else if ((1000 <= d) && (d < 10000)) {
            return (d / 10) * 10 / 1000.0D + "公里";
        } else if ((10000 <= d) && (d < 100000)) {
            return (d / 100) * 100 / 1000.0D + "公里";
        }
        return (d / 1000) + "公里";
    }
}
