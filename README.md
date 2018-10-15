# MUTANT-XMEN

[![codecov](https://codecov.io/gh/drubervany/xmen/branch/master/graph/badge.svg)](https://codecov.io/gh/drubervany/xmen)
[![Build Status](https://api.travis-ci.org/drubervany/xmen.svg?branch=master)](https://travis-ci.org/drubervany/xmen)


## Arquiterura:

![Screenshot](arquitetura.png)


Arquitetura desenvolvida para muitas execuções para menor tempo de processamento.

**API Gatawey:** Reposta até 29.9s, após esse periodo ocorrete timeout.
**Lamda:** Processamento maximo de 5min, após a AWS interrompe o processamento.
**Dynamodb:** Banco de Dados NOSQL, onde é gravado todo o documento recebido na requisição.

**Obs.:** Existem problemas de processamento e reposta para esta aquitetura!

## AWS-LAMBDA-JAVA-SERVERLESS

###### REQUEST

DNA Mutante:
POST → /mutant/

Body:
```json
{
    "dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

###### RESPONSE

| CODIGO | DESCRIÇÃO |
|---|---|
| 200 |  Mutante |
```json
{
    "dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"], "mutante": true
}
```

###### REQUEST

DNA Humano:
POST → /mutant/

Body:
```json
{
    "dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

###### RESPONSE

| CODIGO | DESCRIÇÃO |
|---|---|
| 403 |  Humano |
```json
{
    "dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"], "mutante": false
}
```

###### REQUEST

GET → /stats/

###### RESPONSE

| CODIGO | DESCRIÇÃO |
|---|---|
| 200 |  OK |
```json
{
    "count_mutant_dna":40, "count_human_dna":100, "ratio":0.4
}
```

# Framework Utilizado

Foi utilizado esta tecnologia para que atenda os tráfegos agressivos (entre 100 e milhões de pedidos por segundo).


**O Serverless Framework** – É utilizado para criação de microsserviços que são executados em resposta a eventos, autoescala para que cobre apenas quando eles são executados. Isso reduz o custo total de manutenção de seus aplicativos, permitindo que você crie mais lógica, mais rapidamente.

1. **Instalar via npm:**
  ```bash
  npm install -g serverless
  ```

2. **Configure suas [Provider Credentials](./docs/providers/aws/guide/credentials.md)**. [Assista ao vídeo sobre como configurar credenciais](https://www.youtube.com/watch?v=HSd9uYj2LJA)

3 **Comandos:**

Completo
  ```bash
  serverless deploy -v
  ```

Simplificado
 ```bash
  sls deploy -v
  ```


4. **Deploy:**

  Use isso quando tiver feito alterações em suas Funções, Eventos ou Recursos `serverless.yml` ou simplesmente desejar fazer o deploy de todas as alterações em seu Serviço ao mesmo tempo
  ```bash
  serverless deploy -v
  ```

5. **Deploy a Function:**

  Use isso para fazer o upload e sobregravar rapidamente seu código do AWS Lambda na AWS.
  ```bash
  serverless deploy function -f verificar
  ```
   ```bash
  serverless deploy function -f estatistica
  ```

6. **Invoke a Function:**

  Invoca uma função do AWS Lambda na AWS e retorna logs.
  ```bash
  serverless invoke -f verificar -l
  ```
  ```bash
  serverless invoke -f estatistica -l
  ```

7. **Fetch the Function Logs:**

  Abra uma guia separada em seu console e transmita todos os logs para uma função específica usando este comando.
  ```bash
  serverless logs -f verificar -t
  ```
  ```bash
  serverless logs -f estatistica -t
  ```

8. **Remove a Service:**

  Remove todas as funções, eventos e recursos da sua conta da AWS.
  ```bash
  serverless remove
  ```

