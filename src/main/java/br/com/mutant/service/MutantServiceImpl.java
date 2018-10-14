package br.com.mutant.service;

import java.util.List;

import br.com.mutant.entity.DNAEntity;
import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.model.Estatistica;
import br.com.mutant.repository.DynamodbRepository;
import br.com.mutant.validation.DNAMutantValidation;

/**
 * @author rubervany
 */
public class MutantServiceImpl implements MutantService {

	private DynamodbRepository repository;

	public MutantServiceImpl(DynamodbRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean processar(List<String> dna) throws InvalidDnaException {

		if (dna == null || dna.isEmpty())
			throw new InvalidDnaException();

		boolean dnaMutant = DNAMutantValidation.newInstance().withLista(dna).validar();

		repository.gravar(new DNAEntity(dnaMutant, dna));

		return dnaMutant;
	}

	@Override
	public Estatistica consultarEstatisticas() throws InvalidDnaException {

		int multants = repository.consultarMutants();
		int humanos = repository.consultarHumanos();
		return new Estatistica(multants, humanos);
	}
}
