package testing;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainTests extends TestCase {

    public void test() {
        final long a = System.currentTimeMillis();
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));

            System.out.println("Passed: " + TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - a)));
            Assert.assertTrue((System.currentTimeMillis() - a) > TimeUnit.SECONDS.toMillis(30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void test2() {
        final Set<MyObject> unsorted = Sets.newHashSet();
        unsorted.add(new MyObject(1));
        unsorted.add(new MyObject(5));
        unsorted.add(new MyObject(12));
        unsorted.add(new MyObject(1555));

        final Set<MyObject> sorted = Sets.newLinkedHashSet();
        sorted.addAll(unsorted.stream()
                .sorted(Comparator.comparingLong(MyObject::getBalance).reversed())
                .limit(2)
                .collect(Collectors.toSet()));

        sorted.forEach(myObject -> {
            System.out.println(myObject.getBalance());
        });
        org.junit.Assert.assertTrue(sorted.size() == 2);
    }
    public class MyObject {
        private final long balance;

        public MyObject(final long balance) {
            this.balance = balance;
        }

        public long getBalance() {
            return balance;
        }
    }
    //-----------------------------------------------------------------------//
    //                                                                       //
    //                          JOHAN'S TESTS                                //
    //                                                                       //
    //-----------------------------------------------------------------------//
    public void test3() throws InterruptedException {
        long a = System.currentTimeMillis();
        Thread.sleep(10000);
        System.out.println(((System.currentTimeMillis() - a) / 1000));
    }
    String format(double number){
        Preconditions.checkNotNull(number,"Number may not be null");
        if (number < 1000){
            return String.valueOf(number);
        }else if (number < 999999) {
            return String.format("%.2fK%n",number / 1000);
        } else if (number < 999999999) {
            return String.format("%.2fM%n",number / 1000000);
        }else if (number > 99999999){
            return String.format("%.2fB%n",number / 1000000000);
        }else return String.format("%.2fT%n",number / 1000000000 * 100);
    }
    public void test4(){
        double i = 100;
        System.out.println(format(i));
        System.out.println(format(1000000000 * 100));


    }
}
