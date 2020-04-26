public class ROr implements Regex {
    public final Regex left, right;
    public ROr(Regex left, Regex right) {
	this.left = left; this.right = right;
    }
    public String toString() {
	return "(" + left.toString() + "|" + right.toString() + ")";
    }
}