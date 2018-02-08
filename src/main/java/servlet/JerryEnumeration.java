package servlet;

import java.util.Enumeration;
import java.util.Iterator;

public class JerryEnumeration<T> implements Enumeration<T> {

    private Iterator<T> iterator;

    public JerryEnumeration(){
        this.iterator = new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }

            @Override
            public void remove() {
            }
        };
    }

    public JerryEnumeration(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}