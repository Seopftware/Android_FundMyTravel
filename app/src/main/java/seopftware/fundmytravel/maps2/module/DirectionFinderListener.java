package seopftware.fundmytravel.maps2.module;

import java.util.List;

/**
 * Created by MSI on 2018-01-29.
 */

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Routes> route);

}
