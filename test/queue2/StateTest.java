package queue2;

import org.junit.Assert;
import org.junit.Test;

public class StateTest {


    @Test
    public void falses(){
        State state = new State();
        Assert.assertFalse(state.getActive0().isValue());
        Assert.assertFalse(state.getActive1().isValue());

        state.getActive0().setValue(true);
        Assert.assertTrue(state.getActive0().isValue());
        state.getActive0().setValue(false);
        Assert.assertFalse(state.getActive0().isValue());

        State state2 = new State(new State.Active(false), new State.Active(false), 0, 1, 0);
        Assert.assertFalse(state.getActive0().isValue());
        Assert.assertFalse(state.getActive1().isValue());


    }

}
