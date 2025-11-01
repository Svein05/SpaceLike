# Instalacion y Ejecucion

## Requisitos

- JDK 11 o superior
- Git
- Eclipse IDE

## Importar el Proyecto en Eclipse

1. Clonar el repositorio
```bash
git clone https://github.com/Svein05/SpaceLike.git
cd SpaceLike
```

2. Abrir Eclipse
3. File > Import > Gradle > Existing Gradle Project
4. Seleccionar la carpeta del proyecto
5. Finish

## Ejecutar el Juego

1. Window > Show View > Other > Gradle > Gradle Tasks
2. Expandir SpaceLike > lwjgl3 > application
3. Doble clic en "run"

## Compilar el Proyecto

1. Expandir SpaceLike > lwjgl3 > build
2. Doble clic en "build"

## Generar JAR Ejecutable

1. Expandir SpaceLike > lwjgl3 > other
2. Doble clic en "dist"
3. El JAR se genera en: `lwjgl3/build/libs/`

## Limpiar el Proyecto

1. Expandir SpaceLike > lwjgl3 > build
2. Doble clic en "clean"
