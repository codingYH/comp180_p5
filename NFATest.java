import org.junit.Assert;
import org.junit.Test;

public class NFATest {
    @Test
    public void match() {
        Parser p = new Parser("(a*b*)*");
        Regex r = p.parse();
        NFA nfa = new NFA(r);
        Assert.assertFalse(nfa.match("ababababababababababababababababababababababababababc", 4));
    }

    @Test
    public void match2(){
        Parser p2 = new Parser("(a|b)*c");
        Regex r2 = p2.parse();
        NFA nfa2 = new NFA(r2);
        Assert.assertTrue(nfa2.match("abc", 4));
        Assert.assertFalse(nfa2.match("aca", 4));
    }
}
