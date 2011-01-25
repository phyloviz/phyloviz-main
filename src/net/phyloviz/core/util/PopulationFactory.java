package net.phyloviz.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import net.phyloviz.core.data.Population;

public class PopulationFactory {

	public Population loadPopulation(Reader r) throws IOException {

		Population pop = new Population();
		BufferedReader in = new BufferedReader(r);

		String[] tokens = in.readLine().split("\t", -1);

		// Store headers.
		for (int i = 0; i < tokens.length; i++)
			pop.addColumn(tokens[i], null);

		while (in.ready()) {
			tokens = in.readLine().split("\t", -1);

			pop.addIsolate(tokens);
		}
		in.close();

		return pop;
	}
}
