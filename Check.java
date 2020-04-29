import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class Check extends RecursiveAction {
    private final NFA nfa;
    private String query;
    private Object state;
    public static AtomicBoolean found =  new AtomicBoolean();

    Check(NFA nfa, String s, Object t, boolean f) {
        this.nfa = nfa;
        query = s;
        state = t;
        found = new AtomicBoolean(f);
    }

    Check(NFA nfa, String s, Object t) {
        this.nfa = nfa;
        query = s;
        state = t;
    }

    @Override
    protected void compute() {
        if (!found.get()) {
            if (query.isEmpty() && nfa.final_states().contains(state)) {
                found.set(true);
            }
            List<Map.Entry<String, Object>> result = new ArrayList<>();
            List<Map.Entry<Character, Object>> trans = nfa.transition(state);
            if (trans != null) {
                if (!query.isEmpty()) {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals(query.charAt(0))) {
                            result.add(Map.entry(query.substring(1, query.length()), e.getValue()));
                        } else if (e.getKey().equals('#')) {
                            result.add(Map.entry(query, e.getValue()));
                        }
                    }
                } else {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals('#')) {
                            result.add(e);
                        }
                    }
                }
                if (!result.isEmpty()) {
                    for (Map.Entry r : result) {
                        Check c = new Check(nfa, (String) r.getKey(), r.getValue());
                        invokeAll(c);
                        }
                    }
                }
            }
        }
    }

