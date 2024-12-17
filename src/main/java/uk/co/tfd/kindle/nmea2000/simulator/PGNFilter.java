package uk.co.tfd.kindle.nmea2000.simulator;

import java.util.HashSet;
import java.util.Set;

public class PGNFilter {
    private Set<Long> pgnFilterList = new HashSet<>();
    private Set<Long> rejects = new HashSet<>();
    private Set<Long> accepted = new HashSet<>();

    public boolean shouldSend(long pgn) {
        if ( pgnFilterList.size() == 0 || pgnFilterList.contains(-1) || pgnFilterList.contains(pgn) ) {
            accepted.add(pgn);
            return true;
        }
        rejects.add(pgn);
        return false;
    }

    public void setPgnFilterList(Set<Long> pgnFilterList) {
        this.pgnFilterList = pgnFilterList;
    }

    public Set<Long> getPgnFilterList() {
        return pgnFilterList;
    }

    public void reset() {
        accepted.clear();
        rejects.clear();
    }

    @Override
    public String toString() {
        return "Filter:"+pgnFilterList+" accept:"+accepted+" rejects:"+rejects;
    }
}
