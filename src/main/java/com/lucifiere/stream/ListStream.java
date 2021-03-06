package com.lucifiere.stream;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.lucifiere.funtion.BinaryOperator;
import com.lucifiere.funtion.Consumer;
import com.lucifiere.funtion.Function;
import com.lucifiere.funtion.Predicate;

import java.util.*;

/**
 * LIST流
 *
 * @author wuhuilin
 * @date 2020-08-05.
 */
public class ListStream<T> implements Stream<T> {

    private final List<T> innerList;

    public ListStream(List<T> innerList) {
        this.innerList = Lists.newArrayList(innerList);
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        if (isEmpty()) {
            return emptyStream();
        }
        Iterator<T> it = innerList.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (!predicate.test(t)) {
                it.remove();
            }
        }
        return this;
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        List<R> rList = new ArrayList<>();
        for (T t : innerList) {
            rList.add(mapper.apply(t));
        }
        return Streams.of(rList);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, List<? extends R>> mapper) {
        if (isEmpty()) {
            return emptyStream();
        }
        List<R> rList = new ArrayList<>();
        for (T t : innerList) {
            rList.addAll(mapper.apply(t));
        }
        return Streams.of(rList);
    }

    @Override
    public Stream<T> distinct() {
        if (isEmpty()) {
            return emptyStream();
        }
        Set<T> set = new HashSet<>(innerList);
        return Streams.of(new ArrayList<>(set));
    }

    @Override
    public Stream<T> sorted() {
        Optional<T> optional = this.findAny();
        if (optional.isPresent() && optional.get() instanceof Comparable) {
            List<Comparable> compList = (List<Comparable>) innerList;
            Collections.sort(compList);
        }
        return this;
    }

    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
        if (isEmpty()) {
            return emptyStream();
        }
        Collections.sort(innerList, comparator);
        return this;
    }

    @Override
    public Stream<T> limit(int maxSize) {
        if (innerList.size() <= maxSize) {
            return this;
        }
        return Streams.of(innerList.subList(0, maxSize));
    }

    @Override
    public Stream<T> skip(int n) {
        if (innerList.size() > n) {
            return emptyStream();
        }
        return Streams.of(innerList.subList(n, innerList.size()));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (T t : innerList) {
            action.accept(t);
        }
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        for (T t : innerList) {
            identity = accumulator.apply(identity, t);
        }
        return identity;
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        if (isEmpty()) {
            return Optional.absent();
        }
        T min = innerList.get(0);
        for (T t : innerList) {
            if (comparator.compare(t, min) < 0) {
                min = t;
            }
        }
        return Optional.of(min);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        if (isEmpty()) {
            return Optional.absent();
        }
        T max = innerList.get(0);
        for (T t : innerList) {
            if (comparator.compare(t, max) > 0) {
                max = t;
            }
        }
        return Optional.of(max);
    }

    @Override
    public long count() {
        return innerList.size();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        if (isEmpty()) {
            return false;
        }
        for (T t : innerList) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        if (isEmpty()) {
            return true;
        }
        for (T t : innerList) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        if (isEmpty()) {
            return true;
        }
        for (T t : innerList) {
            if (predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<T> findFirst() {
        if (isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(innerList.get(0));
    }

    @Override
    public Optional<T> findAny() {
        if (isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(innerList.get(0));
    }

    @Override
    public List<T> toList() {
        return innerList;
    }

    @Override
    public Set<T> toSet() {
        return new HashSet<>(innerList);
    }

    @Override
    public <K> Map<K, List<T>> singleGroupBy(Function<T, K> function) {
        Map<K, List<T>> map = new HashMap<>();
        for (T value : innerList) {
            K key = function.apply(value);
            if (null == map.get(key)) {
                map.put(key, new ArrayList<T>());
            }
            map.get(key).add(value);
        }
        return map;
    }

    @Override
    public <K> Map<K, T> groupBy(Function<T, K> function) {
        Map<K, T> map = new HashMap<>();
        for (T value : innerList) {
            map.put(function.apply(value), value);
        }
        return map;
    }

    private boolean isEmpty() {
        return null == innerList || innerList.size() <= 0;
    }

    public static <T> Stream<T> emptyStream() {
        return (Stream<T>) Streams.of(Collections.emptyList());
    }
}
