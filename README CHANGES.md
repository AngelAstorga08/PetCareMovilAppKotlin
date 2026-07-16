# ProyectoAngel — Módulo de Veterinaria (PetCare)

> Este README describe la parte del proyecto relacionada con veterinaria. `fragment_client.xml` y `ClientFragment.kt` (pantalla "Clientes" con CRUD de clave/nombre/edad) siguen fuera de alcance por estar desactualizados y sujetos a refactorización, según indicación directa del propietario del proyecto.

## Cambios de esta iteración

A petición del propietario, se hicieron los siguientes cambios estructurales sobre el estado descrito en la versión anterior de este README (donde los XML de veterinaria ya tenían el contenido correcto pero los nombres de archivo y el código Kotlin seguían siendo los del dominio anterior):

1. **Se renombraron y separaron en Fragments propios los XML que eran de veterinaria pero estaban mal ubicados:**

   | Antes | Ahora | Fragment nuevo |
   |---|---|---|
   | `activity_main.xml` (contenido del dashboard, dentro de la Activity) | `fragment_home.xml` | `HomeFragment.kt` (`ui/home/`) |
   | `item_cliente.xml` (formulario "Agendar cita", mal usado como fila de RecyclerView) | `fragment_agendar_cita.xml` | `AgendarCitaFragment.kt` (`ui/appointment/`) |
   | `veterinario.xml` (sin registrar en ningún lado) | `fragment_panel_citas.xml` | `PanelCitasFragment.kt` (`ui/appointment/`) |

   `fragment_login.xml` no se renombró: su nombre ya era correcto (es, y sigue siendo, la pantalla de login).

2. **`MainActivity` ahora solo maneja la navegación entre fragmentos.** `activity_main.xml` quedó reducido a un único `FragmentContainerView` que aloja el `NavHostFragment` (`app:navGraph="@navigation/nav_graph"`). `MainActivity.kt` ya no tiene ningún `findViewById` de UI propia, solo aplica el padding de `WindowInsets` al contenedor de navegación.

3. **Se creó `HomeFragment` como pantalla real de "barra de navegación"** (el menú con tarjetas Mis citas / Mis mascotas / Mi perfil / Administrar + próximas citas), con el mismo contenido que tenía `activity_main.xml`. Es el primer destino al que se llega después del login.

   Sobre la duda planteada de si es común que `MainActivity` maneje esto: sí, es el patrón estándar recomendado por Android (arquitectura de una sola Activity + Navigation Component) — por eso se dejó así: una única Activity "delgada" que solo aloja el `NavHostFragment`, y toda la UI real (incluida la que actúa como menú/barra de navegación) vive en Fragments.

4. **`nav_graph.xml`** ahora incluye `homeFragment`, `agendarCitaFragment` y `panelCitasFragment` como destinos, y la acción `action_login_to_home` reemplaza a la anterior `action_login_to_clients` (el login ahora lleva a `HomeFragment`, no directo a `ClientFragment`).

5. **`item_cliente.xml` se recreó con contenido mínimo** (`tv_nombre`, `tv_clave`) para que `ClientAdapter.kt` (pantalla de Clientes, fuera de alcance) siga compilando — antes apuntaba al formulario de "Agendar cita", que no tenía esos ids.

6. **Login: renombrado de campo/método (a pedido explícito, no solo "mínimo para compilar")**
   - `LoginFragment.kt` ya no busca `R.id.et_username` (no existía en `fragment_login.xml`, rompía la compilación) — ahora usa `R.id.et_email`, que sí existe.
   - `User.kt`: el campo `userName` se renombró a `email`, con `@SerializedName("userName")` para **no** cambiar la llave JSON enviada al backend real.
   - `ApiService.kt`, `LoginRepository.kt`, `LoginViewModel.kt`: el método `loginUser(userName, password)` se renombró a `login(email, password)` en las tres capas, manteniendo intacta la lógica existente (`Result` sellado, `LiveData`, coroutines, manejo de errores con try/catch).
   - `registerUser` se dejó con el mismo nombre (no se pidió cambiarlo), pero también recibe `email` en vez de `userName` por consistencia con `User.kt`.
   - **No se tocó la ruta `@POST("api/Auth/LogIn")`.** No tengo acceso a red en este entorno para verificar el contrato real del backend desplegado en `Constants.BASE_URL`, así que no fue posible confirmar si ese path debía cambiar también. Si el login no conecta correctamente contra ese backend, ese es el primer lugar a revisar.

## Corrección posterior: `android:borderRadius` no existe

Al intentar compilar el proyecto real (algo que yo no pude hacer en este entorno por falta de red), salió este error:

```
error: attribute android:borderRadius not found.
```

`android:borderRadius` **no es un atributo real de Android** — no existe en el framework, así que el linker de recursos falla apenas se intenta usar. Este atributo ya estaba en el contenido original de `activity_main.xml` e `item_cliente.xml` (de donde salieron `fragment_home.xml` y `fragment_agendar_cita.xml`); yo lo copié tal cual sin detectarlo, porque mi verificación anterior solo cruzó `id`s contra `findViewById`, no validó que cada atributo existiera. Quedó corregido así:

- Se crearon 6 drawables `shape` en `res/drawable/` (`bg_white_r8`, `bg_success_r8`, `bg_error_r8`, `bg_white_r12`, `bg_badge_confirmada_r12`, `bg_badge_pendiente_r12`) que combinan el color de fondo que ya tenía cada vista con el radio de esquina que `borderRadius` intentaba aplicar.
- Se reemplazó cada `android:background="#COLOR"` + `android:borderRadius="Xdp"` por `android:background="@drawable/bg_..."` correspondiente, en `fragment_home.xml` y `fragment_agendar_cita.xml`.
- Se confirmó (`grep -rn "borderRadius"` sobre todo `res/`) que no queda ninguna otra ocurrencia, y se validó que los XML modificados siguen siendo bien formados.
- Se revisaron también los demás atributos `android:*` usados en todos los layouts del proyecto para descartar que hubiera otro atributo inventado igual — no se encontró ninguno más.

