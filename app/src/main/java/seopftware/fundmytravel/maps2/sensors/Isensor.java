package seopftware.fundmytravel.maps2.sensors;

/**
 * Created by MSI on 2018-01-31.
 */

public interface Isensor {

    public boolean isSupport();

    public void on(int speed);

    public void off();

    public float getMaximumRange();

}
