package br.com.mutant.serializer;

import java.util.List;

public class DNASerializer {

	private List<String> dna;
	private boolean mutant;

	public DNASerializer() {
	}

	public DNASerializer(boolean mutant, List<String> dna) {
		this.mutant = mutant;
		this.dna = dna;
	}

	public boolean isMutant() {
		return mutant;
	}

	public List<String> getDNA() {
		return dna;
	}
}
