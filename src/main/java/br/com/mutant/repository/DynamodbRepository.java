package br.com.mutant.repository;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.Select;

import br.com.mutant.entity.DNAEntity;

public class DynamodbRepository {

	private static final String HUMANO = "0";
	private static final String MUTANT = "1";

	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

	public DynamodbRepository() {
		this.client = AmazonDynamoDBClientBuilder.defaultClient();
		this.mapper = new DynamoDBMapper(client);
	}
	
	public DynamodbRepository(AmazonDynamoDB client, DynamoDBMapper mapper) {
		this.client = client;
		this.mapper = mapper;
	}

	public void gravar(DNAEntity dna) {
		mapper.save(dna);
	}

	public int consultarMutants() {
		return consultarEstatisticas(MUTANT);
	}

	public int consultarHumanos() {
		return consultarEstatisticas(HUMANO);
	}

	private int consultarEstatisticas(String filtro) {

		Condition mutantCondition = new Condition()
				.withComparisonOperator(ComparisonOperator.EQ)
				.withAttributeValueList(new AttributeValue().withN(filtro));

		Map<String, Condition> keyConditions = new HashMap<>();
		keyConditions.put("Mutant", mutantCondition);

		QueryRequest request = new QueryRequest("DNA");
		request.setIndexName("Mutant-index");
		request.setSelect(Select.COUNT);
		request.setKeyConditions(keyConditions);

		QueryResult result = client.query(request);
		
		return result.getCount().intValue();
	}

}
