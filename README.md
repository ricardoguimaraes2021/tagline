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

## Tecnologias

| Tecnologia | Descrição |
|------------|-----------|
| **Kotlin** | Linguagem de programação |
| **Jetpack Compose** | UI toolkit moderno |
| **MVVM** | Arquitetura |
| **Hilt** | Dependency Injection |
| **Retrofit** | HTTP Client |
| **Coil** | Image Loading |
| **Firebase Auth** | Autenticação |
| **Firebase Firestore** | Base de dados |
| **TMDB API** | Dados de filmes/séries |
| **Navigation Compose** | Navegação |

## Screenshots

*Em breve*

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

## Estrutura do Projeto

```
app/src/main/java/com/example/tagline/
├── data/
│   ├── api/           # Serviços de API (TMDB, WatchMode)
│   ├── model/         # Modelos de dados
│   └── repository/    # Repositórios
├── di/                # Dependency Injection (Hilt)
├── ui/
│   ├── navigation/    # Navegação
│   ├── screens/       # Ecrãs da app
│   └── theme/         # Tema e cores
└── util/              # Utilidades
```

## Autor

**Ricardo Guimarães** - Projeto Final

## Licença

Este projeto foi desenvolvido para fins académicos.

