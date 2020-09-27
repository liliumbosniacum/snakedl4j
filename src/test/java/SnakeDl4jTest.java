import com.liliumbosniacum.snakedl4j.network.Action;
import com.liliumbosniacum.snakedl4j.network.GameState;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class containing different tests from different classes.
 *
 * @author mirza
 */
public class SnakeDl4jTest {

    @Test
    public void testActionIndex() {
        assertThat(Action.MOVE_UP.getActionIndex()).isEqualTo(0);
        assertThat(Action.MOVE_RIGHT.getActionIndex()).isEqualTo(1);
        assertThat(Action.MOVE_DOWN.getActionIndex()).isEqualTo(2);
        assertThat(Action.MOVE_LEFT.getActionIndex()).isEqualTo(3);
    }

    @Test
    public void testGetGameStateString() {
        final GameState state = new GameState(new Boolean[]{
                false,
                true,
                true,
                false,
                true
        });

        assertThat(state.getGameStateString()).isEqualTo("01101");
    }
}
