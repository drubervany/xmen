package br.com.mutant.teste.repositoty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.util.json.Jackson;

import br.com.mutant.entity.DNAEntity;
import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.parameter.DNAParameter;
import br.com.mutant.repository.DynamodbRepository;

@RunWith(PowerMockRunner.class)
public class DynamodbRepositoryTest {

	private DynamodbRepository repository;
	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

	public DynamodbRepositoryTest() {
		this.client = mock(AmazonDynamoDB.class);
		this.mapper = mock(DynamoDBMapper.class);
		this.repository = new DynamodbRepository(this.client, this.mapper);
	}

	@Test
	public void consultarHumanos() throws InvalidDnaException {

		QueryResult result = mock(QueryResult.class);
		when(client.query(any())).thenReturn(result);
		when(result.getCount()).thenReturn(1);

		int quantidade = repository.consultarHumanos();

		ArgumentCaptor<QueryRequest> argumento = ArgumentCaptor.forClass(QueryRequest.class);
		verify(client, times(1)).query(argumento.capture());
		QueryRequest queryRequest = argumento.getValue();

		assertEquals("Mutant-index", queryRequest.getIndexName());
		assertEquals("DNA", queryRequest.getTableName());
		assertEquals("COUNT", queryRequest.getSelect());
		assertEquals("0", queryRequest.getKeyConditions().get("Mutant").getAttributeValueList().get(0).getN());
		assertEquals(1, quantidade);
	}

	@Test
	public void consultarMutants() throws InvalidDnaException {

		QueryResult result = mock(QueryResult.class);
		when(client.query(any())).thenReturn(result);
		when(result.getCount()).thenReturn(1);

		int quantidade = repository.consultarMutants();

		ArgumentCaptor<QueryRequest> argumento = ArgumentCaptor.forClass(QueryRequest.class);
		verify(client, times(1)).query(argumento.capture());
		QueryRequest queryRequest = argumento.getValue();

		assertEquals("Mutant-index", queryRequest.getIndexName());
		assertEquals("DNA", queryRequest.getTableName());
		assertEquals("COUNT", queryRequest.getSelect());
		assertEquals("1", queryRequest.getKeyConditions().get("Mutant").getAttributeValueList().get(0).getN());
		assertEquals(1, quantidade);
	}

	@Test(expected = InvalidDnaException.class)
	@SuppressWarnings("unchecked")
	public void consultarQueryErros() throws InvalidDnaException {

		QueryResult result = mock(QueryResult.class);
		when(client.query(any())).thenThrow(InvalidDnaException.class);
		when(result.getCount()).thenReturn(1);

		repository.consultarHumanos();
	}

	@Test
	public void salvarDNANulo() throws InvalidDnaException {

		repository.gravar(null);

		ArgumentCaptor<DNAEntity> argumento = ArgumentCaptor.forClass(DNAEntity.class);
		verify(mapper, times(1)).save(argumento.capture());
		DNAEntity response = argumento.getValue();

		assertNull(response);
	}

	@Test
	public void salvarDNAListaNulo() throws InvalidDnaException {

		DNAEntity dna = new DNAEntity();
		dna.setDna(new ArrayList<>());
		dna.setMutant(true);
		
		repository.gravar(dna);

		ArgumentCaptor<DNAEntity> argumento = ArgumentCaptor.forClass(DNAEntity.class);
		verify(mapper, times(1)).save(argumento.capture());
		DNAEntity response = argumento.getValue();

		assertNotNull(response);
		assertEquals(dna.getDna(), response.getDna());
	}

	@Test
	public void salvarDNASucesso() throws InvalidDnaException {

		String json = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		DNAEntity dna = Jackson.fromJsonString(json, DNAEntity.class);

		repository.gravar(dna);

		ArgumentCaptor<DNAEntity> argumento = ArgumentCaptor.forClass(DNAEntity.class);
		verify(mapper, times(1)).save(argumento.capture());
		DNAEntity response = argumento.getValue();

		assertEquals(dna.getDna(), response.getDna());

	}

}
