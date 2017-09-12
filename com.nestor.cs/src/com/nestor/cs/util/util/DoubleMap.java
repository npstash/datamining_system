//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// DoubleMap.java (javan.util)
// Utworzono 2005-07-14 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.util.HashMap;
import java.util.Map;

public class DoubleMap {

    private class OneSideMap extends HashMap {

        private OneSideMap otherMap;

        @Override
        public synchronized Object put(Object key, Object value) {
            this.otherMap.putOrig(value, key);
            return putOrig(key, value);
        }

        private Object putOrig(Object key, Object value) {
            return super.put(key, value);
        }

        @Override
        public synchronized Object remove(Object key) {
            this.otherMap.removeOrig(get(key));
            return removeOrig(key);
        }

        private Object removeOrig(Object key) {
            return super.remove(key);
        }

        protected void setOtherMap(OneSideMap other) {
            this.otherMap = other;
        }
    }

    private OneSideMap end = new OneSideMap();

    private OneSideMap front = new OneSideMap();

    public DoubleMap() {
        this.front.setOtherMap(this.end);
        this.end.setOtherMap(this.front);
    }

    public Map getEndMap() {
        return this.end;
    }

    public Map getFrontMap() {
        return this.front;
    }
}