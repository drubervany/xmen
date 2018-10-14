package br.com.mutant.teste.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.json.Jackson;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.handler.StatHandler;
import br.com.mutant.model.Estatistica;
import br.com.mutant.repository.DynamodbRepository;
import br.com.mutant.serializer.EstatisticaSerializer;
import br.com.mutant.service.MutantService;
import br.com.mutant.service.MutantServiceImpl;
import br.com.mutant.utils.ApiGatewayResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { MutantService.class })
public class StatHandlerTest {

	@InjectMocks
	private StatHandler statHandler;

	@Mock
	private MutantService mutantService;

	@Mock
	MutantServiceImpl mutantServiceImpl;

	@Mock
	DynamodbRepository dynamodbRepository;

	private AwsProxyRequest request;
	private Context context;

	public StatHandlerTest() {
		this.request = mock(AwsProxyRequest.class);
		this.context = mock(Context.class);
		this.mutantService = mock(MutantService.class);
		this.statHandler = new StatHandler(this.mutantService);
	}

	@Test
	public void consultaEstatisticaSemConteudo() {
		ApiGatewayResponse response = statHandler.handleRequest(request, context);
		assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatusCode());
	}

	@Test
	public void estatisticaRetornadaComSucesso() throws InvalidDnaException {

		Estatistica estatistica = new Estatistica(10, 9);
		when(mutantService.consultarEstatisticas()).thenReturn(estatistica);

		ApiGatewayResponse response = statHandler.handleRequest(request, context);

		assertEquals(Status.OK.getStatusCode(), response.getStatusCode());

	}

	@Test
	public void estatisticaRetornadaRatioZero() throws InvalidDnaException {

		Estatistica estatistica = new Estatistica(10, 0);
		when(mutantService.consultarEstatisticas()).thenReturn(estatistica);

		ApiGatewayResponse response = statHandler.handleRequest(request, context);

		EstatisticaSerializer estatisticas = Jackson.fromJsonString(response.getBody(), EstatisticaSerializer.class);

		assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
		assertEquals(10, estatisticas.getCountMutantDna());
		assertEquals(0, estatisticas.getCountHumanDna());
		assertEquals(BigDecimal.ZERO, estatisticas.getRatio());

	}

	@Test
	public void maiorParaHumano() throws InvalidDnaException {

		Estatistica estatistica = new Estatistica(40, 100);
		when(mutantService.consultarEstatisticas()).thenReturn(estatistica);

		ApiGatewayResponse response = statHandler.handleRequest(request, context);

		EstatisticaSerializer estatisticas = Jackson.fromJsonString(response.getBody(), EstatisticaSerializer.class);

		assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
		assertEquals(40, estatisticas.getCountMutantDna());
		assertEquals(100, estatisticas.getCountHumanDna());
		assertEquals(new BigDecimal("0.40"), estatisticas.getRatio());

	}

	@Test
	public void maiorParaMutant() throws InvalidDnaException {

		Estatistica estatistica = new Estatistica(100, 40);
		when(mutantService.consultarEstatisticas()).thenReturn(estatistica);

		ApiGatewayResponse response = statHandler.handleRequest(request, context);

		EstatisticaSerializer estatisticas = Jackson.fromJsonString(response.getBody(), EstatisticaSerializer.class);

		assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
		assertEquals(100, estatisticas.getCountMutantDna());
		assertEquals(40, estatisticas.getCountHumanDna());
		assertEquals(new BigDecimal("2.50"), estatisticas.getRatio());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void erroInternoNoServicoDePesquisa() throws InvalidDnaException {

		when(mutantService.consultarEstatisticas()).thenThrow(InvalidDnaException.class);

		ApiGatewayResponse response = statHandler.handleRequest(request, context);

		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());

	}
}
