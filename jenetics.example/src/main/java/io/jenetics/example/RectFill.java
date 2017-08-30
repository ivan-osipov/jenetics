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
package org.jenetics.example;

import static org.jenetics.engine.limit.byFixedGeneration;

import java.util.Random;

import org.jenetics.AnyChromosome;
import org.jenetics.AnyGene;
import org.jenetics.Genotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

public class RectFill {

	private static final int MAX_RECT_COUNT = 100;

	static final class Rect {
		static final Rect EMPTY = new Rect(-1, -1, -1, -1);
		final int x1, x2, y1, y2;
		Rect(final int x1, final int x2, final int y1, final int y2) {
			this.x1 = x1; this.x2 = x2; this.y1 = y1; this.y2 = y2;
		}

		static Rect newInstance() {
			final Random random = RandomRegistry.getRandom();
			return random.nextBoolean()
				? new Rect(
					random.nextInt(100),
					random.nextInt(100),
					random.nextInt(100),
					random.nextInt(100))
				: EMPTY;
		}
	}

	static final Codec<ISeq<Rect>, AnyGene<Rect>> CODEC = Codec.of(
		Genotype.of(AnyChromosome.of(Rect::newInstance, MAX_RECT_COUNT)),
		gt -> gt.getChromosome()
				.stream()
				.map(AnyGene::getAllele)
				.filter(r -> r != Rect.EMPTY)
				.collect(ISeq.toISeq())
	);

	static int fitness(final ISeq<Rect> rects) {
		// Here comes your fitness function.
		return rects.length();
	}

	public static void main(final String[] args) {
		final Engine<AnyGene<Rect>, Integer> engine = Engine
			.builder(RectFill::fitness, CODEC)
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new SwapMutator<>(),
				new SinglePointCrossover<>())
			.build();

		final ISeq<Rect> best = CODEC.decode(
			engine.stream()
				.limit(byFixedGeneration(10))
				.collect(EvolutionResult.toBestGenotype())
		);

		System.out.println(best);
	}

}
