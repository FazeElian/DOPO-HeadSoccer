dyn4j 5.0.2 - libreria de fisica 2D (licencia BSD-3, ver dyn4j-LICENSE.md).
Compilado desde el codigo fuente oficial: https://github.com/dyn4j/dyn4j (tag 5.0.2).

Agregarla al proyecto:
- Consola:  javac -cp lib/dyn4j-5.0.2.jar -d bin src/domain/*.java
            java  -cp bin:lib/dyn4j-5.0.2.jar presentation.DOPOSoccerGUI   (en Windows usar ; en vez de :)
- BlueJ:    Tools > Preferences > Libraries > Add File... > lib/dyn4j-5.0.2.jar (y reiniciar BlueJ)
- IntelliJ: File > Project Structure > Libraries > + > lib/dyn4j-5.0.2.jar
- VS Code:  Java Projects > Referenced Libraries > + > lib/dyn4j-5.0.2.jar

Solo domain/PhysicsEngine.java importa la libreria.
