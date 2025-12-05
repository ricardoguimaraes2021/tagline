<p align="center">
  <img src="tagline_logo.png" alt="Tagline Logo" width="200"/>
</p>

<h1 align="center">Tagline</h1>

<p align="center">
  <strong>A tua lista de filmes e séries para nunca perderes nada!</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase"/>
</p>

---

## Sobre

**Tagline** é uma aplicação Android para gerir a tua lista pessoal de filmes e séries que queres assistir. Pesquisa qualquer título, vê a classificação, descobre onde está disponível para streaming e guarda na tua lista para nunca mais esqueceres!

## Funcionalidades

- 🔐 **Autenticação** - Login, registo e recuperação de password com Firebase
- 🔍 **Pesquisa** - Encontra filmes e séries por título usando a API TMDB
- ⭐ **Classificações** - Vê a pontuação de cada título
- 📺 **Onde Assistir** - Descobre em que plataformas está disponível (Netflix, HBO, Disney+, etc.)
- 📋 **Minha Lista** - Guarda os títulos que queres ver
- 🎬 **Filtros** - Filtra por filmes, séries ou já vistos
- ✅ **Marcar como Visto** - Acompanha o teu progresso
- 🕐 **Histórico de Pesquisas** - Acede rapidamente às pesquisas recentes
- 💾 **Cache Local** - Carregamento mais rápido com Room Database

## Screenshots

<p align="center">
  <img src="screenshots/01_login.png" width="200" alt="Login"/>
  <img src="screenshots/02_search.png" width="200" alt="Pesquisa"/>
  <img src="screenshots/03_search_results.png" width="200" alt="Resultados"/>
  <img src="screenshots/04_details.png" width="200" alt="Detalhes"/>
</p>

<p align="center">
  <img src="screenshots/05_details_providers.png" width="200" alt="Onde Assistir"/>
  <img src="screenshots/06_my_list.png" width="200" alt="Minha Lista"/>
  <img src="screenshots/07_my_list_series.png" width="200" alt="Séries"/>
  <img src="screenshots/08_my_list_watched.png" width="200" alt="Vistos"/>
</p>

## Tecnologias

| Tecnologia | Descrição |
|------------|-----------|
| **Kotlin** | Linguagem de programação |
| **Jetpack Compose** | UI toolkit moderno |
| **MVVM** | Arquitetura |
| **Hilt** | Dependency Injection |
| **Retrofit** | HTTP Client |
| **Room** | Base de dados local (cache) |
| **Coil** | Image Loading |
| **Firebase Auth** | Autenticação |
| **Firebase Firestore** | Base de dados cloud |
| **TMDB API** | Dados de filmes/séries |
| **Navigation Compose** | Navegação |

## Arquitetura

A aplicação segue a arquitetura **MVVM (Model-View-ViewModel)** com **Clean Architecture**:

```
app/src/main/java/com/example/tagline/
├── data/
│   ├── api/              # Serviços de API (TMDB, WatchMode)
│   ├── local/            # Room Database
│   │   ├── dao/          # Data Access Objects
│   │   └── entity/       # Entidades da base de dados
│   ├── model/            # Modelos de dados
│   └── repository/       # Repositórios (fonte única de dados)
├── di/                   # Dependency Injection (Hilt modules)
├── ui/
│   ├── navigation/       # Navegação entre ecrãs
│   ├── screens/          # Ecrãs da app (Composables + ViewModels)
│   └── theme/            # Tema, cores e tipografia
└── util/                 # Utilidades e extensões
```

### Cache Local (Room)

A app utiliza Room Database para cache local:

| Tabela | Descrição | Validade |
|--------|-----------|----------|
| `genres` | Lista de géneros | Permanente |
| `cached_media` | Detalhes de filmes/séries | 24 horas |
| `search_history` | Histórico de pesquisas | Últimas 20 |

**Benefícios:**
- Carregamento mais rápido dos detalhes já visitados
- Histórico de pesquisas para acesso rápido
- Menos chamadas à API (economia de dados)

## Configuração

### Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17
- Conta Firebase
- API Key TMDB

### Instalação

1. Clona o repositório
```bash
git clone https://github.com/ricardoguimaraes2021/tagline.git
```

2. Configura o Firebase
   - Cria um projeto no [Firebase Console](https://console.firebase.google.com/)
   - Adiciona uma app Android com package `com.example.tagline`
   - Faz download do `google-services.json` para `app/`
   - Ativa Authentication (Email/Password) e Firestore

3. Configura as API Keys em `local.properties`
```properties
TMDB_API_KEY=a_tua_api_key
WATCHMODE_API_KEY=a_tua_api_key
```

4. Compila e executa no Android Studio

## Autor

**Ricardo Guimarães** - Projeto Final

## Licença

Este projeto foi desenvolvido para fins académicos.
