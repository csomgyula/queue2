package queue2;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Sequence2Test {

    @Test
    public void test(){
        Sequence2 sequence = new Sequence2();
        TestThread test1, test2;

        test1 = new TestThread(sequence, 100);
        test2 = new TestThread(sequence, 100);

        test1.start();
        test2.start();

        List<Integer> list1 = null, list2 = null;
        try {
            test1.join();
            test2.join();

            list1 = test1.getIntegers();
            list2 = test2.getIntegers();

            for (int integer : list2){
                System.out.println(integer);
                Assert.assertFalse(list1.contains(integer));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class TestThread extends Thread{
        private List<Integer> integers;
        private Sequence2 sequence;
        private int rounds;

        public TestThread(Sequence2 sequence, int rounds) {
            this.integers = new ArrayList<Integer>();
            this.sequence = sequence;
            this.rounds = rounds;
        }

        public void run(){
            sequence.attach();

            int currentRound = 0;

            while(currentRound < rounds){
                integers.add(sequence.next());
                currentRound++;
            }
        }

        public List<Integer> getIntegers() {
            return integers;
        }
    }
}
