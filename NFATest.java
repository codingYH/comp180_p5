import org.junit.Assert;
import org.junit.Test;

public class NFATest {
    @Test
    public void match(){
        Parser p = new Parser("(a*b*)*");
        Regex r = p.parse();
        NFA nfa = new NFA(r);
        Assert.assertFalse(nfa.match("ababababababababababababac", 4));
    }
}
