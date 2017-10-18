/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class HList<T extends HList<T>> {
	private HList() {}

	public static final class Nil extends HList<Nil> {
		private static final Nil NIL = new Nil();
		private Nil() {}
	}

	public static final class Cons<E, L extends HList<L>> {
		private E _e;
		private L _l;

		private Cons(final E e, final L l) {
			_e = e;
			_l = l;
		}

		public E head() {
			return _e;
		}

		public L tail() {
			return _l;
		}

	}

	public static final class Append<L, R, LR> {
		private final BiFunction<L, R, LR> _append;

		private Append(final BiFunction<L, R, LR> f) {
			_append = requireNonNull(f);
		}

		public LR append(final L l, final R r) {
			return _append.apply(l, r);
		}

		public static <L extends HList<L>> Append<Nil, L, L> append() {
			return new Append<>((nil, l) -> l);
		}

		public static <
			X,
			A extends HList<A>,
			B,
			C extends HList<C>,
			H extends Append<A, B, C>
		>
		Append<Cons<X, A>, B, Cons<X, C>> append(final H h) {
			return new Append<>((c, l) -> cons(c.head(), h.append(c.tail(), l)));
		}
	}


	public static Nil nil() {
		return Nil.NIL;
	}

	public static <E, L extends HList<L>> Cons<E, L> cons(final E e, final L l) {
		return new Cons<E, L>(e, l);
	}

	public static <E> Cons<E, Nil> cons(final E e) {
		return cons(e, nil());
	}

}
