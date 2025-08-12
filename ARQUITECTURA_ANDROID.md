# 📱 HuertoPlan - Documentación de Arquitectura Android

## 🎯 Introducción

HuertoPlan es una aplicación Android moderna para la gestión de huertos y parcelas agrícolas, construida siguiendo las mejores prácticas de **Clean Architecture** combinada con el patrón **MVVM** (Model-View-ViewModel). Esta documentación está diseñada para ser didáctica y servir como referencia para entender la arquitectura moderna de Android.

## 🏗️ Arquitectura General

### Patrón Arquitectónico: Clean Architecture + MVVM

La aplicación implementa una **arquitectura de 3 capas** con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────┐
│            PRESENTATION LAYER               │
│  (UI Components, ViewModels, Navigation)    │
├─────────────────────────────────────────────┤
│              DOMAIN LAYER                   │
│        (Use Cases, Business Logic)          │
├─────────────────────────────────────────────┤
│               DATA LAYER                    │
│    (Repository, DAOs, Database, Models)     │
└─────────────────────────────────────────────┘
```

### Tecnologías Clave

- **UI**: Jetpack Compose (UI declarativa moderna)
- **Navegación**: Navigation Compose
- **Inyección de dependencias**: Hilt
- **Base de datos**: Room Database
- **Programación asíncrona**: Kotlin Coroutines + Flow
- **Arquitectura**: Clean Architecture + MVVM

---

## 🗃️ CAPA DE DATOS (Data Layer)

### Jerarquía del Modelo de Dominio

```
User (Usuario)
├── Terrain (Terreno/Huerto)
    ├── Sector (Sección dentro del terreno)
        ├── Bancal (Cama de cultivo individual con posición y dimensiones)
```

### 📊 Entidades de Base de Datos

#### **User.kt** - Entidad Usuario
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String
) {
    init {
        require(id.isNotBlank()) { "User ID cannot be blank" }
        require(name.isNotBlank()) { "User name cannot be blank" }
    }
}
```

**Características importantes**:
- Utiliza Room como ORM (Object-Relational Mapping)
- Validación en el constructor con `require()` 
- Patrón data class para inmutabilidad por defecto
- Primary key tipo String para flexibilidad (UUIDs)

#### **Terrain.kt** - Entidad Terreno
```kotlin
@Entity(
    tableName = "terrains",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
```

**Características**:
- **Foreign Key** con `onDelete = CASCADE`: Si se borra un usuario, se borran automáticamente sus terrenos
- Mantiene la **integridad referencial** de la base de datos

### 📋 DAOs (Data Access Objects)

Los DAOs son interfaces que definen las operaciones de base de datos:

