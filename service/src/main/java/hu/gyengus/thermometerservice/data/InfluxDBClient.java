package hu.gyengus.thermometerservice.data;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;

import hu.gyengus.thermometerservice.domain.Temperature;

public class InfluxDBClient implements DBClient {
    private InfluxDB influxDB;
    private final String databaseURL;
    private final String username;
    private final String password;

    public InfluxDBClient(final String databaseURL, final String username, final String password) {
        this.databaseURL = databaseURL;
        this.username = username;
        this.password = password;
        
        this.connect();
    }
    
    private void connect() {
        if (!this.databaseURL.isEmpty() && !this.username.isEmpty()) {
            this.influxDB = InfluxDBFactory.connect(this.databaseURL, this.username, this.password);
            this.influxDB.disableBatch();
        }
        this.influxDB = null;
    }

    public void sendTemperature(final Temperature temperature) {
        if (this.influxDB != null && temperature != null) {
            if (!this.isConnected()) {
                this.connect();
            }
            Point point = Point.measurement(temperature.getMeasurement()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("value", temperature.getTemperature()).build();
            this.influxDB.write(point);
        }
    }
    
    private boolean isConnected() {
        Pong response = this.influxDB.ping();
        return response.isGood();
    }

    public void destroy() {
        if (this.influxDB != null) {
            this.influxDB.close();
        }
    }
}
