package br.com.mutant.handler;

import javax.ws.rs.core.Response.Status;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.json.Jackson;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.parameter.DNAParameter;
import br.com.mutant.repository.DynamodbRepository;
import br.com.mutant.serializer.DNASerializer;
import br.com.mutant.service.MutantService;
import br.com.mutant.service.MutantServiceImpl;
import br.com.mutant.utils.ApiGatewayResponse;

public class MutantHandler implements RequestHandler<AwsProxyRequest, ApiGatewayResponse> {

	private MutantService mutantService;

	public MutantHandler() {
		mutantService = new MutantServiceImpl(new DynamodbRepository());
	}

	public MutantHandler(MutantService mutantService) {
		this.mutantService = mutantService;
	}

	@Override
	public ApiGatewayResponse handleRequest(final AwsProxyRequest request, final Context context) {

		if (request.getBody() == null || request.getBody().isEmpty()) {
			return ApiGatewayResponse.builder().setStatusCode(Status.BAD_REQUEST.getStatusCode()).build();
		}

		DNAParameter dnaParameter = Jackson.fromJsonString(request.getBody(), DNAParameter.class);

		boolean mutant;
		try {
			mutant = mutantService.processar(dnaParameter.getDNA());

			DNASerializer responseDNA = new DNASerializer(mutant, dnaParameter.getDNA());
			if (mutant) {
				return ApiGatewayResponse.builder().setStatusCode(Status.OK.getStatusCode()).setObjectBody(responseDNA)
						.build();
			} else {
				return ApiGatewayResponse.builder().setStatusCode(Status.FORBIDDEN.getStatusCode())
						.setObjectBody(responseDNA).build();
			}
		} catch (InvalidDnaException e) {
			return ApiGatewayResponse.builder().setStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
		}
	}
}
