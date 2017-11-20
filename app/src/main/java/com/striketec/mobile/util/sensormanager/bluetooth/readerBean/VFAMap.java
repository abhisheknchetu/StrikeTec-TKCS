package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * VFAMap extends ConcurrentSkipListMap<Integer, VFA>
 *
 * @author 1014
 */
public class VFAMap extends ConcurrentSkipListMap<Double, VFA> {

    private int maxSize;
    private static final long serialVersionUID = 1L;

    public VFAMap() {
        this.maxSize = PunchDetectionConfig.getInstance().getVfaBufferSize();
    }

    public VFAMap(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * add an item in VFAMap with key & Value pair.
     */
    @Override
    public VFA put(Double key, VFA value) {
        if (this.size() >= this.maxSize) {
            try {
                this.pollFirstEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.put(key, value);
    }

    /**
     * Get max value from the collection.
     *
     * @return
     */
    public VFA getMaxValueOfVFA() {
        if (this.size() == 0) {
            return new VFA();
        }
        return Collections.max(this.values(), VFA.VFAVelocityComparator);
    }

    public void removeContentBeforeTime(double punchTime) {
        Map<Double, VFA> headMap = this.headMap(punchTime, true);
        for (Entry<Double, VFA> obj : headMap.entrySet()) {
            this.remove(obj.getKey());
        }
    }

}
