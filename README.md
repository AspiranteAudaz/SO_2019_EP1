# SO_2019_EP1

## Java e Git

### Compilar

```sh
javac *.java
```

### Rodar

```sh
java Main
```

### Desenvolver nova feature

```sh
git pull
git checkout DEV
git branch Categoria_feature
git checkout Categoria_feature
```

### Incluir nova feature

```sh
git checkout DEV
git merge Categoria_feature
git push
```

## Notas de desenvolvimento

### Escalonador

Escalonador:

* Não muda variaveis do sistema, so muda as filas. 

* Não executa programa, só chama o sistema para executar, passando o BCP do processo.


* ```BCP tabelaBCP[];```

    Tabela com todos os BCPs de cada processo, já inicializados.

### Sistema

Sistema muda os registradores e os valores dos BCPs.

* ```char Executa(BCP processo)```

    Método chamado para __rodar__ o processo escalonado, só retorna quando para de rodar, seja por limite de quantum ou por bloqueio.




