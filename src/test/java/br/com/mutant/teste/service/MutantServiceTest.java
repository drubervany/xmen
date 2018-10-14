package br.com.mutant.teste.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.util.json.Jackson;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.model.Estatistica;
import br.com.mutant.parameter.DNAParameter;
import br.com.mutant.repository.DynamodbRepository;
import br.com.mutant.service.MutantService;
import br.com.mutant.service.MutantServiceImpl;
import br.com.mutant.validation.DNAMutantValidation;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DNAMutantValidation.class })
public class MutantServiceTest {

	private MutantService mutantService;
	private DynamodbRepository repository;

	public MutantServiceTest() {
		PowerMockito.mockStatic(DNAMutantValidation.class);

		this.repository = mock(DynamodbRepository.class);
		this.mutantService = new MutantServiceImpl(repository);
	}

	@Test(expected = InvalidDnaException.class)
	public void erroAoValidarListaNulo() throws InvalidDnaException {

		DNAParameter dnaParameter = Jackson.fromJsonString("{}", DNAParameter.class);
		mutantService.processar(dnaParameter.getDNA());
	}

	@Test(expected = InvalidDnaException.class)
	public void listaVaizaRetornoFalse() throws InvalidDnaException {

		String json = "{\"dna\":[]}";
		DNAParameter dnaParameter = Jackson.fromJsonString(json, DNAParameter.class);
		mutantService.processar(dnaParameter.getDNA());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveSerMutantVerdadeiro() throws InvalidDnaException {

		String json = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		DNAParameter dnaParameter = Jackson.fromJsonString(json, DNAParameter.class);

		DNAMutantValidation dnaMutantValidation = mock(DNAMutantValidation.class);
		when(DNAMutantValidation.newInstance()).thenReturn(dnaMutantValidation);
		when(dnaMutantValidation.withLista(anyList())).thenReturn(dnaMutantValidation);
		when(dnaMutantValidation.validar()).thenReturn(true);
		

		boolean response = mutantService.processar(dnaParameter.getDNA());

		verify(repository, times(1)).gravar(any());

		assertTrue(response);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveSerMutantFalso() throws InvalidDnaException {

		String json = "{\"dna\":[\"ATGCGA\",\"AGTGC\",\"TTATTT\",\"AGACGG\",\"GCGTCA\",\"TCACTG\"]}";
		DNAParameter dnaParameter = Jackson.fromJsonString(json, DNAParameter.class);

		DNAMutantValidation dnaMutantValidation = mock(DNAMutantValidation.class);
		when(DNAMutantValidation.newInstance()).thenReturn(dnaMutantValidation);
		when(dnaMutantValidation.withLista(anyList())).thenReturn(dnaMutantValidation);
		when(dnaMutantValidation.validar()).thenReturn(false);

		boolean response = mutantService.processar(dnaParameter.getDNA());

		verify(repository, times(1)).gravar(any());

		assertFalse(response);

	}

	@Test
	public void consultaResultadoSucesso() throws InvalidDnaException {

		when(repository.consultarHumanos()).thenReturn(10);
		when(repository.consultarMutants()).thenReturn(2);

		Estatistica response = mutantService.consultarEstatisticas();

		verify(repository, times(1)).consultarHumanos();
		verify(repository, times(1)).consultarMutants();

		assertEquals(10, response.getCountHumanDna());
		assertEquals(2, response.getCountMutantDna());

	}

	@Test(expected = InvalidDnaException.class)
	@SuppressWarnings("unchecked")
	public void consultaEstatisticaException() throws InvalidDnaException {

		when(repository.consultarHumanos()).thenThrow(InvalidDnaException.class);
		when(repository.consultarMutants()).thenThrow(InvalidDnaException.class);
		mutantService.consultarEstatisticas();
	}

}
