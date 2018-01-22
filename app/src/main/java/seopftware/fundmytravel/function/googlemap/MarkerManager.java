package seopftware.fundmytravel.function.googlemap;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of collections of markers on the map. Delegates all Marker-related events to each
 * collection's individually managed listeners.
 * <p/>
 * All marker operations (adds and removes) should occur via its collection class.
 * That is, don't add a marker via a collection, then remove it via Marker.remove()
 */

public class MarkerManager implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.InfoWindowAdapter {

    private final GoogleMap mMap;

    private final Map<String, Collection> mNamedCollections = new HashMap<String, Collection>();
    private final Map<Marker, Collection> mAllMarkers = new HashMap<Marker, Collection>();

    // 생성자
    public MarkerManager(GoogleMap map) {
        this.mMap = map;
    }

    public Collection newCollection() {

        return new Collection();
    }

    /**
     * Create a new named collection, which can later be looked up by {@link #getCollection(String)}
     * @param id a unique id for this collection.
     * */
    public Collection newCollection(String id) {

        if(mNamedCollections.get(id) !=null) {

            throw new IllegalArgumentException("collection id is not unique: " + id);
        }

        Collection collection = new Collection();
        mNamedCollections.put(id, collection);
        return collection;
    }

    /**
     * Gets a named collection that was created by {@link #newCollection(String)}
     * @param id the unique id for this collection.
     **/

    public Collection getCollection(String id) {
        return mNamedCollections.get(id);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Collection collection = mAllMarkers.get(marker);

        if(collection !=null && collection.mInfoWindowAdapter !=null) {
            return collection.mInfoWindowAdapter.getInfoWindow(marker);
        }

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if(collection != null && collection.mInfoWindowAdapter !=null) {
            return collection.mInfoWindowAdapter.getInfoContents(marker);
        }

        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if(collection !=null && collection.mInfoWindowClickListener !=null) {
            collection.mInfoWindowClickListener.onInfoWindowClick(marker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if(collection != null && collection.mMarkerClickListener !=null) {
            return collection.mMarkerClickListener.onMarkerClick(marker);
        }

        return false;
    }

    // 마커 클릭 후 드래그
    @Override
    public void onMarkerDragStart(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if(collection !=null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDragStart(marker);
        }
    }

    // 마커 클릭 후 드래그
    @Override
    public void onMarkerDrag(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDrag(marker);
        }
    }

    // 마커 클릭 후 드래그
    @Override
    public void onMarkerDragEnd(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDragEnd(marker);
        }
    }


    /**
     * Removes a marker from its collection.
     *
     * @param marker the marker to remove.
     * @return true if the marker was removed.
     **/

    public boolean remove(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        return collection != null && collection.remove(marker);
    }

    public class Collection {
        private final Set<Marker> mMarkers = new HashSet<Marker>();
        private GoogleMap.OnInfoWindowClickListener mInfoWindowClickListener;
        private GoogleMap.OnMarkerClickListener mMarkerClickListener;
        private GoogleMap.OnMarkerDragListener mMarkerDragListener;
        private GoogleMap.InfoWindowAdapter mInfoWindowAdapter;

        public Collection() {

        }

        // 구글맵에 마커 추가하기
        public Marker addMarker(MarkerOptions opts) {
            Marker marker = mMap.addMarker(opts);
            mMarkers.add(marker);
            mAllMarkers.put(marker, Collection.this); // Map에 marker 담아두기
            return marker;
        }

        // 구글맵에 마커 제거하기
        public boolean remove(Marker marker) {
            if(mMarkers.remove(marker)) {
                mAllMarkers.remove(marker);
                marker.remove();
                return true;
            }
            return false;
        }

        // 마커 모두 지우기 (아마 쓸일 없을 듯!)
        public void clear() {
            for (Marker marker : mMarkers) {
                marker.remove();
                mAllMarkers.remove(marker);
            }

            mMarkers.clear();
        }

        public java.util.Collection<Marker> gerMarkers() {
            return Collections.unmodifiableCollection(mMarkers);
        }

        public void setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener infoWindowClickListener) {
            mInfoWindowClickListener = infoWindowClickListener;
        }

        public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener markerClickListener) {
            mMarkerClickListener = markerClickListener;
        }

        public void setOnMarkerDragListener(GoogleMap.OnMarkerDragListener markerDragListener) {
            mMarkerDragListener = markerDragListener;
        }

        public void setOnInfoWindowAdapter(GoogleMap.InfoWindowAdapter infoWindowAdapter) {
            mInfoWindowAdapter = infoWindowAdapter;
        }



    }



}
