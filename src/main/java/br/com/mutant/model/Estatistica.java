package br.com.mutant.model;

public class Estatistica {

	private int countMutantDna;
	private int countHumanDna;

	public Estatistica(int countMutantDna, int countHumanDna) {
		this.countMutantDna = countMutantDna;
		this.countHumanDna = countHumanDna;
	}

	public int getCountMutantDna() {
		return countMutantDna;
	}

	public int getCountHumanDna() {
		return countHumanDna;
	}
}
