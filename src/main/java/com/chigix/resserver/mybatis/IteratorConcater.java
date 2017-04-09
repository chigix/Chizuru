package com.chigix.resserver.mybatis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 */
public abstract class IteratorConcater<T> implements Iterator<T> {

    private Iterator<T> currentIt = null;

    private final List<OnElementReturning> returningListeners = new ArrayList<>();

    @Override
    public boolean hasNext() {
        if (currentIt == null) {
            currentIt = nextIterator();
            if (!currentIt.hasNext()) {
                return false;
            }
        }
        if (currentIt.hasNext()) {
            return true;
        }
        currentIt = nextIterator();
        return currentIt.hasNext();
    }

    @Override
    public T next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        T ele = currentIt.next();
        returningListeners.forEach((listener) -> {
            listener.apply(ele);
        });
        return ele;
    }

    protected abstract Iterator<T> nextIterator();

    public IteratorConcater<T> addListener(OnElementReturning<T> l) {
        returningListeners.add(l);
        return this;
    }

    @FunctionalInterface
    public static interface OnElementReturning<T> {

        void apply(T e) throws NoSuchElementException;

    }

}
