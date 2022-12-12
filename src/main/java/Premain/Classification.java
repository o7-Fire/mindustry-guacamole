package Premain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * {
 * "status": "SUCCESS",
 * "error_code": "",
 * "message": "OK",
 * "data": [
 * {
 * "url": "SIPI_Jelly_Beans_4.1.07.tiff.jpg",
 * "hash": "e18f8618f8663732ccf7deebac1200e52a16d77f9d618943030a260322427fb9",
 * "hex": "e18f8618f8663732ccf7deebac1200e52a16d77f9d618943030a260322427fb9",
 * "cache": {
 * "data": {
 * "Digital": 0.9983923089457676,
 * "Neutral": 0.9990656971931458,
 * "Drawing": 0.0006733882473781705,
 * "Porn": 0.0001992419856833294,
 * "Hentai": 0.00004998780423193239,
 * "Sexy": 0.00001167691243608715
 * },
 * "model": {
 * "url": "Default",
 * "size": "Default"
 * },
 * "timestamp": 1669705646169,
 * "average_time": 0,
 * "time": 2,
 * "hex": "e18f8618f8663732ccf7deebac1200e52a16d77f9d618943030a260322427fb9",
 * "cache": "File"
 * }
 * },
 * {
 * "url": "Screenshot from 2022-11-29 13-39-52.png",
 * "hash": "bea9cd685a210f137776512117895c498ef031c7cb3ca508910853ed058569e7",
 * "hex": "bea9cd685a210f137776512117895c498ef031c7cb3ca508910853ed058569e7",
 * "cache": null
 * }
 * ]
 * }
 */
//create gson class from this

public class Classification implements Serializable {
    public String status;
    public String errorCode;
    public String message;
    @SerializedName("data")
    public List<Data> data;//multiple images ?

    public void sort() {
        for (Data d : data) {
            d.sort();
        }
    }

    public static class Data implements Serializable {
        @SerializedName("data")
        public List<Map<String, Double>> data;//multiple frames ?
        @SerializedName("model")
        private Model model;
        private long timestamp;
        private String hex;
        private long time;
        private String cache;
        private double averageTime;

        public void sort() {
            List<Map<String, Double>> sorted = new LinkedList<>();
            for (Map<String, Double> unsortedMap : data) {
                Map<String, Double> sortedMap =
                        unsortedMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (e1, e2) -> e1, LinkedHashMap::new));
                sorted.add(sortedMap);
            }
            data = sorted;
        }


    }

    public static class Model implements Serializable {
        public String url;
        public String size;
    }
}
