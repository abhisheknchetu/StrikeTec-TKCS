package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

import com.efd.punch.PunchVFAData;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * PunchDetectedMap extends ConcurrentSkipListMap<Integer, String>
 *
 * @author saurabh.nath
 */
public class PunchDetectedMap extends ConcurrentSkipListMap<Double, PunchVFAData> {

    private int maxSize;
    private static final long serialVersionUID = 1L;

    public PunchDetectedMap() {
        this.maxSize = PunchDetectionConfig.getInstance().getVfaBufferSize();
    }

    public PunchDetectedMap(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * add an item in VFAMap with key & Value pair.
     */
    @Override
    public PunchVFAData put(Double key, PunchVFAData value) {
        if (this.size() >= this.maxSize) {
            try {
                this.pollFirstEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.put(key, value);
    }

    public void removeContentBeforeTime(double punchTime) {
        Map<Double, PunchVFAData> headMap = this.headMap(punchTime, true);
        for (Entry<Double, PunchVFAData> obj : headMap.entrySet()) {
            this.remove(obj.getKey());
        }
    }

}
