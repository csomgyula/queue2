package queue2;

import java.util.concurrent.atomic.AtomicReference;

/**
 * TODO: handle circular sequences
 */
public class Sequence2 {
    private final AtomicReference<State> state;

    public Sequence2() {
        state = new AtomicReference<State>(new State());
    }

    public int next() {
        int myId = getMyId();

        // 1 Actual state
        State actualState = state.get(); // can it be cached? not the state but its content...

        // 2 Handle activity

        // 2.1 Mark myself as active
        activateMe(myId, actualState);

        // 2.2 Is other thread active
        boolean otherIsActive = isOtherActive(myId, actualState);

        // 3 Determine next number
        int nextValue;
        Integer nextOffer = null;

        // 3.1 Other thread is not active
        if (!otherIsActive) {
            nextValue = actualState.getValue() + 1;
        }

        // 3.2 Other thread is active
        else {
            // 3.2.1 There was no previous offer
            if (!wasTherePreviousOffer(myId, actualState)) {
                nextValue = actualState.getValue() + 2;
                nextOffer = actualState.getValue() + 1;
            }

            // 3.2.2 There was previous offer
            else {
                nextValue = actualState.getValue() + 1;
                nextOffer = actualState.getOffer();

                // 3.2.2.1 In case of overflow (think of ring buffers and so on)
                if (nextValue == nextOffer){
                    nextValue++;
                }
            }
        }

        // 3.3 Init new state
        State newState = newState(myId, actualState, nextValue, nextOffer);

        // 4 Try to set the new state
        boolean setSucceeded = state.compareAndSet(actualState, newState);

        // 5 use my number if I won and accept its offer otherwise

        // 5.1 use my number if I won
        if (setSucceeded){
            return nextValue;
        }

        // 5.2 accept its offer otherwise
        else{
            actualState = state.get();
            int itsValue = actualState.getValue();
            Integer itsOffer = actualState.getOffer();
            return itsOffer != null ? itsOffer : itsValue + 1;
            // FIXME:
            // if myValue was itsValue + 1, try to incerement state.value
            // nullify offer if any
        }
    }

    // -- ------------------------------------------------------------------------------------------
    // -- thread id handling
    private volatile Long thread0id, thread1id;

    public synchronized void attach() {
        if (thread0id == null) {
            thread0id = Thread.currentThread().getId();
        } else if (thread1id == null) {
            thread1id = Thread.currentThread().getId();
        } else {
            throw new IllegalStateException("Sequence is already used by 2 threads");
        }
    }

    public synchronized void detach() {
        long id = Thread.currentThread().getId();
        if (thread0id == id) {
            thread0id = null;
        } else if (thread1id == id) {
            thread1id = null;
        } else {
            throw new IllegalStateException("Thread is not attached to this sequence");
        }
    }

    public boolean isAttached() {
        long id = Thread.currentThread().getId();
        return thread0id == id || thread1id == id;
    }

    protected int getMyId() {
        long id = Thread.currentThread().getId();
        if (thread0id == id) {
            return 0;
        } else if (thread1id == id) {
            return 1;
        } else {
            throw new IllegalStateException("Thread is not attached to this sequence");
        }
    }

    // -- ------------------------------------------------------------------------------------------
    // -- state handling
    protected void activateMe(int myId, State state){
        switch (myId) {
            case 0: state.getActive0().setValue(true); break;
            case 1: state.getActive1().setValue(true); break;
            default:
                throw new IllegalArgumentException("Illegal thread index: " + myId);
        }
    }

    protected boolean isOtherActive(int myId, State state) {
        switch (myId) {
            case 0:
                return state.getActive1().isValue();
            case 1:
                return state.getActive0().isValue();
            default:
                throw new IllegalArgumentException("Illegal thread index: " + myId);
        }
    }

    protected boolean wasTherePreviousOffer(int myId, State state) {
        return state.getOfferer() == myId && state.getOffer() != null;
    }

    protected State newState(int myId, State prev, int value, Integer offer){
        State.Active iAmInactive = new State.Active(false);
        switch (myId) {
            case 0:
                return new State(iAmInactive, prev.getActive1(), value, offer, myId);
            case 1:
                return new State(prev.getActive0(), iAmInactive, value, offer, myId);
            default:
                throw new IllegalArgumentException("Illegal thread index: " + myId);
        }
    }
}
