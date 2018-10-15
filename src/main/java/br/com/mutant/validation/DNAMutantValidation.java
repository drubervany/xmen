package br.com.mutant.validation;

import java.util.List;

public final class DNAMutantValidation {

	char[][] lista;
	int totalLinhas;
	int totalColunas;
	int contDNAMutant = 0;

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

		for (int linhaAtual = 0; linhaAtual < this.totalLinhas; linhaAtual++) {
			for (int colunaAtual = 0; colunaAtual < this.totalColunas; colunaAtual++) {
				validarHorizontal(linhaAtual, colunaAtual);
				validarVertical(linhaAtual, colunaAtual);
				validarOblicua(linhaAtual, colunaAtual);
				if (this.contDNAMutant > 1)
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

	private void validarOblicua(int linha, int coluna) {
		if (linha < (totalLinhas - 4) && coluna <= (totalColunas - 4)) {
			if (validar(linha, coluna + 4) == validar(linha + 1, coluna + 1)
					&& validar(linha + 1, coluna + 1) == validar(linha + 2, coluna + 2)
					&& validar(linha + 2, coluna + 2) == validar(linha + 3, coluna + 3)) {
				this.contDNAMutant++;
			}
		}
		if (linha < (totalLinhas - 4) && coluna <= (totalColunas - 4)) {
			if (validar(linha, coluna + 4) == validar(linha + 1, coluna + 3)
					&& validar(linha + 1, coluna + 3) == validar(linha + 2, coluna + 2)
					&& validar(linha + 2, coluna + 2) == validar(linha + 3, coluna + 1)) {
				this.contDNAMutant++;
			}
		}
	}

	private void validarVertical(int linha, int coluna) {
		if (linha < (totalLinhas - 4)) {
			if (validar(linha, coluna) == validar(linha + 1, coluna)
					&& validar(linha + 1, coluna) == validar(linha + 2, coluna)
					&& validar(linha + 2, coluna) == validar(linha + 3, coluna))
				this.contDNAMutant++;
		}
	}

	private void validarHorizontal(int linha, int coluna) {
		if (coluna < (totalColunas - 4)) {
			if (validar(linha, coluna) == validar(linha, coluna + 1)
					&& validar(linha, coluna + 1) == validar(linha, coluna + 2)
					&& validar(linha, coluna + 2) == validar(linha, coluna + 3))
				this.contDNAMutant++;
		}
	}

	private char validar(int linha, int coluna) {
		try {
			return lista[linha][coluna];
		} catch (Exception e) {
			return 'X';
		}
	}
}
