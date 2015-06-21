package queue2;

public class State {
    public static class Active {
        private boolean value;

        public Active(boolean value) {
            this.value = value;
        }

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    private volatile Active active0, active1;
    private volatile int value;
    private volatile Integer offer;
    private volatile int offerer;

    public State(){
        this(new Active(false), new Active(false), -1, null, 0);
    }

    public State(Active active0, Active active1, int value, Integer offer, int offerer) {
        this.active0 = active0;
        this.active1 = active1;
        this.value = value;
        this.offer = offer;
        this.offerer = offerer;
    }

    public Active getActive0() {
        return active0;
    }

    public Active getActive1() {
        return active1;
    }

    public int getValue() {
        return value;
    }

    public Integer getOffer() {
        return offer;
    }

    public int getOfferer() {
        return offerer;
    }
}
