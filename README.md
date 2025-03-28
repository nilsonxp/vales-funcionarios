Sistema de gerenciamento de vales para funcionários, permitindo o controle de créditos concedidos pela empresa e suas quitações.

## 📋 Sobre o Projeto

Este sistema foi desenvolvido para gerenciar vales (adiantamentos) concedidos a funcionários. A aplicação permite que administradores registrem vales, acompanhem pagamentos e gerenciem usuários, enquanto os funcionários podem visualizar seus próprios vales e receber notificações.

### Principais Funcionalidades

- **Autenticação e Autorização**: Sistema de login com JWT e controle de acesso baseado em papéis (ADMIN e USER)
- **Gestão de Vales**: Criação, listagem e quitação de vales
- **Notificações em Tempo Real**: Alertas via WebSocket quando novos vales são criados ou quitados
- **Preferências de Notificação**: Usuários podem personalizar como recebem notificações
- **Auditoria**: Registro de todas as ações realizadas no sistema
- **Interface Responsiva**: Frontend adaptável para diferentes dispositivos

## 🚀 Tecnologias Utilizadas

### Backend
- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT para autenticação
- WebSockets para notificações em tempo real

### DevOps
- Docker e Docker Compose para containerização
- Maven como gerenciador de dependências e build

## 🔧 Configuração do Ambiente

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Maven

### Banco de Dados
O projeto utiliza PostgreSQL que é configurado automaticamente via Docker. Para iniciar o banco de dados:

```bash
cd backend
docker-compose up -d
```

Isso iniciará:
- PostgreSQL na porta 5432
- pgAdmin na porta 5050 (acesso via http://localhost:5050 com email: admin@admin.com e senha: admin)

### Variáveis de Ambiente
O projeto está configurado para usar as seguintes configurações de banco de dados:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/vales_funcionarios
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## 🏃‍♂️ Executando o Projeto

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

Para sistemas Windows:
```bash
cd backend
mvnw.cmd spring-boot:run
```

A API estará disponível em http://localhost:8080

## 🔒 Segurança e Autenticação

O sistema utiliza Spring Security com JWT para autenticação. Os endpoints são protegidos com base nos seguintes papéis:

- `USER`: Funcionários regulares que podem visualizar seus próprios vales e gerenciar suas preferências de notificação
- `ADMIN`: Administradores que podem criar e quitar vales, gerenciar usuários e acessar registros de auditoria

### Endpoints Públicos
- `/api/auth/login`: Autenticação de usuários
- `/api/usuarios/cadastrar`: Cadastro de novos usuários

### Exemplo de Autenticação
```
POST /api/auth/login
Content-Type: application/json

{
  "cpf": "12345678901",
  "senha": "senha123"
}
```

A resposta conterá um token JWT que deve ser incluído no cabeçalho de todas as requisições subsequentes:
```
Authorization: Bearer {seu-token-jwt}
```

## 📱 Notificações em Tempo Real

O sistema utiliza WebSockets para enviar notificações em tempo real aos usuários. As notificações são enviadas quando:

- Um novo vale é criado para o usuário
- Um vale do usuário é quitado

Os usuários podem configurar suas preferências de notificação, escolhendo quais eventos desejam receber alertas.

## 📝 API Endpoints

### Autenticação
- `POST /api/auth/login`: Autenticar usuário

### Usuários
- `POST /api/usuarios/cadastrar`: Cadastrar novo usuário
- `GET /api/usuarios`: Listar todos os usuários (ADMIN)
- `GET /api/usuarios/me`: Obter perfil do usuário logado

### Vales
- `POST /api/vales/{cpf}`: Criar vale para um usuário (ADMIN)
- `GET /api/vales/me`: Listar vales do usuário logado
- `PATCH /api/vales/{id}/quitar`: Marcar vale como quitado (ADMIN)

### Notificações
- `GET /api/notificacoes`: Listar todas as notificações do usuário
- `GET /api/notificacoes/nao-lidas`: Listar notificações não lidas
- `GET /api/notificacoes/contador`: Obter contador de notificações não lidas
- `PATCH /api/notificacoes/{id}/ler`: Marcar notificação como lida
- `PATCH /api/notificacoes/ler-todas`: Marcar todas as notificações como lidas

### Preferências de Notificação
- `GET /api/preferencias/notificacoes/me`: Obter preferências do usuário
- `PUT /api/preferencias/notificacoes/me`: Atualizar preferências do usuário

### Auditoria
- `GET /api/auditoria`: Buscar logs de auditoria com filtros (ADMIN)

## 🧪 Testes

Para executar os testes:
```bash
cd backend
./mvnw test
```

## 📦 Estrutura do Projeto

```
backend/
├── src/main/java/com/evoxdev/vales_fiados_app/
│   ├── config/          # Configurações do Spring (Security, WebSocket)
│   ├── controller/      # Controladores REST
│   ├── dto/             # Objetos de transferência de dados
│   ├── entity/          # Entidades JPA
│   ├── mapper/          # Conversão entre entidades e DTOs
│   ├── repository/      # Repositórios JPA
│   ├── security/        # Configurações de segurança e JWT
│   ├── service/         # Lógica de negócios
│   └── ValesFiadosAppApplication.java  # Classe principal
├── src/main/resources/
│   ├── application.properties  # Configurações da aplicação
│   └── static/                 # Recursos estáticos (JS, CSS)
├── src/test/                   # Testes automatizados
├── .mvn/                       # Configurações do Maven Wrapper
├── docker-compose.yml          # Configuração do Docker
└── pom.xml                     # Dependências Maven
```

## 🤝 Contribuição

Para contribuir com o projeto:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença Apache 2.0. Veja o arquivo LICENSE para mais detalhes.

## 📧 Contato

Para dúvidas ou sugestões, entre em contato com a equipe de desenvolvimento.
