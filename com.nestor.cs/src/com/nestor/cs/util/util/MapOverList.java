//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MapOverList.java (javan.util)
// Utworzono 2005-04-12 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MapOverList {

    public class InnerList implements List {

        private List list = null;

        private InnerList(List l) {
            this.list = l;
        }

        public void add(int index, Object value) {
            this.list.add(index, value);
        }

        public boolean add(Object value) {
            return this.list.add(value);
        }

        public boolean addAll(Collection values) {
            return this.list.addAll(values);
        }

        public boolean addAll(int index, Collection values) {
            return this.list.addAll(index, values);
        }

        public void clear() {
            this.list.clear();
            MapOverList.this.innerMap.clear();
        }

        public boolean contains(Object value) {
            return this.list.contains(value);
        }

        public boolean containsAll(Collection values) {
            return this.list.containsAll(values);
        }

        @Override
        public boolean equals(Object obj) {
            return MapOverList.this.innerMap.equals(obj);
        }

        public Object get(int index) {
            return this.list.get(index);
        }

        @Override
        public int hashCode() {
            return this.list.hashCode();
        }

        public int indexOf(Object value) {
            return this.list.indexOf(value);
        }

        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        public Iterator iterator() {
            return this.list.iterator();
        }

        public int lastIndexOf(Object value) {
            return this.list.lastIndexOf(value);
        }

        public ListIterator listIterator() {
            return null;
        }

        public ListIterator listIterator(int index) {
            return null;
        }

        public Object remove(int index) {
            Object value = this.list.remove(index);
            if (MapOverList.this.innerMap.containsValue(value)) {
                MapOverList.this.innerMap.remove(MapOverList.this.innerMap.getFirstKeyForValue(value));
            }
            return value;
        }

        public boolean remove(Object value) {
            if (MapOverList.this.innerMap.containsValue(value)) {
                MapOverList.this.innerMap.remove(MapOverList.this.innerMap.getFirstKeyForValue(value));
            }
            return this.list.remove(value);
        }

        public boolean removeAll(Collection values) {
            Iterator iter = values.iterator();
            while (iter.hasNext()) {
                Object value = iter.next();
                if (MapOverList.this.innerMap.containsValue(value)) {
                    MapOverList.this.innerMap.remove(MapOverList.this.innerMap.getFirstKeyForValue(value));
                }
            }
            return this.list.removeAll(values);
        }

        public boolean retainAll(Collection values) {
            Iterator iter = MapOverList.this.innerMap.values().iterator();
            while (iter.hasNext()) {
                Object value = iter.next();
                if (!values.contains(value)) {
                    MapOverList.this.innerMap.remove(MapOverList.this.innerMap.getFirstKeyForValue(value));
                }
            }
            return this.list.retainAll(values);
        }

        public Object set(int index, Object value) {
            Object oldObject = get(index);
            this.list.set(index, value);
            if (oldObject != null) {
                MapOverList.this.innerMap.remove(MapOverList.this.innerMap.getFirstKeyForValue(oldObject));
            }
            return oldObject;
        }

        public int size() {
            return this.list.size();
        }

        public List subList(int start, int end) {
            return this.list.subList(start, end);
        }

        public Object[] toArray() {
            return this.list.toArray();
        }

        public Object[] toArray(Object[] array) {
            return this.list.toArray(array);
        }

        @Override
        public String toString() {
            return this.list.toString();
        }
    }

    public class InnerMap implements Map {

        private Map map = null;

        private InnerMap(Map m) {
            this.map = m;
        }

        public void clear() {
            MapOverList.this.innerList.clear();
            this.map.clear();
        }

        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.map.containsValue(value);
        }

        public Set entrySet() {
            return this.map.entrySet();
        }

        @Override
        public boolean equals(Object obj) {
            Collection ovalues = null;
            if (obj instanceof Map) {
                Map m = (Map) obj;
                if (!m.keySet().containsAll(this.map.keySet())) {
                    return false;
                }
                if (!this.map.keySet().containsAll(m.keySet())) {
                    return false;
                }
                ovalues = m.values();
            }
            if (obj instanceof Collection) {
                ovalues = (Collection) obj;
            }
            if (ovalues != null) {
                if (!MapOverList.this.innerList.containsAll(ovalues)) {
                    return false;
                }
                if (!ovalues.containsAll(MapOverList.this.innerList)) {
                    return false;
                }
                return true;
            }
            return false;
        }

        public Object get(Object key) {
            return this.map.get(key);
        }

        private Object getFirstKeyForValue(Object value) {
            Iterator miter = this.map.entrySet().iterator();
            while (miter.hasNext()) {
                Map.Entry entry = (Map.Entry) miter.next();
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public Set keySet() {
            return this.map.keySet();
        }

        public Object put(Object key, Object value) {
            MapOverList.this.innerList.add(value);
            return this.map.put(key, value);
        }

        public void putAll(Map m) {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }

        public Object remove(Object key) {
            Object obj = this.map.remove(key);
            MapOverList.this.innerList.remove(obj);
            return obj;
        }

        public int size() {
            return this.map.size();
        }

        @Override
        public String toString() {
            return this.map.toString();
        }

        public Collection values() {
            return this.map.values();
        }
    }

    private InnerList innerList = null;

    private InnerMap innerMap = null;

    public MapOverList() {
        super();
        this.innerList = new InnerList(new Vector());
        this.innerMap = new InnerMap(new LinkedHashMap());
    }

    public MapOverList(List l, Map m) {
        super();
        this.innerList = new InnerList(l);
        this.innerMap = new InnerMap(m);
    }

    public List asList() {
        return this.innerList;
    }

    public Map asMap() {
        return this.innerMap;
    }

}