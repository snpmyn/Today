package widget.location.kit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.zsp.core.R;

import java.util.Locale;

import util.mmkv.MmkvKit;
import widget.location.listener.LocationKitListener;
import widget.location.value.LocationConstant;
import widget.toast.ToastKit;

/**
 * Created on 2019/4/24.
 *
 * @author 郑少鹏
 * @desc 定位配套原件
 */
public class LocationKit {
    /**
     * 定位管理器
     */
    private LocationManager locationManager;
    /**
     * 定位监听
     */
    private LocationListener locationListener;

    public static LocationKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 执行
     *
     * @param appCompatActivity   活动
     * @param locationKitListener 定位配套原件监听
     */
    public void execute(AppCompatActivity appCompatActivity, LocationKitListener locationKitListener) {
        startLocation(appCompatActivity, locationKitListener);
    }

    /**
     * 开始定位
     *
     * @param appCompatActivity   活动
     * @param locationKitListener 定位配套原件监听
     */
    private void startLocation(@NonNull AppCompatActivity appCompatActivity, LocationKitListener locationKitListener) {
        locationManager = (LocationManager) appCompatActivity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {
            // 经度
            double longitude = location.getLongitude();
            // 纬度
            double latitude = location.getLatitude();
            // 精度
            float accuracy = location.getAccuracy();
            String locationInfo = String.format(Locale.CHINA, "经度 %1$.2f，纬度 %2$.2f，精度 %3$.2f 米", longitude, latitude, accuracy);
            MmkvKit.defaultMmkv().encode(LocationConstant.LOCATION_INFO, locationInfo);
            if (null != locationKitListener) {
                locationKitListener.onLocationChanged(locationInfo);
            }
        };
        // 是否允许 GPS 定位
        boolean areGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 是否允许网络定位
        boolean areNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (areGPSEnabled) {
            // 权限在前面已申请
            // 此处因不设置报错
            if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        } else if (areNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        } else {
            ToastKit.showShort(appCompatActivity.getString(R.string.needTurnOnLocationServices));
        }
    }

    /**
     * 移除更新
     */
    public void removeUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    private static final class InstanceHolder {
        static final LocationKit INSTANCE = new LocationKit();
    }
}