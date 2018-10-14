package br.com.mutant.service;

import java.util.List;

import br.com.mutant.exception.InvalidDnaException;
import br.com.mutant.model.Estatistica;

/**
 * @author rubervany
 */
public interface MutantService {

	boolean processar(List<String> dna) throws InvalidDnaException;

	Estatistica consultarEstatisticas() throws InvalidDnaException;

}
