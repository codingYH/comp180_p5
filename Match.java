public class Match {
    public static void main(String[] args) {
      if (args.length != 2) {
        System.out.println("Usage: java Match [regex] [string]");
  	    System.out.println("       Put regex in quotes to avoid shell parsing weirdness");
  	    return;
  	}

  	Parser p = new Parser(args[0]);
    Regex r = p.parse();
  	NFA nfa = new NFA(r);
  	if (nfa.match(args[1], 4)) {
      System.out.println("yes");
    }
  	else {
      System.out.println("no");
  	}
  }
}
