package cn.lds.common.data.postbody;

import java.util.List;

/**
 * poi下发 body
 * Created by leadingsoft on 17/12/25.
 */

public class PoiPostBean {

    /**
     * necessityNodes : [{"necessityNodeDestination":"string","necessityNodeLatitude":0,"necessityNodeLongitude":0}]
     * poiNode : {"destinations":"string","latitude":0,"longitude":0}
     */

    private PoiNodeBean poiNode;
    private List<NecessityNodesBean> necessityNodes;

    public PoiNodeBean getPoiNode() {
        return poiNode;
    }

    public void setPoiNode(PoiNodeBean poiNode) {
        this.poiNode = poiNode;
    }

    public List<NecessityNodesBean> getNecessityNodes() {
        return necessityNodes;
    }

    public void setNecessityNodes(List<NecessityNodesBean> necessityNodes) {
        this.necessityNodes = necessityNodes;
    }

    public static class PoiNodeBean {
        /**
         * destinations : string
         * latitude : 0
         * longitude : 0
         */

        private String destinations;
        private double latitude;
        private double longitude;

        public String getDestinations() {
            return destinations;
        }

        public void setDestinations(String destinations) {
            this.destinations = destinations;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public static class NecessityNodesBean {
        /**
         * necessityNodeDestination : string
         * necessityNodeLatitude : 0
         * necessityNodeLongitude : 0
         */

        private String necessityNodeDestination;
        private double necessityNodeLatitude;
        private double necessityNodeLongitude;

        public String getNecessityNodeDestination() {
            return necessityNodeDestination;
        }

        public void setNecessityNodeDestination(String necessityNodeDestination) {
            this.necessityNodeDestination = necessityNodeDestination;
        }

        public double getNecessityNodeLatitude() {
            return necessityNodeLatitude;
        }

        public void setNecessityNodeLatitude(double necessityNodeLatitude) {
            this.necessityNodeLatitude = necessityNodeLatitude;
        }

        public double getNecessityNodeLongitude() {
            return necessityNodeLongitude;
        }

        public void setNecessityNodeLongitude(double necessityNodeLongitude) {
            this.necessityNodeLongitude = necessityNodeLongitude;
        }
    }
}