#### **UserDao.kt**
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}
```

**Conceptos clave**:
- **`@Dao`**: Annotation que marca la interfaz como Data Access Object
- **`Flow<List<User>>`**: Emisión reactiva de datos - la UI se actualiza automáticamente cuando cambian los datos
- **`suspend fun`**: Función de corrutina para operaciones asíncronas
- **`OnConflictStrategy.REPLACE`**: Si existe un conflicto (mismo ID), reemplaza el registro

### 🏪 Repository Pattern

#### **HuertoPlanRepository.kt**
```kotlin
@Singleton
class HuertoPlanRepository @Inject constructor(
    private val userDao: UserDao,
    private val terrainDao: TerrainDao,
    private val sectorDao: SectorDao,
    private val bancalDao: BancalDao
) {
    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
}
```

**Características importantes**:
- **`@Singleton`**: Una sola instancia en toda la aplicación
- **`@Inject constructor`**: Hilt inyecta automáticamente las dependencias
- **`withContext(Dispatchers.IO)`**: Ejecuta operaciones de base de datos en el hilo de I/O
- **Abstrae el acceso a datos**: La capa superior no sabe si los datos vienen de base de datos, red, o caché

### 🗄️ Database Setup

#### **HuertoPlanDatabase.kt**
```kotlin
@Database(
    entities = [User::class, Terrain::class, Sector::class, Bancal::class],
    version = 1,
    exportSchema = false
)
abstract class HuertoPlanDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // ...
    
    companion object {
        @Volatile
        private var INSTANCE: HuertoPlanDatabase? = null
        
        fun getDatabase(context: Context): HuertoPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(/*...*/).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Conceptos clave**:
- **Singleton Pattern**: `@Volatile` + `synchronized` garantiza una sola instancia thread-safe
- **Room Database**: Abstracción sobre SQLite con validación en tiempo de compilación
- **Migration strategy**: `fallbackToDestructiveMigration()` para desarrollo (¡NO usar en producción!)

---

## 🎯 CAPA DE DOMINIO (Domain Layer)

### Use Cases (Casos de Uso)

Los Use Cases contienen la **lógica de negocio** y son reutilizables desde diferentes partes de la aplicación.

#### **CreateUserUseCase.kt**
```kotlin
class CreateUserUseCase @Inject constructor(
    private val repository: HuertoPlanRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider
) {
    suspend operator fun invoke(name: String): Result<User> = withContext(dispatcherProvider.io) {
        try {
            ValidationUtils.validateUserName(name)
            val user = User(id = UUID.randomUUID().toString(), name = name)
            repository.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Características**:
- **`operator fun invoke()`**: Permite llamar al Use Case como una función: `createUserUseCase(name)`
- **Validación**: Utiliza `ValidationUtils` para validar entrada
- **Result<T>**: Patrón para manejar éxito/error de forma type-safe
- **Dependency Injection**: Los dispatchers se inyectan para facilitar testing

#### **ValidationUtils.kt** - Lógica de Validación
```kotlin
object ValidationUtils {
    fun validateUserName(name: String): String {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.length >= 2) { "Name must be at least 2 characters" }
        require(name.length <= 50) { "Name cannot exceed 50 characters" }
        return name.trim()
    }
}
```

### Manejo de Errores del Dominio

#### **HuertoPlanError.kt**
```kotlin
sealed class HuertoPlanError : Exception() {
    object UserNotFound : HuertoPlanError()
    object TerrainNotFound : HuertoPlanError()
    data class ValidationError(override val message: String) : HuertoPlanError()
    data class DatabaseError(override val cause: Throwable) : HuertoPlanError()
}
```

**Ventajas de sealed class**:
- **Exhaustive when**: El compilador asegura que manejes todos los casos
- **Type Safety**: Cada error puede tener propiedades específicas
- **Hierarchical**: Hereda de Exception para integración con Result<T>

### Estado de UI

#### **UiState.kt**
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}
```

Este patrón permite manejar los 3 estados principales de cualquier operación asíncrona en la UI.

---

## 🖥️ CAPA DE PRESENTACIÓN (Presentation Layer)

### ViewModels con MVVM

#### **UserViewModel.kt**
```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()
    
    fun createUser(name: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            createUserUseCase(name)
                .onSuccess { user -> _uiState.value = UiState.Success(user) }
                .onFailure { error -> _uiState.value = UiState.Error(error) }
        }
    }
}
```

**Características del ViewModel**:
- **`@HiltViewModel`**: Hilt maneja la inyección de dependencias automáticamente
- **StateFlow**: Estado reactivo que sobrevive a cambios de configuración
- **viewModelScope**: Las corrutinas se cancelan automáticamente cuando el ViewModel se destruye
- **Separation of Concerns**: El ViewModel no sabe nada sobre Android Framework (testable)

### UI con Jetpack Compose

#### **BancalesScreen.kt** (Ejemplo simplificado)
```kotlin
@Composable
fun BancalesScreen(
    userViewModel: UserViewModel = hiltViewModel()
) {
    val uiState by userViewModel.uiState.collectAsStateWithLifecycle()
    
    when (uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> BancalesContent(user = uiState.data)
        is UiState.Error -> ErrorMessage(uiState.exception)
    }
}
```

**Conceptos de Compose**:
- **`@Composable`**: Función que describe UI declarativamente
- **`collectAsStateWithLifecycle()`**: Recoge el StateFlow respetando el ciclo de vida
- **`by`**: Delegación de propiedades para extraer el valor del State
- **Recomposition**: La UI se reconstruye automáticamente cuando cambia el estado

### Navegación

#### **AppNavigation.kt**
```kotlin
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AppScreens.BancalesScreen.route) {
            BancalesScreen()
        }
    }
}
```

#### **AppScreens.kt** - Rutas Centralizadas
```kotlin
sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object BancalesScreen : AppScreens("bancales_screen")
}
```

---

