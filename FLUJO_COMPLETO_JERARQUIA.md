# 🌱 Flujo Completo: Jerarquía User → Terrain → Sector → Bancal

## 📊 **Jerarquía de Datos Implementada**

```
User (Usuario)
├── Terrain (Terreno/Huerto) 
│   └── "Mi Primer Huerto" (creado automáticamente)
│       └── Sector (Sección del terreno)
│           └── "Zona Principal" (creado automáticamente)
│               └── Bancal (Cama de cultivo individual)
│                   ├── "Bancal de Tomates" (creado por usuario)
│                   ├── "Bancal de Lechugas" (creado por usuario)
│                   └── ... (más bancales)
└── [Futuro] Terrain adicionales
    └── [Futuro] Sectores adicionales
        └── Bancales en esos sectores
```

## 🔄 **Flujo de Funcionamiento Completo**

### **1. Registro de Usuario Nuevo** 
```
LoginScreen → "Crear Usuario" → Formulario completo:
├── Nombre completo: "Ana García"
├── Username: "ana123" 
└── Contraseña: "password123"
    ↓
CreateUserUseCase → Usuario creado en base de datos
    ↓
InitializeUserDataUseCase → Se ejecuta automáticamente:
    ├── Crea Terrain: "Mi Primer Huerto" (userId: ana_id)
    └── Crea Sector: "Zona Principal" (terrainId: huerto_id)
    ↓
UserViewModel → _user.value = ana
    ↓
LoginScreen → Navegación automática a BancalesScreen
```

### **2. Inicialización de BancalesScreen**
```
BancalesScreen → userViewModel.user cambia
    ↓
BancalViewModel.setUserId(ana_id) → Se ejecuta una sola vez
    ↓
GetUserSectorsUseCase(ana_id):
    ├── Busca Terrains de Ana → ["Mi Primer Huerto"]
    └── Busca Sectores del primer Terrain → ["Zona Principal"]
    ↓
BancalViewModel → _sectors.value = ["Zona Principal"]
    ↓
loadBancalesForSector("zona_principal_id"):
    └── Busca Bancales en "Zona Principal" → [] (vacío inicialmente)
    ↓
UI → Muestra "No hay bancales aún. Usa el botón 'Crear' para empezar"
```

### **3. Creación del Primer Bancal**
```
Usuario presiona "Crear Bancal"
    ↓
CreateBancalDialog → Se abre con:
    ├── Título: "Crear Nuevo Bancal"
    ├── Subtítulo: "Sector: Zona Principal"
    └── Campos: [Nombre] [Ancho] [Largo]
    ↓
Usuario completa formulario:
    ├── Nombre: "Bancal de Tomates"
    ├── Ancho: "2.5" metros
    └── Largo: "8.0" metros
    ↓
BancalViewModel.createBancal("Bancal de Tomates", 2.5, 8.0)
    ├── Sector actual: "Zona Principal" (primer sector disponible)
    ├── Posición inicial: (400, 400) centro de pantalla
    └── CreateBancalUseCase(name, sectorId, x, y, width, height)
    ↓
Room Database → Inserción de Bancal:
    ├── id: UUID generado
    ├── name: "Bancal de Tomates"
    ├── sectorId: "zona_principal_id" (FK)
    ├── x: 400, y: 400
    ├── width: 2.5, height: 8.0
    ↓
StateFlow → getBancalesBySectorIdUseCase emite actualización
    ↓
BancalesScreen → UI se actualiza automáticamente:
    └── Bancal aparece draggable en pantalla con dimensiones correctas
```

## 🏗️ **Arquitectura de Datos en Base de Datos**

### **Tabla Users**
```sql
CREATE TABLE users (
    id TEXT PRIMARY KEY,           -- UUID del usuario
    name TEXT NOT NULL,            -- "Ana García"
    username TEXT NOT NULL UNIQUE, -- "ana123"
    passwordHash TEXT NOT NULL     -- Hash SHA-256 con salt
);
```

### **Tabla Terrains**  
```sql
CREATE TABLE terrains (
    id TEXT PRIMARY KEY,           -- UUID del terreno
    name TEXT NOT NULL,            -- "Mi Primer Huerto"
    userId TEXT NOT NULL,          -- FK a users.id
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);
```

### **Tabla Sectors**
```sql
CREATE TABLE sectors (
    id TEXT PRIMARY KEY,           -- UUID del sector  
    name TEXT NOT NULL,            -- "Zona Principal"
    terrainId TEXT NOT NULL,       -- FK a terrains.id
    FOREIGN KEY (terrainId) REFERENCES terrains(id) ON DELETE CASCADE
);
```