## Verificación realizada (y su límite)

No fue posible ejecutar `./gradlew build` ni ninguna compilación real en este entorno (sin acceso a red para que Gradle descargue dependencias) — por eso el error de `android:borderRadius` de la sección anterior no lo detecté yo, sino que salió al compilar en un entorno real. Mi verificación se limitó a lo que se puede comprobar sin compilar (coincidencia de nombres de `id`, `layout` y, ahora también, de atributos existentes). No reemplaza una compilación real. Lo que sí se hizo, de forma verificable:

- Se listaron todos los `R.layout.*` usados en Kotlin y se confirmó que cada uno tiene un archivo XML correspondiente.
- Se listó, para cada `Fragment`/`Adapter`, cada `R.id.*` que su código Kotlin busca con `findViewById`, y se confirmó contra los `android:id` reales del XML que infla — sin discrepancias, en `LoginFragment`, `HomeFragment`, `AgendarCitaFragment`, `ClientAdapter`, `ClientFragment` y `MainActivity`.
- Se confirmó que `action_login_to_home` está declarado en `nav_graph.xml` y coincide con el usado en `LoginFragment.kt`.
- Se buscaron residuos de los nombres antiguos (`et_username`, `R.id.main`, `action_login_to_clients`, `loginUser(`) en todo el código: no quedan referencias activas (solo aparecen en comentarios explicativos).

[No verificado] Dos dependencias que ya se usaban en el proyecto antes de estos cambios (`androidx.cardview.widget.CardView` en el dashboard y `androidx.recyclerview.widget.RecyclerView` en el formulario de cita y en clientes) no aparecen como `implementation` explícita en `app/build.gradle.kts`. Esto no es algo que haya introducido en esta iteración — ya eran así — pero como no pude correr Gradle, no puedo confirmar si se resuelven de forma transitiva (vía Material Components) o si haría falta agregarlas explícitamente.

## Pendientes (no implementado, se deja documentado para continuar)

- Conectar las tarjetas de `HomeFragment` (Mis citas, Mis mascotas, Mi perfil, Cerrar sesión) con destinos reales de navegación — hoy son listeners vacíos con `TODO`.
- Implementar la lógica real de `AgendarCitaFragment` (cargar veterinarios, `DatePicker` para la fecha, `RecyclerView` de horas, y guardar la cita).
- Agregar a `ApiService.kt` los endpoints de citas, mascotas y veterinarios (hoy solo existen `Auth` y `Client`), y sus `dto`/`entities` correspondientes.
- `PanelCitasFragment` sigue siendo un mockup estático (sin ids en sus vistas); falta darle datos reales.
- Confirmar contra la documentación real del backend si el path `api/Auth/LogIn` y la llave JSON `userName` (preservada vía `@SerializedName`) siguen siendo correctos, o si conviene actualizarlos también.

## Propósito

La aplicación en construcción es **PetCare**, una app de clínica veterinaria para clientes/dueños de mascotas, con las siguientes pantallas:

1. **Login** (`fragment_login.xml` / `LoginFragment`) — acceso con correo y contraseña.
2. **Home** (`fragment_home.xml` / `HomeFragment`) — menú de navegación: citas, mascotas, perfil y (para administradores) gestión de usuarios, más un resumen de próximas citas.
3. **Agendar cita** (`fragment_agendar_cita.xml` / `AgendarCitaFragment`) — formulario para elegir clínica, veterinario, fecha, hora y mascota.
4. **Panel de citas** (`fragment_panel_citas.xml` / `PanelCitasFragment`) — listado/gestión de citas con filtros, estadísticas y paginación.

## Arquitectura

```
app/src/main/java/com/example/proyectoangel/
├── data/
│   ├── api/            → ApiService (Retrofit)
│   └── repository/     → LoginRepository, ClientRepository
├── models/
│   ├── dto/             → LoginResponseDto, ClientResponseDto, Result
│   └── entities/        → User (email, password), Client
├── ui/
│   ├── login/            → LoginFragment (fragment_login.xml)
│   ├── home/             → HomeFragment (fragment_home.xml) — nuevo
│   ├── appointment/       → AgendarCitaFragment, PanelCitasFragment — nuevo
│   ├── client/            → ClientFragment (fragment_client.xml, EXCLUIDO) + ClientAdapter (item_cliente.xml)
│   └── viewmodel/        → LoginViewModel, ClientViewModel
├── constants/            → Constants.kt (BASE_URL)
└── MainActivity.kt       → solo aloja el NavHostFragment (activity_main.xml)
```

Patrón de capas: **View** (`Fragment`) → **ViewModel** (`LiveData` + `viewModelScope`) → **Repository** → **ApiService** (Retrofit).

## Stack tecnológico (verificado en `build.gradle.kts` / `libs.versions.toml`)

- Kotlin, Android SDK (`minSdk` 26, `targetSdk`/`compileSdk` 36)
- AndroidX Navigation Component 2.7.7
- Retrofit 2.11.0 + Gson converter
- OkHttp logging-interceptor 4.12.0
- Kotlin Coroutines 1.8.0
- AndroidX Lifecycle 2.7.0
- Material Components, ConstraintLayout, CardView, RecyclerView (ver nota de verificación arriba)

## Explícitamente fuera de alcance

`fragment_client.xml` y `ClientFragment.kt` (pantalla "Clientes" con CRUD de clave/nombre/edad) — dominio genérico anterior, desactualizado y sujeto a refactorización.
