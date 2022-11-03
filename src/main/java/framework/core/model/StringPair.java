package framework.core.model;

import java.util.Objects;

public class StringPair {

    private final  String first;
    private final  String second;
    private int hashCode;

    public StringPair(String first, String second){
        this.first = first;
        this.second = second;
        this.hashCode = Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StringPair))
           return false;

        StringPair sp = (StringPair) obj;
        return sp.getFirst().equals(this.first) && sp.getSecond().equals(this.second);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public int getHashCode() {
        return hashCode;
    }
}
