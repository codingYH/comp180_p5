public class Parser {
    /* General grammar:

       E ::= a | EE | E|E | E* | (E)

       LL(1) grammar:

       S ::= E$
       E ::= T|E | T
       T ::= FT | F
       F ::= G* | G
       G ::= a | (E)

     */

    private String s;
    private int i;

    public Parser(String s) { this.s = s; }

    private char lookahead() {
      if (i == s.length()) { return '$';}
      return s.charAt(i);
    }

    private Regex parse_G() {
      char l = lookahead();
      if (l >= 'a' && l <= 'z') {
        i++;
        return new RChar(l);
      }
      else if (l == '(') {
        i++;
        Regex re = parse_E();
        if (lookahead() != ')') {
          throw new ParseError("Expecting right paren", i);
        }
        i++;
        return re;
      }
      else {
        throw new ParseError("Unexpected char", i);
      }
    }

    private Regex parse_F() {
      Regex left = parse_G();
      if (lookahead() == '*') {
        i++;
        return new RStar(left);
      }
      else { return left; }
    }

    private Regex parse_T() {
      Regex left = parse_F();
      char l = lookahead();
      if ((l >= 'a' && l <= 'z') || (l == '(')) {
        Regex right = parse_T();
        return new RSeq(left, right);
      }
      else { return left; }
    }

    private Regex parse_E() {
      Regex left = parse_T();
      if (lookahead() == '|') {
        i++;
        Regex right = parse_E();
        return new ROr(left, right);
      }
      else { return left; }
    }

    public Regex parse() {
      Regex re = parse_E();
      if (i != s.length()) {
        throw new ParseError("Unexpected text after regexp", i);
      }
      return re;
    }
}
