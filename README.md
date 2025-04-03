# Integração de Clientes com CRM

Este microsserviço Spring Boot realiza a integração entre o sistema de clientes e o novo CRM. 

## Funcionalidades

- Consome a API de clientes para obter lista de clientes criados/alterados no dia anterior
- Divide os clientes em grupos de até 15 clientes, se necessário
- Publica os grupos de clientes no tópico "CLIENTES" do Kafka
- Agendamento automático diário (1h da manhã)
- Endpoint para execução manual da integração

## Requisitos

- Java 21
- Maven
- Kafka

## Configuração

As configurações da aplicação podem ser encontradas no arquivo `application.yml`:

```yaml
spring:
  application:
    name: cliente-crm-integracao
  kafka:
    bootstrap-servers: localhost:9092

api:
  clientes:
    url: https://xyz.net/api/v1/clientes

app:
  kafka:
    topic: CLIENTES
  integracao:
    tamanho-grupo: 15
```

## Execução

```bash
mvn spring-boot:run
```

## Endpoint para execução manual

```
POST /api/integracao/clientes
```

## Testes

Os testes utilizam Cucumber para BDD, JUnit 5 e Rest Assured.

Para executar os testes:

```bash
mvn test
```

### Cenários de teste

- Integração bem-sucedida com poucos clientes (10 clientes)
- Integração com divisão em grupos (28 clientes)
- Integração sem clientes para processar