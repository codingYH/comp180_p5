import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLongArray;

public class Check extends RecursiveAction {
    private final NFA nfa;
    private String query;
    private Object state;
    private static ForkJoinPool pool;
    public static AtomicBoolean found = new AtomicBoolean();
    //    public static HashSet<String> discovered = new HashSet<>();
    public static LinkedBlockingQueue<String> discovered = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<Check> cl = new LinkedBlockingQueue<>();
    private int nthread;

    Check(NFA nfa, String s, Object t, boolean f, ForkJoinPool p, int n) {
        this.nfa = nfa;
        query = s;
        state = t;
        found = new AtomicBoolean(f);
        pool = p;
        nthread = n;
    }

    Check(NFA nfa, String s, Object t, int n) {
        this.nfa = nfa;
        query = s;
        state = t;
        nthread = n;
    }

    @Override
    protected void compute() {
        discovered.add(query + state);
        if (query.isEmpty() && nfa.final_states().contains(state)) {
            found.set(true);
            System.out.println("find!!!!!");
            pool.shutdown();
        } else {
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
                    for (Map.Entry r : result) {
                        if (discovered.contains((String) r.getKey() + r.getValue()) == false) {
                            Check c = new Check(nfa, (String) r.getKey(), r.getValue(), nthread);
                            cl.add(c);
                            /*if (cl.size() > nthread) {
                                consume();
                            }*/
                        }
                    }
                }
            }
            long in = System.currentTimeMillis();
            while(cl.size() <= 1 && pool.getActiveThreadCount() > 1 && System.currentTimeMillis() - in <= 5);
//            while(cl.size() <= 1 && pool.getRunningThreadCount() > 1&& System.currentTimeMillis() - in <= 70);
            consume();
        }
    }

    private void consume() {
        LinkedBlockingQueue<Check> checks = cl;
        cl = new LinkedBlockingQueue<>();
        for (Check c :checks){
            if (discovered.contains((String) c.query + c.state) == true){
                checks.remove(c);
            }
        }
        if (!checks.isEmpty()){
//            System.out.println(pool.getRunningThreadCount());
//            System.out.println(checks);
            invokeAll(checks);
        }
    }
}