public class RStar implements Regex {
    public final Regex re;
    public RStar(Regex re) {
	this.re = re;
    }
    public String toString() {
	return "(" + re.toString() + ")*";
    }
}