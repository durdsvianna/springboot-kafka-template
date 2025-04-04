package com.exemplo.clientecrm.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe abstrata utilitária para gerar dados comuns usados em testes.
 * Contém métodos estáticos para gerar CPF, CNPJ e Datas de Nascimento aleatórias.
 * Por ser abstrata e ter construtor privado, não pode ser instanciada.
 */
public abstract class TestToolsUtil {

    // Construtor privado para evitar instanciação
    private TestToolsUtil() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada.");
    }

    /**
     * Gera um número de CPF válido e formatado (XXX.XXX.XXX-XX) de forma aleatória.
     *
     * @return Uma String representando um CPF válido e formatado.
     */
    public static String gerarCpfAleatorio() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> digitos = new ArrayList<>();

        // Gera os 9 primeiros dígitos aleatoriamente
        for (int i = 0; i < 9; i++) {
            digitos.add(random.nextInt(10)); // Gera dígitos de 0 a 9
        }

        // Calcula o primeiro dígito verificador
        digitos.add(calcularDigitoVerificador(digitos));

        // Calcula o segundo dígito verificador
        digitos.add(calcularDigitoVerificador(digitos));

        // Formata o CPF
        StringBuilder cpfFormatado = new StringBuilder();
        for (int i = 0; i < digitos.size(); i++) {
            cpfFormatado.append(digitos.get(i));
            if (i == 2 || i == 5) {
                cpfFormatado.append(".");
            } else if (i == 8) {
                cpfFormatado.append("-");
            }
        }

        return cpfFormatado.toString();
    }

    /**
     * Gera um número de CNPJ válido e formatado (XX.XXX.XXX/XXXX-XX) de forma aleatória.
     * Os 4 dígitos da filial (após a barra) são gerados aleatoriamente,
     * diferente do comum "0001".
     *
     * @return Uma String representando um CNPJ válido e formatado.
     */
    public static String gerarCnpjAleatorio() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> digitos = new ArrayList<>();

        // Gera os 12 primeiros dígitos aleatoriamente
        for (int i = 0; i < 12; i++) {
            digitos.add(random.nextInt(10)); // Gera dígitos de 0 a 9
        }

        // Calcula o primeiro dígito verificador (dígito 13)
        digitos.add(calcularDigitoVerificadorCnpj(digitos));

        // Calcula o segundo dígito verificador (dígito 14)
        digitos.add(calcularDigitoVerificadorCnpj(digitos));

        // Formata o CNPJ
        StringBuilder cnpjFormatado = new StringBuilder();
        for (int i = 0; i < digitos.size(); i++) {
            cnpjFormatado.append(digitos.get(i));
            if (i == 1 || i == 4) {
                cnpjFormatado.append(".");
            } else if (i == 7) {
                cnpjFormatado.append("/");
            } else if (i == 11) {
                cnpjFormatado.append("-");
            }
        }
        return cnpjFormatado.toString();
    }


    /**
     * Gera uma data de nascimento aleatória para uma pessoa com idade entre
     * 18 e 80 anos (aproximadamente).
     *
     * @return Um objeto java.util.Date representando a data de nascimento.
     */
    public static Date gerarDataNascimentoAleatoria() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Calendar calendar = Calendar.getInstance();

        // Define um intervalo de dias para subtrair da data atual
        // Mínimo: 18 anos * ~365.25 dias/ano
        // Máximo: 80 anos * ~365.25 dias/ano
        int minDias = (int) (18 * 365.25);
        int maxDias = (int) (80 * 365.25);

        // Gera um número aleatório de dias para subtrair
        int diasParaSubtrair = random.nextInt(minDias, maxDias + 1);

        // Subtrai os dias da data atual
        calendar.add(Calendar.DAY_OF_YEAR, -diasParaSubtrair);

        // Retorna a data resultante como java.util.Date
        return calendar.getTime();
    }


    // --- Métodos auxiliares privados ---

    /**
     * Calcula um dígito verificador de CPF com base nos dígitos anteriores.
     *
     * @param digitos Lista de inteiros contendo os dígitos base (9 ou 10 dígitos).
     * @return O dígito verificador calculado (0 a 9).
     */
    private static int calcularDigitoVerificador(List<Integer> digitos) {
        int peso = digitos.size() + 1;
        int soma = 0;

        for (int digito : digitos) {
            soma += digito * peso;
            peso--;
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : (11 - resto);
    }

    /**
     * Calcula um dígito verificador de CNPJ com base nos dígitos anteriores.
     *
     * @param digitos Lista de inteiros contendo os dígitos base (12 ou 13 dígitos).
     * @return O dígito verificador calculado (0 a 9).
     */
    private static int calcularDigitoVerificadorCnpj(List<Integer> digitos) {
        int[] pesos = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}; // Pesos incluem o dígito 13 para calcular o 14º
        int pesoIndexInicial = pesos.length - digitos.size(); // Ajusta o início dos pesos

        int soma = 0;
        for (int i = 0; i < digitos.size(); i++) {
            soma += digitos.get(i) * pesos[pesoIndexInicial + i];
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : (11 - resto);
    }

    // --- Exemplo de Uso (opcional, pode ser removido ou colocado em outra classe) ---
    /*
    public static void main(String[] args) {
        System.out.println("CPF Aleatório: " + gerarCpfAleatorio());
        System.out.println("CNPJ Aleatório: " + gerarCnpjAleatorio());
        System.out.println("Data Nasc. Aleatória: " + gerarDataNascimentoAleatoria());

        // Exemplo com data formatada (requer SimpleDateFormat)
        // java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        // System.out.println("Data Nasc. Formatada: " + sdf.format(gerarDataNascimentoAleatoria()));
    }
    */
}