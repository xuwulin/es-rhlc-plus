package com.xwl.esplus.core.toolkit;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

/**
 * 简单Stream实现
 *
 * @author xwl
 * @since 2022/3/16 17:33
 */
public class SimpleStream<T> {

    /**
     * 成员变量
     */
    private Collection<T> collection;

    /**
     * 构造方法
     *
     * @param collection
     */
    private SimpleStream(Collection<T> collection) {
        this.collection = collection;
    }

    /**
     * 工厂方法（静态方法）
     *
     * @param collection 参数
     * @param <T>        类上的泛型 T 不能配合静态方法使用，静态方法需要单独定义自己的泛型：public static <T>
     * @return
     */
    public static <T> SimpleStream<T> of(Collection<T> collection) {
        return new SimpleStream<>(collection);
    }

    /**
     * filter过滤方法
     *
     * @param predicate 参数：接收一个参数，返回一个bool值，使用JDK自带的Predicate，参数泛型和类上泛型保持一致
     * @return SimpleStream<T>  返回：返回结果能继续链式调用（.map()），所以应该是返回SimpleStream类型
     */
    public SimpleStream<T> filter(Predicate<T> predicate) {
        // 创建一个新的集合，原有的集合保证其不变性
        List<T> result = new ArrayList<>();
        for (T t : collection) {
            if (predicate.test(t)) {
                result.add(t);
            }
        }
        return new SimpleStream<>(result);
    }

    /**
     * map转换方法：把一种元素转换成另一种元素
     *
     * @param function 参数：接收一个参数，返回一个结果，使用JDK自带的Function，有两个泛型，参数类型和返回值类型（两个类型可以一样也可以不一样），参数泛型和类上泛型保持一致，返回值泛型可以是另一种类型，用U表示（可用任意字母表示）
     * @param <U>      Function的返回值类型，在类上没有定义，需要在方法上定义，public <U>
     * @return SimpleStream<U>  返回：返回结果能继续链式调用（.forEach()），所以应该是返回SimpleStream类型，并且泛型经过map的转换变更为U了
     */
    public <U> SimpleStream<U> map(Function<T, U> function) {
        // 创建一个新的集合，原有的集合保证其不变性，且泛型为U，因为转换后的元素类型可能和转换前的元素类型不是同一种
        List<U> result = new ArrayList<>();
        for (T t : collection) {
            // T就是原有集合中的元素类型，U是转换后的元素类型
            U u = function.apply(t);
            result.add(u);
        }
        return new SimpleStream<>(result);
    }

    /**
     * forEach消费方法
     *
     * @param consumer 参数：接收一个参数，没有返回，使用JDK自带的Consumer，参数泛型和类上泛型保持一致
     */
    public void forEach(Consumer<T> consumer) {
        for (T t : collection) {
            consumer.accept(t);
        }
    }

    /**
     * reduce合并方法
     *
     * @param o        初始值
     * @param operator 参数：入参和出参类型一致，使用JDK自带的BinaryOperator，参数泛型和类上泛型保持一致
     * @return
     */
    public T reduce(T o, BinaryOperator<T> operator) {
        // 上次的合并结果
        T p = o;
        for (T t : collection) {
            // t是本次遍历的元素
            p = operator.apply(p, t);
        }
        return p;
    }

    /**
     * collect收集方法：提供一个新的容器，将元素加入其中
     * 分析参数：需要一个集合，用于收集元素，还需要一个元素才能完成收集，即需要两个参数，不需要返回值
     *
     * @param supplier 参数：表示创建容器的操作，是变化的，由调用者决定，不需要参数，返回一个容器类型，使用JDK自带的Supplier<C>，泛型就是返回的容器类型C
     * @param consumer 参数：表示添加元素的操作，是变化的，由调用者决定，接收两个参数，没有返回值，使用JDK自带的BiConsumer<C, T>，第一个泛型C表示收集元素的集合，第二泛型T表示被收集的元素
     * @param <C> 返回值类型：是一个容器类型，是不确定的，需要在方法上定义出来使用 <C>
     * @return
     */
    public <C> C collect(Supplier<C> supplier, BiConsumer<C, T> consumer) {
        // 创建容器
        C c = supplier.get();
        for (T t : collection) {
            // 往容器中添加元素
            consumer.accept(c, t);
        }
        return c;
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        SimpleStream.of(list)
                .filter(x -> (x & 1) == 1)
                .map(x -> x * x)
                .forEach(System.out::println);

        System.out.println(SimpleStream.of(list).reduce(0, Integer::sum));
        System.out.println(SimpleStream.of(list).reduce(Integer.MAX_VALUE, Integer::min));
        System.out.println(SimpleStream.of(list).reduce(Integer.MIN_VALUE, Integer::max));



        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5, 1, 3, 6,7);
        HashSet<Integer> collect = SimpleStream.of(list2).collect(() -> new HashSet<>(), (set, t) -> set.add(t));
        HashSet<Integer> collect2 = SimpleStream.of(list2).collect(HashSet::new, HashSet::add);
        System.out.println(collect);
        System.out.println(collect2);

        StringBuilder collect3 = SimpleStream.of(list2).collect(StringBuilder::new, StringBuilder::append);
        System.out.println(collect3);

        StringJoiner collect4 = SimpleStream.of(list2)
                .collect(() -> new StringJoiner("-"), (joiner, t) -> joiner.add(String.valueOf(t)));
        System.out.println(collect4);

        StringJoiner collect5 = SimpleStream.of(list2)
                .map(t -> String.valueOf(t))
                .collect(() -> new StringJoiner("="), StringJoiner::add);
        System.out.println(collect5);

        HashMap<Integer, Integer> collect6 = SimpleStream.of(list2)
                .collect(HashMap::new, (map, t) -> {
                    if (!map.containsKey(t)) {
                        map.put(t, 1);
                    } else {
                        Integer v = map.get(t);
                        map.put(t, v + 1);
                    }
                });
        System.out.println(collect6);

        // map.computeIfAbsent()：
        // 如果key在map中不存在，将key连同新生成的value值存入map，并返回value
        // 如果key在map中存在，会返回此key上次的value值
        HashMap<Integer, AtomicInteger> collect7 = SimpleStream.of(list2)
                .collect(HashMap::new, (map, t) -> map.computeIfAbsent(t, k -> new AtomicInteger()).getAndIncrement());
        System.out.println(collect7);
    }


}
