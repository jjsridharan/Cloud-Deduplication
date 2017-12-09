public class Pairing<L,R> {

  private final L left;
  private final R right;

  public Pairing(L left, R right) 
  {
    this.left = left;
    this.right = right;
  }

  public L getLeft() { return left; }
  public R getRight() { return right; }
}

