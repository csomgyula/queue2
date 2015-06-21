package queue2;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Sequence2GTest {

    @Test
    public void test(){
        Sequence2G sequence = new Sequence2G();
        TestThread test0, test1;

        test0 = new TestThread(sequence, 100);
        test1 = new TestThread(sequence, 100);

        test0.start();
        test1.start();

        List<Integer> list1 = null, list2 = null;
        try {
            test0.join();
            test1.join();

            dumpResults(0, test0.getResults());
            dumpResults(1, test1.getResults());

            list1 = test0.getIntegers();
            list2 = test1.getIntegers();

            for (int integer : list2){
                System.out.println(integer);
                 Assert.assertFalse(list1.contains(integer));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class TestThread extends Thread{
        private List<Sequence2G.Result> results;
        private List<Integer> integers;
        private Sequence2G sequence;
        private int rounds;

        public TestThread(Sequence2G sequence, int rounds) {
            this.results = new ArrayList<Sequence2G.Result>();
            this.integers = new ArrayList<Integer>();
            this.sequence = sequence;
            this.rounds = rounds;
        }

        public void run(){
            sequence.attach();

            int currentRound = 0;

            while(currentRound < rounds){
                Sequence2G.Result result = sequence.next();
                assert !result.actualState.getActive0().isValue()
                        || !result.actualState.getActive1().isValue();
                this.results.add(result);
                integers.add(result.value);
                currentRound++;
            }
        }

        public List<Sequence2G.Result> getResults() {
            return results;
        }

        public List<Integer> getIntegers() {
            return integers;
        }
    }


    protected static void dumpResults(int id, List<Sequence2G.Result> results){
        System.out.println("\nthread " + id + " r.s: " + results.size());
        for (Sequence2G.Result result : results){
            // System.out.print(result);
            System.out.print(" ps.v: "  + result.previousState.getValue());
            System.out.print(" ps.o: "  + result.previousState.getOffer());
            System.out.print(" ps.or: " + result.previousState.getOfferer());

            System.out.print(" as.v: "  + result.actualState.getValue());
            System.out.print(" as.o: "  + result.actualState.getOffer());
            System.out.print(" as.or: " + result.actualState.getOfferer());

            System.out.print(" oa:" + result.otherWasActive);
            System.out.print(" v:" + result.value);
            System.out.print(" w:" + result.won);

            System.out.println();
        }
    }
}
