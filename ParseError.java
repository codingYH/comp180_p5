public class ParseError extends RuntimeException {
    String text;
    int pos;
    ParseError(String text, int pos){
	this.text = text; this.pos = pos;
    }
    public String toString() {
	return "Parse Error at position " + pos + ": " + text;
    }
}