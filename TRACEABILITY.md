# TRACEABILITY - Rastreabilidade de Requisitos

Este documento mapeia todos os requisitos funcionais (RF) e não funcionais (RNF) para seus respectivos locais de implementação no código.

## Requisitos Funcionais

| Requisito | Onde no Código | Como foi atendido | Justificativa |
|-----------|---------------|-------------------|---------------|
| **RF001 - Login/Logout** | `LoginActivity.kt`, `LoginViewModel.kt`, `InMemoryUserRepository.kt`, `ShoppingListActivity.kt` | Tela de login com validação de e-mail (regex) e campos obrigatórios. Botão "Cadastrar" navega para RegisterActivity. Logout no menu do ShoppingListActivity limpa sessão (setCurrentUser(null)) e retorna ao login. | Atende validação de credenciais, navegação entre telas e gerenciamento de sessão conforme especificado. |
| **RF002 - Cadastro** | `RegisterActivity.kt`, `RegisterViewModel.kt`, `InMemoryUserRepository.kt` | Formulário com nome, e-mail, senha e confirmar senha. Validações: campos obrigatórios, e-mail válido (regex), senhas coincidem, senha mínima 6 caracteres. Após cadastro, define usuário como corrente e navega para Suas Listas. | Implementa todas as validações requeridas e cria novo usuário armazenado em memória por e-mail. |
| **RF003 - Listas** | `ShoppingListActivity.kt`, `ShoppingListAdapter.kt`, `AddEditListActivity.kt`, `ShoppingListViewModel.kt`, `AddEditListViewModel.kt`, `InMemoryListRepository.kt` | CRUD completo: RecyclerView com listas ordenadas A-Z (sortedBy title.lowercase()), cards com título e imagem/placeholder. FAB para adicionar. Seleção de imagem via SAF (OpenDocument). Validação de título obrigatório. Excluir lista também remove seus itens (deleteItemsByListId). | Atende ordenação, vinculação por userId, imagem opcional via SAF, e exclusão em cascata de itens. |
| **RF004 - Itens (MAIOR PESO)** | `ItemActivity.kt`, `ItemAdapter.kt`, `AddEditItemActivity.kt`, `ItemViewModel.kt`, `AddEditItemViewModel.kt`, `InMemoryItemRepository.kt`, `Category.kt` | CRUD completo de itens com nome, quantidade, unidade e categoria. RecyclerView com três tipos de ViewHolders: Header (seções Pendentes/Comprados), CategoryHeader (categoria com ícone), ItemData (item com checkbox, nome, quantidade). Ordenação A-Z, agrupamento por categoria, seção "Comprados" separada visualmente com strikethrough e opacidade. Toggle de comprado com atualização imediata. Validações: todos os campos obrigatórios, quantidade > 0. | Maior peso da rubrica: implementa todos os detalhes (ícones por categoria, agrupamento, separação visual de comprados, ordenação, validações completas). |
| **RF005 - Buscas** | `ShoppingListActivity.kt` (menu), `ItemActivity.kt` (menu), `ShoppingListViewModel.kt`, `ItemViewModel.kt` | SearchView no menu de ambas telas. Busca de listas por título (contains ignoreCase) e itens por nome (contains ignoreCase). Filtragem em memória responsiva. | Atende busca case-insensitive em listas e itens conforme especificado. |

## Requisitos Não Funcionais

| Requisito | Onde no Código | Como foi atendido | Justificativa |
|-----------|---------------|-------------------|---------------|
| **MVVM + SOLID** | Todos os ViewModels, Repositories, Activities | Arquitetura MVVM com separação clara: Models (User, ShoppingList, Item, Category), Repositories (interfaces + implementações), ViewModels (lógica de negócio), Activities/Adapters (UI). SOLID: SRP (1 arquivo = 1 responsabilidade, 1 Activity/Fragment por tela, 1 ViewModel por tela, 1 Adapter por RecyclerView, 1 Repository por entidade), Interface Segregation (interfaces de repositórios), Dependency Injection (ViewModelFactory com injeção manual). | Segue padrão MVVM rigoroso e princípios SOLID com SRP 1:1 conforme exigido. |
| **SharedFlow sem combine** | Todos os ViewModels | Uso de `MutableSharedFlow(replay=1)` para expor estados e eventos. NENHUM uso de `combine` ou `StateFlow`. Transformações simples (map, filter) quando necessário. | Atende restrição de usar SharedFlow com replay=1 e evitar combine conforme especificado. |
| **Repositórios Singleton** | `InMemoryUserRepository`, `InMemoryListRepository`, `InMemoryItemRepository` | Cada repositório é singleton (getInstance() com double-check locking). Armazena dados em memória durante vida do app. Listas vinculadas por userId, itens por listId. | Mantém dados em memória por processo, permitindo navegação entre telas sem perder dados. |
| **ViewBinding** | `build.gradle.kts`, todas Activities | ViewBinding habilitado no build.gradle. Todas Activities usam binding (ActivityLoginBinding, etc.) evitando findViewById. | Segurança de tipo e performance conforme boas práticas Android. |
| **Anti-NullPointer** | Todos os arquivos | Tipos não nulos com defaults (String = ""), validações antes de usar (?.let, Elvis ?:), verificações de intent extras, try-catch em parsing de URIs. Evitado lateinit desnecessário. | Previne NullPointerException conforme especificado. |
| **Material Design 3** | Layouts XML, `build.gradle.kts` | Uso de Material Components: MaterialButton, MaterialCardView, MaterialTextView, TextInputLayout, FloatingActionButton, MaterialToolbar, AppBarLayout. Tema Material3. | UI moderna e consistente com guidelines Android. |
| **SAF para Imagens** | `AddEditListActivity.kt` | ActivityResultContracts.OpenDocument para seleção de imagem. URI armazenada como String em memória. Placeholder quando ausente. | Acesso seguro a arquivos e armazenamento de URI conforme especificado. |
| **Rotação sem Perda** | ViewModels com SavedStateHandle, Repositórios Singleton | ViewModels sobrevivem a mudanças de configuração (by viewModels). Repositórios singleton mantêm dados. onResume recarrega dados do repositório. | Preserva estado e dados durante rotação conforme especificado. |
| **RecyclerView + DiffUtil** | `ShoppingListAdapter`, `ItemAdapter` | ListAdapter com DiffUtil.ItemCallback para otimização de updates. | Performance otimizada para listas grandes. |
| **Validações** | Todos os ViewModels | Validações antes de salvar: e-mail regex, campos obrigatórios, senhas coincidem, quantidade > 0, título obrigatório. Mensagens de erro claras via Toast. | Previne dados inválidos e orienta usuário. |

