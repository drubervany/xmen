package br.com.mutant.validation;

import java.util.List;

public final class DNAMutantValidation {

	char[][] lista;
	int totalLinhas;
	int totalColunas;

	public static DNAMutantValidation newInstance() {
		return new DNAMutantValidation();
	}

	public DNAMutantValidation withLista(List<String> dna) {
		this.lista = dna.stream().map(String::toCharArray).toArray(char[][]::new);
		this.totalLinhas = this.lista.length;
		this.totalColunas = validaTamanhoColunas(this.lista);
		return this;
	}

	public boolean validar() {
		return verificarDNA();
	}

	private boolean verificarDNA() {

		for (int linhaAtual = 0; linhaAtual < totalLinhas; linhaAtual++) {
			for (int colunaAtual = 0; colunaAtual < totalColunas; colunaAtual++) {
				boolean dnaHorizontal = validarHorizontal(linhaAtual, colunaAtual);
				boolean dnaVertical = validarVertical(linhaAtual, colunaAtual);
				boolean dnaOblicua = validarOblicua(linhaAtual, colunaAtual);
				if (dnaHorizontal || dnaVertical || dnaOblicua)
					return true;
			}
		}
		return false;
	}

	private int validaTamanhoColunas(char[][] lista) {
		int colunaAtual = 0;
		for (char[] nucleonideos : lista) {
			if (colunaAtual < nucleonideos.length)
				colunaAtual = nucleonideos.length;
		}
		return colunaAtual;
	}

	private boolean validarOblicua(int linha, int coluna) {
		if (linha < (totalLinhas - 4) && coluna < (totalColunas - 4)) {
			return validar(linha, coluna) == validar(linha + 1, coluna + 1)
					&& validar(linha + 1, coluna + 1) == validar(linha + 2, coluna + 2)
					&& validar(linha + 2, coluna + 2) == validar(linha + 3, coluna + 3);
		}
		return false;
	}

	private boolean validarVertical(int linha, int coluna) {
		if (linha < (totalLinhas - 4)) {
			return validar(linha, coluna) == validar(linha + 1, coluna)
					&& validar(linha + 1, coluna) == validar(linha + 2, coluna)
					&& validar(linha + 2, coluna) == validar(linha + 3, coluna);
		}
		return false;

	}

	private boolean validarHorizontal(int linha, int coluna) {
		if (coluna < (totalColunas - 4)) {
			return validar(linha, coluna) == validar(linha, coluna + 1)
					&& validar(linha, coluna + 1) == validar(linha, coluna + 2)
					&& validar(linha, coluna + 2) == validar(linha, coluna + 3);
		}
		return false;
	}

	private char validar(int linha, int coluna) {
		try {
			return lista[linha][coluna];
		} catch (Exception e) {
			return 'X';
		}
	}
}