## 💉 INYECCIÓN DE DEPENDENCIAS CON HILT

### Setup de la Aplicación

#### **HuertoApp.kt**
```kotlin
@HiltAndroidApp
class HuertoApp : Application()
```

**`@HiltAndroidApp`**: Genera el código necesario para Hilt y sirve como punto de entrada para la inyección de dependencias.

### Módulos de Dependencias

#### **DatabaseModule.kt**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideHuertoPlanDatabase(@ApplicationContext context: Context): HuertoPlanDatabase {
        return HuertoPlanDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideUserDao(database: HuertoPlanDatabase): UserDao = database.userDao()
    
    @Provides
    @Singleton
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider {
        return object : CoroutineDispatcherProvider {
            override val main: CoroutineDispatcher = Dispatchers.Main
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val default: CoroutineDispatcher = Dispatchers.Default
        }
    }
}
```

**Características**:
- **`@Module`**: Clase que proporciona dependencias
- **`@InstallIn(SingletonComponent::class)`**: Las dependencias viven durante toda la aplicación
- **`@Provides`**: Método que crea y configura una dependencia
- **`@Singleton`**: Una sola instancia para toda la aplicación

### Activity Setup

#### **MainActivity.kt**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HuertoPlanTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
```

**`@AndroidEntryPoint`**: Permite que Hilt inyecte dependencias en Android components (Activities, Fragments, etc.).

---

## 🧪 TESTING STRATEGY

### Unit Testing con MockK

#### **UserViewModelTest.kt**
```kotlin
@ExtendWith(MockKExtension::class)
class UserViewModelTest {
    
    @RelaxedMockK
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase
    
    @RelaxedMockK  
    private lateinit var createUserUseCase: CreateUserUseCase
    
    private lateinit var userViewModel: UserViewModel
    
    @Before
    fun setup() {
        userViewModel = UserViewModel(getUserByIdUseCase, createUserUseCase)
    }
    
    @Test
    fun `when createUser is called with valid name, then uiState should be Success`() = runTest {
        // Given
        val userName = "Test User"
        val expectedUser = User("123", userName)
        coEvery { createUserUseCase(userName) } returns Result.success(expectedUser)
        
        // When
        userViewModel.createUser(userName)
        
        // Then
        val uiState = userViewModel.uiState.value
        assertTrue(uiState is UiState.Success)
        assertEquals(expectedUser, (uiState as UiState.Success).data)
    }
}
```

**Herramientas de testing**:
- **MockK**: Biblioteca de mocking para Kotlin
- **`@RelaxedMockK`**: Crea mocks que devuelven valores por defecto
- **`runTest`**: Maneja corrutinas en tests
- **`coEvery`**: Configura comportamiento de funciones suspend

---

## 📊 FLUJO DE DATOS REACTIVO

### Flow Pattern para Reactividad

```kotlin
// 1. DAO emite datos como Flow
fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

// 2. Repository expone el Flow sin modificación
fun getAllUsers(): Flow<List<User>> = repository.getAllUsers()

// 3. ViewModel convierte Flow a StateFlow para la UI
private val _users = repository.getAllUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
val users: StateFlow<List<User>> = _users

// 4. UI observa y se recompone automáticamente
@Composable
fun UserList(viewModel: UserViewModel = hiltViewModel()) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    // UI se actualiza automáticamente cuando cambian los users
}
```

### Ventajas del Flow Pattern

1. **Reactividad**: Los cambios en la base de datos se propagan automáticamente a la UI
2. **Efficiency**: Solo se actualizan los componentes que realmente cambiaron
3. **Lifecycle-aware**: `collectAsStateWithLifecycle()` maneja automáticamente el ciclo de vida
4. **Thread Safety**: Flow maneja automáticamente el threading

---

## 🚀 MEJORES PRÁCTICAS IMPLEMENTADAS

### 1. Separation of Concerns
- **Data Layer**: Solo maneja persistencia y acceso a datos
- **Domain Layer**: Solo contiene lógica de negocio  
- **Presentation Layer**: Solo maneja UI y interacción con usuario

### 2. Dependency Inversion
- Las capas superiores no dependen de implementaciones concretas
- Se usan interfaces y abstracciones
- Fácil testing y cambio de implementaciones

