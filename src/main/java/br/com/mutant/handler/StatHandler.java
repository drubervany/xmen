package br.com.mutant.handler;

import javax.ws.rs.core.Response.Status;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.model.Estatistica;
import br.com.mutant.repository.DynamodbRepository;
import br.com.mutant.serializer.EstatisticaSerializer;
import br.com.mutant.service.MutantService;
import br.com.mutant.service.MutantServiceImpl;
import br.com.mutant.utils.ApiGatewayResponse;

public class StatHandler implements RequestHandler<AwsProxyRequest, ApiGatewayResponse> {

	private MutantService mutantService;

	public StatHandler() {
		this.mutantService = new MutantServiceImpl(new DynamodbRepository());
	}

	public StatHandler(MutantService mutantService) {
		this.mutantService = mutantService;
	}

	@Override
	public ApiGatewayResponse handleRequest(final AwsProxyRequest request, final Context context) {

		Estatistica estatistica = null;
		try {
			estatistica = mutantService.consultarEstatisticas();
			if (estatistica == null)
				return ApiGatewayResponse.builder().setStatusCode(Status.NO_CONTENT.getStatusCode()).build();

			EstatisticaSerializer responseEstatistica = new EstatisticaSerializer(estatistica);

			return ApiGatewayResponse.builder().setStatusCode(Status.OK.getStatusCode())
					.setObjectBody(responseEstatistica).build();
		} catch (InvalidDnaException e) {
			return ApiGatewayResponse.builder().setStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
		}

	}
}
