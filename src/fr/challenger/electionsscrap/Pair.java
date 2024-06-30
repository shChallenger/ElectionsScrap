package fr.challenger.electionsscrap;

public class Pair<T, V> {
	
	private final T left;
	private final V right;
	
	public static <A, B> Pair<A, B> of(final A left, final B right)
	{
		return new Pair<A, B>(left, right);
	}

	public Pair(final T left, final V right)
	{
		this.left = left;
		this.right = right;
	}

	public T getLeft() {
		return left;
	}

	public V getRight() {
		return right;
	}

}