### **Tabla Bancales**
```sql
CREATE TABLE bancales (
    id TEXT PRIMARY KEY,           -- UUID del bancal
    name TEXT NOT NULL,            -- "Bancal de Tomates"
    sectorId TEXT NOT NULL,        -- FK a sectors.id
    x REAL NOT NULL,              -- Posición X (400.0)
    y REAL NOT NULL,              -- Posición Y (400.0)  
    width REAL NOT NULL,          -- Ancho en metros (2.5)
    height REAL NOT NULL,         -- Largo en metros (8.0)
    FOREIGN KEY (sectorId) REFERENCES sectors(id) ON DELETE CASCADE
);
```

## 🎯 **Estados por Defecto**

### **Usuario Nuevo (Ana García)**
```json
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Ana García", 
    "username": "ana123",
    "passwordHash": "salt123:hash456"
  },
  "terrain": {
    "id": "650e8400-e29b-41d4-a716-446655440001", 
    "name": "Mi Primer Huerto",
    "userId": "550e8400-e29b-41d4-a716-446655440000"
  },
  "sector": {
    "id": "750e8400-e29b-41d4-a716-446655440002",
    "name": "Zona Principal", 
    "terrainId": "650e8400-e29b-41d4-a716-446655440001"
  },
  "bancales": [] 
}
```

### **Después de Crear Primer Bancal**
```json
{
  "bancales": [{
    "id": "850e8400-e29b-41d4-a716-446655440003",
    "name": "Bancal de Tomates",
    "sectorId": "750e8400-e29b-41d4-a716-446655440002", 
    "x": 400.0,
    "y": 400.0,
    "width": 2.5,
    "height": 8.0
  }]
}
```

## 🔮 **Funcionalidades Futuras Preparadas**

### **Múltiples Terrenos por Usuario**
```
User: Ana García
├── Terrain: "Mi Primer Huerto" (actual por defecto)
│   └── Sector: "Zona Principal"
│       ├── Bancal: "Tomates"
│       └── Bancal: "Lechugas"
├── Terrain: "Huerto de Invierno" (futuro)
│   ├── Sector: "Invernadero Norte"
│   └── Sector: "Invernadero Sur" 
└── Terrain: "Huerto Comunitario" (futuro)
    └── Sector: "Mi Parcela"
```

### **Selector de Contexto (Futuro)**
```
BancalesScreen Header:
[Dropdown] Mi Primer Huerto > Zona Principal ▼
    ├── Mi Primer Huerto > Zona Principal (actual)
    ├── Mi Primer Huerto > Zona Secundaria
    ├── Huerto de Invierno > Invernadero Norte
    └── Huerto Comunitario > Mi Parcela
```

## ✅ **Validaciones y Constraints Implementadas**

### **Integridad Referencial**
- ✅ **FK Cascades**: Al eliminar User → se eliminan sus Terrains → se eliminan sus Sectors → se eliminan sus Bancales
- ✅ **NOT NULL constraints**: Todos los campos obligatorios
- ✅ **Username UNIQUE**: No se permiten usernames duplicados

### **Validaciones de Negocio**
- ✅ **Nombres válidos**: 1-50 caracteres, no vacíos
- ✅ **Dimensiones válidas**: Width/Height > 0, coordenadas ≥ 0
- ✅ **Sector requerido**: No se puede crear bancal sin sector

## 🎯 **Resultado del Flujo Completo**

### **Flujo de Éxito Completo:**
1. ✅ **Usuario se registra** → Datos iniciales se crean automáticamente
2. ✅ **BancalesScreen se carga** → Muestra contexto: "Zona Principal"  
3. ✅ **Usuario crea bancal** → Se guarda vinculado al sector por defecto
4. ✅ **UI se actualiza** → Bancal aparece draggable con dimensiones correctas
5. ✅ **Persistencia completa** → Datos sobreviven a reinicios de app

### **Arquitectura Escalable:**
- 🔄 **Datos reactivos** con Flow → UI siempre sincronizada
- 🏗️ **Clean Architecture** → Fácil añadir nuevas funcionalidades
- 📊 **Jerarquía clara** → Estructura lógica para futuras expansiones
- 🛡️ **Validación robusta** → Integridad de datos garantizada

**El sistema está completamente preparado para el desarrollo futuro de selectores de terreno/sector y creación de múltiples contextos de trabajo.** 🚀