## Estrutura de Arquivos (SRP 1:1)

### Models
- `User.kt` - Modelo de usuário
- `ShoppingList.kt` - Modelo de lista de compras
- `Item.kt` - Modelo de item
- `Category.kt` - Enum de categorias com ícones

### Repositories (1 por entidade)
- `UserRepository.kt` + `InMemoryUserRepository.kt` - Gerencia usuários e sessão
- `ListRepository.kt` + `InMemoryListRepository.kt` - Gerencia listas por usuário
- `ItemRepository.kt` + `InMemoryItemRepository.kt` - Gerencia itens por lista

### ViewModels (1 por tela)
- `LoginViewModel.kt` - Login
- `RegisterViewModel.kt` - Cadastro
- `ShoppingListViewModel.kt` - Dashboard de Listas
- `AddEditListViewModel.kt` - Adicionar/Editar Lista
- `ItemViewModel.kt` - Tela de Itens
- `AddEditItemViewModel.kt` - Adicionar/Editar Item
- `ViewModelFactory.kt` - Factory para injeção de dependências

### Activities (1 por tela)
- `LoginActivity.kt` - Tela de Login
- `RegisterActivity.kt` - Tela de Cadastro
- `ShoppingListActivity.kt` - Dashboard de Listas
- `AddEditListActivity.kt` - Adicionar/Editar Lista
- `ItemActivity.kt` - Tela de Itens
- `AddEditItemActivity.kt` - Adicionar/Editar Item

### Adapters (1 por RecyclerView)
- `ShoppingListAdapter.kt` - Adapter de listas
- `ItemAdapter.kt` - Adapter de itens (3 ViewHolders)

### Layouts (1 por tela/componente)
- `activity_login.xml` - Layout de Login
- `activity_register.xml` - Layout de Cadastro
- `activity_shopping_list.xml` - Layout de Dashboard
- `activity_add_edit_list.xml` - Layout Adicionar/Editar Lista
- `activity_item.xml` - Layout de Itens
- `activity_add_edit_item.xml` - Layout Adicionar/Editar Item
- `item_shopping_list.xml` - Card de Lista
- `item_shopping_item.xml` - Card de Item
- `item_header.xml` - Header de Seção
- `item_category_header.xml` - Header de Categoria

## Comentários no Código

Todos os arquivos possuem comentários `[RF###]` e `[RNF]` no topo e inline indicando:
1. Qual requisito está sendo atendido
2. Como foi implementado
3. Justificativa da decisão

## Checklist de Aceitação

✅ Login/Cadastro/Logout com validações e navegação corretas (RF001, RF002)
✅ Suas Listas: cards A-Z, imagem/placeholder, FAB, excluir remove itens (RF003)
✅ Itens: ícone, nome, quantidade, A-Z, agrupados por categoria, seção "Comprados" separada (RF004)
✅ Busca funcional em listas e itens (RF005)
✅ SharedFlow (replay=1) usado; nenhuma ocorrência de `combine` (RNF)
✅ SRP 1:1, repositórios por entidade, MVVM + SOLID (RNF)
✅ Sem NullPointer: tipos não nulos, validações, try-catch (RNF)
✅ Rotação preserva estado via ViewModel e repositórios singleton (RNF)
✅ ViewBinding habilitado (RNF)
✅ Material Design 3 (RNF)
✅ SAF para imagens (RNF)
✅ DiffUtil nos adapters (RNF)

