package br.com.mutant.serializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.mutant.model.Estatistica;

public class EstatisticaSerializer {

	@JsonProperty("count_mutant_dna")
	private int countMutantDna;
	@JsonProperty("count_human_dna")
	private int countHumanDna;

	public EstatisticaSerializer() {
	}

	public EstatisticaSerializer(Estatistica estatistica) {
		this.countMutantDna = estatistica.getCountMutantDna();
		this.countHumanDna = estatistica.getCountHumanDna();
	}

	public int getCountMutantDna() {
		return countMutantDna;
	}

	public int getCountHumanDna() {
		return countHumanDna;
	}

	@JsonProperty("ratio")
	public BigDecimal getRatio() {
		if (this.countHumanDna != 0) {
			return new BigDecimal(this.countMutantDna).divide(new BigDecimal(this.countHumanDna), 2,
					RoundingMode.HALF_UP);
		}
		return BigDecimal.ZERO;
	}
}
