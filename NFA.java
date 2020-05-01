import javax.lang.model.element.UnknownElementException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NFA {
  private static int i;
  private Object startState = new Object();
  private List<Object> finalStates = new ArrayList<>();
  private List<Object> states = new ArrayList<>();
  private HashMap<Object, List<Map.Entry<Character, Object>>> transitions = new HashMap<>();
  private static Lock lock = new ReentrantLock();
  private static Condition c = lock.newCondition();

  NFA() {
    makeStart();
  }

  void makeStart() {
    startState = i++;
    states.add(startState);
  }

  Object newState() {
    int state = i++;
    states.add(state);
    return state;
  }

  void newTransition(Object start, char c, Object end) {
    if (start == null || end == null ||
            !states.contains(start) || !states.contains(end)) {
      throw new UnsupportedOperationException("states don't exist!");
    } else if ((c < 'a' || c > 'z') && c != '#') {
      throw new UnsupportedOperationException("character " + c + " don't support!");
    } else {
      Map.Entry<Character, Object> t = Map.entry(c, end);
      List<Map.Entry<Character, Object>> trans = transitions.get(start);
      if (trans == null) {
        trans = new ArrayList<>();
      }
      trans.add(t);
      transitions.put(start, trans);
    }
  }

  void makeFinal(Object s) {
    finalStates.add(s);
  }

  NFA(Regex re) {
    if (re instanceof ROr) {
      ROr or = (ROr) re;
      makeStart();
      NFA l = new NFA(or.left);
      NFA r = new NFA(or.right);
      states.addAll(l.states);
      states.addAll(r.states);
      transitions.putAll(l.transitions);
      transitions.putAll(r.transitions);
      finalStates.addAll(l.finalStates);
      finalStates.addAll(r.finalStates);
      newTransition(startState, '#', l.startState);
      newTransition(startState, '#', r.startState);
    }
    if (re instanceof RChar) {
      RChar rChar = (RChar) re;
      makeStart();
      Object f = newState();
      newTransition(startState, rChar.c, f);
      makeFinal(f);
    }
    if (re instanceof RSeq) {
      RSeq seq = (RSeq) re;
      NFA l = new NFA(seq.left);
      NFA r = new NFA(seq.right);
      startState = l.startState;
      finalStates = r.finalStates;
      states.addAll(l.states);
      states.addAll(r.states);
      transitions.putAll(l.transitions);
      transitions.putAll(r.transitions);
      for (Object o : l.finalStates) {
        newTransition(o, '#', r.startState);
      }
    }
    if (re instanceof RStar) {
      RStar star = (RStar) re;
      makeStart();
      makeFinal(startState);
      NFA n = new NFA(star.re);
      states.addAll(n.states);
      transitions.putAll(n.transitions);
      newTransition(startState, '#', n.startState);
      for (Object o : n.finalStates) {
        newTransition(o, '#', startState);
      }
    }
  }

  public List<Object> states() {
    return states;
  }

  public Object start_state() {
    return startState;
  }

  public List<Object> final_states() {
    return finalStates;
  }

  public List<Map.Entry<Character, Object>> transition(Object state) {
    return transitions.get(state);
  }

  boolean match(String s, int nthreads) {
//    lock.lock();
    ForkJoinPool pool = new ForkJoinPool(nthreads);
//    while (Check.found != null){
//      try {
//        c.await();
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
    pool.invoke(new Check(this, s, startState, pool, false));
    boolean r = Check.found.get();
//    Check.found = null;
//    c.signalAll();
//    lock.unlock();
//    pool.shutdown();
    return r;
  }

}
