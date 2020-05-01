import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLongArray;

public class Check extends RecursiveTask<Boolean> {
    private final NFA nfa;
    private  LinkedBlockingQueue<String> discovered;
    private  LinkedBlockingQueue<String> queue;


    Check(NFA nfa, LinkedBlockingQueue<String> d, LinkedBlockingQueue<String> q) {
        this.nfa = nfa;
        discovered = d;
        queue = q;
    }

    @Override
    protected Boolean compute() {
        if (queue.isEmpty())
            return false;
        String c = queue.poll();
        String query = c.split("!")[0];
        int state = Integer.parseInt(c.split("!")[1]);
        if (query.isEmpty() && nfa.final_states().contains(state)) {
            return true;
        } else {
            List<Map.Entry<Character, Object>> trans = nfa.transition(state);
            List<Check> checks = new ArrayList<>();
            if (trans != null) {
                if (!query.isEmpty()) {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals(query.charAt(0))) {
                            String key = query.substring(1, query.length()) +"!" +e.getValue();
                            if (!discovered.contains(key)){
                                discovered.add(key);
                                queue.add(key);
                                Check next = new Check(nfa, discovered, queue);
                                checks.add(next);
                            }
                        } else if (e.getKey().equals('#')) {
                            String key = query +"!" + e.getValue();
                            if (!discovered.contains(key)){
                                discovered.add(key);
                                queue.add(key);
                                Check next = new Check(nfa, discovered, queue);
                                checks.add(next);
                            }
                        }
                    }
                } else {
                    for (Map.Entry e : trans) {
                        if (e.getKey().equals('#')) {
                            String key =query +"!" + e.getValue();
                            if (!discovered.contains(key)){
                                discovered.add(key);
                                queue.add(key);
                                Check next = new Check(nfa, discovered, queue);
                                checks.add(next);
                            }
                        }
                    }
                }
            }
            invokeAll(checks);
            boolean result = false;
            for (Check check: checks){
                result = result || check.join();
            }
            return result;
        }
    }
}