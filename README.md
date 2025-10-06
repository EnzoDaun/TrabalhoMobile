# App de Lista de Compras - ParcialM2

Aplicativo Android nativo em Kotlin para gerenciamento de listas de compras com funcionalidades completas de CRUD, busca, categorização e separação de itens comprados.

## 📋 Funcionalidades Implementadas

### RF001 - Login e Logout
- Tela de login com validação de e-mail (regex) e campos obrigatórios
- Link para cadastro de nova conta
- Logout no menu com confirmação
- Gerenciamento de sessão do usuário

### RF002 - Cadastro de Usuário
- Formulário completo: nome, e-mail, senha e confirmação
- Validações: e-mail válido, senhas coincidem, campos obrigatórios, senha mínima 6 caracteres
- Navegação automática após cadastro bem-sucedido

### RF003 - Gerenciamento de Listas
- Dashboard "Suas Listas" com RecyclerView
- Cards com título e imagem (ou placeholder)
- Ordenação alfabética A-Z
- Adicionar, editar e excluir listas
- Seleção de imagem via SAF (Storage Access Framework)
- FAB (Floating Action Button) para adicionar
- Busca por título de lista
- Exclusão de lista remove todos os itens associados

### RF004 - Gerenciamento de Itens (Maior Peso)
- CRUD completo de itens: nome, quantidade, unidade, categoria
- RecyclerView com agrupamento por categoria
- Ícones coloridos para cada categoria (9 categorias disponíveis)
- Ordenação alfabética A-Z dentro de cada categoria
- Seção "Pendentes" e "Comprados" separadas visualmente
- Checkbox para marcar/desmarcar como comprado
- Itens comprados com efeito strikethrough e opacidade reduzida
- Busca por nome de item
- Validações completas de todos os campos

### RF005 - Busca
- Busca de listas por título (case-insensitive)
- Busca de itens por nome (case-insensitive)
- Filtragem em tempo real

## 🏗️ Arquitetura

### MVVM + SOLID
- **Model**: User, ShoppingList, Item, Category
- **View**: Activities + Layouts XML
- **ViewModel**: 1 ViewModel por tela com SharedFlow (replay=1)
- **Repository**: 1 interface + implementação por entidade (Singleton)

### Princípios SOLID Aplicados
- **SRP (Single Responsibility Principle)**: 1:1
  - 1 Activity por tela
  - 1 ViewModel por Activity
  - 1 Layout XML por tela
  - 1 Adapter por RecyclerView
  - 1 Repository por entidade (User, List, Item)
  
- **Interface Segregation**: Interfaces específicas para cada repositório

- **Dependency Injection**: ViewModelFactory com injeção manual

## 🔧 Tecnologias e Bibliotecas

- **Linguagem**: Kotlin
- **SDK mínimo**: Android 24 (Nougat)
- **SDK alvo**: Android 36
- **UI**: Material Design 3
- **ViewBinding**: Habilitado para type-safe view access
- **Coroutines + Flow**: Para operações assíncronas e fluxo de dados
- **SharedFlow**: Para gerenciamento de estado (replay=1)
- **RecyclerView + DiffUtil**: Para listas otimizadas
- **SAF**: Para seleção segura de imagens

## 📦 Estrutura de Pacotes

```
com.example.parcialm/
├── model/
│   ├── User.kt
│   ├── ShoppingList.kt
│   ├── Item.kt
│   └── Category.kt
├── repository/
│   ├── UserRepository.kt
│   ├── InMemoryUserRepository.kt
│   ├── ListRepository.kt
│   ├── InMemoryListRepository.kt
│   ├── ItemRepository.kt
│   └── InMemoryItemRepository.kt
├── viewmodel/
│   ├── LoginViewModel.kt
│   ├── RegisterViewModel.kt
│   ├── ShoppingListViewModel.kt
│   ├── AddEditListViewModel.kt
│   ├── ItemViewModel.kt
│   ├── AddEditItemViewModel.kt
│   └── ViewModelFactory.kt
└── ui/
    ├── LoginActivity.kt
    ├── RegisterActivity.kt
    ├── ShoppingListActivity.kt
    ├── AddEditListActivity.kt
    ├── ItemActivity.kt
    ├── AddEditItemActivity.kt
    ├── ShoppingListAdapter.kt
    └── ItemAdapter.kt
```

## 🎨 Categorias Disponíveis

1. 🍎 Frutas
2. 🥬 Verduras
3. 🥩 Carnes
4. 🥛 Laticínios
5. 🍞 Padaria
6. 🥤 Bebidas
7. 🧹 Limpeza
8. 🧴 Higiene
9. 📦 Outros

## 💾 Persistência de Dados

- **Em Memória**: Dados armazenados em repositórios Singleton durante a execução do app
- **Rotação**: Dados preservados via ViewModel + SavedStateHandle
- **Vinculação**: Listas vinculadas a usuários (userId), itens vinculados a listas (listId)
- **Sessão**: Usuário atual mantido no UserRepository

## ✅ Validações Implementadas

### Login
- E-mail não vazio e formato válido (regex)
- Senha não vazia

### Cadastro
- Todos os campos obrigatórios
- E-mail válido (regex)
- Senhas coincidem
- Senha mínima de 6 caracteres

### Lista
- Título obrigatório

### Item
- Nome obrigatório
- Quantidade obrigatória e > 0
- Unidade obrigatória
- Categoria obrigatória

## 🔄 Fluxo de Navegação

```
LoginActivity
├── → RegisterActivity → ShoppingListActivity (após cadastro)
└── → ShoppingListActivity (após login)
    ├── → AddEditListActivity (adicionar/editar lista)
    └── → ItemActivity (visualizar itens da lista)
        └── → AddEditItemActivity (adicionar/editar item)
```

## 🚀 Como Executar

1. Abrir o projeto no Android Studio
2. Aguardar sincronização do Gradle
3. Executar em emulador ou dispositivo físico (API 24+)
4. Tela inicial: Login
   - Cadastre um novo usuário ou use credenciais de teste

## 📝 Observações Importantes

- **Dados em memória**: Ao fechar o app, todos os dados são perdidos
- **Imagens**: URIs de imagens são armazenadas como String (sem cópia de arquivo)
- **Sem backend**: Toda lógica é local no dispositivo
- **Sem bibliotecas externas**: Apenas AndroidX e Material Components

## 📄 Documentação Adicional

Consulte `TRACEABILITY.md` para rastreabilidade completa de requisitos e mapeamento código-requisitos.

## 🎯 Critérios de Aceitação Atendidos

✅ RF001-RF005 implementados e funcionais
✅ MVVM + SOLID com SRP 1:1
✅ SharedFlow (replay=1) sem uso de `combine`
✅ Repositórios singleton por entidade
✅ ViewBinding habilitado
✅ Material Design 3
✅ Validações completas
✅ Busca funcional
✅ Ordenação e agrupamento
✅ Seção "Comprados" separada
✅ SAF para imagens
✅ Rotação sem perda de dados
✅ Comentários [RF###] no código
✅ Anti-NullPointer

