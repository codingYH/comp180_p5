import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class Check extends RecursiveAction {
    private final NFA nfa;
    private String query;
    private Object state;
    public static AtomicBoolean found =  new AtomicBoolean();
    public volatile static HashSet<String> discovered = new HashSet<>();

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
            discovered.add(query + state);
            if (query.isEmpty() && nfa.final_states().contains(state)) {
                found.set(true);
            }
            List<Map.Entry<String, Object>> result = new ArrayList<>();
            List<Map.Entry<Character, Object>> trans = nfa.transition(state);
            if (trans != null) {
                if (!query.isEmpty()) {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals(query.charAt(0))) {
                            String key = query.substring(1, query.length());
                            Object value = e.getValue();
                            result.add(Map.entry(key, value));
                        } else if (e.getKey().equals('#')) {
                            String key = query;
                            Object value = e.getValue();
                            result.add(Map.entry(key, value));
                        }
                    }
                } else {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals('#')) {
                            String key = query;
                            Object value = e.getValue();
                            result.add(Map.entry(key, value));
                        }
                    }
                }
                if (!result.isEmpty()) {
                    List<Check> cl = new ArrayList<>();
                    for (Map.Entry r : result) {
                        if(discovered.contains((String) r.getKey() + r.getValue()) == false){
                            Check c = new Check(nfa, (String) r.getKey(), r.getValue());
                            cl.add(c);
                        }
                    }
                    invokeAll(cl);
                }
            }
        }
    }
}