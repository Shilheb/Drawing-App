@echo off
echo Compilation du projet Drawing Application...

REM Créer le répertoire de sortie
if not exist "target\classes" mkdir "target\classes"

REM Compiler les sources Java (sans les dépendances externes pour le moment)
echo Compilation des classes de base...
javac -d target\classes -cp "src\main\java" src\main\java\com\drawing\model\shapes\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\factory\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\observer\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\strategy\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\utils\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\command\*.java
javac -d target\classes -cp "src\main\java;target\classes" src\main\java\com\drawing\model\drawing\*.java

echo Compilation terminée !
echo.
echo Pour tester les classes de base, vous pouvez utiliser :
echo java -cp target\classes com.drawing.factory.ShapeFactory
echo.
pause
