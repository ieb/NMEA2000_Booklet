package uk.co.tfd.kindle.nmea2000.simulator;

import java.util.HashSet;
import java.util.Set;

public class PGNFilter {
    private Set<Integer> pgnFilterList = new HashSet<>();

    public boolean shouldSend(long pgn) {
        return pgnFilterList.size() == 0 || pgnFilterList.contains(-1) || pgnFilterList.contains(pgn);
    }

    public void setPgnFilterList(Set<Integer> pgnFilterList) {
        this.pgnFilterList = pgnFilterList;
    }

    public Set<Integer> getPgnFilterList() {
        return pgnFilterList;
    }
}
