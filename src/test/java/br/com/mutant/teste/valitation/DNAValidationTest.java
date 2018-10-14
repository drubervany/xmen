package br.com.mutant.teste.valitation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.util.json.Jackson;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.parameter.DNAParameter;
import br.com.mutant.validation.DNAMutantValidation;

@RunWith(PowerMockRunner.class)
public class DNAValidationTest {

	public DNAValidationTest() {
	}

	@Test
	public void mutantVerdadeiro() throws InvalidDnaException {

		String json = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		DNAParameter dna = Jackson.fromJsonString(json, DNAParameter.class);

		boolean mutant = DNAMutantValidation.newInstance().withLista(dna.getDNA()).validar();

		assertTrue(mutant);

	}

	@Test
	public void mutantFalso() throws InvalidDnaException {

		String json = "{\"dna\":[\"ATGCGA\",\"AGTGC\",\"TTATTT\",\"AGACGG\",\"GCGTCA\",\"TCACTG\"]}";
		DNAParameter dna = Jackson.fromJsonString(json, DNAParameter.class);

		boolean mutant = DNAMutantValidation.newInstance().withLista(dna.getDNA()).validar();

		assertFalse(mutant);

	}
}