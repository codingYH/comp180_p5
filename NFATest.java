import org.junit.Assert;
import org.junit.Test;

public class NFATest {
    @Test
    public void match(){
        Parser p = new Parser("(ab)|(cd)");
        //generate regex
        Regex r = p.parse();
        //generate nfa which stores state objects and transitions
        NFA nfa = new NFA(r);
        //"abc" matches "ab*|c"
        Assert.assertTrue(nfa.match("ab", 4));
        //"bac" matches "ab*|c"
        Assert.assertFalse(nfa.match("abcd", 4));
        //"cc" doesn't matches "ab*|c"
        Assert.assertFalse(nfa.match("abc", 4));
    }
}
