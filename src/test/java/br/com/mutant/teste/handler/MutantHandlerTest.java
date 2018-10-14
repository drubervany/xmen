package br.com.mutant.teste.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.handler.MutantHandler;
import br.com.mutant.parameter.DNAParameter;
import br.com.mutant.serializer.DNASerializer;
import br.com.mutant.service.MutantService;
import br.com.mutant.utils.ApiGatewayResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { Jackson.class })
public class MutantHandlerTest {

	private MutantHandler mutantHandler;
	private MutantService mutantService;
	private AwsProxyRequest request;
	private Context context;

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public MutantHandlerTest() {
		PowerMockito.mockStatic(Jackson.class);

		this.request = mock(AwsProxyRequest.class);
		this.context = mock(Context.class);
		this.mutantService = mock(MutantService.class);
		this.mutantHandler = new MutantHandler(this.mutantService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveSerUmaRequesicaoRuimEnvioBodyVazio() throws InvalidDnaException {

		when(request.getBody()).thenReturn("");

		ApiGatewayResponse response = mutantHandler.handleRequest(request, context);

		verify(mutantService, times(0)).processar(anyList());

		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveSerUmaRequesicaoRuimEnvioBodyNulo() throws InvalidDnaException {

		when(request.getBody()).thenReturn(null);

		ApiGatewayResponse response = mutantHandler.handleRequest(request, context);

		verify(mutantService, times(0)).processar(anyList());

		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveRetornarMutantVerdadeiro() throws Exception {
		boolean ehMutant = true;

		String json = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		DNAParameter dnaParameter = objectMapper.readValue(json, DNAParameter.class);

		when(request.getBody()).thenReturn(json);
		when(Jackson.fromJsonString(anyString(), eq(DNAParameter.class))).thenReturn(dnaParameter);
		when(this.mutantService.processar(anyList())).thenReturn(ehMutant);

		ApiGatewayResponse response = mutantHandler.handleRequest(request, context);

		DNASerializer dnaSerializer = objectMapper.readValue(response.getBody(), DNASerializer.class);

		verify(mutantService, times(1)).processar(anyList());

		assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
		assertEquals(dnaParameter.getDNA(), dnaSerializer.getDNA());
		assertEquals(ehMutant, dnaSerializer.isMutant());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveRetornarMutantFalso() throws Exception {
		boolean ehMutant = false;

		String json = "{\"dna\":[\"ATGCGA\",\"AGTGC\",\"TTATTT\",\"AGACGG\",\"GCGTCA\",\"TCACTG\"]}";
		DNAParameter dnaParameter = objectMapper.readValue(json, DNAParameter.class);

		when(request.getBody()).thenReturn(json);
		when(Jackson.fromJsonString(anyString(), eq(DNAParameter.class))).thenReturn(dnaParameter);
		when(this.mutantService.processar(anyList())).thenReturn(ehMutant);

		ApiGatewayResponse response = mutantHandler.handleRequest(request, context);

		DNASerializer dnaSerializer = objectMapper.readValue(response.getBody(), DNASerializer.class);

		verify(mutantService, times(1)).processar(anyList());

		assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatusCode());
		assertEquals(dnaParameter.getDNA(), dnaSerializer.getDNA());
		assertEquals(ehMutant, dnaSerializer.isMutant());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveRetornarErroAoProcessarDNA() throws Exception {

		String json = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		DNAParameter dnaParameter = objectMapper.readValue(json, DNAParameter.class);

		when(request.getBody()).thenReturn(json);
		when(Jackson.fromJsonString(anyString(), eq(DNAParameter.class))).thenReturn(dnaParameter);
		when(this.mutantService.processar(anyList())).thenThrow(InvalidDnaException.class);

		ApiGatewayResponse response = mutantHandler.handleRequest(request, context);

		verify(mutantService, times(1)).processar(anyList());

		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());

	}
}
