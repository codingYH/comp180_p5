public class RSeq implements Regex {
    public final Regex left, right;
    public RSeq(Regex left, Regex right) {
	this.left = left; this.right = right;
    }
    public String toString() {
	return left.toString() + right.toString();
    }
}