### 3. Single Responsibility Principle
- Cada Use Case tiene una sola responsabilidad
- Cada DAO maneja una sola entidad
- Cada ViewModel maneja una sola pantalla/funcionalidad

### 4. Error Handling
- Use Cases devuelven `Result<T>` para manejo type-safe de errores
- Sealed classes para errores específicos del dominio
- UI states que incluyen estado de error

### 5. Threading
- Operaciones de base de datos en `Dispatchers.IO`
- UI updates en `Dispatchers.Main`
- Dispatchers inyectables para testing

---

## 🔄 TRANSICIÓN ARQUITECTÓNICA (Archivo Legacy)

### MainRepository.kt (Legacy - En proceso de migración)

**Nota**: Este archivo representa la arquitectura anterior y debe migrarse gradualmente:

```kotlin
@Singleton
class MainRepository @Inject constructor() {
    private val _users = mutableListOf<User>()
    private val _terrains = mutableListOf<Terrain>()
    // Almacenamiento en memoria
}
```

**Diferencias con la nueva arquitectura**:
- ❌ Datos en memoria (no persistentes)
- ❌ No usa Room Database  
- ❌ No utiliza Flow para reactividad
- ❌ Lógica de negocio mezclada con acceso a datos

**Plan de migración**:
1. ✅ Implementar nueva arquitectura con Room
2. 🔄 Migrar ViewModels a usar nuevos Use Cases
3. ❌ Eliminar MainRepository legacy
4. ✅ Consolidar toda la lógica en Clean Architecture

---

## 📚 CONCEPTOS CLAVE PARA ANDROID SENIOR (10 años sin Android)

### 1. **Jetpack Compose vs XML Views**
```kotlin
// ANTES (XML + findViewById)
val button = findViewById<Button>(R.id.button)
button.setOnClickListener { /* */ }

// AHORA (Compose declarativo)
@Composable
fun MyButton() {
    Button(onClick = { /* */ }) {
        Text("Click me")
    }
}
```

### 2. **Coroutines vs AsyncTask**
```kotlin
// ANTES (AsyncTask - deprecated)
class MyAsyncTask : AsyncTask<Void, Void, User>()

// AHORA (Coroutines)
viewModelScope.launch {
    val user = withContext(Dispatchers.IO) {
        repository.getUser()
    }
}
```

### 3. **Room vs SQLiteOpenHelper**
```kotlin
// ANTES (SQLiteOpenHelper manual)
SQLiteDatabase db = helper.getWritableDatabase();
ContentValues values = new ContentValues();

// AHORA (Room automático + type-safe)
@Query("SELECT * FROM users WHERE id = :id")
suspend fun getUserById(id: String): User?
```

### 4. **StateFlow vs LiveData**
```kotlin
// LiveData (todavía válido)
val userData: LiveData<User> = repository.getUser()

// StateFlow (más moderno, mejor para Compose)
val userData: StateFlow<User> = repository.getUser()
    .stateIn(viewModelScope, SharingStarted.Lazily, User.empty())
```

### 5. **Hilt vs Dagger2**
```kotlin
// ANTES (Dagger2 - complejo)
@Component(modules = [AppModule::class])
interface AppComponent { /* mucho boilerplate */ }

// AHORA (Hilt - simple)
@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint  
class MainActivity : ComponentActivity()
```

---

## 🎯 CONCLUSIÓN

HuertoPlan implementa una **arquitectura moderna y escalable** que sigue las mejores prácticas actuales de Android:

✅ **Clean Architecture** con separación clara de capas
✅ **MVVM** con ViewModels y data binding reactivo  
✅ **Jetpack Compose** para UI declarativa
✅ **Room Database** para persistencia local
✅ **Hilt** para inyección de dependencias simple
✅ **Coroutines + Flow** para programación asíncrona reactiva
✅ **Testing strategy** completa con MockK
✅ **Type Safety** con Kotlin y sealed classes

Esta arquitectura te permitirá:
- **Escalar** fácilmente añadiendo nuevas funcionalidades
- **Mantener** el código de forma sencilla
- **Testear** cada componente de forma aislada
- **Reutilizar** lógica de negocio entre diferentes partes de la app
- **Seguir** las recomendaciones oficiales de Google para Android

¡La aplicación está bien posicionada para el desarrollo moderno de Android! 🚀